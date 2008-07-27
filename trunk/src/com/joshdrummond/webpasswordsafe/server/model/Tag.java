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

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 * POJO model for a tag
 * 
 * @author Josh Drummond
 *
 */
@Entity
@Table(name="tags")
public class Tag
{
    @Id
    @GeneratedValue
    @Column(name="id")
    private long id;
    
    @Column(name="name", nullable=false, updatable=false, unique=false)
    private String name;
    
    @ManyToMany(mappedBy="tags")
    private Set<Password> passwords;
    
    public Tag()
    {
        passwords = new HashSet<Password>();
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
    
}
