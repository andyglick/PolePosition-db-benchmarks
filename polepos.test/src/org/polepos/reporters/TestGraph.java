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

import junit.framework.Assert;
import org.junit.Test;
import org.polepos.framework.TeamCar;
import org.polepos.framework.TimedLapsResult;
import org.polepos.monitoring.MonitoringType;
import org.polepos.util.CarStub;

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.polepos.monitoring.MonitoringResult.create;

/**
 * @author roman.stoffel@gamlor.info
 * @since 19.09.11
 */
public class TestGraph {


    @Test
    public void returnsMonitoringTypes(){
        final Graph graph = TestDataFactory.createEmptyGraph();
        final MonitoringType cpu = MonitoringType.create("CPU");
        final MonitoringType memory = MonitoringType.create("Memory");
        final TimedLapsResult result = TestDataFactory.createResult(graph, 100, "t-1",
                asList(create(cpu, 0.5),
                        create(memory, 0.3)));
        graph.addResult(new TeamCar(result.getTeam(), new CarStub(result.getTeam())), result);

        final Collection<MonitoringType> types = graph.availableMonitoryingTypes();
        Assert.assertTrue(types.contains(cpu));
        Assert.assertTrue(types.contains(memory));
    }


}
