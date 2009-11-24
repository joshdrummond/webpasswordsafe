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
package com.joshdrummond.webpasswordsafe.client.ui;

import java.util.List;
import java.util.Set;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.joshdrummond.webpasswordsafe.client.remote.PasswordService;
import com.joshdrummond.webpasswordsafe.client.remote.UserService;
import com.joshdrummond.webpasswordsafe.common.model.Password;
import com.joshdrummond.webpasswordsafe.common.model.PasswordData;
import com.joshdrummond.webpasswordsafe.common.model.Permission;
import com.joshdrummond.webpasswordsafe.common.model.Subject;
import com.joshdrummond.webpasswordsafe.common.model.Tag;


/**
 * @author Josh Drummond
 *
 */
public class PasswordDialog extends Window implements PermissionListener
{
    private Password password;
    private TextField<String> nameTextBox;
    private TextField<String> usernameTextBox;
    private TextField<String> passwordTextBox;
    private TextField<String> tagsTextBox;
    private TextArea notesTextArea;
    private CheckBox activeCheckBox;
    private FormData formData = new FormData("98%"); 

    public PasswordDialog(Password password)
    {
        this.password = password;
        this.setHeading("Password");
        this.setModal(true);
        
        FormPanel form = new FormPanel();
        form.setHeaderVisible(false);
        form.setFrame(true);
        
        nameTextBox = new TextField<String>();
        nameTextBox.setFieldLabel("Name");
        form.add(nameTextBox, formData);
        
        usernameTextBox = new TextField<String>();
        usernameTextBox.setFieldLabel("Username");
        form.add(usernameTextBox, formData);

        passwordTextBox = new TextField<String>();
        passwordTextBox.setFieldLabel("Password");
        form.add(passwordTextBox, formData);

        Button generateButton = new Button("Generate Password", new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
                doGeneratePassword();
			}
		});
        form.add(generateButton, formData);
        
        if (password.getId() > 0)
        {
            Button currentButton = new Button("Current Password", new SelectionListener<ButtonEvent>()
            {
                @Override
                public void componentSelected(ButtonEvent ce)
                {
                    doGetCurrentPassword();
                }
            });
            form.add(currentButton, formData);
        }
        
        tagsTextBox = new TextField<String>();
        tagsTextBox.setFieldLabel("Tags");
        form.add(tagsTextBox, formData);

        notesTextArea = new TextArea();
        notesTextArea.setFieldLabel("Notes");
        form.add(notesTextArea, formData);

        activeCheckBox = new CheckBox();
        activeCheckBox.setBoxLabel("Active");
        form.add(activeCheckBox, formData);
        
        Button editPermissionsButton = new Button("Edit Permissions", new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
                doEditPermissions();
			}
		});
        form.add(editPermissionsButton, formData);

        Button saveButton = new Button("Save", new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
                doSave();
			}
		});

        Button cancelButton = new Button("Cancel", new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
                doCancel();
			}
		});
        
        form.setButtonAlign(HorizontalAlignment.CENTER);
        form.addButton(saveButton);
        form.addButton(cancelButton);
        
        setFields();
        
        this.add(form);
    }

    /**
     * 
     */
    protected void doGetCurrentPassword()
    {
        AsyncCallback<String> callback = new AsyncCallback<String>()
        {
            public void onFailure(Throwable caught)
            {
                MessageBox.alert("Error", caught.getMessage(), null);
            }

            public void onSuccess(String result)
            {
                passwordTextBox.setValue(result);
            }
        };
        PasswordService.Util.getInstance().getCurrentPassword(password.getId(), callback);
    }

    /**
     * 
     */
    protected void doGeneratePassword()
    {
        AsyncCallback<String> callback = new AsyncCallback<String>()
        {

            public void onFailure(Throwable caught)
            {
                MessageBox.alert("Error", caught.getMessage(), null);
            }

            public void onSuccess(String result)
            {
                passwordTextBox.setValue(result);
            }
            
        };
        PasswordService.Util.getInstance().generatePassword(callback);
    }

    /**
     * 
     */
    protected void doSave()
    {
        if (validateFields())
        {
            password.setName(safeString(nameTextBox.getValue()));
            password.setUsername(safeString(usernameTextBox.getValue()));
            PasswordData passwordDataItem = new PasswordData();
            passwordDataItem.setPassword(safeString(passwordTextBox.getValue()));
            password.addPasswordData(passwordDataItem);
            String[] tagNames = safeString(tagsTextBox.getValue()).replaceAll(",", " ").split(" ");
            password.removeTags();
            for (String tagName : tagNames)
            {
                tagName = tagName.trim();
                if (!"".equals(tagName))
                {
                    Tag tag = new Tag(tagName);
                    password.addTag(tag);
                }
            }
            password.setNotes(safeString(notesTextArea.getValue()));
            password.setActive(activeCheckBox.getValue());

            AsyncCallback<Void> callback = new AsyncCallback<Void>()
            {
                public void onFailure(Throwable caught)
                {
                    MessageBox.alert("Error", caught.getMessage(), null);
                }

                public void onSuccess(Void result)
                {
                    hide();
                }
            };
            if (password.getId() == 0)
            {
                PasswordService.Util.getInstance().addPassword(password, callback);
            }
            else
            {
                PasswordService.Util.getInstance().updatePassword(password, callback);
            }
        }
    }

    /**
     * @return
     */
    private boolean validateFields()
    {
        return true;
    }

    /**
     * 
     */
    protected void doEditPermissions()
    {
        AsyncCallback<List<Subject>> callback = new AsyncCallback<List<Subject>>()
        {
            public void onFailure(Throwable caught)
            {
                MessageBox.alert("Error", caught.getMessage(), null);
            }

            public void onSuccess(List<Subject> result)
            {
                doShowPermissionDialog(result);
            }
        };
        UserService.Util.getInstance().getSubjects(true, callback);
    }
    
    private void doShowPermissionDialog(List<Subject> subjects)
    {
        new PermissionDialog(this, password, subjects).show();
    }

    /**
     * 
     */
    private void setFields()
    {
        nameTextBox.setValue(password.getName());
        usernameTextBox.setValue(password.getUsername());
        tagsTextBox.setValue(password.getTagsAsString());
        notesTextArea.setValue(password.getNotes());
        activeCheckBox.setValue(password.isActive());
    }

    /**
     * 
     */
    protected void doCancel()
    {
        hide();
    }

    /* (non-Javadoc)
     * @see com.joshdrummond.webpasswordsafe.client.ui.PermissionListener#doPermissionsChanged(java.util.List)
     */
    public void doPermissionsChanged(Set<Permission> permissions)
    {
        password.clearPermissions();
        for (Permission permission : permissions)
        {
            password.addPermission(permission);
        }
    }

    public String safeString(String s)
    {
        return (null != s) ? s.trim() : "";
    }
}
