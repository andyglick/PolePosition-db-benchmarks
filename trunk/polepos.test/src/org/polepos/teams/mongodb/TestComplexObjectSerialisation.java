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


import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import org.junit.Assert;
import org.junit.Test;
import org.polepos.data.*;
import org.polepos.framework.NullVisitor;
import org.polepos.framework.Visitor;
import org.polepos.util.OneArgAction;
import org.polepos.util.OneArgFunction;
import org.polepos.util.TwoArgAction;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static org.polepos.teams.mongodb.DeserializationOptions.DO_NOT_INCLUDE_REFERENCES;


public class TestComplexObjectSerialisation {
    private static final OneArgFunction<BasicDBObject, DBRef> FAKE_REFERENCE_BUILDER = new OneArgFunction<BasicDBObject, DBRef>() {
        @Override
        public DBRef invoke(final BasicDBObject basicDBObject) {
            return new DBRef(null, null, basicDBObject.get("_id")) {
                @Override
                public DBObject fetch() {
                    return basicDBObject;
                }
            };
        }
    };
    private static final ComplexObjectSerialisation fakeReferenceSerialisation = ComplexObjectSerialisation.create(FAKE_REFERENCE_BUILDER);

    @Test
    public void serializeSimpleDocument() {
        final ComplexHolder0 toSerialize = ComplexHolder0.generate(1, 1, false);
        final SerialisationResult document = fakeReferenceSerialisation.convertToDocument(toSerialize);

        final ComplexHolder0 deserializedDocument = fakeReferenceSerialisation.convertFromDocument(document.getResult());

        assertAreEqual(toSerialize, deserializedDocument);
    }

    @Test
    public void doNotSerializeSubChildren() {
        final ComplexHolder0 toSerialize = ComplexHolder0.generate(5, 5, false);
        final SerialisationResult document = fakeReferenceSerialisation.convertToDocument(toSerialize);

        final Collection<BasicDBObject> referencedObjects = document.getReferencedObjects();
        for (BasicDBObject referencedObject : referencedObjects) {
            final Object type = referencedObject.get(ComplexObjectSerialisation.TYPE_ATTRIBUTE);
            Assert.assertEquals("ComplexHolder2", type);
        }
    }


    @Test
    public void partialRestore() {
        final ComplexHolder0 toSerialize = ComplexHolder0.generate(5, 5, false);
        final SerialisationResult document = fakeReferenceSerialisation.convertToDocument(toSerialize);

        final ComplexHolder0 deserializedDocument =
                fakeReferenceSerialisation.convertFromDocument(document.getResult(), DO_NOT_INCLUDE_REFERENCES);

        assertPartiallyEqual(toSerialize, deserializedDocument);
    }

    @Test
    public void returnsListOfComplex2Objects() {
        final ComplexHolder0 toSerialize = ComplexHolder0.generate(5, 5, false);
        final SerialisationResult result = fakeReferenceSerialisation.convertToDocument(toSerialize);
        boolean hasReferencedObjects = false;
        for (BasicDBObject refObject : result.getReferencedObjects()) {
            final ComplexHolder0 docs = fakeReferenceSerialisation.convertFromDocument(refObject);
            Assert.assertTrue(docs instanceof ComplexHolder2);
            hasReferencedObjects = true;
        }
        Assert.assertTrue(hasReferencedObjects);
    }

    @Test
    public void serializeMoreComplexDocument() {
        final ComplexHolder0 toSerialize = ComplexHolder0.generate(5, 5, false);
        final SerialisationResult document = fakeReferenceSerialisation.convertToDocument(toSerialize);

        final ComplexHolder0 deserializedDocument = fakeReferenceSerialisation.convertFromDocument(document.getResult());

        assertAreEqual(toSerialize, deserializedDocument);
    }

    @Test
    public void updatingReturnsTheSameDocuments() {
        MongoDBHelper.withCollection(new OneArgAction<DBCollection>() {
            @Override
            public void invoke(DBCollection dbCollection) {
                final ComplexHolder0 toSerialize = ComplexHolder0.generate(5, 5, false);
                final DBCollectionOperations collection = DBCollectionOperations.create(dbCollection, ConsistencyOption.NORMAL);
                final ComplexObjectSerialisation serializer = ComplexObjectSerialisation.create(collection);
                final SerialisationResult result = serializer.convertToDocument(toSerialize);
                dbCollection.insert(result.allObjectsAsArray());

                final DBObject rootObject = dbCollection.findOne(new BasicDBObject("_id", result.getResult().get("_id")));

                final ComplexHolder0 deserializedDocument = serializer.convertFromDocument(rootObject);
                deserializedDocument.traverse(new NullVisitor<ComplexHolder0>(), new Visitor<ComplexHolder0>() {
                    @Override
                    public void visit(ComplexHolder0 holder) {
                        holder.setName("updated");
                    }
                });
                int documentsBeforeUpdate = collection.find().size();
                final SerialisationResult serialisationResult = serializer.convertToDocument(deserializedDocument);
                for (BasicDBObject object : serialisationResult.allObjectsAsArray()) {
                    collection.save(object);
                }
                int documentsAfterUpdate = collection.find().size();
                Assert.assertEquals(documentsBeforeUpdate,documentsAfterUpdate);


            }
        });
    }

