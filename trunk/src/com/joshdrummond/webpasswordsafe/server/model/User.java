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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

/**
 * POJO model for a user
 * 
 * @author Josh Drummond
 *
 */
@Entity
@Table(name = "users")
@PrimaryKeyJoinColumn(name = "id")
public class User extends Subject {
    @Column(name = "username", nullable = false, length = 64, updatable = false, unique = true)
    @Index(name = "idx_user_username")
    private String userName;

    @Column(name = "password", nullable = false, length = 64)
    private String password;

    @Column(name = "fullname", length = 100, nullable=false)
    private String fullName;

    @Column(name = "email", length = 100, nullable=false)
    private String email;

    @Column(name = "active", nullable = false)
    @Type(type = "yes_no")
    private boolean activeFlag;

    @Column(name = "date_created", nullable = false)
    private Date dateCreated;

    @Column(name = "last_login")
    private Date lastLogin;

    @ManyToMany
    @JoinTable(name="user_groups",
            joinColumns={@JoinColumn(name="user_id")},
            inverseJoinColumns={@JoinColumn(name="group_id")})
    private Set<Group> groups;
    
    public User() {
        super( 'U' );
        groups = new HashSet<Group>();
    }

    public User( String userName, String password ) {
        this();
        this.userName = userName;
        this.password = password;
    }

    public Set<Group> getGroups()
    {
        return this.groups;
    }

    public void setGroups(Set<Group> groups)
    {
        this.groups = groups;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName( String userName ) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword( String password ) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName( String fullName ) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail( String email ) {
        this.email = email;
    }

    public boolean isActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag( boolean activeFlag ) {
        this.activeFlag = activeFlag;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated( Date dateCreated ) {
        this.dateCreated = dateCreated;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin( Date lastLogin ) {
        this.lastLogin = lastLogin;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + getId() + '\'' +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", activeFlag=" + activeFlag +
                '}';
    }

    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof User ) ) {
            return false;
        }
        if ( !super.equals( o ) ) {
            return false;
        }

        User user = (User)o;

        if ( activeFlag != user.activeFlag ) {
            return false;
        }
        if ( email != null ? !email.equals( user.email ) : user.email != null ) {
            return false;
        }
        if ( fullName != null ? !fullName.equals( user.fullName ) : user.fullName != null ) {
            return false;
        }
        if ( !password.equals( user.password ) ) {
            return false;
        }
        if ( !userName.equals( user.userName ) ) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + userName.hashCode();
        result = 31 * result + password.hashCode();
        result = 31 * result + ( fullName != null ? fullName.hashCode() : 0 );
        result = 31 * result + ( email != null ? email.hashCode() : 0 );
        result = 31 * result + ( activeFlag ? 1 : 0 );
        return result;
    }

}
