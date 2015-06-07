/*
    Copyright 2010-2015 Josh Drummond

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

import net.webpasswordsafe.common.util.Constants.AuthenticationStatus;
import org.apache.log4j.Logger;
import com.rsa.authagent.authapi.AuthSession;
import com.rsa.authagent.authapi.AuthSessionFactory;


/**
 * @author Josh Drummond
 *
 */
public class RsaSecurIdAuthenticator implements Authenticator
{
    private static Logger LOG = Logger.getLogger(RsaSecurIdAuthenticator.class);
    private String configPath;

    @Override
    public AuthenticationStatus authenticate(String principal, String[] credentials)
    {
        AuthenticationStatus authStatus = AuthenticationStatus.FAILURE;
        try
        {
            AuthSessionFactory api = AuthSessionFactory.getInstance(configPath);
            AuthSession authSession = api.createUserSession();
            int status = authSession.check(principal, credentials[0]);
            authSession.close();
            api.shutdown();
            authStatus = (status == AuthSession.ACCESS_OK) ? AuthenticationStatus.SUCCESS : AuthenticationStatus.FAILURE;
        }
        catch (Exception e)
        {
            // an exception is expected when bad credentials are used
            LOG.debug("rsa securid error authenticating: "+ e.getMessage());
            authStatus = AuthenticationStatus.FAILURE;
        }
        LOG.debug("RsaSecurIdAuthenticator: login success for "+principal+"? "+authStatus.name());
        return authStatus;
    }

    public String getConfigPath()
    {
        return configPath;
    }

    public void setConfigPath(String configPath)
    {
        this.configPath = configPath;
    }

}
