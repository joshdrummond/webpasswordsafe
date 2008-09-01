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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Josh Drummond
 * 
 */
public class UserDTO
    extends SubjectDTO
    implements IsSerializable
{

    private String username;
    private String password;
    private String fullname;
    private String email;
    private boolean isActive;
    private Date dateCreated;
    private Date lastLogin;
    private List<RoleDTO> roles;
    private List<GroupDTO> groups;
    

    /**
     * 
     */
    public UserDTO()
    {
        super();
        roles = new ArrayList<RoleDTO>();
        groups = new ArrayList<GroupDTO>();
    }

    /**
     * @param id
     * @param username
     * @param fullname
     * @param email
     * @param isActive
     */
    public UserDTO(long id, String username, String fullname, String email, boolean isActive)
    {
        this();
        this.email = email;
        this.fullname = fullname;
        this.id = id;
        this.isActive = isActive;
        this.username = username;
        this.password = "";
    }

    /**
     * @return the username
     */
    public String getUsername()
    {
        return this.username;
    }

    /**
     * @param username
     *            the username to set
     */
    public void setUsername(String username)
    {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword()
    {
        return this.password;
    }

    /**
     * @param password
     *            the password to set
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * @return the fullname
     */
    public String getFullname()
    {
        return this.fullname;
    }

    /**
     * @param fullname
     *            the fullname to set
     */
    public void setFullname(String fullname)
    {
        this.fullname = fullname;
    }

    /**
     * @return the email
     */
    public String getEmail()
    {
        return this.email;
    }

    /**
     * @param email
     *            the email to set
     */
    public void setEmail(String email)
    {
        this.email = email;
    }

    /**
     * @return the isActive
     */
    public boolean isActive()
    {
        return this.isActive;
    }

    /**
     * @param isActive
     *            the isActive to set
     */
    public void setActive(boolean isActive)
    {
        this.isActive = isActive;
    }

    /**
     * @return the dateCreated
     */
    public Date getDateCreated()
    {
        return this.dateCreated;
    }

    /**
     * @param dateCreated
     *            the dateCreated to set
     */
    public void setDateCreated(Date dateCreated)
    {
        this.dateCreated = dateCreated;
    }

    /**
     * @return the lastLogin
     */
    public Date getLastLogin()
    {
        return this.lastLogin;
    }

    /**
     * @param lastLogin
     *            the lastLogin to set
     */
    public void setLastLogin(Date lastLogin)
    {
        this.lastLogin = lastLogin;
    }

}
