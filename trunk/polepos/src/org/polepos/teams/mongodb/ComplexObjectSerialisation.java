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


import com.db4o.foundation.ArgumentNullException;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import org.bson.types.ObjectId;
import org.polepos.data.*;
import org.polepos.util.OneArgFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.polepos.util.JavaLangUtils.rethrow;

/**
 * Yes, in reality we better use a library which does this for you. But in reality we also
 * would have a more sane data-model, which would make the mapping easier.
 *
 * It also keeps a map of the loaded documents and ids to recognize objects on updates
 */
final class ComplexObjectSerialisation {
    static final String TYPE_ATTRIBUTE = "_t";
    private static final String PACKAGE_NAME = ComplexHolder0.class.getPackage().getName();
    static final String NAME_ATTRIBUTE = "name";
    static final String CHILDREN = "children";
    static final String ARRAY = "array";
    static final String REFERENCE_TO_ORIGINAL_DOCUMENT = "_refToOriginal";
    static final String FIELD_I1 = "_i1";
    static final String FIELD_I2 = "_i2";
    static final String FIELD_I3 = "_i3";
    static final String FIELD_I4 = "_i4";
    private static final String ID_FIELD = "_id";

    private final OneArgFunction<BasicDBObject, DBRef> refCreator;
    private final Map<ComplexHolder0,ObjectId> objectToId = new HashMap<ComplexHolder0, ObjectId>();

    private ComplexObjectSerialisation(OneArgFunction<BasicDBObject, DBRef> refCreator) {
        this.refCreator = refCreator;
    }



    public static ComplexObjectSerialisation create(DBCollectionOperations collection) {
        return create(createReferenceCreator(collection));
    }

    public static ComplexObjectSerialisation create(OneArgFunction<BasicDBObject, DBRef> refCreator) {
        if (null == refCreator) {
            throw new ArgumentNullException("requires a reference creator");
        }
        return new ComplexObjectSerialisation(refCreator);
    }

    public static OneArgFunction<BasicDBObject, DBRef> createReferenceCreator(final DBCollectionOperations collection) {
        if (null == collection) {
            throw new ArgumentNullException("requires a collection");
        }
        return new OneArgFunction<BasicDBObject, DBRef>() {
            @Override
            public DBRef invoke(BasicDBObject basicDBObject) {
                final DB db = collection.getDB();
                final Object id = basicDBObject.get(ID_FIELD);
                if (null == id) {
                    throw new IllegalStateException("Expected an '_id' on the object");
                }
                return new DBRef(db, collection.getName(), id);
            }
        };
    }

    public SerialisationResult convertToDocument(ComplexHolder0 holder) {
        List<BasicDBObject> holder2Objects = new ArrayList<BasicDBObject>();
        BasicDBObject document = convertToDocument(holder, holder2Objects);
        return new SerialisationResult(document, holder2Objects);
    }

    public ComplexHolder0 convertFromDocument(DBObject data) {
        return convertFromDocument(data, DeserializationOptions.FULL_DESERIALISATION);
    }

    public ComplexHolder0 convertFromDocument(DBObject data, DeserializationOptions deserializationOption) {
        ComplexHolder0 instance = createInstance(getAsString(data, TYPE_ATTRIBUTE));
        if(null!=data.get(ID_FIELD)){
            objectToId.put(instance,(ObjectId) data.get(ID_FIELD));
        }
        instance.setName(getAsString(data, NAME_ATTRIBUTE));
        if (null != data.get(CHILDREN)) {
            instance.setChildren(fromMongoObjectToList(getAsList(data, CHILDREN), deserializationOption));
        }
        if (null != data.get(ARRAY)) {
            final List<ComplexHolder0> arrayContent = fromMongoObjectToList(getAsList(data, ARRAY), deserializationOption);
            instance.setArray(arrayContent.toArray(new ComplexHolder0[arrayContent.size()]));
        }
        readAttributes(data, instance);
        return instance;
    }

    public ObjectId idOf(ComplexHolder0 object) {
        return objectToId.get(object);
    }

    private BasicDBObject convertToDocument(ComplexHolder0 holder, List<BasicDBObject> referencedDocumentsCollector) {
        BasicDBObject dbObject = createDocumentWithAttributesOnly(holder);
        dbObject.put(CHILDREN, toMongoObject(holder.getChildren(), referencedDocumentsCollector));
        if (null != holder.getArray()) {
            dbObject.put(ARRAY, toMongoObject(asList(holder.getArray()), referencedDocumentsCollector));
        }
        return dbObject;
    }

