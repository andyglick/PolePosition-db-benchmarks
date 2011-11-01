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
import org.polepos.util.NoArgAction;

import java.util.Collection;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static junit.framework.Assert.assertEquals;
import static org.polepos.monitoring.PortUtil.getFreePort;
import static org.polepos.monitoring.remote.ServerForTests.withRunningServer;

/**
 * @author roman.stoffel@gamlor.info
 * @since 15.09.11
 */
public class TestSamplingCollector {

    @Test
    public void callsSamplers() {
        CallCountingSampler check = new CallCountingSampler();
        final SamplingSession sampling = SessionFactory.localSession(singleton(check));
        MonitoringTestUtils.waitFor(MonitoringTestUtils.TEST_INTERVAL_IN_MILLISEC * 2);

        sampling.sampleAndReturnResults();
        check.assertHasBeenCalled();

    }

    @Test
    public void returnsResult() {
        CallCountingSampler check = new CallCountingSampler();
        final SamplingSession sampling = SessionFactory.localSession(singleton(check));
        MonitoringTestUtils.waitFor(MonitoringTestUtils.TEST_INTERVAL_IN_MILLISEC * 2);

        final Collection<MonitoringResult> results = sampling.sampleAndReturnResults();
        assertEquals(1, results.size());
    }

    @Test
    public void onlyLocalSampling() {
        final SessionFactory sessionFactory = SessionFactory.create(Samplers.create(asList(CPULoadCollector.class.getSimpleName())));
        final SamplingSession sampling = sessionFactory.monitoringWithDBHost("");

        final Collection<MonitoringResult> results = sampling.sampleAndReturnResults();
        assertEquals(1, results.size());
    }

    @Test
    public void remoteAndLocalSampling() {
        final int port = getFreePort();
        withRunningServer("service:jmx:jmxmp://0.0.0.0:" + port, new NoArgAction() {
            @Override
            public void invoke() {
                String remote = "service:jmx:jmxmp://localhost:" + port;

                final SessionFactory sessionFactory = SessionFactory.create(
                        Samplers.create(asList(CPULoadCollector.class.getSimpleName())));
                final SamplingSession sampling = sessionFactory.monitoringWithDBHost(remote);

                final Collection<MonitoringResult> results = sampling.sampleAndReturnResults();
                assertEquals(2, results.size());
            }
        });

    }


}
