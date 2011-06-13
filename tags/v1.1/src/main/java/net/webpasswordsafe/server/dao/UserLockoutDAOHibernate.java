/*
    Copyright 2011 Josh Drummond

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
import net.webpasswordsafe.common.model.User;
import net.webpasswordsafe.common.model.UserLockout;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;


/**
 * DAO implementation for UserLockout
 * 
 * @author Josh Drummond
 *
 */
@Repository("userLockoutDAO")
public class UserLockoutDAOHibernate extends GenericHibernateDAO<UserLockout, Long> implements UserLockoutDAO {

    @Override
    public UserLockout findByUser(User user)
    {
        List<UserLockout> lockouts = findByCriteria(Restrictions.eq("user", user));
        return (lockouts.size() > 0) ? lockouts.get(0) : null;
    }
}
