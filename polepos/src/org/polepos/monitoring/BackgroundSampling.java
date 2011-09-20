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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author roman.stoffel@gamlor.info
 * @since 14.09.11
 */
final class BackgroundSampling {
    private final Timer timer;
    static final long FIRST_RUN_DELAY_IN_MILLISEC =5;
    static final long INTERVAL_IN_MILLISEC =50;
    private AtomicReference<Collection<MonitoringResult>> lastResult
            = new AtomicReference<Collection<MonitoringResult>>(new ArrayList<MonitoringResult>());
    private final long interval;

    private BackgroundSampling(long interval) {
        this.interval = interval;
        this.timer = new Timer(true);
    }

    public static BackgroundSampling start(Collection<? extends Sampler> samplers){
        return start(samplers,INTERVAL_IN_MILLISEC);
    }

    public static BackgroundSampling start(Collection<? extends Sampler> samplers,
                                           final long intervalInMillisec){
        final BackgroundSampling sampling = new BackgroundSampling(intervalInMillisec);
        sampling.schedule(samplers);
        return sampling;
    }

    private void schedule(final Collection<? extends Sampler> samplers) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ArrayList<MonitoringResult> results = new ArrayList<MonitoringResult>();
                for (Sampler sampler : samplers) {
                    results.add(sampler.sample());
                }
                lastResult.set(results);
            }
        },FIRST_RUN_DELAY_IN_MILLISEC,interval );
    }


    public Collection<MonitoringResult> stopAndCollectResults() {
        timer.cancel();
        return lastResult.get();
    }
}
