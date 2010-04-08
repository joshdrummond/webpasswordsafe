/*
    Copyright 2008-2010 Josh Drummond

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
import javax.annotation.Resource;
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
import com.joshdrummond.webpasswordsafe.common.model.Template;
import com.joshdrummond.webpasswordsafe.common.model.TemplateDetail;
import com.joshdrummond.webpasswordsafe.common.model.User;
import com.joshdrummond.webpasswordsafe.common.util.Constants.Function;
import com.joshdrummond.webpasswordsafe.common.util.Utils;
import com.joshdrummond.webpasswordsafe.server.ServerSessionUtil;
import com.joshdrummond.webpasswordsafe.server.dao.PasswordAccessAuditDAO;
import com.joshdrummond.webpasswordsafe.server.dao.PasswordDAO;
import com.joshdrummond.webpasswordsafe.server.dao.TagDAO;
import com.joshdrummond.webpasswordsafe.server.dao.TemplateDAO;
import com.joshdrummond.webpasswordsafe.server.plugin.audit.AuditLogger;
import com.joshdrummond.webpasswordsafe.server.plugin.authorization.Authorizer;
import com.joshdrummond.webpasswordsafe.server.plugin.encryption.Encryptor;
import com.joshdrummond.webpasswordsafe.server.plugin.generator.PasswordGenerator;


/**
 * Implementation of Password Service
 * 
 * @author Josh Drummond
 *
 */
@Service("passwordService")
public class PasswordServiceImpl implements PasswordService
{
    private static final long serialVersionUID = -9164403179286398287L;
    private static Logger LOG = Logger.getLogger(PasswordServiceImpl.class);
    
    @Autowired
    private PasswordDAO passwordDAO;
    
    @Autowired
    private TagDAO tagDAO;
    
    @Autowired
    private PasswordAccessAuditDAO passwordAccessAuditDAO;
    
    @Autowired
    private TemplateDAO templateDAO;
    
    @Autowired
    private PasswordGenerator passwordGenerator;
    
    @Autowired
    private LoginService loginService;
    
    @Autowired
    private Encryptor encryptor;
    
    @Resource
    private AuditLogger auditLogger;
    
