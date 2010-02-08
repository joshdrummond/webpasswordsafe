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
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import com.joshdrummond.webpasswordsafe.common.model.Password;
import com.joshdrummond.webpasswordsafe.common.model.PasswordAccessAudit;


/**
 * DAO implementation for PasswordAccessAudit
 * 
 * @author Josh Drummond
 *
 */
@Repository("passwordAccessAuditDAO")
public class PasswordAccessAuditDAOHibernate 
    extends GenericHibernateDAO<PasswordAccessAudit, Long> 
    implements PasswordAccessAuditDAO
{

    /* (non-Javadoc)
     * @see com.joshdrummond.webpasswordsafe.server.dao.PasswordAccessAuditDAO#findAccessAuditByPassword(com.joshdrummond.webpasswordsafe.common.model.Password)
     */
    public List<PasswordAccessAudit> findAccessAuditByPassword(Password password)
    {
        return findByCriteria(Order.desc("dateAccessed"), Restrictions.eq("password", password));
    }

}
