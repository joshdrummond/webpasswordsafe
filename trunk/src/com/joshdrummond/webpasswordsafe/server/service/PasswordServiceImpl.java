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
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.joshdrummond.webpasswordsafe.client.remote.LoginService;
import com.joshdrummond.webpasswordsafe.client.remote.PasswordService;
import com.joshdrummond.webpasswordsafe.common.model.AccessLevel;
import com.joshdrummond.webpasswordsafe.common.model.Password;
import com.joshdrummond.webpasswordsafe.common.model.PasswordAccessAudit;
import com.joshdrummond.webpasswordsafe.common.model.PasswordData;
import com.joshdrummond.webpasswordsafe.common.model.Permission;
import com.joshdrummond.webpasswordsafe.common.model.Tag;
import com.joshdrummond.webpasswordsafe.common.model.User;
import com.joshdrummond.webpasswordsafe.server.dao.PasswordAccessAuditDAO;
import com.joshdrummond.webpasswordsafe.server.dao.PasswordDAO;
import com.joshdrummond.webpasswordsafe.server.dao.TagDAO;
import com.joshdrummond.webpasswordsafe.server.dao.UserDAO;
import com.joshdrummond.webpasswordsafe.server.encryption.Encryptor;
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
    
    @Autowired
    private LoginService loginService;
    
    @Autowired
    private Encryptor encryptor;

    @Transactional(propagation=Propagation.REQUIRED)
    public void addPassword(Password password)
    {
        Date now = new Date();
        User loggedInUser = loginService.getLogin();
        password.setUserCreated(loggedInUser);
        password.setDateCreated(now);
        password.setUserLastUpdate(loggedInUser);
        password.setDateLastUpdate(now);
        password.getCurrentPasswordData().setUserCreated(loggedInUser);
        password.getCurrentPasswordData().setDateCreated(now);
        password.getCurrentPasswordData().setPassword(encryptor.encrypt(password.getCurrentPasswordData().getPassword()));
        passwordDAO.makePersistent(password);
        LOG.info(password.getName() + " added");
    }

    @Transactional(propagation=Propagation.REQUIRED)
    public void updatePassword(Password updatePassword)
    {
        LOG.debug("updating password");
        User loggedInUser = loginService.getLogin();
        Password password = passwordDAO.findAllowedPasswordById(updatePassword.getId(), loggedInUser, AccessLevel.WRITE);
        if (password != null)
        {
            Date now = new Date();

            // update simple fields
            password.setName(updatePassword.getName());
            password.setUsername(updatePassword.getUsername());
            password.setNotes(updatePassword.getNotes());
            password.setDateLastUpdate(now);
            password.setUserLastUpdate(loggedInUser);
            password.setActive(updatePassword.isActive());
            password.setMaxHistory(updatePassword.getMaxHistory());
            
            // update tags
            password.removeTags();
            for (Tag tag : updatePassword.getTags())
            {
                Tag pTag = tagDAO.findTagByName(tag.getName());
                if (null != pTag)
                {
                    password.addTag(pTag);
                }
                else
                {
                    password.addTag(tag);
                }
            }
            
            // update password data, push others back in history if applicable
            PasswordData updatePasswordData = updatePassword.getCurrentPasswordData();
            String updatePasswordVal = updatePasswordData.getPassword();
            // if user entered a password value and its not the same as the current one...
            if (!"".equals(updatePasswordVal))
            {
                String currentPasswordVal = encryptor.decrypt(password.getCurrentPasswordData().getPassword());
                if (!updatePasswordVal.equals(currentPasswordVal))
                {
                    updatePasswordData.setUserCreated(loggedInUser);
                    updatePasswordData.setDateCreated(now);
                    updatePasswordData.setPassword(encryptor.encrypt(updatePasswordVal));
                    password.addPasswordData(updatePasswordData);
                }
            }
            // trim history if not infinite
            password.pruneDataHistory();

            // update permissions if allowed to grant
            if (passwordDAO.findAllowedPasswordById(updatePassword.getId(), loggedInUser, AccessLevel.GRANT) != null)
            {
                // keep the permissions that haven't changed
                password.getPermissions().retainAll(updatePassword.getPermissions());
                // add the permissions that have changed
                for (Permission permission : updatePassword.getPermissions())
                {
                    if (permission.getId() == 0)
                    {
                        password.addPermission(permission);
                    }
                }
            }
            else
            {
            	LOG.debug("no access to grant permissions");
            }
            
        }
        else
        {
            LOG.debug("no access to update password");
        }
    }

    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public List<Password> searchPassword(String query, boolean activeOnly, Collection<Tag> tags)
    {
    	query = (null == query) ?  "" : query.trim();
        User loggedInUser = loginService.getLogin();
        List<Password> passwords = passwordDAO.findPasswordByFuzzySearch(query, loggedInUser, activeOnly, tags);
        LOG.debug("searching for password query ["+query+"] activeOnly="+activeOnly+" tags="+tags+" by ["+loggedInUser.getUsername()+"] found "+passwords.size());
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
        User loggedInUser = loginService.getLogin();
        Password password = passwordDAO.findAllowedPasswordById(passwordId, loggedInUser, AccessLevel.READ);
        if (password != null)
        {
            LOG.debug("returning current password value for ["+password.getName()+"]");
            currentPasswordValue = encryptor.decrypt(password.getCurrentPasswordData().getPassword());
            createPasswordAccessAuditEntry(password, loggedInUser);
        }
        else
        {
            LOG.debug("passwordId "+passwordId+" not found");
        }
        return currentPasswordValue;
    }
    
    @Transactional(propagation=Propagation.REQUIRED, readOnly=false)
    private void createPasswordAccessAuditEntry(Password password, User user)
    {
        LOG.debug("creating access audit entry for password=["+password.getName()+"] user=["+user.getName()+"]");
        PasswordAccessAudit passwordAccessAudit = new PasswordAccessAudit();
        passwordAccessAudit.setDateAccessed(new Date());
        passwordAccessAudit.setPassword(password);
        passwordAccessAudit.setUser(user);
        passwordAccessAuditDAO.makePersistent(passwordAccessAudit);
    }
    
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public Password getPassword(long passwordId)
    {
        User loggedInUser = loginService.getLogin();
        Password password = passwordDAO.findAllowedPasswordById(passwordId, loggedInUser, AccessLevel.READ);
        LOG.debug("permissions for "+passwordId+":");
        for (Permission p : password.getPermissions())
            LOG.debug(p.getSubject().getName()+"="+p.getAccessLevelObj().name()+","+p.getAccessLevel());
        return password;
    }
    
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public List<PasswordAccessAudit> getPasswordAccessAuditData(long passwordId)
    {
        List<PasswordAccessAudit> accessAuditList = new ArrayList<PasswordAccessAudit>(0);
        User loggedInUser = loginService.getLogin();
        Password password = passwordDAO.findAllowedPasswordById(passwordId, loggedInUser, AccessLevel.READ);
        if (null != password)
        {
            accessAuditList = passwordAccessAuditDAO.findAccessAuditByPassword(password);
        }
        LOG.debug("found "+accessAuditList.size() + " password access audit entries");
        return accessAuditList;
    }
    
    @Transactional(propagation=Propagation.REQUIRED, readOnly=false)
    public List<PasswordData> getPasswordHistoryData(long passwordId)
    {
        List<PasswordData> decryptedPasswordDataList = new ArrayList<PasswordData>(0);
        User loggedInUser = loginService.getLogin();
        Password password = passwordDAO.findAllowedPasswordById(passwordId, loggedInUser, AccessLevel.READ);
        if (null != password)
        {
            decryptedPasswordDataList = new ArrayList<PasswordData>(password.getPasswordData().size());
            for (PasswordData passwordData : password.getPasswordData())
            {
                decryptedPasswordDataList.add(new PasswordData(encryptor.decrypt(passwordData.getPassword()), 
                        passwordData.getDateCreated(), passwordData.getUserCreated()));
            }
            createPasswordAccessAuditEntry(password, loggedInUser);
        }
        LOG.debug("found "+decryptedPasswordDataList.size() + " password history values");
        return decryptedPasswordDataList;
    }
    
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public List<Tag> getAvailableTags()
    {
        List<Tag> tags = tagDAO.findTagsInUse();
        LOG.debug("found "+tags.size() + " tags in use");
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
