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

import com.sun.management.OperatingSystemMXBean;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.management.ManagementFactory;

import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author roman.stoffel@gamlor.info
 * @since 14.09.11
 */
public class TestCPULoadCollector {

    private CPULoadCollector toTest;

    @Before
    public void setup(){
        this.toTest = new CPULoadCollector((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean());
    }

    @Test
    public void returnsNumberBiggerThanZero(){
        double number = (Double)toTest.sample().getValue();
        assertNotNull(number);
        Assert.assertTrue(number>0.0);
    }
    @Test
    public void buildsAverage(){
        OperatingSystemMXBean mxBeanMock = mock(OperatingSystemMXBean.class);
        when(mxBeanMock.getSystemCpuLoad()).thenReturn(0.5)
                .thenReturn(0.75).thenReturn(1.0);

        toTest = new CPULoadCollector(mxBeanMock);

        toTest.sample();
        toTest.sample();
        final Result result = toTest.sample();
        Assert.assertEquals(0.75,(Double )result.getValue(),0.001);
    }
}
