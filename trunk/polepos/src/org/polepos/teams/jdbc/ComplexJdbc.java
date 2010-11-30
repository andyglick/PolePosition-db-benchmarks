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
	
	private static final int ROOT_ID = 1; 
	
	private static final String HOLDER_TABLE0 = "complexHolder0";
	
    private static final String[] HOLDER_TABLES = new String[]{
        "complexHolder1",
        "complexHolder2",
        "complexHolder3",
        "complexHolder4",
    };
    
    private static final String CHILDREN_TABLE = "children";
    
    private static final String ARRAY_TABLE = "tarray";
    
    private static final int ID = 1;
    
    private static final int PREVIOUS = 2;
    
    private static final int NAME = 3;
    
    private static final int TYPE = 4;
    
    private static final int INT_FIELD = 2;
    
    private static final int CHILD = 2;
    
    private static final int POS = 3;
    
    public void takeSeatIn(Car car, TurnSetup setup) throws CarMotorFailureException{
        
        super.takeSeatIn(car, setup);
        openConnection();
        
        dropTable(HOLDER_TABLE0);
        dropTable(CHILDREN_TABLE);
        dropTable(ARRAY_TABLE);
        
        createTable( HOLDER_TABLE0, new String[]{ "id", "previous", "name", "type" }, 
                new Class[]{Integer.TYPE, Integer.TYPE, String.class, Integer.TYPE} );
        
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
		final IdGenerator idGenerator = new IdGenerator(ROOT_ID);
		final Stack<Integer> parentIds = new Stack<Integer>();
		final PreparedStatement complexHolder0Stat = prepareStatement("insert into complexHolder0 (id, previous, name, type) values (?,?,?,?)");
		final PreparedStatement[] complexHolderStats = new PreparedStatement[4];
		for (int i = 0; i < complexHolderStats.length; i++) {
			int idx = i + 1;
			String table = "complexHolder" + idx;
			complexHolderStats[i] = prepareStatement("insert into " + table + "(id, i" +  idx + ") values (?,?)"); 
		}
		final PreparedStatement arrayStat = prepareStatement("insert into tarray (parent, child, pos) values (?,?,?)");
		final PreparedStatement childrenStat = prepareStatement("insert into children (parent, child, pos) values (?,?,?)");
		final Map<ComplexHolder0,Integer> ids = new HashMap<ComplexHolder0, Integer>();
		ComplexHolder0 holder = ComplexHolder0.generate(depth(), objectCount());
		// addToCheckSum(holder);
		holder.traverse(new Visitor<ComplexHolder0>() {
			@Override
			public void visit(ComplexHolder0 holder) {
				int id = (int) idGenerator.nextId();
				ids.put(holder, id);
				try {
					int type = 0;
					
					if(holder instanceof ComplexHolder1){
						type=1;
						complexHolderStats[0].setInt(1, id);
						ComplexHolder1 complexHolder1 = (ComplexHolder1) holder;
						complexHolderStats[0].setInt(2, complexHolder1._i1);
						complexHolderStats[0].addBatch();
					}
					
					if(holder instanceof ComplexHolder2){
						type=2;
						complexHolderStats[1].setInt(1, id);
						ComplexHolder2 complexHolder2 = (ComplexHolder2) holder;
						complexHolderStats[1].setInt(2, complexHolder2._i2);
						complexHolderStats[1].addBatch();
					}
					
					if(holder instanceof ComplexHolder3){
						type=3;
						complexHolderStats[2].setInt(1, id);
						ComplexHolder3 complexHolder3 = (ComplexHolder3) holder;
						complexHolderStats[2].setInt(2, complexHolder3._i3);
						complexHolderStats[2].addBatch();
					}
					
					if(holder instanceof ComplexHolder4){
						type =4;
						complexHolderStats[3].setInt(1, id);
						ComplexHolder4 complexHolder4 = (ComplexHolder4) holder;
						complexHolderStats[3].setInt(2, complexHolder4._i4);
						complexHolderStats[3].addBatch();
					}
					
					complexHolder0Stat.setInt(1, id);
					complexHolder0Stat.setInt(2, holder.getPrevious() == null ? 0 : ids.get(holder.getPrevious()));
					complexHolder0Stat.setString(3, holder.getName());
					complexHolder0Stat.setInt(4, type);
					complexHolder0Stat.addBatch();


					
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
		try {
			final PreparedStatement complexHolder0Stat = 
				prepareStatement("select * from complexHolder0 where id=?");
			
			final PreparedStatement[] complexHolderStats = new PreparedStatement[4];
			for (int i = 0; i < complexHolderStats.length; i++) {
				int idx = i + 1;
				String table = "complexHolder" + idx;
				complexHolderStats[i] = prepareStatement("select * from " + table + " where id=?"); 
			}
			final PreparedStatement arrayStat = prepareStatement("select * from " + ARRAY_TABLE + " where parent=? order by pos");
			final PreparedStatement childrenStat = prepareStatement("select * from " + CHILDREN_TABLE + " where parent=? order by pos");

			Map<Integer, ComplexHolder0> read = new HashMap<Integer, ComplexHolder0>();
			ComplexHolder0 holder = readHolder(
					read, 
					complexHolder0Stat, 
					complexHolderStats,
					arrayStat,
					childrenStat,
					ROOT_ID);
			
			addToCheckSum(holder.checkSum());
			
			complexHolder0Stat.close();
			for (int i = 0; i < complexHolderStats.length; i++) {
				complexHolderStats[i].close(); 
			}
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private ComplexHolder0 readHolder(
			Map<Integer, ComplexHolder0> read, 
			final PreparedStatement complexHolder0Stat, 
			PreparedStatement[] complexHolderStats, 
			PreparedStatement arrayStat, 
			PreparedStatement childrenStat, 
			int id) throws SQLException {
		
		ComplexHolder0 existing = read.get(id);
		if(existing != null){
			return existing;
		}
		complexHolder0Stat.setInt(1, id); 
		ResultSet resultSet0 = executeQuery(complexHolder0Stat);
		int type = resultSet0.getInt(TYPE);
		ComplexHolder0 holder = (ComplexHolder0) ComplexHolder0.FACTORIES[type].run();
		read.put(id, holder);
		
		holder.setName(resultSet0.getString(NAME));
		int previousId = resultSet0.getInt(PREVIOUS);
		if(previousId> 0){
			holder.setPrevious(
				readHolder(
						read, 
						complexHolder0Stat,
						complexHolderStats, 
						arrayStat, 
						childrenStat, 
						previousId));
		}
		
		if(holder instanceof ComplexHolder1){
			ComplexHolder1 complexHolder1 = (ComplexHolder1) holder; 
			complexHolderStats[0].setInt(1, id);
			ResultSet resultSet1 = executeQuery(complexHolderStats[0]);
			complexHolder1._i1 = resultSet1.getInt(INT_FIELD);
			close(resultSet1);
		}
		
		if(holder instanceof ComplexHolder2){
			ComplexHolder2 complexHolder2 = (ComplexHolder2) holder; 
			complexHolderStats[1].setInt(1, id);
			ResultSet resultSet2 = executeQuery(complexHolderStats[1]);
			complexHolder2._i2 = resultSet2.getInt(INT_FIELD);
			close(resultSet2);
		}

		if(holder instanceof ComplexHolder3){
			ComplexHolder3 complexHolder3 = (ComplexHolder3) holder; 
			complexHolderStats[2].setInt(1, id);
			ResultSet resultSet3 = executeQuery(complexHolderStats[2]);
			complexHolder3._i3 = resultSet3.getInt(INT_FIELD);
			close(resultSet3);
		}

		if(holder instanceof ComplexHolder4){
			ComplexHolder4 complexHolder4 = (ComplexHolder4) holder; 
			complexHolderStats[3].setInt(1, id);
			ResultSet resultSet4 = executeQuery(complexHolderStats[3]);
			complexHolder4._i4 = resultSet4.getInt(INT_FIELD);
			close(resultSet4);
		}
		arrayStat.setInt(1, id);
		ResultSet arrayResultSet = arrayStat.executeQuery();
		List<Integer> arrayIds = new ArrayList<Integer>();
		while(arrayResultSet.next()){
			arrayIds.add(arrayResultSet.getInt(CHILD));
		}
		if(! arrayIds.isEmpty()){
			ComplexHolder0[] array = new ComplexHolder0[arrayIds.size()];
			holder.setArray(array);
			int idx = 0;
			for (Integer childId : arrayIds) {
				array[idx++] = readHolder(
						read, 
						complexHolder0Stat,
						complexHolderStats, 
						arrayStat, 
						childrenStat, 
						childId);
			}
		}
		arrayResultSet.close();
		
		childrenStat.setInt(1, id);
		ResultSet childrenResultSet = childrenStat.executeQuery();
		List<Integer> childrenIds = new ArrayList<Integer>();
		while(childrenResultSet.next()){
			childrenIds.add(childrenResultSet.getInt(CHILD));
		}
		if(! childrenIds.isEmpty()){
			List<ComplexHolder0> children = new ArrayList();
			holder.setChildren(children);
			for (Integer childId : childrenIds) {
				children.add(readHolder(
						read, 
						complexHolder0Stat,
						complexHolderStats, 
						arrayStat, 
						childrenStat, 
						childId));
			}
		}
		childrenResultSet.close();
		
		close(resultSet0);
		return holder;
	}

	private void close(ResultSet resultSet) throws SQLException {
		if(resultSet.next()){
			throw new IllegalStateException("More than one complexHolder0 found.");
		}
		resultSet.close();
	}

	private ResultSet executeQuery(final PreparedStatement statement)
			throws SQLException {
		ResultSet resultSet0 = statement.executeQuery();
		if(! resultSet0.next()){
			throw new IllegalStateException("No row found.");
		}
		return resultSet0;
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
