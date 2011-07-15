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
import org.polepos.circuits.commits.CommitsDriver;
import org.polepos.circuits.commits.LightObject;

public final class CommitMongoDB extends AbstractMongoDBDriver implements CommitsDriver {
    private static final String NAME_FIELD = "name";

    public CommitMongoDB(ConsistencyOption consistencyOption, Mongo mongoInstance) {
        super(consistencyOption, mongoInstance);
    }

    @Override
    public void write() {
        int commitctr = 0;
        int commitInterval = 50000;

        int count = setup().getObjectCount();


        for (int i = 1; i <= count; i++) {
            insertObjectNumber(i);
            if (commitInterval > 0 && ++commitctr >= commitInterval) {
                commitctr = 0;
                dbCollection().commit();
            }
        }
        dbCollection().commit();
    }

    @Override
    public void commits() {
        int idbase = setup().getObjectCount() + 1;
        int count = setup().getCommitCount();

        for (int i = 1; i <= count; i++) {
            insertObjectNumber(i);
            insertObjectNumber(idbase + i);
            dbCollection().commit();
        }
    }

    private void insertObjectNumber(int i) {
        dbCollection().insert(serialize(new LightObject(i)));
    }


    private DBObject serialize(LightObject toSerialize) {
        DBObject document = new BasicDBObject();
        document.put(NAME_FIELD, toSerialize.getName());
        return document;
    }

    private LightObject deSerialize(DBObject toDeserialize) {
        LightObject instance = new LightObject();
        instance.setName((String) toDeserialize.get(NAME_FIELD));
        return instance;
    }
}
