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
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.rpc.XsrfProtectedService;


/**
 * 
 * @author Josh Drummond
 *
 */
public interface UserService extends XsrfProtectedService {
    
    public void changePassword(String password);
    public void addUser(User user);
    public void updateUser(User user);
    public boolean isUserTaken(String username);
    public List<User> getUsers(boolean includeOnlyActive);
    public void verifyInitialization();
    public Group getEveryoneGroup();
    public List<Group> getGroups(boolean includeEveryoneGroup);
    public List<Subject> getSubjects(boolean includeOnlyActive);
    public Group getGroupWithUsers(long groupId);
    public User getUserWithGroups(long userId);
    public void addGroup(Group group);
    public void updateGroup(Group group);
    public void deleteGroup(Group group);
    public boolean isGroupTaken(String groupName, long ignoreGroupId);
    public boolean unblockIP(String ipaddress);

    /**
     * Utility class for simplifying access to the instance of async service.
     */
    public static class Util {
        private static UserServiceAsync instance;
        public static UserServiceAsync getInstance()
        {
            if (instance == null) {
                instance = (UserServiceAsync) GWT.create(UserService.class);
                ServiceDefTarget target = (ServiceDefTarget) instance;
                target.setServiceEntryPoint(GWT.getModuleBaseURL() + "rpc/UserService");
            }
            return instance;
        }
    }
}
