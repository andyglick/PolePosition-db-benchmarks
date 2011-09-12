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
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import org.bson.types.ObjectId;
import org.polepos.circuits.complex.Complex;
import org.polepos.data.ComplexHolder0;
import org.polepos.data.ComplexHolder2;
import org.polepos.framework.NullVisitor;
import org.polepos.framework.Visitor;

import java.util.*;

import static org.polepos.teams.mongodb.DeserializationOptions.DO_NOT_INCLUDE_REFERENCES;

public final class ComplexMongoDB extends AbstractMongoDBDriver implements Complex {
    private static final String ID_KEY = "_id";

    /**
     * This is here to reduce the impact of the document-transfer in the
     * query benchmark parts. The query is also fast in MongoDB. However
     * it would transfer the whole document, which shows in this benchmark.
     */
    private static final BasicDBObject SELECT_MASK = createSelectMask();

    private ObjectId objectID;

    ComplexMongoDB(ConsistencyOption consistencyOption, Mongo monoInstance) {
        super(consistencyOption, monoInstance);
    }

    @Override
    public void doPreparation() {
        BasicDBObject index = new BasicDBObject();
        index.put("_i2", 1);
        index.put(ComplexObjectSerialisation.TYPE_ATTRIBUTE, 1);
        dbCollection().createIndex(index);
    }

    @Override
    public Object write() {
        return write(false);
    }

    Object write(boolean disjunctSpecial) {
        ComplexObjectSerialisation serializer = newSerializer();
        ComplexHolder0 holder = ComplexHolder0.generate(depth(), objects(), disjunctSpecial);
        addToCheckSum(holder);

        SerialisationResult result = serializer.convertToDocument(holder);
        dbCollection().insert(result.allObjectsAsArray());
        objectID = (ObjectId) result.getResult().get("_id");

        dbCollection().commit();

        return objectID;
    }

    @Override
    public void read() {
        ComplexObjectSerialisation serializer = newSerializer();
        ComplexHolder0 holder = loadObjectFromDB(objectID, serializer);
        addToCheckSum(holder);
    }

    @Override
    public void query() {
        ComplexObjectSerialisation serializer = newSerializer();
        Map<ObjectId, ComplexHolder2> fetchCache = new HashMap<ObjectId, ComplexHolder2>();
        int selectCount = selects();
        int firstInt = objects() * objects() + objects();
        int lastInt = firstInt + (objects() * objects() * objects()) - 1;
        int currentInt = firstInt;
        for (int run = 0; run < selectCount; run++) {
            BasicDBObject query = new BasicDBObject();
            query.put("_i2", currentInt);
            query.put(ComplexObjectSerialisation.TYPE_ATTRIBUTE, "ComplexHolder2");
            final DBObject resultDoc = dbCollection().findOne(query, SELECT_MASK);
            ComplexHolder2 holder = fetchCache.get(resultDoc.get("_id"));
            if (null == holder) {
                holder = (ComplexHolder2) serializer.convertFromDocument(resultDoc, DO_NOT_INCLUDE_REFERENCES);
                fetchCache.put((ObjectId) resultDoc.get("_id"), holder);
            }
            addToCheckSum(holder.ownCheckSum());
            List<ComplexHolder0> children = holder.getChildren();
            for (ComplexHolder0 child : children) {
                addToCheckSum(child.ownCheckSum());
            }
            ComplexHolder0[] array = holder.getArray();
            for (ComplexHolder0 arrayElement : array) {
                addToCheckSum(arrayElement.ownCheckSum());
            }
            currentInt++;
            if (currentInt > lastInt) {
                currentInt = firstInt;
            }
        }
    }

    @Override
    public void update() {
        update(this.objectID);
    }

    void update(ObjectId objectID) {
        ComplexObjectSerialisation serializer = newSerializer();
        ComplexHolder0 holder = loadObjectFromDB(objectID, serializer);
        holder.traverse(new NullVisitor(),
                new Visitor<ComplexHolder0>() {
                    @Override
                    public void visit(ComplexHolder0 holder) {
                        addToCheckSum(holder.ownCheckSum());
                        holder.setName("updated");
                        List<ComplexHolder0> children = holder.getChildren();
                        ComplexHolder0[] array = new ComplexHolder0[children.size()];
                        for (int i = 0; i < array.length; i++) {
                            array[i] = children.get(i);
                        }
                        holder.setArray(array);
                    }
                });
        for (BasicDBObject object : serializer.convertToDocument(holder).allObjectsAsArray()) {
            dbCollection().save(object);
        }
        dbCollection().commit();
    }

    @Override
    public void delete() {
        delete(this.objectID);
    }

    void delete(ObjectId objectID) {
        final ComplexObjectSerialisation serializer = newSerializer();
        ComplexHolder0 holder = loadObjectFromDB(objectID, serializer);

        // Why do we keep the id? The current implementation doesn't track objects
        // By identity. So there can be multiple copies of the same object.
        // This id tracking is just enough to make the deletion work.
        final Set<ObjectId> alreadyVisited = new HashSet<ObjectId>();
        holder.traverse(
                new NullVisitor(),
                new Visitor<ComplexHolder0>() {
                    @Override
                    public void visit(ComplexHolder0 holder) {
                        if (!alreadyVisited.contains(serializer.idOf(holder))) {
                            addToCheckSum(holder.ownCheckSum());
                            alreadyVisited.add(serializer.idOf(holder));
                            dbCollection().remove(rootID(serializer.idOf(holder)));
                        }
                    }
                });
        dbCollection().remove(rootID(objectID));
        dbCollection().commit();
    }

    private ComplexObjectSerialisation newSerializer() {
        return ComplexObjectSerialisation.create(ComplexObjectSerialisation.createReferenceCreator(dbCollection()));
    }

    private ComplexHolder0 loadObjectFromDB(ObjectId objectID, ComplexObjectSerialisation serializer) {
        final DBObject object = loadRootDoc(objectID);
        return serializer.convertFromDocument(object);
    }

    private DBObject loadRootDoc(ObjectId objectID) {
        return dbCollection().findOne(rootID(objectID));
    }

    private DBObject rootID(ObjectId objectID) {
        return new BasicDBObject(ID_KEY, objectID);
    }

    private static BasicDBObject createSelectMask() {
        BasicDBObject theMask = new BasicDBObject();
        fieldFor(theMask, "");
        fieldFor(theMask, ComplexObjectSerialisation.ARRAY + ".");
        fieldFor(theMask, ComplexObjectSerialisation.CHILDREN + ".");
        return theMask;
    }

    private static void fieldFor(BasicDBObject theMask, String prefix) {
        theMask.put(prefix + ComplexObjectSerialisation.TYPE_ATTRIBUTE, 1);
        theMask.put(prefix + ComplexObjectSerialisation.NAME_ATTRIBUTE, 1);
        theMask.put(prefix + ComplexObjectSerialisation.REFERENCE_TO_ORIGINAL_DOCUMENT, 1);
        theMask.put(prefix + ComplexObjectSerialisation.FIELD_I1, 1);
        theMask.put(prefix + ComplexObjectSerialisation.FIELD_I2, 1);
        theMask.put(prefix + ComplexObjectSerialisation.FIELD_I3, 1);
        theMask.put(prefix + ComplexObjectSerialisation.FIELD_I4, 1);
    }


}
