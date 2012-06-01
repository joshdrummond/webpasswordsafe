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
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import net.sf.gilead.pojo.gwt.LightEntity;


/**
 * Domain model POJO for a tag
 * 
 * @author Josh Drummond
 *
 */
@Entity
@Table(name="tags")
public class Tag extends LightEntity implements Serializable, Comparable<Tag>
{
    private static final long serialVersionUID = 2413955215022013023L;
    public static final int LENGTH_NAME = 100;
    
    @Id
    @GeneratedValue
    @Column(name="id")
    private long id;
    
    @Column(name="name", length=LENGTH_NAME, nullable=false, updatable=false, unique=true)
    private String name;
    
    @ManyToMany(mappedBy="tags")
    private Set<Password> passwords;
    
    public Tag()
    {
        passwords = new HashSet<Password>();
    }
    
    public Tag(String name)
    {
        this();
        this.name = name;
    }
    
    public long getId()
    {
        return this.id;
    }
    public void setId(long id)
    {
        this.id = id;
    }
    public String getName()
    {
        return this.name;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public Set<Password> getPasswords()
    {
        return this.passwords;
    }

    public void setPasswords(Set<Password> passwords)
    {
        this.passwords = passwords;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((this.name == null) ? 0 : this.name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Tag)) {
            return false;
        }
        Tag other = (Tag) obj;
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Tag [name=" + this.name + "]";
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Tag otherTag)
    {
        return getName().compareTo(otherTag.getName());
    }

}