    private BasicDBObject createDocumentWithAttributesOnly(ComplexHolder0 holder) {
        ObjectId id = objectToId.get(holder);
        if(null==id){
            id=new ObjectId();
        }
        BasicDBObject dbObject = new BasicDBObject("_id", id);
        dbObject.put(NAME_ATTRIBUTE, holder.getName());
        dbObject.put(TYPE_ATTRIBUTE, holder.getClass().getSimpleName());
        writeSubTypeAttributes(holder, dbObject);
        return dbObject;
    }

    public List<ComplexHolder0> fromMongoObjectToList(List data, DeserializationOptions deserializationOption) {
        List<ComplexHolder0> objects = new ArrayList<ComplexHolder0>(data.size());
        for (Object o : data) {
            objects.add(restoreDocumentOrReference(o, deserializationOption));
        }
        return objects;
    }

    private ComplexHolder0 restoreDocumentOrReference(Object o, DeserializationOptions deserializationOption) {
        DBObject dbObj = (DBObject) o;
        if (null != dbObj.get(REFERENCE_TO_ORIGINAL_DOCUMENT)) {
            return deserializationOption.deserialize(this, dbObj);
        } else {
            return convertFromDocument(dbObj, deserializationOption);
        }
    }

    private static List getAsList(DBObject data, String attributeName) {
        return (List) data.get(attributeName);
    }

    private static String getAsString(DBObject data, String attribute) {
        return (String) data.get(attribute);
    }

    private static ComplexHolder0 createInstance(String className) {
        try {
            return (ComplexHolder0) Thread.currentThread().getContextClassLoader()
                    .loadClass(PACKAGE_NAME + "." + className).newInstance();
        } catch (Exception e) {
            throw rethrow(e);
        }
    }

    private static void writeSubTypeAttributes(ComplexHolder0 holder, BasicDBObject dataStorage) {
        if (holder instanceof ComplexHolder1) {
            dataStorage.put(FIELD_I1, ((ComplexHolder1) holder)._i1);
        }
        if (holder instanceof ComplexHolder2) {
            dataStorage.put(FIELD_I2, ((ComplexHolder2) holder)._i2);
        }
        if (holder instanceof ComplexHolder3) {
            dataStorage.put(FIELD_I3, ((ComplexHolder3) holder)._i3);
        }
        if (holder instanceof ComplexHolder4) {
            dataStorage.put(FIELD_I4, ((ComplexHolder4) holder)._i4);
        }
    }

    private static void readAttributes(DBObject dataStorage, ComplexHolder0 holder) {
        if (holder instanceof ComplexHolder1) {
            ((ComplexHolder1) holder)._i1 = (Integer) dataStorage.get(FIELD_I1);
        }
        if (holder instanceof ComplexHolder2) {
            ((ComplexHolder2) holder)._i2 = (Integer) dataStorage.get(FIELD_I2);
        }
        if (holder instanceof ComplexHolder3) {
            ((ComplexHolder3) holder)._i3 = (Integer) dataStorage.get(FIELD_I3);
        }
        if (holder instanceof ComplexHolder4) {
            ((ComplexHolder4) holder)._i4 = (Integer) dataStorage.get(FIELD_I4);
        }
    }

    private List<Object> toMongoObject(List<ComplexHolder0> children, List<BasicDBObject> referencedDocumentsCollector) {
        List<Object> objects = new ArrayList<Object>(children.size());
        for (ComplexHolder0 child : children) {
            final BasicDBObject document = convertToDocument(child, referencedDocumentsCollector);
            if (isDocumentOnlyReferenced(child)) {
                referencedDocumentsCollector.add(document);
                DBObject copy = createDocumentWithAttributesOnly(child);
                copy.put(REFERENCE_TO_ORIGINAL_DOCUMENT, refCreator.invoke(document));
                objects.add(copy);
            } else {
                objects.add(document);
            }
        }
        return objects;
    }

    private static boolean isDocumentOnlyReferenced(ComplexHolder0 child) {
        return child.getClass().equals(ComplexHolder2.class);
    }

}
