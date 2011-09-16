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

import org.polepos.framework.Car;
import org.polepos.framework.Team;

import java.awt.*;

/**
* @author roman.stoffel@gamlor.info
* @since 15.07.11
*/
public class CarStub extends Car {
    private final String name;

    public CarStub(Team team,String name,Color color) {
        super(team, covertToString(color));
        this.name = name;
    }

    public CarStub() {
        this(new TeamStub());
    }

    public CarStub(Team teamStub) {
        this(teamStub,Color.RED);
    }

    public CarStub(Team teamStub,Color color) {
        this(teamStub,"car-for:"+teamStub.name(),color);
    }

    private static String covertToString(Color color) {
        return "0x"+ hexValue(color.getRed())
                +hexValue(color.getGreen())
                +hexValue(color.getBlue());
    }

    private static String hexValue(int color) {
        String value = Integer.toHexString(color);
        if(value.length()==2){
            return value;
        } else {
            return "0"+value;
        }
    }

    @Override
    public String name() {
        return name;
    }
}
