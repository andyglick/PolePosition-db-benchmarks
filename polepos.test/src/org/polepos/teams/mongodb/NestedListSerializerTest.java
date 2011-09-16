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
import com.mongodb.DBObject;
import junit.framework.Assert;
import org.junit.Test;
import org.polepos.data.ListHolder;
import org.polepos.util.OneArgAction;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class NestedListSerializerTest {

    @Test
    public void serializeSimpleDocument() {
        final ListHolder toSerialize = ListHolder.generate(1, 1, 0);
        final DBObject document = NestedListSerializer.convertToDocument(toSerialize);

        final ListHolder deserializedDocument = NestedListSerializer.convertFromDocument(document);

        assertAreEqual(toSerialize, deserializedDocument);
    }
    @Test
    public void serializeComplexNoReuseDocument() {
        final ListHolder toSerialize = ListHolder.generate(3, 50, 0);
        final DBObject document = NestedListSerializer.convertToDocument(toSerialize);

        final ListHolder deserializedDocument = NestedListSerializer.convertFromDocument(document);

        assertAreEqual(toSerialize, deserializedDocument);
    }
    @Test
    public void serializeComplexWithReuseDocument() {
        final ListHolder toSerialize = ListHolder.generate(4, 50, 30);
        final DBObject document = NestedListSerializer.convertToDocument(toSerialize);

        final ListHolder deserializedDocument = NestedListSerializer.convertFromDocument(document);

        assertAreEqual(toSerialize, deserializedDocument);
    }
    @Test
    public void serializeWithRealDB() {

        MongoDBHelper.withCollection(new OneArgAction<DBCollection>() {
            @Override
            public void invoke(DBCollection collection) {
                final ListHolder original = ListHolder.generate(4, 50, 30);
                DBObject document = NestedListSerializer.convertToDocument(original);
                collection.insert(document);

                final ListHolder restoredVerison = NestedListSerializer.convertFromDocument(collection.findOne());
                assertAreEqual(original,restoredVerison);

            }
        });
    }

    private void assertAreEqual(ListHolder toSerialize,
                                ListHolder deserializedDocument) {
        assertAreEqual(toSerialize, deserializedDocument, new HashSet<ListHolder>());
    }

    private void assertAreEqual(ListHolder toSerialize,
                                ListHolder deserializedDocument, Set<ListHolder> alreadyChecked) {
        Assert.assertEquals(toSerialize.id(), deserializedDocument.id());
        Assert.assertEquals(toSerialize.name(), deserializedDocument.name());
        alreadyChecked.add(toSerialize);
        if (null != toSerialize.list()) {
            Assert.assertEquals(toSerialize.list().size(), deserializedDocument.list().size());
            Iterator<ListHolder> desirializedIt = deserializedDocument.list().iterator();
            for (ListHolder originalChild : toSerialize.list()) {
                final ListHolder next = desirializedIt.next();
                if (!alreadyChecked.contains(originalChild)) {
                    assertAreEqual(originalChild, next, alreadyChecked);
                }
            }
        }

    }
}
