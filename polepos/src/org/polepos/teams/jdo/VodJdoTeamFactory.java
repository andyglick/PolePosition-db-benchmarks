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


package org.polepos.teams.jdo;

import org.polepos.framework.*;
import org.polepos.teams.*;

/**
 * VOD JDO and VOD JPA are not compatible to run in the same ClassLoader
 * space. That's why we use a special classloader for VOD JDO that knows
 * it's vodjdo.jar. 
 *
 */
public class VodJdoTeamFactory extends TeamFactory{
	
	public static Team newVodJdoTeam(){
		return newTeam("vodjdo.jar", "lib/vod", "org.polepos.teams.jdo.VodJdoTeam", "com.versant", "org.polepos.teams.jdo");
	}

	
}