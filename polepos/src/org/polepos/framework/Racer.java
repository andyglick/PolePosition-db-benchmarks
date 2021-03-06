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

import org.polepos.monitoring.Monitoring;
import org.polepos.reporters.Reporter;

import java.util.List;

public class Racer implements Runnable {

    private final List<Circuit> circuits;

    private final List<Team> teams;

    private final List<Reporter> reporters;

    private final Monitoring monitoring = Monitoring.createInstance();


    public Racer(List<Circuit> circuits_, List<Team> teams_, List<Reporter> reporters_) {
        circuits = circuits_;
        teams = teams_;
        reporters = reporters_;
    }

    public void run() {

        synchronized (this) {

            long start = System.currentTimeMillis();

            for (Reporter reporter : reporters) {
                reporter.startSeason();
            }


            for (Team team : teams) {

                for (Car car : team.cars()) {
                    runCircuitWithCar(team, car);
                }
            }


            for (Reporter reporter : reporters) {
                reporter.endSeason();
            }

            long stop = System.currentTimeMillis();
            long duration = stop - start;

            System.out.println("\n****************************************************");
            System.out.println("The F1 season was run O.K. without lethal accidents.");
            System.out.println("****************************************************\n");
            System.out.println("Overall time taken: " + duration + "ms\n");
            System.out.println("Reporters present:");
            for (Reporter reporter : reporters) {
                System.out.println(reporter);
            }
            this.notify();
        }

    }

    private void runCircuitWithCar(Team team, Car car) {
        for (Circuit circuit : circuits) {

            Driver[] drivers = circuit.nominate(team);

            if (drivers == null || drivers.length == 0) {

                for (Reporter reporter : reporters) {
                    reporter.noDriver(team, circuit);
                }

            } else {

                System.out.println("\n** Racing " + team.name() + "/"
                        + car.name() + " on " + circuit.name() + "\n");

                for (Reporter reporter : reporters) {
                    reporter.sendToCircuit(circuit);
                }

                for (Driver driver : drivers) {
                    System.out.println("** On track: " + team.name() + "/" + car.name());
                    RacingStrategy racingStrategy = circuit.racingStrategy();
                    racingStrategy.race(monitoring,team, car, driver, reporters);
                }

            }

        }
    }
}
