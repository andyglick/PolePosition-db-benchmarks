

package org.polepos.teams.jpa;

import org.polepos.circuits.arraylists.*;
import org.polepos.teams.jpa.data.JpaListHolder;


/**
 * @author Christian Ernst
 */
public class ArrayListsJpa extends JpaDriver implements ArrayListsDriver {
    
	 public void write() {
	        
	        int count = 1000;
	        int elements = setup().getObjectSize();
	        
	        begin();
	        for (int i = 1; i<= count; i++) {
	            store(JpaListHolder.generate(i, elements));
	        }
	        commit();
	    }

	    public void read() {
	        readExtent(JpaListHolder.class);
	    }

}
