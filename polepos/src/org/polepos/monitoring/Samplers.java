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

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarProxy;
import org.hyperic.sigar.SigarProxyCache;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.Collection;

import static java.util.Arrays.asList;

/**
 * @author roman.stoffel@gamlor.info
 * @since 16.09.11
 */
public final class Samplers {
    private final OperatingSystemMXBean osBean;
    private final SigarProxy sigar;

    private Samplers(){
        this.osBean = ManagementFactory.getOperatingSystemMXBean();
        this.sigar = SigarProxyCache.newInstance(new Sigar(),(int)BackgroundSampling.INTERVAL_IN_MILLISEC/2);

    }

    public static Samplers defaultInstance(){
        return new Samplers();
    }

    public Sampler cpuLoad() {
        return CPULoadCollector.create(osBean);
    }

    public Collection<? extends Sampler> allSamplers(){
        return asList(cpuLoad(),networkReads());
    }

    public Sampler diskReads() {
        return DiskReadCounter.create(sigar);
    }

    public Sampler networkReads() {
        return NetworkReceiveCollector.create(sigar);
    }
}
