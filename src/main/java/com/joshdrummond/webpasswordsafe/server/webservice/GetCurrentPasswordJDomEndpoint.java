/*
    Copyright 2010 Josh Drummond

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
import org.jdom.Namespace;
import org.jdom.xpath.XPath;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.ws.server.endpoint.AbstractJDomPayloadEndpoint;
import com.joshdrummond.webpasswordsafe.client.remote.LoginService;
import com.joshdrummond.webpasswordsafe.client.remote.PasswordService;
import com.joshdrummond.webpasswordsafe.common.model.Password;


/**
 * 
 * @author Josh Drummond
 *
 */
public class GetCurrentPasswordJDomEndpoint extends AbstractJDomPayloadEndpoint
    implements InitializingBean
{
    private static Logger LOG = Logger.getLogger(GetCurrentPasswordJDomEndpoint.class);
    private LoginService loginService;
    private PasswordService passwordService;
    private Namespace namespace; 
    private XPath authnUsernameXPath, authnPasswordXPath, passwordNameXPath; 

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
            String passwordName = extractPasswordNameFromRequest(element);
            boolean isSuccess = false;
            String message = "";
            String currentPassword = "";
            try
            {
                boolean isAuthnValid = loginService.login(authnUsername, authnPassword);
                if (isAuthnValid)
                {
                    Password password = passwordService.getPassword(passwordName);
                    if (password != null)
                    {
                        currentPassword = passwordService.getCurrentPassword(password.getId());
                        isSuccess = true;
                    }
                    else
                    {
                        message = "Password not found";
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
            returnDoc = createResponse(isSuccess, message, currentPassword);
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
    private Element createResponse(boolean isSuccess, String message, String currentPassword)
    {
        Element responseElement = new Element("GetCurrentPasswordResponse", namespace);
        responseElement.addContent(new Element("success", namespace).setText(String.valueOf(isSuccess))); 
        responseElement.addContent(new Element("message", namespace).setText(message));
        responseElement.addContent(new Element("password", namespace).setText(currentPassword));
        return responseElement;  
    }

    private String extractAuthnUsernameFromRequest(Element element)
        throws JDOMException
    {
        return authnUsernameXPath.valueOf(element);
    }

    private String extractAuthnPasswordFromRequest(Element element)
        throws JDOMException
    {
        return authnPasswordXPath.valueOf(element);
    }

    private String extractPasswordNameFromRequest(Element element)
        throws JDOMException
    {
        return passwordNameXPath.valueOf(element);
    }

    public void setLoginService(LoginService loginService) {
		this.loginService = loginService;
	}

	public void setPasswordService(PasswordService passwordService) {
		this.passwordService = passwordService;
	}

	/* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception
    {
        namespace = Namespace.getNamespace("wps", "http://www.joshdrummond.com/webpasswordsafe/schemas"); 
        authnUsernameXPath = XPath.newInstance("/wps:GetCurrentPasswordRequest/wps:authnUsername"); 
        authnUsernameXPath.addNamespace(namespace);
        authnPasswordXPath = XPath.newInstance("/wps:GetCurrentPasswordRequest/wps:authnPassword");
        authnPasswordXPath.addNamespace(namespace);
        passwordNameXPath = XPath.newInstance("/wps:GetCurrentPasswordRequest/wps:passwordName");
        passwordNameXPath.addNamespace(namespace);
    }

}
