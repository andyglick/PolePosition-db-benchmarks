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

import java.util.*;

import javax.persistence.Query;

import org.polepos.circuits.nestedlists.*;
import org.polepos.framework.*;
import org.polepos.teams.jpa.data.ListHolder;
import org.polepos.teams.jpa.data.InheritanceHierarchy4;

/**
 * @author Christian Ernst
 */
public class NestedListsJpa extends JpaDriver implements NestedLists {

	@Override
	public void create() throws Throwable {
		begin();
		store(ListHolder.generate(depth(), objects(), reuse()));
		commit();
	}
	
	@Override
	public void read() throws Throwable {
		begin();
		ListHolder root = root();
		root.accept(new Visitor<ListHolder>(){
			public void visit(ListHolder listHolder){
				addToCheckSum(listHolder);
			}
		});
		commit();
	}
	
	private ListHolder root() {
        String filter = " this._name = '" + ListHolder.ROOT_NAME + "'";

        Query query = db().createQuery(
				"SELECT this FROM " + ListHolder.class.getSimpleName()
						+ " this WHERE " + filter);
        Collection<ListHolder> result = (Collection<ListHolder>)query.getResultList();
        if(result.size() != 1){
        	throw new IllegalStateException();
        }
        Iterator<ListHolder> it = result.iterator();
        return it.next();
	}
	
	@Override
	public void update() throws Throwable {
		begin();
		ListHolder root = root();
		addToCheckSum(root.update(depth(), new Procedure<ListHolder>() {
			@Override
			public void apply(ListHolder obj) {
				store(obj);
			}
		}));
		commit();
	}

	@Override
	public void delete() throws Throwable {
		begin();
		ListHolder root = root();
		addToCheckSum(root.delete(depth(), new Procedure<ListHolder>() {
			@Override
			public void apply(ListHolder listHolder) {
				delete(listHolder);
			}
		}));
		commit();
	}

}
