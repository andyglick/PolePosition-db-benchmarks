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

import org.polepos.framework.*;

/**
 * @author Herkules
 */
public class JdbcCar extends Car {

	protected Connection _connection;
	
	protected Statement _statement;

	private final String _dbType;
	
	private String _name;
	
	private boolean _executeBatch;

	private final static Map<Class, String> _colTypesMap = new HashMap<Class, String>();

	static {
		_colTypesMap.put(String.class, "VARCHAR(100)");
		_colTypesMap.put(Integer.TYPE, "INTEGER");
	}

	public JdbcCar(Team team, String dbtype) throws CarMotorFailureException {
		super(team);
		_dbType = dbtype;
		JdbcSettings jdbcSettings = Jdbc.settings();
		_website = jdbcSettings.getWebsite(dbtype);
		_description = jdbcSettings.getDescription(dbtype);
		_name = jdbcSettings.getName(dbtype);
		_executeBatch = jdbcSettings.getExecuteBatch(dbtype);

		try {
			Class.forName(jdbcSettings.getDriverClass(dbtype)).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			throw new CarMotorFailureException();
		}
	}

	public String name() {
		if (_name != null) {
			return _name;
		}
		return _dbType;
	}

	public void openConnection() throws CarMotorFailureException {

		try {
			assert null == _connection : "database has to be closed before opening";
			JdbcSettings jdbcSettings = Jdbc.settings();
			
			Properties props = new Properties();
			String username = jdbcSettings.getUsername(_dbType);
			if(username != null){
				props.put("user", username);
			}
			String password = jdbcSettings.getPassword(_dbType);
			if(password != null){
				props.put("password", password);
			}
			
			// If we don't use this setting, HSQLDB will hold all tables
			// in memory completely, which is not what other engines do.
			props.put("hsqldb.default_table_type", "cached");
			
			
			_connection = DriverManager.getConnection(jdbcSettings.getConnectUrl(_dbType), props);
			_connection.setAutoCommit(false);
			
			if("hsqldb".equals(_dbType)){
				hsqlDbWriteDelayToZero(_connection);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new CarMotorFailureException();
		}
	}

	public static void hsqlDbWriteDelayToZero(Connection connection) {
		
		// To be fair to other database engines, commits should
		// be ACID. Especially the "D" (durable) is not satisfied
		// if #sync() of the database file runs in a timer instead
		// of directly after a commit call.
		
		try {
			Statement statement = connection.createStatement();
			statement.executeUpdate("SET WRITE_DELAY 0");
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	

	/**
	 * 
	 */
	public void close() {
		if(_connection == null) {
			return;
		}
		closeStatement();
		commit();
		closeConnection();
	}

	private void closeConnection() {
		closeStatement();
		try {
			_connection.close();
		} catch (SQLException sqlex) {
			sqlex.printStackTrace();
		}
		_connection = null;
	}

	private void closeStatement() {
		if (_statement != null) {
			try {
				_statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				_statement = null;
			}
		}
	}

	/**
	 * Commit changes.
	 */
	public void commit() {
		try {
			_connection.commit();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Declarative statement executor
	 */
	public void executeSQL(String sql) {
		try {
			_statement = _connection.createStatement();
			_statement.execute(sql);
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			closeStatement();
		}
	}
	

	/**
	 * Declarative statement executor
	 */
	public ResultSet executeQuery(String sql) {
		Log.logger.fine(sql);
		ResultSet rs = null;
		try {
			_statement = _connection.createStatement();
			rs = _statement.executeQuery(sql);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return rs;
	}
	
	public void closeQuery(ResultSet rs) {
		closeResultSet(rs);
		closeStatement();
	}

	public void closeResultSet(ResultSet rs) {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Declarative statement executor
	 */
	public ResultSet executeQueryForUpdate(String sql) {
		Log.logger.fine(sql);
		ResultSet rs = null;
		try {
			_statement = _connection.createStatement(ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_UPDATABLE);
			rs = _statement.executeQuery(sql);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return rs;
	}

	public void executeUpdate(String sql) {
		try {
			_statement = _connection.createStatement();
			_statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeStatement();
		}
	}

	public void dropTable(String tablename) {
		
		// TODO: "drop table if exists" is nonstandard.
		// A better approach would be to look in the catalog and
		// to delete a table only if it can be found there.
		
		String sql = "drop table if exists " + tablename;
		
		try {
			_statement = _connection.createStatement();
			_statement.executeUpdate(sql);
			return;
		} catch (SQLException e) {
			System.out.println("SQL dialect not supported: 'drop table if exists'. Trying plain 'drop table'");
		} finally {
			closeStatement();
		}
		
		sql = "drop table " + tablename;
		
		try {
			_statement = _connection.createStatement();
			_statement.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println("Table could not be dropped: " + tablename);
		} finally {
			closeStatement();
		}
		
	}
	
	protected String createTable(){
		return "create table";
	}

	/**
	 * Create a new table, use the first column name as the primary key
	 */
	public void createTable(String tablename, String[] colnames,
			Class[] coltypes) {
		String sql = createTable() + " " + tablename + " (" + colnames[0]
				+ "  INTEGER NOT NULL";

		for (int i = 1; i < colnames.length; i++) {
			sql += ", " + colnames[i] + " " + _colTypesMap.get(coltypes[i]);
		}
		sql += ", PRIMARY KEY(" + colnames[0] + "))";
		executeSQL(sql);
	}

	public void createIndex(String tablename, String colname) {
		// The maximum length for index names is 18 for Derby.
		String sql = "CREATE INDEX X" + tablename + "_" + colname + " ON "
				+ tablename + " (" + colname + ")";
		executeSQL(sql);
	}

	/**
	 * Retrieve a prepared statement.
	 */
	public PreparedStatement prepareStatement(String sql) {
		PreparedStatement stmt = null;
		try {
			stmt = _connection.prepareStatement(sql);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return stmt;
	}

	public boolean executeBatch() {
		return _executeBatch;
	}

}
