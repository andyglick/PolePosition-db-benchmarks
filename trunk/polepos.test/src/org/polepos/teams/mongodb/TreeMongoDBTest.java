package org.polepos.teams.mongodb;


import com.mongodb.Mongo;
import org.junit.Test;
import org.polepos.util.NoArgAction;
import org.polepos.util.OneArgAction;

import static org.polepos.teams.mongodb.CircuitsTestsHelpers.assertDeleteCleansAll;
import static org.polepos.teams.mongodb.RaceUtils.newTurn;
import static org.polepos.util.JavaLangUtils.rethrow;

public class TreeMongoDBTest {

    @Test
    public void deletingObjectsWorks() {
        MongoDBHelper.withMongoDBRunning(new OneArgAction<Mongo>() {
            @Override
            public void invoke(Mongo mongo) {
                final TreesMongoDB toTest = prepareForTest(mongo);

                assertDeleteCleansAll(mongo, toTest, new NoArgAction() {
                    @Override
                    public void invoke() {
                        try {
                            toTest.delete();
                        } catch (Throwable throwable) {
                            rethrow(throwable);
                        }
                    }
                });
            }
        });
    }

    private TreesMongoDB prepareForTest(Mongo mongo) {
        final TreesMongoDB toTest = new TreesMongoDB(ConsistencyOption.NORMAL, mongo);
        toTest.configure(new CarStub(), newTurn());
        toTest.prepare();
        try {
            toTest.write();
        } catch (Throwable e) {
            rethrow(e);
        }
        return toTest;
    }
}
