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

public abstract class TimedLapsCustomBarRendererBase extends
		CustomBarRendererBase {

	public TimedLapsCustomBarRendererBase(Graph graph) {
		super(graph);
	}

	protected List<TurnData> prepareTurnsData(final Graph graph) {
		
		graph.worst = 0;
		
		List<TurnData> runs = new ArrayList<TurnData>();
	
		final List<TurnSetup> setups = graph.setups();
		List<TeamCar> teamCars = graph.teamCars();
	
		for (final TurnSetup setup : setups) {
	
			TurnData turnData = new TurnData(setup, 0);
			long best = Long.MAX_VALUE;
			long worst = 0;
			for (TeamCar teamCar : teamCars) {
				long time = graph.timeFor(teamCar, setup);
				TeamData teamData = new TeamData(teamCar, 0, time);
				if (time != 0 && time < best) {
					best = time;
				}
				if(time > graph.worst){
					graph.worst = time;
				}
				turnData.teams.add(teamData);
			}
			turnData.best = best == 0 ? 1 : best;
	
			for (TeamData teamData : turnData.teams) {
				teamData.orderOfMagnitude = teamData.val / (double)turnData.best;
			}
	
			Collections.sort(turnData.teams, new Comparator<TeamData>() {
	
				@Override
				public int compare(TeamData team1, TeamData team2) {
					long time1 = graph.timeFor(team1.teamCar, setup);
					long time2 = graph.timeFor(team2.teamCar, setup);
	
					if (time1 > time2) {
						return 1;
					}
					if (time1 < time2) {
						return -1;
					}
					return 0;
				}
			});
	
			runs.add(turnData);
		}
		return runs;
	}

}