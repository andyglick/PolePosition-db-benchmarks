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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * @author roman.stoffel@gamlor.info
 * @since 16.09.11
 */
public final class Samplers {
    private static final int INTERVAL = 25;
    private final SigarProxy sigar;
    private final Collection<String> listOfUsedSamplers;
    private final String machineName;

    private Samplers(Collection<String> listOfUsedSamplers,String machineName) {
        this.listOfUsedSamplers = listOfUsedSamplers;
        this.machineName = machineName;
        this.sigar = SigarProxyCache.newInstance(new Sigar(), INTERVAL);
    }

    public static Samplers create(Collection<String> listOfUsedSamplers,String machineName){
        return new Samplers(new ArrayList<String>(listOfUsedSamplers),machineName);
    }

    public Sampler cpuLoad() {
        return CPULoadCollector.create(sigar,machineName);
    }

    public Collection<? extends Sampler> samplers(){
        return filter(asList(cpuLoad(), networkReads(),networkSends(), diskReads()), listOfUsedSamplers);
    }
    public static Collection<String> allSamplerNames(){
        return asList(
                CPULoadCollector.class.getSimpleName(),
                NetworkCollector.ReceiveNetworkCollector.class.getSimpleName(),
                NetworkCollector.SendNetworkCollector.class.getSimpleName(),
                DiskReadCounter.class.getSimpleName()
                );
    }


    public Sampler diskReads() {
        return DiskReadCounter.create(sigar,machineName);
    }

    public Sampler networkReads() {
        return NetworkCollector.createReceiveCollector(sigar,machineName);
    }
    public Sampler networkSends() {
        return NetworkCollector.createSendCollector(sigar,machineName);
    }

    private Collection<? extends Sampler> filter(List<Sampler> samplers,
                                                 Collection<String> listOfUsedSamplers) {
        ArrayList<Sampler> result = new ArrayList<Sampler>();
        for (Sampler sampler : samplers) {
            if(listOfUsedSamplers.contains(sampler.getClass().getSimpleName())){
                result.add(sampler);
            }
        }
        return result;
    }
}
