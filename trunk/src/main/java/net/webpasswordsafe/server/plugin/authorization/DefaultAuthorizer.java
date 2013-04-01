/*
    Copyright 2010-2013 Josh Drummond

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

import java.util.Map;
import net.webpasswordsafe.common.model.User;
import net.webpasswordsafe.common.util.Constants;
import net.webpasswordsafe.common.util.Constants.Function;
import net.webpasswordsafe.common.util.Constants.Role;
import net.webpasswordsafe.server.report.ReportConfig;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * @author Josh Drummond
 *
 */
public class DefaultAuthorizer implements Authorizer
{
    private static Logger LOG = Logger.getLogger(DefaultAuthorizer.class);

    @Autowired
    private ReportConfig reportConfig;
    private boolean allowAdminBypassPasswordPermissions;

    @Override
    public boolean isAuthorized(User user, String action)
    {
        boolean isAuthorized = false;
        
        if ((user != null) && (action != null))
        {
            if (action.equals(Function.ADD_GROUP.name()) ||
                action.equals(Function.UPDATE_GROUP.name()) ||
                action.equals(Function.DELETE_GROUP.name()) ||
                action.equals(Function.ADD_USER.name()) ||
                action.equals(Function.UPDATE_USER.name()) ||
                (allowAdminBypassPasswordPermissions && action.equals(Function.BYPASS_PASSWORD_PERMISSIONS.name())) ||
                action.equals(Function.BYPASS_TEMPLATE_SHARING.name()) ||
                action.equals(Function.UNBLOCK_IP.name()))
            {
                isAuthorized = user.getRoles().contains(Role.ROLE_ADMIN);
            }
            else if (action.equals(Function.ADD_PASSWORD.name()) ||
                action.equals(Function.ADD_TEMPLATE.name()) ||
                action.equals(Function.UPDATE_TEMPLATE.name()))
            {
                isAuthorized = user.getRoles().contains(Role.ROLE_USER);
            }
            else if (action.startsWith(Constants.VIEW_REPORT_PREFIX))
            {
                String reportName = action.substring(Constants.VIEW_REPORT_PREFIX.length());
                Map<String, Object> report = reportConfig.getReport(reportName);
                if (report != null)
                {
                    Role reportRole = Role.valueOf((String)report.get(Constants.ROLE));
                    isAuthorized = user.getRoles().contains(reportRole);
                }
            }
        }

        LOG.debug("user=["+((user==null)?"":user.getUsername())+"] action=["+action+"] authorized? "+isAuthorized);
        return isAuthorized;
    }

    public boolean isAllowAdminBypassPasswordPermissions()
    {
        return allowAdminBypassPasswordPermissions;
    }

    public void setAllowAdminBypassPasswordPermissions(
            boolean allowAdminBypassPasswordPermissions)
    {
        this.allowAdminBypassPasswordPermissions = allowAdminBypassPasswordPermissions;
    }
    
}
