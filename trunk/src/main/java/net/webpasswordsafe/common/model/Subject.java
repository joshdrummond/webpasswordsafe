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
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import net.sf.gilead.pojo.gwt.LightEntity;


/**
 * Domain model POJO for a subject
 * 
 * @author Josh Drummond
 * 
 */
@Entity
@Table(name="subjects")
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class Subject extends LightEntity implements Serializable
{
    private static final long serialVersionUID = -4007345820647655599L;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="id", insertable=false)
    private long id;

    @Column(name="type", length=1, nullable=false, updatable=false)
    private char type;

    public Subject()
    {
    }

    public Subject(char type)
    {
        this.type = type;
    }

    public char getType()
    {
        return type;
    }

    public void setType(char type)
    {
        this.type = type;
    }

    public void setId(long id)
    {
        this.id = id;
    }
    
    public long getId()
    {
        return id;
    }

    public abstract String getName();
    
    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof Subject))
        {
            return false;
        }

        Subject subject = (Subject) o;

        if (id != subject.id)
        {
            return false;
        }
        if (type != subject.type)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result;
        result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) type;
        return result;
    }

    @Override
    public String toString()
    {
        return "Subject{" + "id=" + id + ", type=" + type + '}';
    }
}
