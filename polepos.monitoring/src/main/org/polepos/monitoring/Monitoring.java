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

    private static final PropertiesHandler PROPERTIES = new PropertiesHandler("settings/Monitoring.properties");

    public static LoadMonitoringResults monitor(final NoArgAction run) {
        return monitor(new NoArgFunction<Void>() {
            @Override
            public Void invoke() {
                run.invoke();
                return null;
            }
        }).getMonitoring();
    }

    static <T> ResultAndData<T> monitor(final NoArgFunction<T> run) {
        return monitor(readSettings(), createDefaultMonitors(), run);
    }

    static Collection<? extends Sampler> createDefaultMonitors() {
        return Samplers.newInstance().allSamplers();
    }

    static <T> ResultAndData<T> monitor(MonitoringSettings settings, Collection<? extends Sampler> samplers,
                                        final NoArgFunction<T> run) {
        if (settings.isEnabled()) {
            SamplingCollector sampling = SamplingCollector.start(samplers);
            T data = run.invoke();
            final Collection<MonitoringResult> results = sampling.stopAndCollectResults();
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
