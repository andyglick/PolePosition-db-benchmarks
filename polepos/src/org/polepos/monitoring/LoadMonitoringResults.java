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
public final class LoadMonitoringResults implements Iterable<Result>{
    private final Map<String,Result> results;

    private LoadMonitoringResults(Map<String,Result> results) {
        this.results = results;
    }

    public static LoadMonitoringResults create(Collection<Result> results){
        Map<String,Result> map = new HashMap<String, Result>();
        for (Result result : results) {
            map.put(result.getName(),result);
        }
        return new LoadMonitoringResults(map);
    }

    @Override
    public Iterator<Result> iterator() {
        return results.values().iterator();
    }

    public Result byName(String name){
        return results.get(name);
    }

    @Override
    public String toString() {
        return "LoadMonitoringResults{" +
                "results=" + results +
                '}';
    }

    public static LoadMonitoringResults sumUp(Iterable<LoadMonitoringResults> resultsCollections){
        Map<String,Result> sumMpa = new HashMap<String, Result>();
        int count = 0;
        for (LoadMonitoringResults resultCollection : resultsCollections) {
            count++;
            for (Map.Entry<String, Result> resultEntry : resultCollection.results.entrySet()) {
                if(!sumMpa.containsKey(resultEntry.getKey())) {
                    sumMpa.put(resultEntry.getKey(),resultEntry.getValue());
                } else{
                    double oldResult = sumMpa.get(resultEntry.getKey()).getValue();
                    double average = MathUtil.incrementalAverage(oldResult,count, resultEntry.getValue().getValue());
                    sumMpa.put(resultEntry.getKey(),Result.create(resultEntry.getKey(),average));
                }
            }
        }
        return LoadMonitoringResults.create(sumMpa.values());
    }
}
