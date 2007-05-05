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


import org.polepos.circuits.bahrain.*;
import org.polepos.circuits.barcelona.*;
import org.polepos.circuits.imola.*;
import org.polepos.circuits.melbourne.*;
import org.polepos.circuits.monaco.*;
import org.polepos.circuits.montreal.*;
import org.polepos.circuits.nurburgring.*;
import org.polepos.circuits.sepang.*;
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

		return new Team[] {
				db4oTeam(Db4oVersions.JAR57, null),
				db4oTeam(Db4oVersions.JAR55, null),
				db4oTeam(Db4oVersions.JAR52, null),
				db4oTeam(Db4oVersions.JAR57, new int[] {Db4oOptions.CLIENT_SERVER, Db4oOptions.CLIENT_SERVER_TCP }),
				db4oTeam(Db4oVersions.JAR55, new int[] {Db4oOptions.CLIENT_SERVER,Db4oOptions.CLIENT_SERVER_TCP }),
				db4oTeam(Db4oVersions.JAR52, new int[] {Db4oOptions.CLIENT_SERVER, Db4oOptions.CLIENT_SERVER_TCP }),
		};
	}

	public Circuit[] circuits() {
		return new Circuit[] { 
				 new Melbourne(),
				 new Sepang(),
				 new Bahrain(),
				 new Imola(),
				 new Barcelona(),
				 new Monaco(),
				 new Nurburgring(),
				 new Montreal(),
		};
	}

	public Driver[] drivers() {
		return new Driver [] {
				new MelbourneDb4o(),
		        new SepangDb4o(),
		        new BahrainDb4o(),
		        new ImolaDb4o(),
		        new BarcelonaDb4o(),
		        new MonacoDb4o(),
		        new NurburgringDb4o(),
		        new MontrealDb4o(),
		};
	}
    
}