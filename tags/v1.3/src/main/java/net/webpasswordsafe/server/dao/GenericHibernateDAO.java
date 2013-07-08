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

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Generic DAO implementation for Hibernate
 * 
 * @author Josh Drummond
 *
 */
public abstract class GenericHibernateDAO<T, ID extends Serializable> implements GenericDAO<T, ID> {
    private Class<T> persistenceClass;
    
    @Autowired
    private SessionFactory sessionFactory;

    @SuppressWarnings({"unchecked"})
    protected GenericHibernateDAO() {
        this.persistenceClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public SessionFactory getSessionFactory()
    {
        return this.sessionFactory;
    }

    public Session getSession()
    {
        return getSessionFactory().getCurrentSession();
    }
    
    public void setSessionFactory(SessionFactory sessionFactory)
    {
        this.sessionFactory = sessionFactory;
    }

    public Class<T> getPersistentClass() {
        return this.persistenceClass;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public T findById(ID id) {
        return (T) getSession().load(getPersistentClass(), id);
    }

    @Override
    public List<T> findAll() {
        return findByCriteria();
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public List<T> findByExample(T exampleInstance, String... excludeProperty) {
        Criteria crit = getSession().createCriteria(getPersistentClass());
        Example example = Example.create(exampleInstance);
        for (String exclude : excludeProperty) {
            example.excludeProperty(exclude);
        }
        crit.add(example);
        return crit.list();
    }

    @Override
    public T makePersistent(T entity) {
        getSession().saveOrUpdate(entity);
        return entity;
    }

    @Override
    public T makeTransient(T entity) {
        getSession().delete(entity);
        return entity;
    }

    @Override
    public void flush() {
        getSession().flush();
    }

    @Override
    public void clear() {
        getSession().clear();
    }

    protected List<T> findByCriteria(Criterion... criterion) {
        return findByCriteria(null, criterion);
    }
    
    @SuppressWarnings({"unchecked"})
    protected List<T> findByCriteria(Order order, Criterion... criterion) {
        Criteria crit = getSession().createCriteria(getPersistentClass());
        for (Criterion c : criterion) {
            crit.add(c);
        }
        if (null != order)
        {
            crit.addOrder(order);
        }
        return crit.list();
    }
}
