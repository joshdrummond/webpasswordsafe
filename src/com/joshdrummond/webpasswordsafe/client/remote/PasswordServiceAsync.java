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
package com.joshdrummond.webpasswordsafe.client.remote;

import java.util.Collection;
import java.util.List;
import com.joshdrummond.webpasswordsafe.common.model.Password;
import com.joshdrummond.webpasswordsafe.common.model.PasswordAccessAudit;
import com.joshdrummond.webpasswordsafe.common.model.PasswordData;
import com.joshdrummond.webpasswordsafe.common.model.Tag;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * 
 * @author Josh Drummond
 *
 */
public interface PasswordServiceAsync {
    
    public void addPassword(Password password, AsyncCallback<Void> callback);
    public void updatePassword(Password password, AsyncCallback<Void> callback);
    public void searchPassword(String query, boolean activeOnly, Collection<Tag> tags, AsyncCallback<List<Password>> callback);
    public void generatePassword(AsyncCallback<String> callback);
    public void getCurrentPassword(long passwordId, AsyncCallback<String> callback);
    public void getAvailableTags(AsyncCallback<List<Tag>> callback);
    public void getPassword(long passwordId, AsyncCallback<Password> callback);
    public void getPasswordAccessAuditData(long passwordId, AsyncCallback<List<PasswordAccessAudit>> callback);
    public void getPasswordHistoryData(long passwordId, AsyncCallback<List<PasswordData>> callback);
}
