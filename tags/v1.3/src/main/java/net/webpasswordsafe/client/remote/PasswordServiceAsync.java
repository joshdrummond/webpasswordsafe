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
package net.webpasswordsafe.client.remote;

import java.util.Collection;
import java.util.List;
import net.webpasswordsafe.common.model.Password;
import net.webpasswordsafe.common.model.PasswordAccessAudit;
import net.webpasswordsafe.common.model.PasswordData;
import net.webpasswordsafe.common.model.Tag;
import net.webpasswordsafe.common.model.Template;
import net.webpasswordsafe.common.util.Constants.Match;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * 
 * @author Josh Drummond
 *
 */
public interface PasswordServiceAsync {
    
    public void addPassword(Password password, AsyncCallback<Void> callback);
    public void updatePassword(Password password, AsyncCallback<Void> callback);
    public void searchPassword(String query, boolean activeOnly, Collection<Tag> tags, Match tagMatch, AsyncCallback<List<Password>> callback);
    public void generatePassword(AsyncCallback<String> callback);
    public void getCurrentPassword(long passwordId, AsyncCallback<String> callback);
    public void getAllTags(AsyncCallback<List<Tag>> callback);
    public void getAvailableTags(AsyncCallback<List<Tag>> callback);
    public void getPassword(long passwordId, AsyncCallback<Password> callback);
    public void isPasswordTaken(String passwordName, String username, long ignorePasswordId, AsyncCallback<Boolean> callback);
    public void getPasswordAccessAuditData(long passwordId, AsyncCallback<List<PasswordAccessAudit>> callback);
    public void getPasswordHistoryData(long passwordId, AsyncCallback<List<PasswordData>> callback);
    public void addTemplate(Template template, AsyncCallback<Void> callback);
    public void updateTemplate(Template template, AsyncCallback<Void> callback);
    public void deleteTemplate(Template template, AsyncCallback<Void> callback);
    public void getTemplates(boolean includeShared, AsyncCallback<List<Template>> callback);
    public void getTemplateWithDetails(long templateId, AsyncCallback<Template> callback);
    public void isTemplateTaken(String templateName, long ignoreTemplateId, AsyncCallback<Boolean> callback);

}
