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

import com.mongodb.DB;
import com.mongodb.Mongo;
import org.polepos.framework.DriverBase;

/**
 * @author roman.stoffel@gamlor.info
 * @since 13.07.11
 */
public abstract class AbstractMongoDBDriver extends DriverBase {
    private final Mongo mongoInstance;
    private final DB dbInstance;
    private final DBCollectionOperations dbCollection;

    protected AbstractMongoDBDriver(ConsistencyOption consistencyOption, Mongo monoInstance) {
        mongoInstance = monoInstance;
        dbInstance = mongoInstance.getDB(MongoDBTeam.DB_NAME);
        dbCollection = DBCollectionOperations.create(dbInstance.getCollection(collectionName()), consistencyOption);
    }

    public String collectionName() {
        return this.getClass().getSimpleName() + "Collection";
    }

    @Override
    public final void prepare() {
        doPreparation();
    }

    protected void doPreparation() {
    }



    @Override
    public final void closeDatabase() {
    }

    protected DBCollectionOperations dbCollection() {
        return dbCollection;
    }
}
