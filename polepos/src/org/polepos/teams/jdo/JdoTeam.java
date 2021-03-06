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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;

import org.polepos.framework.Car;
import org.polepos.framework.DriverBase;
import org.polepos.framework.Team;
import org.polepos.teams.jdo.data.ComplexHolder0;
import org.polepos.teams.jdo.data.ComplexHolder1;
import org.polepos.teams.jdo.data.ComplexHolder2;
import org.polepos.teams.jdo.data.ComplexHolder3;
import org.polepos.teams.jdo.data.ComplexHolder4;
import org.polepos.teams.jdo.data.InheritanceHierarchy0;
import org.polepos.teams.jdo.data.InheritanceHierarchy1;
import org.polepos.teams.jdo.data.InheritanceHierarchy2;
import org.polepos.teams.jdo.data.InheritanceHierarchy3;
import org.polepos.teams.jdo.data.InheritanceHierarchy4;
import org.polepos.teams.jdo.data.JB0;
import org.polepos.teams.jdo.data.JB1;
import org.polepos.teams.jdo.data.JB2;
import org.polepos.teams.jdo.data.JB3;
import org.polepos.teams.jdo.data.JB4;
import org.polepos.teams.jdo.data.JN1;
import org.polepos.teams.jdo.data.JdoIndexedObject;
import org.polepos.teams.jdo.data.JdoIndexedPilot;
import org.polepos.teams.jdo.data.JdoLightObject;
import org.polepos.teams.jdo.data.JdoListHolder;
import org.polepos.teams.jdo.data.JdoPilot;
import org.polepos.teams.jdo.data.JdoTree;
import org.polepos.teams.jdo.data.ListHolder;


public class JdoTeam extends Team{
    
	protected Car[] mCars;
	
	protected JdoTeam(boolean initialize){
		if(! initialize){
			return;
		}
        String[] impls = Jdo.settings().getJdoImplementations();
        
        if(impls == null){
            System.out.println("No JDO engine configured.");
            mCars = new Car[0];
        }else{
        
            List <Car> cars = new ArrayList<Car>();
            
            for (String impl : impls) {
                
                String[] jdosqldbs = Jdo.settings().getJdbc(impl);
                
                if(jdosqldbs != null && jdosqldbs.length > 0){
                    for(String sqldb : jdosqldbs){
                        try {
                            cars.add(new JdoCar(this, impl, sqldb, Jdo.settings().color(impl)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }else{
                    try {
                        cars.add(new JdoCar(this, impl, null, Jdo.settings().color(impl)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } 
                }
            }
            
            mCars = new Car[ cars.size() ];
            cars.toArray(mCars);
        }
		
	}
    
    public JdoTeam() {
    	this(true);
    }
    
	
    @Override
	public String name(){
		return "JDO";
	}

    @Override
    public String description() {
        return "the JDO team";
    }

    @Override
	public Car[] cars(){
		return mCars;
	}
    
    public String databaseFile() {
    	// not supported yet
    	return null;
    }

    @Override
    public DriverBase[] drivers() {
        return new DriverBase[]{
        	new FlatObjectJdo(),
        	new NestedListsJdo(),
        	new InheritanceHierarchyJdo(),
        	new ComplexJdo(),
            new TreesJdo(),
            new NativeIdsJdo(),
            new CommitsJdo(),
            new ArrayListsJdo(),
            new StringsJdo(),
        	new ComplexConcurrencyJdo(),
        };
    }
    
    @Override
    public String website() {
        return null;
    }


	@Override
    public void setUp() {
		for(int i = 0; i < mCars.length;i++){		
		    JdoCar jdoCar = (JdoCar)mCars[i];
			PersistenceManager pm = jdoCar.getPersistenceManager();
		    deleteAll(pm);
		    pm.close();
		}
	}
	
	@Override
	protected void tearDown() {
		for(int i = 0; i < mCars.length;i++){		
		    JdoCar jdoCar = (JdoCar)mCars[i];
		    jdoCar.tearDown();
		}
	}


	public void deleteAll(PersistenceManager pm) {

		deleteAll(pm, ComplexHolder4.class);
		deleteAll(pm, ComplexHolder3.class);
		deleteAll(pm, ComplexHolder2.class);
		deleteAll(pm, ComplexHolder1.class);
		deleteAll(pm, ComplexHolder0.class);
		
		deleteAll(pm, InheritanceHierarchy4.class);
		deleteAll(pm, InheritanceHierarchy3.class);
		deleteAll(pm, InheritanceHierarchy2.class);
		deleteAll(pm, InheritanceHierarchy1.class);
		deleteAll(pm, InheritanceHierarchy0.class);
		
		deleteAll(pm, JdoIndexedObject.class);
		deleteAll(pm, ListHolder.class);

		// old courses
		deleteAll(pm, JB0.class);
		deleteAll(pm, JB1.class);
		deleteAll(pm, JB2.class);
		deleteAll(pm, JB3.class);
		deleteAll(pm, JB4.class);
		deleteAll(pm, JdoIndexedPilot.class);
		deleteAll(pm, JdoPilot.class);
		deleteAll(pm, JdoTree.class);
		deleteAll(pm, JdoLightObject.class);
		deleteAll(pm, JdoListHolder.class);
		deleteAll(pm, JN1.class);
		
	}


	private void deleteAll(PersistenceManager pm, Class clazz) {
		//checkExtentSize(pm, clazz,"");
		
		// 1. try Query.deletePersistentAll()
		// deletePersistentAll(pm,clazz);
		
		//2. try PersistenceManager.delete(Extent.iterator().next()) with batches 
		deleteAllBatched(pm, clazz);
		
		//checkExtentSize(pm, clazz,"");
	}


	private void deletePersistentAll(PersistenceManager pm, Class clazz) {
		pm.currentTransaction().begin();
		pm.newQuery(pm.getExtent(clazz,false)).deletePersistentAll();
		pm.currentTransaction().commit();
	}
	
	private void checkExtentSize(PersistenceManager pm, Class clazz, String msg){
		pm.currentTransaction().begin();
		Collection collection = (Collection) pm.newQuery(pm.getExtent(clazz,false)).execute();
		System.out.println(msg + " " + clazz.getSimpleName() + " size: " + collection.size());
		pm.currentTransaction().rollback();
	}

	private void deleteAllBatched(PersistenceManager pm, Class clazz) {
	    pm.currentTransaction().begin();
	    int batchSize = 10000;
            int commitctr = 0;
            Extent extent = pm.getExtent(clazz,false);
            Iterator it = extent.iterator();
            while(it.hasNext()){
                pm.deletePersistent(it.next());
                if ( batchSize > 0  &&  ++commitctr >= batchSize){
                    commitctr = 0;
                    pm.currentTransaction().commit();
                    pm.currentTransaction().begin();
                }
            }
            extent.closeAll();
            pm.currentTransaction().commit();
	}

}
