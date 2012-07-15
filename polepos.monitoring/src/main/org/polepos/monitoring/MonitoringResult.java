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

/**
 * @author roman.stoffel@gamlor.info
 * @since 14.09.11
 */
public final class MonitoringResult{
    private final MonitoringType name;
    private final double value;

    private MonitoringResult(MonitoringType name, double value) {
        this.name = name;
        this.value = value;
    }

    public static MonitoringResult create(MonitoringType name, double value){
        return new MonitoringResult(name, value);
    }

    public MonitoringType getType() {
        return name;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Result{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}