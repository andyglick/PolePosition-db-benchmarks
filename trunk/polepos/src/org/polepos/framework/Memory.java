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


package org.polepos.framework;


public class Memory {
    
    public static long used(){
        return calculateUsedMemory();
    }
    
    private static long calculateUsedMemory(){
        Runtime rt = Runtime.getRuntime();
        while(true){
            long memory1 = rt.freeMemory();    
            System.gc();
            System.runFinalization();
            System.gc();
            System.runFinalization();
            long memory2 = rt.freeMemory();
            if(memory2 >= memory1){
                break;
            }
        };
        return rt.totalMemory() - rt.freeMemory();
    }

}
