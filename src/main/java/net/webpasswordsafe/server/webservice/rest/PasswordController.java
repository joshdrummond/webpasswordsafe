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

import javax.servlet.http.HttpServletRequest;
import net.webpasswordsafe.client.remote.LoginService;
import net.webpasswordsafe.client.remote.PasswordService;
import net.webpasswordsafe.common.model.Password;
import net.webpasswordsafe.server.ServerSessionUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

/**
 * Set of password related REST webservices
 * 
 * @author Josh Drummond
 *
 */
@Controller
public class PasswordController {
	
    @Autowired
    private PasswordService passwordService;
    @Autowired
    protected LoginService loginService;
	@Autowired
	private View jsonView;

    private static Logger LOG = Logger.getLogger(PasswordController.class);
	private static final String DATA_FIELD = "data";
	private static final String ERROR_FIELD = "error";

	@RequestMapping(value = "/password/{passwordId}", method = RequestMethod.GET)
	public ModelAndView getCurrentPassword(@PathVariable("passwordId") String passwordId, HttpServletRequest request, 
			@RequestHeader("X-WPS-Username") String authnUsername,
			@RequestHeader("X-WPS-Password") String authnPassword)
	{
        boolean isSuccess = false;
        String message = "";
        String currentPassword = "";
        try
        {
        	ServerSessionUtil.setIP(request.getRemoteAddr());
            boolean isAuthnValid = loginService.login(authnUsername, authnPassword);
            if (isAuthnValid)
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
        if (isSuccess)
        {
        	return new ModelAndView(jsonView, DATA_FIELD, currentPassword);
        }
        else
        {
        	return new ModelAndView(jsonView, ERROR_FIELD, message);
        }
	}
}
