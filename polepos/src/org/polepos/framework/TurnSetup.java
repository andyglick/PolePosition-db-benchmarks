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

package org.polepos.framework;

import java.util.*;



/**
 * @author Herkules
 */
public class TurnSetup implements Cloneable{
    
    private Map<SetupProperty, SetupProperty> mSettings = new Hashtable<SetupProperty, SetupProperty>();
    
    public TurnSetup() {
    }

    public TurnSetup(SetupProperty... properties) {
    	for (SetupProperty property : properties) {
			mSettings.put(property, property);
		}
    }

    TurnSetup deepClone(){
        TurnSetup res = null;
        try {
            res = (TurnSetup)this.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        res.mSettings = new Hashtable<SetupProperty, SetupProperty>();
        for(SetupProperty sp : mSettings.keySet()){
            res.mSettings.put(sp, sp);
        }
        return res;
    }
    
    
    private int getSetting(String key){
        SetupProperty p = mSettings.get(new SetupProperty(key, 0));
        if(p != null){
            return p.value();
        }
        return 0;
    }
    
    public int getCommitInterval(){
        return getSetting(TurnSetupConfig.COMMITINTERVAL);
    }

    public int getCommitCount(){
        return getSetting(TurnSetupConfig.COMMITCOUNT);
    }

    public int getObjectCount(){
        return getSetting(TurnSetupConfig.OBJECTCOUNT);
    }
    
    public int getSelectCount(){
        return getSetting(TurnSetupConfig.SELECTCOUNT);
    }
    
    public int getUpdateCount(){
        return getSetting(TurnSetupConfig.UPDATECOUNT);
    }
    
    public int getTreeWidth(){
        return getSetting(TurnSetupConfig.TREEWIDTH);
    }
    
    public int getTreeDepth(){
        return getSetting(TurnSetupConfig.TREEDEPTH);
    }
    
    public int getObjectSize(){
        return getSetting(TurnSetupConfig.OBJECTSIZE);
    }
    
    public int getMostImportantValueForGraph(){
        for (int i = 0; i < TurnSetupConfig.AVAILABLE_SETTINGS.length; i++) {
            int val = getSetting(TurnSetupConfig.AVAILABLE_SETTINGS[i]);
            if(val > 0){
                return val;
            }
        }
        return 0;
    }
    
    public String getMostImportantNameForGraph(){
        for (int i = 0; i < TurnSetupConfig.AVAILABLE_SETTINGS.length; i++) {
            int val = getSetting(TurnSetupConfig.AVAILABLE_SETTINGS[i]);
            if(val > 0){
                return TurnSetupConfig.AVAILABLE_SETTINGS[i];
            }
        }
        return "";
    }
    
    public Set<SetupProperty> properties() {
        return Collections.unmodifiableSet(mSettings.keySet());
    }
    
    public void addSetting(SetupProperty setupProperty){
    	mSettings.put(setupProperty, setupProperty);
    }
    
}
