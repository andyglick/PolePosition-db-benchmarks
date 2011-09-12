package org.polepos.teams.mongodb;

import org.polepos.framework.Car;
import org.polepos.framework.DriverBase;
import org.polepos.framework.Team;

/**
* @author roman.stoffel@gamlor.info
* @since 15.07.11
*/
class TeamStub extends Team {

    @Override
    public String name() {
        return "stub team";
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
