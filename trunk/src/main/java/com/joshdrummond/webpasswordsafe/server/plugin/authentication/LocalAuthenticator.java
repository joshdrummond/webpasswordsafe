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
package com.joshdrummond.webpasswordsafe.server.plugin.authentication;

import com.joshdrummond.webpasswordsafe.common.model.User;
import com.joshdrummond.webpasswordsafe.server.dao.UserDAO;
import com.joshdrummond.webpasswordsafe.server.plugin.encryption.Digester;


/**
 * @author Josh Drummond
 *
 */
public class LocalAuthenticator implements Authenticator
{
    private UserDAO userDAO;
    private Digester digester;
    
    /* (non-Javadoc)
     * @see com.joshdrummond.webpasswordsafe.server.Authenticator#authenticate(java.lang.String, java.lang.String)
     */
    @Override
    public boolean authenticate(String username, String password)
    {
        boolean isValid = false;
        User user = userDAO.findActiveUserByUsername(username);
        if (null != user)
        {
            isValid = digester.check(password, user.getAuthnPasswordValue());
        }
        return isValid;
    }

    public UserDAO getUserDAO()
    {
        return this.userDAO;
    }

    public void setUserDAO(UserDAO userDAO)
    {
        this.userDAO = userDAO;
    }

    public Digester getDigester()
    {
        return this.digester;
    }

    public void setDigester(Digester digester)
    {
        this.digester = digester;
    }

}
