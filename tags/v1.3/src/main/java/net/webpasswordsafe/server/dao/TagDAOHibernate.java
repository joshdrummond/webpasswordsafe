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

import java.util.List;
import net.webpasswordsafe.common.model.Tag;
import net.webpasswordsafe.common.model.User;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;


/**
 * DAO implementation for Tag
 * 
 * @author Josh Drummond
 *
 */
@Repository("tagDAO")
public class TagDAOHibernate extends GenericHibernateDAO<Tag, Long> implements TagDAO
{

    @Override
    public Tag findTagByName(String name)
    {
        List<Tag> tags = findByCriteria(Restrictions.eq("name", name));
        return (tags.size() > 0) ? tags.get(0) : null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Tag> findTagsInUse()
    {
        Query hqlQuery = getSession().createQuery("select distinct t from Tag t join t.passwords p order by t.name asc");
        return hqlQuery.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Tag> findTagsByUser(User user)
    {
        Query hqlQuery = getSession().createQuery("select distinct t from Tag t join t.passwords p join p.permissions pm where ((pm.subject = :user) or (pm.subject in (select g from Group g join g.users u where u = :user))) order by t.name asc");
        hqlQuery.setEntity("user", user);
        return hqlQuery.list();
    }

}
