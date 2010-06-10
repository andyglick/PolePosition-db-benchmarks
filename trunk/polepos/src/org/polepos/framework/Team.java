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

import java.util.*;

/**
 * a specific database category or engine that requires specific source code
 */
public abstract class Team 
{
    // whether run test concurrently, false by default
	private boolean concurrent;
	
	// total concurrent thread count
	private int concurrentCount = 0;
    
    public int getConcurrentCount() {
		return concurrentCount;
	}

	public void setConcurrentCount(int concurrentCount) {
		if (concurrentCount > 0) {
			concurrent = true;
			this.concurrentCount = concurrentCount;
		} else {
			concurrent = false;
			this.concurrentCount = 0;
		}
	}

	public boolean isConcurrent() {
		return concurrent;
	}

    public abstract String name();
    
    public abstract String description();
    
    public abstract Car[] cars();
    
    public abstract DriverBase[] drivers();
    
    public abstract String website();
    
    public abstract String databaseFile();
    
    protected void setUp() {
    	
    }
    
    protected void tearDown() {
    	
    }
    
    public Driver[] nominate(Circuit circuit) {
        Vector <DriverBase> vec = new Vector <DriverBase> ();
        DriverBase[] drivers = drivers();
        for (int i = 0; i < drivers.length; i++) {
            if(circuit.requiredDriver().isInstance(drivers[i])){
                vec.add(drivers[i]);
            }
        }
        DriverBase[] result = new DriverBase[vec.size()];
        vec.toArray(result);
        return result;
    }
    
    /**
     * Override to apply special options and configuration settings to the test run
     */
	public void configure(int[] options, ConfigurationSetting[] configurations) {
		
	}

}
