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
