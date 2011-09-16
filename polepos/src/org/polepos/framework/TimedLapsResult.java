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


package org.polepos.framework;

import org.polepos.monitoring.LoadMonitoringResults;

public class TimedLapsResult extends Result{
	
    private final long _time;
    
    private final long _checkSum;
    
    private final long _memory;    
    
    private final long _databaseSize;
    
	public TimedLapsResult(Circuit circuit, Team team, Lap lap,
			TurnSetup setup,
                  LoadMonitoringResults loadMonitoring, int index, long time, long memory,
			long databaseSize, long checkSum) {
		super(circuit, lap, team, setup,loadMonitoring, index);
		
		_time = time;
		_checkSum = checkSum;
		_memory = memory;
		_databaseSize = databaseSize;
	}
	
    public long getTime(){
    	return _time;
    }
    
    public long getCheckSum(){
        return _checkSum;
    }
    
    public long getMemory(){
        return _memory;
    }
    
    public long getDatabaseSize() {
    	return _databaseSize;
    }

	@Override
	public long getIterations() {
		return 0;
	}
    

}
