/*
    Copyright 2015 Josh Drummond

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
package net.webpasswordsafe.server.controller;

import java.net.URLEncoder;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.webpasswordsafe.client.remote.LoginService;
import net.webpasswordsafe.server.plugin.authentication.sso.SsoAuthenticator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * 
 * @author Josh Drummond
 *
 */
@Controller
public class SsoController
{
    @Autowired
    protected LoginService loginService;
    @Resource
    private SsoAuthenticator ssoAuthenticator;

    @RequestMapping(value="/sso", method=RequestMethod.GET)
    public String sso(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap)
    {
        modelMap.addAttribute("status", loginService.checkSsoLogin());
        modelMap.addAttribute("user", ssoAuthenticator.getPrincipal());
        modelMap.addAttribute("baseUrl", getBaseUrl(request));
        return "sso";
    }
    
    @RequestMapping(value="/logout", method=RequestMethod.GET)
    public String logout(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap)
    {
        loginService.logout();
        String baseUrl = getBaseUrl(request);
        modelMap.addAttribute("baseUrl", baseUrl);
        modelMap.addAttribute("logoutUrl", getLogoutUrl(baseUrl));
        return "logout";
    }
    
    private String getBaseUrl(HttpServletRequest request)
    {
        return request.getRequestURL().toString().replace(request.getRequestURI(), request.getContextPath());
    }

    private String getLogoutUrl(String baseUrl)
    {
        String logoutUrl = "";
        try
        {
            logoutUrl = ssoAuthenticator.getLogoutUrl().replace("$1", URLEncoder.encode(baseUrl+"/", "UTF-8"));
        }
        catch (Exception e) {}
        if ("".equals(logoutUrl))
        {
            logoutUrl = baseUrl+"/";
        }
        return logoutUrl;
    }

}
