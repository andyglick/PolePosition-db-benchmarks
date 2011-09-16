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

import org.polepos.reporters.Reporter;

import java.util.List;

public class TimedLapsRacingStrategy implements RacingStrategy {
	
	private final TimedLapsCircuitBase _circuit;

	public TimedLapsRacingStrategy(TimedLapsCircuitBase circuit) {
		_circuit = circuit;
	}

	@Override
	public void race(Team team, Car car, Driver driver, List<Reporter> reporters) {

		long startTime = System.currentTimeMillis();
        
        TurnSetup[] setups = _circuit.lapSetups();
        TurnResult[] results = _circuit.race(team, car, driver);

        long stopTime = System.currentTimeMillis();
        long t = stopTime - startTime;
        System.out.println("Time[ms]: " + t);	

        for (Reporter reporter : reporters) {
            reporter.report(team, car, setups, results);
        }
		
	}

}
