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

import org.approvaltests.Approvals;
import org.junit.Test;
import org.polepos.framework.TeamCar;
import org.polepos.framework.TimedLapsResult;
import org.polepos.monitoring.MonitoringResult;
import org.polepos.monitoring.MonitoringType;
import org.polepos.util.CarStub;

import java.awt.*;
import java.util.Collections;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.polepos.monitoring.MonitoringResult.create;
import static org.polepos.reporters.TestDataFactory.createEmptyGraph;
import static org.polepos.reporters.TestDataFactory.createResult;

/**
 * @author roman.stoffel@gamlor.info
 * @since 16.09.11
 */
public class TestLoadGraphRenderer extends RenderingApprovalBase {

    private static final MonitoringType CPU_LOAD = MonitoringType.percentUnit("CPU-Load", "% CPU");
    private static final MonitoringType MEMORY_LOAD = MonitoringType.percentUnit("Memory-Load", "% Mem.");
    private static final MonitoringType BYTES_SENT = MonitoringType.create("Bytes sent"," kb","kb", 1);
    private Graph graph;

    @Override
    protected void additionalSetup() {

        this.graph = createEmptyGraph();
    }

    @Test
    public void emptyResult() {
        LoadGraphRenderer toTest = new LoadGraphRenderer(graph,CPU_LOAD);


        toTest.render(graphic());
        Approvals.approve(image());
    }
    @Test
    public void noReportAvailable() {
        LoadGraphRenderer toTest = new LoadGraphRenderer(graph,CPU_LOAD);
        final TimedLapsResult result = createResult(graph, 100, "t-1", Collections.<MonitoringResult>emptyList());
        graph.addResult(new TeamCar(result.getTeam(),new CarStub(result.getTeam(), Color.GREEN)),result );


        toTest.render(graphic());
        Approvals.approve(image());
    }

    @Test
    public void singleMonitoringResult() {
        LoadGraphRenderer toTest = new LoadGraphRenderer(graph, CPU_LOAD);

        final TimedLapsResult result = createResult(graph, 100, "t-1", singleton(create(CPU_LOAD, 0.5)));
        graph.addResult(new TeamCar(result.getTeam(),new CarStub(result.getTeam(), Color.GREEN)),result );

        toTest.render(graphic());
        Approvals.approve(image());
    }
    @Test
    public void printMemoryLoad() {
        LoadGraphRenderer toTest = new LoadGraphRenderer(graph,MEMORY_LOAD);

        addTeam("t-1", 0.5, 0.8, Color.GREEN);
        addTeam("t-1", 0.5, 0.7, Color.BLUE);

        toTest.render(graphic());
        Approvals.approve(image());
    }
    @Test
    public void useOtherUnits() {
        LoadGraphRenderer toTest = new LoadGraphRenderer(graph,BYTES_SENT);


        final TimedLapsResult result = createResult(graph, 100, "t-1", singleton(create(BYTES_SENT, 100.0)));
        graph.addResult(new TeamCar(result.getTeam(), new CarStub(result.getTeam(), Color.GREEN)), result);

        toTest.render(graphic());
        Approvals.approve(image());
    }
    @Test
    public void multipleTeams() {
        LoadGraphRenderer toTest = new LoadGraphRenderer(graph, CPU_LOAD);

        addTeam("t-1",0.5,0.7, Color.GREEN);
        addTeam("t-2",0.3,0.75, Color.BLUE);

        toTest.render(graphic());
        Approvals.approve(image());
    }

    private void addTeam(String teamName, double cpuLoad, double memLoad, Color color) {
        final TimedLapsResult result = createResult(graph, 100, teamName,
                asList(create(CPU_LOAD, cpuLoad),create(MEMORY_LOAD, memLoad)));
        graph.addResult(new TeamCar(result.getTeam(),new CarStub(result.getTeam(), color)),result );
    }
}
