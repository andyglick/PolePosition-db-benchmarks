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

import org.junit.Assert;
import org.junit.Test;
import org.polepos.util.NoArgAction;

import javax.management.JMX;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.lang.management.ManagementFactory;

import static org.polepos.monitoring.remote.ServerForTests.withRunningServer;
import static org.polepos.util.JavaLangUtils.rethrow;

/**
 * @author roman.stoffel@gamlor.info
 * @since 27.09.11
 */
public class TestMonitoringServer {

    @Test
    public void exportsBean() throws Exception {

        withRunningServer(new NoArgAction() {
            @Override
            public void invoke() {
                try {
                    final MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer();
                    final MBeanInfo beanInfo = beanServer.getMBeanInfo(MonitoringServer.NAME);
                    Assert.assertNotNull(beanInfo);
                } catch (Exception e) {
                    throw rethrow(e);
                }
            }
        });
    }

    @Test
    public void canDoMonitoring() throws Exception {

        withRunningServer(new NoArgAction() {
            @Override
            public void invoke() {
                final MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer();
                final MonitoringMXBean beanInfo = JMX.newMBeanProxy(beanServer, MonitoringServer.NAME, MonitoringMXBean.class);
                beanInfo.start();
                final String result = beanInfo.stop();
                Assert.assertNotNull(result);
            }
        });

    }
    @Test
    public void secondStartRestarts() throws Exception {

        withRunningServer(new NoArgAction() {
            @Override
            public void invoke() {
                final MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer();
                final MonitoringMXBean beanInfo = JMX.newMBeanProxy(beanServer, MonitoringServer.NAME, MonitoringMXBean.class);
                beanInfo.start();
                beanInfo.start();
                final String result = beanInfo.stop();
                Assert.assertNotNull(result);
            }
        });

    }
    @Test
    public void canJustStartAgain() throws Exception {

        withRunningServer(new NoArgAction() {
            @Override
            public void invoke() {
                final MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer();
                final MonitoringMXBean beanInfo1 = JMX.newMBeanProxy(beanServer, MonitoringServer.NAME, MonitoringMXBean.class);
                final MonitoringMXBean beanInfo2 = JMX.newMBeanProxy(beanServer, MonitoringServer.NAME, MonitoringMXBean.class);
                beanInfo1.start();
                beanInfo2.start();
            }
        });

    }

    @Test
    public void isReachableUnderConfiguredPort() throws Exception {
        final String connectionString = ServerForTests.newUrl();
        withRunningServer(connectionString, new NoArgAction() {
            @Override
            public void invoke() {
                try {
                    JMXServiceURL url =
                            new JMXServiceURL(connectionString);
                    JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
                    Assert.assertNotNull(jmxc);
                } catch (Exception e) {
                    throw rethrow(e);
                }
            }
        });
    }

}