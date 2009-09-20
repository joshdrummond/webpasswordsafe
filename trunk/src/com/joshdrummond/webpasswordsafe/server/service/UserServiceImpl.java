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

import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.gwtwidgets.server.spring.ServletUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.joshdrummond.webpasswordsafe.client.remote.UserService;
import com.joshdrummond.webpasswordsafe.common.model.User;
import com.joshdrummond.webpasswordsafe.server.dao.UserDAO;
import com.joshdrummond.webpasswordsafe.server.encryption.Digester;

/**
 * 
 * @author Josh Drummond
 *
 */
@Service("userService")
public class UserServiceImpl implements UserService {
	
    private static Logger LOG = Logger.getLogger(UserServiceImpl.class);
    private static final long serialVersionUID = -8656307779047768662L;
    
    @Autowired
    private UserDAO userDAO;
    
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
    public void addUser(User user)
    {
        user.setPassword(digester.digest(user.getPassword()));
        user.setDateCreated(new Date());
        userDAO.makePersistent(user);
        LOG.info(user.getUsername() + " added");
    }

    @Transactional(propagation=Propagation.REQUIRED)
    public void updateUser(User user)
    {
//        User user = userDAO.findById(userDTO.getId(), false);
        if (!user.getPassword().equals(""))
        {
            user.setPassword(digester.digest(user.getPassword()));
        }
        userDAO.makePersistent(user);
        LOG.info(user.getUsername() + " updated");
    }
    
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public List<User> getUsers(boolean includeOnlyActive)
    {
        List<User> users = userDAO.findAllUsers(includeOnlyActive);
        LOG.info("found "+users.size()+" users");
        return users;
    }
    
    
    // getters and setters
    
    public UserDAO getUserDAO()
    {
        return this.userDAO;
    }

    public void setUserDAO(UserDAO userDAO)
    {
        this.userDAO = userDAO;
    }

    public Digester getDigester()
    {
        return this.digester;
    }

    public void setDigester(Digester digester)
    {
        this.digester = digester;
    }

}
