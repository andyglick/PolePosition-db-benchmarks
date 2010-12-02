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

import java.util.*;

import org.hibernate.*;
import org.polepos.circuits.complex.*;
import org.polepos.teams.hibernate.data.*;


public class ComplexHibernate extends HibernateDriver implements Complex {
	
	@Override
	public void write() {
		Transaction tx = begin();
		ComplexHolder0 holder = ComplexHolder0.generate(depth(), objectCount());
		addToCheckSum(holder);
		store(new ComplexRoot(holder));
		tx.commit();
	}

	@Override
	public void read() {
		ComplexHolder0 holder = root();
		addToCheckSum(holder);
	}

	private ComplexHolder0 root() {
		String from = "from org.polepos.teams.hibernate.data.ComplexRoot";
		Iterator it = db().iterate(from);
		if(! it.hasNext()){
			throw new IllegalStateException("no ComplexRoot found");
		}
		ComplexRoot root = (ComplexRoot) it.next();
		if(it.hasNext()){
			throw new IllegalStateException("More than one ComplexRoot found");
		}
		ComplexHolder0 holder = root.getHolder();
		return holder;
	}

	@Override
	public void query() {
//		int selectCount = selectCount();
//		int firstInt = objectCount() * objectCount() + objectCount();
//		int lastInt = firstInt + (objectCount() * objectCount() * objectCount()) - 1;
//		int currentInt = firstInt;
//		for (int run = 0; run < selectCount; run++) {
//			
//			Query query = null;
//			query.constrain(ComplexHolder2.class);
//			query.descend("_i2").constrain(currentInt);
//			ObjectSet<ComplexHolder2> result = query.execute();
//			if(result.size() != 1) {
//				throw new IllegalStateException("" + result.size());
//			}
//			ComplexHolder2 holder = result.get(0);
//			
//			addToCheckSum(holder.ownCheckSum());
//			
//			currentInt++;
//			if(currentInt > lastInt){
//				currentInt = firstInt;
//			}
//		}
//		
	}
	
	@Override
	public void update() {
//		ComplexHolder0 holder = root();
//		holder.traverse(new NullVisitor(),
//				new Visitor<ComplexHolder0>() {
//			@Override
//			public void visit(ComplexHolder0 holder) {
//				addToCheckSum(holder.ownCheckSum());
//				holder.setName("updated");
//				ComplexHolder2 newChild = new ComplexHolder2();
//				newChild._i1 = 1;
//				newChild._i2 = 2;
//				newChild.setName("added");
//				holder.addChild(newChild);
//				store(holder.getChildren());
//				store(holder);
//			}
//		});
	}

	@Override
	public void delete() {
//		ComplexHolder0 holder = root();
//		
//		holder.traverse(
//			new NullVisitor(),
//			new Visitor<ComplexHolder0>() {
//			@Override
//			public void visit(ComplexHolder0 holder) {
//				addToCheckSum(holder.ownCheckSum());
//				db().delete(holder);
//			}
//		});
	}

}
