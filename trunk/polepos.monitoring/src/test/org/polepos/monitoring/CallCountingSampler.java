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

package org.polepos.monitoring;

import java.util.concurrent.atomic.AtomicInteger;

import static junit.framework.Assert.assertTrue;

/**
* @author roman.stoffel@gamlor.info
* @since 15.09.11
*/
class CallCountingSampler implements Sampler {
    private final AtomicInteger wasCalled = new AtomicInteger(0);
    @Override
    public MonitoringResult collectResult() {
        wasCalled.incrementAndGet();
        return MonitoringResult.create(MonitoringType.create("test"), 1.0);
    }

    public void assertHasBeenCalled(){
        wasCalledAtLeast(1);
    }
    public void wasCalledAtLeast(int times){
        assertTrue(wasCalled.intValue() >= times);
    }
}
