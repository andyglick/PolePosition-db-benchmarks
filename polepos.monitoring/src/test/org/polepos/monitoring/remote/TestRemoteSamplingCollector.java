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
import org.polepos.monitoring.MonitoringResult;
import org.polepos.monitoring.Samplers;
import org.polepos.monitoring.SamplingSession;
import org.polepos.util.NoArgAction;
import org.polepos.util.NoArgFunction;

import java.util.Collection;

import static org.polepos.monitoring.remote.ServerForTests.newUrl;
import static org.polepos.monitoring.remote.ServerForTests.withRunningServer;
import static org.polepos.util.JavaLangUtils.rethrow;

/**
 * @author roman.stoffel@gamlor.info
 * @since 27.09.11
 */
public class TestRemoteSamplingCollector {

    @Test
    public void expectResult() {
        withRunningServer(new NoArgAction() {
            @Override
            public void invoke() {
                SamplingSession remote = RemoteSamplingRepository.remote(
                        new Monitoring(Samplers.create(Samplers.allSamplerNames(),"client-machine")));
                final Collection<MonitoringResult> results = remote.sampleAndReturnResults();

                Assert.assertTrue(results.size() != 0);
            }
        });
    }

    @Test
    public void worksRemotely() throws Exception {
        final String url = newUrl();
        withRunningServer(url,new NoArgAction() {
            @Override
            public void invoke() {
                try {
                    SamplingSession remote = RemoteSamplingRepository.newFactory().invoke(url);
                    final Collection<MonitoringResult> results = remote.sampleAndReturnResults();

                    Assert.assertTrue(results.size() != 0);

                } catch (Exception e) {
                    throw rethrow(e);
                }
            }
        });
    }
    @Test
    public void returnsSameInstanceForSameHost() throws Exception {
        final String url = newUrl();
        withRunningServer(url,new NoArgAction() {
            @Override
            public void invoke() {
                try {
                    final RemoteSamplingRepository factory = new RemoteSamplingRepository();
                    NoArgFunction<SamplingSession> remote1 = factory.forHost(url);
                    NoArgFunction<SamplingSession> remote2 = factory.forHost(url);

                    Assert.assertSame(remote1,remote2);
                } catch (Exception e) {
                    throw rethrow(e);
                }
            }
        });
    }
}
