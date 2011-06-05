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
import net.webpasswordsafe.common.model.IPLockout;
import net.webpasswordsafe.server.ServerSessionUtil;
import net.webpasswordsafe.server.dao.IPLockoutDAO;
import net.webpasswordsafe.server.plugin.audit.AuditLogger;
import org.apache.log4j.Logger;


/**
 * @author Josh Drummond
 *
 */
public class IPLockoutAuthenticator implements Authenticator
{
    private static Logger LOG = Logger.getLogger(IPLockoutAuthenticator.class);
    @Resource
    private IPLockoutDAO ipLockoutDAO;
    @Resource
    private AuditLogger auditLogger;
    private Authenticator authenticator;
    private int lockoutLength;
    private int failedLoginThreshold;
    private Set<String> whitelist;

    @Override
    public boolean authenticate(String username, String password)
    {
        boolean isAuthSuccess = false;
        boolean isLockedOut = false;
        Date dateNow = new Date();
        String ipaddress = ServerSessionUtil.getIP();
        IPLockout lockout = ipLockoutDAO.findByIP(ipaddress);
        if (!isWhitelistIP(ipaddress))
        {
            if ((null != lockout) && (null != lockout.getLockoutDate()))
            {
                isLockedOut = true;
                Date endLockout = new Date(lockout.getLockoutDate().getTime() + (lockoutLength * 60000));
                if (dateNow.getTime() > endLockout.getTime())
                {
                    isLockedOut = false;
                    lockout.setLockoutDate(null);
                }
            }
        }
        
        if (!isLockedOut)
        {
            isAuthSuccess = authenticator.authenticate(username, password);
            if (!isWhitelistIP(ipaddress))
            {
                if (!isAuthSuccess)
                {
                    lockout = (null == lockout) ? new IPLockout(ipaddress, 0) : lockout;
                    int failCount = lockout.getFailCount() + 1;
                    if (failCount >= failedLoginThreshold)
                    {
                        lockout.setFailCount(0);
                        lockout.setLockoutDate(dateNow);
                        LOG.debug("IPLockoutAuthenticator: "+ipaddress+" is locked out");
                        auditLogger.log(dateNow, username, ipaddress, "lockout", ipaddress, true, "IP blocked");
                    }
                    else
                    {
                        lockout.setFailCount(failCount);
                    }
                    ipLockoutDAO.makePersistent(lockout);
                }
                else
                {
                    if (null != lockout)
                    {
                        lockout.setFailCount(0);
                    }
                }
            }
        }

        LOG.debug("IPLockoutAuthenticator: login success for "+username+"? "+isAuthSuccess);
        return isAuthSuccess;
    }
    
    private boolean isWhitelistIP(String ipaddress)
    {
        return whitelist.contains(ipaddress);
    }

    public Authenticator getAuthenticator()
    {
        return authenticator;
    }

    public void setAuthenticator(Authenticator authenticator)
    {
        this.authenticator = authenticator;
    }

    public int getLockoutLength()
    {
        return lockoutLength;
    }

    public void setLockoutLength(int lockoutLength)
    {
        this.lockoutLength = lockoutLength;
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
