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

public class LogarithmicTimedLapsCustomBarRenderer extends TimedLapsCustomBarRendererBase {
	public LogarithmicTimedLapsCustomBarRenderer(Graph graph) {
		super(graph);
	}

	@Override
	protected int barWidth(TeamData teamData) {
		return (int) (barUnits(teamData.orderOfMagnitude) * widthPerOrderOfMagnitude());
	}

	@Override
	protected String legendInsideBar(TeamData teamData) {
		return teamData.val+ "ms";
	}

	@Override
	protected String legendOnRightOfBar(TeamData teamData) {
		return String.format("%.1fx", teamData.orderOfMagnitude);
	}

	@Override
	protected String magnitudeAxisLegend(int i) {
		if(i == 1){
			return "best";
		}
		return String.format("%.0fx", Math.pow(10, i-1));
	}

	@Override
	protected boolean doDrawXAxisMarker(int i) {
		return true;
	}

}
