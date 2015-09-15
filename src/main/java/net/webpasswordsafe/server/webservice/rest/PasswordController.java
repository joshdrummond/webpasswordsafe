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
package net.webpasswordsafe.server.webservice.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import net.webpasswordsafe.client.remote.LoginService;
import net.webpasswordsafe.client.remote.PasswordService;
import net.webpasswordsafe.common.model.AccessLevel;
import net.webpasswordsafe.common.model.Password;
import net.webpasswordsafe.common.model.PasswordData;
import net.webpasswordsafe.common.model.Permission;
import net.webpasswordsafe.common.model.Tag;
import net.webpasswordsafe.common.model.User;
import net.webpasswordsafe.common.util.Constants;
import net.webpasswordsafe.common.util.Constants.AuthenticationStatus;
import net.webpasswordsafe.common.util.Constants.Match;
import net.webpasswordsafe.common.util.Utils;
import net.webpasswordsafe.server.ServerSessionUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;


/**
 * Set of password related REST webservices
 * 
 * @author Josh Drummond
 *
 */
@Controller
public class PasswordController
{
    @Autowired
    private PasswordService passwordService;
    @Autowired
    protected LoginService loginService;
    @Autowired
    private View jsonView;
    private static Logger LOG = Logger.getLogger(PasswordController.class);

    
    @RequestMapping(value = "/passwords", method = RequestMethod.GET)
    public ModelAndView getPasswordList(@RequestParam(value="query",required=false) String query,
            HttpServletRequest request,
            @RequestHeader(Constants.REST_AUTHN_USERNAME) String authnUsername,
            @RequestHeader(Constants.REST_AUTHN_PASSWORD) String authnPassword,
            @RequestHeader(value=Constants.REST_AUTHN_TOTP, required=false) String authnTOTP)
    {
        boolean isSuccess = false;
        String message = "";
        List<Map<String, String>> passwordList = new ArrayList<Map<String, String>>();
        try
        {
            ServerSessionUtil.setIP(request.getRemoteAddr());
            AuthenticationStatus authStatus = loginService.login(authnUsername, Utils.buildCredentials(authnPassword, authnTOTP));
            if (AuthenticationStatus.SUCCESS == authStatus)
            {
                List<Password> results = passwordService.searchPassword(query, true, new HashSet<Tag>(), Match.AND);
                for (Password password : results)
                {
                    Map<String, String> passwordMap = new HashMap<String, String>();
                    passwordMap.put("id", String.valueOf(password.getId()));
                    passwordMap.put("title", password.getName());
                    passwordMap.put("username", password.getUsername());
                    passwordMap.put("notes", password.getNotes());
                    passwordMap.put("tags", password.getTagsAsString());
                    passwordList.add(passwordMap);
                }
                isSuccess = true;
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
        return createModelAndView(isSuccess, message, "passwordList", passwordList);
    }
    
    
    @RequestMapping(value = "/passwords/{passwordId}", method = RequestMethod.GET)
    public ModelAndView getPassword(@PathVariable("passwordId") String passwordId,
            HttpServletRequest request,
            @RequestHeader(Constants.REST_AUTHN_USERNAME) String authnUsername,
            @RequestHeader(Constants.REST_AUTHN_PASSWORD) String authnPassword,
            @RequestHeader(value=Constants.REST_AUTHN_TOTP, required=false) String authnTOTP)
    {
        boolean isSuccess = false;
        String message = "";
        Map<String, String> passwordMap = new HashMap<String, String>();
        try
        {
            ServerSessionUtil.setIP(request.getRemoteAddr());
            AuthenticationStatus authStatus = loginService.login(authnUsername, Utils.buildCredentials(authnPassword, authnTOTP));
            if (AuthenticationStatus.SUCCESS == authStatus)
            {
                Password password = passwordService.getPassword(Long.valueOf(passwordId));
                if (password != null)
                {
                    passwordMap.put("id", String.valueOf(password.getId()));
                    passwordMap.put("title", password.getName());
                    passwordMap.put("username", password.getUsername());
                    passwordMap.put("notes", password.getNotes());
                    passwordMap.put("tags", password.getTagsAsString());
                    passwordMap.put("active", password.isActive() ? "Y" : "N");
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
        return createModelAndView(isSuccess, message, "password", passwordMap);
    }
    
    
    @RequestMapping(value = "/passwords/{passwordId}/currentValue", method = RequestMethod.GET)
    public ModelAndView getCurrentPassword(@PathVariable("passwordId") String passwordId,
            HttpServletRequest request, 
            @RequestHeader(Constants.REST_AUTHN_USERNAME) String authnUsername,
            @RequestHeader(Constants.REST_AUTHN_PASSWORD) String authnPassword,
            @RequestHeader(value=Constants.REST_AUTHN_TOTP, required=false) String authnTOTP)
    {
        boolean isSuccess = false;
        String message = "";
        String currentPassword = "";
        try
        {
            ServerSessionUtil.setIP(request.getRemoteAddr());
            AuthenticationStatus authStatus = loginService.login(authnUsername, Utils.buildCredentials(authnPassword, authnTOTP));
            if (AuthenticationStatus.SUCCESS == authStatus)
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
        return createModelAndView(isSuccess, message, "currentPassword", currentPassword);
    }
    
    
    @RequestMapping(value = "/passwords", method = RequestMethod.POST)
    public ModelAndView addPassword(@RequestBody Map<String, Object> passwordMap,
            HttpServletRequest request,
            @RequestHeader(Constants.REST_AUTHN_USERNAME) String authnUsername,
            @RequestHeader(Constants.REST_AUTHN_PASSWORD) String authnPassword,
            @RequestHeader(value=Constants.REST_AUTHN_TOTP, required=false) String authnTOTP)
    {
        boolean isSuccess = false;
        String message = "";
        String passwordId = "";
        try
        {
            ServerSessionUtil.setIP(request.getRemoteAddr());
            AuthenticationStatus authStatus = loginService.login(authnUsername, Utils.buildCredentials(authnPassword, authnTOTP));
            if (AuthenticationStatus.SUCCESS == authStatus)
            {
                User loggedInUser = loginService.getLogin();
                Password password = new Password();
                password.setName(Utils.safeString(passwordMap.get("title")));
                password.setUsername(Utils.safeString(passwordMap.get("username")));
                PasswordData passwordDataItem = new PasswordData();
                passwordDataItem.setPassword(Utils.safeString(passwordMap.get("password")));
                password.addPasswordData(passwordDataItem);
                password.setNotes(Utils.safeString(passwordMap.get("notes")));
                password.setMaxHistory(-1);
                String active = Utils.safeString(passwordMap.get("active")).toLowerCase();
                password.setActive(active.equals("") || active.equals("true") || active.equals("yes") || active.equals("y"));
                password.addPermission(new Permission(loggedInUser, AccessLevel.GRANT));
                password.addTagsAsString(Utils.safeString(passwordMap.get("tags")));
                if (passwordService.isPasswordTaken(password.getName(), password.getUsername(), password.getId()))
                {
                    message = "Password title and username already exists";
                }
                else
                {
                    passwordService.addPassword(password);
                    passwordId = String.valueOf(password.getId());
                    isSuccess = true;
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
        return createModelAndView(isSuccess, message, "passwordId", passwordId);
    }
    
    
    @RequestMapping(value = "/passwords", method = RequestMethod.PUT)
    public ModelAndView updatePassword(@RequestBody Map<String, Object> passwordMap,
            HttpServletRequest request,
            @RequestHeader(Constants.REST_AUTHN_USERNAME) String authnUsername,
            @RequestHeader(Constants.REST_AUTHN_PASSWORD) String authnPassword,
            @RequestHeader(value=Constants.REST_AUTHN_TOTP, required=false) String authnTOTP)
    {
        boolean isSuccess = false;
        String message = "";
        String passwordId = "";
        try
        {
            ServerSessionUtil.setIP(request.getRemoteAddr());
            AuthenticationStatus authStatus = loginService.login(authnUsername, Utils.buildCredentials(authnPassword, authnTOTP));
            if (AuthenticationStatus.SUCCESS == authStatus)
            {
                passwordId = Utils.safeString(passwordMap.get("id"));
                int iPasswordId = Utils.safeInt(passwordId);
                if (iPasswordId > 0)
                {
                    Password password = passwordService.getPassword(iPasswordId);
                    if (null != password)
                    {
                        Password updatePassword = password.cloneCopy();
                        updatePassword.setName(Utils.safeString(passwordMap.get("title")));
                        updatePassword.setUsername(Utils.safeString(passwordMap.get("username")));
                        PasswordData passwordDataItem = new PasswordData();
                        passwordDataItem.setPassword(Utils.safeString(passwordMap.get("password")));
                        updatePassword.addPasswordData(passwordDataItem);
                        updatePassword.setNotes(Utils.safeString(passwordMap.get("notes")));
                        String active = Utils.safeString(passwordMap.get("active")).toLowerCase();
                        updatePassword.setActive(active.equals("") || active.equals("true") || active.equals("yes") || active.equals("y"));
                        updatePassword.addTagsAsString(Utils.safeString(passwordMap.get("tags")));
                        if (passwordService.isPasswordTaken(updatePassword.getName(), updatePassword.getUsername(), updatePassword.getId()))
                        {
                            message = "Password title and username already exists";
                        }
                        else
                        {
                            passwordService.updatePassword(updatePassword);
                            isSuccess = true;
                        }
                    }
                    else
                    {
                        message = "invalid id or no access";
                    }
                }
                else
                {
                    message = "invalid id or no access";
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
        return createModelAndView(isSuccess, message, "passwordId", passwordId);
    }

    
    private ModelAndView createModelAndView(boolean isSuccess, String message, String dataKey, Object dataValue)
    {
        ModelAndView mv = new ModelAndView(jsonView);
        mv.addObject("success", isSuccess);
        mv.addObject("message", message);
        mv.addObject(dataKey, dataValue);
        return mv;
    }
}
