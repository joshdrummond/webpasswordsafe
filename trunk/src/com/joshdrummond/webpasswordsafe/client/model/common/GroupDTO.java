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
import java.util.List;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Josh Drummond
 * 
 */
public class GroupDTO
    extends SubjectDTO
    implements IsSerializable
{
    private String name;
    private transient List users;
    /**
     * 
     */
    public GroupDTO()
    {
        super();
        users = new ArrayList();
    }
    /**
     * @return the name
     */
    public String getName()
    {
        return this.name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }
    /**
     * @return the users
     */
    public List getUsers()
    {
        return this.users;
    }
    /**
     * @param users the users to set
     */
    public void setUsers(List users)
    {
        this.users = users;
    }
    /**
     * @param userDTO
     */
    public void addUser(UserDTO user)
    {
        users.add(user);
    }

}
