/*
 * This file is part of the PolePosition database benchmark
 * http://www.polepos.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA  02111-1307, USA.MA  02111-1307, USA.
 */

package org.polepos.teams.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import org.junit.Test;
import org.polepos.framework.DriverBase;
import org.polepos.util.OneArgAction;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class MongoDBTeamTest {

    private final MongoDBTeam toTest;

    public MongoDBTeamTest() {
        this.toTest = new MongoDBTeam();
    }

    @Test
    public void hasDriver(){
        final List<DriverBase> drivers = asList(toTest.drivers());
        assertFalse(drivers.isEmpty());
    }

    @Test
    public void setupCleansDB(){
        MongoDBHelper.withMongoDBRunning(new OneArgAction<Mongo>() {
            @Override
            public void invoke(Mongo mongo) {

                storeData(mongo);
                toTest.setUp();

                final DBCollection collection = collection(mongo);
                final int entryCount = collection.find().size();
                assertEquals(0, entryCount);
            }
        });

    }

    private void storeData(Mongo mongo) {
        collection(mongo).insert(new BasicDBObject("test", "object"));
    }

    private DBCollection collection(Mongo mongo) {
        DB db = mongo.getDB(MongoDBTeam.DB_NAME);
        return db.getCollection("CollectionName");
    }
}
