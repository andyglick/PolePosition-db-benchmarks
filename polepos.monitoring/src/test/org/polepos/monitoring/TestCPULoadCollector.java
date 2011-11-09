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
import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarProxy;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author roman.stoffel@gamlor.info
 * @since 14.09.11
 */
public class TestCPULoadCollector {

    private Sampler toTest;
    private SigarProxy sigarMock;
    private Cpu cpuMock;
    private String clientMachines = "client-machine";

    @Before
    public void setup() throws SigarException {
        this.sigarMock = mock(SigarProxy.class);
        this.cpuMock = mock(Cpu.class);
        when(sigarMock.getCpu()).thenReturn(cpuMock);
        this.toTest = CPULoadCollector.create(sigarMock,clientMachines);
    }

    @Test
    public void returnsNumberBiggerThanZero(){
        double number = toTest.collectResult().getValue();
        assertNotNull(number);
        Assert.assertTrue(number>=0.0);
    }
    @Test
    public void returnsDiff(){
        when(cpuMock.getTotal()).thenReturn(1000L);
        when(cpuMock.getUser()).thenReturn(200L);
        when(cpuMock.getSys()).thenReturn(100L);
        final MonitoringResult result = toTest.collectResult();
        Assert.assertEquals(300L, result.getValue(),0.001);
    }
    @Test
    public void returnsDiffOverTime(){
        when(cpuMock.getTotal()).thenReturn(1000L,2000L);
        when(cpuMock.getUser()).thenReturn(200L,300L);
        when(cpuMock.getSys()).thenReturn(100L,150L);
        this.toTest = CPULoadCollector.create(sigarMock,clientMachines);
        final MonitoringResult result = toTest.collectResult();
        Assert.assertEquals(150L, result.getValue(),0.001);
    }
}
