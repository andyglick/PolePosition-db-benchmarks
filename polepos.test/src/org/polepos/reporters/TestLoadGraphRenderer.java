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
import org.polepos.framework.FixedTimeResult;
import org.polepos.framework.TeamCar;
import org.polepos.framework.TimedLapsResult;
import org.polepos.monitoring.MonitoringResult;
import org.polepos.monitoring.MonitoringType;
import org.polepos.util.CarStub;
import org.polepos.util.CircuitStub;

import java.awt.*;
import java.util.Collections;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.polepos.monitoring.MonitoringResult.create;
import static org.polepos.reporters.TestDataFactory.*;

/**
 * @author roman.stoffel@gamlor.info
 * @since 16.09.11
 */
public class TestLoadGraphRenderer extends RenderingApprovalBase {

    private static final MonitoringType CPU_LOAD = MonitoringType.create("CPU-Usage", " ms CPU","ms");
    private static final MonitoringType MEMORY_LOAD = MonitoringType.create("Memory-Load", "kb Mem.","kb");
    private static final MonitoringType BYTES_SENT = MonitoringType.create("Bytes sent"," kb","kb");
    private Graph graph;

    @Override
    protected void additionalSetup() {

        this.graph = createEmptyGraph();
    }

    @Test
    public void emptyResult() {
        LoadGraphRenderer toTest = LoadGraphRenderer.create(graph,CPU_LOAD);


        toTest.render(graphic());
        Approvals.approve(image());
    }
    @Test
    public void noReportAvailable() {
        LoadGraphRenderer toTest = LoadGraphRenderer.create(graph,CPU_LOAD);
        final TimedLapsResult result = createResult(graph, 100, "t-1", Collections.<MonitoringResult>emptyList());
        graph.addResult(new TeamCar(result.getTeam(),new CarStub(result.getTeam(), Color.GREEN)),result );


        toTest.render(graphic());
        Approvals.approve(image());
    }

    @Test
    public void singleMonitoringResult() {
        LoadGraphRenderer toTest = LoadGraphRenderer.create(graph, CPU_LOAD);

        final TimedLapsResult result = createResult(graph, 100, "t-1", singleton(create(CPU_LOAD, 50)));
        graph.addResult(new TeamCar(result.getTeam(),new CarStub(result.getTeam(), Color.GREEN)),result );

        toTest.render(graphic());
        Approvals.approve(image());
    }
    @Test
    public void printMemoryLoad() {
        LoadGraphRenderer toTest = LoadGraphRenderer.create(graph,MEMORY_LOAD);

        addTeam("t-1", 50, 8000, Color.GREEN);
        addTeam("t-1", 50, 7000, Color.BLUE);

        toTest.render(graphic());
        Approvals.approve(image());
    }
    @Test
    public void useOtherUnits() {
        LoadGraphRenderer toTest = LoadGraphRenderer.create(graph,BYTES_SENT);


        final TimedLapsResult result = createResult(graph, 100, "t-1", singleton(create(BYTES_SENT, 100.0)));
        graph.addResult(new TeamCar(result.getTeam(), new CarStub(result.getTeam(), Color.GREEN)), result);

        toTest.render(graphic());
        Approvals.approve(image());
    }
    @Test
    public void multipleTeams() {
        LoadGraphRenderer toTest = LoadGraphRenderer.create(graph, CPU_LOAD);

        addTeam("t-1",50,70, Color.GREEN);
        addTeam("t-2", 30, 750, Color.BLUE);

        toTest.render(graphic());
        Approvals.approve(image());
    }
    @Test
    public void rendersPerIterationGraph() {
        this.graph = createEmptyGraph(new CircuitStub(true));
        LoadGraphRenderer toTest = LoadGraphRenderer.create(graph, CPU_LOAD);

        addTeamWithFixedResult("t-1",50,250,300, Color.GREEN);
        addTeamWithFixedResult("t-2", 100, 500, 600, Color.BLUE);
        addTeamWithFixedResult("t-3", 200, 500, 600, Color.CYAN);

        toTest.render(graphic());
        Approvals.approve(image());
    }

    private void addTeam(String teamName, double cpuLoad, double memLoad, Color color) {
        final TimedLapsResult result = createResult(graph, 100, teamName,
                asList(create(CPU_LOAD, cpuLoad),create(MEMORY_LOAD, memLoad)));
        graph.addResult(new TeamCar(result.getTeam(), new CarStub(result.getTeam(), color)), result);
    }

    private void addTeamWithFixedResult(String teamName, int iterations, double cpuLoad, double memLoad, Color color) {
        final FixedTimeResult result = createFixedTimeResult(graph, iterations, teamName,
                asList(create(CPU_LOAD, cpuLoad), create(MEMORY_LOAD, memLoad)));
        graph.addResult(new TeamCar(result.getTeam(),new CarStub(result.getTeam(), color)),result );
    }
}
