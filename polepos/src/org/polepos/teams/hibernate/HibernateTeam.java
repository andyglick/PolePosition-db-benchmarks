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

package org.polepos.teams.hibernate;

import org.hibernate.Query;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.polepos.framework.Car;
import org.polepos.framework.CarMotorFailureException;
import org.polepos.framework.DriverBase;
import org.polepos.framework.Team;
import org.polepos.teams.hibernate.data.HB4;
import org.polepos.teams.hibernate.data.HN1;
import org.polepos.teams.hibernate.data.HibernateIndexedPilot;
import org.polepos.teams.hibernate.data.HibernateLightObject;
import org.polepos.teams.hibernate.data.HibernateListHolder;
import org.polepos.teams.hibernate.data.HibernatePilot;
import org.polepos.teams.hibernate.data.HibernateTree;
import org.polepos.teams.jdbc.Jdbc;


public class HibernateTeam extends Team{
    
    private final Car[] mCars;
    
    public HibernateTeam(){
        String[] dbs = Jdbc.settings().getHibernateTypes();
        mCars = new Car[ dbs.length ];
        for( int i = 0; i < dbs.length; i++ ){
            mCars[ i ] = new HibernateCar(dbs[ i ] );
        }
    }

	public String name(){
		return "Hibernate";
	}

    @Override
    public String description() {
        return "relational persistence for idiomatic Java";
    }

    public String databaseFile() {
    	// not supported yet
    	return null;
    }


	public Car[] cars(){
		return mCars;
	}
    
    public DriverBase[] drivers() {
        return new DriverBase[]{
            new MelbourneHibernate(),
            new SepangHibernate(),
            new BahrainHibernate(),
            new ImolaHibernate(),
            new BarcelonaHibernate(),
            new MonacoHibernate(),
            new MontrealHibernate(),
            new NurburgringHibernate(),
        };
    }
    
    @Override
    public String website() {
        return "http://www.hibernate.org";
    }
    
	@Override
	protected void setUp() {
		for(int i = 0; i < mCars.length;i++){
			Session session = null;
			try {
				session = ((HibernateCar)mCars[i]).openSession();
			} catch (CarMotorFailureException e) {
				e.printStackTrace();
			}
			Transaction txn = session.beginTransaction();
			session.delete("from " + HB4.class.getName());
			txn.commit();

			txn = session.beginTransaction();
			session.delete("from " + HibernateIndexedPilot.class.getName());
			txn.commit();
			
			txn = session.beginTransaction();
			session.delete("from " + HibernatePilot.class.getName());
			txn.commit();
			
			txn = session.beginTransaction();
			session.delete("from " + HibernateTree.class.getName());
			txn.commit();
			
			txn = session.beginTransaction();
			session.delete("from " + HibernateLightObject.class.getName());
			txn.commit();
			
			txn = session.beginTransaction();
			session.delete("from " + HibernateListHolder.class.getName());
			txn.commit();
			
			txn = session.beginTransaction();
			session.delete("from " + HN1.class.getName());
			txn.commit();
			
			((HibernateCar)mCars[i]).closeSession(session);
		}
	}


}
