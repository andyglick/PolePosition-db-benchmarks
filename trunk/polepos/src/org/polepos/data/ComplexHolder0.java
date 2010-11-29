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

import com.db4o.foundation.*;

public class ComplexHolder0 {
	
	private ComplexHolder0 _child;
	
	private String _name;
	
	private List<ComplexHolder0> _children;
	
	private ComplexHolder0[] _array;
	
	
	public static ComplexHolder0 generate(int depth, int leafs){
		ComplexHolder0 complexHolder = new ComplexHolder0();
		complexHolder._children = new ArrayList<ComplexHolder0>();
		complexHolder._name = "root";
		complexHolder._child = new ComplexHolder0();
		createChildren(depth -1, leafs);
		return complexHolder;
	}
	
	
	private static void createChildren(int depth, int leafs) {
		if(depth < 1){
			return;
		}
		// _array = new ComplexHolder0[depth];
		
		
		for (int i = 0; i < depth; i++) {
			
			
			
		}
		// TODO Auto-generated method stub
		
	}


	private static final Closure4[] FACTORIES = {
		new Closure4(){
			@Override
			public Object run() {
				return new ComplexHolder0();
			}
		},
		new Closure4(){
			@Override
			public Object run() {
				return new ComplexHolder1();
			}
		},
		new Closure4(){
			@Override
			public Object run() {
				return new ComplexHolder2();
			}
		},
		new Closure4(){
			@Override
			public Object run() {
				return new ComplexHolder3();
			}
		},
		new Closure4(){
			@Override
			public Object run() {
				return new ComplexHolder4();
			}
		}
	};


}
