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
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import net.webpasswordsafe.client.remote.LoginService;
import net.webpasswordsafe.client.remote.PasswordService;
import net.webpasswordsafe.common.model.AccessLevel;
import net.webpasswordsafe.common.model.Password;
import net.webpasswordsafe.common.model.PasswordAccessAudit;
import net.webpasswordsafe.common.model.PasswordData;
import net.webpasswordsafe.common.model.Permission;
import net.webpasswordsafe.common.model.Tag;
import net.webpasswordsafe.common.model.Template;
import net.webpasswordsafe.common.model.TemplateDetail;
import net.webpasswordsafe.common.model.User;
import net.webpasswordsafe.common.util.Constants.Match;
import net.webpasswordsafe.common.util.Utils;
import net.webpasswordsafe.common.util.Constants.Function;
import net.webpasswordsafe.server.ServerSessionUtil;
import net.webpasswordsafe.server.dao.PasswordAccessAuditDAO;
import net.webpasswordsafe.server.dao.PasswordDAO;
import net.webpasswordsafe.server.dao.TagDAO;
import net.webpasswordsafe.server.dao.TemplateDAO;
import net.webpasswordsafe.server.plugin.audit.AuditLogger;
import net.webpasswordsafe.server.plugin.authorization.Authorizer;
import net.webpasswordsafe.server.plugin.encryption.Encryptor;
import net.webpasswordsafe.server.plugin.generator.PasswordGenerator;
import net.webpasswordsafe.server.service.helper.WPSXsrfProtectedServiceServlet;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional; 


/**
 * Implementation of Password Service
 * 
 * @author Josh Drummond
 *
 */
@Service("passwordService")
public class PasswordServiceImpl extends WPSXsrfProtectedServiceServlet implements PasswordService
{
    private static final long serialVersionUID = -2328314230839034189L;
    private static Logger LOG = Logger.getLogger(PasswordServiceImpl.class);
    
    @Autowired
    private PasswordDAO passwordDAO;
    
    @Autowired
    private TagDAO tagDAO;
    
    @Autowired
    private PasswordAccessAuditDAO passwordAccessAuditDAO;
    
    @Autowired
    private TemplateDAO templateDAO;
    
    @Resource
    private PasswordGenerator passwordGenerator;
    
    @Autowired
    private LoginService loginService;
    
    @Resource
    private Encryptor encryptor;
    
    @Resource
    private AuditLogger auditLogger;
    
