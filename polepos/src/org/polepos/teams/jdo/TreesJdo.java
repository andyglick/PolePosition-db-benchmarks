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

package org.polepos.teams.jdo;

import org.polepos.circuits.trees.*;
import org.polepos.framework.*;
import org.polepos.teams.db4o.*;
import org.polepos.teams.jdo.data.JdoTree;
import org.polepos.teams.jdo.data.JdoTreeVisitor;


public class TreesJdo extends JdoDriver implements TreesDriver {
    
    private Object oid;
    
	public void write(){
		begin();
        JdoTree tree = JdoTree.createTree(setup().getDepth());
        db().makePersistent(tree);
        oid = db().getObjectId(tree);
		commit();
	}

	public void read(){
		begin();
        JdoTree tree = (JdoTree)db().getObjectById(oid, false);
        JdoTree.traverse(tree, new JdoTreeVisitor() {
            public void visit(JdoTree tree) {
                addToCheckSum(tree.getDepth());
            }
        });
	}
    
	public void delete(){
		begin();
        JdoTree tree = (JdoTree)db().getObjectById(oid, false);
        JdoTree.traverse(tree, new JdoTreeVisitor() {
            public void visit(JdoTree tree) {
                db().deletePersistent(tree);
            }
        });
		commit();
	}
	
    @Override
    public void copyStateFrom(DriverBase masterDriver) {
    	TreesJdo master = (TreesJdo) masterDriver;
    	oid = master.oid;
    }

}
