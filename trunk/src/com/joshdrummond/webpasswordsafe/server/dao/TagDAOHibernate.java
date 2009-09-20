/*
    Copyright 2008 Josh Drummond

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
import org.hibernate.criterion.Restrictions;

import com.joshdrummond.webpasswordsafe.common.model.Tag;
import com.joshdrummond.webpasswordsafe.common.model.User;

/**
 * DAO implementation for Tag
 * 
 * @author Josh Drummond
 *
 */
public class TagDAOHibernate extends GenericHibernateDAO<Tag, Long> implements TagDAO
{

    /* (non-Javadoc)
     * @see com.joshdrummond.webpasswordsafe.server.dao.TagDAO#findTagByName(java.lang.String)
     */
    public Tag findTagByName(String name)
    {
        List<Tag> tags = findByCriteria(Restrictions.eq("name", name));
        return (tags.size() > 0) ? tags.get(0) : null;
    }

    /* (non-Javadoc)
     * @see com.joshdrummond.webpasswordsafe.server.dao.TagDAO#findTagsForUser(com.joshdrummond.webpasswordsafe.server.model.User)
     */
    public List<Tag> findTagsForUser(User user)
    {
        Query hqlQuery = getSession().createQuery("select distinct t from Tag t join t.passwords p where p.userCreated = :userCreated order by t.name asc").setEntity("userCreated", user);
        return hqlQuery.list();
    }

}
