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

import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarProxy;

import static org.polepos.util.JavaLangUtils.rethrow;

/**
 * @author roman.stoffel@gamlor.info
 * @since 14.09.11
 */
public final class CPULoadCollector implements Sampler {
    private final SigarProxy systemInfo;
    private long initialLoad = 0;

    final MonitoringType type;

    CPULoadCollector(SigarProxy systemInfo,String name, long initialLoad) {
        this.systemInfo = systemInfo;
        this.initialLoad = initialLoad;
        this.type = MonitoringType.create("CPU Time on "+ name, "ms CPU","ms");
    }

    @Override
    public MonitoringResult collectResult() {
        final Cpu cpu = getCPU(systemInfo);
        long load = usedTime(cpu)-initialLoad;
        return MonitoringResult.create(type, load);
    }


    public static Sampler create(SigarProxy systemInfo, String machineName) {
            final Cpu cpu = getCPU(systemInfo);
            return new CPULoadCollector(systemInfo,machineName, usedTime(cpu));
    }
    private static Cpu getCPU(SigarProxy systemInfo) {
        try {
            return systemInfo.getCpu();
        } catch (SigarException e) {
            throw rethrow(e);
        }
    }

    private static long usedTime(Cpu cpu) {
        return cpu.getUser()+cpu.getSys();
    }
}
