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
import org.bson.types.ObjectId;
import org.polepos.circuits.flatobject.FlatObject;
import org.polepos.data.IndexedObject;
import org.polepos.util.NoArgFunction;

/**
 * @author roman.stoffel@gamlor.info
 * @since 13.07.11
 */
public final class FlatObjectMongoDB extends AbstractMongoDBDriver implements FlatObject{

    private static final String INT_FIELD = "_int";
    private static final String STRING_FIELD = "_string";
    private static final String DOCUMENT_ID = "_id";

    protected FlatObjectMongoDB(ConsistencyOption consistencyOption, Mongo mongoInstance) {
        super(consistencyOption, mongoInstance);
    }

    @Override
    protected void doPreparation() {
        super.doPreparation();
        dbCollection().createIndex(new BasicDBObject(INT_FIELD, 1));
        dbCollection().createIndex(new BasicDBObject(STRING_FIELD, 1));
    }

    @Override
    public void write() throws Throwable {
        initializeTestId(objects());
		while ( hasMoreTestIds()){
			IndexedObject indexedObject = new IndexedObject(nextTestId());
			dbCollection().insert(serialize(indexedObject));
			if(doCommit()){
				dbCollection().commit();
			}
            addToCheckSum(indexedObject);
		}
    }

    @Override
    public void queryIndexedString() throws Throwable {
        queryIndexRun(new NoArgFunction<DBObject>() {
            @Override
            public DBObject invoke() {
                return new BasicDBObject(STRING_FIELD, IndexedObject.queryString(nextTestId()));
            }
        });
    }

    @Override
    public void queryIndexedInt() throws Throwable {
        queryIndexRun(new NoArgFunction<DBObject>() {
            @Override
            public DBObject invoke() {
                return new BasicDBObject(INT_FIELD, nextTestId());
            }
        });
    }

    @Override
    public void update() throws Throwable {
        initializeTestId(updates());
        while(hasMoreTestIds()) {
            final DBObject document = dbCollection().findOne(new BasicDBObject(INT_FIELD, nextTestId()));
            IndexedObject indexedObject = deSerialize(document);
        	indexedObject.updateString();
        	dbCollection().save(serialize(indexedObject,(ObjectId) document.get(DOCUMENT_ID)));
            addToCheckSum(indexedObject);
        }
        dbCollection().commit();
    }

    @Override
    public void delete() throws Throwable {
        initializeTestId(updates());
        while(hasMoreTestIds()) {
            final DBObject document = dbCollection().findOne(new BasicDBObject(INT_FIELD, nextTestId()));
            IndexedObject indexedObject = deSerialize(document);
        	addToCheckSum(indexedObject);
            dbCollection().remove(document);
        }
        dbCollection().commit();
    }

    private void queryIndexRun(NoArgFunction<DBObject> queryToRunFactory) {
        initializeTestId(selects());
        while(hasMoreTestIds()) {
            final DBCursor result = dbCollection().find(queryToRunFactory.invoke());
            for (DBObject document : result) {
                addToCheckSum(deSerialize(document));
            }
        }
    }

    private DBObject serialize(IndexedObject indexedObject) {
        return serialize(indexedObject,new ObjectId());
    }
    private DBObject serialize(IndexedObject indexedObject, ObjectId withExistingId) {
        DBObject serializedVersion = new BasicDBObject(DOCUMENT_ID,withExistingId);
        serializedVersion.put(INT_FIELD,indexedObject._int);
        serializedVersion.put(STRING_FIELD,indexedObject._string);
        return serializedVersion;
    }
    private IndexedObject deSerialize(DBObject serializedVersion) {
        IndexedObject result = new IndexedObject();
        result._int = (Integer) serializedVersion.get(INT_FIELD);
        result._string = (String) serializedVersion.get(STRING_FIELD);
        return result;
    }
}
