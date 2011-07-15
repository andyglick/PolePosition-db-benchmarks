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


import com.mongodb.Mongo;
import org.polepos.framework.Car;
import org.polepos.framework.DriverBase;
import org.polepos.framework.Team;

import java.net.UnknownHostException;

import static org.polepos.util.JavaLangUtils.rethrow;

public final class MongoDBTeam extends Team{
    public static final String DB_NAME = "ComplexMongoDB";
    private final MongoDBConfiguration configuration = new MongoDBConfiguration();

    @Override
    public String name() {
        return "mongoDB";
    }

    @Override
    public String description() {
        return "open source, high-performance, schema-free, document-oriented database";
    }

    @Override
    public Car[] cars() {
        return new Car[]{
                new MongoDBCar(this)
        };
    }

    @Override
    public DriverBase[] drivers() {
        final ConsistencyOption consistencyUsed = readLevelFromConfig();
        final Mongo mongoInstance;
        try {
            mongoInstance = new Mongo(readHostFromConfig(),readPortFromConfig());
        } catch (UnknownHostException e) {
            throw rethrow(e);
        }
        return new DriverBase[]{
                new ArrayListMongoDB(consistencyUsed,mongoInstance),
                new CommitMongoDB(consistencyUsed,mongoInstance),
                new FlatObjectMongoDB(consistencyUsed,mongoInstance),
                new InheritanceHierarchyMongoDB(consistencyUsed,mongoInstance),
                new NativeIdsMongoDB(consistencyUsed,mongoInstance),
                new NestedListsMongoDB(consistencyUsed,mongoInstance),
                new StringsMongoDB(consistencyUsed,mongoInstance),
                new TreesMongoDB(consistencyUsed,mongoInstance),
                new ComplexMongoDB(consistencyUsed, mongoInstance),
                new ComplexConcurrencyMongoDB(consistencyUsed, mongoInstance)
        };
    }

    @Override
    public String website() {
        return "http://www.mongodb.org";
    }

    @Override
    public String databaseFile() {
        return null;
    }

    @Override
    public void setUp() {
        try {
            final Mongo mongo = new Mongo(readHostFromConfig(),readPortFromConfig()); 
            mongo.getDB(DB_NAME).dropDatabase();
        } catch (UnknownHostException e) {
            throw rethrow(e);
        }
    }

    private ConsistencyOption readLevelFromConfig() {
        final String consistencyLevel = configuration.getConsistencyLevel();
        if(null==consistencyLevel){
            return ConsistencyOption.NORMAL;
        }else{
            return ConsistencyOption.valueOf(consistencyLevel);
        }
    }

    private String readHostFromConfig() {
        return configuration.getHost();
    }
    private int readPortFromConfig() {
        return configuration.getPort();
    }
}
