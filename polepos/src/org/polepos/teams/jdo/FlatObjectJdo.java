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

import java.util.*;

import javax.jdo.*;

import org.polepos.circuits.flatobject.*;
import org.polepos.data.*;
import org.polepos.teams.jdo.data.*;

public class FlatObjectJdo extends JdoDriver implements FlatObject {
    
	public void write(){
		begin();
        initializeTestId(objects());
		while ( hasMoreTestIds()){
			JdoIndexedObject indexedObject = new JdoIndexedObject(nextTestId());
			store(indexedObject);
			if(doCommit()){
				commit();
				begin();
			}
            addToCheckSum(indexedObject);
		}
		commit();
	}
 
    public void queryIndexedString() {
        begin();
        initializeTestId(setup().getSelectCount());
        String filter = "this._string == param";
        while(hasMoreTestIds()) {
            Query query = db().newQuery(JdoIndexedObject.class, filter);
            query.declareParameters("String param");
            doQuery(query, IndexedObject.queryString(nextTestId()));
        }
        commit();
    }
            
    public void queryIndexedInt() {
	begin();
        initializeTestId(setup().getSelectCount());
        String filter = "this._int == param";
        while(hasMoreTestIds()) {
            Query query = db().newQuery(JdoIndexedObject.class, filter);
            query.declareParameters("Integer param");
            doQuery(query, nextTestId());
        }
        commit();
    }
	
    public void update() {
    	begin();
    	String filter = "this._int == param";
        initializeTestId(setup().getUpdateCount());
        while(hasMoreTestIds()) {
            Query query = db().newQuery(JdoIndexedObject.class, filter);
            query.declareParameters("Integer param");
            Collection result = (Collection)query.execute(nextTestId());
            JdoIndexedObject indexedObject = (JdoIndexedObject) result.iterator().next();
        	indexedObject.updateString();
            addToCheckSum(indexedObject);
        }
        commit();
	}
    
    public void delete() {
    	begin();
    	String filter = "this._int == param";
        initializeTestId(setup().getUpdateCount());
        while(hasMoreTestIds()) {
            Query query = db().newQuery(JdoIndexedObject.class, filter);
            query.declareParameters("Integer param");
            Collection result = (Collection)query.execute(nextTestId());
            JdoIndexedObject indexedObject = (JdoIndexedObject) result.iterator().next();
            addToCheckSum(indexedObject);
        	indexedObject.updateString();
        	delete(indexedObject);
        }
        commit();
    }

}
