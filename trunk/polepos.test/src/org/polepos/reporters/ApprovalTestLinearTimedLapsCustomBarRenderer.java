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
import org.polepos.util.CarStub;
import org.polepos.util.TeamStub;

import java.awt.*;

import static org.polepos.reporters.TestDataFactory.createEmptyGraph;
import static org.polepos.reporters.TestDataFactory.createResult;

/**
 * @author roman.stoffel@gamlor.info
 * @since 16.09.11
 */
public class ApprovalTestLinearTimedLapsCustomBarRenderer extends RenderingApprovalBase{

    private Graph graph;

    @Override
    protected void additionalSetup() {

        this.graph = createEmptyGraph();
    }

    @Test
    public void oneItemGraph() throws Exception {
        final TimedLapsResult result = createResult(graph, 100, TeamStub.DEFAULT_NAME);
        graph.addResult(new TeamCar(result.getTeam(), new CarStub(result.getTeam(),Color.BLUE)), result);


        LinearTimedLapsCustomBarRenderer toTest = new LinearTimedLapsCustomBarRenderer(graph);

        toTest.render(graphic());
        Approvals.approve(image());
    }
    @Test
    public void threeItemResult() throws Exception {
        addTimeToGraph(100,"t-1",Color.BLUE);
        addTimeToGraph(75,"t-2",Color.GREEN);
        addTimeToGraph(50,"t-3",Color.RED);


        LinearTimedLapsCustomBarRenderer toTest = new LinearTimedLapsCustomBarRenderer(graph);

        toTest.render(graphic());
        Approvals.approve(image());
    }

    private void addTimeToGraph(int timeInMilliSec,String teamName,Color color) {
        final TimedLapsResult result = createResult(graph,timeInMilliSec,teamName);
        graph.addResult(new TeamCar(result.getTeam(), new CarStub(result.getTeam(),color)), result);
    }
}
