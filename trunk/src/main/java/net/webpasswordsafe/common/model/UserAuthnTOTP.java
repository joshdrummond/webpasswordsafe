/*
    Copyright 2013 Josh Drummond

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
import org.hibernate.annotations.Type;
import net.sf.gilead.pojo.gwt.LightEntity;


/**
 * Domain model POJO for a user authn time-based one time password
 * 
 * @author Josh Drummond
 *
 */
@Entity
@Table(name="user_authn_totp")
public class UserAuthnTOTP extends LightEntity implements Serializable
{
    private static final long serialVersionUID = -8013071060505686608L;
    public static final int LENGTH_KEY = 16;
    public static final int LENGTH_KEY_CRYPTED = 200;

    @Id
    @GeneratedValue
    @Column(name="id")
    private long id;
    
    @Column(name="secret_key", length=LENGTH_KEY_CRYPTED)
    private String key;

    @Column(name="enabled", nullable=false)
    @Type(type = "yes_no")
    private boolean enabled;

    @ManyToOne
    @JoinColumn(name="user_id", updatable=false, nullable=false, unique=true)
    private User user;
    
    public UserAuthnTOTP()
    {
        enabled = false;
        key = "";
    }
    
    @Override
    public String toString()
    {
        return "UserAuthnTOTP [id=" + id + ", key=" + key + ", enabled="
                + enabled + ", user=" + user + "]";
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

}
