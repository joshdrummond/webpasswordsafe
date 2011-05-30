/*
    Copyright 2011 Josh Drummond

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

import java.util.Date;
import javax.annotation.Resource;
import org.apache.log4j.Logger;
import com.joshdrummond.webpasswordsafe.common.model.User;
import com.joshdrummond.webpasswordsafe.common.model.UserLockout;
import com.joshdrummond.webpasswordsafe.server.ServerSessionUtil;
import com.joshdrummond.webpasswordsafe.server.dao.UserDAO;
import com.joshdrummond.webpasswordsafe.server.dao.UserLockoutDAO;
import com.joshdrummond.webpasswordsafe.server.plugin.audit.AuditLogger;


/**
 * @author Josh Drummond
 *
 */
public class UserLockoutAuthenticator implements Authenticator
{
    private static Logger LOG = Logger.getLogger(UserLockoutAuthenticator.class);
    @Resource
    private UserDAO userDAO;
    @Resource
    private UserLockoutDAO userLockoutDAO;
    @Resource
    private AuditLogger auditLogger;
    private Authenticator authenticator;
    private int failedLoginThreshold;

    @Override
    public boolean authenticate(String username, String password)
    {
        boolean isAuthSuccess = false;
        User user = userDAO.findActiveUserByUsername(username);
        if (null != user)
        {
            isAuthSuccess = authenticator.authenticate(username, password);
            if (!isAuthSuccess)
            {
                UserLockout lockout = userLockoutDAO.findByUser(user);
                if (null != lockout)
                {
                    int failCount = lockout.getFailCount() + 1;
                    if (failCount >= failedLoginThreshold)
                    {
                        lockout.setFailCount(0);
                        user.setActiveFlag(false);
                        LOG.debug("UserLockoutAuthenticator: "+username+" is locked out");
                        auditLogger.log(new Date(), username, ServerSessionUtil.getIP(), "lockout", username, true, "user disabled");
                    }
                    else
                    {
                        lockout.setFailCount(failCount);
                    }
                }
                else
                {
                    lockout = new UserLockout(user, 1);
                }
            }
            else
            {
                UserLockout lockout = userLockoutDAO.findByUser(user);
                if (null != lockout)
                {
                    lockout.setFailCount(0);
                }
            }
        }

        LOG.debug("UserLockoutAuthenticator: login success for "+username+"? "+isAuthSuccess);
        return isAuthSuccess;
    }

    public Authenticator getAuthenticator()
    {
        return authenticator;
    }

    public void setAuthenticator(Authenticator authenticator)
    {
        this.authenticator = authenticator;
    }

    public int getFailedLoginThreshold()
    {
        return failedLoginThreshold;
    }

    public void setFailedLoginThreshold(int failedLoginThreshold)
    {
        this.failedLoginThreshold = failedLoginThreshold;
    }

}
