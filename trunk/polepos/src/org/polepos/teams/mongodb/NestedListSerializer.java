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
import org.bson.types.ObjectId;
import org.polepos.data.ListHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class NestedListSerializer {

    static final String DOCUMENT_ID = "_id";
    static final String ID_FIELD = "id";
    static final String NAME_FIELD = "name";
    private static final String MAP_OF_LIST_ITEMS = "mapOfListItems";
    private static final String LIST_FIELD = "list";
    private static final int INITIAL_HASHMAP_SIZE = 1024 * 1024;

    static DBObject convertToDocument(ListHolder toSerialize){
        return convertToDocument(toSerialize,new ObjectId());
    }
    static DBObject convertToDocument(ListHolder toSerialize, ObjectId idOfDocument){
        final HashMap<String, DBObject> childrens = new HashMap<String, DBObject>(INITIAL_HASHMAP_SIZE);
        final DBObject document = serialize(toSerialize, childrens);
        final DBObject rootDocument = new BasicDBObject(DOCUMENT_ID,idOfDocument);
        rootDocument.put(ID_FIELD,document.get(ID_FIELD));
        rootDocument.put(NAME_FIELD,document.get(NAME_FIELD));
        rootDocument.put(LIST_FIELD,document.get(LIST_FIELD));
        rootDocument.put(MAP_OF_LIST_ITEMS,childrens);
        return rootDocument;
    }

    static ListHolder convertFromDocument(DBObject document) {
        Map<String,DBObject> documentMap = (Map<String, DBObject>) document.get(MAP_OF_LIST_ITEMS);
        return convertFromDocument(document,documentMap,new HashMap<Long, ListHolder>());
    }

    private static ListHolder convertFromDocument(DBObject document,
                                          Map<String,DBObject> documentMap,
                                          Map<Long,ListHolder> deserializedObjectsMap) {
        ListHolder result = new ListHolder();
        result.id((Long) document.get(ID_FIELD));
        deserializedObjectsMap.put(result.id(), result);
        result.name((String) document.get(NAME_FIELD));
        if(null!=document.get(LIST_FIELD)) {
            result.list(deSerializeList((List<Long>) document.get(LIST_FIELD), documentMap, deserializedObjectsMap));
        }
        return result;
    }

    private static List<ListHolder> deSerializeList(List<Long> idsOfChildDocuments,
                                                    Map<String, DBObject> documentMap,
                                                    Map<Long, ListHolder> deserializedObjectsMap) {
        List<ListHolder> resultList = new ArrayList<ListHolder>(idsOfChildDocuments.size());
        for (Long idOfListMember : idsOfChildDocuments) {
            ListHolder holder = deserializedObjectsMap.get(idOfListMember);
            if(null==holder){
                final DBObject document = documentMap.get(String.valueOf(idOfListMember));
                holder = convertFromDocument(document,documentMap, deserializedObjectsMap);
            }
            resultList.add(holder);
        }
        return resultList;
    }

    private static DBObject serialize(ListHolder toSerialize, Map<String,DBObject> listItemMap) {
        DBObject document = new BasicDBObject();
        document.put(ID_FIELD, toSerialize.id());
        listItemMap.put(String.valueOf(toSerialize.id()),document);
        document.put(NAME_FIELD, toSerialize.name());
        if(null!=toSerialize.list()) {
            document.put(LIST_FIELD, idsOfList(toSerialize.list(),listItemMap));
        }
        return document;
    }

    private static Object idsOfList(List<ListHolder> list, Map<String,DBObject> listItemMap) {
        ArrayList<Long> ids = new ArrayList<Long>(list.size());
        for (ListHolder listHolder : list) {
            ids.add(listHolder.id());
            if(!listItemMap.containsKey(String.valueOf(listHolder.id()))){
                serialize(listHolder, listItemMap);
            }
        }
        return ids;
    }
}
