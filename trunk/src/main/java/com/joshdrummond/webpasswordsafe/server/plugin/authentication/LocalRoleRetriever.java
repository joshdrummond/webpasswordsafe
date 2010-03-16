/*
    Copyright 2010 Josh Drummond

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
package com.joshdrummond.webpasswordsafe.server.plugin.authentication;

import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;
import com.joshdrummond.webpasswordsafe.common.model.User;
import com.joshdrummond.webpasswordsafe.common.util.Constants;


/**
 * @author Josh Drummond
 *
 */
public class LocalRoleRetriever implements RoleRetriever
{
    private static Logger LOG = Logger.getLogger(LocalRoleRetriever.class);

    public Set<Constants.Role> retrieveRoles(User user)
    {
        Set<Constants.Role> roles = new HashSet<Constants.Role>();
        
        if (null != user)
        {
            roles.add(Constants.Role.ROLE_USER);
            if (user.getUsername().equals(Constants.ADMIN_USER_NAME))
            {
                roles.add(Constants.Role.ROLE_ADMIN);
            }
        }
        
        LOG.debug(user.getUsername() + " has roles="+roles.toString());
        return roles;
    }
}
