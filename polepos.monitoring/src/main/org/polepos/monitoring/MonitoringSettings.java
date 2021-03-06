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

import java.util.*;

import static java.util.Collections.unmodifiableCollection;

/**
 * @author roman.stoffel@gamlor.info
 * @since 20.09.11
 */
public final class MonitoringSettings {
    static final String MONITORING_IS_ENABLED = "monitoring";
    static final String SAMPLERS = "samplers";
    public static final String REMOTE = "remote.collect";
    private static final String CONNECTOR = "agent.connector";
    static String MACHINE_NAME = "monitoring.name";
    private final boolean isEnabled;
    private final Collection<String> samplers;
    private final String connectionUrl;
    private final Map<String,String> settings;

    public MonitoringSettings(boolean enabled,
                              String connectionUrl,String[] samplers,Map<String,String> settings) {
        isEnabled = enabled;
        this.connectionUrl = connectionUrl;
        this.samplers = Arrays.asList(samplers);
        this.settings = settings;
    }
    public MonitoringSettings(boolean enabled,
                              String connectionUrl,String[] samplers) {
        this(enabled, connectionUrl, samplers, Collections.<String, String>emptyMap());
    }

    public static MonitoringSettings create(PropertiesHandler properties){
        return new MonitoringSettings(properties.getBoolean(MONITORING_IS_ENABLED),
                properties.get(CONNECTOR),
                properties.getArray(SAMPLERS),
                toLower(properties.asMap()));
    }

    private static Map<String, String> toLower(Map<String, String> values) {
        Map<String, String> result = new HashMap<String, String>();
        for (Map.Entry<String, String> stringStringEntry : values.entrySet()) {
            result.put(stringStringEntry.getKey().toLowerCase(),
                    stringStringEntry.getValue());
        }
        return result;
    }

    public static MonitoringSettings readFromConfig() {
        final PropertiesHandler properties = new PropertiesHandler(Monitoring.SETTINGS_FILE_NAME);
        return create(properties);
    }
    public String getConnectionUrl(){
        return connectionUrl;
    }

    public boolean isEnabled(){
        return isEnabled;
    }

    public Collection<String> getSamplers() {
        return unmodifiableCollection(samplers);
    }

    public String machineName() {
        String name = settings.get(MACHINE_NAME);
        if(null==name){
            return "?unknown-machine?";
        }
        return name;
    }


    public String remoteHostForTeam(String teamName) {
        String host = settings.get((REMOTE + "." + teamName).toLowerCase());
        if(null==host){
            host = settings.get(REMOTE.toLowerCase());
        }
        if(null==host){
            host = "";
        }
        return host;
    }
}
