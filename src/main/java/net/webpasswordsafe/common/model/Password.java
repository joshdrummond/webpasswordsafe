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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import net.sf.gilead.pojo.gwt.LightEntity;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Type;


/**
 * Domain model POJO for a password
 * 
 * @author Josh Drummond
 *
 */
@Entity
@Table(name="passwords")
public class Password extends LightEntity implements Serializable
{
    private static final long serialVersionUID = 7174192307771387126L;
    public static final int LENGTH_NAME = 100;
    public static final int LENGTH_USERNAME = 100;
    public static final int LENGTH_TARGET = 64;

    @Id
    @GeneratedValue
    @Column(name="id")
    private long id;
    
    @Column(name="name", length=LENGTH_NAME, nullable=false)
    private String name;
    
    @Column(name="username", length=LENGTH_USERNAME, nullable=false)
    private String username;
    
    @Column(name="target", length=LENGTH_TARGET)
    private String target;

    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    @Column(name="notes")
    private String notes;
    
    @Column(name="max_history", nullable=false)
    private int maxHistory;
    
    @Column(name="active", nullable=false)
    @Type(type="yes_no")
    private boolean active;
    
    @Column(name="date_created", updatable=false, nullable=false)
    private Date dateCreated;
    
    @ManyToOne
    @JoinColumn(name="user_created_id", updatable=false, nullable=false)
    private User userCreated;
    
    @Column(name="date_last_update", nullable=false)
    private Date dateLastUpdate;
    
    @ManyToOne
    @JoinColumn(name="user_last_update_id", nullable=false)
    private User userLastUpdate;
    
    @OneToMany(cascade={CascadeType.ALL}, orphanRemoval=true)
    @JoinColumn(name="password_id")
    @IndexColumn(name="password_position")
    private List<PasswordData> passwordData;
    
    @ManyToMany(cascade={CascadeType.ALL})
    @JoinTable(name="password_tags",
            joinColumns={@JoinColumn(name="password_id")},
            inverseJoinColumns={@JoinColumn(name="tag_id")})
    private Set<Tag> tags;

    @OneToMany(cascade={CascadeType.ALL}, orphanRemoval=true, mappedBy="password")
    private Set<Permission> permissions;
    
    @Transient
    private AccessLevel maxEffectiveAccessLevel;

    public Password()
    {
        maxHistory = -1;
        active = true;
        target = "";
        passwordData = new ArrayList<PasswordData>();
        tags = new HashSet<Tag>();
        permissions = new HashSet<Permission>();
    }
    
    public void addPermission(Permission permission)
    {
        permission.setPassword(this);
        this.permissions.add(permission);
    }
    
    public void clearPermissions()
    {
        this.permissions.clear();
    }
    
    public void removePermission(Permission permission)
    {
        this.permissions.remove(permission);
    }
    
    public void removePermissionsBySubject(Subject subject)
    {
        Set<Permission> removeSet = new HashSet<Permission>();
        for (Permission permission : permissions)
        {
            if (permission.getSubject().getId() == subject.getId())
            {
                removeSet.add(permission);
            }
        }
        permissions.removeAll(removeSet);
    }
    
    public Set<Permission> getPermissions() {
        return this.permissions;
    }

    protected void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }
    
    public Set<Tag> getTags()
    {
        return this.tags;
    }

    protected void setTags(Set<Tag> tags)
    {
        this.tags = tags;
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
    public String getUsername()
    {
        return this.username;
    }
    public void setUsername(String username)
    {
        this.username = username;
    }
    public String getTarget()
    {
        return this.target;
    }
    public void setTarget(String target)
    {
        this.target = target;
    }
    public String getNotes()
    {
        return this.notes;
    }
    public void setNotes(String notes)
    {
        this.notes = notes;
    }
    public int getMaxHistory()
    {
        return this.maxHistory;
    }
    public void setMaxHistory(int maxHistory)
    {
        this.maxHistory = maxHistory;
    }
    public boolean isActive()
    {
        return this.active;
    }
    public void setActive(boolean active)
    {
        this.active = active;
    }
    public Date getDateCreated()
    {
        return this.dateCreated;
    }
    public void setDateCreated(Date dateCreated)
    {
        this.dateCreated = dateCreated;
    }
    public User getUserCreated()
    {
        return this.userCreated;
    }
    public void setUserCreated(User userCreated)
    {
        this.userCreated = userCreated;
    }
    public Date getDateLastUpdate()
    {
        return this.dateLastUpdate;
    }
    public void setDateLastUpdate(Date dateLastUpdate)
    {
        this.dateLastUpdate = dateLastUpdate;
    }
    public User getUserLastUpdate()
    {
        return this.userLastUpdate;
    }
    public void setUserLastUpdate(User userLastUpdate)
    {
        this.userLastUpdate = userLastUpdate;
    }
    public List<PasswordData> getPasswordData()
    {
        return this.passwordData;
    }
    public PasswordData getCurrentPasswordData()
    {
        return this.passwordData.get(0);
    }
    protected void setPasswordData(List<PasswordData> passwordData)
    {
        this.passwordData = passwordData;
    }
    public void addPasswordData(PasswordData passwordDataItem)
    {
        passwordDataItem.setParent(this);
        this.passwordData.add(0, passwordDataItem);
    }
    
    public void pruneDataHistory()
    {
        if ((maxHistory > -1) && ((passwordData.size()-1) > maxHistory))
        {
            int start = passwordData.size() - 1;
            int end = maxHistory + 1;
            for (int i = start; i >= end; i--)
            {
                passwordData.remove(i);
            }
        }
    }
    
    public void removePasswordData()
    {
        passwordData.clear();
    }
    
    public String getTagsAsString()
    {
        StringBuilder tagString = new StringBuilder();
        Set<Tag> sortedTags = new TreeSet<Tag>(tags);
        for (Tag tag : sortedTags)
        {
            tagString.append(tag.getName()).append(" ");
        }
        return tagString.toString().trim();
    }

    public void addTag(Tag tag)
    {
        tag.getPasswords().add(this);
        tags.add(tag);
    }
    
    public void removeTags()
    {
        for (Tag tag : tags)
        {
            tag.getPasswords().remove(this);
        }
        tags.clear();
    }

    public AccessLevel getMaxEffectiveAccessLevel()
    {
        return maxEffectiveAccessLevel;
    }

    public void setMaxEffectiveAccessLevel(AccessLevel maxEffectiveAccessLevel)
    {
        this.maxEffectiveAccessLevel = maxEffectiveAccessLevel;
    }

}
