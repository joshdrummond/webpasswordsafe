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

import java.util.Date;
import java.util.StringTokenizer;
import com.joshdrummond.webpasswordsafe.client.model.common.PasswordDTO;
import com.joshdrummond.webpasswordsafe.server.dao.TagDAO;
import com.joshdrummond.webpasswordsafe.server.model.Password;
import com.joshdrummond.webpasswordsafe.server.model.PasswordData;
import com.joshdrummond.webpasswordsafe.server.model.Tag;

/**
 * DTO <-> Domain Object assembler for Password
 * 
 * @author Josh Drummond
 *
 */
public class PasswordAssembler
{
    public static Password createDO(PasswordDTO passwordDTO, TagDAO tagDAO)
    {
        Password password = null;
        if (null != passwordDTO)
        {
            // core data
            password = new Password();
            password.setName(passwordDTO.getName());
            password.setUsername(passwordDTO.getUsername());
            password.setNotes(passwordDTO.getNotes());
            password.setActive(passwordDTO.isActive());
            password.setMaxHistory(passwordDTO.getMaxHistory());
            // tags
            StringTokenizer st = new StringTokenizer(passwordDTO.getTags());
            while (st.hasMoreTokens())
            {
                String tagName = st.nextToken();
                Tag tag = tagDAO.findTagByName(tagName);
                if (tag == null)
                {
                    tag = new Tag(tagName);
                }
                tag.getPasswords().add(password);
                password.addTag(tag);
            }
            // set initial password
            PasswordData passwordDataItem = new PasswordData();
            passwordDataItem.setParent(password);
            passwordDataItem.setPassword(passwordDTO.getCurrentPassword());
            password.addPasswordData(passwordDataItem);
        }
        return password;
    }
    
    public static PasswordDTO buildDTO(Password password)
    {
        PasswordDTO passwordDTO = null;
        if (null != password)
        {
            passwordDTO = new PasswordDTO();
            passwordDTO.setId(password.getId());
            passwordDTO.setName(password.getName());
            passwordDTO.setUsername(password.getUsername());
            passwordDTO.setNotes(password.getNotes());
            passwordDTO.setMaxHistory(password.getMaxHistory());
            passwordDTO.setActive(password.isActive());
            passwordDTO.setDateCreated(new Date(password.getDateCreated().getTime()));
            passwordDTO.setUserCreated(UserAssembler.buildDTO(password.getUserCreated()));
            passwordDTO.setDateLastUpdated(new Date(password.getDateLastUpdate().getTime()));
            passwordDTO.setUserLastUpdated(UserAssembler.buildDTO(password.getUserLastUpdate()));
            passwordDTO.setTags(password.getTagsAsString());
        }
        return passwordDTO;
    }
}
