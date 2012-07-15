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


package org.polepos.enhance;

import java.net.*;

import com.versant.jpa.enhancer.main.*;

public class VodJpaEnhance {
	
	public static void main(String[] args) {
		
        args = new String[]{
                "-b",
                "bin",
                "-d",
                "bin",
                "-l"
        };
        try {
            EnhancerMain.main(args);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        
        // The following is the Ant task way. Specifying a persistence unit doesn't seem to work.
		
//		Project project = new Project();
//		EnhancerTask enhancerTask = new EnhancerTask();
//		enhancerTask.setProject(project);
//		enhancerTask.setInheritsClasspath(true);
//		// enhancerTask.setPersistenceUnit("vodjpa");
//		File destdir = new File("bin");
//		System.out.println(destdir.getAbsolutePath());
//		enhancerTask.setDestdir(destdir);
//		FileSet fs = new FileSet();
//		fs.setProject(project);
//		fs.setDir(destdir);
//		fs.createInclude().setName("**/*.class");
//		fs.createInclude().setName("**/*.xml");
//		enhancerTask.addFileSet(fs);
//		enhancerTask.execute();
	}

}
