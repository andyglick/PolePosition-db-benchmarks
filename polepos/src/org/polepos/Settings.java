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

package org.polepos;


public class Settings {
    
    private static boolean DEBUG = false;
    
    private static boolean CONCURRENCY = true;
    
    public static boolean LOGARITHMIC = false;
    
    public static final String CIRCUIT = isDebug() ? "settings/DebugCircuits.properties" : "settings/Circuits.properties" ;
    
    public static final String JDBC = "settings/Jdbc.properties";
    
    public static final String JDO = "settings/Jdo.properties";
    
    public static boolean isDebug(){
		String debug = System.getProperty("polepos.debug");
		if(debug != null){
			return Boolean.parseBoolean(debug);
		}
        String className = Settings.class.getName() ;
        if(DEBUG){
            System.out.println(className + ".DEBUG is set to true.\n");
        }
		return DEBUG;
    }
	
	public static boolean isConcurrency(){
		String concurrency = System.getProperty("polepos.concurrency");
		if(concurrency != null){
			return Boolean.parseBoolean(concurrency);
		}
		return CONCURRENCY;
	}

}
