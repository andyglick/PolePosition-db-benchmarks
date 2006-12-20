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

package org.polepos.teams.db4o;

import java.io.*;

import org.polepos.framework.*;

import com.db4o.*;
import com.db4o.ext.*;


public class Db4oCar extends Car {
    
	private String name;
    
    private boolean _clientServer;
    
    private boolean _clientServerOverTcp;

	Db4oCar(boolean clientServer, boolean clientServerOverTcp) {
        _clientServer = clientServer;
        _clientServerOverTcp = clientServerOverTcp;
        name = Db4o.version().substring(5);
	}

	@Override
	public String name() {
		return name;
	}
    
    /**
     * Open database in the configured mode.
     */
    public ExtObjectContainer createObjectContainer()
    {
        if(! _clientServer){
            return Db4o.openFile( Db4oTeam.path() ).ext();
        }   

        if(_clientServerOverTcp){
            try {
                return Db4o.openClient(Db4oTeam.SERVER_HOST, Db4oTeam.SERVER_PORT, Db4oTeam.SERVER_USER, Db4oTeam.SERVER_PASSWORD).ext();
            } catch (IOException e) {
                
                // Can happen if port not available
                // Check SERVER_ settings
                
                e.printStackTrace();
            }
            return null;
        }
        // embedded client server mode
		return Db4oTeam.server.openClient().ext();
	}
}
