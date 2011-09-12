package org.polepos.teams.mongodb;


import com.mongodb.Mongo;
import org.junit.Test;
import org.polepos.util.NoArgAction;
import org.polepos.util.OneArgAction;

import static org.polepos.teams.mongodb.CircuitsTestsHelpers.assertDeleteCleansAll;
import static org.polepos.teams.mongodb.CircuitsTestsHelpers.assertUpdateDoesNotIncreaseDocumentCount;
import static org.polepos.util.JavaLangUtils.rethrow;

public class ComplexMongoDBTest {


    @Test
    public void writeComplexObject() {
        MongoDBHelper.withMongoDBRunning(new OneArgAction<Mongo>() {
            @Override
            public void invoke(Mongo mongo) {
                ComplexMongoDB toTest = new ComplexMongoDB(ConsistencyOption.NORMAL, mongo);
                toTest.configure(new CarStub(), RaceUtils.newTurn());
                toTest.prepare();
                toTest.write();

            }
        });

    }


    @Test
    public void updateDoesNotCreateMoreDocuments() {
        MongoDBHelper.withMongoDBRunning(new OneArgAction<Mongo>() {
            @Override
            public void invoke(Mongo mongo) {
                final ComplexMongoDB toTest = prepareForTest(mongo);

                assertUpdateDoesNotIncreaseDocumentCount(mongo, toTest, new NoArgAction() {
                    @Override
                    public void invoke() {
                        try {
                            toTest.update();
                        } catch (Throwable throwable) {
                            rethrow(throwable);
                        }
                    }
                });
            }
        });

    }

    @Test
    public void deletingObjectsWorks() {
        MongoDBHelper.withMongoDBRunning(new OneArgAction<Mongo>() {
            @Override
            public void invoke(Mongo mongo) {
                final ComplexMongoDB toTest = prepareForTest(mongo);

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

    private ComplexMongoDB prepareForTest(Mongo mongo) {
        final ComplexMongoDB toTest = new ComplexMongoDB(ConsistencyOption.NORMAL, mongo);
        toTest.configure(new CarStub(), RaceUtils.newTurn());
        toTest.prepare();
        toTest.write();
        return toTest;
    }


}
