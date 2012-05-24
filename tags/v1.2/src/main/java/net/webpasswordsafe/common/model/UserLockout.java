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
 * Domain model POJO for an user_lockout
 * 
 * @author Josh Drummond
 *
 */
@Entity
@Table(name="user_lockout")
public class UserLockout extends LightEntity implements Serializable
{
    private static final long serialVersionUID = 241364219723780789L;

    @Id
    @GeneratedValue
    @Column(name="id")
    private long id;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false, updatable=false, unique=true)
    private User user;

    @Column(name="fail_count", nullable=false)
    private int failCount;

    public UserLockout()
    {
        this(null, 0);
    }
    
    public UserLockout(User user, int failCount)
    {
        this.user = user;
        this.failCount = failCount;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public int getFailCount()
    {
        return failCount;
    }

    public void setFailCount(int failCount)
    {
        this.failCount = failCount;
    }

}
