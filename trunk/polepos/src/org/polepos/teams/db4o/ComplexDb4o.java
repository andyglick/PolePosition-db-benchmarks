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

import org.polepos.circuits.complex.*;
import org.polepos.data.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.query.*;

public class ComplexDb4o extends Db4oDriver implements Complex {
	
	@Override
	public void write() {
		ComplexHolder0 root = ComplexHolder0.generate(depth(), objectCount());
		store(new ComplexRoot(root));
		addToCheckSum(root);
	}

	@Override
	public void read() {
		ObjectSet<ComplexRoot> result = db().query(ComplexRoot.class);
		if(result.size() != 1) {
			throw new IllegalStateException();
		}
		ComplexHolder0 holder = result.get(0)._holder;
		db().activate(holder, Integer.MAX_VALUE);
		addToCheckSum(holder);
	}

	@Override
	public void query() {
		int holderId = objectCount() * objectCount() + objectCount();
		System.err.println("QUERYING FOR " + holderId);
		Query query = db().query();
		query.constrain(ComplexHolder2.class);
//		query.descend("_id2").constrain(holderId);
		ObjectSet<ComplexHolder2> result = query.execute();
//		if(result.size() != 1) {
//			throw new IllegalStateException("" + result.size());
//		}
		for (ComplexHolder2 holder : result) {
			if(holder.getClass() == ComplexHolder2.class) {
				System.err.println(holder._i2);
			}
		}
		ComplexHolder2 holder = result.get(0);
		db().activate(holder, Integer.MAX_VALUE);
		addToCheckSum(holder);
	}

	@Override
	public void delete() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configure(Configuration config) {
		config.objectClass(ComplexHolder2.class).objectField("_i2").indexed(true);
	}


}
