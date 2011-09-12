package org.polepos.teams.mongodb;

import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import junit.framework.Assert;
import org.polepos.util.NoArgAction;

final class CircuitsTestsHelpers {
    private CircuitsTestsHelpers() {
    }

    static void assertUpdateDoesNotIncreaseDocumentCount(
            Mongo mongo,
            AbstractMongoDBDriver circuit,
            NoArgAction updateClosuere) {

        final DBCollection collection = mongo.getDB(MongoDBTeam.DB_NAME).getCollection(circuit.collectionName());
        int countBeforeUpdate = collection.find().size();
        updateClosuere.invoke();
        int countAfterUpdate = collection.find().size();
        Assert.assertEquals(countBeforeUpdate, countAfterUpdate);

    }
    static void assertDeleteCleansAll(
            Mongo mongo,
            AbstractMongoDBDriver circuit,
            NoArgAction deleteClosure) {

        final DBCollection collection = mongo.getDB(MongoDBTeam.DB_NAME).getCollection(circuit.collectionName());
        deleteClosure.invoke();
        int afterDeletionsRun = collection.find().size();
        Assert.assertEquals(0, afterDeletionsRun);

    }
}
