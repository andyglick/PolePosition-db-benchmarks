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
import org.polepos.circuits.trees.Tree;
import org.polepos.circuits.trees.TreeVisitor;
import org.polepos.circuits.trees.TreesDriver;

public final class TreesMongoDB extends AbstractMongoDBDriver implements TreesDriver {

    private ObjectId treeRootID;
    private static final String ID_FIELD = "id";
    private static final String NAME_FIELD = "name";
    private static final String DEPTH_FIELD = "depth";
    private static final String PRECEDING_FIELD = "preceding";
    private static final String SUBSEQUENT_FIELD = "subsequent";
    private static final String DOCUMENT_ID_FIELD = "_id";

    public TreesMongoDB(ConsistencyOption consistencyOption, Mongo mongoInstance) {
        super(consistencyOption, mongoInstance);
    }



    @Override
    public void write() {
        Tree tree = Tree.createTree(setup().getDepth());
        DBObject document = serialize(tree);
        dbCollection().insert(document);
        treeRootID = (ObjectId) document.get(DOCUMENT_ID_FIELD);
		dbCollection().commit();
    }

    @Override
    public void read() {
        Tree tree = readTree();
        Tree.traverse(tree, new TreeVisitor() {
            public void visit(Tree tree) {
                addToCheckSum(tree.getDepth());
            }
        });
    }

    @Override
    public void delete() {
        dbCollection().remove(new BasicDBObject(DOCUMENT_ID_FIELD, treeRootID));
    }

    private Tree readTree() {
        final DBObject document = dbCollection().findOne(new BasicDBObject(DOCUMENT_ID_FIELD, treeRootID));
        return deSerialize(document);
    }

    private Tree deSerialize(DBObject document) {
        Tree result = new Tree();
        result.id = (Integer) document.get(ID_FIELD);
        result.name = (String) document.get(NAME_FIELD);
        result.depth = (Integer) document.get(DEPTH_FIELD);
        if(null!=document.get(PRECEDING_FIELD)){
            result.preceding = deSerialize((DBObject)document.get(PRECEDING_FIELD));
        }
        if(null!=document.get(SUBSEQUENT_FIELD)){
            result.subsequent = deSerialize((DBObject)document.get(SUBSEQUENT_FIELD));
        }
        return result;
    }

    private DBObject serialize(Tree tree){
        DBObject document = new BasicDBObject(DOCUMENT_ID_FIELD,new ObjectId());
        document.put(ID_FIELD,tree.id);
        document.put(NAME_FIELD,tree.name);
        document.put(DEPTH_FIELD,tree.depth);
        if(null!=tree.preceding) {
            document.put(PRECEDING_FIELD,serialize(tree.preceding));
        }
        if(null!=tree.subsequent){
            document.put(SUBSEQUENT_FIELD,serialize(tree.subsequent));
        }
        return document;
    }
}
