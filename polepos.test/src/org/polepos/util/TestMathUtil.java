/*
 * This file is part of the PolePosition database benchmark
 * http://www.polepos.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA  02111-1307, USA.MA  02111-1307, USA.
 */

package org.polepos.util;

import junit.framework.Assert;
import org.junit.Test;

/**
 * @author roman.stoffel@gamlor.info
 * @since 15.09.11
 */
public class TestMathUtil {

    private static final double DELTA = 0.0001;

    @Test
    public void oneEntryAverage(){
        double average = MathUtil.incrementalAverage(0.0,1,2.5);
        Assert.assertEquals(2.5,average, DELTA);
    }

    @Test
    public void twoNumbersAverage(){
        double n1 = 0.5;
        double n2 = 1.0;
        double expectedAverage = (n1+n2) / 2;

        double result = MathUtil.incrementalAverage(n1,2,n2);
        Assert.assertEquals(expectedAverage,result, DELTA);
    }
    @Test
    public void threeNumbersAverage(){
        double n1 = 0.5;
        double n2 = 1.0;
        double n3 = 1.5;
        double twoNumbersAverage = (n1+n2) / 2;
        double expectedAverage = (n1+n2+n3) / 3;

        double result = MathUtil.incrementalAverage(twoNumbersAverage,3,n3);
        Assert.assertEquals(expectedAverage,result, DELTA);
    }
}
