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

import java.util.Iterator;

import org.hibernate.*;
import org.hibernate.classic.Session;
import org.polepos.framework.Car;
import org.polepos.framework.CheckSummable;
import org.polepos.framework.DriverBase;
import org.polepos.framework.TurnSetup;

public abstract class HibernateDriver extends DriverBase{
	
    private Session _session;

	public void configure( Car car, TurnSetup setup ){
        super.configure(car, setup);
	}

	public void prepare() {
        _session = hibernateCar().openSession();
	}
	
	public void closeDatabase(){
        hibernateCar().closeSession(_session);
	}
    
    public HibernateCar hibernateCar(){
        return (HibernateCar)car();
    }
		
	public Session db(){
		return _session;
	}

	protected void doQuery(String query) {
        
		try{
			Iterator it = db().createQuery( query ).list().iterator();
			while(it.hasNext()){
				Object o = it.next();
                if(o instanceof CheckSummable){
                    addToCheckSum(((CheckSummable)o).checkSum());
                }
			}
		}
		catch ( HibernateException hex ){
			hex.printStackTrace();
		}		
	}
	
	protected void doQuery(String query, Object singleParam) {
	    try{
	        Iterator it = db().createQuery( query ).setParameter(0, singleParam).list().iterator();
	        while(it.hasNext()){
	            Object o = it.next();
	            if(o instanceof CheckSummable){
	                addToCheckSum(((CheckSummable)o).checkSum());
	            }
	        }
	    }
	    catch ( HibernateException hex ){
	        hex.printStackTrace();
	    }       
	}
	
	protected void doSingleResultQuery(String query) {
		try{
			Object o = db().createQuery( query ).uniqueResult();
            if(o instanceof CheckSummable){
                addToCheckSum((CheckSummable)o);
            }
		}
		catch ( HibernateException hex ){
			hex.printStackTrace();
		}		
	}
	
	protected void doSingleResultQuery(String query, Object singleParam) {
	    try{
	        Object o = db().createQuery( query ).setParameter(0, singleParam).uniqueResult();
	        if(o instanceof CheckSummable){
	            addToCheckSum((CheckSummable)o);
	        }
	    }
	    catch ( HibernateException hex ){
	        hex.printStackTrace();
	    }       
	}
	
	protected <T> T queryForSingle(String query) {
		Iterator<T> it = db().createQuery( query ).list().iterator();
		if(! it.hasNext()){
			throw new RuntimeException("none found");
		}
		T res = it.next();
		if(it.hasNext()){
			throw new RuntimeException("more than one found");
		}
		return res;
	}
	
	protected <T> T queryForSingle(String query, Object param) {
		Iterator<T> it = db().createQuery( query).setParameter(0, param) .list().iterator();
		if(! it.hasNext()){
			throw new RuntimeException("none found");
		}
		T res = it.next();
		if(it.hasNext()){
			throw new RuntimeException("more than one found");
		}
		return res;
	}

	
	protected Transaction begin() {
		return db().beginTransaction();
	}
	
    protected void store(Object obj) {
    	db().save(obj);
	}
    
    protected void delete(Object obj) {
    	db().delete(obj);
	}

}
