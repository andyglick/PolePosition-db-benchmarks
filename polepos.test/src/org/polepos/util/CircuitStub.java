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

package org.polepos.util;

import org.polepos.framework.CircuitBase;
import org.polepos.framework.Lap;
import org.polepos.framework.RacingStrategy;
import org.polepos.framework.TimedLapsRacingStrategy;

import java.util.Collections;
import java.util.List;

/**
 * @author roman.stoffel@gamlor.info
 * @since 16.09.11
 */
public class CircuitStub extends CircuitBase{
    @Override
    public List<Lap> laps() {
        return Collections.emptyList();
    }

    @Override
    public Class<?> requiredDriver() {
        throw new Error("TODO");
    }

    @Override
    public String description() {
        return "CircuitStub";
    }

    @Override
    public boolean isConcurrency() {
        return false;
    }

    @Override
    public RacingStrategy racingStrategy() {
        return new TimedLapsRacingStrategy(null);
    }

    @Override
    public boolean isFixedTime() {
        return false;
    }
}
