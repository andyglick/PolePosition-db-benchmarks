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
import org.polepos.reporters.CustomBarRendererBase.*;

public class FixedTimeCustomBarRenderer extends CustomBarRendererBase {
	
	private static final int BEST_MAGNITUDE = 5;

	public FixedTimeCustomBarRenderer(Graph graph) {
		super(graph);
	}

	@Override
	protected List<TurnData> prepareTurnsData(final Graph graph) {
		List<TurnData> runs = new ArrayList<TurnData>();

		final List<TurnSetup> setups = graph.setups();
		List<TeamCar> teamCars = graph.teamCars();

		for (final TurnSetup setup : setups) {

			TurnData turnData = new TurnData(setup, 0);
			long best = 0;
			for (TeamCar teamCar : teamCars) {
				long iterations = graph.iterationsFor(teamCar, setup);
				TeamData teamData = new TeamData(teamCar, 0, iterations);
				if (iterations != 0 && iterations > best) {
					best = iterations;
				}
				turnData.teams.add(teamData);
			}
			turnData.best = best;

			for (TeamData teamData : turnData.teams) {
				teamData.orderOfMagnitude = (double)turnData.best / (teamData.val == 0 ? 1 : teamData.val) ;
			}

			Collections.sort(turnData.teams, new Comparator<TeamData>() {

				@Override
				public int compare(TeamData team1, TeamData team2) {
					long iterations1 = graph.iterationsFor(team1.teamCar, setup);
					long iterations2 = graph.iterationsFor(team2.teamCar, setup);

					if (iterations1 < iterations2) {
						return 1;
					}
					if (iterations1 > iterations2) {
						return -1;
					}
					return 0;
				}
			});

			runs.add(turnData);
		}
		return runs;
	}

	@Override
	protected int barWidth(TeamData teamData) {
		return (int)((BEST_MAGNITUDE - barUnits(teamData.orderOfMagnitude)) * widthPerOrderOfMagnitude());
	}

	@Override
	protected String taskLegend() {
		return " iterations";
	}

	@Override
	protected String magnitudeBarLegend(TeamData teamData) {
		
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

}
