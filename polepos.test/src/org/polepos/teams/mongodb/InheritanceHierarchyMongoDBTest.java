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


import com.mongodb.Mongo;
import org.junit.Test;
import org.polepos.util.CarStub;
import org.polepos.util.NoArgAction;
import org.polepos.util.OneArgAction;

import static org.polepos.teams.mongodb.CircuitsTestsHelpers.assertDeleteCleansAll;
import static org.polepos.teams.mongodb.RaceUtils.newTurn;
import static org.polepos.util.JavaLangUtils.rethrow;

public class InheritanceHierarchyMongoDBTest {


    @Test
    public void deletingObjectsWorks() {
        MongoDBHelper.withMongoDBRunning(new OneArgAction<Mongo>() {
            @Override
            public void invoke(Mongo mongo) {
                final InheritanceHierarchyMongoDB toTest = prepareForTest(mongo);

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


    private InheritanceHierarchyMongoDB prepareForTest(Mongo mongo) {
        final InheritanceHierarchyMongoDB toTest = new InheritanceHierarchyMongoDB(ConsistencyOption.NORMAL, mongo);
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
