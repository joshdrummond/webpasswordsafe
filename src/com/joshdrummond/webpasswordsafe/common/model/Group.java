/*
    Copyright 2008-2009 Josh Drummond

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

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import org.hibernate.annotations.Index;


/**
 * Domain model POJO for a group
 * 
 * @author Josh Drummond
 *
 */
@Entity
@Table(name = "groups")
@PrimaryKeyJoinColumn(name = "id")
public class Group extends Subject {

	private static final long serialVersionUID = 5845591346545424763L;

	@Column(name = "name", nullable = false, length = 100, unique=true)
    @Index(name = "idx_group_name")
    private String name;

    @ManyToMany(mappedBy="groups")
    private Set<User> users;
    
    public Group() {
        super( 'G' );
        users = new HashSet<User>();
    }

    public Group( String name ) {
        this();
        this.name = name;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Set<User> getUsers()
    {
        return this.users;
    }

    public void setUsers(Set<User> users)
    {
        this.users = users;
    }

    public void addUser(User user)
    {
        users.add(user);
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof Group ) ) {
            return false;
        }
        if ( !super.equals( o ) ) {
            return false;
        }

        Group group = (Group)o;

        if ( !name.equals( group.name ) ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id='" + getId() + '\'' +
                "name='" + name + '\'' +
                '}';
    }
}
