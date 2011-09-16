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
import org.polepos.framework.DriverBase;
import org.polepos.framework.Team;

/**
* @author roman.stoffel@gamlor.info
* @since 15.07.11
*/
public class TeamStub extends Team {

    private final String name;
    public static final String DEFAULT_NAME = "stub team";

    public TeamStub() {
        this(DEFAULT_NAME);
    }

    public TeamStub(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String description() {
        return "stub";
    }

    @Override
    public Car[] cars() {
        return new Car[]{new CarStub(this)};
    }

    @Override
    public DriverBase[] drivers() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String website() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String databaseFile() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setUp() {
        throw new UnsupportedOperationException();
    }
}
