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
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.webpasswordsafe.client.remote.LoginService;
import net.webpasswordsafe.client.remote.UserService;
import net.webpasswordsafe.common.model.Group;
import net.webpasswordsafe.common.model.User;
import net.webpasswordsafe.common.util.Constants;
import net.webpasswordsafe.common.util.Constants.AuthenticationStatus;
import net.webpasswordsafe.common.util.Utils;
import net.webpasswordsafe.server.ServerSessionUtil;
import net.webpasswordsafe.server.report.JasperReportServlet;

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
 * Set of user related REST webservices
 * 
 * @author Josh Drummond
 *
 */
@Controller
public class UserController
{
    @Autowired
    private UserService userService;
    @Autowired
    protected LoginService loginService;
    @Autowired
    private View jsonView;
    private static Logger LOG = Logger.getLogger(UserController.class);

    @RequestMapping(value = "/groups/{groupId}", method = RequestMethod.DELETE)
    public ModelAndView deleteGroup(@PathVariable("groupId") String groupId,
            HttpServletRequest request,
            @RequestHeader(Constants.REST_AUTHN_USERNAME) String authnUsername,
            @RequestHeader(Constants.REST_AUTHN_PASSWORD) String authnPassword,
            @RequestHeader(value=Constants.REST_AUTHN_TOTP, required=false) String authnTOTP)
    {
        boolean isSuccess = false;
        String message = "";
        try
        {
            ServerSessionUtil.setIP(request.getRemoteAddr());
            AuthenticationStatus authStatus = loginService.login(authnUsername, Utils.buildCredentials(authnPassword, authnTOTP));
            if (AuthenticationStatus.SUCCESS == authStatus)
            {
                Group group = userService.getGroupWithUsers(Long.valueOf(groupId));
                if (group != null)
                {
                    userService.deleteGroup(group);
                    isSuccess = true;
                }
                else
                {
                    message = "Group not found";
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
        return createModelAndView(isSuccess, message, "groupId", groupId);
    }

    @RequestMapping(value = "/groups", method = RequestMethod.GET)
    public ModelAndView listGroups(@RequestParam(value="includeEveryone", defaultValue="false") String includeEveryone,
            HttpServletRequest request, HttpServletResponse response,
            @RequestHeader(Constants.REST_AUTHN_USERNAME) String authnUsername,
            @RequestHeader(Constants.REST_AUTHN_PASSWORD) String authnPassword,
            @RequestHeader(value=Constants.REST_AUTHN_TOTP, required=false) String authnTOTP)
    {
        boolean isSuccess = false;
        String message = "";
        List<Map<String, String>> groupList = new ArrayList<Map<String, String>>();
        try
        {
            ServerSessionUtil.setIP(request.getRemoteAddr());
            AuthenticationStatus authStatus = loginService.login(authnUsername, Utils.buildCredentials(authnPassword, authnTOTP));
            if (AuthenticationStatus.SUCCESS == authStatus)
            {
                List<Group> results = userService.getGroups(Boolean.parseBoolean(includeEveryone));
                for (Group group : results)
                {
                    Map<String, String> groupMap = new HashMap<String, String>();
                    groupMap.put("id", String.valueOf(group.getId()));
                    groupMap.put("name", group.getName());
                    groupList.add(groupMap);
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
        }
        return createModelAndView(isSuccess, message, "groupList", groupList);
    }

    @RequestMapping(value = "/groups/{groupId}", method = RequestMethod.GET)
    public ModelAndView getGroup(@PathVariable("groupId") String groupId,
            HttpServletRequest request, HttpServletResponse response,
            @RequestHeader(Constants.REST_AUTHN_USERNAME) String authnUsername,
            @RequestHeader(Constants.REST_AUTHN_PASSWORD) String authnPassword,
            @RequestHeader(value=Constants.REST_AUTHN_TOTP, required=false) String authnTOTP)
    {
        boolean isSuccess = false;
        String message = "";
        Map<String, Object> groupMap = new HashMap<String, Object>();
        try
        {
            ServerSessionUtil.setIP(request.getRemoteAddr());
            AuthenticationStatus authStatus = loginService.login(authnUsername, Utils.buildCredentials(authnPassword, authnTOTP));
            if (AuthenticationStatus.SUCCESS == authStatus)
            {
                for (Group group : userService.getGroups(true))
                {
                    if (group.getId() == Utils.safeInt(groupId))
                    {
                        Group groupWithUsers = userService.getGroupWithUsers(group.getId());
                        groupMap.put("id", String.valueOf(groupWithUsers.getId()));
                        groupMap.put("name", groupWithUsers.getName());
                        List<Map<String, String>> userList = new ArrayList<Map<String, String>>();
                        for (User user : groupWithUsers.getUsers()) {
                            Map<String, String> userMap = new HashMap<String, String>();
                            userMap.put("id", String.valueOf(user.getId()));
                            userMap.put("username", user.getUsername());
                            userList.add(userMap);
                        }
                        groupMap.put("users", userList);
                        isSuccess = true;
                        break;
                    }
                }

                if (isSuccess == false)
                    message = "Group not found";
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
        }
        return createModelAndView(isSuccess, message, "groupMap", groupMap);
    }

    @RequestMapping(value = "/groups", method = RequestMethod.POST)
    public ModelAndView addGroup(@RequestBody Map<String, Object> groupMap,
            HttpServletRequest request,
            @RequestHeader(Constants.REST_AUTHN_USERNAME) String authnUsername,
            @RequestHeader(Constants.REST_AUTHN_PASSWORD) String authnPassword,
            @RequestHeader(value=Constants.REST_AUTHN_TOTP, required=false) String authnTOTP)
    {
        boolean isSuccess = false;
        String message = "";
        String groupId = "";
        try
        {
            ServerSessionUtil.setIP(request.getRemoteAddr());
            AuthenticationStatus authStatus = loginService.login(authnUsername, Utils.buildCredentials(authnPassword, authnTOTP));
            if (AuthenticationStatus.SUCCESS == authStatus)
            {
                Group group = new Group();
                group.setName(Utils.safeString(groupMap.get("name")));

                boolean isGroupTaken = userService.isGroupTaken(group.getName(), 0);
                if (!isGroupTaken)
                {
                    userService.addGroup(group);
                    groupId = String.valueOf(group.getId());
                    isSuccess = true;
                }
                else
                {
                    message = "Group already exists";
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
        return createModelAndView(isSuccess, message, "groupId", groupId);
    }

    @RequestMapping(value = "/reports", method = RequestMethod.GET)
    public void getReport(HttpServletRequest request, HttpServletResponse response,
            @RequestHeader(Constants.REST_AUTHN_USERNAME) String authnUsername,
            @RequestHeader(Constants.REST_AUTHN_PASSWORD) String authnPassword,
            @RequestHeader(value=Constants.REST_AUTHN_TOTP, required=false) String authnTOTP)
    {
        try
        {
            ServerSessionUtil.setIP(request.getRemoteAddr());
            AuthenticationStatus authStatus = loginService.login(authnUsername, Utils.buildCredentials(authnPassword, authnTOTP));
            if (AuthenticationStatus.SUCCESS == authStatus)
            {
                JasperReportServlet servlet = new JasperReportServlet();
                servlet.doPost(request, response);
            }
            else
            {
            }
            loginService.logout();
        }
        catch (Exception e)
        {
            LOG.error(e.getMessage(), e);
        }
        
    }
    
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ModelAndView listUsers(@RequestParam(value="activeOnly", defaultValue="false") String activeOnly,
            HttpServletRequest request, HttpServletResponse response,
            @RequestHeader(Constants.REST_AUTHN_USERNAME) String authnUsername,
            @RequestHeader(Constants.REST_AUTHN_PASSWORD) String authnPassword,
            @RequestHeader(value=Constants.REST_AUTHN_TOTP, required=false) String authnTOTP)
    {
        boolean isSuccess = false;
        String message = "";
        List<Map<String, String>> userList = new ArrayList<Map<String, String>>();
        try
        {
            ServerSessionUtil.setIP(request.getRemoteAddr());
            AuthenticationStatus authStatus = loginService.login(authnUsername, Utils.buildCredentials(authnPassword, authnTOTP));
            if (AuthenticationStatus.SUCCESS == authStatus)
            {
                List<User> results = userService.getUsers(Boolean.parseBoolean(activeOnly));
                for (User user : results)
                {
                    Map<String, String> userMap = new HashMap<String, String>();
                    userMap.put("id", String.valueOf(user.getId()));
                    userMap.put("username", user.getUsername());
                    userMap.put("fullname", user.getFullname());
                    userMap.put("email", user.getEmail());
                    userMap.put("active", Boolean.toString(user.isActiveFlag()));
                    userList.add(userMap);
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
        }
        return createModelAndView(isSuccess, message, "userList", userList);
    }

    @RequestMapping(value = "/users/{userId}", method = RequestMethod.GET)
    public ModelAndView getUser(@PathVariable("userId") String userId,
            HttpServletRequest request, HttpServletResponse response,
            @RequestHeader(Constants.REST_AUTHN_USERNAME) String authnUsername,
            @RequestHeader(Constants.REST_AUTHN_PASSWORD) String authnPassword,
            @RequestHeader(value=Constants.REST_AUTHN_TOTP, required=false) String authnTOTP)
    {
        boolean isSuccess = false;
        String message = "";
        Map<String, Object> userMap = new HashMap<String, Object>();
        try
        {
            ServerSessionUtil.setIP(request.getRemoteAddr());
            AuthenticationStatus authStatus = loginService.login(authnUsername, Utils.buildCredentials(authnPassword, authnTOTP));
            if (AuthenticationStatus.SUCCESS == authStatus)
            {
                for (User user : userService.getUsers(false))
                {
                    if (user.getId() == Utils.safeInt(userId))
                    {
                        User userWithGroups = userService.getUserWithGroups(Utils.safeInt(userId));
                        userMap.put("id", String.valueOf(user.getId()));
                        userMap.put("username", user.getUsername());
                        userMap.put("fullname", user.getFullname());
                        userMap.put("email", user.getEmail());
                        userMap.put("active", Boolean.toString(user.isActiveFlag()));
                        List<Map<String, String>> groupList = new ArrayList<Map<String, String>>();
                        for (Group group : userWithGroups.getGroups()) {
                            Map<String, String> groupMap = new HashMap<String, String>();
                            groupMap.put("id", String.valueOf(group.getId()));
                            groupMap.put("name", group.getName());
                            groupList.add(groupMap);
                        }
                        userMap.put("groups", groupList);
                        isSuccess = true;
                        break;
                    }
                }

                if (isSuccess == false)
                    message = "User not found";
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
        }
        return createModelAndView(isSuccess, message, "userMap", userMap);
    }

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public ModelAndView addUser(@RequestBody Map<String, Object> userMap,
            HttpServletRequest request,
            @RequestHeader(Constants.REST_AUTHN_USERNAME) String authnUsername,
            @RequestHeader(Constants.REST_AUTHN_PASSWORD) String authnPassword,
            @RequestHeader(value=Constants.REST_AUTHN_TOTP, required=false) String authnTOTP)
    {
        boolean isSuccess = false;
        String message = "";
        String userId = "";
        try
        {
            ServerSessionUtil.setIP(request.getRemoteAddr());
            AuthenticationStatus authStatus = loginService.login(authnUsername, Utils.buildCredentials(authnPassword, authnTOTP));
            if (AuthenticationStatus.SUCCESS == authStatus)
            {
                User user = new User();
                user.setUsername(Utils.safeString(userMap.get("username")));
                user.updateAuthnPasswordValue(Utils.safeString(userMap.get("password")));
                user.setFullname(Utils.safeString(userMap.get("fullname")));
                user.setEmail(Utils.safeString(userMap.get("email")));
                String activeFlag = Utils.safeString(userMap.get("active")).toLowerCase();
                user.setActiveFlag(activeFlag.equals("true") || activeFlag.equals("yes") || activeFlag.equals("y"));
                
                boolean isUserTaken = userService.isUserTaken(user.getUsername());
                if (!isUserTaken)
                {
                    userService.addUser(user);
                    userId = String.valueOf(user.getId());
                    isSuccess = true;
                }
                else
                {
                    message = "Username already exists";
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
        return createModelAndView(isSuccess, message, "userId", userId);
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
