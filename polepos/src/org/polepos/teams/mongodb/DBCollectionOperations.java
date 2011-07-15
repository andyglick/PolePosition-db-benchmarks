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


import com.mongodb.*;

/**
 * The main purpose of this wrapper is to control the consistency level used
 * by all operations of the benchmark.
 */
final class DBCollectionOperations {
    private final DBCollection theCollection;
    private final ConsistencyOption consistencyOption;

    DBCollectionOperations(DBCollection theCollection, ConsistencyOption consistencyOption) {
        this.theCollection = theCollection;
        this.consistencyOption = consistencyOption;
    }

    public static DBCollectionOperations create(DBCollection collection, ConsistencyOption consistencyOption) {
        return new DBCollectionOperations(collection,consistencyOption);
    }

    public void createIndex(BasicDBObject index) {
        theCollection.createIndex(index);
    }

    public void commit(){
        consistencyOption.commitImplementation(theCollection);
    }

    public WriteResult insert(DBObject... arr) throws MongoException {
        return theCollection.insert(arr,consistencyOption.writeConcernUsed());
    }

    public DBCursor find() {
        return theCollection.find();
    }

    public DBCursor find(DBObject ref) {
        return theCollection.find(ref);
    }

    public DBObject findOne(DBObject obj) throws MongoException {
        return theCollection.findOne(obj);
    }


    public DBObject findOne(DBObject obj, DBObject fields) {
        return theCollection.findOne(obj, fields);
    }

    public WriteResult save(DBObject jo) {
        return theCollection.save(jo,consistencyOption.writeConcernUsed());
    }

    public WriteResult remove(DBObject o) throws MongoException {
        return theCollection.remove(o,consistencyOption.writeConcernUsed());
    }


    public String getName() {
        return theCollection.getName();
    }

    public DB getDB() {
        return theCollection.getDB();
    }

}
