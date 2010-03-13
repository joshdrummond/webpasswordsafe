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
import org.apache.log4j.Logger;
import org.gwtwidgets.server.spring.ServletUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.context.HttpSessionContextIntegrationFilter;
import org.springframework.security.context.SecurityContext;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.joshdrummond.webpasswordsafe.client.remote.LoginService;
import com.joshdrummond.webpasswordsafe.common.model.User;
import com.joshdrummond.webpasswordsafe.server.dao.UserDAO;
import com.joshdrummond.webpasswordsafe.server.plugin.audit.AuditLogger;
import com.joshdrummond.webpasswordsafe.server.plugin.authentication.Authenticator;


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

    
    /* (non-Javadoc)
     * @see com.joshdrummond.webpasswordsafe.client.LoginService#getLogin()
     */
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public User getLogin()
    {
        String username = getUsername();
        User user = userDAO.findActiveUserByUsername(username);
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
                try
                {
                    GrantedAuthority[] roles = new GrantedAuthority[] { new GrantedAuthorityImpl("ROLE_USER") };
                    Authentication authUser = new UsernamePasswordAuthenticationToken(username, null, roles);
                    SecurityContextHolder.getContext().setAuthentication(authUser);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                ServletUtils.getRequest().getSession().setAttribute(HttpSessionContextIntegrationFilter.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
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
        SecurityContextHolder.getContext().setAuthentication(null);
        ServletUtils.getRequest().getSession().removeAttribute(HttpSessionContextIntegrationFilter.SPRING_SECURITY_CONTEXT_KEY);
        return true;
    }
    
    /**
     * Grabs the username from the current security session, or null if doesn't exist
     * 
     */
    private String getUsername()
    {
        String username = null;
        SecurityContext securityContext = (SecurityContext)ServletUtils.getRequest().getSession().getAttribute(HttpSessionContextIntegrationFilter.SPRING_SECURITY_CONTEXT_KEY);
        if (null != securityContext)
        {
            Authentication authUser = securityContext.getAuthentication();
            if (null != authUser)
            {
                username = authUser.getPrincipal().toString();
            }
        }
        return username;
    }
}
