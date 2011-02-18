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


package org.polepos.teams.jdbc;

import org.polepos.circuits.complexconcurrency.*;
import org.polepos.framework.*;

public class ComplexConcurrencyJdbc extends JdbcDriver implements ComplexConcurrencyDriver {
	
	private ComplexJdbc _delegate = new ComplexJdbc();
	
	@Override
	public void prefillDatabase() {
		_delegate.write();
	}
	
	@Override
	public void race() {
		int[] ids = new int[writes()];
		for (int i = 0; i < writes(); i++) {
			ids[i] = ((Integer)_delegate.write()).intValue();
		}
		_delegate.query();
		for (int i = 0; i < updates(); i++) {
			_delegate.update(ids[i]);
		}
		for (int i = 0; i < deletes(); i++) {
			_delegate.delete(ids[i]);
		}
	}
	
	@Override
	public void prepare() {
		_delegate.prepare();
	}
	
	@Override
	public void prepareDatabase() {
		_delegate.prepareDatabase();
	}
	
	@Override
	public void configure(Car car, TurnSetup setup) {
		super.configure(car, setup);
		_delegate.configure(car, setup);
	}
	
	@Override
	public void closeDatabase() {
		_delegate.closeDatabase();
	}

	@Override
	public ComplexConcurrencyJdbc clone() {
		ComplexConcurrencyJdbc clone = (ComplexConcurrencyJdbc) super.clone();
		clone._delegate = new ComplexJdbc(_delegate.idGenerator());
		return clone;
	}

}
