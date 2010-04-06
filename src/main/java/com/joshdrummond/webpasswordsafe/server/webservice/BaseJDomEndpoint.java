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

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.ws.server.endpoint.AbstractJDomPayloadEndpoint;
import org.springframework.ws.transport.context.TransportContextHolder;
import org.springframework.ws.transport.http.HttpServletConnection;
import com.joshdrummond.webpasswordsafe.client.remote.LoginService;
import com.joshdrummond.webpasswordsafe.server.ServerSessionUtil;


/**
 * Base abstract class for all web services in WebPasswordSafe
 * 
 * @author Josh Drummond
 *
 */
public abstract class BaseJDomEndpoint extends AbstractJDomPayloadEndpoint
    implements InitializingBean
{
    protected Namespace namespace;
    protected LoginService loginService;
    protected XPath authnUsernameXPath, authnPasswordXPath; 

    public void setLoginService(LoginService loginService)
    {
        this.loginService = loginService;
    }

    protected String extractAuthnUsernameFromRequest(Element element)
        throws JDOMException
    {
        return authnUsernameXPath.valueOf(element);
    }
    
    protected String extractAuthnPasswordFromRequest(Element element)
        throws JDOMException
    {
        return authnPasswordXPath.valueOf(element);
    }

    protected void setIPAddress()
    {
        ServerSessionUtil.setIP(((HttpServletConnection)TransportContextHolder.getTransportContext().
                getConnection()).getHttpServletRequest().getRemoteAddr());
    }

    protected Element createBaseResponse(String responseName, boolean isSuccess, String message)
    {
        Element responseElement = new Element(responseName, namespace);
        responseElement.addContent(new Element("success", namespace).setText(String.valueOf(isSuccess))); 
        responseElement.addContent(new Element("message", namespace).setText(message)); 
        return responseElement;  
    }
    
    protected void afterPropertiesSetBase(String requestName) throws Exception
    {
        namespace = Namespace.getNamespace("wps", "http://www.joshdrummond.com/webpasswordsafe/schemas"); 
        authnUsernameXPath = XPath.newInstance("/wps:"+requestName+"/wps:authnUsername"); 
        authnUsernameXPath.addNamespace(namespace);
        authnPasswordXPath = XPath.newInstance("/wps:"+requestName+"/wps:authnPassword");
        authnPasswordXPath.addNamespace(namespace);
    }

}