    @Autowired
    private Authorizer authorizer;
    
    
    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void addPassword(Password password)
    {
        Date now = new Date();
        User loggedInUser = loginService.getLogin();
        if (authorizer.isAuthorized(loggedInUser, Function.ADD_PASSWORD))
        {
            password.setUserCreated(loggedInUser);
            password.setDateCreated(now);
            password.setUserLastUpdate(loggedInUser);
            password.setDateLastUpdate(now);
            password.getCurrentPasswordData().setUserCreated(loggedInUser);
            password.getCurrentPasswordData().setDateCreated(now);
            password.getCurrentPasswordData().setPassword(encryptor.encrypt(password.getCurrentPasswordData().getPassword()));
            passwordDAO.makePersistent(password);
            auditLogger.log(now, loggedInUser.getUsername(), ServerSessionUtil.getIP(), "add password", password.getName(), true, "");
        }
        else
        {
            auditLogger.log(now, ServerSessionUtil.getUsername(), ServerSessionUtil.getIP(), "add password", password.getName(), false, "not authorized");
            throw new RuntimeException("Not Authorized!");
        }
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void updatePassword(Password updatePassword)
    {
        LOG.debug("updating password");
        Date now = new Date();
        User loggedInUser = loginService.getLogin();
        Password password = passwordDAO.findAllowedPasswordById(updatePassword.getId(), loggedInUser, AccessLevel.WRITE);
        if (password != null)
        {
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
            auditLogger.log(now, loggedInUser.getUsername(), ServerSessionUtil.getIP(), "update password", updatePassword.getName(), true, "");
        }
        else
        {
            auditLogger.log(now, loggedInUser.getUsername(), ServerSessionUtil.getIP(), "update password", updatePassword.getName(), false, "write access denied");
        }
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public List<Password> searchPassword(String query, boolean activeOnly, Collection<Tag> tags)
    {
    	query = Utils.safeString(query);
    	Date now = new Date();
        User loggedInUser = loginService.getLogin();
        List<Password> passwords = passwordDAO.findPasswordByFuzzySearch(query, loggedInUser, activeOnly, tags);
        auditLogger.log(now, loggedInUser.getUsername(), ServerSessionUtil.getIP(), "search password", "query=["+query+"] activeOnly=["+activeOnly+"] tags=["+tags+"]", true, "found "+passwords.size());
        return passwords;
    }
 
    @Override
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public String generatePassword()
    {
        LOG.debug("generating password...");
        return passwordGenerator.generatePassword();
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public String getCurrentPassword(long passwordId)
    {
        String currentPasswordValue = "";
        Date now = new Date();
        User loggedInUser = loginService.getLogin();
        Password password = passwordDAO.findAllowedPasswordById(passwordId, loggedInUser, AccessLevel.READ);
        if (password != null)
        {
            auditLogger.log(now, loggedInUser.getUsername(), ServerSessionUtil.getIP(), "get current password value", password.getName(), true, "");
            currentPasswordValue = encryptor.decrypt(password.getCurrentPasswordData().getPassword());
            createPasswordAccessAuditEntry(password, loggedInUser);
        }
        else
        {
            auditLogger.log(now, loggedInUser.getUsername(), ServerSessionUtil.getIP(), "get current password value", String.valueOf(passwordId), false, "invalid id or no access");
        }
        return currentPasswordValue;
    }
    
    @Transactional(propagation=Propagation.REQUIRED)
    private void createPasswordAccessAuditEntry(Password password, User user)
    {
        LOG.debug("creating access audit entry for password=["+password.getName()+"] user=["+user.getName()+"]");
        PasswordAccessAudit passwordAccessAudit = new PasswordAccessAudit();
        passwordAccessAudit.setDateAccessed(new Date());
        passwordAccessAudit.setPassword(password);
        passwordAccessAudit.setUser(user);
        passwordAccessAuditDAO.makePersistent(passwordAccessAudit);
    }
    
    @Override
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public Password getPassword(long passwordId)
    {
        Date now = new Date();
        User loggedInUser = loginService.getLogin();
        Password password = passwordDAO.findAllowedPasswordById(passwordId, loggedInUser, AccessLevel.READ);
        if (password != null)
        {
            password.setMaxEffectiveAccessLevel(passwordDAO.getMaxEffectiveAccessLevel(password, loggedInUser));
            auditLogger.log(now, loggedInUser.getUsername(), ServerSessionUtil.getIP(), "get password", password.getName(), true, "");
        }
        else
        {
            auditLogger.log(now, loggedInUser.getUsername(), ServerSessionUtil.getIP(), "get password", String.valueOf(passwordId), false, "invalid id or no access");
        }
        return password;
    }
    
    @Override
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public Password getPassword(String passwordName)
    {
        Date now = new Date();
        User loggedInUser = loginService.getLogin();
        Password password = passwordDAO.findAllowedPasswordByName(passwordName, loggedInUser, AccessLevel.READ);
        if (password != null)
        {
            auditLogger.log(now, loggedInUser.getUsername(), ServerSessionUtil.getIP(), "get password", passwordName, true, "");
        }
        else
        {
            auditLogger.log(now, loggedInUser.getUsername(), ServerSessionUtil.getIP(), "get password", passwordName, false, "invalid name or no access");
        }
        return password;
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public List<PasswordAccessAudit> getPasswordAccessAuditData(long passwordId)
    {
        Date now = new Date();
        List<PasswordAccessAudit> accessAuditList = new ArrayList<PasswordAccessAudit>(0);
        User loggedInUser = loginService.getLogin();
        Password password = passwordDAO.findAllowedPasswordById(passwordId, loggedInUser, AccessLevel.READ);
        if (null != password)
        {
            accessAuditList = passwordAccessAuditDAO.findAccessAuditByPassword(password);
            auditLogger.log(now, loggedInUser.getUsername(), ServerSessionUtil.getIP(), "get password access audit data", password.getName(), true, "");
        }
        else
        {
            auditLogger.log(now, loggedInUser.getUsername(), ServerSessionUtil.getIP(), "get password access audit data", String.valueOf(passwordId), false, "invalid id or no access");
        }
        LOG.debug("found "+accessAuditList.size() + " password access audit entries");
        return accessAuditList;
    }
    
    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public List<PasswordData> getPasswordHistoryData(long passwordId)
    {
        Date now = new Date();
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
            auditLogger.log(now, loggedInUser.getUsername(), ServerSessionUtil.getIP(), "get password history data", password.getName(), true, "");
        }
        else
        {
            auditLogger.log(now, loggedInUser.getUsername(), ServerSessionUtil.getIP(), "get password history data", String.valueOf(passwordId), false, "invalid id or no access");
        }
        LOG.debug("found "+decryptedPasswordDataList.size() + " password history values");
        return decryptedPasswordDataList;
    }
    
    @Override
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public List<Tag> getAvailableTags()
    {
        List<Tag> tags = tagDAO.findTagsInUse();
        LOG.debug("found "+tags.size() + " tags in use");
        return tags;
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void addTemplate(Template template)
    {
        Date now = new Date();
        User loggedInUser = loginService.getLogin();
        template.setUser(loggedInUser);
        templateDAO.makePersistent(template);
        auditLogger.log(now, loggedInUser.getUsername(), ServerSessionUtil.getIP(), "add template", template.getName(), true, "");
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void updateTemplate(Template updateTemplate)
    {
        LOG.debug("updating template");
        Date now = new Date();
        User loggedInUser = loginService.getLogin();
        Template template = templateDAO.findById(updateTemplate.getId(), false);
        if (template != null)
        {
            // update simple fields
            template.setName(updateTemplate.getName());
            template.setShare(updateTemplate.isShare());
            
            // update details
            // keep the permissions that haven't changed
            template.getTemplateDetails().retainAll(updateTemplate.getTemplateDetails());
            // add the permissions that have changed
            for (TemplateDetail templateDetail : updateTemplate.getTemplateDetails())
            {
                if (templateDetail.getId() == 0)
                {
                    template.addDetail(templateDetail);
                }
            }
            auditLogger.log(now, loggedInUser.getUsername(), ServerSessionUtil.getIP(), "update template", updateTemplate.getName(), true, "");
        }
        else
        {
            LOG.debug("template not found");
        }
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public List<Template> getTemplates(boolean includeShared)
    {
        User loggedInUser = loginService.getLogin();
        return templateDAO.findTemplatesByUser(loggedInUser, includeShared);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public Template getTemplateWithDetails(long templateId)
    {
        Template template = templateDAO.findById(templateId, false);
        if (template != null)
        {
            template.getTemplateDetails().size();
        }
        return template;
    }

}
