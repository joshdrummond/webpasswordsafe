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
package net.webpasswordsafe.server.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;


/**
 * Servlet Filter that adds CSP headers to the HTTP response
 * 
 * @author Josh Drummond
 *
 */
public class ContentSecurityPolicyFilter implements Filter
{

    @Override
    public void init(FilterConfig config) throws ServletException
    {
    }

    @Override
    public void destroy()
    {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException
    {
        HttpServletResponse httpResponse = ((HttpServletResponse) response);
        // GWT/GXT compiled code still uses inline script/style and eval :(
        String policy = "default-src 'none'; script-src 'self' 'unsafe-inline' 'unsafe-eval'; connect-src 'self'; img-src 'self'; frame-src 'self'; style-src 'self' 'unsafe-inline'";
        // Chrome
        httpResponse.addHeader("Content-Security-Policy", policy);
        // Safari
        httpResponse.addHeader("X-WebKit-CSP", policy);
        // Firefox, IE
        httpResponse.addHeader("X-Content-Security-Policy", "default-src 'self' data:; options inline-script eval-script");
        chain.doFilter(request, response);
    }

}
