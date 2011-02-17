/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package org.polepos.circuits.arraylists;

import org.polepos.framework.*;


public class ArrayLists extends TimedLapsCircuitBase{

    @Override
    public String description() {
        return "writes and reads 1000 ArrayLists";
    }

    @Override
    protected void addLaps() {
        add(new Lap("write"));
        add(new Lap("read"));
    }
    
    @Override
    public Class requiredDriver() {
        return ArrayListsDriver.class;
    }


}
