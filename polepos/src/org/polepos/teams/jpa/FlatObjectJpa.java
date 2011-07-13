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

import java.util.Collection;

import javax.persistence.Query;

import org.polepos.circuits.flatobject.FlatObject;
import org.polepos.data.IndexedObject;
import org.polepos.teams.jpa.data.JpaIndexedObject;

/**
 * @author Christian Ernst
 */
public class FlatObjectJpa extends JpaDriver implements FlatObject {

	public void write() {
		begin();
		initializeTestId(objects());
		while (hasMoreTestIds()) {
			JpaIndexedObject indexedObject = new JpaIndexedObject(nextTestId());
			store(indexedObject);
			if (doCommit()) {
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
		String filter = "this._string = :param";
		while (hasMoreTestIds()) {
			Query query = db().createQuery(
					"SELECT this FROM " + JpaIndexedObject.class.getName()
							+ " this WHERE " + filter);
			doQuery(query, IndexedObject.queryString(nextTestId()));
		}
		commit();
	}

	public void queryIndexedInt() {
		begin();
		initializeTestId(setup().getSelectCount());
		String filter = "this._int = :param";
		while (hasMoreTestIds()) {
			Query query = db().createQuery(
					"SELECT this FROM " + JpaIndexedObject.class.getName()
							+ " this WHERE " + filter);
			doQuery(query, nextTestId());
		}
		commit();
	}

	public void update() {
		begin();
		String filter = "this._int = :param";
		initializeTestId(setup().getUpdateCount());
		while (hasMoreTestIds()) {
			Query query = db().createQuery(
					"SELECT this FROM " + JpaIndexedObject.class.getName()
							+ " this WHERE " + filter);
			query.setParameter("param", nextTestId());
			Collection result = (Collection) query.getResultList();
			JpaIndexedObject indexedObject = (JpaIndexedObject) result
					.iterator().next();
			indexedObject.updateString();
			addToCheckSum(indexedObject);
		}
		commit();
	}

	public void delete() {
		begin();
		String filter = "this._int = :param";
		initializeTestId(setup().getUpdateCount());
		while (hasMoreTestIds()) {
			Query query = db().createQuery(
					"SELECT this FROM " + JpaIndexedObject.class.getName()
							+ " this WHERE " + filter);
			query.setParameter("param", nextTestId());
			Collection result = (Collection) query.getResultList();
			JpaIndexedObject indexedObject = (JpaIndexedObject) result
					.iterator().next();
			addToCheckSum(indexedObject);
			indexedObject.updateString();
			delete(indexedObject);
		}
		commit();
	}

}
