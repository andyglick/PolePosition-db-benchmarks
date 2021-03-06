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

package org.polepos.util;

public final class MathUtil {
    private MathUtil(){}

	public static double toLogedValue(double d) {
		double logMemory = Math.log(d + 2);
		double valForOutput = 1 / logMemory;
		return valForOutput;
	}

    /**
     * Calculating the incremental average of numbers.
     * @param currentAverage the last calculated average
     * @param numberOfEntries the numbers of entries, including then valueOfEntryToAdd
     * @param valueOfEntryToAdd the new value which is added the the average.
     * @return
     */
    public static double incrementalAverage(double currentAverage, int numberOfEntries, double valueOfEntryToAdd) {
        return currentAverage / numberOfEntries *(numberOfEntries-1) + (valueOfEntryToAdd /numberOfEntries);
    }
}
