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


package org.polepos.data;

import java.util.*;

import org.polepos.framework.*;

public class ListHolder implements CheckSummable {
	
	public static final String ROOT_NAME = "root";
	
	private static final IdGenerator _idGenerator = new IdGenerator();
	
	private long _id;

	private String _name;
	
	private List<ListHolder> _list;
	
	private transient boolean _visited;
	
	public static ListHolder generate(int depth, int leafs, int reuse){
		ListHolder root = generate(new ArrayList<ListHolder>(), depth, leafs, reuse);
		root._name = ROOT_NAME;
		return root;
	}
	
	
	public static ListHolder generate(List<ListHolder> flatList, int depth, int leafs, int reuse){
		if(depth == 0){
			return null;
		}
		ListHolder listHolder = new ListHolder();
		listHolder._id = _idGenerator.nextId();
		
		flatList.add(listHolder);
		if(depth == 1){
			return listHolder;
		}
		listHolder._list = new ArrayList<ListHolder>();
		int childDepth = depth -1;
		for (int i = leafs -1; i >= 0; i--) {
			if(i < reuse){
				int indexInList = (flatList.size() - i) / 2;
				listHolder._list.add(flatList.get(indexInList) );
			} else {
				ListHolder child = generate(flatList, childDepth, leafs, reuse);
				child._name = "child:" + depth + ":" + i;
				listHolder._list.add(child);
			}
		}
		return listHolder;
	}

	@Override
	public long checkSum() {
		return _name.hashCode() * (_list == null ? 1 : _list.size());
	}

	public void accept(Visitor<ListHolder> visitor) {
		visitor.visit(this);
		if(_list == null){
			return;
		}
		Iterator<ListHolder> i = _list.iterator();
		while(i.hasNext()){
			ListHolder child = i.next();
			
			if(child._id > _id){
				
				// We might still get multiple visits of the same
				// child this way but that's fine, we are simulating
				// network access.
				child.accept(visitor);
			}
		}
	}


	public int update(int maxDepth, int depth, int updateCount, Procedure<Object> storeProcedure) {
		if(depth > maxDepth){
			return 0;
		}
		int updatedCount = 1;
		_name = "updated " + _name;
		if(_list != null){
			for (int i = 0; i < updateCount; i++) {
				if(i < _list.size()){
					ListHolder child = _list.get(i);
					updatedCount += child.update(maxDepth, depth +  1, updateCount, storeProcedure);
				}
			}
			if(_list.size() > 1){
				_list.remove(_list.size() - 1);
			}
		}
		storeProcedure.apply(this);
		return updatedCount;
	}
	

}
