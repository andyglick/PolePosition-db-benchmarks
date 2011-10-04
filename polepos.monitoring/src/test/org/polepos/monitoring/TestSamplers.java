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

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

/**
 * @author roman.stoffel@gamlor.info
 * @since 16.09.11
 */
public class TestSamplers {

    @Test
    public void returnsCPULoadMeter(){
        final Sampler sampler = Samplers.create(Samplers.allSamplerNames()).cpuLoad();
        Assert.assertTrue(sampler.collectResult().getValue() >=0.0);
    }
    @Test
    public void returnDiskReadCounter(){
        final Sampler sampler = Samplers.create(Samplers.allSamplerNames()).diskReads();
        Assert.assertTrue(sampler.collectResult().getValue() >= 0.0);
    }
    @Test
    public void returnNetwork(){
        final Sampler sampler = Samplers.create(Samplers.allSamplerNames()).networkReads();
        Assert.assertTrue(sampler.collectResult().getValue() >= 0.0);
    }
    @Test
    public void returnSendNetwork(){
        final Sampler sampler = Samplers.create(Samplers.allSamplerNames()).networkSends();
        Assert.assertTrue(sampler.collectResult().getValue() >= 0.0);
    }
    @Test
    public void doesContainAllSamplers(){
        final Collection<? extends Sampler> samplers = Samplers.create(Samplers.allSamplerNames()).samplers();
        Assert.assertEquals(4,samplers.size());
    }
    @Test
    public void filtersByName(){
        final Collection<? extends Sampler> samplers = Samplers.create(asList(DiskReadCounter.class.getSimpleName()))
                .samplers();
        assertContains(DiskReadCounter.class,samplers);
        assertEquals(1, samplers.size());
    }

    private void assertContains(Class<?> requiredType, Collection<? extends Sampler> samplers) {
        for (Sampler sampler : samplers) {
            if(requiredType.isInstance(sampler)){
                return;
            }
        }
        Assert.fail("Couldn't find "+requiredType+" in "+ samplers);
    }
}
