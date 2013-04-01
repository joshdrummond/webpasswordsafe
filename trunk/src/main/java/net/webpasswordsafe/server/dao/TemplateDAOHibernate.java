/*
    Copyright 2009-2013 Josh Drummond

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
import net.webpasswordsafe.common.model.Subject;
import net.webpasswordsafe.common.model.Template;
import net.webpasswordsafe.common.model.User;
import net.webpasswordsafe.common.util.Constants.Function;
import net.webpasswordsafe.server.plugin.authorization.Authorizer;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


/**
 * DAO implementation for Template
 * 
 * @author Josh Drummond
 *
 */
@Repository("templateDAO")
public class TemplateDAOHibernate extends GenericHibernateDAO<Template, Long> implements TemplateDAO
{
    @Autowired
    private Authorizer authorizer;

    @Override
    public List<Template> findTemplatesByUser(User user, boolean includeShared)
    {
        if (authorizer.isAuthorized(user, Function.BYPASS_TEMPLATE_SHARING.name()))
        {
            return findByCriteria(Order.asc("name"));
        }
        else
        {
            if (includeShared)
            {
                return findByCriteria(Order.asc("name"), Restrictions.or(Restrictions.eq("user", user), Restrictions.eq("shared", true)));
            }
            else
            {
                return findByCriteria(Order.asc("name"), Restrictions.eq("user", user));
            }
        }
    }

    @Override
    public Template findUpdatableTemplateById(long templateId, User user)
    {
        List<Template> templates = null;
        if (authorizer.isAuthorized(user, Function.BYPASS_TEMPLATE_SHARING.name()))
        {
            templates = findByCriteria(Restrictions.eq("id", templateId));
        }
        else
        {
            templates = findByCriteria(Restrictions.eq("id", templateId), 
                Restrictions.or(Restrictions.eq("user", user), Restrictions.eq("shared", true)));
        }
        return (templates.size() > 0) ? templates.get(0) : null;
    }

    @Override
    public Template findTemplateByName(String name)
    {
        List<Template> templates = findByCriteria(Restrictions.eq("name", name));
        return (templates.size() > 0) ? templates.get(0) : null;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<Template> findTemplatesByDetailSubject(Subject subject)
    {
        Query hqlQuery = getSession().createQuery("select distinct t from Template t join t.templateDetails td where td.subject = :subject");
        hqlQuery.setEntity("subject", subject);
        return hqlQuery.list();
    }

}
