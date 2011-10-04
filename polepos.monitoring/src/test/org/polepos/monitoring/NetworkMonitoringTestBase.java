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
import org.hyperic.sigar.NetInterfaceStat;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarProxy;
import org.junit.Test;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author roman.stoffel@gamlor.info
 * @since 03.10.11
 */
public abstract class NetworkMonitoringTestBase {

    private static final double DELTA = 0.001;

    @Test
    public final void returnsBytes() throws SigarException {
        SigarProxy sigarMock = mock(SigarProxy.class);
        setupMockToReturn(sigarMock,100L,200L,300L,100L,200L,300L,100L,200L,300L);

        final Sampler toTest = createTestInstance(sigarMock);
        final MonitoringResult result = toTest.collectResult();
        Assert.assertEquals(0.0, result.getValue(), DELTA);
    }

    @Test
    public final void sumsUp() throws SigarException {
        SigarProxy sigarMock = mock(SigarProxy.class);
        setupMockToReturn(sigarMock,100L,200L,300L,100L,200L,300L,200L,250L,330L);

        final Sampler toTest = createTestInstance(sigarMock);
        final MonitoringResult result = toTest.collectResult();
        Assert.assertEquals(180.0,result.getValue(), DELTA);
    }

    protected abstract Sampler createTestInstance(SigarProxy sigarMock);

    protected abstract long channel(NetInterfaceStat netStats);

    protected void setupMockToReturn(SigarProxy sigarMock, long firstValueInKByte, long...otherInKByte) throws SigarException {
        when(sigarMock.getNetInterfaceList()).thenReturn(new String[]{"etc0","etc1","etc2"});
        NetInterfaceStat netStats = mock(NetInterfaceStat.class);
        when(sigarMock.getNetInterfaceStat(anyString())).thenReturn(netStats);
        when(channel(netStats)).thenReturn(firstValueInKByte* NetworkCollector.KILO_BYTE,toKiloByte(otherInKByte));
    }

    private Long[] toKiloByte(long[] otherInKByte) {
        Long[] result = new Long[otherInKByte.length];
        for(int i=0;i<otherInKByte.length;i++){
            result[i] = otherInKByte[i]  * NetworkCollector.KILO_BYTE;
        }
        return result;
    }
}
