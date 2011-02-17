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

public abstract class CircuitBase implements Circuit {

	protected TurnSetup[] _turnSetups;
	
	protected Circuit _reportTo = this;

	@Override
	public void setTurnSetups(TurnSetup[] turnSetups) {
		_turnSetups = turnSetups;
	}

	/**
	 * public official name for reporting
	 */
	@Override
	public String name() {
	    String name = internalName();
	    return name.substring(0,1).toUpperCase() + name.substring(1);
	}

	/**
	 * internal name for BenchmarkSettings.properties
	 */
	@Override
	public String internalName() {
	    String name = className();
	    int pos = name.lastIndexOf(".");
	    return name.substring(pos + 1).toLowerCase();
	}

	protected String className() {
		return circuitClass().getName();
	}

	protected Class<?> circuitClass() {
		return this.getClass();
	}

	@Override
	public abstract List<Lap> laps();

	@Override
	public Driver[] nominate(Team team) {
		return team.nominate(this);
	}

	public void reportTo(Circuit circuit) {
		_reportTo = circuit;
	}

	public TurnSetup[] turnSetups() {
		return _turnSetups;
	}
    /**
     * setups are needed for reporting
     */
    @Override
	public TurnSetup[] lapSetups(){
        return _turnSetups;
    }


}
