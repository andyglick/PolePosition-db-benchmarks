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

import java.util.Collection;
import java.util.Collections;


final class SerialisationResult {
    private BasicDBObject result;
    private Collection<BasicDBObject> referencedObjects;

    public SerialisationResult(BasicDBObject result, Collection<BasicDBObject> referencedObjects) {
        this.result = result;
        this.referencedObjects = referencedObjects;
    }

    public BasicDBObject getResult() {
        return result;
    }

    public Collection<BasicDBObject> getReferencedObjects() {
        return Collections.unmodifiableCollection(referencedObjects);
    }

    public BasicDBObject[] allObjectsAsArray(){
        final BasicDBObject[] array = referencedObjects.toArray(new BasicDBObject[referencedObjects.size() + 1]);
        array[array.length-1] =result;
        return array;
    }
}
