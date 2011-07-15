/* 
This file is part of the PolePosition database benchmark
http://www.polepos.org

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
MA  02111-1307, USA. */


package org.polepos.teams.mongodb;


import com.mongodb.Mongo;
import org.bson.types.ObjectId;
import org.polepos.circuits.complexconcurrency.ComplexConcurrencyDriver;
import org.polepos.framework.Car;
import org.polepos.framework.DriverBase;
import org.polepos.framework.TurnSetup;

public final class ComplexConcurrencyMongoDB extends DriverBase implements ComplexConcurrencyDriver {

    private ComplexMongoDB delegate;
    private final ConsistencyOption consistencyOption;
    private final Mongo mongoInstance;

    public ComplexConcurrencyMongoDB(ConsistencyOption consistencyOption, Mongo mongoInstance) {
        this.consistencyOption = consistencyOption;
        this.mongoInstance = mongoInstance;
        this.delegate  = new ComplexMongoDB(consistencyOption, mongoInstance);
    }

    @Override
    public void prepare() {
        delegate.prepare();
    }

    @Override
    public void closeDatabase() {
        delegate.closeDatabase();
    }

    @Override
    public void prefillDatabase() {
		delegate.write();
    }

    @Override
    public void race() {
		ObjectId[] ids = new ObjectId[writes()];
		for (int i = 0; i < writes(); i++) {
			ids[i] = ((ObjectId)delegate.write(true));
		}
		delegate.query();
		for (int i = 0; i < updates(); i++) {
			delegate.update(ids[i]);
		}
		for (int i = 0; i < deletes(); i++) {
			delegate.delete(ids[i]);
		}
    }


	@Override
	public void configure(Car car, TurnSetup setup) {
		super.configure(car, setup);
		delegate.configure(car, setup);
	}

    @Override
    public DriverBase clone() {
        ComplexConcurrencyMongoDB clone = (ComplexConcurrencyMongoDB) super.clone();
        clone.delegate = new ComplexMongoDB(consistencyOption, mongoInstance);
        return clone;
    }
}
