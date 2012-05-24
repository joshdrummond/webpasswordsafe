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

import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;


/**
 * @author Josh Drummond
 *
 */
public class CompositeAuthenticator implements Authenticator
{
    public static final String KEY_ANYUSER = "anyUser";
    public static final String KEY_USERS = "users";
    public static final String KEY_AUTHENTICATOR = "authenticator";
    private static Logger LOG = Logger.getLogger(CompositeAuthenticator.class);
    private List<Map<String, Object>> authenticators;

    @SuppressWarnings("unchecked")
    @Override
    public boolean authenticate(String username, String password)
    {
        boolean valid = false;
        if (authenticators != null)
        {
            for (Map<String,Object> authEntry : authenticators)
            {
                boolean useForAuthentication = false;
                
                // check if use this for any user
                if (authEntry.containsKey(KEY_ANYUSER))
                {
                    useForAuthentication = Boolean.valueOf((String)authEntry.get(KEY_ANYUSER));
                }
                // if still not determined, check user list if exists
                if (!useForAuthentication)
                {
                    if (authEntry.containsKey(KEY_USERS))
                    {
                        List<String> users = (List<String>)authEntry.get(KEY_USERS);
                        useForAuthentication = users.contains(username);
                    }
                }
                
                // if should use this for authentication for this user, give it a try!
                if (useForAuthentication)
                {
                    Authenticator authenticator = (Authenticator)authEntry.get(KEY_AUTHENTICATOR);
                    valid = authenticator.authenticate(username, password);
                }
                
                if (valid) break;
            }
        }
        
        LOG.debug("CompositeAuthenticator: login success for "+username+"? "+valid);
        return valid;
    }

    public List<Map<String, Object>> getAuthenticators()
    {
        return authenticators;
    }

    public void setAuthenticators(List<Map<String, Object>> authenticators)
    {
        this.authenticators = authenticators;
    }

}
