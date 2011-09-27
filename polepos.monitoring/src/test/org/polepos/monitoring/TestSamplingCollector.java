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

import org.junit.Test;

import java.util.Collection;

import static java.util.Collections.singleton;
import static junit.framework.Assert.assertEquals;

/**
 * @author roman.stoffel@gamlor.info
 * @since 15.09.11
 */
public class TestSamplingCollector {

    @Test
    public void callsSamplers(){
        CallCountingSampler check = new CallCountingSampler();
        final SamplingCollector sampling = SamplingCollector.start(singleton(check));
        MonitoringTestUtils.waitFor(MonitoringTestUtils.TEST_INTERVAL_IN_MILLISEC * 2);

        sampling.stopAndCollectResults();
        check.assertHasBeenCalled();

    }
    @Test
    public void returnsResult(){
        CallCountingSampler check = new CallCountingSampler();
        final SamplingCollector sampling = SamplingCollector.start(singleton(check));
        MonitoringTestUtils.waitFor(MonitoringTestUtils.TEST_INTERVAL_IN_MILLISEC * 2);

        final Collection<MonitoringResult> results = sampling.stopAndCollectResults();
        assertEquals(1, results.size());
    }


}
