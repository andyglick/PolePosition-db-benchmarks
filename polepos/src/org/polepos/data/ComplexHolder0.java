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

import com.db4o.foundation.*;

public class ComplexHolder0 implements CheckSummable {
	
	private ComplexHolder0 _previous;
	
	private String _name;
	
	private List<ComplexHolder0> _children = new ArrayList<ComplexHolder0>();
	
	private ComplexHolder0[] _array;
	
	public static void main(String[] args) {
		ComplexHolder0 root = generate(10, 1);
		System.out.println(root.checkSum());
	}
	
	public static ComplexHolder0 generate(int depth, int leafs){
		ComplexHolder0 complexHolder = new ComplexHolder0();
		complexHolder._name = "root";
		complexHolder._previous = complexHolder;
		createChildren(complexHolder, depth -1, leafs);
		return complexHolder;
	}
	
	
	private static void createChildren(ComplexHolder0 root, int depth, int numChildren) {
		if(depth < 1){
			return;
		}
		
		int factoryIdx = 0;
		int holderIdx = 0;
		List<ComplexHolder0> parentLevel = Arrays.asList(root);
		for (int i = 0; i < depth; i++) {
			Closure4<ComplexHolder0> curFactory = FACTORIES[factoryIdx];
			List<ComplexHolder0> childLevel = new ArrayList<ComplexHolder0>();

			ComplexHolder0 previous = null;
			for (ComplexHolder0 curParent : parentLevel) {
				for (int childIdx = 0; childIdx < numChildren; childIdx++) {
					ComplexHolder0 curChild = curFactory.run();
					curChild._name = String.valueOf(holderIdx);
					curChild._previous = previous;
					curChild._array = createArray(holderIdx);
					curParent.addChild(curChild);
					childLevel.add(curChild);
					previous = curChild;
					holderIdx++;
				}
			}

			parentLevel = childLevel;
			
			factoryIdx++;
			if(factoryIdx == FACTORIES.length) {
				factoryIdx = 0;
			}
		}
		
	}

	private static ComplexHolder0[] createArray(int holderIdx) {
		ComplexHolder0[] holders = new ComplexHolder0[] {
			new ComplexHolder0(),
			new ComplexHolder1(),
			new ComplexHolder2(),
			new ComplexHolder3(),
			new ComplexHolder4(),
		};
		for (int i = 0; i < holders.length; i++) {
			holders[i]._name = "a" + holderIdx + "_" + i;
		}
		return holders;
	}

	private void addChild(ComplexHolder0 child) {
		_children.add(child);
	}


	private static final Closure4[] FACTORIES = {
		new Closure4<ComplexHolder0>(){
			@Override
			public ComplexHolder0 run() {
				return new ComplexHolder0();
			}
		},
		new Closure4<ComplexHolder0>(){
			@Override
			public ComplexHolder0 run() {
				return new ComplexHolder1();
			}
		},
		new Closure4<ComplexHolder0>(){
			@Override
			public ComplexHolder0 run() {
				return new ComplexHolder2();
			}
		},
		new Closure4<ComplexHolder0>(){
			@Override
			public ComplexHolder0 run() {
				return new ComplexHolder3();
			}
		},
		new Closure4<ComplexHolder0>(){
			@Override
			public ComplexHolder0 run() {
				return new ComplexHolder4();
			}
		}
	};

	@Override
	public long checkSum() {
		return internalCheckSum(new IdentityHashMap<ComplexHolder0, ComplexHolder0>());
	}

	private long internalCheckSum(IdentityHashMap<ComplexHolder0, ComplexHolder0> visited) {
		if(visited.containsKey(this)) {
			return 0;
		}
		visited.put(this, this);
		long checkSum = internalCheckSum();
		for (ComplexHolder0 child : _children) {
			checkSum += child.internalCheckSum(visited);
		}
		if(_array != null) {
			for (ComplexHolder0 child : _array) {
				checkSum += child.internalCheckSum(visited);
			}
		}
		if(_previous != null) {
			checkSum += _previous.internalCheckSum(visited);
		}
		return checkSum;
	}

	protected long internalCheckSum() {
		return _name.hashCode();
	}


}
