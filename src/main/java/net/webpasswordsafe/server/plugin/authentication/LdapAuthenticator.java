/*
    Copyright 2010-2015 Josh Drummond

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

import net.webpasswordsafe.common.util.Constants.AuthenticationStatus;
import org.apache.log4j.Logger;
import org.springframework.ldap.core.LdapTemplate;


/**
 * @author Josh Drummond
 *
 */
public class LdapAuthenticator implements Authenticator
{
    private LdapTemplate ldapTemplate;
    private String filter;
    private String base;
    private static Logger LOG = Logger.getLogger(LdapAuthenticator.class);

    @Override
    public AuthenticationStatus authenticate(String principal, String[] credentials)
    {
        AuthenticationStatus authStatus = AuthenticationStatus.FAILURE;
        try
        {
            String userFilter = filter.replace("$1", principal);
            LOG.debug("ldap filter="+userFilter);
            authStatus = ldapTemplate.authenticate(base, userFilter, credentials[0]) ? AuthenticationStatus.SUCCESS : AuthenticationStatus.FAILURE;
        }
        catch (Exception e)
        {
            // an exception is expected when bad credentials are used
            LOG.debug("ldap error authenticating: "+ e.getMessage());
            authStatus = AuthenticationStatus.FAILURE;
        }
        LOG.debug("LdapAuthenticator: login success for "+principal+"? "+authStatus.name());
        return authStatus;
    }

    public LdapTemplate getLdapTemplate() {
        return ldapTemplate;
    }

    public void setLdapTemplate(LdapTemplate ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }
    
}
