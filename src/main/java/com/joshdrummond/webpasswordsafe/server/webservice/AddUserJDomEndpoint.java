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
package com.joshdrummond.webpasswordsafe.server.webservice;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import com.joshdrummond.webpasswordsafe.client.remote.UserService;
import com.joshdrummond.webpasswordsafe.common.model.User;


/**
 * AddUser web service
 * 
 * @author Josh Drummond
 *
 */
public class AddUserJDomEndpoint extends BaseJDomEndpoint
{
    private static Logger LOG = Logger.getLogger(AddUserJDomEndpoint.class);
    private UserService userService;
    private XPath usernameXPath, passwordXPath, fullnameXPath, emailXPath, activeXPath;

    /* (non-Javadoc)
     * @see org.springframework.ws.server.endpoint.AbstractJDomPayloadEndpoint#invokeInternal(org.jdom.Element)
     */
    @Override
    protected Element invokeInternal(Element element) throws Exception
    {
        Element returnDoc = null;
        try
        {
            String authnUsername = extractAuthnUsernameFromRequest(element);
            String authnPassword = extractAuthnPasswordFromRequest(element);
            User user = extractUserFromRequest(element);
            boolean isSuccess = false;
            String message = "";
            try
            {
                setIPAddress();
                boolean isAuthnValid = loginService.login(authnUsername, authnPassword);
                if (isAuthnValid)
                {
                    boolean isUserTaken = userService.isUserTaken(user.getUsername());
                    if (!isUserTaken)
                    {
                        userService.addUser(user);
                        isSuccess = true;
                    }
                    else
                    {
                        message = "Username already exists";
                    }
                }
                else
                {
                    message = "Invalid authentication";
                }
                loginService.logout();
            }
            catch (Exception e)
            {
                LOG.error(e.getMessage(), e);
                isSuccess = false;
                message = e.getMessage();
            }
            returnDoc = createResponse(isSuccess, message);
        }
        catch (Exception e)
        {
            LOG.error(e.getMessage(), e);
            throw e;
        }
        return returnDoc;
    }

    /**
     * @param isSuccess
     * @param message
     * @return
     */
    private Element createResponse(boolean isSuccess, String message)
    {
        return createBaseResponse("AddUserResponse", isSuccess, message);
    }

    public void setUserService(UserService userService)
    {
        this.userService = userService;
    }

    /**
     * @param element
     * @return
     */
    private User extractUserFromRequest(Element element)
        throws JDOMException
    {
        User user = new User();
        user.setUsername(usernameXPath.valueOf(element));
        user.setPassword(passwordXPath.valueOf(element));
        user.setFullname(fullnameXPath.valueOf(element));
        user.setEmail(emailXPath.valueOf(element));
        String activeFlag = activeXPath.valueOf(element).trim().toLowerCase();
        user.setActiveFlag(activeFlag.equals("true") || activeFlag.equals("yes") || activeFlag.equals("y"));
        return user;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception
    {
        afterPropertiesSetBase("AddUserRequest");
        usernameXPath = XPath.newInstance("/wps:AddUserRequest/wps:user/wps:username"); 
        usernameXPath.addNamespace(namespace);
        passwordXPath = XPath.newInstance("/wps:AddUserRequest/wps:user/wps:password");
        passwordXPath.addNamespace(namespace);
        fullnameXPath = XPath.newInstance("/wps:AddUserRequest/wps:user/wps:fullname");
        fullnameXPath.addNamespace(namespace);
        emailXPath = XPath.newInstance("/wps:AddUserRequest/wps:user/wps:email");
        emailXPath.addNamespace(namespace);
        activeXPath = XPath.newInstance("/wps:AddUserRequest/wps:user/wps:active");
        activeXPath.addNamespace(namespace);
    }

}
