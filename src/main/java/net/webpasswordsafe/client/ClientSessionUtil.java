/*
    Copyright 2008-2012 Josh Drummond

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
package net.webpasswordsafe.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.webpasswordsafe.common.model.Group;
import net.webpasswordsafe.common.model.User;
import net.webpasswordsafe.common.util.Constants.Function;


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
    private Map<Function, Boolean> authorizations;
    private List<Map<String, Object>> reports;
    
    public static ClientSessionUtil getInstance()
    {
        return clientSessionUtil;
    }
    
    private ClientSessionUtil()
    {
        user = new User();
        isLoggedIn = false;
        authorizations = new HashMap<Function, Boolean>();
        reports = new ArrayList<Map<String,Object>>();
    }

    public boolean isAuthorized(Function function)
    {
        boolean isAuthorized = false;
        if (isLoggedIn())
        {
            if (authorizations.containsKey(function))
            {
                isAuthorized = authorizations.get(function);
            }
        }
        return isAuthorized;
    }
    
    public void setAvailableReports(List<Map<String, Object>> reports)
    {
        this.reports = reports;
    }
    
    public List<Map<String, Object>> getAvailableReports()
    {
        return reports;
    }
    
    public void setAuthorizations(Map<Function, Boolean> authorizations)
    {
        this.authorizations = authorizations;
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
