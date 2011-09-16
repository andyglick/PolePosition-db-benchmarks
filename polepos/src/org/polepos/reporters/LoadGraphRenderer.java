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

import org.polepos.framework.TeamCar;
import org.polepos.framework.TurnSetup;
import org.polepos.monitoring.LoadMonitoringResults;

import java.awt.*;

/**
 * @author roman.stoffel@gamlor.info
 * @since 15.09.11
 */
public final class LoadGraphRenderer extends TimedLapsCustomBarRendererBase {

    public LoadGraphRenderer(Graph graph) {
        super(graph);
    }
	@Override
	protected boolean doDrawXAxisMarker(int i) {
		return true;
	}

	@Override
	protected int barWidth(TeamData teamData) {
		return (int) (((double)teamData.val / (double)graph.worst) * maxBarWidthWithLegend());
	}

	@Override
	protected String legendOnRightOfBar(TeamData teamData) {
		return teamData.val + "% CPU";
	}

	@Override
	protected String magnitudeAxisLegend(int i) {
		if(i == 1){
			return "best";
		}
		return String.format("%.0fx", Math.pow(10, i-1));
	}

	@Override
	protected String legendInsideBar(TeamData teamData) {
		return "";
	}

	@Override
	double maxBarWidthWithLegend() {
		String worst = "" + graph.worst + " % CPU";
		return maxBarWidth() - textWidth(worst) -1;
	}

    @Override
    protected long valueToShow(Graph graph, TurnSetup setup, TeamCar teamCar) {
        final LoadMonitoringResults loadMonitoringResults = graph.loadMonitoring(teamCar, setup);
        if(loadMonitoringResults.iterator().hasNext()){
            return (long)(100L * loadMonitoringResults.iterator().next().getValue());
        } else{
            return 0L;
        }
    }

    @Override
	protected int renderXLegend(Graphics graphics, int y, int axisX) {
        String legend = "System CPU Load in % (less is better)";
		graphics.setFont(graphData().turnLegendFont);
		graphics.drawString(legend, axisX, (int)(y+graphData().markerHeight+textHeight(legend)));
		return (int) (y + stride()*2);
	}


}
