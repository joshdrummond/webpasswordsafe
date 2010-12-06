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
package com.joshdrummond.webpasswordsafe.server.dao;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.joshdrummond.webpasswordsafe.common.model.AccessLevel;
import com.joshdrummond.webpasswordsafe.common.model.Password;
import com.joshdrummond.webpasswordsafe.common.model.Tag;
import com.joshdrummond.webpasswordsafe.common.model.User;
import com.joshdrummond.webpasswordsafe.common.util.Constants.Function;
import com.joshdrummond.webpasswordsafe.server.plugin.authorization.Authorizer;


/**
 * DAO implementation for Password
 * 
 * @author Josh Drummond
 *
 */
@Repository("passwordDAO")
public class PasswordDAOHibernate extends GenericHibernateDAO<Password, Long> implements PasswordDAO
{
    @Autowired
    private Authorizer authorizer;

    @Override
    @SuppressWarnings("unchecked")
	public List<Password> findPasswordByFuzzySearch(String query, User user, boolean activeOnly, Collection<Tag> tags)
    {
        StringBuilder hqlString = new StringBuilder();
        hqlString.append("select distinct pw from Password pw join pw.permissions pm left join fetch pw.tags ");
        hqlString.append("where (pw.name like :query or pw.username like :query or pw.notes like :query) ");
        hqlString.append(activeOnly ? "and pw.active = :active " : "");
        for (int tagCounter = 0; tagCounter < tags.size(); tagCounter++)
        {
            hqlString.append("and :tag");
            hqlString.append(tagCounter);
            hqlString.append(" in elements(pw.tags) ");
        }
        hqlString.append("and pm.accessLevel in (:aclread, :aclwrite, :aclgrant)");
        if (!authorizer.isAuthorized(user, Function.BYPASS_PASSWORD_PERMISSIONS))
        {
        	hqlString.append(" and ((pm.subject = :user) or (pm.subject in (select g from Group g join g.users u where u = :user)))");
        }
        hqlString.append(" order by pw.name asc");
        
    	Query hqlQuery = getSession().createQuery(hqlString.toString());
    	hqlQuery.setString("query", "%"+query+"%");
        if (!authorizer.isAuthorized(user, Function.BYPASS_PASSWORD_PERMISSIONS))
        {
        	hqlQuery.setEntity("user", user);
        }
    	if (activeOnly)
    	{
    	    hqlQuery.setString("active", "Y");
    	}
        hqlQuery.setString("aclread", AccessLevel.READ.name());
        hqlQuery.setString("aclwrite", AccessLevel.WRITE.name());
        hqlQuery.setString("aclgrant", AccessLevel.GRANT.name());
        int tagCounter = 0;
        for (Tag tag : tags)
        {
            hqlQuery.setEntity("tag"+tagCounter, tag);
            tagCounter++;
        }
        return hqlQuery.list();
    }

    @Override
    public Password findAllowedPasswordById(long passwordId, User user, AccessLevel accessLevel)
    {
        return findAllowedPassword(true, String.valueOf(passwordId), user, accessLevel);
    }
    
    @Override
    public Password findAllowedPasswordByName(String passwordName, User user, AccessLevel accessLevel)
    {
        return findAllowedPassword(false, passwordName, user, accessLevel);
    }

    private Password findAllowedPassword(boolean isPasswordId, String password, User user, AccessLevel accessLevel)
    {
        String sqlAccessLevelIn = null;
        switch (accessLevel) {
            case GRANT : sqlAccessLevelIn = "(:aclgrant) "; break;
            case WRITE : sqlAccessLevelIn = "(:aclwrite, :aclgrant) "; break;
            case READ : sqlAccessLevelIn = "(:aclread, :aclwrite, :aclgrant) "; break;
        }
        StringBuilder hqlString = new StringBuilder();
        hqlString.append("select distinct pw from Password pw join pw.permissions pm left join fetch pw.permissions left join fetch pw.tags where ");
        hqlString.append(isPasswordId ? "pw.id = :passwordId " : "pw.name = :passwordName ");
        if (!authorizer.isAuthorized(user, Function.BYPASS_PASSWORD_PERMISSIONS))
        {
            hqlString.append(" and pm.accessLevel in ");
            hqlString.append(sqlAccessLevelIn);
            hqlString.append("and ((pm.subject = :user) or (pm.subject in (select g from Group g join g.users u where u = :user)))");
        }
        Query hqlQuery = getSession().createQuery(hqlString.toString());
        if (isPasswordId)
        {
            hqlQuery.setLong("passwordId", Long.valueOf(password));
        }
        else
        {
            hqlQuery.setString("passwordName", password);
        }
        if (!authorizer.isAuthorized(user, Function.BYPASS_PASSWORD_PERMISSIONS))
        {
            hqlQuery.setEntity("user", user);
            if (accessLevel.equals(AccessLevel.GRANT) || accessLevel.equals(AccessLevel.WRITE) || accessLevel.equals(AccessLevel.READ))
            {
                hqlQuery.setString("aclgrant", AccessLevel.GRANT.name());
            }
            if (accessLevel.equals(AccessLevel.WRITE) || accessLevel.equals(AccessLevel.READ))
            {
                hqlQuery.setString("aclwrite", AccessLevel.WRITE.name());
            }
            if (accessLevel.equals(AccessLevel.READ))
            {
                hqlQuery.setString("aclread", AccessLevel.READ.name());
            }
        }
        return (Password)hqlQuery.uniqueResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public AccessLevel getMaxEffectiveAccessLevel(Password password, User user)
    {
        AccessLevel maxEffectiveAccessLevel = null;
        if (authorizer.isAuthorized(user, Function.BYPASS_PASSWORD_PERMISSIONS))
        {
            maxEffectiveAccessLevel = AccessLevel.GRANT;
        }
        else
        {
            StringBuilder hqlString = new StringBuilder();
            hqlString.append("select distinct pm.accessLevel from Permission pm ");
            hqlString.append("where pm.password = :password ");
            hqlString.append("and pm.accessLevel in (:aclread, :aclwrite, :aclgrant) ");
            hqlString.append("and ((pm.subject = :user) or (pm.subject in (select g from Group g join g.users u where u = :user)))");
            Query hqlQuery = getSession().createQuery(hqlString.toString());
            hqlQuery.setEntity("password", password);
            hqlQuery.setEntity("user", user);
            hqlQuery.setString("aclread", AccessLevel.READ.name());
            hqlQuery.setString("aclwrite", AccessLevel.WRITE.name());
            hqlQuery.setString("aclgrant", AccessLevel.GRANT.name());
            Set<String> accessLevels = new HashSet<String>(hqlQuery.list());
            if (accessLevels.contains(AccessLevel.GRANT.name()))
            {
                maxEffectiveAccessLevel = AccessLevel.GRANT;
            }
            else if (accessLevels.contains(AccessLevel.WRITE.name()))
            {
                maxEffectiveAccessLevel = AccessLevel.WRITE;
            }
            else if (accessLevels.contains(AccessLevel.READ.name()))
            {
                maxEffectiveAccessLevel = AccessLevel.READ;
            }
        }
        return maxEffectiveAccessLevel;
    }

    @Override
    public Password findPasswordByName(String passwordName)
    {
        List<Password> passwords = findByCriteria(Restrictions.eq("name", passwordName));
        return (passwords.size() > 0) ? passwords.get(0) : null;
    }
    
}
