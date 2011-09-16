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
