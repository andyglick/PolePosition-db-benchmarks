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

import org.polepos.framework.*;

public class LogarithmicFixedTimeCustomBarRenderer extends FixedTimeCustomBarRendererBase {
	
	private static final int BEST_MAGNITUDE = 5;

	public LogarithmicFixedTimeCustomBarRenderer(Graph graph) {
		super(graph);
	}


	@Override
	protected int barWidth(TeamData teamData) {
		return (int)((BEST_MAGNITUDE - barUnits(teamData.orderOfMagnitude)) * widthPerOrderOfMagnitude());
	}

	@Override
	protected String legendOnRightOfBar(TeamData teamData) {
		
		double orderOfMagnitude = teamData.orderOfMagnitude;
		if(orderOfMagnitude == 1){
			return "";
		}
		return String.format("1/%.1fx", orderOfMagnitude);
	}

	@Override
	protected String magnitudeAxisLegend(int i) {
		if(i == BEST_MAGNITUDE - 1){
			return "best";
		}
		if(i > BEST_MAGNITUDE - 1){
			return "";
		}
		return String.format("1/%.0fx", Math.pow(10, BEST_MAGNITUDE - 1 - i));
	}

	@Override
	protected boolean doDrawXAxisMarker(int i) {
		return i < BEST_MAGNITUDE;
	}
	
	@Override
	protected String legendInsideBar(TeamData teamData) {
		return teamData.val+ " iterations";
	}


}
