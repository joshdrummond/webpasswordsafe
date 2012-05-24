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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import net.sf.gilead.pojo.gwt.LightEntity;


/**
 * Domain model POJO for a permission
 * 
 * @author Josh Drummond
 *
 */
@Entity
@Table(name="permissions")
public class Permission extends LightEntity implements Serializable
{

    private static final long serialVersionUID = 4513050098482215994L;

    @Id
    @GeneratedValue
    @Column(name="id")
    private long id;
    
    @ManyToOne
    @JoinColumn(name="password_id", updatable=false, nullable=false)
    private Password password;
    
    @ManyToOne
    @JoinColumn(name="subject_id", updatable=false, nullable=false)
    private Subject subject;
    
    @Column(name="access_level", length=5, nullable=false)
    private String accessLevel;
    
    public Permission()
    {
    }
    
    public Permission(Subject subject, AccessLevel accessLevel)
    {
        this.subject = subject;
        this.accessLevel = accessLevel.name();
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
    public Subject getSubject()
    {
        return this.subject;
    }
    public void setSubject(Subject subject)
    {
        this.subject = subject;
    }
    public String getAccessLevel()
    {
        return this.accessLevel;
    }
    public AccessLevel getAccessLevelObj()
    {
        return AccessLevel.valueOf(this.accessLevel);
    }
    public void setAccessLevel(String accessLevel)
    {
        // test this is a valid accessLevel, will throw IllegalArgumentException if not
        AccessLevel.valueOf(accessLevel);
        this.accessLevel = accessLevel;
    }

    @Override
    public String toString()
    {
        return "Permission [accessLevel=" + this.accessLevel + ", id="
                + this.id + ", subject=" + this.subject + "]";
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((this.accessLevel == null) ? 0 : this.accessLevel.hashCode());
        result = prime * result + (int) (this.id ^ (this.id >>> 32));
        result = prime * result
                + ((this.subject == null) ? 0 : this.subject.hashCode());
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
        if (!(obj instanceof Permission))
        {
            return false;
        }
        Permission other = (Permission) obj;
        if (this.accessLevel == null)
        {
            if (other.accessLevel != null)
            {
                return false;
            }
        } else if (!this.accessLevel.equals(other.accessLevel))
        {
            return false;
        }
        if (this.id != other.id)
        {
            return false;
        }
        if (this.subject == null)
        {
            if (other.subject != null)
            {
                return false;
            }
        } else if (!this.subject.equals(other.subject))
        {
            return false;
        }
        return true;
    }

    
}
