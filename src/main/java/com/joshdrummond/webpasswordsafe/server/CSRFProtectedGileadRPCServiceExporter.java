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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.gwtwidgets.server.spring.gilead.GileadRPCServiceExporter;
import com.google.gwt.user.client.rpc.SerializationException;
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
    
    @Override
    protected void onBeforeRequestDeserialized(String serializedRequest)
    {
        if (isStrongCsrfProtection)
        {
            HttpServletRequest servletRequest = getThreadLocalRequest();
            HttpSession session = servletRequest.getSession(false);
            // If there is currently no session or if the client doesn't know about it yet then don't check.
            // Otherwise the client must provide the id of the session in a header.
            if (session != null && !session.isNew()) {
                String sessionId = servletRequest.getHeader(Constants.HEADER_KEY_CSRF_TOKEN);
                if (sessionId == null || !sessionId.equals(servletRequest.getSession().getId())) {
                    throw new SecurityException(
                        "Blocked request without session header (CSRF attack?)");
                }
            }
        }

        super.onBeforeRequestDeserialized(serializedRequest);
    }

    @Override
    public String processCall(String payload) throws SerializationException
    {
        checkPermutationStrongName();
        return super.processCall(payload);
    }

    public void setStrongCsrfProtection(boolean isStrongCsrfProtection)
    {
        this.isStrongCsrfProtection = isStrongCsrfProtection;
    }
    
}
