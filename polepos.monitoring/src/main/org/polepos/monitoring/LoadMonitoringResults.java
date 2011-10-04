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

import org.polepos.util.MathUtil;

import java.util.*;

/**
 * @author roman.stoffel@gamlor.info
 * @since 14.09.11
 */
public final class LoadMonitoringResults implements Iterable<MonitoringResult>{
    private final Map<MonitoringType,MonitoringResult> results;

    private LoadMonitoringResults(Map<MonitoringType,MonitoringResult> results) {
        this.results = results;
    }

    public static LoadMonitoringResults create(Collection<MonitoringResult> results){
        Map<MonitoringType,MonitoringResult> map = new HashMap<MonitoringType, MonitoringResult>();
        for (MonitoringResult result : results) {
            map.put(result.getType(),result);
        }
        return new LoadMonitoringResults(map);
    }

    @Override
    public Iterator<MonitoringResult> iterator() {
        return results.values().iterator();
    }

    public MonitoringResult byType(MonitoringType name){
        final MonitoringResult returnValue = tryGetType(name);
        if(null==returnValue){
            throw new IllegalArgumentException("Couldn't find entry for "+name);
        }
        return returnValue;
    }

    public MonitoringResult tryGetType(MonitoringType name) {
        return results.get(name);
    }

    public Collection<MonitoringResult> asCollection(){
        return new ArrayList<MonitoringResult>(results.values());
    }

    @Override
    public String toString() {
        return "LoadMonitoringResults{" +
                "results=" + results +
                '}';
    }

    public static LoadMonitoringResults sumUp(Iterable<LoadMonitoringResults> resultsCollections){
        Map<MonitoringType,MonitoringResult> sumMpa = new HashMap<MonitoringType, MonitoringResult>();
        int count = 0;
        for (LoadMonitoringResults resultCollection : resultsCollections) {
            count++;
            for (Map.Entry<MonitoringType, MonitoringResult> resultEntry : resultCollection.results.entrySet()) {
                if(!sumMpa.containsKey(resultEntry.getKey())) {
                    sumMpa.put(resultEntry.getKey(),resultEntry.getValue());
                } else{
                    double oldResult = sumMpa.get(resultEntry.getKey()).getValue();
                    double average = MathUtil.incrementalAverage(oldResult,count, resultEntry.getValue().getValue());
                    sumMpa.put(resultEntry.getKey(), MonitoringResult.create(resultEntry.getKey(), average));
                }
            }
        }
        return LoadMonitoringResults.create(sumMpa.values());
    }
}
