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
package com.joshdrummond.webpasswordsafe.server;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.gwtwidgets.server.spring.gilead.GileadRPCServiceExporter;
import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;
import com.joshdrummond.webpasswordsafe.common.util.Constants;


/**
 * Wrapper to guard against CSRF attacks while using GWT-SL RPC exporter
 * 
 * @author Josh Drummond
 *
 */
public class CSRFProtectedGileadRPCServiceExporter extends GileadRPCServiceExporter
{
    private static final long serialVersionUID = 1L;
    private boolean isStrongCsrfProtection = true;
    private static Logger LOG = Logger.getLogger(CSRFProtectedGileadRPCServiceExporter.class);

    @Override
    protected void onBeforeRequestDeserialized(String serializedRequest)
    {
        if (isStrongCsrfProtection)
        {
            HttpServletRequest servletRequest = getThreadLocalRequest();
            HttpSession session = servletRequest.getSession(false);
            // If there is currently no session or if the client doesn't know about it yet then don't check.
            // Otherwise the client must provide the id of the session in a header.
            LOG.debug("session="+session);
            if (session != null)
            {
                if (session.isNew() || (session.getAttribute(Constants.CSRF_TOKEN_KEY) == null))
                {
                    // either new session or old session without csrf token set, so set it but don't check
                    session.setAttribute(Constants.CSRF_TOKEN_KEY, session.getId());
                    Cookie cookie = new Cookie(Constants.CSRF_TOKEN_KEY, session.getId());
                    cookie.setPath(servletRequest.getContextPath());
                    getThreadLocalResponse().addCookie(cookie);
                }
                else
                {
                    // session exists, and csrf token was set, so client should know about it, thus enforce it
                    String clientCsrfToken = servletRequest.getHeader(Constants.CSRF_TOKEN_KEY);
                    String serverCsrfToken = (String)session.getAttribute(Constants.CSRF_TOKEN_KEY);
                    LOG.debug("clientCsrfToken="+clientCsrfToken);
                    LOG.debug("serverCsrfToken="+serverCsrfToken);
                    if ((clientCsrfToken == null) || !clientCsrfToken.equals(serverCsrfToken)) {
                        throw new SecurityException(
                            "Blocked request without session header (CSRF attack?)");
                    }
                }
            }
        }

        super.onBeforeRequestDeserialized(serializedRequest);
    }

    @Override
    public String processCall(String payload) throws SerializationException
    {
        //LOG.debug("payload="+payload);
        checkPermutationStrongName();
        String response = super.processCall(payload);
        //LOG.debug("response="+response);
        return response;
    }

    @Override
    protected String handleIncompatibleRemoteServiceException(IncompatibleRemoteServiceException e)
        throws SerializationException {
        logger.error(e.getMessage(), e);
        return RPC.encodeResponseForFailure(null, e);
    }

    public void setStrongCsrfProtection(boolean isStrongCsrfProtection)
    {
        this.isStrongCsrfProtection = isStrongCsrfProtection;
    }
    
}
