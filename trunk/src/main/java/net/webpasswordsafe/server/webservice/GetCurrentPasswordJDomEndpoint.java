/*
    Copyright 2010-2013 Josh Drummond

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
package net.webpasswordsafe.server.webservice;

import net.webpasswordsafe.client.remote.PasswordService;
import net.webpasswordsafe.common.model.Password;
import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;


/**
 * GetCurrentPassword web service
 * 
 * @author Josh Drummond
 *
 */
@Endpoint
public class GetCurrentPasswordJDomEndpoint extends BaseJDomEndpoint
{
    private static Logger LOG = Logger.getLogger(GetCurrentPasswordJDomEndpoint.class);
    @Autowired
    private PasswordService passwordService;
    private XPath passwordIdXPath; 
    
    public GetCurrentPasswordJDomEndpoint()
        throws JDOMException
    {
        setXPath();
    }
    
    private void setXPath()
        throws JDOMException
    {
        setBaseXPath("GetCurrentPasswordRequest");
        passwordIdXPath = XPath.newInstance("/wps:GetCurrentPasswordRequest/wps:passwordId");
        passwordIdXPath.addNamespace(namespace);
    }
    
    @PayloadRoot(namespace=NAMESPACE_URI, localPart="GetCurrentPasswordRequest")
    public @ResponsePayload Element handleGetCurrentPasswordRequest(@RequestPayload Element requestDoc)
        throws Exception
    {
        Element returnDoc = null;
        try
        {
            boolean isSuccess = false;
            String message = "";
            String currentPassword = "";
            try
            {
                String authnUsername = extractAuthnUsernameFromRequest(requestDoc);
                String authnPassword = extractAuthnPasswordFromRequest(requestDoc);
                String passwordId = extractPasswordNameFromRequest(requestDoc);
                setIPAddress();
                boolean isAuthnValid = loginService.login(authnUsername, authnPassword);
                if (isAuthnValid)
                {
                    Password password = passwordService.getPassword(Long.valueOf(passwordId));
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

    private Element createResponse(boolean isSuccess, String message, String currentPassword)
    {
        Element responseElement = createBaseResponse("GetCurrentPasswordResponse", isSuccess, message);
        responseElement.addContent(new Element("password", namespace).setText(currentPassword));
        return responseElement;  
    }

    private String extractPasswordNameFromRequest(Element element)
        throws JDOMException
    {
        return passwordIdXPath.valueOf(element);
    }

}