    /**
     * Note that a retrieved document may have a little different structure.
     * Therefore we need to check that we actually can restore that structure as well.
     */
    @Test
    public void serializeWithRealDB() {
        MongoDBHelper.withCollection(new OneArgAction<DBCollection>() {
            @Override
            public void invoke(DBCollection dbCollection) {
                final ComplexHolder0 toSerialize = ComplexHolder0.generate(5, 5, false);
                final ComplexObjectSerialisation serializer = ComplexObjectSerialisation.create(
                        ComplexObjectSerialisation.createReferenceCreator(DBCollectionOperations.create(dbCollection, ConsistencyOption.NORMAL)));
                final SerialisationResult result = serializer.convertToDocument(toSerialize);
                dbCollection.insert(result.allObjectsAsArray());

                final DBObject rootObject = dbCollection.findOne(new BasicDBObject("_id", result.getResult().get("_id")));

                final ComplexHolder0 deserializedDocument = serializer.convertFromDocument(rootObject);
                assertAreEqual(toSerialize, deserializedDocument);
            }
        });
    }

    private void assertAreEqual(ComplexHolder0 original, ComplexHolder0 deserializedDocument) {
        assertFieldsAreEqual(original, deserializedDocument);
        final TwoArgAction<ComplexHolder0, ComplexHolder0> assertMethodForChildren = new TwoArgAction<ComplexHolder0, ComplexHolder0>() {
            @Override
            public void invoke(ComplexHolder0 complexHolder0, ComplexHolder0 complexHolder01) {
                assertAreEqual(complexHolder0, complexHolder01);
            }
        };
        assertAreEqual(original.getChildren(),
                deserializedDocument.getChildren(),
                assertMethodForChildren);
        if (null == original.getArray()) {
            Assert.assertNull(deserializedDocument.getArray());
        } else {
            assertAreEqual(Arrays.asList(original.getArray()),
                    Arrays.asList(deserializedDocument.getArray()),
                    assertMethodForChildren);
        }
    }

    private void assertFieldsAreEqual(ComplexHolder0 original, ComplexHolder0 deserializedDocument) {
        Assert.assertEquals(original.getClass(), deserializedDocument.getClass());
        Assert.assertEquals(original.getName(), deserializedDocument.getName());
        assertSpecialFieldsAreEqual(original, deserializedDocument);
    }

    private void assertPartiallyEqual(ComplexHolder0 original, ComplexHolder0 deserializedDocument) {
        assertFieldsAreEqual(original, deserializedDocument);
        final TwoArgAction<ComplexHolder0, ComplexHolder0> assertMethodForChildren = new TwoArgAction<ComplexHolder0, ComplexHolder0>() {
            @Override
            public void invoke(ComplexHolder0 complexHolder0, ComplexHolder0 complexHolder01) {
                assertPartiallyEqual(complexHolder0, complexHolder01);
            }
        };
        if (!deserializedDocument.getClass().equals(ComplexHolder2.class)) {
            assertAreEqual(original.getChildren(),
                    deserializedDocument.getChildren(),
                    assertMethodForChildren);
            if (null == original.getArray()) {
                Assert.assertNull(deserializedDocument.getArray());
            } else {
                assertAreEqual(Arrays.asList(original.getArray()),
                        Arrays.asList(deserializedDocument.getArray()),
                        assertMethodForChildren);
            }
        }
    }

    private void assertSpecialFieldsAreEqual(ComplexHolder0 holder, ComplexHolder0 deserializedDocument) {

        if (holder instanceof ComplexHolder1) {
            Assert.assertEquals(((ComplexHolder1) holder)._i1, ((ComplexHolder1) deserializedDocument)._i1);
        }
        if (holder instanceof ComplexHolder2) {
            Assert.assertEquals(((ComplexHolder2) holder)._i2, ((ComplexHolder2) deserializedDocument)._i2);
        }
        if (holder instanceof ComplexHolder3) {
            Assert.assertEquals(((ComplexHolder3) holder)._i3, ((ComplexHolder3) deserializedDocument)._i3);
        }
        if (holder instanceof ComplexHolder4) {
            Assert.assertEquals(((ComplexHolder4) holder)._i4, ((ComplexHolder4) deserializedDocument)._i4);
        }
    }

    private void assertAreEqual(List<ComplexHolder0> originalList,
                                List<ComplexHolder0> deserializedDocument,
                                TwoArgAction<ComplexHolder0, ComplexHolder0> assertMethod) {
        Assert.assertEquals(originalList.size(), deserializedDocument.size());
        Iterator<ComplexHolder0> desirializedIt = deserializedDocument.iterator();
        for (ComplexHolder0 original : originalList) {
            Assert.assertTrue(desirializedIt.hasNext());
            assertMethod.invoke(original, desirializedIt.next());
        }
    }
}
