/*
    Copyright 2010-2013 Josh Drummond

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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import net.sf.gilead.pojo.gwt.LightEntity;


/**
 * Domain model POJO for a user authn password
 * 
 * @author Josh Drummond
 *
 */
@Entity
@Table(name="user_authn_password")
public class UserAuthnPassword extends LightEntity implements Serializable
{
    private static final long serialVersionUID = -212604787809969177L;
    public static final int LENGTH_PASSWORD = 100;
    public static final int LENGTH_PASSWORD_CRYPTED = 100;

    @Id
    @GeneratedValue
    @Column(name="id")
    private long id;
    
    @Column(name="password", length=LENGTH_PASSWORD_CRYPTED, nullable=false)
    private String password;

    @ManyToOne
    @JoinColumn(name="user_id", updatable=false, nullable=false, unique=true)
    private User user;
    
    public UserAuthnPassword()
    {
    }
    
    public UserAuthnPassword(String password)
    {
        setPassword(password);
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    @Override
    public String toString()
    {
        return "UserAuthnPassword [id=" + id + ", password=" + password
                + ", user=" + user + "]";
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result
                + ((password == null) ? 0 : password.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserAuthnPassword other = (UserAuthnPassword) obj;
        if (id != other.id)
            return false;
        if (password == null)
        {
            if (other.password != null)
                return false;
        } else if (!password.equals(other.password))
            return false;
        return true;
    }

}
