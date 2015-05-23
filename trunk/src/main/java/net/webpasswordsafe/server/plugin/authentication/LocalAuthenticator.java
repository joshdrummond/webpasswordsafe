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
package net.webpasswordsafe.server.plugin.authentication;

import javax.annotation.Resource;
import net.webpasswordsafe.common.model.User;
import net.webpasswordsafe.common.util.Constants.AuthenticationStatus;
import net.webpasswordsafe.server.dao.UserDAO;
import net.webpasswordsafe.server.plugin.encryption.Digester;
import org.apache.log4j.Logger;


/**
 * @author Josh Drummond
 *
 */
public class LocalAuthenticator implements Authenticator
{
    @Resource
    private UserDAO userDAO;
    @Resource
    private Digester digester;
    private static Logger LOG = Logger.getLogger(LocalAuthenticator.class);

    @Override
    public AuthenticationStatus authenticate(String principal, String[] credentials)
    {
        AuthenticationStatus authStatus = AuthenticationStatus.FAILURE;
        User user = userDAO.findActiveUserByUsername(principal);
        if (null != user)
        {
            authStatus = digester.check(credentials[0], user.getAuthnPasswordValue()) ? AuthenticationStatus.SUCCESS : AuthenticationStatus.FAILURE;
        }
        LOG.debug("LocalAuthenticator: login success for "+principal+"? "+authStatus.name());
        return authStatus;
    }

}
