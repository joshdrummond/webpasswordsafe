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
package net.webpasswordsafe.server.dao;

import java.util.Collection;
import java.util.List;
import net.webpasswordsafe.common.model.AccessLevel;
import net.webpasswordsafe.common.model.Password;
import net.webpasswordsafe.common.model.Subject;
import net.webpasswordsafe.common.model.Tag;
import net.webpasswordsafe.common.model.User;
import net.webpasswordsafe.common.util.Constants.Match;


/**
 * DAO interface for Password
 *  
 * @author Josh Drummond
 *
 */
public interface PasswordDAO extends GenericDAO<Password, Long> {

    public List<Password> findPasswordByFuzzySearch(String query, User user, boolean activeOnly, Collection<Tag> tags, Match tagMatch);
    public Password findPasswordByName(String passwordName, String username);
    public Password findAllowedPasswordById(long passwordId, User user, AccessLevel accessLevel);
    public List<Password> findPasswordsByPermissionSubject(Subject subject);
    public AccessLevel getMaxEffectiveAccessLevel(Password password, User user);

}
