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


package org.polepos.reporters;

import java.util.*;

import org.polepos.*;
import org.polepos.framework.*;
import org.polepos.reporters.CustomBarRendererBase.*;
import org.polepos.reporters.Graph.*;

public class RendererFactory {
	
	private static final double SCALE_FACTOR = 100;
	
    public static CustomBarRendererBase newRenderer(Graph graph) {
        if (graph.circuit().isFixedTime()) {
            if (Settings.LOGARITHMIC) {
                return new LogarithmicFixedTimeCustomBarRenderer(graph);
            }
            return new LinearFixedTimeCustomBarRenderer(graph);
        }
        if (Settings.LOGARITHMIC) {
            return new LogarithmicTimedLapsCustomBarRenderer(graph);
        }
        // return new LinearTimedLapsCustomBarRenderer(graph);
        graph = toFixedTime(graph);
        return new LinearFixedTimeCustomBarRenderer(graph);
    }

	private static Graph toFixedTime(final Graph oldGraph) {
		final Graph newGraph = new Graph(oldGraph.circuit(), oldGraph.lap());
		final long slowest = findSlowest(oldGraph);
		oldGraph.traverseResults(new ResultProcessor() {
			@Override
			public void process(TurnCombination turnCombination, Result result) {
				TimedLapsResult timedLapsResult = (TimedLapsResult) result;
				long time = timedLapsResult.getTime();
				double factor = (double)slowest / (double)time;
				long iterations = (long)(factor * SCALE_FACTOR);
				FixedTimeResult fixedTimeResult = new FixedTimeResult(oldGraph.circuit(),oldGraph.lap(), result.getTeam(), result.getSetup(), result.getLoadMonitoring(), result.getIndex(), iterations);
				newGraph.addResult(turnCombination.teamCar, fixedTimeResult);
			}
		});
		return newGraph;
	}

	private static long findSlowest(Graph oldGraph) {
		long slowest = 0;
		for(Result result : oldGraph.resultValues()){
			TimedLapsResult timedLapsResult = (TimedLapsResult) result;
			slowest = Math.max(slowest, timedLapsResult.getTime());
		}
		return slowest;
	}

}
