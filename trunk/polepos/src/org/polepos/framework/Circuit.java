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

import java.lang.reflect.*;
import java.util.*;

import org.polepos.util.*;
import org.polepos.watcher.*;

/**
 * a set of timed test cases that work against the same data
 */
public abstract class Circuit{
    
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

	private final List<Lap> mLaps;
    
    private TurnSetup[] mLapSetups;
        
    // TODO: watcher can be installed, and should be sorted, i.e. memory watcher
	// should start before time watcher
    private TimeWatcher _timeWatcher;
    
    // TODO: The effect of MemoryWatcher is too strong on the running tests.
    //       We should investigate if we can get less intrusive results with JMX
    // private MemoryWatcher _memoryWatcher;
    
    private FileSizeWatcher _fileSizeWatcher;
    
    protected Circuit(){
        initWatchers();
        mLaps = new ArrayList<Lap>();
        addLaps();
    }
    
    public void setTurnSetups(TurnSetup[] turnSetups){
    	mLapSetups = turnSetups;
    }

	private void initWatchers() {
		_timeWatcher = new TimeWatcher();
		// _memoryWatcher = new MemoryWatcher();
		_fileSizeWatcher = new FileSizeWatcher();
	}
    
	/**
     * public official name for reporting
	 */
    public final String name(){
        String name = internalName();
        return name.substring(0,1).toUpperCase() + name.substring(1);
    }

    /**
     * internal name for BenchmarkSettings.properties
     */
    public final String internalName(){
        String name = this.getClass().getName();
        int pos = name.lastIndexOf(".");
        return name.substring(pos + 1).toLowerCase();
    }
    
    /**
     * describes the intent of this circuit, what it wants to test
     */
	public abstract String description();

    /**
     * @return the driver class needed to run on this Circuit
     */
    public abstract Class<? extends Driver> requiredDriver();
    
    /**
     * @return the methods that are intended to be run 
     */
    protected abstract void addLaps();
    
    public void add(Lap lap){
        mLaps.add(lap);
    }
    
    /**
     * setups are needed for reporting
     */
    public TurnSetup[] lapSetups(){
        return mLapSetups;
    }
    
    public List<Lap> laps() {
        return Collections.unmodifiableList(mLaps);
    }
    
    /**
     * calling all the laps for all the lapSetups
     */
    public TurnResult[] race( Team team, Car car, Driver driver){
        TurnResult[] results = new TurnResult[ mLapSetups.length ];

        int index = 0;
        
        Driver[] drivers = null;
    	
        boolean concurrent = team.isConcurrent() && driver.canConcurrent();
        
        if (concurrent) {
			drivers = new Driver[team.getConcurrentCount()];
			drivers[0] = driver;
			for (int i = 1; i < drivers.length; ++i) {
				drivers[i] = driver.clone();
			}
		}
        
        for(TurnSetup setup : mLapSetups) {
            System.out.println("*** Turn " + index);
            results[index++] = runTurn(team, car, driver, index, drivers, concurrent, setup);
        }
        return results;
    }

	private TurnResult runTurn(Team team, Car car, Driver driver, int index,
			Driver[] drivers, boolean concurrent, TurnSetup setup) {
		
		Map<Lap, Set<LapReading>> lapReadings = new HashMap<Lap, Set<LapReading>>();
		for (Lap lap : laps()) {
			
			if(lap.reportResult()) {
				lapReadings.put(lap, new HashSet<LapReading>());
			}
		}
		boolean warmUp = _numRuns > 1;
		for (int runIdx = 0; runIdx < _numRuns; runIdx++) {
			team.setUp();
			
			try {
				if (concurrent) {
					for (int i = 0; i < drivers.length; ++i) {
						drivers[i].takeSeatIn(car, setup);
					}
				} else {
					driver.takeSeatIn(car, setup);
				}
			} catch (CarMotorFailureException e1) {
				// FIXME reasonable exception handling
				throw new RuntimeException("Circuit aborted", e1);
			}
			
		    try {
		    	if (concurrent) {
					for (Driver d : drivers) {
						d.prepare();
					}
				} else {
					driver.prepare();
				}
		    } catch (CarMotorFailureException e) {
		        e.printStackTrace();
		    }        
			
			for(Lap lap : mLaps) {
				
            	System.out.println("*** Lap " + lap.name());

			    LapReading lapReading = runLap(team, driver, drivers, concurrent, setup, lap);
			    if(!warmUp && lap.reportResult()) {
			    	lapReadings.get(lap).add(lapReading);
			    }
			}
	
			if(concurrent) {
				for (Driver d : drivers) {
					d.backToPit();
				}
			} else {
				driver.backToPit();
			}
			
			tearDownTurn(team, driver, drivers);
			warmUp = false;
		}
		
		TurnResult turnResult = new TurnResult();
		for (Lap lap : laps()) {
			if(!lap.reportResult()) {
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
			Result lapResult = new Result(this, team, lap, setup, index, time, memory, fileSize, checkSum);
			turnResult.report(lapResult);
		}
		return turnResult;
	}

	private void tearDownTurn(Team team, Driver driver, Driver[] drivers) {
		team.tearDown();
		
		driver.circuitCompleted();
		if(drivers != null){
			for (int i = 0; i < drivers.length; i++) {
				drivers[i].circuitCompleted();
			}
		}
	}

	private LapReading runLap(Team team, Driver driver,
			Driver[] drivers, boolean concurrent, TurnSetup setup, Lap lap) {
		Method method = null; 
         
		try {
		    method = driver.getClass().getDeclaredMethod(lap.name(), (Class[])null);
		} catch (SecurityException e) {
		    e.printStackTrace();
		} catch (NoSuchMethodException e) {
		    e.printStackTrace();
		}
		
		if( ! lap.hot() ){
			if (concurrent) {
				for (Driver d : drivers) {
					d.backToPit();
				}
			} else {
				driver.backToPit();
			}
		    
		    try {
		    	if (concurrent) {
					for (Driver d : drivers) {
						d.prepare();
					}
				} else {
					driver.prepare();
				}
		    } catch (CarMotorFailureException e) {
		        e.printStackTrace();
		    }        
		}
		
		RunLapThread[] threads = null;
		if(concurrent) {
			threads = new RunLapThread[drivers.length];
			for(int i = 0; i < drivers.length; ++i) {
				threads[i] = new RunLapThread(method, drivers[i]);
			}
		}
		
		// _memoryWatcher.start();
		_timeWatcher.start();
		_fileSizeWatcher.monitorFile(team.databaseFile());
		_fileSizeWatcher.start();
		
		try {
			if(concurrent) {
				for (RunLapThread t : threads) {
					t.start();
				}
			} else {
				method.invoke(driver, (Object[]) null);
			}
		} catch (Exception e) {
		    System.err.println("Exception on calling method " + method);
		    e.printStackTrace();
		}
		
		if(concurrent) {
			for (RunLapThread t : threads) {
				try {
					t.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
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
	
	public static class NullMemoryUsage implements MemoryUsage {
		public long usedMemory() {
			return 0;
		}
	}
    
}

class RunLapThread extends Thread {
	Method method;
	Driver driver;
	public RunLapThread(Method method, Driver driver) {
		super();
		this.method = method;
		this.driver = driver;
	}
	public void run () {
		try {
			method.invoke(driver, (Object[]) null);
		} catch (Exception e) {
            System.err.println("Exception on calling method " + method);
            e.printStackTrace();
        }
	}
}