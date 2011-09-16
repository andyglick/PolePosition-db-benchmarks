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

import junit.framework.Assert;
import org.junit.Test;

import java.util.Collections;

import static java.util.Arrays.asList;
import static org.polepos.monitoring.LoadMonitoringResults.create;

/**
 * @author roman.stoffel@gamlor.info
 * @since 15.09.11
 */
public class TestLoadMonitoringResults {

    private static final String RESULT_ONE = "test1";
    private static final String RESULT_TWO = "test2";

    @Test
    public void singleResultSumIp(){
        final LoadMonitoringResults original = create(Collections.singleton(Result.create(RESULT_ONE, 1.0)));
        LoadMonitoringResults sum = LoadMonitoringResults.sumUp(Collections.singleton(original));
        Assert.assertEquals(1.0,sum.iterator().next().getValue());
    }
    @Test
    public void sumsUp(){
        final LoadMonitoringResults original1 = create(Collections.singleton(Result.create(RESULT_ONE, 1.0)));
        final LoadMonitoringResults original2 = create(Collections.singleton(Result.create(RESULT_ONE, 0.5)));
        LoadMonitoringResults sum = LoadMonitoringResults.sumUp(asList(original1, original2));
        Assert.assertEquals(0.75, sum.iterator().next().getValue(),0.001);
    }
    @Test
    public void keepsDifferentTypesAppart(){
        final LoadMonitoringResults original1 = create(asList(Result.create(RESULT_ONE, 1.0), Result.create(RESULT_TWO, 2.0)));
        final LoadMonitoringResults original2 = create(asList(Result.create(RESULT_ONE, 0.5), Result.create(RESULT_TWO, 1.0)));
        LoadMonitoringResults sum = LoadMonitoringResults.sumUp(asList(original1, original2));
        Assert.assertEquals(0.75, sum.byName(RESULT_ONE).getValue(),0.001);
        Assert.assertEquals(1.5, sum.byName(RESULT_TWO).getValue(),0.001);
    }
}
