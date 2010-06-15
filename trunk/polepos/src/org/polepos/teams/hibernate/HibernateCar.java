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

package org.polepos.teams.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.classic.Session;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.polepos.framework.*;
import org.polepos.teams.hibernate.data.HB0;
import org.polepos.teams.hibernate.data.HN1;
import org.polepos.teams.hibernate.data.HibernateIndexedPilot;
import org.polepos.teams.hibernate.data.HibernateLightObject;
import org.polepos.teams.hibernate.data.HibernateListHolder;
import org.polepos.teams.hibernate.data.HibernatePilot;
import org.polepos.teams.hibernate.data.HibernateTree;
import org.polepos.teams.jdbc.Jdbc;

/**
 *
 * @author Herkules
 */
public class HibernateCar extends Car
{
    
    private SessionFactory mFactory;
    
    private final String mDBType;
    
    
    public HibernateCar(Team team, String dbType){
    	super(team);
        mDBType = dbType;
    }

    public String name(){
        return Jdbc.settings().getName(mDBType)+"-"+Jdbc.settings().getVersion(mDBType);
    }
    
    public Session openSession() throws CarMotorFailureException{
    	
        if ( mFactory == null) {
            mFactory = getSessionFactory();
        }
        
        try {
            Session session = mFactory.openSession();
            if("hsqldb".equals(mDBType)){
	            session.createSQLQuery("SET WRITE_DELAY 0").executeUpdate();
			}
			return session;
        } catch (HibernateException e) {
            e.printStackTrace();
            throw new CarMotorFailureException();
        }
    }
    
    public void closeSession(Session session){
        try {
            session.close();
        } catch (HibernateException e) {
            e.printStackTrace();
        }
    }
    
    
    /**
     *
     */
    private SessionFactory getSessionFactory()
    {
        try
        {
            Configuration cfg = new Configuration()
                    .addClass( HibernatePilot.class )
                    .addClass( HibernateTree.class )
                    .addClass( HibernateIndexedPilot.class )
                    .addClass(HB0.class)
            		.addClass( HibernateLightObject.class)
            		.addClass( HibernateListHolder.class)
            		.addClass( HN1.class);
            
            try{
                Class.forName( Jdbc.settings().getDriverClass( mDBType ) ).newInstance();
            } catch ( Exception ex ) {
                ex.printStackTrace();
            }
            
            String connectUrl = Jdbc.settings().getConnectUrl( mDBType );
			cfg.setProperty("hibernate.connection.url", connectUrl);
            
            String user = Jdbc.settings().getUsername( mDBType );
            if(user != null){
                cfg.setProperty("hibernate.connection.user", user);
            }
            
            String password = Jdbc.settings().getPassword( mDBType );
            if(password != null){
                cfg.setProperty("hibernate.connection.password", password);
            }
            
            String dialect = Jdbc.settings().getHibernateDialect( mDBType );
            if(dialect != null){
                cfg.setProperty("hibernate.dialect", dialect);    
            }
            
            String jdbcDriverClass = Jdbc.settings().getDriverClass( mDBType );
            if(jdbcDriverClass != null){
                cfg.setProperty("hibernate.connection.driver_class", jdbcDriverClass);    
            }
            
            cfg.setProperty("hibernate.query.substitutions", "true 1, false 0, yes 'Y', no 'N'");
            cfg.setProperty("hibernate.connection.pool_size", "20");
            cfg.setProperty("hibernate.proxool.pool_alias", "pool1");
            cfg.setProperty("hibernate.jdbc.batch_size", "20");
            cfg.setProperty("hibernate.jdbc.fetch_size", "500");
            cfg.setProperty("hibernate.use_outer_join", "true");
            cfg.setProperty("hibernate.jdbc.batch_versioned_data", "true");
            cfg.setProperty("hibernate.jdbc.use_streams_for_binary", "true");
            cfg.setProperty("hibernate.max_fetch_depth", "1");
            cfg.setProperty("hibernate.cache.region_prefix", "hibernate.test");
            cfg.setProperty("hibernate.cache.use_query_cache", "true");
            cfg.setProperty("hibernate.cache.provider_class", "org.hibernate.cache.EhCacheProvider");
            
            cfg.setProperty("hibernate.proxool.pool_alias", "pool1");
            
            cfg.setProperty("hibernate.connection.writedelay", "0");
 
            
            
            SessionFactory factory = cfg.buildSessionFactory();     
            new SchemaExport(cfg).create(true, true);
            return factory;         
        }
        catch ( MappingException mex )
        {
            mex.printStackTrace();
        }
        catch ( HibernateException hex )
        {
            hex.printStackTrace();
        }       
        return null;
    }

}
