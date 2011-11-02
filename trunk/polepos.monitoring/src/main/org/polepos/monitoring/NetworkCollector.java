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
public abstract class NetworkCollector implements Sampler {
    private final SigarProxy sigar;
    private final long initialValue;
    final static long KILO_BYTE = 1024;
    private final List<String> listOfDevices;
    private final MonitoringType type;

    protected NetworkCollector(MonitoringType type, SigarProxy sigar, List<String> listOfDevices) {
        this.sigar = sigar;
        this.initialValue = collectValue(sigar, listOfDevices);
        this.listOfDevices = listOfDevices;
        this.type = type;
    }

    @Override
    public MonitoringResult collectResult() {
        long currentValue = collectValue(sigar, listOfDevices);
        return MonitoringResult.create(type,(double) ((currentValue - initialValue) / KILO_BYTE));
    }

    public static Sampler createReceiveCollector(SigarProxy sigar) {
        List<String> listOfDevices = listOfMonitoredDevices(sigar);
        return new ReceiveNetworkCollector(sigar, listOfDevices);
    }
    public static Sampler createSendCollector(SigarProxy sigar) {
        List<String> listOfDevices = listOfMonitoredDevices(sigar);
        return new SendNetworkCollector(sigar, listOfDevices);
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

    private long collectValue(SigarProxy sigar, List<String> listOfDevices) {
        long value = 0;
        try {
            for (String ni : listOfDevices) {
                final NetInterfaceStat netInterfaceStat = sigar.getNetInterfaceStat(ni);
                final long rxBytes = readData(netInterfaceStat);
                value += rxBytes;
            }
        } catch (SigarException e) {
            throw rethrow(e);
        }
        return value;
    }
    protected abstract long readData(NetInterfaceStat netInterfaceStat);

    static class ReceiveNetworkCollector extends NetworkCollector{

        protected ReceiveNetworkCollector(SigarProxy sigar, List<String> listOfDevices) {
            super(MonitoringType.create("Network received bytes"+MonitoringType.machineNameAppendix(), "kb", "kbyte"),
                    sigar, listOfDevices);
        }

        protected long readData(NetInterfaceStat netInterfaceStat) {
            return netInterfaceStat.getRxBytes();
        }

    }

    static class SendNetworkCollector extends NetworkCollector{

        protected SendNetworkCollector(SigarProxy sigar, List<String> listOfDevices) {
            super(MonitoringType.create("Network sent bytes"+MonitoringType.machineNameAppendix(), "kb", "kbyte"),
                    sigar, listOfDevices);
        }

        protected long readData(NetInterfaceStat netInterfaceStat) {
            return netInterfaceStat.getTxBytes();
        }

    }
}
