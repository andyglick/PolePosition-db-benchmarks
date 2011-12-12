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
import org.polepos.monitoring.LoadMonitoringResults;
import org.polepos.monitoring.MonitoringResult;
import org.polepos.monitoring.MonitoringType;

import java.util.*;


public class Graph {
	
	public static interface ResultProcessor {
		
		void process(TurnCombination turnCombination, Result result);
		
	}
    
    private final List<TeamCar>teamCars= new ArrayList<TeamCar>();
	private final List<TurnSetup> setups=new ArrayList<TurnSetup>();
    private final Map<TurnCombination,Result> results=new HashMap<TurnCombination,Result>();
	
    private final Circuit circuit;
    private final Lap lap;
    
    public long best;
    public long worst;
    
    public Graph(Circuit circuit, Lap lap){
        this.circuit = circuit;
        this.lap = lap;
    }
    
    Iterable<Result> resultValues(){
    	return results.values();
    }
    
    public void traverseResults(ResultProcessor processor){
    	for(Map.Entry<TurnCombination, Result> entry : results.entrySet()){
    		processor.process(entry.getKey(), entry.getValue());
    	}
    }

    public void addResult(TeamCar teamCar, Result result) {
        TurnSetup setup = result.getSetup();
        results.put(new TurnCombination(teamCar,setup),result);
		if(!teamCars.contains(teamCar)) {
			teamCars.add(teamCar);
		}
		if(!setups.contains(setup)) {
			setups.add(setup);
		}
    }
    
    public Circuit circuit(){
        return circuit;
    }
    
    public Lap lap(){
        return lap;
    }
    
    public void compareCheckSums(){
        
        for (TurnSetup setup : setups()){
            
            long checkSum = 0;
            boolean first = true;
            
            for (TeamCar teamCar: teamCars()){
                
                if(first){
                    Result res = results.get(new TurnCombination(teamCar,setup)); 
                    if(res != null){
                        checkSum = res.getCheckSum();
                        first = false;
                    }
                } else{
                    Result res = results.get(new TurnCombination(teamCar,setup));
                    if(res != null){
                        if(checkSum != res.getCheckSum()){
                        	System.err.println("Inconsistent checksum for " + teamCar.getTeam().name() + "/" + teamCar.getCar().name() + " in " + circuit.name() + ":" + lap.name());
                        }
                    }
                }
            }
        }
    }
	
	
	public List<TeamCar> teamCars() {
		return Collections.unmodifiableList(teamCars);
	}

	public List<TurnSetup> setups() {
		return Collections.unmodifiableList(setups);
	}
	
	public final long timeFor(TeamCar teamCar, TurnSetup setup) {
		return timeFor(new TurnCombination(teamCar, setup));
	}

	public final LoadMonitoringResults loadMonitoring(TeamCar teamCar, TurnSetup setup) {
		Result res = results.get((new TurnCombination(teamCar,setup)));
        return res.getLoadMonitoring();
	}
	
	public final long timeFor(TurnCombination turnCombination) {
        Result res = results.get(turnCombination);
        if(res == null){
            return Integer.MAX_VALUE;
        }
		return res.getTime();
	}

	public final long iterationsFor(TeamCar teamCar, TurnSetup setup) {
		return iterationsFor(new TurnCombination(teamCar,setup));
	}

	public long iterationsFor(TurnCombination turnCombination) {
        Result res = results.get(turnCombination);
        if(res == null){
            return 0;
        }
		return res.getIterations();
	}
	
	public final long memoryFor(TeamCar teamCar, TurnSetup setup) {
	    Result res = results.get(new TurnCombination(teamCar,setup));
        if(res == null){
            return Integer.MAX_VALUE;
        }
		return res.getMemory();
	}
	
	public final long sizeFor(TeamCar teamCar, TurnSetup setup) {
	    Result res = results.get(new TurnCombination(teamCar,setup));
        if(res == null){
            return Integer.MAX_VALUE;
        }
		return res.getDatabaseSize();
	}

    public Collection<MonitoringType> availableMonitoryingTypes() {
        Set<MonitoringType> types = new HashSet<MonitoringType>();
        for (Result result : results.values()) {
            for (MonitoringResult monitoringResult : result.getLoadMonitoring()) {
                types.add(monitoringResult.getType());
            }
        }
        return types;
    }

    class TurnCombination {
        
        final TeamCar teamCar;
        final TurnSetup setup;
		
		public TurnCombination(TeamCar teamCar, TurnSetup setup) {
			this.teamCar = teamCar;
			this.setup = setup;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj==this) {
				return true;
			}
			if(obj==null||obj.getClass()!=getClass()) {
				return false;
			}
			TurnCombination key=(TurnCombination)obj;
			return teamCar.equals(key.teamCar) && setup.equals(key.setup);
		}
		
		@Override
		public int hashCode() {
			return teamCar.hashCode() + setup.hashCode();
		}
	}

}
