/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package org.polepos.teams.db4o;

import org.polepos.circuits.montreal.*;
import org.polepos.runner.db4o.*;

import com.db4o.config.*;
import com.db4o.query.*;


public class MontrealDb4o extends Db4oDriver implements MontrealDriver {
	
	public MontrealDb4o(Db4oEngine engine) {
		super(engine);
		
	}

	@Override
	public void configure(Configuration config) {
		
	}

    public void write() {
        
        int count = 1000;
        int elements = setup().getObjectSize();
        
        begin();
        for (int i = 1; i<= count; i++) {
            store(ListHolder.generate(i, elements));
        }
        commit();
    }

    public void read() {
        
        Query q = db().query();
        q.constrain(ListHolder.class);
        doQuery(q);
        
    }

}
