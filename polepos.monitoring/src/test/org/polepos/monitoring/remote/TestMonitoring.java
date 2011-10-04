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

package org.polepos.monitoring.remote;

import org.junit.Assert;
import org.junit.Test;
import org.polepos.monitoring.LoadMonitoringResults;
import org.polepos.monitoring.Samplers;

import static org.polepos.monitoring.remote.Serialisation.fromJSON;

/**
 * @author roman.stoffel@gamlor.info
 * @since 27.09.11
 */
public class TestMonitoring {

    @Test
    public void canStartStop() {
        Monitoring toTest = newTestInstance();

        toTest.start();
        LoadMonitoringResults result = fromJSON(toTest.stop());
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.iterator().hasNext());
    }

    private Monitoring newTestInstance() {
        final Samplers samplers = Samplers.create(Samplers.allSamplerNames());
        return new Monitoring(samplers);
    }

    @Test
    public void doNotThrowOnStartingTwice() {
        Monitoring toTest = newTestInstance();

        toTest.start();
        toTest.start();
    }

    @Test
    public void restartingWorks() {
        Monitoring toTest = newTestInstance();

        toTest.start();
        toTest.stop();
        toTest.start();
        String result = toTest.stop();
        Assert.assertNotNull(result);
    }

}
