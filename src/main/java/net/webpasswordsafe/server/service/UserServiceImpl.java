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
package net.webpasswordsafe.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import net.webpasswordsafe.client.remote.LoginService;
import net.webpasswordsafe.client.remote.UserService;
import net.webpasswordsafe.common.model.Group;
import net.webpasswordsafe.common.model.IPLockout;
import net.webpasswordsafe.common.model.Password;
import net.webpasswordsafe.common.model.Subject;
import net.webpasswordsafe.common.model.Template;
import net.webpasswordsafe.common.model.User;
import net.webpasswordsafe.common.util.Constants.Function;
import net.webpasswordsafe.server.ServerSessionUtil;
import net.webpasswordsafe.server.dao.GroupDAO;
import net.webpasswordsafe.server.dao.IPLockoutDAO;
import net.webpasswordsafe.server.dao.PasswordDAO;
import net.webpasswordsafe.server.dao.TemplateDAO;
import net.webpasswordsafe.server.dao.UserDAO;
import net.webpasswordsafe.server.plugin.audit.AuditLogger;
import net.webpasswordsafe.server.plugin.authorization.Authorizer;
import net.webpasswordsafe.server.plugin.encryption.Digester;
import net.webpasswordsafe.server.service.helper.WPSXsrfProtectedServiceServlet;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import static net.webpasswordsafe.common.util.Constants.*;


/**
 * Implementation of User Service
 * 
 * @author Josh Drummond
 *
 */
@Service("userService")
public class UserServiceImpl extends WPSXsrfProtectedServiceServlet implements UserService
{
    private static final long serialVersionUID = 2818717240050539864L;
    private static Logger LOG = Logger.getLogger(UserServiceImpl.class);
    
    @Autowired
    private UserDAO userDAO;
    
    @Autowired
    private GroupDAO groupDAO;
    
    @Autowired
    private IPLockoutDAO ipLockoutDAO;
    
    @Autowired
    private PasswordDAO passwordDAO;
    
    @Autowired
    private TemplateDAO templateDAO;
    
    @Resource
    private Digester digester;

    @Resource
    private AuditLogger auditLogger;
    
    @Resource
    private Authorizer authorizer;
    
    @Autowired
    private LoginService loginService;

    
    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void changePassword(String password)
    {
        Date now = new Date();
        User loggedInUser = getLoggedInUser();
        if (null != loggedInUser)
        {
            loggedInUser.updateAuthnPasswordValue(digester.digest(password));
            userDAO.makePersistent(loggedInUser);
            auditLogger.log(now, loggedInUser.getUsername(), ServerSessionUtil.getIP(), "change password", "", true, "");
        }
        else
        {
            auditLogger.log(now, "", ServerSessionUtil.getIP(), "change password", "", false, "not logged in");
            throw new RuntimeException("Not logged in");
        }
    }
    
    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void addUser(User newUser)
    {
        Date now = new Date();
        User loggedInUser = getLoggedInUser();
        if (authorizer.isAuthorized(loggedInUser, Function.ADD_USER.name()))
        {
            addUserInternal(newUser);
        }
        else
        {
            auditLogger.log(now, ServerSessionUtil.getUsername(), ServerSessionUtil.getIP(), "add user", userTarget(newUser), false, "not authorized");
            throw new RuntimeException("Not Authorized!");
        }
    }
    
