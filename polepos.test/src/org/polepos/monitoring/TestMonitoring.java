package org.polepos.monitoring;

import org.junit.Test;
import org.polepos.util.NoArgFunction;

import java.util.Collection;
import java.util.Collections;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static org.polepos.monitoring.MonitoringTestUtils.TEST_INTERVAL_IN_MILLISEC;
import static org.polepos.monitoring.MonitoringTestUtils.waitFor;

/**
 * @author roman.stoffel@gamlor.info
 * @since 14.09.11
 */
public class TestMonitoring {

    private static final int WAIT_TIME_IN_MILLISEC = 1000;

    @Test
    public void monitoringReturnsNotNull() {
        final LoadMonitoringResults results = Monitoring.monitor(testSettings(),
                Collections.<Sampler>emptyList(),new NoArgFunction<Object>() {
            @Override
            public Object invoke() {
                return null;
            }
        }).getMonitoring();
        assertNotNull(results);
    }

    private MonitoringSettings testSettings() {
        return new MonitoringSettings(true,TEST_INTERVAL_IN_MILLISEC);
    }

    @Test
    public void monitoringRunsSamplers() {
        CallCountingSampler sampler = new CallCountingSampler();
        final String ourResult = "result";
        final Monitoring.ResultAndData<String> results = Monitoring.monitor(testSettings(),Collections.singleton(sampler),
                new NoArgFunction<String> ()
                {
                    @Override
                    public String invoke() {
                        waitFor(WAIT_TIME_IN_MILLISEC);
                        return ourResult;
                    }
                });
        assertNotNull(results);
        assertEquals(ourResult, results.getData());
        sampler.assertHasBeenCalled();
    }

    @Test
    public void defaultSetIsNotNull() {
        final Collection<? extends Sampler> defaults = Monitoring.createDefaultMonitors();
        assertNotNull(defaults);
    }
    @Test
    public void canDisableMonitoring() {
        final MonitoringSettings settings = new MonitoringSettings(false, TEST_INTERVAL_IN_MILLISEC);
        final Collection<? extends Sampler> defaults = Monitoring.createDefaultMonitors();
        final LoadMonitoringResults results = Monitoring.monitor(settings, defaults, new NoArgFunction<Object>() {
            @Override
            public Object invoke() {
                waitFor(WAIT_TIME_IN_MILLISEC);
                return null;
            }
        }).getMonitoring();
        assertFalse(results.iterator().hasNext());
    }

    @Test
    public void returnsOriginalValue() {
        final Monitoring.ResultAndData results = Monitoring.monitor(testSettings(),Monitoring.createDefaultMonitors(),new NoArgFunction<String>() {
            @Override
            public String invoke() {
                return "hello world";
            }
        });

        assertEquals("hello world", results.getData());
        assertNotNull(results.getMonitoring());
    }
}
