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

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import org.polepos.monitoring.LoadMonitoringResults;
import org.polepos.monitoring.MonitoringResult;
import org.polepos.monitoring.MonitoringType;

/**
 * @author roman.stoffel@gamlor.info
 * @since 27.09.11
 */
public final class Serialisation {

    private Serialisation(){}

    public static String toJSON(LoadMonitoringResults data) {
        XStream xstream = newSerializer();
        return xstream.toXML(data);
    }

    private static XStream newSerializer() {
        XStream xstream = new XStream(new JettisonMappedXmlDriver());
        xstream.setMode(XStream.NO_REFERENCES);
        xstream.alias("loadresult", LoadMonitoringResults.class);
        xstream.alias("result", MonitoringResult.class);
        xstream.alias("resulttype", MonitoringType.class);
        return xstream;
    }

    public static LoadMonitoringResults fromJSON(String data) {
        XStream xstream = newSerializer();
        return (LoadMonitoringResults) xstream.fromXML(data);
    }
}
