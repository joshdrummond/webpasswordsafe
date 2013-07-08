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
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import net.sf.gilead.pojo.gwt.LightEntity;
import org.hibernate.annotations.Type;


/**
 * Domain model POJO for a template
 * 
 * @author Josh Drummond
 *
 */
@Entity
@Table(name="templates")
public class Template extends LightEntity implements Serializable
{
    private static final long serialVersionUID = 4904231831095270401L;
    public static final int LENGTH_NAME = 100;

    @Id
    @GeneratedValue
    @Column(name="id")
    private long id;
    
    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;
    
    @Column(name="name", length=LENGTH_NAME, nullable=false, unique=true)
    private String name;
    
    @Column(name="shared", nullable=false)
    @Type(type="yes_no")
    private boolean shared;
    
    @OneToMany(cascade={CascadeType.ALL}, orphanRemoval=true, mappedBy="parent")
    private Set<TemplateDetail> templateDetails;

    public Template()
    {
        name = "";
        shared = true;
        templateDetails = new HashSet<TemplateDetail>();
    }
    
    public long getId()
    {
        return this.id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public User getUser()
    {
        return this.user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean isShared()
    {
        return this.shared;
    }

    public void setShared(boolean shared)
    {
        this.shared = shared;
    }

    public Set<TemplateDetail> getTemplateDetails()
    {
        return this.templateDetails;
    }

    public void setTemplateDetails(Set<TemplateDetail> templateDetails)
    {
        this.templateDetails = templateDetails;
    }
    
    public void addDetail(TemplateDetail detail)
    {
        detail.setParent(this);
        templateDetails.add(detail);
    }
    
    public void clearDetails()
    {
        this.templateDetails.clear();
    }
    
    public void removeDetailsBySubject(Subject subject)
    {
        Set<TemplateDetail> removeSet = new HashSet<TemplateDetail>();
        for (TemplateDetail detail : templateDetails)
        {
            if (detail.getSubject().getId() == subject.getId())
            {
                removeSet.add(detail);
            }
        }
        templateDetails.removeAll(removeSet);
    }
}
