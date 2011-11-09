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

import org.polepos.monitoring.MonitoringSettings;
import org.polepos.monitoring.Samplers;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import java.lang.management.ManagementFactory;
import java.util.HashMap;

import static org.polepos.util.JavaLangUtils.rethrow;

/**
 * @author roman.stoffel@gamlor.info
 * @since 27.09.11
 */
public final class MonitoringServer {
    static final ObjectName NAME = name("org.polepos.monitoring.remote:type=Monitoring");
    static final Object waitUntilInterrupted = new Object();

    private static ObjectName name(String name) {
        try {
            return new ObjectName(name);
        } catch (MalformedObjectNameException e) {
            throw rethrow(e);
        }
    }

    public static void main(String[] args) throws Exception {
        main(MonitoringSettings.readFromConfig());
    }

    public static void main(MonitoringSettings config) throws Exception {
        final MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer();
         (JMXConnectorServerFactory.newJMXConnectorServer(
                 new JMXServiceURL(config.getConnectionUrl()), new HashMap<String, Object>(), beanServer)).start();
        final Monitoring theBean = new Monitoring(Samplers.create(config.getSamplers(),config.machineName()));
        try {
            beanServer.registerMBean(theBean, NAME);

            waitUntilIterrupted();

        } finally {
            beanServer.unregisterMBean(NAME);
        }
    }

    private static void waitUntilIterrupted() {
        synchronized (waitUntilInterrupted) {
            try {
                waitUntilInterrupted.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
