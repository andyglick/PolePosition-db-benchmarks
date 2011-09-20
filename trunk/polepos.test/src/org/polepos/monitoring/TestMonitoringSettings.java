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

import org.junit.Test;
import org.polepos.framework.PropertiesHandler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author roman.stoffel@gamlor.info
 * @since 20.09.11
 */
public class TestMonitoringSettings {

    @Test
    public void isEnabled(){
        PropertiesHandler handler = monitoringCfg(true);
        MonitoringSettings toTest = MonitoringSettings.create(handler);
        assertTrue(toTest.isEnabled());
    }
    @Test
    public void isDisabled(){
        PropertiesHandler handler = monitoringCfg(false);
        MonitoringSettings toTest = MonitoringSettings.create(handler);
        assertFalse(toTest.isEnabled());
    }
    @Test
    public void samplingRate(){
        PropertiesHandler handler = samplingRate("100");
        MonitoringSettings toTest = MonitoringSettings.create(handler);
        assertEquals(100L,toTest.getSamplingRateInMillisec());
    }
    @Test
    public void defaultSampling(){
        PropertiesHandler handler = samplingRate(null);
        MonitoringSettings toTest = MonitoringSettings.create(handler);
        assertEquals(BackgroundSampling.INTERVAL_IN_MILLISEC,toTest.getSamplingRateInMillisec());
    }

    private PropertiesHandler monitoringCfg(boolean isMonitoringEnabled) {
        PropertiesHandler handler = mock(PropertiesHandler.class);
        when(handler.getBoolean(MonitoringSettings.MONITORING_IS_ENABLED)).thenReturn(isMonitoringEnabled);
        return handler;
    }
    private PropertiesHandler samplingRate(String samplingRate) {
        PropertiesHandler handler = mock(PropertiesHandler.class);
        when(handler.get(MonitoringSettings.MONITORING_SAMPLING_RATE)).thenReturn(samplingRate);
        return handler;
    }
}
