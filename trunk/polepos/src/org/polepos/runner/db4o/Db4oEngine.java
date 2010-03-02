/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package org.polepos.runner.db4o;

import java.io.*;

import org.polepos.teams.db4o.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.io.*;

public class Db4oEngine {

	private ObjectServer _server;
    
    public static final int SERVER_PORT = 4488;
    
    public static final String SERVER_HOST = "localhost";
    
    public static final String SERVER_USER = "db4o";
    
    public static final String SERVER_PASSWORD = "db4o";
    
    public static final String FOLDER;
    
    static {
        FOLDER = Db4oTeam.class.getResource("/").getPath() + "data/db4o";
    }

    public static final String DB4O_FILE = "dbbench.yap";

	public static final String PATH = FOLDER + "/" + DB4O_FILE; 
    
	private final Storage _storage = 
//		new MemoryStorage();
		new CachingStorage(new FileStorage());

	private void startServer(Configuration config) {
// ???	    try {
//			deleteDatabaseFile();
//		} 
//	    catch (IOException e) {
//	    	throw new RuntimeException(e);
//		}
        if(_server == null){
	        _server = Db4o.openServer(config, PATH, SERVER_PORT);
	        _server.grantAccess(SERVER_USER, SERVER_PASSWORD);
        }
	}

	public void stopServer() {
    	if(_server != null){
    		_server.close();
    		_server = null;
    	}
	}
	
    /**
     * get rid of the database file.
     * @throws IOException 
     */
    public void deleteDatabaseFile() throws IOException {
    	stopServer();
        _storage.delete(PATH);
    }    

	public final String databaseFile(){
        return PATH;
    }

	public ExtObjectContainer openFile(Configuration config) {
		return Db4o.openFile(config(config), PATH).ext();
	}

	public ExtObjectContainer openNetworkingClient(Configuration config) {
		Configuration enhConfig = config(config);
		startServer(enhConfig);
		return Db4o.openClient(enhConfig, SERVER_HOST, SERVER_PORT, SERVER_USER, SERVER_PASSWORD).ext();
	}

	public ExtObjectContainer openEmbeddedClient(Configuration config) {
		Configuration enhConfig = config(config);
		startServer(enhConfig);
		return _server.openClient().ext();
	}

	private Configuration config(Configuration config) {
		config.storage(_storage);
		return config;
	}
}
