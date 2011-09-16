package org.polepos.monitoring;

import org.junit.Test;
import org.polepos.util.NoArgAction;
import org.polepos.util.NoArgFunction;

import java.util.Collection;
import java.util.Collections;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.polepos.monitoring.MonitoringTestUtils.TEST_INTERVAL_IN_MILLISEC;
import static org.polepos.monitoring.MonitoringTestUtils.waitFor;
import static org.polepos.util.NoArgAction.NULL_ACTION;

/**
 * @author roman.stoffel@gamlor.info
 * @since 14.09.11
 */
public class TestMonitoring {

    @Test
    public void monitoringReturnsNotNull() {
        final LoadMonitoringResults results = Monitoring.monitor(Collections.<Sampler>emptyList(),NULL_ACTION);
        assertNotNull(results);
    }
    @Test
    public void monitoringRunsSamplers() {
        CallCountingSampler sampler = new CallCountingSampler();
        final LoadMonitoringResults results = Monitoring.monitor(Collections.singleton(sampler),
                new NoArgAction() {
                    @Override
                    public void invoke() {
                        waitFor(TEST_INTERVAL_IN_MILLISEC*2);
                    }
                },TEST_INTERVAL_IN_MILLISEC);
        assertNotNull(results);
        sampler.assertHasBeenCalled();
    }

    @Test
    public void defaultSetIsNotNull() {
        final Collection<? extends Sampler> defaults = Monitoring.createDefaultMonitors();
        assertNotNull(defaults);
    }

    @Test
    public void returnsOriginalValue() {
        final Monitoring.ResultAndData results = Monitoring.monitor(new NoArgFunction<String>() {
            @Override
            public String invoke() {
                return "hello world";
            }
        });

        assertEquals("hello world", results.getData());
        assertNotNull(results.getMonitoring());
    }
}
