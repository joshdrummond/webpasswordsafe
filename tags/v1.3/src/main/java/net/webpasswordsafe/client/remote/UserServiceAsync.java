/*
    Copyright 2008-2013 Josh Drummond

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
import net.webpasswordsafe.common.model.Group;
import net.webpasswordsafe.common.model.Subject;
import net.webpasswordsafe.common.model.User;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * 
 * @author Josh Drummond
 *
 */
public interface UserServiceAsync {
    
    public void changePassword(String password, AsyncCallback<Void> callback);
    public void addUser(User user, AsyncCallback<Void> callback);
    public void updateUser(User user, AsyncCallback<Void> callback);
    public void isUserTaken(String username, AsyncCallback<Boolean> callback);
    public void getUsers(boolean includeOnlyActive, AsyncCallback<List<User>> callback);
    public void verifyInitialization(AsyncCallback<Void> callback);
    public void getEveryoneGroup(AsyncCallback<Group> callback);
    public void getGroups(boolean includeEveryoneGroup, AsyncCallback<List<Group>> callback);
    public void getSubjects(boolean includeOnlyActive, AsyncCallback<List<Subject>> callback);
    public void getGroupWithUsers(long groupId, AsyncCallback<Group> callback);
    public void getUserWithGroups(long userId, AsyncCallback<User> callback);
    public void addGroup(Group group, AsyncCallback<Void> callback);
    public void updateGroup(Group group, AsyncCallback<Void> callback);
    public void deleteGroup(Group group, AsyncCallback<Void> callback);
    public void isGroupTaken(String groupName, long ignoreGroupId, AsyncCallback<Boolean> callback);
    public void unblockIP(String ipaddress, AsyncCallback<Boolean> callback);
}
