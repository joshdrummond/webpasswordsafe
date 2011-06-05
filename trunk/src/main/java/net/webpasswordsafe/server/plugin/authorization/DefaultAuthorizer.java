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
package net.webpasswordsafe.server.plugin.authorization;

import net.webpasswordsafe.common.model.User;
import net.webpasswordsafe.common.util.Constants.Function;
import net.webpasswordsafe.common.util.Constants.Role;
import org.apache.log4j.Logger;


/**
 * @author Josh Drummond
 *
 */
public class DefaultAuthorizer implements Authorizer
{
    private static Logger LOG = Logger.getLogger(DefaultAuthorizer.class);

    @Override
    public boolean isAuthorized(User user, Function function)
    {
        boolean isAuthorized = false;
        
        if (user != null)
        {
            switch (function)
            {
                case ADD_GROUP:
                case UPDATE_GROUP:
                case ADD_USER:
                case UPDATE_USER:
                case BYPASS_PASSWORD_PERMISSIONS:
                case BYPASS_TEMPLATE_SHARING:
                case VIEW_REPORT_PasswordPermissions:
                case VIEW_REPORT_CurrentPasswordExport:
                case VIEW_REPORT_PasswordAccessAudit:
                    isAuthorized = user.getRoles().contains(Role.ROLE_ADMIN);
                    break;
                case ADD_PASSWORD:
                case ADD_TEMPLATE:
                case UPDATE_TEMPLATE:
                case VIEW_REPORT_Groups:
                case VIEW_REPORT_Users:
                    isAuthorized = user.getRoles().contains(Role.ROLE_USER);
                    break;
            }
        }

        LOG.debug("user=["+((user==null)?"":user.getUsername())+"] function=["+function+"] authorized? "+isAuthorized);
        return isAuthorized;
    }
}
