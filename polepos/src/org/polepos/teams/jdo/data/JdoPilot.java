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

package org.polepos.teams.jdo.data;

import org.polepos.framework.*;

public class JdoPilot implements CheckSummable {
	
	private String mName;
	
	private String mFirstName;
	
	private int mPoints;
	
	private int mLicenseID;

	public JdoPilot() {
	}

	public JdoPilot(String name, String firstName, int points, int licenseID) {
		mName = name;
		mFirstName = firstName;
		mPoints = points;
		mLicenseID = licenseID;
	}

	public JdoPilot(String name, int points) {
		mName = name;
		mPoints = points;
	}

	public int getPoints() {
		return mPoints;
	}

	public void setPoints(int points) {
		mPoints = points;
	}

	public void addPoints(int points) {
		this.mPoints += points;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public String getFirstName() {
		return mFirstName;
	}

	public void setFirstName(String firstname) {
		mFirstName = firstname;
	}

	public int getLicenseID() {
		return mLicenseID;
	}

	public void setLicenseID(int id) {
		mLicenseID = id;
	}

	public String toString() {
		return mName + "/" + mPoints;
	}

	public long checkSum() {
		return getPoints();
	}

}
