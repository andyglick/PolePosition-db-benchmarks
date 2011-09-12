package org.polepos.teams.mongodb;

import org.polepos.framework.Car;
import org.polepos.framework.Team;

/**
* @author roman.stoffel@gamlor.info
* @since 15.07.11
*/
class CarStub extends Car {
    CarStub() {
        super(new TeamStub(), "0x000000");
    }

    CarStub(Team team) {
        super(team, "0x000000");
    }

    @Override
    public String name() {
        return "mockCar";
    }
}
