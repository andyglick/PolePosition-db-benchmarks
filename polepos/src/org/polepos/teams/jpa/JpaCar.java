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

package org.polepos.teams.jpa;

import java.io.IOException;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

import org.polepos.framework.*;
import org.polepos.teams.jdbc.Jdbc;

/**
 * @author Christian Ernst
 */
public class JpaCar extends Car {

    protected transient EntityManagerFactory _emf;

    private final String              _dbName;
    private final String              _name;

    public JpaCar(Team team, String name, String dbName, String color) throws IOException{
    	super(team, color);

        _name = name;
        _dbName = dbName;

        _website = Jpa.settings().getWebsite(name);
        _description = Jpa.settings().getDescription(name);

        initialize();

    }

    private boolean isSQL() {
        return _dbName != null;
    }
    
    private void initialize() throws IOException {
	    Properties props = new Properties();
	    try {
			_emf = Persistence.createEntityManagerFactory(persistenceUnitName(),props);
		} catch (PersistenceException e) {
			e.printStackTrace();
		} 
    }
    
    protected void reinitialize() throws IOException{
    	if(_emf != null){
    		_emf.close();
    	}
    	initialize();
    }

	private String persistenceUnitName() {
		if(_dbName == null){
			return _name;
		}
		return _name+"/"+_dbName;
	}

    /**
     *
     */
    public EntityManager getEntityManager() {
        return _emf.createEntityManager();
    }

    @Override
    public String name() {
        if(isSQL()){
            return Jpa.settings().getName(_name) + "/" +Jdbc.settings().getName(_dbName)+"-"+Jdbc.settings().getVersion(_dbName);
        }
        return Jpa.settings().getVendor(_name) + "/" + Jpa.settings().getName(_name)+"-"+Jpa.settings().getVersion(_name);
    }
    
    public boolean canRecreateDatabase(){
    	return false;
    }
    
    public void recreateDatabase(){
    	// do nothing, override in specific implementations
    }
    
    


}
