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
package net.webpasswordsafe.server;

import java.util.Set;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import net.webpasswordsafe.common.util.Constants;
import net.webpasswordsafe.common.util.Constants.Role;
import org.gwtwidgets.server.spring.ServletUtils;


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
        if (ServletUtils.getRequest() != null)
        {
            ServletUtils.getRequest().getSession().invalidate();
        }
    }

    public static String getUsername()
    {
        if (ServletUtils.getRequest() != null)
        {
            usernameRef.set((String)ServletUtils.getRequest().getSession().getAttribute(Constants.SESSION_KEY_USERNAME));
        }
        return usernameRef.get();
    }
    
    @SuppressWarnings("unchecked")
    public static Set<Role> getRoles()
    {
        if (ServletUtils.getRequest() != null)
        {
            rolesRef.set((Set<Role>)ServletUtils.getRequest().getSession().getAttribute(Constants.SESSION_KEY_ROLES));
        }
        return rolesRef.get();
    }
    
    public static String getIP()
    {
        if (ServletUtils.getRequest() != null)
        {
            ipRef.set(ServletUtils.getRequest().getRemoteAddr());
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
        if (ServletUtils.getRequest() != null)
        {
            if (username != null)
            {
                ServletUtils.getRequest().getSession().setAttribute(Constants.SESSION_KEY_USERNAME, usernameRef.get());
            }
            else
            {
                ServletUtils.getRequest().getSession().removeAttribute(Constants.SESSION_KEY_USERNAME);
            }
        }
    }
    
    public static void setRoles(Set<Role> roles)
    {
        rolesRef.set(roles);
        if (ServletUtils.getRequest() != null)
        {
            if (roles != null)
            {
                ServletUtils.getRequest().getSession().setAttribute(Constants.SESSION_KEY_ROLES, rolesRef.get());
            }
            else
            {
                ServletUtils.getRequest().getSession().removeAttribute(Constants.SESSION_KEY_ROLES);
            }
        }
    }
    
    public static void initCsrfSession()
    {
        HttpSession session = ServletUtils.getRequest().getSession(false);
        if (session.isNew() || (session.getAttribute(Constants.CSRF_TOKEN_KEY) == null))
        {
            // either new session or old session without csrf token set, so set it
            session.setAttribute(Constants.CSRF_TOKEN_KEY, session.getId());
            Cookie cookie = new Cookie(Constants.CSRF_TOKEN_KEY, session.getId());
            cookie.setPath("".equals(ServletUtils.getRequest().getContextPath()) ? "/" : ServletUtils.getRequest().getContextPath());
            ServletUtils.getResponse().addCookie(cookie);
        }
    }
}
