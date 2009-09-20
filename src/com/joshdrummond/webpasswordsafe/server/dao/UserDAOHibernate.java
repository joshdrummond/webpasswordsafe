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
import org.hibernate.criterion.Restrictions;

import com.joshdrummond.webpasswordsafe.common.model.User;

/**
 * DAO implementation for User
 * 
 * @author Josh Drummond
 *
 */
public class UserDAOHibernate extends GenericHibernateDAO<User, Long> implements UserDAO {

    /* (non-Javadoc)
     * @see com.joshdrummond.webpasswordsafe.server.dao.UserDAO#findActiveUserByUsername(String)
     */
    public User findActiveUserByUsername(String username) {
        List<User> users = findByCriteria(Restrictions.eq("username", username), Restrictions.eq("activeFlag", true));
        return (users.size() > 0) ? users.get(0) : null;
    }

    /* (non-Javadoc)
     * @see com.joshdrummond.webpasswordsafe.server.dao.UserDAO#findAllUsers(boolean)
     */
    public List<User> findAllUsers(boolean includeOnlyActive)
    {
        return includeOnlyActive ? findByCriteria(Restrictions.eq("activeFlag", true)) : findByCriteria();
    }
}

