/*
    Copyright 2015 Josh Drummond

    This file is part of WebPasswordSafe.

    WebPasswordSafe is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    WebPasswordSafe is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with WebPasswordSafe; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/
package net.webpasswordsafe.common.dto;

import java.io.Serializable;
import net.webpasswordsafe.common.model.Group;


/**
 * 
 * @author Josh Drummond
 *
 */
public class SystemSettings implements Serializable
{
    private static final long serialVersionUID = 2669369473714925351L;
    private boolean isSsoEnabled;
    private String logoutUrl;
    private Group everyoneGroup;
    
    public SystemSettings()
    {
    }
    
    public boolean isSsoEnabled() {
        return isSsoEnabled;
    }
    public void setSsoEnabled(boolean isSsoEnabled) {
        this.isSsoEnabled = isSsoEnabled;
    }
    public String getLogoutUrl() {
        return logoutUrl;
    }
    public void setLogoutUrl(String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }
    public Group getEveryoneGroup() {
        return everyoneGroup;
    }
    public void setEveryoneGroup(Group everyoneGroup) {
        this.everyoneGroup = everyoneGroup;
    }

}
