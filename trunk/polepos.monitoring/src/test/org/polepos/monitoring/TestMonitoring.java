package org.polepos.monitoring;

import org.junit.Test;
import org.polepos.util.NoArgFunction;

import static java.util.Arrays.asList;
import static junit.framework.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.polepos.monitoring.MonitoringTestUtils.waitFor;

/**
 * @author roman.stoffel@gamlor.info
 * @since 14.09.11
 */
public class TestMonitoring {

    private static final int WAIT_TIME_IN_MILLISEC = 1000;

    @Test
    public void monitoringReturnsNotNull() {
        final LoadMonitoringResults results = new Monitoring(true,collector()).monitor(
                new NoArgFunction<Object>() {
                    @Override
                    public Object invoke() {
                        return null;
                    }
                }).getMonitoring();
        assertNotNull(results);
    }

    private SessionFactory collector(Sampler...samplers) {
        SessionFactory sf = mock(SessionFactory.class);
        when(sf.accordingToConfiguration()).thenReturn(SessionFactory.localSession(asList(samplers)));
        return sf;
    }

    @Test
    public void monitoringRunsSamplers() {
        CallCountingSampler sampler = new CallCountingSampler();
        final String ourResult = "result";
        final Monitoring.ResultAndData<String> results = new Monitoring(true,collector(sampler)).monitor(
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
    public void canDisableMonitoring() {
        final MonitoringSettings settings = new MonitoringSettings(false,"","",new String[]{"1"});
        final LoadMonitoringResults results = Monitoring.createInstance(settings).monitor(new NoArgFunction<Object>() {
            @Override
            public Object invoke() {
                waitFor(WAIT_TIME_IN_MILLISEC);
                return null;
            }
        }).getMonitoring();
        assertFalse(results.iterator().hasNext());
    }

    private SessionFactory defaultListeners() {
        return collector(Samplers.create(Samplers.allSamplerNames()).cpuLoad());
    }

    @Test
    public void returnsOriginalValue() {
        final Monitoring.ResultAndData results = new Monitoring(true,defaultListeners()).monitor(new NoArgFunction<String>() {
            @Override
            public String invoke() {
                return "hello world";
            }
        });

        assertEquals("hello world", results.getData());
        assertNotNull(results.getMonitoring());
    }
}
