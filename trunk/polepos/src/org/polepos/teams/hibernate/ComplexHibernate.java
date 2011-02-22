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

import java.io.*;
import java.util.*;

import org.hibernate.*;
import org.polepos.circuits.complex.*;
import org.polepos.framework.*;
import org.polepos.teams.hibernate.data.*;


public class ComplexHibernate extends HibernateDriver implements Complex {
	
	
	private Serializable _rootId;

	@Override
	public Object write() {
		return write(false);
	}
		
	public Object write(boolean disjunctSpecial) {
		Transaction tx = begin();
		ComplexHolder0 holder = ComplexHolder0.generate(depth(), objects(), disjunctSpecial);
		addToCheckSum(holder);
		_rootId = store(holder);
		tx.commit();
		return _rootId;
	}

	@Override
	public void read() {
		ComplexHolder0 holder = read(_rootId);
		addToCheckSum(holder);
	}
	
	public ComplexHolder0 read(Serializable _id) {
		return (ComplexHolder0)db().load(ComplexHolder0.class,_id);
	}

	@Override
	public void query() {
		int selectCount = selects();
		int firstInt = objects() * objects() + objects();
		int lastInt = firstInt + (objects() * objects() * objects()) - 1;
		int currentInt = firstInt;
		for (int run = 0; run < selectCount; run++) {
			String query = "from org.polepos.teams.hibernate.data.ComplexHolder2 where i2 = ?";
			Iterator it = db().createQuery(query).setParameter(0, currentInt).iterate();
			if(! it.hasNext()){
				throw new IllegalStateException("no ComplexHolder2 found");
			}
			ComplexHolder2 holder = (ComplexHolder2) it.next();
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
	
	public void update(Serializable id) {
		Transaction tx = begin();
		ComplexHolder0 holder = read(id);
		holder.traverse(new NullVisitor<ComplexHolder0>(),
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
		tx.commit();
	}

	@Override
	public void delete() {
		deleteById(_rootId);
	}
	
	public void deleteById(Serializable id) {
		ComplexHolder0 holder = read(id);
		holder.traverse(
			new NullVisitor<ComplexHolder0>(),
			new Visitor<ComplexHolder0>() {
			@Override
			public void visit(ComplexHolder0 holder) {
				addToCheckSum(holder.ownCheckSum());
				db().delete(holder);
			}
		});
	}
}
