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
package net.webpasswordsafe.server.plugin.authentication;

import java.util.Date;
import java.util.Set;
import javax.annotation.Resource;
import net.webpasswordsafe.common.model.User;
import net.webpasswordsafe.common.model.UserLockout;
import net.webpasswordsafe.server.ServerSessionUtil;
import net.webpasswordsafe.server.dao.UserDAO;
import net.webpasswordsafe.server.dao.UserLockoutDAO;
import net.webpasswordsafe.server.plugin.audit.AuditLogger;
import org.apache.log4j.Logger;


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
    private Set<String> whitelist;

    @Override
    public boolean authenticate(String username, String password)
    {
        boolean isAuthSuccess = false;
        User user = userDAO.findActiveUserByUsername(username);
        if (null != user)
        {
            isAuthSuccess = authenticator.authenticate(username, password);
            if (!isWhitelistUser(username))
            {
                if (!isAuthSuccess)
                {
                    UserLockout lockout = userLockoutDAO.findByUser(user);
                    lockout = (null == lockout) ? new UserLockout(user, 0) : lockout;
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
                    userLockoutDAO.makePersistent(lockout);
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
        }

        LOG.debug("UserLockoutAuthenticator: login success for "+username+"? "+isAuthSuccess);
        return isAuthSuccess;
    }

    private boolean isWhitelistUser(String username)
    {
        return whitelist.contains(username);
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

    public Set<String> getWhitelist()
    {
        return whitelist;
    }

    public void setWhitelist(Set<String> whitelist)
    {
        this.whitelist = whitelist;
    }

}
