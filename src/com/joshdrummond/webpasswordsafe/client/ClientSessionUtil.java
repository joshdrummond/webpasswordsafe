/*
    Copyright 2008-2009 Josh Drummond

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

package com.joshdrummond.webpasswordsafe.client;

import com.joshdrummond.webpasswordsafe.common.model.Group;
import com.joshdrummond.webpasswordsafe.common.model.User;

/**
 * Client-side session state and utilities
 * 
 * @author Josh Drummond
 *
 */
public class ClientSessionUtil
{
	private static final ClientSessionUtil clientSessionUtil = new ClientSessionUtil();
	
    private User user;
    private Group everyoneGroup;
    private boolean isLoggedIn;
    
    public static ClientSessionUtil getInstance()
    {
    	return clientSessionUtil;
    }
    
    private ClientSessionUtil()
    {
        user = new User();
        isLoggedIn = false;
    }

    public boolean isAuthorized(String permission)
    {
        return isLoggedIn();
    }
    
    public boolean isLoggedIn()
    {
        return isLoggedIn;
    }
    
    public void setLoggedIn(boolean isLoggedIn)
    {
        this.isLoggedIn = isLoggedIn;
    }

    public User getLoggedInUser()
    {
        return user;
    }
    
    public void setLoggedInUser(User user)
    {
        this.user = user;
    }
    
    public Group getEveryoneGroup()
    {
        return this.everyoneGroup;
    }

    public void setEveryoneGroup(Group everyoneGroup)
    {
        this.everyoneGroup = everyoneGroup;
    }

}
