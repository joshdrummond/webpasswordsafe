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
package com.joshdrummond.webpasswordsafe.client.model.common;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Josh Drummond
 *
 */
public class PermissionDTO
    implements IsSerializable
{
    private long id;
    private SubjectDTO subject;
    private int accessLevel;
    /**
     * 
     */
    public PermissionDTO()
    {
    }
    /**
     * @param accessLevel
     * @param subject
     */
    public PermissionDTO(SubjectDTO subject, int accessLevel)
    {
        this.accessLevel = accessLevel;
        this.subject = subject;
    }
    /**
     * @return the id
     */
    public long getId()
    {
        return this.id;
    }
    /**
     * @param id the id to set
     */
    public void setId(long id)
    {
        this.id = id;
    }
    /**
     * @return the subject
     */
    public SubjectDTO getSubject()
    {
        return this.subject;
    }
    /**
     * @param subject the subject to set
     */
    public void setSubject(SubjectDTO subject)
    {
        this.subject = subject;
    }
    /**
     * @return the accessLevel
     */
    public int getAccessLevel()
    {
        return this.accessLevel;
    }
    /**
     * @param accessLevel the accessLevel to set
     */
    public void setAccessLevel(int accessLevel)
    {
        this.accessLevel = accessLevel;
    }

}
