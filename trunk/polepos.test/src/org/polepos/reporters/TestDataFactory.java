/*
 * This file is part of the PolePosition database benchmark
 * http://www.polepos.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA  02111-1307, USA.MA  02111-1307, USA.
 */

package org.polepos.reporters;

import org.polepos.framework.Lap;
import org.polepos.framework.TimedLapsResult;
import org.polepos.framework.TurnSetup;
import org.polepos.monitoring.MonitoringResult;
import org.polepos.util.CircuitStub;
import org.polepos.util.TeamStub;

import java.util.Collection;
import java.util.Collections;

import static org.polepos.monitoring.LoadMonitoringResults.create;

/**
 * @author roman.stoffel@gamlor.info
 * @since 16.09.11
 */
final class TestDataFactory {


    private TestDataFactory(){}


    static Graph createEmptyGraph() {
        return new Graph(new CircuitStub(), new Lap("stub",true,true,false));
    }

    static TimedLapsResult createResult(Graph forGraph,int time,String teamName) {
        return createResult(forGraph, time, teamName, Collections.<MonitoringResult>emptyList());
    }

    static TimedLapsResult createResult(Graph forGraph, int time, String teamName, Collection<MonitoringResult> monitoringResults) {
        return new TimedLapsResult(
                forGraph.circuit(), new TeamStub(teamName),forGraph.lap(),
                new TurnSetup(), create(monitoringResults),0,time,0,0,0);
    }
}
