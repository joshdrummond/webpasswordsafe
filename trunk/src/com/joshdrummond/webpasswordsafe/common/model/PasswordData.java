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
package com.joshdrummond.webpasswordsafe.common.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import net.sf.gilead.pojo.java5.LightEntity;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.Parameter;
import org.jasypt.hibernate.type.EncryptedStringType;


@TypeDef(name="encryptedString", typeClass=EncryptedStringType.class,
    parameters={@Parameter(name="encryptorRegisteredName", value="strongHibernateStringEncryptor")})

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

	@Id
    @GeneratedValue
    @Column(name="id")
    private long id;

    @Column(name="password", length=128, updatable=false, nullable=false)
    @Type(type="encryptedString")
    private String password;
    
    @Column(name="date_created", updatable=false, nullable=false)
    private Date dateCreated;
    
    @ManyToOne
    @JoinColumn(name="user_created_id", updatable=false, nullable=false)
    private User userCreated;
    
    @ManyToOne
    @JoinColumn(name="password_id", nullable=false)
    private Password parent;

    @Column(name="password_position", nullable=false)
    private int passwordPosition;

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

    public int getPasswordPosition()
    {
        return this.passwordPosition;
    }

    public void setPasswordPosition(int passwordPosition)
    {
        this.passwordPosition = passwordPosition;
    }
    
}
