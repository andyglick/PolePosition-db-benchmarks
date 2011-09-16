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

import org.polepos.util.NoArgAction;
import org.polepos.util.NoArgFunction;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.Collection;
import java.util.Collections;

/**
 * @author roman.stoffel@gamlor.info
 * @since 14.09.11
 */
public final class Monitoring {

    public static LoadMonitoringResults monitor(Collection<? extends Sampler> samplers,NoArgAction run){
        return monitor(samplers,run,BackgroundSampling.INTERVAL_IN_MILLISEC);
    }
    public static LoadMonitoringResults monitor(NoArgAction run){
        return monitor(createDefaultMonitors(),run);
    }
    public static <T> ResultAndData<T> monitor(final NoArgFunction<T> run){
        final MutableReference<T> ref = new MutableReference<T>();
        LoadMonitoringResults results = monitor(createDefaultMonitors(),new NoArgAction() {
            @Override
            public void invoke() {
                ref.set(run.invoke());
            }
        });
        return new ResultAndData<T>(results, ref.get());
    }

    static Collection<? extends Sampler> createDefaultMonitors() {
        final OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        CPULoadCollector cpuLoad = CPULoadCollector.create(osBean);
        return Collections.singleton(cpuLoad);
    }

    static LoadMonitoringResults monitor(Collection<? extends Sampler> samplers,
                           NoArgAction run,
                           long samplingIntervalInMillisec){
        BackgroundSampling sampling = BackgroundSampling.start(samplers,samplingIntervalInMillisec);
        run.invoke();
        final Collection<Result> results = sampling.stopAndCollectResults();
        return LoadMonitoringResults.create(results);
    }

    public static class ResultAndData<T>{
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
