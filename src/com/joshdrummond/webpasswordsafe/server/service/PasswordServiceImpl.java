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
import com.joshdrummond.webpasswordsafe.client.remote.PasswordService;
import com.joshdrummond.webpasswordsafe.common.model.Password;
import com.joshdrummond.webpasswordsafe.common.model.PasswordAccessAudit;
import com.joshdrummond.webpasswordsafe.common.model.Tag;
import com.joshdrummond.webpasswordsafe.common.model.User;
import com.joshdrummond.webpasswordsafe.server.dao.PasswordAccessAuditDAO;
import com.joshdrummond.webpasswordsafe.server.dao.PasswordDAO;
import com.joshdrummond.webpasswordsafe.server.dao.TagDAO;
import com.joshdrummond.webpasswordsafe.server.dao.UserDAO;
import com.joshdrummond.webpasswordsafe.server.plugin.generator.PasswordGenerator;

/**
 * Implementation of Password Service
 * 
 * @author Josh Drummond
 *
 */
@Service("passwordService")
public class PasswordServiceImpl implements PasswordService {

    private static final long serialVersionUID = -9164403179286398287L;
    private static Logger LOG = Logger.getLogger(PasswordServiceImpl.class);
    
    @Autowired
    private PasswordDAO passwordDAO;
    
    @Autowired
    private UserDAO userDAO;
    
    @Autowired
    private TagDAO tagDAO;
    
    @Autowired
    private PasswordAccessAuditDAO passwordAccessAuditDAO;
    
    @Autowired
    private PasswordGenerator passwordGenerator;

    @Transactional(propagation=Propagation.REQUIRED)
    public void addPassword(Password password)
    {
        Date now = new Date();
        User loggedInUser = userDAO.findActiveUserByUsername((String)ServletUtils.getRequest().getSession().getAttribute("username"));
        password.setUserCreated(loggedInUser);
        password.setDateCreated(now);
        password.setUserLastUpdate(loggedInUser);
        password.setDateLastUpdate(now);
        password.getPasswordData().get(0).setUserCreated(loggedInUser);
        password.getPasswordData().get(0).setDateCreated(now);
        passwordDAO.makePersistent(password);
        LOG.info(password.getName() + " added");
    }

    @Transactional(propagation=Propagation.REQUIRED)
    public void updatePassword(Password password)
    {
        LOG.debug("updating password");
    }

    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public List<Password> searchPassword(String query)
    {
    	if ((null == query) || ("".equals(query.trim())))
    	{
    		query = "%";
    	}
        List<Password> passwords = passwordDAO.findPasswordByFuzzySearch(query);
        LOG.debug("searching for password query ["+query+"] found "+passwords.size());
        return passwords;
    }
 
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public String generatePassword()
    {
        LOG.debug("generating password...");
        return passwordGenerator.generatePassword();
    }

    @Transactional(propagation=Propagation.REQUIRED, readOnly=false)
    public String getCurrentPassword(long passwordId)
    {
        String currentPasswordValue = "";
        Password password = passwordDAO.findById(passwordId, false);
        User loggedInUser = userDAO.findActiveUserByUsername((String)ServletUtils.getRequest().getSession().getAttribute("username"));
        if (password != null)
        {
            LOG.debug("returning current password value for ["+password.getName()+"]");
            currentPasswordValue = password.getPasswordData().get(0).getPassword();
            PasswordAccessAudit passwordAccessAudit = new PasswordAccessAudit();
            passwordAccessAudit.setDateAccessed(new Date());
            passwordAccessAudit.setPassword(password);
            passwordAccessAudit.setUser(loggedInUser);
            passwordAccessAuditDAO.makePersistent(passwordAccessAudit);
        }
        else
        {
            LOG.debug("passwordId "+passwordId+" not found");
        }
        return currentPasswordValue;
    }
    
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public List<Tag> getAvailableTags()
    {
        User loggedInUser = userDAO.findActiveUserByUsername((String)ServletUtils.getRequest().getSession().getAttribute("username"));
        List<Tag> tags = tagDAO.findTagsForUser(loggedInUser);
        LOG.debug("found "+tags.size() + " tags for "+loggedInUser.getUsername());
        return tags;
    }
    
    // Getters and Setters
    
    public PasswordDAO getPasswordDAO()
    {
        return this.passwordDAO;
    }

    public void setPasswordDAO(PasswordDAO passwordDAO)
    {
        this.passwordDAO = passwordDAO;
    }

    public UserDAO getUserDAO()
    {
        return this.userDAO;
    }

    public void setUserDAO(UserDAO userDAO)
    {
        this.userDAO = userDAO;
    }

    public TagDAO getTagDAO()
    {
        return this.tagDAO;
    }

    public void setTagDAO(TagDAO tagDAO)
    {
        this.tagDAO = tagDAO;
    }

    public PasswordAccessAuditDAO getPasswordAccessAuditDAO()
    {
        return this.passwordAccessAuditDAO;
    }

    public void setPasswordAccessAuditDAO(PasswordAccessAuditDAO passwordAccessAuditDAO)
    {
        this.passwordAccessAuditDAO = passwordAccessAuditDAO;
    }

    public PasswordGenerator getPasswordGenerator()
    {
        return this.passwordGenerator;
    }

    public void setPasswordGenerator(PasswordGenerator passwordGenerator)
    {
        this.passwordGenerator = passwordGenerator;
    }

}
