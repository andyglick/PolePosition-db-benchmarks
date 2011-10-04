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

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.polepos.util.JavaLangUtils.rethrow;

/**
 * @author roman.stoffel@gamlor.info
 * @since 19.09.11
 */
public final class MonitoringType {

    private final String name;
    private final String label;
    private final String unitOfMeasurment;
    private final int scaleFactory;

    MonitoringType(String name, String label,String unitOfMeasurment, int scaleFactory) {
        this.name = name;
        this.label = label;
        this.unitOfMeasurment = unitOfMeasurment;
        this.scaleFactory = scaleFactory;
    }

    public String getName() {
        return name;
    }
    public String getLabel() {
        return label;
    }

    public long calculateDisplayNumber(double input){
        return (long) (scaleFactory*input);
    }

    public String getUnitOfMeasurment() {
        return unitOfMeasurment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MonitoringType that = (MonitoringType) o;

        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "MonitoringType{" +
                "name='" + name + '\'' +
                '}';
    }

    public static MonitoringType create(String name) {
        return percentUnit(name, name);
    }
    public static MonitoringType percentUnit(String name, String label) {
        return create(name,label,"%", 100);
    }
    public static MonitoringType create(String name, String label,String unitOfMeasurment,  int displayScaleFactor) {
        return new MonitoringType(name,label,unitOfMeasurment, displayScaleFactor);
    }
    public static String machineNameAppendix() {
        try {
            return " on "+ InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw rethrow(e);
        }
    }

}