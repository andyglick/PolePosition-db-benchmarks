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

import org.polepos.monitoring.LoadMonitoringResults;
import org.polepos.monitoring.Samplers;
import org.polepos.monitoring.SamplingSession;
import org.polepos.monitoring.SessionFactory;

/**
 * @author roman.stoffel@gamlor.info
 * @since 27.09.11
 */
public final class Monitoring implements MonitoringMXBean {
    private SamplingSession sampling;
    private final Samplers samplers;

    public Monitoring(Samplers samplers) {
        this.samplers = samplers;
    }


    @Override
    public synchronized void start() {
        sampling = SessionFactory.localSession(samplers.samplers());
    }

    @Override
    public synchronized String stop() {
        final LoadMonitoringResults results = LoadMonitoringResults.create(sampling.sampleAndReturnResults());
        return Serialisation.toJSON(results);
    }
}
