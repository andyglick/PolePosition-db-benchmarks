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

import org.datanucleus.NucleusContext;
import org.datanucleus.api.jdo.JDOPersistenceManagerFactory;
import org.datanucleus.store.schema.SchemaAwareStoreManager;
import org.polepos.framework.Car;
import org.polepos.framework.Team;
import org.polepos.teams.jdbc.Jdbc;
import org.polepos.teams.jdbc.JdbcCar;
import org.polepos.teams.jdbc.JdbcTeam;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.datastore.JDOConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;


public class JdoCar extends Car {

    private transient PersistenceManagerFactory _persistenceManagerFactory;

    private final String _dbName;
    
    private final String _jdoName;
    
    private final Properties properties = new Properties();
    
    private String _name; 

    JdoCar(Team team, String name, String dbName, String color) {
    	super(team, color);

        _jdoName = name;
        _dbName = dbName;

        _website = Jdo.settings().getWebsite(name);
        _description = Jdo.settings().getDescription(name);

        initialize();

    }

    private boolean isSQL() {
        return _dbName != null;
    }
    
    private void initialize() {
  	
        properties.setProperty("javax.jdo.PersistenceManagerFactoryClass", Jdo.settings()
            .getFactory(_jdoName));
        
        properties.setProperty("javax.jdo.option.Multithreaded", "true");
        properties.setProperty("javax.jdo.option.Optimistic", "true");
        
        // Versant VODJDO specific settings
        properties.setProperty("versant.metadata.0", "org/polepos/teams/jdo/data/vod.jdo");
        properties.setProperty("versant.allowPmfCloseWithPmHavingOpenTx","true");
        properties.setProperty("versant.vdsSchemaEvolve","true");
        properties.setProperty("versant.hyperdrive", "false");
        properties.setProperty("versant.remoteAccess", "false");
        properties.setProperty("versant.l2CacheEnabled", "true");
        properties.setProperty("versant.retainConnectionInOptTx", "true");
        properties.setProperty("versant.readLockOptQueryResults", "false");
        properties.setProperty("versant.l2CacheMaxObjects", "5000000");
        properties.setProperty("versant.l2QueryCacheEnabled", "true");
        properties.setProperty("versant.logDownloader", "none");
        properties.setProperty("versant.logging.logEvents", "none");
        properties.setProperty("versant.metricSnapshotIntervalMs", "1000000000");
        properties.setProperty("versant.metricStoreCapacity", "0");
        properties.setProperty("versant.vdsNamingPolicy", "none");
        properties.setProperty("versant.remoteMaxActive", "30");
        properties.setProperty("versant.maxActive", "30");
        properties.setProperty("javax.jdo.option.NontransactionalRead", "true");

        if (isSQL()) {
            try {
                Class.forName(Jdbc.settings().getDriverClass(_dbName)).newInstance();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            properties.setProperty("javax.jdo.option.ConnectionDriverName", Jdbc.settings()
                .getDriverClass(_dbName));
            String connectUrl = Jdbc.settings().getConnectUrl(_dbName);
            
			properties.setProperty("javax.jdo.option.ConnectionURL", connectUrl);
            
            String user = Jdbc.settings().getUsername(_dbName);
            if (user != null) {
                properties.setProperty("javax.jdo.option.ConnectionUserName", user);
            }

            String password = Jdbc.settings().getPassword(_dbName);
            if (password != null) {
                properties.setProperty("javax.jdo.option.ConnectionPassword", password);
            }
        } else {

            properties.setProperty("javax.jdo.option.ConnectionURL", Jdo.settings().getURL(_jdoName));

            String user = Jdo.settings().getUsername(_jdoName);
            if (user != null) {
                properties.setProperty("javax.jdo.option.ConnectionUserName", user);
            }

            String password = Jdo.settings().getPassword(_jdoName);
            if (password != null) {
                properties.setProperty("javax.jdo.option.ConnectionPassword", password);
            }
        }

        properties.setProperty("datanucleus.autoCreateTables", "true");
        properties.setProperty("datanucleus.autoCreateColumns", "true");
        properties.setProperty("datanucleus.autoCreateConstraints", "false");
        properties.setProperty("datanucleus.validateTables", "false");
        properties.setProperty("datanucleus.validateColumns", "false");
        properties.setProperty("datanucleus.validateConstraints", "false");
        properties.setProperty("datanucleus.metadata.validate", "false");
        properties.setProperty("datanucleus.connectionPool.maxIdle", "15");
        properties.setProperty("datanucleus.connectionPool.minIdle", "5");
        properties.setProperty("datanucleus.connectionPool.maxActive", "30");
        properties.setProperty("datanucleus.connectionPoolingType", "DBCP");
		properties.setProperty("datanucleus.manageRelationships", "false");
		properties.setProperty("datanucleus.valuegeneration.sequence.allocationSize","100");
		properties.setProperty("datanucleus.valuegeneration.increment.allocationSize","100");
		properties.setProperty("datanucleus.connectionPool.maxStatements","20");
		properties.setProperty("datanucleus.autoStartMechanism","None");
		properties.setProperty("datanucleus.cache.level2.type","ehcache");

		_persistenceManagerFactory = JDOHelper.getPersistenceManagerFactory(properties, JDOHelper.class.getClassLoader());
		
		// Datanucleus Schema create
		if(_persistenceManagerFactory instanceof JDOPersistenceManagerFactory){
			NucleusContext ctx = ((JDOPersistenceManagerFactory)_persistenceManagerFactory).getNucleusContext();

			Set classNames = new HashSet();
			classNames.add("org.polepos.teams.jdo.data.ComplexHolder0");
			classNames.add("org.polepos.teams.jdo.data.ComplexHolder1");
			classNames.add("org.polepos.teams.jdo.data.ComplexHolder2");
			classNames.add("org.polepos.teams.jdo.data.ComplexHolder3");
			classNames.add("org.polepos.teams.jdo.data.ComplexHolder4");
			classNames.add("org.polepos.teams.jdo.data.InheritanceHierarchy0");
			classNames.add("org.polepos.teams.jdo.data.InheritanceHierarchy1");		
			classNames.add("org.polepos.teams.jdo.data.InheritanceHierarchy2");	
			classNames.add("org.polepos.teams.jdo.data.InheritanceHierarchy3");	
			classNames.add("org.polepos.teams.jdo.data.InheritanceHierarchy4");	
			classNames.add("org.polepos.teams.jdo.data.JB0");	
			classNames.add("org.polepos.teams.jdo.data.JB1");	
			classNames.add("org.polepos.teams.jdo.data.JB2");	
			classNames.add("org.polepos.teams.jdo.data.JB3");	
			classNames.add("org.polepos.teams.jdo.data.JB4");	
			classNames.add("org.polepos.teams.jdo.data.JdoIndexedObject");	
			classNames.add("org.polepos.teams.jdo.data.JdoIndexedPilot");	
			classNames.add("org.polepos.teams.jdo.data.JdoLightObject");	
			classNames.add("org.polepos.teams.jdo.data.JdoListHolder");	
			classNames.add("org.polepos.teams.jdo.data.JdoPilot");	
			classNames.add("org.polepos.teams.jdo.data.JdoTree");	
			classNames.add("org.polepos.teams.jdo.data.JN1");
			classNames.add("org.polepos.teams.jdo.data.ListHolder");

			try
			{
			    //Properties props = new Properties();
			    // Set any properties for schema generation
			    ((SchemaAwareStoreManager)ctx.getStoreManager()).createSchema(classNames, properties);
			}
			catch(Exception e)
			{
			   e.printStackTrace();
			}
		}

		
        
        PersistenceManager pm = _persistenceManagerFactory.getPersistenceManager();
		((JdoTeam)team()).deleteAll(pm);
        pm.close();
        _persistenceManagerFactory.close();
        
        properties.setProperty("datanucleus.autoCreateSchema", "false");

        _persistenceManagerFactory = JDOHelper.getPersistenceManagerFactory(properties, JDOHelper.class.getClassLoader());
        
    }

    public PersistenceManager getPersistenceManager() {
        PersistenceManager pm = _persistenceManagerFactory.getPersistenceManager();
        if(! "hsqldb".equals(_dbName)){
        	return pm;
        }
        JDOConnection dataStoreConnection = pm.getDataStoreConnection();
        Connection connection = (Connection) dataStoreConnection.getNativeConnection();
        JdbcCar.hsqlDbWriteDelayToZero(connection);
        try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return pm;
    }

    @Override
    public String name() {
    	if(_name != null){
    		return _name;
    	}
       
        if(isSQL()){
        	// Creating a JdbcCar to get the version name from it.
        	JdbcTeam jdbcTeam = new JdbcTeam();
        	JdbcCar jdbcCar = new JdbcCar(jdbcTeam, _dbName, color());
        	_name = Jdo.settings().getName(_jdoName) + "/" + jdbcCar.name(); 
        } else {
        	_name = Jdo.settings().getVendor(_jdoName) + "/" + Jdo.settings().getName(_jdoName)+"-"+Jdo.settings().getVersion(_jdoName);
        }
        
        return _name;
    }

}
