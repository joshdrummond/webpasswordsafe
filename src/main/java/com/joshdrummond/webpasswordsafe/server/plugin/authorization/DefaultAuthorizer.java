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
package com.joshdrummond.webpasswordsafe.server.plugin.authorization;

import org.apache.log4j.Logger;
import com.joshdrummond.webpasswordsafe.common.model.User;


/**
 * @author Josh Drummond
 *
 */
public class DefaultAuthorizer implements Authorizer
{
    private static Logger LOG = Logger.getLogger(DefaultAuthorizer.class);

    public boolean isAuthorized(User user, String function)
    {
        boolean isAuthorized = false;
        
        if ("ADD_GROUP".equals(function))
        {
            isAuthorized = user.getRoles().contains("ROLE_ADMIN");
        }
        else if ("ADD_USER".equals(function))
        {
            isAuthorized = user.getRoles().contains("ROLE_ADMIN");
        }
        else if ("ADD_PASSWORD".equals(function))
        {
            isAuthorized = user.getRoles().contains("ROLE_USER");
        }
        LOG.debug("user=["+user.getName()+"] function=["+function+"] authorized? "+isAuthorized);

        return isAuthorized;
    }

}
