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
package com.joshdrummond.webpasswordsafe.server.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

/**
 * POJO model for password_data
 * 
 * @author Josh Drummond
 *
 */
@Entity
@Table(name="password_data")
public class PasswordData
{
    @Id
    @GeneratedValue
    @Column(name="id")
    private long id;
    
    @Column(name="password", length=108, nullable=false)
    //encrypted
    private String password;
    
    @Column(name="date_created", insertable=false, updatable=false, nullable=false)
    @Generated(GenerationTime.INSERT)
    private Date dateCreated;
    
    @ManyToOne
    @JoinColumn(name="user_created_id", nullable=false, updatable=false)
    private User userCreated;
    
    @ManyToOne
    @JoinColumn(name="password_id", nullable=false)
    private Password parent;

    public PasswordData()
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

    public String getPassword()
    {
        return this.password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public Date getDateCreated()
    {
        return this.dateCreated;
    }

    public void setDateCreated(Date dateCreated)
    {
        this.dateCreated = dateCreated;
    }

    public User getUserCreated()
    {
        return this.userCreated;
    }

    public void setUserCreated(User userCreated)
    {
        this.userCreated = userCreated;
    }

    public Password getParent()
    {
        return this.parent;
    }

    public void setParent(Password parent)
    {
        this.parent = parent;
    }
    
}
