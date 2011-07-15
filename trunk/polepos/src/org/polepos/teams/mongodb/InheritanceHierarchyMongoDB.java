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
import org.polepos.circuits.inheritancehierarchy.InheritanceHierarchy;
import org.polepos.data.InheritanceHierarchy4;

public final class InheritanceHierarchyMongoDB extends AbstractMongoDBDriver implements InheritanceHierarchy {


    private static final String I1_FIELD = "i1";
    private static final String I2_FIELD = "i2";
    private static final String I3_FIELD = "i3";
    private static final String I4_FIELD = "i4";

    public InheritanceHierarchyMongoDB(ConsistencyOption consistencyOption, Mongo mongoInstance) {
        super(consistencyOption, mongoInstance);
    }

    @Override
    protected void doPreparation() {
        super.doPreparation();
        dbCollection().createIndex(new BasicDBObject(I2_FIELD, 1));
    }

    @Override
    public void write() {
        int count = setup().getObjectCount();
        for (int i = 1; i <= count; i++) {
            InheritanceHierarchy4 inheritancheHierarchy4 = new InheritanceHierarchy4();
            inheritancheHierarchy4.setAll(i);
            final DBObject document = serialize(inheritancheHierarchy4);
            dbCollection().insert(document);
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

    @Override
    public void query() {
        int count = setup().getSelectCount();
        for (int i = 1; i <= count; i++) {
            final DBCursor dbCursor = dbCollection().find(new BasicDBObject(I2_FIELD, i));
            for (DBObject dbObject : dbCursor) {
                addToCheckSum(deSerialize(dbObject));
            }
        }
    }

    @Override
    public void delete() {
        for (DBObject dbObject : dbCollection().find()) {
            dbCollection().remove(dbObject);
            addToCheckSum(5);
        }
        dbCollection().commit();
    }


    private DBObject serialize(InheritanceHierarchy4 toSerialize) {
        DBObject document = new BasicDBObject();
        document.put(I1_FIELD, toSerialize.getI1());
        document.put(I2_FIELD, toSerialize.getI2());
        document.put(I3_FIELD, toSerialize.getI3());
        document.put(I4_FIELD, toSerialize.getI4());
        return document;
    }
    private InheritanceHierarchy4 deSerialize(DBObject document) {
        InheritanceHierarchy4 result = new InheritanceHierarchy4();
        result.setI1((Integer) document.get(I1_FIELD));
        result.setI2((Integer) document.get(I2_FIELD));
        result.setI3((Integer) document.get(I3_FIELD));
        result.setI4((Integer) document.get(I4_FIELD));
        return result;
    }
}
