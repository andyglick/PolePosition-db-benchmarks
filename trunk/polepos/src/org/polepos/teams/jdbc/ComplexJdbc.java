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


package org.polepos.teams.jdbc;

import java.sql.*;
import java.util.*;

import org.polepos.circuits.complex.*;
import org.polepos.data.*;
import org.polepos.framework.*;

public class ComplexJdbc extends JdbcDriver implements Complex {
	
	private static final String HOLDER_TABLE0 = "complexHolder0";
	
    private static final String[] HOLDER_TABLES = new String[]{
        "complexHolder1",
        "complexHolder2",
        "complexHolder3",
        "complexHolder4",
    };
    
    private static final String CHILDREN_TABLE = "children";
    
    private static final String ARRAY_TABLE = "tarray";
    
    public void takeSeatIn(Car car, TurnSetup setup) throws CarMotorFailureException{
        
        super.takeSeatIn(car, setup);
        openConnection();
        
        dropTable(HOLDER_TABLE0);
        dropTable(CHILDREN_TABLE);
        dropTable(ARRAY_TABLE);
        
        createTable( HOLDER_TABLE0, new String[]{ "id", "previous", "name" }, 
                new Class[]{Integer.TYPE, Integer.TYPE, String.class} );
        
        createTable(CHILDREN_TABLE, 
        		new String[]{ "parent", "child", "pos"}, 
                new Class[] {Integer.TYPE, Integer.TYPE, Integer.TYPE},
                null);
        createIndex( CHILDREN_TABLE, "parent" );
        createIndex( CHILDREN_TABLE, "child" );
        createIndex( CHILDREN_TABLE, "pos" );
        
        createTable(ARRAY_TABLE, 
        		new String[]{ "parent", "child", "pos"}, 
                new Class[] {Integer.TYPE, Integer.TYPE, Integer.TYPE},
                null);
        createIndex( ARRAY_TABLE, "parent" );
        createIndex( ARRAY_TABLE, "child" );
        createIndex( ARRAY_TABLE, "pos" );
        
        int i = 1;
        for(String table : HOLDER_TABLES){
            dropTable( table);
            createTable( table, new String[]{ "id", "i" + i}, 
                        new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE} );
            if(i == 2){
                createIndex( table, "i2" );
            }
            i++;
        }
        close();

    }
    
	@Override
	public void write() {
		final IdGenerator idGenerator = new IdGenerator();
		final Stack<Integer> parentIds = new Stack<Integer>();
		final PreparedStatement complexHolder0Stat = prepareStatement("insert into complexHolder0 (id, previous, name) values (?,?,?)");
		final PreparedStatement[] complexHolderStats = new PreparedStatement[4];
		for (int i = 0; i < complexHolderStats.length; i++) {
			int idx = i + 1;
			String table = "complexHolder" + idx;
			complexHolderStats[i] = prepareStatement("insert into " + table + "(id, i" +  idx + ") values (?,?)"); 
		}
		final PreparedStatement arrayStat = prepareStatement("insert into tarray (parent, child, pos) values (?,?,?)");
		final PreparedStatement childrenStat = prepareStatement("insert into children (parent, child, pos) values (?,?,?)");
		final Map<ComplexHolder0,Integer> ids = new HashMap<ComplexHolder0, Integer>();
		ComplexHolder0 root = ComplexHolder0.generate(depth(), objectCount());
		root.traverse(new Visitor<ComplexHolder0>() {
			@Override
			public void visit(ComplexHolder0 holder) {
				int id = (int) idGenerator.nextId();
				ids.put(holder, id);
				try {
					complexHolder0Stat.setInt(1, id);
					complexHolder0Stat.setInt(2, holder.getPrevious() == null ? 0 : ids.get(holder.getPrevious()));
					complexHolder0Stat.setString(3, holder.getName());
					complexHolder0Stat.addBatch();
					
					if(holder instanceof ComplexHolder1){
						complexHolderStats[0].setInt(1, id);
						ComplexHolder1 complexHolder1 = (ComplexHolder1) holder;
						complexHolderStats[0].setInt(2, complexHolder1._i1);
						complexHolderStats[0].addBatch();
					}
					
					if(holder instanceof ComplexHolder2){
						complexHolderStats[1].setInt(1, id);
						ComplexHolder2 complexHolder2 = (ComplexHolder2) holder;
						complexHolderStats[1].setInt(2, complexHolder2._i2);
						complexHolderStats[1].addBatch();
					}
					
					if(holder instanceof ComplexHolder3){
						complexHolderStats[2].setInt(1, id);
						ComplexHolder3 complexHolder3 = (ComplexHolder3) holder;
						complexHolderStats[2].setInt(2, complexHolder3._i3);
						complexHolderStats[2].addBatch();
					}
					
					if(holder instanceof ComplexHolder4){
						complexHolderStats[3].setInt(1, id);
						ComplexHolder4 complexHolder4 = (ComplexHolder4) holder;
						complexHolderStats[3].setInt(2, complexHolder4._i4);
						complexHolderStats[3].addBatch();
					}

					
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
				parentIds.push(id);
			}
		}, new Visitor<ComplexHolder0>() {
			@Override
			public void visit(ComplexHolder0 holder) {
				int parentId = parentIds.pop();
				List<ComplexHolder0> children = holder.getChildren();
				for (int i = 0; i < children.size(); i++) {
					ComplexHolder0 child = children.get(i);
					int childId = ids.get(child);
					try {
						childrenStat.setInt(1, parentId);
						childrenStat.setInt(2, childId);
						childrenStat.setInt(3, i);
						childrenStat.addBatch();
						
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
				ComplexHolder0[] array = holder.getArray();
				if(array != null){
					for (int i = 0; i < array.length; i++) {
						ComplexHolder0 entry = array[i];
						int childId = ids.get(entry);
						try {
							arrayStat.setInt(1, parentId);
							arrayStat.setInt(2, childId);
							arrayStat.setInt(3, i);
							arrayStat.addBatch();
							
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}
				}
				
			}
		});
		
		try {
			complexHolder0Stat.executeBatch();
			complexHolder0Stat.close();
			for (int i = 0; i < complexHolderStats.length; i++) {
				complexHolderStats[i].executeBatch();
				complexHolderStats[i].close(); 
			}
			childrenStat.executeBatch();
			childrenStat.close();
			arrayStat.executeBatch();
			arrayStat.close();
			

		} catch (Exception e) {
			throw new RuntimeException(e);		
		}
		
	}

	@Override
	public void read() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void query() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete() {
		// TODO Auto-generated method stub
		
	}


}
