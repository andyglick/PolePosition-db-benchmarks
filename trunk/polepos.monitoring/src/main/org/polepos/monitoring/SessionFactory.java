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

import org.polepos.monitoring.remote.RemoteSamplingSession;
import org.polepos.util.NoArgFunction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author roman.stoffel@gamlor.info
 * @since 28.09.11
 */
public class SessionFactory {

    private final Samplers samplers;
    private final NoArgFunction<SamplingSession> remoteSampleHost;

    SessionFactory(Samplers samplers, NoArgFunction<SamplingSession> remoteSampleHost) {
        this.samplers = samplers;
        this.remoteSampleHost = remoteSampleHost;
    }

    public static SessionFactory create(Samplers samplers, String remoteSampleHost){
        return new SessionFactory(samplers, returnRemoteFactory(remoteSampleHost));
    }

    private static NoArgFunction<SamplingSession> returnRemoteFactory(String remoteSampleHost) {
        if(null==remoteSampleHost || remoteSampleHost.isEmpty()){
            return new NoArgFunction<SamplingSession>() {
                @Override
                public SamplingSession invoke() {
                    return new SamplingSession() {
                        @Override
                        public Collection<MonitoringResult> sampleAndReturnResults() {
                            return Collections.emptyList();
                        }
                    };
                }
            };
        } else {
            return RemoteSamplingSession.newFactory(remoteSampleHost);
        }
    }


    public static SamplingSession localSession(Collection<? extends Sampler> samplers) {
        return new LocalSession(samplers);
    }

    public SamplingSession accordingToConfiguration() {
        final SamplingSession remote = remoteSampleHost.invoke();
        SamplingSession local = localSession(samplers.samplers());
        return compundCollector(local,remote);
    }

    public static SamplingSession compundCollector(final SamplingSession local, final SamplingSession remote) {
        return new SamplingSession() {
            @Override
            public Collection<MonitoringResult> sampleAndReturnResults() {
                final Collection<MonitoringResult> localResults = local.sampleAndReturnResults();
                final Collection<MonitoringResult> remoteResults = remote.sampleAndReturnResults();
                Collection<MonitoringResult> merged = new ArrayList<MonitoringResult>(remoteResults);
                merged.addAll(localResults);
                return merged;
            }
        };
    }

    public static class LocalSession implements SamplingSession {
        private volatile List<Sampler> samplers;

        private LocalSession(Collection<? extends Sampler> samplers) {
            this.samplers = new ArrayList<Sampler>(samplers);
        }


        public List<MonitoringResult> sampleAndReturnResults() {
            ArrayList<MonitoringResult> results = new ArrayList<MonitoringResult>();
            for (Sampler sampler : samplers) {
                results.add(sampler.collectResult());
            }
            return results;
        }
    }
}
