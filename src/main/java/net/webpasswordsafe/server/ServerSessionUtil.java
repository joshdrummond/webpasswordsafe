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
package net.webpasswordsafe.server;

import java.util.Set;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.webpasswordsafe.common.util.Constants;
import net.webpasswordsafe.common.util.Constants.Role;
import org.gwtwidgets.server.spring.ServletUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


/**
 * Utilities to access current server session information
 * 
 * @author Josh Drummond
 *
 */
public class ServerSessionUtil
{
    private static ThreadLocal<String> usernameRef = new ThreadLocal<String>();
    private static ThreadLocal<Set<Role>> rolesRef = new ThreadLocal<Set<Role>>();
    private static ThreadLocal<String> ipRef = new ThreadLocal<String>();
    
    public static void invalidateSession()
    {
        if (getRequest() != null)
        {
            getRequest().getSession().invalidate();
        }
    }

    public static String getUsername()
    {
        if (getRequest() != null)
        {
            usernameRef.set((String)getRequest().getSession().getAttribute(Constants.SESSION_KEY_USERNAME));
        }
        return usernameRef.get();
    }
    
    @SuppressWarnings("unchecked")
    public static Set<Role> getRoles()
    {
        if (getRequest() != null)
        {
            rolesRef.set((Set<Role>)getRequest().getSession().getAttribute(Constants.SESSION_KEY_ROLES));
        }
        return rolesRef.get();
    }
    
    public static String getIP()
    {
        if (getRequest() != null)
        {
            ipRef.set(getRequest().getRemoteAddr());
        }
        return ipRef.get();
    }
    
    public static void setIP(String ip)
    {
        ipRef.set(ip);
    }
    
    public static void setUsername(String username)
    {
        usernameRef.set(username);
        if (getRequest() != null)
        {
            if (username != null)
            {
                getRequest().getSession().setAttribute(Constants.SESSION_KEY_USERNAME, usernameRef.get());
            }
            else
            {
                getRequest().getSession().removeAttribute(Constants.SESSION_KEY_USERNAME);
            }
        }
    }
    
    public static void setRoles(Set<Role> roles)
    {
        rolesRef.set(roles);
        if (getRequest() != null)
        {
            if (roles != null)
            {
                getRequest().getSession().setAttribute(Constants.SESSION_KEY_ROLES, rolesRef.get());
            }
            else
            {
                getRequest().getSession().removeAttribute(Constants.SESSION_KEY_ROLES);
            }
        }
    }
    
    public static void initCsrfSession()
    {
        HttpSession session = getRequest().getSession(false);
        if (session.isNew() || (session.getAttribute(Constants.CSRF_TOKEN_KEY) == null))
        {
            // either new session or old session without csrf token set, so set it
            session.setAttribute(Constants.CSRF_TOKEN_KEY, session.getId());
            Cookie cookie = new Cookie(Constants.CSRF_TOKEN_KEY, session.getId());
            cookie.setPath("".equals(getRequest().getContextPath()) ? "/" : getRequest().getContextPath());
            getResponse().addCookie(cookie);
        }
    }
    
    public static HttpServletRequest getRequest()
    {
    	return ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
    }
    
    public static HttpServletResponse getResponse()
    {
    	return ServletUtils.getResponse();
    }

}
