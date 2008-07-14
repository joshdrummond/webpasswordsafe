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

import java.util.Date;

/**
 * @author Josh Drummond
 *
 */
public class PasswordDataDTO
{
    private long id;
    private String password;
    private Date dateCreated;
    private UserDTO userCreated;
    
    /**
     * 
     */
    public PasswordDataDTO()
    {
    }
    /**
     * @param id
     * @param password
     */
    public PasswordDataDTO(long id, String password)
    {
        this.id = id;
        this.password = password;
    }
    /**
     * @param password
     */
    public PasswordDataDTO(String password)
    {
        this.password = password;
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
     * @return the password
     */
    public String getPassword()
    {
        return this.password;
    }
    /**
     * @param password the password to set
     */
    public void setPassword(String password)
    {
        this.password = password;
    }
    /**
     * @return the dateCreated
     */
    public Date getDateCreated()
    {
        return this.dateCreated;
    }
    /**
     * @param dateCreated the dateCreated to set
     */
    public void setDateCreated(Date dateCreated)
    {
        this.dateCreated = dateCreated;
    }
    /**
     * @return the userCreated
     */
    public UserDTO getUserCreated()
    {
        return this.userCreated;
    }
    /**
     * @param userCreated the userCreated to set
     */
    public void setUserCreated(UserDTO userCreated)
    {
        this.userCreated = userCreated;
    }
    
}
