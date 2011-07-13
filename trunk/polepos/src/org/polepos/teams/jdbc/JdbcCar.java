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

public class JdbcCar extends Car {

	public final String _dbType;
	
	private String _name;
	
	public static final Map<Class, String> colTypesMap = new HashMap<Class, String>();

	static {
		colTypesMap.put(String.class, "VARCHAR(100)");
		colTypesMap.put(Integer.TYPE, "INTEGER");
	}

	public JdbcCar(Team team, String dbtype, String color) {
		super(team, color);
		_dbType = dbtype;
		JdbcSettings jdbcSettings = Jdbc.settings();
		_website = jdbcSettings.getWebsite(dbtype);
		_description = jdbcSettings.getDescription(dbtype);
		try {
        	String driverName = jdbcSettings.getDriverClass(dbtype);
        	if(driverName != null && !driverName.equals("")){
        		Class.forName(driverName).newInstance();
        	}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		_name = jdbcSettings.getName(dbtype) + "-" + getVersion(dbtype);
	}
	
	public Connection openConnection() {
		try {
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
			
			String connectUrl = jdbcSettings.getConnectUrl(_dbType);
			Connection connection = DriverManager.getConnection(connectUrl, props);
			connection.setAutoCommit(false);
			return connection;
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	private String getVersion(String dbtype){
		
	    try {
			Connection conn = openConnection();
			DatabaseMetaData meta = conn.getMetaData();
			
			int majorVersion = 0;
			int minorVersion = 0;
			try {
			  majorVersion = meta.getDatabaseMajorVersion();
			} catch (Exception e) {
				System.out.println("major Version: unsupported feature");
			}

			try {
			  minorVersion = meta.getDatabaseMinorVersion();
			} catch (Exception e) {
				System.out.println("minorVersion unsupported feature");
			}

			String productName = meta.getDatabaseProductName();
			String productVersion = meta.getDatabaseProductVersion();
//			System.out.println("productName" + productName);
//			System.out.println("productVersion" + productVersion);
			conn.close();
			
// The following produces earlier versions than we use:			
			
//			String version = "" + majorVersion + "." + minorVersion;
//			return version;
			
// Remove the part after the dash from Derby and MySQL
			int pos = productVersion.indexOf("-");
			String version = pos > 0 ? productVersion.substring(0, pos -1) : productVersion;
			System.out.println("Detected: " + productName + " " + version);
			return version;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Jdbc.settings().getVersion(dbtype);
	}


	public String name() {
		if (_name != null) {
			return _name;
		}
		return _dbType;
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

}
