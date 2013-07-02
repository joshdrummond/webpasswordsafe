/*
    Copyright 2013 Josh Drummond

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
package net.webpasswordsafe.server.service.helper;

import javax.servlet.http.HttpServletRequest;
import com.google.gwt.user.server.rpc.SerializationPolicy;
import com.google.gwt.user.server.rpc.XsrfTokenServiceServlet;


/**
 * Override method from RemoteServiceServlet
 * 
 * @author Josh Drummond
 *
 */
public class WPSXsrfTokenServiceServlet extends XsrfTokenServiceServlet
{
    private static final long serialVersionUID = -8894151339990646734L;

    /**
     * Attempt to load the RPC serialization policy normally. If it isn't found,
     * try loading it using the context path instead of the URL.
     */
    @Override
    protected SerializationPolicy doGetSerializationPolicy(HttpServletRequest request, String moduleBaseURL, String strongName)
    {
        SerializationPolicy policy = super.doGetSerializationPolicy(request, moduleBaseURL, strongName);
        if(policy == null) {
            return SerializationPolicyUtil.loadSerializationPolicy(this, request, moduleBaseURL, strongName);
        } else {
            return policy;
        }
    }
}
