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
import org.hyperic.sigar.SigarProxy;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * @author roman.stoffel@gamlor.info
 * @since 19.09.11
 */
public class TestNetworkReceiveCollector extends NetworkMonitoringTestBase{


    @Test
    public void describesAsReceived() throws Exception {
        SigarProxy sigarMock = mock(SigarProxy.class);
        setupMockToReturn(sigarMock,100L,200L,300L,100L,200L,300L,100L,200L,300L);

        final Sampler toTest = createTestInstance(sigarMock);
        final MonitoringType type = toTest.collectResult().getType();
        Assert.assertTrue(type.getName().contains("received"));

    }


    protected Sampler createTestInstance(SigarProxy sigarMock) {
        return NetworkCollector.createReceiveCollector(sigarMock,"client-machine");
    }



    protected long channel(NetInterfaceStat netStats) {
        return netStats.getRxBytes();
    }
}
