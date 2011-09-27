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

import java.util.*;

/**
 * @author roman.stoffel@gamlor.info
 * @since 14.09.11
 */
final class SamplingCollector {
    private volatile List<Sampler> samplers;

    private SamplingCollector(Collection<? extends Sampler> samplers) {
        this.samplers = new ArrayList<Sampler>(samplers);
    }

    public static SamplingCollector start(Collection<? extends Sampler> samplers){
        return new SamplingCollector(samplers);
    }

    private List<MonitoringResult> sampleAndReturnResults() {
        ArrayList<MonitoringResult> results = new ArrayList<MonitoringResult>();
        for (Sampler sampler : samplers) {
            results.add(sampler.collectResult());
        }
        return results;
    }


    public Collection<MonitoringResult> stopAndCollectResults() {
        return sampleAndReturnResults();
    }
}
