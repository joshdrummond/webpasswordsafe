/*
    Copyright 2008-2013 Josh Drummond

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
package net.webpasswordsafe.server.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import net.webpasswordsafe.client.remote.LoginService;
import net.webpasswordsafe.common.model.User;
import net.webpasswordsafe.common.util.Constants;
import net.webpasswordsafe.common.util.Constants.Function;
import net.webpasswordsafe.server.ServerSessionUtil;
import net.webpasswordsafe.server.dao.UserDAO;
import net.webpasswordsafe.server.plugin.audit.AuditLogger;
import net.webpasswordsafe.server.plugin.authentication.Authenticator;
import net.webpasswordsafe.server.plugin.authentication.RoleRetriever;
import net.webpasswordsafe.server.plugin.authorization.Authorizer;
import net.webpasswordsafe.server.report.ReportConfig;
import net.webpasswordsafe.server.service.helper.WPSXsrfProtectedServiceServlet;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * Implementation of Login Service
 * 
 * @author Josh Drummond
 *
 */
@Service("loginService")
public class LoginServiceImpl extends WPSXsrfProtectedServiceServlet implements LoginService
{
    private static final long serialVersionUID = 185624826328067937L;
    private static Logger LOG = Logger.getLogger(LoginServiceImpl.class);
    
    @Resource
    private Authenticator authenticator;
    
    @Autowired
    private UserDAO userDAO;
    
    @Autowired
    private ReportConfig reportConfig;

    @Resource
    private AuditLogger auditLogger;
    
    @Resource
    private RoleRetriever roleRetriever;
    
    @Resource
    private Authorizer authorizer;

    
    /* (non-Javadoc)
     * @see net.webpasswordsafe.client.LoginService#getLogin()
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
     * @see net.webpasswordsafe.client.LoginService#login(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public boolean login(String username, String password)
    {
        boolean isValidLogin = false;
        Date now = new Date();
        String message = "";
        username = trimUsername(username);
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

    private String trimUsername(String username)
    {
        if ((null != username) && (username.length() > User.LENGTH_USERNAME))
        {
            return username.substring(0, User.LENGTH_USERNAME);
        }
        else
        {
            return username;
        }
    }

    /* (non-Javadoc)
     * @see net.webpasswordsafe.client.LoginService#logout()
     */
    @Override
    public boolean logout()
    {
        auditLogger.log(new Date(), ServerSessionUtil.getUsername(), ServerSessionUtil.getIP(), "logout", "", true, "");
        ServerSessionUtil.setUsername(null);
        ServerSessionUtil.setRoles(null);
        ServerSessionUtil.invalidateSession();
        return true;
    }

    /* (non-Javadoc)
     * @see net.webpasswordsafe.client.remote.LoginService#getLoginAuthorizations(java.util.Set)
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
            authzMap.put(function, authorizer.isAuthorized(loggedInUser, function.name()));
        }
        LOG.debug("authzMap="+authzMap.toString());
        return authzMap;
    }

    @Override
    public boolean ping()
    {
        ServerSessionUtil.initCsrfSession();
        return true;
    }
    
    @Override
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public List<Map<String, Object>> getLoginReports()
    {
        LOG.debug("inside getLoginReports");
        User loggedInUser = getLogin();
        List<Map<String, Object>> reportList = new ArrayList<Map<String,Object>>();
        for (Map<String, Object> report : reportConfig.getReports())
        {
            if (authorizer.isAuthorized(loggedInUser, Constants.VIEW_REPORT_PREFIX+(String)report.get(Constants.NAME)))
            {
                reportList.add(report);
            }
        }
        return reportList;
    }

}
