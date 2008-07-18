/*
    Copyright 2008 Josh Drummond

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

import org.apache.log4j.Logger;
import org.gwtwidgets.server.spring.ServletUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.joshdrummond.webpasswordsafe.client.remote.LoginService;
import com.joshdrummond.webpasswordsafe.server.authentication.Authenticator;

@Transactional
public class LoginServiceImpl implements LoginService {
    private static Logger LOG = Logger.getLogger(LoginServiceImpl.class);
    private static final long serialVersionUID = 7281742835626324457L;
    private Authenticator authenticator;

    /**
     * @return the authenticator
     */
    public Authenticator getAuthenticator()
    {
        return this.authenticator;
    }

    /**
     * @param authenticator the authenticator to set
     */
    public void setAuthenticator(Authenticator authenticator)
    {
        this.authenticator = authenticator;
    }

    /* (non-Javadoc)
     * @see com.joshdrummond.webpasswordsafe.client.LoginService#getLogin()
     */
    public String getLogin()
    {
        String username = (String)ServletUtils.getRequest().getSession().getAttribute("username");
        return (null == username) ? "" : username;
    }

    /* (non-Javadoc)
     * @see com.joshdrummond.webpasswordsafe.client.LoginService#login(java.lang.String, java.lang.String)
     */
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public boolean login(String username, String password)
    {
        boolean isValidLogin = false;
        if (authenticator.authenticate(username, password))
        {
            LOG.info("login succeeded for "+username);
            isValidLogin = true;
            ServletUtils.getRequest().getSession().setAttribute("username", username);
        }
        else
        {
            LOG.info("login failed for "+username);
        }
        return isValidLogin;
    }

    /* (non-Javadoc)
     * @see com.joshdrummond.webpasswordsafe.client.LoginService#logout()
     */
    public boolean logout()
    {
        ServletUtils.getRequest().getSession().removeAttribute("username");
        return true;
    }
}
