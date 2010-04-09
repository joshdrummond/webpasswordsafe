/*
    Copyright 2008-2010 Josh Drummond

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
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.joshdrummond.webpasswordsafe.client.remote.PasswordService;
import com.joshdrummond.webpasswordsafe.client.remote.UserService;
import com.joshdrummond.webpasswordsafe.common.model.AccessLevel;
import com.joshdrummond.webpasswordsafe.common.model.Password;
import com.joshdrummond.webpasswordsafe.common.model.PasswordData;
import com.joshdrummond.webpasswordsafe.common.model.Permission;
import com.joshdrummond.webpasswordsafe.common.model.Subject;
import com.joshdrummond.webpasswordsafe.common.model.Tag;
import com.joshdrummond.webpasswordsafe.common.util.Utils;
import com.extjs.gxt.ui.client.widget.layout.AbsoluteLayout;
import com.extjs.gxt.ui.client.widget.layout.AbsoluteData;
import com.extjs.gxt.ui.client.widget.form.LabelField;


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
    private NumberField maxHistoryTextBox;
    private TextArea notesTextArea;
    private CheckBox activeCheckBox;

    public PasswordDialog(Password password)
    {
        this.password = password;
        this.setHeading("Password");
        this.setModal(true);
        this.setLayout(new AbsoluteLayout());
        this.setSize(430, 415);
        this.setResizable(false);
        
        LabelField lblfldName = new LabelField("Title:");
        add(lblfldName, new AbsoluteData(7, 6));
        
        nameTextBox = new TextField<String>();
        nameTextBox.setEnabled(password.getId() < 1);
        add(nameTextBox, new AbsoluteData(82, 6));
        nameTextBox.setSize("331px", "22px");

        LabelField lblfldUsername = new LabelField("Username:");
        add(lblfldUsername, new AbsoluteData(7, 34));
        
        usernameTextBox = new TextField<String>();
        usernameTextBox.setEnabled(password.getId() < 1);
        add(usernameTextBox, new AbsoluteData(82, 34));
        usernameTextBox.setSize("331px", "22px");

        LabelField lblfldPassword = new LabelField("Password:");
        add(lblfldPassword, new AbsoluteData(7, 62));
        
        passwordTextBox = new TextField<String>();
        add(passwordTextBox, new AbsoluteData(82, 62));
        passwordTextBox.setSize("331px", "22px");

        Button generateButton = new Button("Generate Password", new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
                doGeneratePassword();
			}
		});
        add(generateButton, new AbsoluteData(82, 90));
        generateButton.setSize("127px", "22px");
        
        Button currentButton = new Button("Current Password", new SelectionListener<ButtonEvent>()
        {
            @Override
            public void componentSelected(ButtonEvent ce)
            {
                doGetCurrentPassword();
            }
        });
        add(currentButton, new AbsoluteData(215, 90));
        currentButton.setSize("127px", "22px");
        currentButton.setEnabled(password.getId() > 0);

        Button historyButton = new Button("View Password History", new SelectionListener<ButtonEvent>()
        {
            @Override
            public void componentSelected(ButtonEvent ce)
            {
                doViewPasswordHistory();
            }
        });
        add(historyButton, new AbsoluteData(215, 311));
        historyButton.setSize("127px", "22px");
        historyButton.setEnabled(password.getId() > 0);

        LabelField lblfldTags = new LabelField("Tags:");
        add(lblfldTags, new AbsoluteData(6, 118));
            
        tagsTextBox = new TextField<String>();
        add(tagsTextBox, new AbsoluteData(82, 118));
        tagsTextBox.setSize("331px", "22px");

        LabelField lblfldNotes = new LabelField("Notes:");
        add(lblfldNotes, new AbsoluteData(6, 146));
        
        notesTextArea = new TextArea();
        add(notesTextArea, new AbsoluteData(82, 146));
        notesTextArea.setSize("331px", "75px");
        
        LabelField lblfldMaxHistory = new LabelField("Max History:");
        add(lblfldMaxHistory, new AbsoluteData(6, 227));
        
        maxHistoryTextBox = new NumberField();
        maxHistoryTextBox.setPropertyEditorType(Integer.class);
        add(maxHistoryTextBox, new AbsoluteData(82, 227));
        maxHistoryTextBox.setSize("76px", "22px");

        LabelField lblfldInfinite = new LabelField("(-1 infinite)");
        add(lblfldInfinite, new AbsoluteData(164, 227));
        
        activeCheckBox = new CheckBox();
        activeCheckBox.setBoxLabel("Active");
        add(activeCheckBox, new AbsoluteData(82, 255));
        
        Button editPermissionsButton = new Button(password.getMaxEffectiveAccessLevel().equals(AccessLevel.GRANT) ? 
                "Edit Permissions" : "View Permissions", new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
                doEditPermissions();
			}
		});
        add(editPermissionsButton, new AbsoluteData(82, 283));
        editPermissionsButton.setSize("260px", "22px");
        
        Button accessAuditButton = new Button("View Access Audit Log", new SelectionListener<ButtonEvent>()
        {
            @Override
            public void componentSelected(ButtonEvent ce)
            {
                doViewAccessAuditLog();
            }
        });
        add(accessAuditButton, new AbsoluteData(82, 311));
        accessAuditButton.setSize("127px", "22px");
        accessAuditButton.setEnabled(password.getId() > 0);

        Button saveButton = new Button("Save", new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
                doSave();
			}
		});
        saveButton.setEnabled(!password.getMaxEffectiveAccessLevel().equals(AccessLevel.READ));

        Button cancelButton = new Button("Cancel", new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
                doCancel();
			}
		});
        
        setButtonAlign(HorizontalAlignment.CENTER);
        addButton(saveButton);
        addButton(cancelButton);
        
        setFields();
    }

    
    private void doViewPasswordHistory()
    {
        new PasswordHistoryDialog(password).show();
    }
    
    
    private void doViewAccessAuditLog()
    {
        new PasswordAccessAuditDialog(password).show();
    }
    
    private void doGetCurrentPassword()
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

    private void doGeneratePassword()
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

    private void doSave()
    {
        if (validateFields())
        {
            password.setName(Utils.safeString(nameTextBox.getValue()));
            password.setUsername(Utils.safeString(usernameTextBox.getValue()));
            PasswordData passwordDataItem = new PasswordData();
            passwordDataItem.setPassword(Utils.safeString(passwordTextBox.getValue()));
            password.addPasswordData(passwordDataItem);
            String[] tagNames = Utils.safeString(tagsTextBox.getValue()).replaceAll(",", " ").split(" ");
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
            password.setNotes(Utils.safeString(notesTextArea.getValue()));
            password.setMaxHistory(Utils.safeInt(String.valueOf(maxHistoryTextBox.getValue())));
            password.setMaxHistory((password.getMaxHistory() < -1) ? -1 : password.getMaxHistory());
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

    private boolean validateFields()
    {
        if (Utils.safeString(nameTextBox.getValue()).equals(""))
        {
            MessageBox.alert("Error", "Must enter Title", null);
            return false;
        }
        if (Utils.safeString(usernameTextBox.getValue()).equals(""))
        {
            MessageBox.alert("Error", "Must enter Username", null);
            return false;
        }
        if ((password.getId() < 1) && (Utils.safeString(passwordTextBox.getValue()).equals("")))
        {
            MessageBox.alert("Error", "Must enter Password", null);
            return false;
        }
        return true;
    }

    private void doEditPermissions()
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

    private void setFields()
    {
        nameTextBox.setValue(password.getName());
        usernameTextBox.setValue(password.getUsername());
        tagsTextBox.setValue(password.getTagsAsString());
        notesTextArea.setValue(password.getNotes());
        activeCheckBox.setValue(password.isActive());
        maxHistoryTextBox.setValue(password.getMaxHistory());
    }

    private void doCancel()
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
}
