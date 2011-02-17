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

package org.polepos;


import java.util.*;

import org.polepos.circuits.arraylists.*;
import org.polepos.circuits.commits.*;
import org.polepos.circuits.complex.*;
import org.polepos.circuits.flatobject.*;
import org.polepos.circuits.inheritancehierarchy.*;
import org.polepos.circuits.nativeids.*;
import org.polepos.circuits.nestedlists.*;
import org.polepos.circuits.strings.*;
import org.polepos.circuits.trees.*;
import org.polepos.framework.*;
import org.polepos.runner.db4o.*;
import org.polepos.teams.db4o.*;

/**
 * Please read the README file in the home directory first.
 * 
 */
public class Db4oVersionRace extends AbstractDb4oVersionsRaceRunner{
    
    public static void main(String[] arguments) {
        new Db4oVersionRace().run();
    }
    
    public Team[] teams() {
        List<Team> teamList = new ArrayList<Team>();
        
        int[] clientServerOptions = new int[] { Db4oOptions.CLIENT_SERVER,
                Db4oOptions.CLIENT_SERVER_TCP };
        
        teamList.add(new Db4oTeam());
        teamList.add(db4oTeam(clientServerOptions));
        
//        String db4oCurrentVersion = System.getProperty("polepos.db4o.current");
//        if (db4oCurrentVersion != null) {
//            teamList.add(db4oTeam(db4oCurrentVersion));
//            teamList.add(db4oTeam(db4oCurrentVersion, options));
//        }
//        
//        teamList.add(db4oTeam(Db4oVersions.JAR78));
//        teamList.add(db4oTeam(Db4oVersions.JAR74));
//        teamList.add(db4oTeam(Db4oVersions.JAR64));
//
        
        Team[] teams = new Team[teamList.size()];
        
        teamList.toArray(teams);
        return teams;
    }

	public Circuit[] circuits() {
		return new Circuit[] { 
				new ReflectiveCircuitBase(Complex.class),
				new ReflectiveCircuitBase(NestedLists.class),
				new ReflectiveCircuitBase(InheritanceHierarchy.class),
				new ReflectiveCircuitBase(FlatObject.class),
				new Trees(), 
				new NativeIds(),
				new Commits(),
				new ArrayLists(),
				new Strings(),
		};
	}

	public DriverBase[] drivers() {
		return new DriverBase [] {
				new ComplexDb4o(),
				new NestedListsDb4o(),
				new InheritanceHierarchyDb4o(),
				new FlatObjectDb4o(),
		        new TreesDb4o(),
		        new NativeIdsDb4o(),
		        new CommitsDb4o(),
		        new StringsDb4o(),
		        new ArrayListsDb4o(),
		};
	}
    
}
