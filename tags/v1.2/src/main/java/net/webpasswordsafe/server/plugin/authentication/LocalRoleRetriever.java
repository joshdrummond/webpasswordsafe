/*
    Copyright 2010-2011 Josh Drummond

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
package net.webpasswordsafe.server.plugin.authentication;

import java.util.HashSet;
import java.util.Set;
import net.webpasswordsafe.common.model.User;
import net.webpasswordsafe.common.util.Constants.Role;
import org.apache.log4j.Logger;


/**
 * @author Josh Drummond
 *
 */
public class LocalRoleRetriever implements RoleRetriever
{
    private static Logger LOG = Logger.getLogger(LocalRoleRetriever.class);
    private Set<String> adminUsers;
    
    @Override
    public Set<Role> retrieveRoles(User user)
    {
        Set<Role> roles = new HashSet<Role>();
        
        if (null != user)
        {
            roles.add(Role.ROLE_USER);
            if (adminUsers.contains(user.getUsername()))
            {
                roles.add(Role.ROLE_ADMIN);
            }
        }
        
        LOG.debug(user.getUsername() + " has roles="+roles.toString());
        return roles;
    }

    public Set<String> getAdminUsers() {
        return adminUsers;
    }

    public void setAdminUsers(Set<String> adminUsers) {
        this.adminUsers = adminUsers;
    }

}
