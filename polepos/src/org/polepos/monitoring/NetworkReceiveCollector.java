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

import org.hyperic.sigar.NetInterfaceStat;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarProxy;

import java.util.ArrayList;
import java.util.List;

import static org.polepos.util.JavaLangUtils.rethrow;

/**
 * @author roman.stoffel@gamlor.info
 * @since 19.09.11
 */
public final class NetworkReceiveCollector implements Sampler {
    private final SigarProxy sigar;
    private final long initialValue;
    final static long KILO_BYTE = 1024;
    private final List<String> listOfDevices;
    private static final MonitoringType TYPE = MonitoringType.create("Network received bytes", "kb", "kbyte", 1);

    NetworkReceiveCollector(SigarProxy sigar, long initialValue, List<String> listOfDevices) {
        this.sigar = sigar;
        this.initialValue = initialValue;
        this.listOfDevices = listOfDevices;
    }

    @Override
    public MonitoringResult collectResult() {
        long currentValue = collectValue(sigar,listOfDevices);
        return MonitoringResult.create(TYPE, (double) ((currentValue - initialValue) / KILO_BYTE));
    }

    public static Sampler create(SigarProxy sigar) {
        List<String> listOfDevices = listOfMonitoredDevices(sigar);
        return new NetworkReceiveCollector(sigar, collectValue(sigar, listOfDevices), listOfDevices);
    }

    private static List<String> listOfMonitoredDevices(SigarProxy sigar)  {
        try {
            return filterOurUnusedDevices(sigar);
        } catch (SigarException e) {
            throw rethrow(e);
        }
    }

    private static List<String> filterOurUnusedDevices(SigarProxy sigar) throws SigarException {
        List<String> deviceNames = new ArrayList<String>();
        for (String device : sigar.getNetInterfaceList()) {
            final NetInterfaceStat starts = sigar.getNetInterfaceStat(device);
            if(inUse(starts)){
                deviceNames.add(device);
            }
        }
        return deviceNames;
    }

    private static boolean inUse(NetInterfaceStat starts) {
        return starts.getRxBytes()!=0||starts.getTxBytes()!=0;
    }

    private static long collectValue(SigarProxy sigar, List<String> listOfDevices) {
        long value = 0;
        try {
            for (String ni : listOfDevices) {
                final NetInterfaceStat netInterfaceStat = sigar.getNetInterfaceStat(ni);
                final long rxBytes = netInterfaceStat.getRxBytes();
                value += rxBytes;
            }
        } catch (SigarException e) {
            throw rethrow(e);
        }
        return value;
    }
}
