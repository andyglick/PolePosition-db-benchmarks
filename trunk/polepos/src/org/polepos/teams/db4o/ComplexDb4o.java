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

import java.util.*;

import org.polepos.circuits.complex.*;
import org.polepos.data.*;
import org.polepos.framework.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.query.*;

public class ComplexDb4o extends Db4oDriver implements Complex {
	
	private int _rootId;
	
	
	@Override
	public Object write() {
		return write(false);
	}
	
	public Object write(boolean disjunctSpecial) {
		ComplexHolder0 holder = ComplexHolder0.generate(depth(), objects(), disjunctSpecial);
		store(holder);
		addToCheckSum(holder);
		commit();
		_rootId = (int) db().getID(holder);
		return _rootId;
	}

	@Override
	public void read() {
		ComplexHolder0 holder = read(_rootId);
		addToCheckSum(holder);
	}
	

	public ComplexHolder0 read(int id) {
		ComplexHolder0 holder = db().getByID(id);
		db().activate(holder, Integer.MAX_VALUE);
		return holder;
	}

	@Override
	public void query() {
		int selectCount = selects();
		int firstInt = objects() * objects() + objects();
		int lastInt = firstInt + (objects() * objects() * objects()) - 1;
		int currentInt = firstInt;
		for (int run = 0; run < selectCount; run++) {
			Query query = db().query();
			query.constrain(ComplexHolder2.class);
			query.descend("_i2").constrain(currentInt);
			ObjectSet<ComplexHolder2> result = query.execute();
			ComplexHolder2 holder = result.get(0);
			db().activate(holder, 3);
			addToCheckSum(holder.ownCheckSum());
			List<ComplexHolder0> children = holder.getChildren();
			for (ComplexHolder0 child : children) {
				addToCheckSum(child.ownCheckSum());
			}
			ComplexHolder0[] array = holder.getArray();
			for (ComplexHolder0 arrayElement : array) {
				addToCheckSum(arrayElement.ownCheckSum());
			}
			currentInt++;
			if(currentInt > lastInt){
				currentInt = firstInt;
			}
		}
	}
	
	@Override
	public void update() {
		update(_rootId);
	}
	
	public void update(int id) {
		ComplexHolder0 holder = read(id);
		db().activate(holder, Integer.MAX_VALUE);
		holder.traverse(new NullVisitor(),
				new Visitor<ComplexHolder0>() {
			@Override
			public void visit(ComplexHolder0 holder) {
				addToCheckSum(holder.ownCheckSum());
				holder.setName("updated");
				List<ComplexHolder0> children = holder.getChildren();
				ComplexHolder0[] array = new ComplexHolder0[children.size()];
				for (int i = 0; i < array.length; i++) {
					array[i] = children.get(i);
				}
				holder.setArray(array);
				store(holder);
			}
		});
	}


	@Override
	public void delete() {
		delete(_rootId);
	}
	
	public void delete(int id) {
		ComplexHolder0 holder = read(id);
		db().activate(holder, Integer.MAX_VALUE);
		holder.traverse(
			new NullVisitor(),
			new Visitor<ComplexHolder0>() {
			@Override
			public void visit(ComplexHolder0 holder) {
				addToCheckSum(holder.ownCheckSum());
				db().delete(holder);
			}
		});
	}


	@Override
	public void configure(Configuration config) {
		config.objectClass(ComplexHolder2.class).objectField("_i2").indexed(true);
		config.activationDepth(1);
	}

}
