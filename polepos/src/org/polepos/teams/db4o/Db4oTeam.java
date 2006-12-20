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

import java.io.File;
import java.util.*;

import org.polepos.framework.*;

import com.db4o.*;
import com.db4o.io.*;

public class Db4oTeam extends Team{
    
    private String _name = db4oName(); 
    
    private boolean _clientServer = false;

    private boolean _clientServerOverTcp = false;
    
    private final List<Driver> _drivers;
    
    public static ObjectServer server;
    
    public static final int SERVER_PORT = 4488;
    
    public static final String SERVER_HOST = "localhost";
    
    public static final String SERVER_USER = "db4o";
    
    public static final String SERVER_PASSWORD = "db4o";
    
    public static final String FOLDER = "data/db4o";

    public static final String DB4O_FILE = "dbbench.yap"; 
    
    public Db4oTeam() {
        _drivers = new ArrayList<Driver>();
        addDrivers();
    }
    
    private void addDrivers(){
        addDriver(new MelbourneDb4o());
        addDriver(new SepangDb4o());
        addDriver(new BahrainDb4o());
        addDriver(new ImolaDb4o());
        addDriver(new BarcelonaDb4o());
        addDriver(new MonacoDb4o());
        addDriver(new NurburgringDb4o());
        addDriver(new MontrealDb4o());
    }
    
    @Override
    public String name(){
		return _name;
	}
    
    @Override
    public String description() {
        return "the open source object database for Java and .NET";
    }

    @Override
    public Car[] cars(){
		return new Car[]{ new Db4oCar(_clientServer, _clientServerOverTcp) };
	}
    
    public void addDriver(Driver driver){
        _drivers.add(driver);
    }
    
    public void addDriver(String driverName){
        try {
            Class clazz = this.getClass().getClassLoader().loadClass(driverName);
            addDriver((Driver)clazz.newInstance());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Driver[] drivers() {
        return _drivers.toArray(new Driver[0]);
    }

    @Override
    public String website() {
        return "http://www.db4o.com";
    }

    @Override
    public void configure(int[] options) {
        _name = db4oName();
        if(options != null){
            for (int i = 0; i < options.length; i++) {
                try{
                    switch (options[i]){
                        case Db4oOptions.NO_FLUSH:
                            Db4o.configure().flushFileBuffers(false);
                            _name += " noflush";
                            break;
                        case Db4oOptions.CLIENT_SERVER:
                            _clientServer = true;
                            _name += " C/S";
                            break;
                        case Db4oOptions.CLIENT_SERVER_TCP:
                            _clientServerOverTcp = true;
                            _name += " TCP";
                            break;
                        case Db4oOptions.MEMORY_IO:
                            _name += " MemIO";
                            Db4o.configure().io(new MemoryIoAdapter());
                            break;
                        case Db4oOptions.CACHED_BTREE_ROOT:
                            Db4o.configure().bTreeCacheHeight(1);
                            break;
                        case Db4oOptions.CONCURRENT_COUNT:
						    _name += " threads = " + Db4oOptions.CONCURRENT_COUNT;
						    setConcurrentCount(Db4oOptions.CONCURRENT_COUNT);
						    break;
                        default:
                    
                    }
                }catch (Throwable t){
                    System.err.println("db4o option not available in this version");
                    t.printStackTrace();
                }
            }
        }
    }
    
    private String db4oName(){
        return "db4o";
    }

	protected void setUp() {
		new File(FOLDER).mkdirs();
	    deleteDatabaseFile();
	    
		if(_clientServer){
            Db4o.configure().messageLevel(-1);
            server = Db4o.openServer(path(), SERVER_PORT);
            server.grantAccess(SERVER_USER, SERVER_PASSWORD);
        }
	}

	protected void tearDown() {
		if(_clientServer && server != null) {
			server.close();
		}
	}
    
	public final static String path(){
        return FOLDER + "/" + DB4O_FILE;
    }
	
    /**
     * get rid of the database file.
     */
    private void deleteDatabaseFile()
    {
        new File( path() ).delete();
    }    
}
