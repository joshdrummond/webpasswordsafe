/*
    Copyright 2008-2009 Josh Drummond

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
package com.joshdrummond.webpasswordsafe.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.gwtwidgets.server.spring.ServletUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.joshdrummond.webpasswordsafe.client.remote.UserService;
import com.joshdrummond.webpasswordsafe.common.model.Group;
import com.joshdrummond.webpasswordsafe.common.model.Subject;
import com.joshdrummond.webpasswordsafe.common.model.User;
import com.joshdrummond.webpasswordsafe.server.dao.GroupDAO;
import com.joshdrummond.webpasswordsafe.server.dao.UserDAO;
import com.joshdrummond.webpasswordsafe.server.encryption.Digester;
import static com.joshdrummond.webpasswordsafe.common.util.Constants.*;


/**
 * Implementation of User Service
 * 
 * @author Josh Drummond
 *
 */
@Service("userService")
public class UserServiceImpl implements UserService
{
    private static Logger LOG = Logger.getLogger(UserServiceImpl.class);
    private static final long serialVersionUID = -8656307779047768662L;
    
    @Autowired
    private UserDAO userDAO;
    
    @Autowired
    private GroupDAO groupDAO;
    
    @Autowired
    private Digester digester;

    @Transactional(propagation=Propagation.REQUIRED)
    public void changePassword(String password)
    {
        String loggedInUsername = (String)ServletUtils.getRequest().getSession().getAttribute("username");
        if (null != loggedInUsername)
        {
            User user = userDAO.findActiveUserByUsername(loggedInUsername);
            user.setPassword(digester.digest(password));
            userDAO.makePersistent(user);
            LOG.info(loggedInUsername + " changed password");
        }
        else
        {
            throw new RuntimeException("Not logged in");
        }
    }
    
    @Transactional(propagation=Propagation.REQUIRED)
    public void addUser(User newUser)
    {
        // create base user
        User user = new User();
        user.setUsername(newUser.getUsername());
        user.setFullname(newUser.getFullname());
        user.setEmail(newUser.getEmail());
        user.setActiveFlag(newUser.isActiveFlag());
        user.setPassword(digester.digest(newUser.getPassword()));
        user.setDateCreated(new Date());
        userDAO.makePersistent(user);
        
        // assign user to everyone group
        Group everyoneGroup = getEveryoneGroup();
        everyoneGroup.addUser(user);
        
        // assign user to other groups
        for (Group newGroup : newUser.getGroups())
        {
            Group group = groupDAO.findGroupByName(newGroup.getName());
            group.addUser(user);
        }
        LOG.info(user.getUsername() + " user added");
    }

    @Transactional(propagation=Propagation.REQUIRED)
    public void updateUser(User updateUser)
    {
        LOG.debug("incoming user password="+updateUser.getPassword());
        // update base user
        User user = userDAO.findById(updateUser.getId(), false);
        user.setFullname(updateUser.getFullname());
        user.setEmail(updateUser.getEmail());
        user.setActiveFlag(updateUser.isActiveFlag());
        if (!updateUser.getPassword().equals(""))
        {
            user.setPassword(digester.digest(updateUser.getPassword()));
            LOG.debug("now it is="+user.getPassword());
        }
        
        // assign everyone group if missing
        
        // remove groups removed
        
        // add groups added
        
        LOG.info(user.getUsername() + " user updated");
    }
    
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public List<User> getUsers(boolean includeOnlyActive)
    {
        List<User> users = userDAO.findAllUsers(includeOnlyActive);
        LOG.info("found "+users.size()+" users");
        return users;
    }

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
	        adminUser = User.newActiveUser(ADMIN_USER_NAME, ADMIN_USER_NAME, ADMIN_USER_NAME, ADMIN_USER_NAME);
	        adminUser.addGroup(getEveryoneGroup());
	        addUser(adminUser);
	    }
	}

    @Transactional(propagation=Propagation.REQUIRED)
	private void verifyEveryoneGroupExists()
	{
	    Group everyoneGroup = getEveryoneGroup();
	    if (null == everyoneGroup)
	    {
	        everyoneGroup = new Group(EVERYONE_GROUP_NAME);
	        addGroup(everyoneGroup);
	    }
	}

    @Transactional(propagation=Propagation.REQUIRED)
    public void addGroup(Group group)
    {
        groupDAO.makePersistent(group);
        LOG.info(group.getName() + " group added");
    }
    
    @Transactional(propagation=Propagation.REQUIRED)
    public void updateGroup(Group updateGroup)
    {
        Group group = groupDAO.findById(updateGroup.getId(), false);
        group.setName(updateGroup.getName());
        group.removeUsers();
        for (User user : updateGroup.getUsers())
        {
            User pUser = userDAO.findById(user.getId(), false);
            LOG.debug("trying to add user="+pUser.getId()+" to group="+group.getId());
            group.addUser(pUser);
        }
        LOG.info(group.getName() + " group updated");
    }
    
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public List<Group> getGroups(boolean includeEveryoneGroup)
    {
        List<Group> groups = groupDAO.findAll();
        if (!includeEveryoneGroup)
        {
            groups.remove(new Group(EVERYONE_GROUP_NAME));
        }
        LOG.info("found "+groups.size()+" groups");
        return groups;
    }
    
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public List<Subject> getSubjects(boolean includeOnlyActive)
    {
        List<User> users = getUsers(includeOnlyActive);
        List<Group> groups = getGroups(true);
        List<Subject> subjects = new ArrayList<Subject>(users.size()+groups.size());
        subjects.addAll(users);
        subjects.addAll(groups);
        LOG.info("found "+subjects.size()+" subjects");
        return subjects;
    }

    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
	public Group getEveryoneGroup()
	{
        return groupDAO.findGroupByName(EVERYONE_GROUP_NAME);
	}
	
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
	public User getAdminUser()
	{
	    return userDAO.findActiveUserByUsername(ADMIN_USER_NAME);
	}

    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public Group getGroupWithUsers(long groupId)
    {
        Group group = groupDAO.findById(groupId, false);
        // fetch users
        int numUsers = group.getUsers().size();
        LOG.info(group.getName()+" has "+numUsers+" users");
        return group;
    }

    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public User getUserWithGroups(long userId)
    {
        User user = userDAO.findById(userId, false);
        // fetch groups
        int numGroups = user.getGroups().size();
        LOG.info(user.getName()+" has "+numGroups+" groups");
        return user;
    }
    
}
