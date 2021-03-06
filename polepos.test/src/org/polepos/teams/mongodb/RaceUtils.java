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

package org.polepos.teams.mongodb;


import org.polepos.framework.SetupProperty;
import org.polepos.framework.TurnSetup;
import org.polepos.framework.TurnSetupConfig;

final class RaceUtils {
    private RaceUtils(){}

    static TurnSetup newTurn() {
        TurnSetup setup = new TurnSetup();
        setup.addSetting(new SetupProperty(TurnSetupConfig.DEPTH,2));
        setup.addSetting(new SetupProperty(TurnSetupConfig.OBJECTCOUNT,5));
        setup.addSetting(new SetupProperty(TurnSetupConfig.SELECTCOUNT,5));
        setup.addSetting(new SetupProperty(TurnSetupConfig.DEPTH,5));
        setup.addSetting(new SetupProperty(TurnSetupConfig.COMMITINTERVAL,1000));
        setup.addSetting(new SetupProperty(TurnSetupConfig.UPDATECOUNT,5));
        return setup;
    }
}
