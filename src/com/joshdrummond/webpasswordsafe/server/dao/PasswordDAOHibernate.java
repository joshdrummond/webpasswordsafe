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

import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import com.joshdrummond.webpasswordsafe.common.model.AccessLevel;
import com.joshdrummond.webpasswordsafe.common.model.Password;
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
	public List<Password> findPasswordByFuzzySearch(String query, User user)
    {
    	Query hqlQuery = getSession().createQuery("select distinct pw from Password pw join pw.permissions pm " +
    			"where (pw.name like :query or pw.username like :query or pw.notes like :query) " +
    			"and pw.active = :active and pm.accessLevel >= :accessLevel and " +
    			"((pm.subject = :user) or (pm.subject in (select g from Group g join g.users u where u = :user))) order by pw.name asc");
    	hqlQuery.setString("query", "%"+query+"%");
    	hqlQuery.setEntity("user", user);
    	hqlQuery.setString("active", "Y");
    	hqlQuery.setInteger("accessLevel", AccessLevel.READ.getId());
        return hqlQuery.list();
    }

    /* (non-Javadoc)
     * @see com.joshdrummond.webpasswordsafe.server.dao.PasswordDAO#findAllowedPasswordById(long, com.joshdrummond.webpasswordsafe.common.model.User, com.joshdrummond.webpasswordsafe.common.model.AccessLevel)
     */
    public Password findAllowedPasswordById(long passwordId, User user, AccessLevel accessLevel)
    {
        Query hqlQuery = getSession().createQuery("select distinct pw from Password pw join pw.permissions pm left join fetch pw.permissions left join fetch pw.tags " +
                "where pw.id = :passwordId and pm.accessLevel >= :accessLevel " +
                "and ((pm.subject = :user) or (pm.subject in (select g from Group g join g.users u where u = :user)))");
        hqlQuery.setLong("passwordId", passwordId);
        hqlQuery.setEntity("user", user);
        hqlQuery.setInteger("accessLevel", accessLevel.getId());
        return (Password)hqlQuery.uniqueResult();
    }

}
