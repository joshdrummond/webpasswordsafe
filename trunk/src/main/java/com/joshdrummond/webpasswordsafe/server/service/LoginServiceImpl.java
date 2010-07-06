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
import javax.annotation.Resource;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.joshdrummond.webpasswordsafe.client.remote.LoginService;
import com.joshdrummond.webpasswordsafe.common.model.User;
import com.joshdrummond.webpasswordsafe.common.util.Constants.Function;
import com.joshdrummond.webpasswordsafe.server.ServerSessionUtil;
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
    
    @Resource
    private Authenticator authenticator;
    
    @Autowired
    private UserDAO userDAO;
    
    @Resource
    private AuditLogger auditLogger;
    
    @Resource
    private RoleRetriever roleRetriever;
    
    @Resource
    private Authorizer authorizer;

    
    /* (non-Javadoc)
     * @see com.joshdrummond.webpasswordsafe.client.LoginService#getLogin()
     */
    @Override
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public User getLogin()
    {
        String username = ServerSessionUtil.getUsername();
        User user = userDAO.findActiveUserByUsername(username);
        if (null != user)
        {
            user.setRoles(ServerSessionUtil.getRoles());
        }
        LOG.debug("logged in user="+((null==user) ? "null":user.getUsername()));
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
        Date now = new Date();
        String message = "";
        if (authenticator.authenticate(username, password))
        {
            User user = userDAO.findActiveUserByUsername(username);
            if (null != user)
            {
                isValidLogin = true;
                user.setLastLogin(now);
                userDAO.makePersistent(user);
                ServerSessionUtil.setUsername(username);
                ServerSessionUtil.setRoles(roleRetriever.retrieveRoles(user));
            }
            else
            {
                message = "user not found";
            }
        }
        else
        {
            message = "authentication failed";
        }
        auditLogger.log(now, username, ServerSessionUtil.getIP(), "login", "", isValidLogin, message);
        return isValidLogin;
    }

    /* (non-Javadoc)
     * @see com.joshdrummond.webpasswordsafe.client.LoginService#logout()
     */
    @Override
    public boolean logout()
    {
        auditLogger.log(new Date(), ServerSessionUtil.getUsername(), ServerSessionUtil.getIP(), "logout", "", true, "");
        ServerSessionUtil.setUsername(null);
        ServerSessionUtil.setRoles(null);
        return true;
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
