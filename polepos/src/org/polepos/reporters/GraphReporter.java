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

import org.polepos.framework.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class GraphReporter extends ReporterBase {
	
	protected Map<CircuitLap,Graph> mGraphs= new LinkedHashMap<CircuitLap,Graph>();
	private java.util.List<Circuit> mCircuits= new ArrayList <Circuit>();
	public static int timeIndex = 0;
	public static int memoryIndex = 0;
	public static int sizeIndex = 0;

	public GraphReporter(String path) {
		super(path);
	}

	@Override
	public void startSeason() {
	}

	@Override
	public boolean append() {
	    return false;
	}

	@Override
	public void reportTaskNames(String[] taskNames) {
	    // do nothing
	}

	@Override
	public void reportTeam(Team team) {
	    // do nothing
	}

	@Override
	public void reportCar(Car car) {
	    // do nothing
	}

	@Override
	public void beginResults() {
	}

	@Override
	public void reportResult(Result result) {
	    Circuit circuit = result.getCircuit();
	    
	    if(! mCircuits.contains(circuit)){
	        mCircuits.add(circuit);
	    }
	    
	    CircuitLap cl = new CircuitLap(circuit, result.getLap());
	    Graph graph = mGraphs.get(cl);
	    if(graph == null){
	        graph = new Graph(result.getCircuit(), result.getLap());
	        mGraphs.put(cl, graph);
	    }
	    graph.addResult(_teamCar, result);
	    
	}

	public void graphs(Map<CircuitLap,Graph> mGraphs) {
		this.mGraphs = mGraphs;
	}

    @Override
	public void endSeason() {
		if(mGraphs != null){
            for (CircuitLap circuitLap : mGraphs.keySet()) {
            	report(mGraphs.get(circuitLap));
			}
        }
	}
	
	protected abstract void report(Graph graph);
}