    @Resource
    private Authorizer authorizer;
    
    
    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void addPassword(Password password)
    {
        Date now = new Date();
        String action = "add password";
        User loggedInUser = getLoggedInUser();
        if (authorizer.isAuthorized(loggedInUser, Function.ADD_PASSWORD.name()))
        {
            if (password.getPermissions().size() > 0)
            {
                password.setUserCreated(loggedInUser);
                password.setDateCreated(now);
                password.setUserLastUpdate(loggedInUser);
                password.setDateLastUpdate(now);
                password.getCurrentPasswordData().setUserCreated(loggedInUser);
                password.getCurrentPasswordData().setDateCreated(now);
                password.getCurrentPasswordData().setPassword(encryptor.encrypt(password.getCurrentPasswordData().getPassword()));
                
                // update tags
                Set<Tag> tags = new HashSet<Tag>(password.getTags());
                password.removeTags();
                for (Tag tag : tags)
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
                
                passwordDAO.makePersistent(password);
                auditLogger.log(now, loggedInUser.getUsername(), ServerSessionUtil.getIP(), action, passwordTarget(password), true, "");
            }
            else
            {
                auditLogger.log(now, ServerSessionUtil.getUsername(), ServerSessionUtil.getIP(), action, passwordTarget(password), false, "missing permissions");
                throw new RuntimeException("Missing Permissions");
            }
        }
        else
        {
            auditLogger.log(now, ServerSessionUtil.getUsername(), ServerSessionUtil.getIP(), action, passwordTarget(password), false, "not authorized");
            throw new RuntimeException("Not Authorized!");
        }
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void updatePassword(Password updatePassword)
    {
        LOG.debug("updating password");
        Date now = new Date();
        String action = "update password";
        User loggedInUser = getLoggedInUser();
        Password password = passwordDAO.findAllowedPasswordById(updatePassword.getId(), loggedInUser, AccessLevel.WRITE);
        if (password != null)
        {
            if (updatePassword.getPermissions().size() > 0)
            {
                String passwordMessage = (updatePassword.getName().equals(password.getName())) ? "" : ("was: "+passwordTarget(password));
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
                auditLogger.log(now, loggedInUser.getUsername(), ServerSessionUtil.getIP(), action, passwordTarget(updatePassword), true, passwordMessage);
            }
            else
            {
                auditLogger.log(now, loggedInUser.getUsername(), ServerSessionUtil.getIP(), action, passwordTarget(updatePassword), false, "missing permissions");
                throw new RuntimeException("Missing Permissions");
            }
        }
        else
        {
            auditLogger.log(now, loggedInUser.getUsername(), ServerSessionUtil.getIP(), action, passwordTarget(updatePassword), false, "write access denied");
        }
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public List<Password> searchPassword(String query, boolean activeOnly, Collection<Tag> tags, Match tagMatch)
    {
        query = Utils.safeString(query);
        Date now = new Date();
        User loggedInUser = getLoggedInUser();
        List<Password> passwords = passwordDAO.findPasswordByFuzzySearch(query, loggedInUser, activeOnly, tags, tagMatch);
        auditLogger.log(now, loggedInUser.getUsername(), ServerSessionUtil.getIP(), "search password", "query=["+query+"] activeOnly=["+activeOnly+"] tags=["+tags+"] tagMatch=["+tagMatch+"]", true, "found "+passwords.size());
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
        String action = "get current password value";
        User loggedInUser = getLoggedInUser();
        Password password = passwordDAO.findAllowedPasswordById(passwordId, loggedInUser, AccessLevel.READ);
        if (password != null)
        {
            auditLogger.log(now, loggedInUser.getUsername(), ServerSessionUtil.getIP(), action, passwordTarget(password), true, "");
            currentPasswordValue = encryptor.decrypt(password.getCurrentPasswordData().getPassword());
            createPasswordAccessAuditEntry(password, loggedInUser);
        }
        else
        {
            auditLogger.log(now, loggedInUser.getUsername(), ServerSessionUtil.getIP(), action, passwordTarget(passwordId), false, "invalid id or no access");
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
        String action = "get password";
        User loggedInUser = getLoggedInUser();
        Password password = passwordDAO.findAllowedPasswordById(passwordId, loggedInUser, AccessLevel.READ);
        if (password != null)
        {
            password.setMaxEffectiveAccessLevel(passwordDAO.getMaxEffectiveAccessLevel(password, loggedInUser));
            auditLogger.log(now, loggedInUser.getUsername(), ServerSessionUtil.getIP(), action, passwordTarget(password), true, "");
        }
        else
        {
            auditLogger.log(now, loggedInUser.getUsername(), ServerSessionUtil.getIP(), action, passwordTarget(passwordId), false, "invalid id or no access");
        }
        return password;
    }
    
    @Override
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public List<PasswordAccessAudit> getPasswordAccessAuditData(long passwordId)
    {
        Date now = new Date();
        String action = "get password access audit data";
        List<PasswordAccessAudit> accessAuditList = new ArrayList<PasswordAccessAudit>(0);
        User loggedInUser = getLoggedInUser();
        Password password = passwordDAO.findAllowedPasswordById(passwordId, loggedInUser, AccessLevel.READ);
        if (null != password)
        {
            accessAuditList = passwordAccessAuditDAO.findAccessAuditByPassword(password);
            auditLogger.log(now, loggedInUser.getUsername(), ServerSessionUtil.getIP(), action, passwordTarget(password), true, "");
        }
        else
        {
            auditLogger.log(now, loggedInUser.getUsername(), ServerSessionUtil.getIP(), action, passwordTarget(passwordId), false, "invalid id or no access");
        }
        LOG.debug("found "+accessAuditList.size() + " password access audit entries");
        return accessAuditList;
    }
    
    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public List<PasswordData> getPasswordHistoryData(long passwordId)
    {
        Date now = new Date();
        String action = "get password history data";
        List<PasswordData> decryptedPasswordDataList = new ArrayList<PasswordData>(0);
        User loggedInUser = getLoggedInUser();
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
            auditLogger.log(now, loggedInUser.getUsername(), ServerSessionUtil.getIP(), action, passwordTarget(password), true, "");
        }
        else
        {
            auditLogger.log(now, loggedInUser.getUsername(), ServerSessionUtil.getIP(), action, passwordTarget(passwordId), false, "invalid id or no access");
        }
        LOG.debug("found "+decryptedPasswordDataList.size() + " password history values");
        return decryptedPasswordDataList;
    }
    
    @Override
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public List<Tag> getAllTags()
    {
        List<Tag> tags = tagDAO.findTagsInUse();
        LOG.debug("found "+tags.size() + " tags");
        return tags;
    }
    
    @Override
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public List<Tag> getAvailableTags()
    {
        User loggedInUser = getLoggedInUser();
        List<Tag> tags = null;
        if (authorizer.isAuthorized(loggedInUser, Function.BYPASS_PASSWORD_PERMISSIONS.name()))
        {
            tags = getAllTags();
        }
        else
        {
            tags = tagDAO.findTagsByUser(loggedInUser);
        }
        LOG.debug("found "+tags.size() + " user tags");
        return tags;
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void addTemplate(Template template)
    {
        Date now = new Date();
        String action = "add template";
        User loggedInUser = getLoggedInUser();
        template.setUser(loggedInUser);
        templateDAO.makePersistent(template);
        auditLogger.log(now, loggedInUser.getUsername(), ServerSessionUtil.getIP(), action, templateTarget(template), true, "");
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void updateTemplate(Template updateTemplate)
    {
        LOG.debug("updating template");
        Date now = new Date();
        String action = "update template";
        User loggedInUser = getLoggedInUser();
        Template template = templateDAO.findUpdatableTemplateById(updateTemplate.getId(), loggedInUser);
        if (template != null)
        {
            String templateMessage = (updateTemplate.getName().equals(template.getName())) ? "" : ("was: "+templateTarget(template));
            // update simple fields
            template.setName(updateTemplate.getName());
            // only change sharing status if original owner is updating or special bypass authz
            if ((template.getUser().getId() == loggedInUser.getId()) || 
                authorizer.isAuthorized(loggedInUser, Function.BYPASS_TEMPLATE_SHARING.name()))
            {
                template.setShared(updateTemplate.isShared());
            }
            
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
            auditLogger.log(now, loggedInUser.getUsername(), ServerSessionUtil.getIP(), action, templateTarget(updateTemplate), true, templateMessage);
        }
        else
        {
            auditLogger.log(now, loggedInUser.getUsername(), ServerSessionUtil.getIP(), action, templateTarget(updateTemplate), false, "invalid id or no access");
        }
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void deleteTemplate(Template updateTemplate)
    {
        LOG.debug("deleting template");
        Date now = new Date();
        String action = "delete template";
        User loggedInUser = getLoggedInUser();
        Template template = templateDAO.findUpdatableTemplateById(updateTemplate.getId(), loggedInUser);
        if (template != null)
        {
            // only allow delete if original owner or special bypass authz
            if ((template.getUser().getId() == loggedInUser.getId()) || 
                authorizer.isAuthorized(loggedInUser, Function.BYPASS_TEMPLATE_SHARING.name()))
            {
                templateDAO.makeTransient(template);
                auditLogger.log(now, loggedInUser.getUsername(), ServerSessionUtil.getIP(), action, templateTarget(template), true, "");
            }
            else
            {
                auditLogger.log(now, loggedInUser.getUsername(), ServerSessionUtil.getIP(), action, templateTarget(template), false, "no access");
            }
        }
        else
        {
            auditLogger.log(now, loggedInUser.getUsername(), ServerSessionUtil.getIP(), action, templateTarget(updateTemplate), false, "invalid id or no access");
        }
    }
    
    @Override
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public List<Template> getTemplates(boolean includeShared)
    {
        User loggedInUser = getLoggedInUser();
        return templateDAO.findTemplatesByUser(loggedInUser, includeShared);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public Template getTemplateWithDetails(long templateId)
    {
        Template template = templateDAO.findById(templateId);
        if (template != null)
        {
            template.getTemplateDetails().size();
        }
        return template;
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public boolean isPasswordTaken(String passwordName, String username, long ignorePasswordId)
    {
        boolean isPasswordTaken = false;
        Password password = passwordDAO.findPasswordByName(passwordName, username);
        if (password != null)
        {
            if (password.getId() != ignorePasswordId)
            {
                isPasswordTaken = true;
            }
        }
        return isPasswordTaken;
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public boolean isTemplateTaken(String templateName, long ignoreTemplateId)
    {
        boolean isTemplateTaken = false;
        Template template = templateDAO.findTemplateByName(templateName);
        if (template != null)
        {
            if (template.getId() != ignoreTemplateId)
            {
                isTemplateTaken = true;
            }
        }
        return isTemplateTaken;
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

    private String passwordTarget(Password password)
    {
        return password.getName() + " (passwordId="+password.getId()+")";
    }

    private String passwordTarget(long passwordId)
    {
        return "passwordId="+passwordId;
    }
    
    private String templateTarget(Template template)
    {
        return template.getName() + " (templateId="+template.getId()+")";
    }

}
