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

import org.polepos.circuits.inheritancehierarchy.*;
import org.polepos.data.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.query.*;


public class InheritanceHierarchyDb4o extends Db4oDriver implements InheritanceHierarchy {
	

	@Override
	public void configure(Configuration config) {
		indexField(config, InheritanceHierarchy2.class, "i2");
	}

	@Override
	public void write(){
        int count = setup().getObjectCount();
        begin();
        for (int i = 1; i<= count; i++) {
            InheritanceHierarchy4 inheritancheHierarchy4 = new InheritanceHierarchy4();
            inheritancheHierarchy4.setAll(i);
            store(inheritancheHierarchy4);
        }
        commit();
    }
    
	@Override
	public void read(){
        doQuery(InheritanceHierarchy4.class);
    }
    
	@Override
	public void query(){
        int count = setup().getSelectCount();
        for (int i = 1; i <= count; i++) {
            Query q = db().query();
            q.constrain(InheritanceHierarchy4.class);
            q.descend("i2").constrain(i);
            doQuery(q);
        }
    }
    
	@Override
	public void delete(){
        begin();
        Query q = db().query();
        q.constrain(InheritanceHierarchy4.class);
        ObjectSet<InheritanceHierarchy4> deleteSet = q.execute();
        while(deleteSet.hasNext()){
            db().delete(deleteSet.next());
            addToCheckSum(5);
        }
        commit();
    }

}
