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

import java.util.*;

import org.polepos.reporters.*;
import org.polepos.util.*;

public class FixedTimeRacingStrategy implements RacingStrategy {
	

	private final FixedTimeCircuitBase _circuit;

	public FixedTimeRacingStrategy(FixedTimeCircuitBase circuit) {
		_circuit = (FixedTimeCircuitBase) circuit;
	}

	@Override
	public void race(Team team, Car car, Driver driver, List<Reporter> reporters) {
		TurnSetup[] turnSetups = _circuit.turnSetups();
		TurnResult[] results = new TurnResult[ turnSetups.length];
		for (int i = 0; i < turnSetups.length; i++) {
			Result result = raceTurn(team, car, driver, turnSetups[i], i);
			TurnResult turnResult = new TurnResult();
			turnResult.report(result);
			results[i] = turnResult;
		}
        for (Reporter reporter : reporters) {
            reporter.report(team, car, turnSetups, results);
        }
		
	}

	private Result raceTurn(Team team, Car car, Driver driver, TurnSetup setup, int setupIndex) {
		car.team().setUp();
		
		driver.configure(car, setup);
		driver.prepare();
		// fill database
		
		driver.closeDatabase();
		
		MemoryUtil.gc();
		
		int threadCount = setup.getThreadCount();
		
		int time = setup.getTime();
		
		DriverBase[] drivers = new DriverBase[threadCount];
		ConcurrentTurnRacer[] racers = new ConcurrentTurnRacer[threadCount];
		Thread[] threads = new Thread[threadCount];
		
		for (int i = 0; i < threads.length; i++) {
			drivers[i] = ((DriverBase)driver).clone();
			// drivers[i].configure(car, setup);
			drivers[i].prepare();
			drivers[i].bulkId(i + 1);
			racers[i] = new ConcurrentTurnRacer((FixedTimeDriver)drivers[i]);
			threads[i] = new Thread(racers[i]);
		}
		
		for (int i = 0; i < threads.length; i++) {
			threads[i].start();
		}
		
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < threads.length; i++) {
			racers[i].stop();
		}
		
		for (int i = 0; i < threads.length; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		long iterations = 0;
		
		for (int i = 0; i < threads.length; i++) {
			drivers[i].closeDatabase();
			iterations += racers[i].iterations();
		}
		
		
		// FIXME: Change to FixedTimeResult
		// Result result = new FixedTimeResult(_circuit, _circuit.laps().get(0), team,turnSetups[i],i,iterations);
		
		Result result = new TimedLapsResult(_circuit, team,_circuit.laps().get(0), setup ,setupIndex,iterations,0,0,0);
		
		team.tearDown();
		driver.circuitCompleted();
		return result;
	}
	
	private static class ConcurrentTurnRacer implements Runnable {
		
		private long _iterations;
		
		private volatile boolean _stopped;
		
		private FixedTimeDriver _driver;
		
		ConcurrentTurnRacer(FixedTimeDriver driver){
			_driver = driver;
		}

		@Override
		public void run() {
			while(! _stopped){
				_driver.race();
				_iterations++;
			}
			
		}
		
		public void stop(){
			_stopped = true;
		}
		
		public long iterations(){
			return _iterations;
		}
	}


}
