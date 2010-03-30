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

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
import com.joshdrummond.webpasswordsafe.common.util.Constants.Function;
import com.joshdrummond.webpasswordsafe.common.util.Constants.Role;
import com.joshdrummond.webpasswordsafe.server.dao.UserDAO;
import com.joshdrummond.webpasswordsafe.server.plugin.audit.AuditLogger;
import com.joshdrummond.webpasswordsafe.server.plugin.authentication.Authenticator;
import com.joshdrummond.webpasswordsafe.server.plugin.authentication.RoleRetriever;
import com.joshdrummond.webpasswordsafe.server.plugin.authorization.Authorizer;


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
    
    @Autowired
    private Authorizer authorizer;

    private static ThreadLocal<String> usernameRef = new ThreadLocal<String>();
    private static ThreadLocal<Set<Role>> rolesRef = new ThreadLocal<Set<Role>>();
    
    /* (non-Javadoc)
     * @see com.joshdrummond.webpasswordsafe.client.LoginService#getLogin()
     */
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public User getLogin()
    {
        String username = getUsername();
        User user = userDAO.findActiveUserByUsername(username);
        if (null != user)
        {
        	if (ServletUtils.getRequest() != null)
        	{
        		rolesRef.set((Set<Constants.Role>)ServletUtils.getRequest().getSession().getAttribute(Constants.SESSION_KEY_ROLES));
        	}
            user.setRoles(rolesRef.get());
        }
        LOG.info("logged in user="+((null==user) ? "null":user.getUsername()));
        return user;
    }

    /* (non-Javadoc)
     * @see com.joshdrummond.webpasswordsafe.client.LoginService#login(java.lang.String, java.lang.String)
     */
    @Override
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
                usernameRef.set(username);
                rolesRef.set(roleRetriever.retrieveRoles(user));
                if (ServletUtils.getRequest() != null)
                {
	                ServletUtils.getRequest().getSession().setAttribute(Constants.SESSION_KEY_USERNAME, usernameRef.get());
	                ServletUtils.getRequest().getSession().setAttribute(Constants.SESSION_KEY_ROLES, rolesRef.get());
                }
            }
        }
        auditLogger.log(username+" login "+ (isValidLogin ? "success" : "failure"));
        return isValidLogin;
    }

    /* (non-Javadoc)
     * @see com.joshdrummond.webpasswordsafe.client.LoginService#logout()
     */
    @Override
    public boolean logout()
    {
        auditLogger.log("logout user "+ getUsername());
        if (ServletUtils.getRequest() != null)
        {
	        ServletUtils.getRequest().getSession().removeAttribute(Constants.SESSION_KEY_USERNAME);
	        ServletUtils.getRequest().getSession().removeAttribute(Constants.SESSION_KEY_ROLES);
        }
        usernameRef.set(null);
        rolesRef.set(null);
        return true;
    }
    
    /**
     * Grabs the username from the current security session, or null if doesn't exist
     * 
     */
    private String getUsername()
    {
    	if (ServletUtils.getRequest() != null)
    	{
    		usernameRef.set((String)ServletUtils.getRequest().getSession().getAttribute(Constants.SESSION_KEY_USERNAME));
    	}
        return usernameRef.get();
    }

    /* (non-Javadoc)
     * @see com.joshdrummond.webpasswordsafe.client.remote.LoginService#getLoginAuthorizations(java.util.Set)
     */
    @Override
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public Map<Function, Boolean> getLoginAuthorizations(Set<Function> functions)
    {
    	LOG.debug("inside getLoginAuthorizations");
        User loggedInUser = getLogin();
        // if passed null, load everything
        if (null==functions)
        {
        	LOG.debug("functions was passed null");
        	functions = new HashSet<Function>(Arrays.asList(Function.values()));
        }
        LOG.debug("functions="+functions.toString());
        Map<Function, Boolean> authzMap = new HashMap<Function, Boolean>(functions.size());
        for (Function function : functions)
        {
            authzMap.put(function, authorizer.isAuthorized(loggedInUser, function));
        }
        LOG.debug("authzMap="+authzMap.toString());
        return authzMap;
    }
}
