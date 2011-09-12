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
