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

import org.springframework.beans.factory.annotation.Autowired;
import com.joshdrummond.webpasswordsafe.common.model.User;
import com.joshdrummond.webpasswordsafe.common.util.Constants;
import com.joshdrummond.webpasswordsafe.server.plugin.audit.AuditLogger;


/**
 * @author Josh Drummond
 *
 */
public class DefaultAuthorizer implements Authorizer
{
    @Autowired
    private AuditLogger auditLogger;
    

    public boolean isAuthorized(User user, Constants.Function function)
    {
        boolean isAuthorized = false;
        
        if (user != null)
        {
            if (function.equals(Constants.Function.ADD_GROUP))
            {
                isAuthorized = user.getRoles().contains(Constants.Role.ROLE_ADMIN);
            }
            else if (function.equals(Constants.Function.UPDATE_GROUP))
            {
                isAuthorized = user.getRoles().contains(Constants.Role.ROLE_ADMIN);
            }
            else if (function.equals(Constants.Function.ADD_USER))
            {
                isAuthorized = user.getRoles().contains(Constants.Role.ROLE_ADMIN);
            }
            else if (function.equals(Constants.Function.UPDATE_USER))
            {
                isAuthorized = user.getRoles().contains(Constants.Role.ROLE_ADMIN);
            }
            else if (function.equals(Constants.Function.ADD_PASSWORD))
            {
                isAuthorized = user.getRoles().contains(Constants.Role.ROLE_USER);
            }
            else if (function.equals(Constants.Function.VIEW_REPORT_CurrentPasswordExport))
            {
                isAuthorized = user.getRoles().contains(Constants.Role.ROLE_ADMIN);
            }
            else if (function.equals(Constants.Function.VIEW_REPORT_Groups))
            {
                isAuthorized = user.getRoles().contains(Constants.Role.ROLE_USER);
            }
            else if (function.equals(Constants.Function.VIEW_REPORT_PasswordAccessAudit))
            {
                isAuthorized = user.getRoles().contains(Constants.Role.ROLE_ADMIN);
            }
            else if (function.equals(Constants.Function.VIEW_REPORT_PasswordPermissions))
            {
                isAuthorized = user.getRoles().contains(Constants.Role.ROLE_USER);
            }
            else if (function.equals(Constants.Function.VIEW_REPORT_Users))
            {
                isAuthorized = user.getRoles().contains(Constants.Role.ROLE_USER);
            }
        }

        auditLogger.log("user=["+((user==null)?"":user.getUsername())+"] function=["+function+"] authorized? "+isAuthorized);
        
        return isAuthorized;
    }
}
