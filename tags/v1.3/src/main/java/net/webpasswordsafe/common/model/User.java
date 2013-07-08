/*
    Copyright 2008-2013 Josh Drummond

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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import net.webpasswordsafe.common.util.Constants;
import net.webpasswordsafe.common.util.Utils;
import org.hibernate.annotations.Type;


/**
 * Domain model POJO for a user
 * 
 * @author Josh Drummond
 *
 */
@Entity
@Table(name="users")
@PrimaryKeyJoinColumn(name="id")
public class User extends Subject
{
    private static final long serialVersionUID = 4024780603653185462L;
    public static final int LENGTH_USERNAME = 100;
    public static final int LENGTH_FULLNAME = 100;
    public static final int LENGTH_EMAIL = 100;

    @Column(name="username", length=LENGTH_USERNAME, nullable=false, updatable=false, unique=true)
    private String username;

    @OneToMany(cascade={CascadeType.ALL}, orphanRemoval=true, mappedBy="user")
    private Set<UserAuthnPassword> authnPassword;
    
    @Column(name="fullname", length=LENGTH_FULLNAME, nullable=false)
    private String fullname;

    @Column(name="email", length=LENGTH_EMAIL, nullable=false)
    private String email;

    @Column(name="active", nullable=false)
    @Type(type = "yes_no")
    private boolean activeFlag;

    @Column(name="date_created", nullable=false)
    private Date dateCreated;

    @Column(name="last_login")
    private Date lastLogin;

    @ManyToMany(mappedBy="users")
    private Set<Group> groups;
    
    @Transient
    private Set<Constants.Role> roles;
    
    public User() {
        super('U');
        groups = new HashSet<Group>();
        roles = new HashSet<Constants.Role>();
        activeFlag = true;
        authnPassword = new HashSet<UserAuthnPassword>(1);
    }

    public static User newActiveUser(String username, String password, String fullname, String email)
    {
        User user = new User();
        user.setUsername(username);
        user.setFullname(fullname);
        user.setEmail(email);
        user.setActiveFlag(true);
        user.setDateCreated(new Date());
        user.updateAuthnPasswordValue(password);
        return user;
    }
    
    public Set<Constants.Role> getRoles()
    {
        return this.roles;
    }

    public void setRoles(Set<Constants.Role> roles)
    {
        this.roles.clear();
        this.roles.addAll(roles);
    }

    public Set<Group> getGroups()
    {
        return this.groups;
    }

    public void setGroups(Set<Group> groups)
    {
        this.groups = groups;
    }
    
    public void addGroup(Group group)
    {
        groups.add(group);
    }
    
    public void removeGroup(Group group)
    {
        groups.remove(group);
    }
    
    public void removeGroups()
    {
        for (Group group : groups)
        {
            group.removeUser(this);
        }
        groups.clear();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername( String username ) {
        this.username = username;
    }

    public void setAuthnPassword(Set<UserAuthnPassword> authnPassword)
    {
        this.authnPassword = authnPassword;
    }

    public Set<UserAuthnPassword> getAuthnPassword() {
        return authnPassword;
    }
    
    public String getAuthnPasswordValue() {
        Set<UserAuthnPassword> uap = getAuthnPassword();
        return ((uap == null) || (uap.size() == 0)) ? "" : uap.iterator().next().getPassword();
    }
    
    // can't be the same as the get() name or else it will treat as bean field and load at inappropriate times
    public void updateAuthnPasswordValue(String password) {
        Set<UserAuthnPassword> uap = getAuthnPassword();
        if ((uap == null) || (uap.size() == 0))
        {
            setAuthnPassword((uap == null) ? new HashSet<UserAuthnPassword>(1) : uap);
            addAuthnPassword(new UserAuthnPassword(password));
        }
        else
        {
            UserAuthnPassword p = uap.iterator().next();
            p.setPassword(password);
        }
    }

    public void addAuthnPassword(UserAuthnPassword p)
    {
        p.setUser(this);
        authnPassword.add(p);
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname( String fullname ) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail( String email ) {
        if (!Utils.isValidEmail(email))
            throw new RuntimeException("Email invalid");
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
    public String getName()
    {
        return getFullname();
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id='" + getId() + '\'' +
                "username='" + username + '\'' +
                ", fullName='" + fullname + '\'' +
                ", email='" + email + '\'' +
                ", activeFlag=" + activeFlag +
                '}';
    }

    @Override
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
        if ( fullname != null ? !fullname.equals( user.fullname ) : user.fullname != null ) {
            return false;
        }
        if ( !username.equals( user.username ) ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + username.hashCode();
        result = 31 * result + ( fullname != null ? fullname.hashCode() : 0 );
        result = 31 * result + ( email != null ? email.hashCode() : 0 );
        result = 31 * result + ( activeFlag ? 1 : 0 );
        return result;
    }

}
