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

import org.polepos.monitoring.MonitoringResult;
import org.polepos.monitoring.SamplingSession;
import org.polepos.util.NoArgFunction;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.Collection;

import static org.polepos.util.JavaLangUtils.rethrow;

/**
 * @author roman.stoffel@gamlor.info
 * @since 27.09.11
 */
public final class RemoteSamplingSession implements SamplingSession {
    private final MonitoringMXBean monitoringBean;

    private RemoteSamplingSession(MonitoringMXBean monitoringBean) {
        this.monitoringBean = monitoringBean;
        monitoringBean.start();
    }

    public static SamplingSession remote(MonitoringMXBean monitoringBean) {
        return new RemoteSamplingSession(monitoringBean);
    }

    @Override
    public Collection<MonitoringResult> sampleAndReturnResults() {
        String result = monitoringBean.stop();
        return Serialisation.fromJSON(result).asCollection();
    }

    public static NoArgFunction<SamplingSession> newFactory(String url) {
        try {
            final JMXConnector jmxc = JMXConnectorFactory.connect(new JMXServiceURL(url), null);
            final MBeanServerConnection connection = jmxc.getMBeanServerConnection();
            final MonitoringMXBean mxBean = JMX.newMBeanProxy(connection, MonitoringServer.NAME, MonitoringMXBean.class);

            return new NoArgFunction<SamplingSession>() {
                @Override
                public SamplingSession invoke() {
                    return remote(mxBean);
                }
            };
        } catch (Exception e) {
            throw rethrow(e);
        }
    }
}
