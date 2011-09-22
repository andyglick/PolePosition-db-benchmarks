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

import org.polepos.framework.PropertiesHandler;

/**
 * @author roman.stoffel@gamlor.info
 * @since 20.09.11
 */
final class MonitoringSettings {
    static final String MONITORING_IS_ENABLED = "monitoring";
    static final String MONITORING_SAMPLING_RATE = "monitoring.samplingrate";
    private final boolean isEnabled;

    MonitoringSettings(boolean enabled) {
        isEnabled = enabled;
    }

    public static MonitoringSettings create(PropertiesHandler properties){
        return new MonitoringSettings(properties.getBoolean(MONITORING_IS_ENABLED));
    }



    public boolean isEnabled(){
        return isEnabled;
    }
}
