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
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * 
 * @author Josh Drummond
 *
 */
public interface LoginServiceAsync {
    
    public void ping(AsyncCallback<Boolean> callback);
    public void login(String username, String password, AsyncCallback<Boolean> callback);
    public void logout(AsyncCallback<Boolean> callback);
    public void getLogin(AsyncCallback<User> callback);
    public void getLoginAuthorizations(Set<Function> functions, AsyncCallback<Map<Function, Boolean>> callback);
    public void getLoginReports(AsyncCallback<List<Map<String, Object>>> callback);

}
