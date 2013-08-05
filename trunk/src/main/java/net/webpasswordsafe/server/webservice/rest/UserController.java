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

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import net.webpasswordsafe.client.remote.LoginService;
import net.webpasswordsafe.client.remote.UserService;
import net.webpasswordsafe.common.model.User;
import net.webpasswordsafe.common.util.Constants;
import net.webpasswordsafe.common.util.Utils;
import net.webpasswordsafe.server.ServerSessionUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

    
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public ModelAndView addUser(@RequestBody Map<String, Object> userMap,
            HttpServletRequest request,
            @RequestHeader(Constants.REST_AUTHN_USERNAME) String authnUsername,
            @RequestHeader(Constants.REST_AUTHN_PASSWORD) String authnPassword)
    {
        boolean isSuccess = false;
        String message = "";
        String userId = "";
        try
        {
            ServerSessionUtil.setIP(request.getRemoteAddr());
            boolean isAuthnValid = loginService.login(authnUsername, authnPassword);
            if (isAuthnValid)
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
