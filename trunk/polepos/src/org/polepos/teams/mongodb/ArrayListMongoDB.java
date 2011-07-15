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
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import org.polepos.circuits.arraylists.ArrayListsDriver;
import org.polepos.circuits.arraylists.ListHolder;

import java.util.List;

public final class ArrayListMongoDB extends AbstractMongoDBDriver implements ArrayListsDriver {

    private static final String LIST_FIELD = "list";

    public ArrayListMongoDB(ConsistencyOption consistencyOption, Mongo mongoInstance) {
        super(consistencyOption, mongoInstance);
    }

    @Override
    public void write() {

        int count = 1000;
        int elements = setup().getObjectSize();


        for (int i = 1; i <= count; i++) {
            DBObject serializedList = serialize(ListHolder.generate(i, elements));
            dbCollection().insert(serializedList);
        }
        dbCollection().commit();
    }

    @Override
    public void read() {
        final DBCursor dbCursor = dbCollection().find();
        for (DBObject dbObject : dbCursor) {
            addToCheckSum(deSerialize(dbObject));
        }
    }

    private DBObject serialize(ListHolder toSerialize) {
        DBObject document = new BasicDBObject();
        document.put(LIST_FIELD, toSerialize.getList());
        return document;
    }

    private ListHolder deSerialize(DBObject toDeserialize) {
        ListHolder instance = new ListHolder();
        instance.setList((List) toDeserialize.get(LIST_FIELD));
        return instance;
    }


}
