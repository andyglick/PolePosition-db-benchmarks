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

import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.WriteConcern;

/**
* @author roman.stoffel@gamlor.info
* @since 13.07.11
*/
enum ConsistencyOption {
    NORMAL(WriteConcern.NORMAL),
    FSYNC_ALL_OPERATIONS(WriteConcern.FSYNC_SAFE),
    FSYNC_ON_COMMIT(WriteConcern.NORMAL){
        @Override
        public void commitImplementation(DBCollection collection) {
            final DB adminInterface = collection.getDB().getMongo().getDB("admin");
            final CommandResult commandResult = adminInterface.doEval("db.runCommand({fsync:1});");
            Double result = (Double) commandResult.get("ok");
            if(null==result || 0.999 > result){
                throw new RuntimeException("FSync operation on MongoDB failled: "+commandResult);
            }
        }
    };


    private final WriteConcern writeConcernUsed;

    ConsistencyOption(WriteConcern writeConcernUsed) {
        this.writeConcernUsed = writeConcernUsed;
    }

    public WriteConcern writeConcernUsed() {
        return writeConcernUsed;
    }

    public void commitImplementation(DBCollection collection){

    }
}
