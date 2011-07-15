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


import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import org.polepos.circuits.strings.N1;
import org.polepos.circuits.strings.StringsDriver;

public final class StringsMongoDB extends AbstractMongoDBDriver implements StringsDriver {

    private static final String S0 = "s0";
    private static final String S1 = "s1";
    private static final String S2 = "s2";
    private static final String S3 = "s3";
    private static final String S4 = "s4";
    private static final String S5 = "s5";
    private static final String S6 = "s6";
    private static final String S7 = "s7";
    private static final String S8 = "s8";
    private static final String S9 = "s9";

    public StringsMongoDB(ConsistencyOption consistencyOption, Mongo mongoInstance) {
        super(consistencyOption,mongoInstance);
    }

    @Override
    public void write() {

        int numobjects = setup().getObjectCount();
        int commitinterval  = setup().getCommitInterval();
        int commitctr = 0;

        for ( int i = 1; i <= numobjects; i++ ){
            dbCollection().insert(serialize(N1.generate(i)));

            if ( commitinterval > 0  &&  ++commitctr >= commitinterval ){
                commitctr = 0;
                dbCollection().commit();
            }

            addToCheckSum(i);
        }
        dbCollection().commit();
    }

    @Override
    public void read() {
        final DBCursor result = dbCollection().find();
        for (DBObject dbObject : result) {
            // the db4o implementation also does nothing with the objects
        }

    }

    private DBObject serialize(N1 toSerialize) {
        DBObject document = new BasicDBObject();
        document.put(S0,toSerialize.getS0());
        document.put(S1,toSerialize.getS1());
        document.put(S2,toSerialize.getS2());
        document.put(S3,toSerialize.getS3());
        document.put(S4,toSerialize.getS4());
        document.put(S5,toSerialize.getS5());
        document.put(S6,toSerialize.getS6());
        document.put(S7,toSerialize.getS7());
        document.put(S8,toSerialize.getS8());
        document.put(S9,toSerialize.getS9());
        return document;
    }
    private N1 deSerialize(DBObject document) {
        N1 result = new N1();
        result.setS0((String) document.get(S0));
        result.setS1((String) document.get(S1));
        result.setS2((String) document.get(S2));
        result.setS3((String) document.get(S3));
        result.setS4((String) document.get(S4));
        result.setS5((String) document.get(S5));
        result.setS6((String) document.get(S6));
        result.setS7((String) document.get(S7));
        result.setS8((String) document.get(S8));
        result.setS9((String) document.get(S9));
        return result;
    }
}
