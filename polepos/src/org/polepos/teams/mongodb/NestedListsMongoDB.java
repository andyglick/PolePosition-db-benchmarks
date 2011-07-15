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


package org.polepos.teams.mongodb;


import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import org.bson.types.ObjectId;
import org.polepos.circuits.nestedlists.NestedLists;
import org.polepos.data.ListHolder;
import org.polepos.framework.Procedure;
import org.polepos.framework.Visitor;

import static org.polepos.teams.mongodb.NestedListSerializer.*;

public final class NestedListsMongoDB extends AbstractMongoDBDriver implements NestedLists {

    protected NestedListsMongoDB(ConsistencyOption consistencyOption, Mongo mongoInstance) {
        super(consistencyOption, mongoInstance);
    }

    @Override
    public void create() throws Throwable {
        final ListHolder listHolder = ListHolder.generate(depth(), objects(), reuse());
        DBObject document = convertToDocument(listHolder);
        dbCollection().insert(document);
        dbCollection().commit();
    }


    @Override
    public void read() throws Throwable {
		ListHolder root = root();
		root.accept(new Visitor<ListHolder>() {
            public void visit(ListHolder listHolder) {
                addToCheckSum(listHolder);
            }
        });
    }

    @Override
    public void update() throws Throwable {
		DBObject rootDoc = rootDocument();
        ListHolder root = root();
		addToCheckSum(root.update(depth(), 0,  new Procedure<ListHolder>() {
			@Override
			public void apply(ListHolder obj) {

			}
		}));
        dbCollection().save(convertToDocument(root, (ObjectId) rootDoc.get(DOCUMENT_ID)));
        dbCollection().commit();
    }

    @Override
    public void delete() throws Throwable {
		DBObject rootDoc = rootDocument();
        ListHolder root = root();
		addToCheckSum(root.delete(depth(), 0,  new Procedure<ListHolder>() {
			@Override
			public void apply(ListHolder obj) {
			}
		}));
        dbCollection().remove(convertToDocument(root, (ObjectId) rootDoc.get(DOCUMENT_ID)));
        dbCollection().commit();
    }

    /**
     * Of course in a document db we only have one document for the whole
     * nested thing. So there only one document.
     * Well we still query run a query to be as similar as other implementations
     * @return
     */
    private ListHolder root() {
        final DBObject document = rootDocument();
        return convertFromDocument(document);
    }

    private DBObject rootDocument() {
        return dbCollection().findOne(new BasicDBObject(NAME_FIELD, ListHolder.ROOT_NAME));
    }

}
