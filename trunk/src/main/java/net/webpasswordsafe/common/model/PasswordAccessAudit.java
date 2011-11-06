/*
    Copyright 2008-2011 Josh Drummond

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
package net.webpasswordsafe.common.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import net.sf.gilead.pojo.gwt.LightEntity;


/**
 * Domain model POJO for a password_access_audit
 * 
 * @author Josh Drummond
 *
 */
@Entity
@Table(name="password_access_audit")
public class PasswordAccessAudit extends LightEntity implements Serializable
{

    private static final long serialVersionUID = -8259517394743959516L;

    @Id
    @GeneratedValue
    @Column(name="id")
    private long id;
    
    @ManyToOne
    @JoinColumn(name="password_id", nullable=false, updatable=false)
    private Password password;
    
    @ManyToOne
    @JoinColumn(name="user_id", nullable=false, updatable=false)
    private User user;
    
    @Column(name="date_accessed", nullable=false, updatable=false)
    private Date dateAccessed;

    public PasswordAccessAudit()
    {
    }

    public long getId()
    {
        return this.id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public Password getPassword()
    {
        return this.password;
    }

    public void setPassword(Password password)
    {
        this.password = password;
    }

    public User getUser()
    {
        return this.user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public Date getDateAccessed()
    {
        return this.dateAccessed;
    }

    public void setDateAccessed(Date dateAccessed)
    {
        this.dateAccessed = dateAccessed;
    }
    
}
