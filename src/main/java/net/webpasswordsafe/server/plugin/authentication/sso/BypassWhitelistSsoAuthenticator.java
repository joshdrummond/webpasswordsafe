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
package net.webpasswordsafe.server.plugin.authentication.sso;

import java.util.HashSet;
import java.util.Set;


/**
 * 
 * @author Josh Drummond
 *
 */
public abstract class BypassWhitelistSsoAuthenticator
    implements SsoAuthenticator
{
    private Set<String> bypassAllowedUsers;
    
    public BypassWhitelistSsoAuthenticator()
    {
        bypassAllowedUsers = new HashSet<String>();
    }
    
    @Override
    public boolean isBypassAllowed(String principal)
    {
        return getBypassAllowedUsers().contains(principal);
    }

    public Set<String> getBypassAllowedUsers() {
        return bypassAllowedUsers;
    }
    public void setBypassAllowedUsers(Set<String> bypassAllowedUsers) {
        this.bypassAllowedUsers = bypassAllowedUsers;
    }

}
