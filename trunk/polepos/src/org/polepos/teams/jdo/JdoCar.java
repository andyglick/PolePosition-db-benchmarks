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

import java.io.*;
import java.util.*;


import javax.jdo.*;

import org.polepos.framework.*;
import org.polepos.teams.jdbc.*;


/**
 * @author Herkules
 */
public class JdoCar extends Car {

    private PersistenceManagerFactory mFactory;

    private final String              mDbName;
    private final String              mName;

    JdoCar(String name, String dbName) throws CarMotorFailureException {

        mName = name;
        mDbName = dbName;

        _website = Jdo.settings().getWebsite(name);
        _description = Jdo.settings().getDescription(name);

        initialize();

    }

    private boolean isSQL() {
        return mDbName != null;
    }
    
    private void initialize() {
        
        Properties properties = new Properties();

        properties.setProperty("javax.jdo.PersistenceManagerFactoryClass", Jdo.settings()
            .getFactory(mName));
        
        properties.setProperty("javax.jdo.option.NontransactionalRead", "true");
        properties.setProperty("javax.jdo.option.Multithreaded", "true");
        
        
        
        properties.setProperty("javax.jdo.option.Optimistic", "true");
        
        // Versant VODJDO specific settings
        properties.setProperty("versant.metadata.0", "org/polepos/teams/jdo/data/package.jdo");

        properties.setProperty("versant.allowPmfCloseWithPmHavingOpenTx","true");
        properties.setProperty("versant.vdsSchemaEvolve","true");
        
        properties.setProperty("versant.hyperdrive", "true");
        properties.setProperty("versant.remoteAccess", "false");
        properties.setProperty("versant.l2CacheEnabled", "true");
        properties.setProperty("versant.l2CacheMaxObjects", "5000000");
        properties.setProperty("versant.l2QueryCacheEnabled", "true");
        properties.setProperty("versant.logDownloader", "none");
        properties.setProperty("versant.logging.logEvents", "none");
        properties.setProperty("versant.metricSnapshotIntervalMs", "1000000000");
        properties.setProperty("versant.metricStoreCapacity", "0");
        properties.setProperty("versant.vdsNamingPolicy", "none");

        if (isSQL()) {
            try {
                Class.forName(Jdbc.settings().getDriverClass(mDbName)).newInstance();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            properties.setProperty("javax.jdo.option.ConnectionDriverName", Jdbc.settings()
                .getDriverClass(mDbName));
            properties.setProperty("javax.jdo.option.ConnectionURL", Jdbc.settings().getConnectUrl(
                mDbName));
            String user = Jdbc.settings().getUsername(mDbName);
            if (user != null) {
                properties.setProperty("javax.jdo.option.ConnectionUserName", user);
            }

            String password = Jdbc.settings().getPassword(mDbName);
            if (password != null) {
                properties.setProperty("javax.jdo.option.ConnectionPassword", password);
            }
        } else {

            properties.setProperty("javax.jdo.option.ConnectionURL", Jdo.settings().getURL(mName));

            String user = Jdo.settings().getUsername(mName);
            if (user != null) {
                properties.setProperty("javax.jdo.option.ConnectionUserName", user);
            }

            String password = Jdo.settings().getPassword(mName);
            if (password != null) {
                properties.setProperty("javax.jdo.option.ConnectionPassword", password);
            }
        }


        properties.setProperty("datanucleus.autoCreateSchema", "true");
        
//        properties.setProperty("datanucleus.validateTables", "false");
//        properties.setProperty("datanucleus.validateConstraints", "false");
//        properties.setProperty("datanucleus.metadata.validate", "false");
        
        properties.setProperty("datanucleus.autoCreateConstraints", "false");
//        properties.setProperty("datanucleus.validateColumns", "false");
        
        properties.setProperty("datanucleus.connectionPoolingType", "DBCP");
        
        mFactory = JDOHelper.getPersistenceManagerFactory(properties, JDOHelper.class
            .getClassLoader());
    }

    public PersistenceManager getPersistenceManager() {
        return mFactory.getPersistenceManager();
    }

    @Override
    public String name() {
       
        if(isSQL()){
            return Jdo.settings().getName(mName) + "/" +Jdbc.settings().getName(mDbName)+"-"+Jdbc.settings().getVersion(mDbName);
        }
        return Jdo.settings().getVendor(mName) + "/" + Jdo.settings().getName(mName)+"-"+Jdo.settings().getVersion(mName);

    }

}
