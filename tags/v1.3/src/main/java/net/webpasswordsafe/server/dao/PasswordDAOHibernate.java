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
package net.webpasswordsafe.server.dao;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.webpasswordsafe.common.model.AccessLevel;
import net.webpasswordsafe.common.model.Group;
import net.webpasswordsafe.common.model.Password;
import net.webpasswordsafe.common.model.Subject;
import net.webpasswordsafe.common.model.Tag;
import net.webpasswordsafe.common.model.User;
import net.webpasswordsafe.common.util.Constants.Function;
import net.webpasswordsafe.common.util.Constants.Match;
import net.webpasswordsafe.server.plugin.authorization.Authorizer;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.impl.SessionFactoryImpl;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


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
    public List<Password> findPasswordByFuzzySearch(String query, User user, boolean activeOnly, Collection<Tag> tags, Match tagMatch)
    {
        //kludge to not use ilike on text column if MSSQL
        boolean isMSSQL = ((SessionFactoryImpl)getSessionFactory()).getDialect().toString().contains("SQLServer");
        Criteria crit = getSession().createCriteria(getPersistentClass());
        crit.setFetchMode("tags", FetchMode.JOIN);
        crit.add(Restrictions.or(Restrictions.or(Restrictions.ilike("name", query, MatchMode.ANYWHERE), 
                Restrictions.ilike("username", query, MatchMode.ANYWHERE)), isMSSQL ? 
                Restrictions.like("notes", query, MatchMode.ANYWHERE) : 
                Restrictions.ilike("notes", query, MatchMode.ANYWHERE)));
        if (activeOnly)
        {
            crit.add(Restrictions.eq("active", true));
        }
        Criterion tagsCriterion = null;
        for (Tag tag : tags)
        {
            Criterion tc = Restrictions.sqlRestriction("? in (select tag_id from password_tags where password_id = {alias}.id)", tag.getId(), StandardBasicTypes.LONG); 
            if (null == tagsCriterion)
            {
                tagsCriterion = tc;
            }
            else
            {
                tagsCriterion = tagMatch.equals(Match.AND) ? Restrictions.and(tagsCriterion, tc) : Restrictions.or(tagsCriterion, tc);
            }
        }
        if (tagsCriterion != null) crit.add(tagsCriterion);
        crit.createAlias("permissions", "pm");
        crit.add(Restrictions.in("pm.accessLevel", 
                new String[] {AccessLevel.READ.name(), AccessLevel.WRITE.name(), AccessLevel.GRANT.name()}));
        if (!authorizer.isAuthorized(user, Function.BYPASS_PASSWORD_PERMISSIONS.name()))
        {
            DetachedCriteria groupQuery = DetachedCriteria.forClass(Group.class);
            groupQuery.setProjection(Projections.id());
            groupQuery.createCriteria("users", "u").add(Restrictions.eq("u.id", user.getId()));
            crit.add(Restrictions.or(Restrictions.eq("pm.subject", user), Subqueries.propertyIn("pm.subject", groupQuery)));
        }
        crit.addOrder(Order.asc("name"));
        crit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return crit.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Password findAllowedPasswordById(long passwordId, User user, AccessLevel accessLevel)
    {
        Password foundPassword = null;
        String sqlAccessLevelIn = null;
        switch (accessLevel) {
            case GRANT : sqlAccessLevelIn = "(:aclgrant) "; break;
            case WRITE : sqlAccessLevelIn = "(:aclwrite, :aclgrant) "; break;
            case READ : sqlAccessLevelIn = "(:aclread, :aclwrite, :aclgrant) "; break;
        }
        StringBuilder hqlString = new StringBuilder();
        hqlString.append("select distinct pw.id from Password pw join pw.permissions pm where pw.id = :passwordId ");
        if (!authorizer.isAuthorized(user, Function.BYPASS_PASSWORD_PERMISSIONS.name()))
        {
            hqlString.append(" and pm.accessLevel in ");
            hqlString.append(sqlAccessLevelIn);
            hqlString.append("and ((pm.subject = :user) or (pm.subject in (select g from Group g join g.users u where u = :user)))");
        }
        Query hqlQuery = getSession().createQuery(hqlString.toString());
        hqlQuery.setLong("passwordId", passwordId);
        if (!authorizer.isAuthorized(user, Function.BYPASS_PASSWORD_PERMISSIONS.name()))
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
        List<Long> passwordIds = hqlQuery.list();
        if (passwordIds.size() > 0)
        {
            foundPassword = findById(passwordIds.get(0));
            foundPassword.getPermissions().size();
            foundPassword.getTags().size();
        }
        return foundPassword;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AccessLevel getMaxEffectiveAccessLevel(Password password, User user)
    {
        AccessLevel maxEffectiveAccessLevel = null;
        if (authorizer.isAuthorized(user, Function.BYPASS_PASSWORD_PERMISSIONS.name()))
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
    public Password findPasswordByName(String passwordName, String username)
    {
        List<Password> passwords = findByCriteria(Restrictions.and(Restrictions.eq("name", passwordName), Restrictions.eq("username", username)));
        return (passwords.size() > 0) ? passwords.get(0) : null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Password> findPasswordsByPermissionSubject(Subject subject)
    {
        Query hqlQuery = getSession().createQuery("select distinct p from Password p join p.permissions pm where pm.subject = :subject");
        hqlQuery.setEntity("subject", subject);
        return hqlQuery.list();
    }
    
}
