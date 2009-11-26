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
package com.joshdrummond.webpasswordsafe.server.dao;

import java.util.Collection;
import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import com.joshdrummond.webpasswordsafe.common.model.AccessLevel;
import com.joshdrummond.webpasswordsafe.common.model.Password;
import com.joshdrummond.webpasswordsafe.common.model.Tag;
import com.joshdrummond.webpasswordsafe.common.model.User;


/**
 * DAO implementation for Password
 * 
 * @author Josh Drummond
 *
 */
@Repository("passwordDAO")
public class PasswordDAOHibernate extends GenericHibernateDAO<Password, Long> implements PasswordDAO
{

    /* (non-Javadoc)
     * @see com.joshdrummond.webpasswordsafe.server.dao.PasswordDAO#findPasswordByFuzzySearch(java.lang.String)
     */
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
        hqlString.append("and pm.accessLevel in (:aclread, :aclwrite, :aclgrant) and ");
        hqlString.append("((pm.subject = :user) or (pm.subject in (select g from Group g join g.users u where u = :user))) order by pw.name asc");
        
    	Query hqlQuery = getSession().createQuery(hqlString.toString());
    	hqlQuery.setString("query", "%"+query+"%");
    	hqlQuery.setEntity("user", user);
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

    /* (non-Javadoc)
     * @see com.joshdrummond.webpasswordsafe.server.dao.PasswordDAO#findAllowedPasswordById(long, com.joshdrummond.webpasswordsafe.common.model.User, com.joshdrummond.webpasswordsafe.common.model.AccessLevel)
     */
    public Password findAllowedPasswordById(long passwordId, User user, AccessLevel accessLevel)
    {
        String sqlAccessLevelIn = null;
        switch (accessLevel) {
            case GRANT : sqlAccessLevelIn = "(:aclgrant) "; break;
            case WRITE : sqlAccessLevelIn = "(:aclwrite, :aclgrant) "; break;
            case READ : sqlAccessLevelIn = "(:aclread, :aclwrite, :aclgrant) "; break;
        }
        Query hqlQuery = getSession().createQuery("select distinct pw from Password pw join pw.permissions pm left join fetch pw.permissions left join fetch pw.tags " +
                "where pw.id = :passwordId and pm.accessLevel in " + sqlAccessLevelIn +
                "and ((pm.subject = :user) or (pm.subject in (select g from Group g join g.users u where u = :user)))");
        hqlQuery.setLong("passwordId", passwordId);
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
        return (Password)hqlQuery.uniqueResult();
    }

}
