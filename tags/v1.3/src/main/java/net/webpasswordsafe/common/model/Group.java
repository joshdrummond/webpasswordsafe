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

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;


/**
 * Domain model POJO for a group
 * 
 * @author Josh Drummond
 *
 */
@Entity
@Table(name="groups")
@PrimaryKeyJoinColumn(name="id")
public class Group extends Subject
{
    private static final long serialVersionUID = 5845591346545424763L;
    public static final int LENGTH_NAME = 100;

    @Column(name="name", length=LENGTH_NAME, nullable=false, unique=true)
    private String name;

    @ManyToMany(cascade={CascadeType.ALL})
    @JoinTable(name="user_groups",
            joinColumns={@JoinColumn(name="group_id")},
            inverseJoinColumns={@JoinColumn(name="user_id")})
    private Set<User> users;
    
    public Group() {
        this("");
    }

    public Group( String name ) {
        super('G');
        this.name = name;
        users = new HashSet<User>();
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
        user.addGroup(this);
        users.add(user);
    }
    
    public void removeUser(User user)
    {
        users.remove(user);
    }
    
    public void removeUsers()
    {
        for (User user : users)
        {
            user.removeGroup(this);
        }
        users.clear();
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof Group ) ) {
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
