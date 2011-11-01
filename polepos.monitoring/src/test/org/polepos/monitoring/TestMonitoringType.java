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
import org.junit.Test;

/**
 * @author roman.stoffel@gamlor.info
 * @since 01.11.11
 */
public class TestMonitoringType {

    @Test
    public void formatingOfPercent(){
        final MonitoringType monitoringType = MonitoringType.percentUnit("CPU", "label");
        Assert.assertEquals("100.00",monitoringType.formatDisplayNumber(10000));
        Assert.assertEquals("99.00",monitoringType.formatDisplayNumber(9900));
        Assert.assertEquals("99.90",monitoringType.formatDisplayNumber(9990));
        Assert.assertEquals("99.99",monitoringType.formatDisplayNumber(9999));
        Assert.assertEquals("99.09",monitoringType.formatDisplayNumber(9909));
        Assert.assertEquals("99.01",monitoringType.formatDisplayNumber(9901));
        Assert.assertEquals("1.00",monitoringType.formatDisplayNumber(100));
        Assert.assertEquals("1.10",monitoringType.formatDisplayNumber(110));
        Assert.assertEquals("1.90",monitoringType.formatDisplayNumber(190));
        Assert.assertEquals("0.10",monitoringType.formatDisplayNumber(10));
        Assert.assertEquals("0.90",monitoringType.formatDisplayNumber(90));
        Assert.assertEquals("0.09",monitoringType.formatDisplayNumber(9));
        Assert.assertEquals("0.01",monitoringType.formatDisplayNumber(1));
        Assert.assertEquals("0.00",monitoringType.formatDisplayNumber(0));
    }
}
