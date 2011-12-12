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


package org.polepos.teams;

import java.io.*;
import java.net.*;

import javax.jms.IllegalStateException;

import org.polepos.framework.*;

public class TeamFactory {
	
	private static String _workspace;
	
	protected static Team newTeam(String jar, String folder, String teamName, String...prefixes) {
		try {
			File file = new File(folder, jar);
			if(! file.exists()){
				throw new IllegalStateException(jar + " not found");
			}
			URL jarUrl = file.toURL();
			
            File[] projectPaths = projectPaths();
            URL[] urls = new URL[projectPaths.length + 1];
            for (int projectIdx = 0; projectIdx < projectPaths.length; projectIdx++) {
				urls[projectIdx] = new File(projectPaths[projectIdx], "bin").toURI().toURL();
			}
            urls[urls.length - 1] = jarUrl;
            
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
			ClassLoader loader=new VersionClassLoader(urls, prefixes, contextClassLoader);
			try{
				Thread.currentThread().setContextClassLoader(loader);
				Class<?> clazz = loader.loadClass(teamName);
				Team team = (Team) clazz.newInstance();
				return team;
			}finally{
				Thread.currentThread().setContextClassLoader(contextClassLoader);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
    protected static File[] libPaths() {
    	File[] projectPaths = projectPaths();
    	File[] libPaths = new File[projectPaths.length];
    	for (int pathIdx = 0; pathIdx < projectPaths.length; pathIdx++) {
			libPaths[pathIdx] = new File(projectPaths[pathIdx], "lib");
		}
    	return libPaths;
    }

    protected  static File[] projectPaths() {
    	return new File[]{ workspaceFile("polepos")};
    }
    
    protected static File workspaceFile(String path) {
    	return new File(workspace(), path);
    }

    protected static void guessWorkSpace() {
        File absoluteFile = new File(new File("lib").getAbsolutePath());
        _workspace = absoluteFile.getParentFile().getParentFile().getAbsolutePath();
        System.out.println("Guessed workspace:\n" + _workspace + "\n");
    }
    
    protected static String workspace() {
    	if(_workspace == null) {
    	    _workspace = System.getProperty("polepos.dir");
    	    if (_workspace == null) {
    	        guessWorkSpace();
    	    }
    	}
    	return _workspace;
    }



}