    @Transactional(propagation=Propagation.REQUIRED)
    private void addUserInternal(User newUser)
    {
        Date now = new Date();
        // create base user
        User user = new User();
        user.setUsername(newUser.getUsername());
        user.setFullname(newUser.getFullname());
        user.setEmail(newUser.getEmail());
        user.setActiveFlag(newUser.isActiveFlag());
        user.updateAuthnPasswordValue(digester.digest(newUser.getAuthnPasswordValue()));
        user.setDateCreated(now);
        userDAO.makePersistent(user);
        
        // assign user to everyone group
        Group everyoneGroup = getEveryoneGroup();
        everyoneGroup.addUser(user);
        
        // assign user to other groups
        for (Group newGroup : newUser.getGroups())
        {
            Group group = groupDAO.findById(newGroup.getId());
            group.addUser(user);
        }
        auditLogger.log(now, ServerSessionUtil.getUsername(), ServerSessionUtil.getIP(), "add user", userTarget(user), true, "");
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void updateUser(User updateUser)
    {
        Date now = new Date();
        String action = "update user";
        User loggedInUser = getLoggedInUser();
        if (authorizer.isAuthorized(loggedInUser, Function.UPDATE_USER.name()))
        {
            // update base user
            User user = userDAO.findById(updateUser.getId());
            user.setFullname(updateUser.getFullname());
            user.setEmail(updateUser.getEmail());
            user.setActiveFlag(updateUser.isActiveFlag());
            if (!updateUser.getAuthnPasswordValue().equals(""))
            {
                user.updateAuthnPasswordValue(digester.digest(updateUser.getAuthnPasswordValue()));
            }
            
            // remove old groups
            for (Group oldGroup : user.getGroups())
            {
                Group group = groupDAO.findById(oldGroup.getId());
                group.removeUser(user);
            }

            // assign everyone group
            Group everyoneGroup = getEveryoneGroup();
            everyoneGroup.addUser(user);
            
            // add new groups
            for (Group newGroup : updateUser.getGroups())
            {
                Group group = groupDAO.findById(newGroup.getId());
                group.addUser(user);
            }
            
            auditLogger.log(now, ServerSessionUtil.getUsername(), ServerSessionUtil.getIP(), action, userTarget(updateUser), true, "");
        }
        else
        {
            auditLogger.log(now, ServerSessionUtil.getUsername(), ServerSessionUtil.getIP(), action, userTarget(updateUser), false, "not authorized");
            throw new RuntimeException("Not Authorized!");
        }
    }
    
    @Override
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public List<User> getUsers(boolean includeOnlyActive)
    {
        List<User> users = userDAO.findAllUsers(includeOnlyActive);
        LOG.debug("found "+users.size()+" users");
        return users;
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void verifyInitialization()
    {
        verifyEveryoneGroupExists();
        verifyAdminUserExists();
    }

    @Transactional(propagation=Propagation.REQUIRED)
    private void verifyAdminUserExists()
    {
        User adminUser = getAdminUser();
        if (null == adminUser)
        {
            adminUser = User.newActiveUser(ADMIN_USER_NAME, ADMIN_USER_NAME, ADMIN_USER_NAME,
                    ADMIN_USER_NAME+"@"+ADMIN_USER_NAME+".com");
            adminUser.addGroup(getEveryoneGroup());
            addUserInternal(adminUser);
        }
    }

    @Transactional(propagation=Propagation.REQUIRED)
    private void verifyEveryoneGroupExists()
    {
        Group everyoneGroup = getEveryoneGroup();
        if (null == everyoneGroup)
        {
            everyoneGroup = new Group(EVERYONE_GROUP_NAME);
            addGroupInternal(everyoneGroup);
        }
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void addGroup(Group group)
    {
        Date now = new Date();
        User loggedInUser = getLoggedInUser();
        if (authorizer.isAuthorized(loggedInUser, Function.ADD_GROUP.name()))
        {
            addGroupInternal(group);
        }
        else
        {
            auditLogger.log(now, ServerSessionUtil.getUsername(), ServerSessionUtil.getIP(), "add group", groupTarget(group), false, "not authorized");
            throw new RuntimeException("Not Authorized!");
        }
    }
    
    @Transactional(propagation=Propagation.REQUIRED)
    private void addGroupInternal(Group group)
    {
        Date now = new Date();
        // update users
        Set<User> users = new HashSet<User>(group.getUsers());
        group.removeUsers();
        for (User user : users)
        {
            User pUser = userDAO.findById(user.getId());
            group.addUser(pUser);
        }

        groupDAO.makePersistent(group);
        auditLogger.log(now, ServerSessionUtil.getUsername(), ServerSessionUtil.getIP(), "add group", groupTarget(group), true, "");
    }
    
    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void updateGroup(Group updateGroup)
    {
        Date now = new Date();
        String action = "update group";
        User loggedInUser = getLoggedInUser();
        if (authorizer.isAuthorized(loggedInUser, Function.UPDATE_GROUP.name()))
        {
            Group group = groupDAO.findById(updateGroup.getId());
            String groupMessage = (updateGroup.getName().equals(group.getName())) ? "" : ("was: "+groupTarget(group));
            group.setName(updateGroup.getName());
            group.removeUsers();
            for (User user : updateGroup.getUsers())
            {
                User pUser = userDAO.findById(user.getId());
                group.addUser(pUser);
            }
            auditLogger.log(now, loggedInUser.getUsername(), ServerSessionUtil.getIP(), action, groupTarget(updateGroup), true, groupMessage);
        }
        else
        {
            auditLogger.log(now, ServerSessionUtil.getUsername(), ServerSessionUtil.getIP(), action, groupTarget(updateGroup), false, "not authorized");
            throw new RuntimeException("Not Authorized!");
        }
    }
    
    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void deleteGroup(Group updateGroup)
    {
        Date now = new Date();
        String action = "delete group";
        User loggedInUser = getLoggedInUser();
        if (authorizer.isAuthorized(loggedInUser, Function.DELETE_GROUP.name()))
        {
            Group group = groupDAO.findById(updateGroup.getId());
            if (group != null)
            {
                // remove associated password permissions
                List<Password> passwords = passwordDAO.findPasswordsByPermissionSubject(group);
                for (Password password : passwords)
                {
                    password.removePermissionsBySubject(group);
                    passwordDAO.makePersistent(password);
                }
                // remove associated template details
                List<Template> templates = templateDAO.findTemplatesByDetailSubject(group);
                for (Template template : templates)
                {
                    template.removeDetailsBySubject(group);
                    templateDAO.makePersistent(template);
                }
                // remove associated users
                group.removeUsers();
                // actually remove group
                groupDAO.flush();
                groupDAO.makeTransient(group);
                auditLogger.log(now, loggedInUser.getUsername(), ServerSessionUtil.getIP(), action, groupTarget(group), true, "");
            }
            else
            {
                auditLogger.log(now, ServerSessionUtil.getUsername(), ServerSessionUtil.getIP(), action, groupTarget(updateGroup), false, "invalid id");
            }
        }
        else
        {
            auditLogger.log(now, ServerSessionUtil.getUsername(), ServerSessionUtil.getIP(), action, groupTarget(updateGroup), false, "not authorized");
            throw new RuntimeException("Not Authorized!");
        }
    }
    
    @Override
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public List<Group> getGroups(boolean includeEveryoneGroup)
    {
        List<Group> groups = groupDAO.findAll();
        if (!includeEveryoneGroup)
        {
            groups.remove(new Group(EVERYONE_GROUP_NAME));
        }
        LOG.debug("found "+groups.size()+" groups");
        return groups;
    }
    
    @Override
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public List<Subject> getSubjects(boolean includeOnlyActive)
    {
        List<User> users = getUsers(includeOnlyActive);
        List<Group> groups = getGroups(true);
        List<Subject> subjects = new ArrayList<Subject>(users.size()+groups.size());
        subjects.addAll(users);
        subjects.addAll(groups);
        LOG.debug("found "+subjects.size()+" subjects");
        return subjects;
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public Group getEveryoneGroup()
    {
        return groupDAO.findGroupByName(EVERYONE_GROUP_NAME);
    }
    
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    private  User getAdminUser()
    {
        return userDAO.findUserByUsername(ADMIN_USER_NAME);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public Group getGroupWithUsers(long groupId)
    {
        Group group = groupDAO.findById(groupId);
        // fetch users
        int numUsers = group.getUsers().size();
        LOG.debug(group.getName()+" has "+numUsers+" users");
        return group;
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public User getUserWithGroups(long userId)
    {
        User user = userDAO.findById(userId);
        // fetch groups
        int numGroups = user.getGroups().size();
        LOG.debug(user.getName()+" has "+numGroups+" groups");
        return user;
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public boolean isUserTaken(String username)
    {
        User user = userDAO.findUserByUsername(username);
        return (null != user);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public boolean isGroupTaken(String groupName, long ignoreGroupId)
    {
        boolean isGroupTaken = false;
        Group group = groupDAO.findGroupByName(groupName);
        if (group != null)
        {
            if (group.getId() != ignoreGroupId)
            {
                isGroupTaken = true;
            }
        }
        return isGroupTaken;
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public boolean unblockIP(String ipaddress)
    {
        boolean isUnblocked = false;
        Date now = new Date();
        String action = "unblock ip";
        User loggedInUser = getLoggedInUser();
        if (authorizer.isAuthorized(loggedInUser, Function.UNBLOCK_IP.name()))
        {
            IPLockout ipLockout = ipLockoutDAO.findByIP(ipaddress);
            if ((null != ipLockout) && (null != ipLockout.getLockoutDate()))
            {
                ipLockout.setLockoutDate(null);
                ipLockout.setFailCount(0);
                isUnblocked = true;
                auditLogger.log(now, ServerSessionUtil.getUsername(), ServerSessionUtil.getIP(), action, ipaddress, true, "");
            }
            else
            {
                auditLogger.log(now, ServerSessionUtil.getUsername(), ServerSessionUtil.getIP(), action, ipaddress, false, "doesn't exist");
            }
        }
        else
        {
            auditLogger.log(now, ServerSessionUtil.getUsername(), ServerSessionUtil.getIP(), action, ipaddress, false, "not authorized");
            throw new RuntimeException("Not Authorized!");
        }
 
        return isUnblocked;
    }

    private User getLoggedInUser()
    {
        User loggedInUser = loginService.getLogin();
        if (null == loggedInUser)
        {
            throw new RuntimeException("Not Logged In!");
        }
        return loggedInUser;
    }

    private String userTarget(User user)
    {
        return user.getUsername() + " (userId="+user.getId()+")";
    }

    private String groupTarget(Group group)
    {
        return group.getName() + " (groupId="+group.getId()+")";
    }

}
