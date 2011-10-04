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

import junit.framework.Assert;
import org.hyperic.sigar.DiskUsage;
import org.hyperic.sigar.FileSystemMap;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarProxy;
import org.junit.Test;
import org.polepos.util.NoArgFunction;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author roman.stoffel@gamlor.info
 * @since 16.09.11
 */
public class TestDiskReadCounter {


    private static final double DELTA = 0.001;
    private static final String DISK_ONE = "/dsk1/";

    @Test
    public void returnsDifferenceSinceStart(){
        DiskUsage sigarMock = mock(DiskUsage.class);
        when(sigarMock.getReads()).thenReturn(1L,5L);

        final DiskReadCounter toTest = new DiskReadCounter(map("C:\\", sigarMock));
        final Double diskReads = toTest.collectResult().getValue();

        Assert.assertEquals(4.0,diskReads, DELTA);
    }


    @Test
    public void returnsLatestNumber(){
        DiskUsage sigarMock = mock(DiskUsage.class);
        when(sigarMock.getReads()).thenReturn(2L,5L,20L);

        final DiskReadCounter toTest = new DiskReadCounter(map("C:\\", sigarMock));
        toTest.collectResult();
        final Double diskReads = toTest.collectResult().getValue();

        Assert.assertEquals(18.0,diskReads, DELTA);
    }
    @Test
    public void diskName(){
        DiskUsage sigarMock = diskWithReads(1L,5).invoke();

        final DiskReadCounter toTest = new DiskReadCounter(map("C:\\",sigarMock));
        final String name = toTest.collectResult().getType().getName();

        Assert.assertTrue(name.startsWith(DiskReadCounter.SAMPLER_NAME+"C:\\"));
    }

    @Test
    public void returnsMostReadDisk(){
        Map<String,NoArgFunction<DiskUsage>> disks = new HashMap<String, NoArgFunction<DiskUsage>>();
        disks.put("/dsk1/", diskWithReads(0,6));
        disks.put("/dsk2/", diskWithReads(0,100));
        disks.put("/dsk3/", diskWithReads(0,3));

        final DiskReadCounter toTest = new DiskReadCounter(disks);
        final Double diskReads = toTest.collectResult().getValue();
        final String name = toTest.collectResult().getType().getName();

        Assert.assertEquals(100.0,diskReads, DELTA);
        Assert.assertTrue(name.startsWith(DiskReadCounter.SAMPLER_NAME+"/dsk2/"));
    }
    @Test
    public void createsInstance() throws SigarException {
        DiskUsage disk = mock(DiskUsage.class);
        when(disk.getReads()).thenReturn(0L, 6L);

        SigarProxy sigar = mock(SigarProxy.class);
        FileSystemMap fileSys = mock(FileSystemMap.class);
        when(fileSys.keySet()).thenReturn(Collections.singleton(DISK_ONE));
        when(sigar.getFileSystemMap()).thenReturn(fileSys);
        when(sigar.getDiskUsage(DISK_ONE)).thenReturn(disk);


        final Sampler toTest = DiskReadCounter.create(sigar);
        final Double diskReads = toTest.collectResult().getValue();
        final String name = toTest.collectResult().getType().getName();

        Assert.assertEquals(6.0,diskReads, DELTA);
        Assert.assertTrue(name.startsWith(DiskReadCounter.SAMPLER_NAME+DISK_ONE));
    }
    private Map<String,  NoArgFunction<DiskUsage>> map(String diskName,final DiskUsage sigarMock) {
        Map<String, NoArgFunction<DiskUsage>> disks = new HashMap<String,  NoArgFunction<DiskUsage>>();
        disks.put(diskName, new NoArgFunction<DiskUsage>() {
            @Override
            public DiskUsage invoke() {
                return sigarMock;
            }
        });
        return disks;
    }

    private NoArgFunction<DiskUsage> diskWithReads(long initalReads,long secondRead) {
        final DiskUsage sigarMock = mock(DiskUsage.class);
        when(sigarMock.getReads()).thenReturn(initalReads,secondRead);
        return new NoArgFunction<DiskUsage>() {
            @Override
            public DiskUsage invoke() {
                return sigarMock;
            }
        };
    }

}
