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

import org.polepos.monitoring.CPULoadCollector;
import org.polepos.monitoring.MonitoringSettings;
import org.polepos.monitoring.PortUtil;
import org.polepos.util.NoArgAction;

import static org.polepos.util.JavaLangUtils.rethrow;

/**
 * @author roman.stoffel@gamlor.info
 * @since 28.09.11
 */
public final class ServerForTests {
    private ServerForTests(){}
    public static void withRunningServer(NoArgAction action) {
        withRunningServer(newUrl(), action);
    }

    public static void withRunningServer(String connectionString, NoArgAction action) {
        final Thread thread = runMain(connectionString);
        waitUntilThreadIsWaiting(thread);
        try {
            action.invoke();
        } finally {
            thread.interrupt();
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw rethrow(e);
            }
        }

    }

    public static String newUrl() {
        return "service:jmx:jmxmp://0.0.0.0:" + PortUtil.getFreePort();
    }

    static void waitUntilThreadIsWaiting(Thread thread) {
        while (thread.getState() != Thread.State.WAITING) {
            Thread.yield();
        }
    }

    static Thread runMain(final String connectionString) {
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MonitoringServer.main(new MonitoringSettings(true,connectionString,
                            new String[]{CPULoadCollector.class.getSimpleName()}));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
        return thread;
    }
}
