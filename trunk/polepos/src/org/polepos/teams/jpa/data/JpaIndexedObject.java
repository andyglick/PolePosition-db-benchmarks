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


package org.polepos.teams.jpa.data;

import javax.persistence.*;

import org.polepos.data.*;
import org.polepos.framework.*;

@Entity
@com.versant.jpa.annotations.Indexes({
	@com.versant.jpa.annotations.Index(name="i_int", attributes={"_int"}),
	@com.versant.jpa.annotations.Index(name="i_string", attributes={"_string"})
})
public class JpaIndexedObject implements CheckSummable{
	
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long oid;
    
    @org.apache.openjpa.persistence.jdbc.Index
	public int _int;
	
    @org.apache.openjpa.persistence.jdbc.Index
	public String _string;
	
	public JpaIndexedObject(){
		
	}
	
	public JpaIndexedObject(int int_, String str){
		_int = int_;
		_string = str;
	}
	
	public JpaIndexedObject(int int_){
		this(int_, IndexedObject.queryString(int_));
	}

	@Override
	public long checkSum() {
		return _string.length();
	}

	public void updateString() {
		_string = _string.toUpperCase();
	}
	
	@Override
	public String toString() {
		return "JdoIndexedObject _int:" + _int + " _string:" + _string;
	}
	
}
