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

package org.polepos.monitoring.remote;

import org.approvaltests.Approvals;
import org.junit.Assert;
import org.junit.Test;
import org.polepos.monitoring.LoadMonitoringResults;
import org.polepos.monitoring.MonitoringResult;
import org.polepos.monitoring.MonitoringType;

import java.util.Collections;

import static java.util.Arrays.asList;

/**
 * @author roman.stoffel@gamlor.info
 * @since 27.09.11
 */
public class TestSerialisation {

    private static final double DELTA = 0.0001;

    @Test
    public void canSerializeEmptyResult(){
        LoadMonitoringResults original = LoadMonitoringResults.create(Collections.<MonitoringResult>emptyList());

        LoadMonitoringResults serialized = Serialisation.fromJSON(Serialisation.toJSON(original));

        Assert.assertFalse(serialized.iterator().hasNext());
    }
    @Test
    public void emptyResultSerialized() throws Exception {
        LoadMonitoringResults original = LoadMonitoringResults.create(Collections.<MonitoringResult>emptyList());

        String serialized = Serialisation.toJSON(original);

        Approvals.approve(serialized);
    }
    @Test
    public void canSerializeResult(){
        MonitoringResult r1 = MonitoringResult.create(MonitoringType.percentUnit("result-1","r-1"),55 );
        MonitoringResult r2 = MonitoringResult.create(MonitoringType.create("result-2","r-2","runits",42),42 );
        LoadMonitoringResults original
                = LoadMonitoringResults.create(asList(r1,r2));

        LoadMonitoringResults serialized = Serialisation.fromJSON(Serialisation.toJSON(original));

        final MonitoringResult sr1 = serialized.byType(r1.getType());
        final MonitoringResult sr2 = serialized.byType(r2.getType());
        Assert.assertEquals(55.0,sr1.getValue(), DELTA);
        Assert.assertEquals(42.0,sr2.getValue(),DELTA);
        Assert.assertEquals("runits",sr2.getType().getUnitOfMeasurment());
    }
    @Test
    public void serializedForm() throws Exception {
        MonitoringResult r1 = MonitoringResult.create(MonitoringType.percentUnit("result-1","r-1"),55 );
        MonitoringResult r2 = MonitoringResult.create(MonitoringType.create("result-2","r-2","runits",42),42 );
        LoadMonitoringResults original
                = LoadMonitoringResults.create(asList(r1,r2));

        String serialized = Serialisation.toJSON(original);

        Approvals.approve(serialized);
    }
}
