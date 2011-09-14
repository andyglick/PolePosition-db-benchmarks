/* 
This file is part of the PolePosition database benchmark
http://www.polepos.org

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
MA  02111-1307, USA. */

package org.polepos.framework;

import org.polepos.util.MemoryUtil;
import org.polepos.watcher.FileSizeWatcher;
import org.polepos.watcher.TimeWatcher;

import java.util.*;

/**
 * a set of timed test cases that work against the same data
 */
public abstract class TimedLapsCircuitBase extends CircuitBase {
    
    public static final String NUM_RUNS_PROPERTY_ID = "POLEPOS_NUM_RUNS";
    public static final String MEMORY_USAGE_PROPERTY_ID = "POLEPOS_MEMORY_USAGE";

	private final int _numRuns = Integer.parseInt(System.getProperty(NUM_RUNS_PROPERTY_ID, "1"));
	private final MemoryUsage _memoryUsage = memoryUsage();

	private static MemoryUsage memoryUsage() {
		try {
			return (MemoryUsage)Class.forName(System.getProperty(MEMORY_USAGE_PROPERTY_ID, SimpleMemoryUsage.class.getName())).newInstance();
		} 
		catch (Exception exc) {
			exc.printStackTrace();
			return new SimpleMemoryUsage();
		}
	}

	List<Lap> _laps;
    
    // TODO: watcher can be installed, and should be sorted, i.e. memory watcher
	// should start before time watcher
    private TimeWatcher _timeWatcher;
    
    // TODO: The effect of MemoryWatcher is too strong on the running tests.
    //       We should investigate if we can get less intrusive results with JMX
    // private MemoryWatcher _memoryWatcher;
    
    private FileSizeWatcher _fileSizeWatcher;
    
    protected TimedLapsCircuitBase(){
        initWatchers();
    }
    
    private void initWatchers() {
		_timeWatcher = new TimeWatcher();
		// _memoryWatcher = new MemoryWatcher();
		_fileSizeWatcher = new FileSizeWatcher();
	}
    
	/**
     * describes the intent of this circuit, what it wants to test
     */
	@Override
	public abstract String description();

    /**
     * @return the driver class needed to run on this Circuit
     */
    @Override
	public abstract Class<?> requiredDriver();
    
    /**
     * @return the methods that are intended to be run 
     */
    protected abstract void addLaps();
    
	protected void add(Lap lap){
        _laps.add(lap);
        lap.circuit(this);
    }
    
    /**
     * calling all the laps for all the lapSetups
     */
	public TurnResult[] race( Team team, Car car, Driver driver){
        TurnResult[] results = new TurnResult[ _turnSetups.length ];

        int index = 0;
        
        for(TurnSetup setup : _turnSetups) {
            System.out.println("*** Turn " + index);
            results[index++] = runTurn(team, car, driver, index, setup);
        }
        return results;
    }

	private TurnResult runTurn(Team team, Car car, Driver driver, int index, TurnSetup setup) {
		
		Map<Lap, Set<LapReading>> lapReadings = new HashMap<Lap, Set<LapReading>>();
		for (Lap lap : laps()) {
			
			if(lap.reportResult()) {
				lapReadings.put(lap, new HashSet<LapReading>());
			}
		}
		boolean warmUp = _numRuns > 1;
		for (int runIdx = 0; runIdx < _numRuns; runIdx++) {
			
			prepareDriverToRunLaps(car, driver, setup);        
			
			for(Lap lap : laps()) {
				
				if(driver.canRunLap(lap)){
					
	            	System.out.println("*** Lap " + lap.name());
	
				    LapReading lapReading = runLap(team, driver, setup, lap);
				    if(!warmUp && lap.reportResult()) {
				    	lapReadings.get(lap).add(lapReading);
				    }
				}
			}
	
			driver.closeDatabase();
						
			tearDownTurn(team, driver);
			warmUp = false;
		}
		
		TurnResult turnResult = new TurnResult();
		for (Lap lap : laps()) {
			if(!lap.reportResult()) {
				continue;
			}
			if(! driver.canRunLap(lap)){
				continue;
			}
			long time = 0;
			long memory = 0;
			long fileSize = 0;
			long checkSum = 0;
			Set<LapReading> curReadings = lapReadings.get(lap);
			for (LapReading curReading : curReadings) {
				time += curReading.time;
				memory += curReading.memory;
				fileSize += curReading.fileSize;
				checkSum += curReading.checkSum;
			}
			Result lapResult = new TimedLapsResult(_reportTo , team, lap, setup, index, time, memory, fileSize, checkSum);
			turnResult.report(lapResult);
		}
		return turnResult;
	}

	private void prepareDriverToRunLaps(Car car, Driver driver, TurnSetup setup) {
		car.team().setUp();
		driver.configure(car, setup);
		driver.prepareDatabase();
		driver.prepare();
	}

	private void tearDownTurn(Team team, Driver driver) {
		team.tearDown();
		driver.circuitCompleted();
	}

	private LapReading runLap(Team team, Driver driver, TurnSetup setup, Lap lap) {
		
		Runnable lapRunnable = driver.prepareLap(lap);
		
		if( ! lap.hot() ){
			driver.closeDatabase();
		    driver.prepare();
		}
		
		MemoryUtil.gc();
		
		// _memoryWatcher.start();
		
		_timeWatcher.start();
		_fileSizeWatcher.monitorFile(team.databaseFile());
		_fileSizeWatcher.start();
		
		lapRunnable.run();
		
		_timeWatcher.stop();
		
		// _memoryWatcher.stop();
		
		_fileSizeWatcher.stop();
		
		return new LapReading(_timeWatcher.value(), _memoryUsage.usedMemory(), _fileSizeWatcher.value(), driver.checkSum());
	}
	
	private final static class LapReading {
		public final long time;
		public final long memory;
		public final long fileSize;
		public final long checkSum;

		public LapReading(long time, long memory, long fileSize, long checkSum) {
			this.time = time;
			this.memory = memory;
			this.fileSize = fileSize;
			this.checkSum = checkSum;
		}
		
		@Override
		public String toString() {
			return time + " ms";
		}
	}
	
	public static interface MemoryUsage {
		long usedMemory();
	}
	
	public static class SimpleMemoryUsage implements MemoryUsage {
		public long usedMemory() {
			return MemoryUtil.usedMemory();
		}
	}

    public boolean isConcurrency(){
		return false;
	}
	
	public void runLapsBefore(Lap lap, TurnSetup turnSetup, DriverBase driver, Car car) {
		prepareDriverToRunLaps(car, driver, turnSetup);
		for (Lap currentLap : laps()) {
			if(currentLap == lap){
				return;
			}
			if(driver.canRunLap(lap)){
			    runLap(car.team(), driver, turnSetup, currentLap);
			}
			
		}
	}
	
	@Override
	public RacingStrategy racingStrategy() {
		return new TimedLapsRacingStrategy(this);
	}
	
	@Override
	public List<Lap> laps() {
		if(_laps == null){
			_laps = new ArrayList<Lap>();
			addLaps();
		}
	    return _laps;
	}

    @Override
    public boolean isFixedTime() {
    	return false;
    }
}

