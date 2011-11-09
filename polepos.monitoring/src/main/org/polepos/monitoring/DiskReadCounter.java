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

import org.hyperic.sigar.DiskUsage;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarProxy;
import org.polepos.util.NoArgFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.polepos.util.JavaLangUtils.rethrow;

/**
 * @author roman.stoffel@gamlor.info
 * @since 16.09.11
 */
public final class DiskReadCounter implements Sampler{
    final static String SAMPLER_NAME = "Disk-Reads on ";
    private final String machineName;
    private final List<DiskInfo> allDisks = new ArrayList<DiskInfo>();

    DiskReadCounter(Map<String,NoArgFunction<DiskUsage>> disks){
        this(disks,"client-machine");
    }
    DiskReadCounter(Map<String,NoArgFunction<DiskUsage>> disks,String machineName) {
        this.machineName = machineName;
        for (Map.Entry<String, NoArgFunction<DiskUsage>> entry : disks.entrySet()) {
            this.allDisks.add(new DiskInfo(entry.getKey(), entry.getValue()));
        }
    }

    public static Sampler create(SigarProxy sigar,String machineName) {
        try {
            Map<String,NoArgFunction<DiskUsage>> disks = new HashMap<String, NoArgFunction<DiskUsage>>();
            for (Object dn : sigar.getFileSystemMap().keySet()) {
                String diskName = (String)dn;

                final NoArgFunction<DiskUsage> disk = tryGetDiskOrReturnNull(sigar, diskName);
                if(null!=disk)  {
                    disks.put(diskName, disk);
                }
            }
            return new DiskReadCounter(disks,machineName);
        } catch (SigarException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static NoArgFunction<DiskUsage> tryGetDiskOrReturnNull(final SigarProxy sigar,final String diskName) {

        try {
            sigar.getDiskUsage(diskName);
        } catch (SigarException e) {
            return null;
        }
            return new NoArgFunction<DiskUsage>() {
                @Override
                public DiskUsage invoke() {
                    try {
                        return sigar.getDiskUsage(diskName);
                    } catch (SigarException e) {
                        throw rethrow(e);
                    }
                }
            };
    }

    @Override
    public MonitoringResult collectResult() {
        final DiskInfo currentRead = currentMostReadDisk();
        return MonitoringResult.create(MonitoringType.create(SAMPLER_NAME+currentRead.getName()+ machineName,"reads","kb"), (double)currentRead.currentRead() );
    }

    private DiskInfo currentMostReadDisk() {
        DiskInfo mostReads = allDisks.get(0);
        for (DiskInfo disk : allDisks) {
            if(disk.currentRead()>mostReads.currentRead()){
                mostReads = disk;
            }
        }
        return mostReads;
    }

    private static class DiskInfo{
        private final long initialRead;
        private final String name;
        private final NoArgFunction<DiskUsage> disk;

        public DiskInfo(String name,NoArgFunction<DiskUsage> disk) {
            this.name = name;
            this.disk = disk;
            initialRead = disk.invoke().getReads();
        }



        public long currentRead() {
            return disk.invoke().getReads()-initialRead;
        }

        public String getName() {
            return name;
        }
    }
}
