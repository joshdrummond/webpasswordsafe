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
package com.joshdrummond.webpasswordsafe.server.assembler;

import com.joshdrummond.webpasswordsafe.client.model.common.UserDTO;
import com.joshdrummond.webpasswordsafe.server.model.User;

/**
 * DTO <-> Domain Object assembler for User
 * 
 * @author Josh Drummond
 *
 */
public class UserAssembler
{

    public static UserDTO buildDTO(User user)
    {
        UserDTO userDTO = null;
        if (null != user)
        {
            userDTO = new UserDTO(user.getId(), user.getUsername(), user.getFullname(), 
                    user.getEmail(), user.isActiveFlag());
        }
        return userDTO;
    }

    public static User createDO(UserDTO userDTO)
    {
        User user = null;
        if (null != userDTO)
        {
            user = new User();
            user.setUsername(userDTO.getUsername());
            user.setFullname(userDTO.getFullname());
            user.setEmail(userDTO.getEmail());
            user.setPassword(userDTO.getPassword());
            user.setActiveFlag(userDTO.isActive());
            user.setDateCreated(userDTO.getDateCreated());
        }
        return user;
    }
    
    public static void updateDO(User user, UserDTO userDTO)
    {
        if ((user != null) && (userDTO != null))
        {
            user.setUsername(userDTO.getUsername());
            user.setFullname(userDTO.getFullname());
            user.setEmail(userDTO.getEmail());
            if (!userDTO.getPassword().equals(""))
            {
                user.setPassword(userDTO.getPassword());
            }
            user.setActiveFlag(userDTO.isActive());
        }
    }
}
