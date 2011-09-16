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


import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import org.polepos.util.OneArgAction;
import org.polepos.util.OperationResult;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.polepos.util.JavaLangUtils.rethrow;

class MongoDBHelper {

    private static final String MONGO_DB_EXECUTABLE = "C:\\progs\\mongodb\\bin\\mongod";
    private static final String DB_PATH = "C:\\progs\\mongodb\\data";
    private static final String MONGO_DB_ARGUMENTS = "--dbpath " + DB_PATH;
    private static final int RETRY_NUMBERS = 10;
    private static final int TIME_OUT_UNIT_RETRY_IN_MILLISEC = 100;
    private static final String TEST_DB = MongoDBTeam.DB_NAME;
    private static final String TEST_COLLECTION = "testCollection";

    public static void withMongoDBRunning(OneArgAction<Mongo> toRun) {
        OperationResult<Mongo> dbInstance = tryGetDBInstance();
        if (dbInstance.wasFailure()) {
            runWithNewDB(toRun);
        } else {
            runAndCloseDB(dbInstance.getResultData(), toRun);
        }
    }

    public static void withCollection(final OneArgAction<DBCollection> toRun) {
        withMongoDBRunning(new OneArgAction<Mongo>() {
            @Override
            public void invoke(Mongo argument) {
                toRun.invoke(collection(argument));
            }
        });
    }

    public static DBCollection collection(Mongo db) {
        return db.getDB(TEST_DB).getCollection(TEST_COLLECTION);
    }

    private static void runWithNewDB(OneArgAction<Mongo> toRun) {
        final Process process = launceMongoDB();
        try {
            waitForDB();
            runAndCloseDB(tryGetDBInstance().getResultData(), toRun);
        } finally {
            endProcess(process);
        }
    }

    private static void waitForDB() {
        for (int i = 0; i < RETRY_NUMBERS; i++) {
            if (tryGetDBInstance().wasSuccessful()) {
                return;
            }
            try {
                Thread.sleep(TIME_OUT_UNIT_RETRY_IN_MILLISEC);
            } catch (InterruptedException e) {
                rethrow(e);
            }
        }
        throw new RuntimeException("Cannot connect to the db", tryGetDBInstance().getException());
    }

    private static void runAndCloseDB(Mongo db, OneArgAction<Mongo> toRun) {
        try {
            db.getDB(TEST_DB).dropDatabase();
            toRun.invoke(db);
            db.getDB(TEST_DB).dropDatabase();
        } finally {
            db.close();
        }
    }

    private static void endProcess(Process process) {
        process.destroy();
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            throw rethrow(e);
        }
    }

    private static Process launceMongoDB() {
        cleanUpDBPath();
        List<String> cmd = new ArrayList<String>();
        cmd.add(MONGO_DB_EXECUTABLE);
        cmd.addAll(Arrays.asList(MONGO_DB_ARGUMENTS.split(" ")));
        ProcessBuilder pb = new ProcessBuilder(cmd);
        try {
            return pb.start();
        } catch (IOException e) {
            throw rethrow(e);
        }
    }

    private static void cleanUpDBPath() {
        final File file = new File(DB_PATH);
        while (file.exists()) {
            deleteDir(file);
        }
        if (!file.exists() && !file.mkdirs()) {
            throw new RuntimeException("Couldn't ensure that the datadirectory exists" + file);
        }
    }

    static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    private static OperationResult<Mongo> tryGetDBInstance() {
        try {
            final Mongo mongo = new Mongo();
            throwIfCannotConnect(mongo);
            return OperationResult.success(mongo);
        } catch (Exception e) {
            return OperationResult.fail(e);
        }
    }

    private static void throwIfCannotConnect(Mongo mongo) {
        mongo.getDB("connection-test-db").getCollection("connection-test").find().size();
    }
}
