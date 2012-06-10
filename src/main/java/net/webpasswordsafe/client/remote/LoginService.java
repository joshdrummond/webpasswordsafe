/*
    Copyright 2008-2012 Josh Drummond

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
package net.webpasswordsafe.client.remote;

import java.util.List;
import java.util.Map;
import java.util.Set;
import net.webpasswordsafe.common.model.User;
import net.webpasswordsafe.common.util.Constants.Function;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.rpc.XsrfProtectedService;
import com.google.gwt.user.server.rpc.NoXsrfProtect;


/**
 * 
 * @author Josh Drummond
 *
 */
public interface LoginService extends XsrfProtectedService {
    
    @NoXsrfProtect
    public boolean ping();
    public boolean login(String username, String password);
    public boolean logout();
    public User getLogin();
    public Map<Function, Boolean> getLoginAuthorizations(Set<Function> functions);
    public List<Map<String, Object>> getLoginReports();
    
    /**
     * Utility class for simplifying access to the instance of async service.
     */
    public static class Util {
        private static LoginServiceAsync instance;
        public static LoginServiceAsync getInstance()
        {
            if (instance == null) {
                instance = (LoginServiceAsync) GWT.create(LoginService.class);
                ServiceDefTarget target = (ServiceDefTarget) instance;
                target.setServiceEntryPoint(GWT.getModuleBaseURL() + "rpc/LoginService");
            }
            return instance;
        }
    }
}
