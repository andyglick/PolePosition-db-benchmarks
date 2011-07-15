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
import org.polepos.circuits.nativeids.NativeIdsDriver;
import org.polepos.data.Pilot;

public final class NativeIdsMongoDB extends AbstractMongoDBDriver implements NativeIdsDriver {
    private static final String ID_FIELD = "_id";
    private static final String FIRST_NAME_FIELD = "firstName";
    private static final String LICENSE_ID_FIELD = "license";
    private static final String NAME_FIELD = "name";
    private static final String POINTS_FIELD = "points";

    private ObjectId[] ids = new ObjectId[0];

    public NativeIdsMongoDB(ConsistencyOption consistencyOption, Mongo mongoInstance) {
        super(consistencyOption, mongoInstance);
    }

    @Override
    public void store() {
        ids = new ObjectId[setup().getSelectCount()];
        int count = setup().getObjectCount();
        for (int i = 1; i <= count; i++) {
            storePilot(i);
        }
        dbCollection().commit();
    }
    @Override
    public void retrieve() {
        for (ObjectId id : ids) {
            Pilot pilot = deSerialize(dbCollection().findOne(new BasicDBObject(ID_FIELD,id)));
            if (pilot == null) {
                System.err.println("Object not found by ID.");
            } else {
                addToCheckSum(pilot.getPoints());
            }
        }
    }

    private void storePilot(int idx) {
        Pilot pilot = new Pilot("Pilot_" + idx, "Jonny_" + idx, idx, idx);
        DBObject document = serialize(pilot);
        dbCollection().insert(document);
        if (idx <= setup().getSelectCount()) {
            ids[idx - 1] = (ObjectId) document.get(ID_FIELD);
        }
        if (isCommitPoint(idx)) {
            dbCollection().commit();
        }
    }

    private boolean isCommitPoint(int idx) {
        int commitInterval = setup().getCommitInterval();
        return commitInterval > 0 && idx % commitInterval == 0 && idx < setup().getObjectCount();
    }

    static DBObject serialize(Pilot toSerialize) {
        DBObject document = new BasicDBObject("_id", new ObjectId());
        document.put(FIRST_NAME_FIELD, toSerialize.getFirstName());
        document.put(LICENSE_ID_FIELD, toSerialize.getLicenseID());
        document.put(NAME_FIELD, toSerialize.getName());
        document.put(POINTS_FIELD, toSerialize.getPoints());
        return document;
    }
    static Pilot deSerialize(DBObject document) {
        Pilot result = new Pilot();
        result.setFirstName((String) document.get(FIRST_NAME_FIELD));
        result.setLicenseID((Integer) document.get(LICENSE_ID_FIELD));
        result.setName((String) document.get(NAME_FIELD));
        result.setPoints((Integer) document.get(POINTS_FIELD));
        result.setFirstName((String) document.get(FIRST_NAME_FIELD));
        result.setFirstName((String) document.get(FIRST_NAME_FIELD));
        return result;
    }
}
