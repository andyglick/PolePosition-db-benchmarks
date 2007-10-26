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
import org.polepos.runner.*;
import org.polepos.teams.db4o.*;
import org.polepos.teams.hibernate.*;
import org.polepos.teams.jdbc.*;
import org.polepos.teams.jdo.*;

/**
 * @author Herkules, Andrew Zhang
 * 
 * This is the Main class to run PolePosition. If JDO is to be tested also,
 * JdoEnhance has to be run first.
 */
public class RunSeason extends AbstractRunner {

	public static void main(String[] args) {
		new RunSeason().run();
	}

	@Override
	public Circuit[] circuits() {
		return new Circuit[] { 
				new Melbourne(), 
				new Sepang(), 
				new Bahrain(),
				new Imola(),
				new Barcelona(), 
		};
	}

	@Override
	public Team[] teams() {
		return new Team[] { 
				new Db4oTeam(), 
				new HibernateTeam(),
				new JdbcTeam(),
				new JdoTeam() 
		};
	}

}
