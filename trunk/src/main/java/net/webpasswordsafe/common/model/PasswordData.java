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
 * Domain model POJO for a password_data
 * 
 * @author Josh Drummond
 *
 */
@Entity
@Table(name="password_data")
public class PasswordData extends LightEntity implements Serializable
{
    private static final long serialVersionUID = -643822521564959563L;
    public static final int LENGTH_PASSWORD = 100;
    public static final int LENGTH_PASSWORD_CRYPTED = 300;

    @Id
    @GeneratedValue
    @Column(name="id")
    private long id;

    @Column(name="password", length=LENGTH_PASSWORD_CRYPTED, updatable=false, nullable=false)
    private String password;
    
    @Column(name="date_created", updatable=false, nullable=false)
    private Date dateCreated;
    
    @ManyToOne
    @JoinColumn(name="user_created_id", updatable=false, nullable=false)
    private User userCreated;
    
    @ManyToOne
    @JoinColumn(name="password_id", updatable=false, insertable=false, nullable=true)
    private Password parent;

    @Column(name="password_position", nullable=true)
    private int passwordPosition;

    public PasswordData()
    {
    }

    public PasswordData(String password, Date dateCreated, User userCreated)
    {
        this.password = password;
        this.dateCreated = dateCreated;
        this.userCreated = userCreated;
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

    public int getPasswordPosition()
    {
        return this.passwordPosition;
    }

    public void setPasswordPosition(int passwordPosition)
    {
        this.passwordPosition = passwordPosition;
    }
    
    @Override
    public String toString()
    {
        return "PasswordData [id=" + this.id + "]";
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (this.id ^ (this.id >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (!(obj instanceof PasswordData))
        {
            return false;
        }
        PasswordData other = (PasswordData) obj;
        if (this.id != other.id)
        {
            return false;
        }
        return true;
    }
}
