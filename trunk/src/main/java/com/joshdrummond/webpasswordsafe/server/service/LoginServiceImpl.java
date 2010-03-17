/*
    Copyright 2008-2010 Josh Drummond

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
package com.joshdrummond.webpasswordsafe.server.service;

import java.util.Date;
import java.util.Set;
import org.apache.log4j.Logger;
import org.gwtwidgets.server.spring.ServletUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.joshdrummond.webpasswordsafe.client.remote.LoginService;
import com.joshdrummond.webpasswordsafe.common.model.User;
import com.joshdrummond.webpasswordsafe.common.util.Constants;
import com.joshdrummond.webpasswordsafe.server.dao.UserDAO;
import com.joshdrummond.webpasswordsafe.server.plugin.audit.AuditLogger;
import com.joshdrummond.webpasswordsafe.server.plugin.authentication.Authenticator;
import com.joshdrummond.webpasswordsafe.server.plugin.authentication.RoleRetriever;


/**
 * Implementation of Login Service
 * 
 * @author Josh Drummond
 *
 */
@Service("loginService")
public class LoginServiceImpl implements LoginService {
	
    private static Logger LOG = Logger.getLogger(LoginServiceImpl.class);
    private static final long serialVersionUID = 7281742835626324457L;
    
    @Autowired
    private Authenticator authenticator;
    
    @Autowired
    private UserDAO userDAO;
    
    @Autowired
    private AuditLogger auditLogger;
    
    @Autowired
    private RoleRetriever roleRetriever;

    
    /* (non-Javadoc)
     * @see com.joshdrummond.webpasswordsafe.client.LoginService#getLogin()
     */
    @SuppressWarnings("unchecked")
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public User getLogin()
    {
        String username = getUsername();
        User user = userDAO.findActiveUserByUsername(username);
        if (null != user)
        {
            user.setRoles((Set<Constants.Role>)ServletUtils.getRequest().getSession().getAttribute(Constants.SESSION_KEY_ROLES));
        }
        LOG.info("logged in user="+((null==user) ? "null":user.getUsername()));
        return user;
    }

    /* (non-Javadoc)
     * @see com.joshdrummond.webpasswordsafe.client.LoginService#login(java.lang.String, java.lang.String)
     */
    @Transactional(propagation=Propagation.REQUIRED)
    public boolean login(String username, String password)
    {
        boolean isValidLogin = false;
        if (authenticator.authenticate(username, password))
        {
            User user = userDAO.findActiveUserByUsername(username);
            if (null != user)
            {
                isValidLogin = true;
                user.setLastLogin(new Date());
                userDAO.makePersistent(user);
                ServletUtils.getRequest().getSession().setAttribute(Constants.SESSION_KEY_USERNAME, username);
                ServletUtils.getRequest().getSession().setAttribute(Constants.SESSION_KEY_ROLES, roleRetriever.retrieveRoles(user));
            }
        }
        auditLogger.log(username+" login "+ (isValidLogin ? "success" : "failure"));
        return isValidLogin;
    }

    /* (non-Javadoc)
     * @see com.joshdrummond.webpasswordsafe.client.LoginService#logout()
     */
    public boolean logout()
    {
        auditLogger.log("logout user "+ getUsername());
        ServletUtils.getRequest().getSession().removeAttribute(Constants.SESSION_KEY_USERNAME);
        ServletUtils.getRequest().getSession().removeAttribute(Constants.SESSION_KEY_ROLES);
        return true;
    }
    
    /**
     * Grabs the username from the current security session, or null if doesn't exist
     * 
     */
    private String getUsername()
    {
        return (String)ServletUtils.getRequest().getSession().getAttribute(Constants.SESSION_KEY_USERNAME);
    }
}
