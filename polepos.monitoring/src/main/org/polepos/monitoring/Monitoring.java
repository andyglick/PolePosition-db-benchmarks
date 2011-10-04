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
import org.polepos.util.NoArgAction;
import org.polepos.util.NoArgFunction;

import java.util.Collection;
import java.util.Collections;

/**
 * @author roman.stoffel@gamlor.info
 * @since 14.09.11
 */
public final class Monitoring {

    public static final String SETTINGS_FILE_NAME = "settings/Monitoring.properties";
    private static final PropertiesHandler PROPERTIES = new PropertiesHandler(SETTINGS_FILE_NAME);

    private final boolean isEnabled;
    private final SessionFactory sessionFactory;

    Monitoring(boolean enabled, SessionFactory sessionFactory) {
        isEnabled = enabled;
        this.sessionFactory = sessionFactory;
    }

    public static Monitoring createInstance(){
        return createInstance(readSettings());
    }

    public static Monitoring createInstance(MonitoringSettings settings){
        SessionFactory sessionFactory = SessionFactory.create(Samplers.create(settings.getSamplers()),
                settings.getRemote());
        return new Monitoring(settings.isEnabled(),sessionFactory);
    }

    public LoadMonitoringResults monitor(final NoArgAction run) {
        return monitor(new NoArgFunction<Void>() {
            @Override
            public Void invoke() {
                run.invoke();
                return null;
            }
        }).getMonitoring();
    }


    <T> ResultAndData<T> monitor(final NoArgFunction<T> run) {
        if (isEnabled) {
            final SamplingSession sampling = sessionFactory.accordingToConfiguration();
            T data = run.invoke();
            final Collection<MonitoringResult> results = sampling.sampleAndReturnResults();
            return new ResultAndData<T>(LoadMonitoringResults.create(results), data);
        } else{
            T data = run.invoke();
            return new ResultAndData<T>(LoadMonitoringResults.create(Collections.<MonitoringResult>emptyList()),data);
        }
    }

    static MonitoringSettings readSettings() {
        return MonitoringSettings.create(PROPERTIES);
    }

    public static class ResultAndData<T> {
        private final LoadMonitoringResults monitoring;
        private final T data;

        ResultAndData(LoadMonitoringResults monitoring, T data) {
            this.monitoring = monitoring;
            this.data = data;
        }

        public LoadMonitoringResults getMonitoring() {
            return monitoring;
        }

        public T getData() {
            return data;
        }
    }
}
