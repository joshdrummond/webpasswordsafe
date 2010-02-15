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
import org.hibernate.annotations.Cascade;
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

	@Id
    @GeneratedValue
    @Column(name="id")
    private long id;
    
    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;
    
    @Column(name="name", length=100, nullable=false, unique=true)
    private String name;
    
    @Column(name="share", nullable=false)
    @Type(type="yes_no")
    private boolean share;
    
    @OneToMany(cascade={CascadeType.ALL}, mappedBy="parent")
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN) 
    private Set<TemplateDetail> templateDetails;

    public Template()
    {
        name = "";
        share = true;
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

    public boolean isShare()
    {
        return this.share;
    }

    public void setShare(boolean share)
    {
        this.share = share;
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
    
}
