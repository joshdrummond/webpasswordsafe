/*
    Copyright 2008-2011 Josh Drummond

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
package net.webpasswordsafe.client.ui;

import java.util.List;
import java.util.Set;
import net.webpasswordsafe.client.WebPasswordSafe;
import net.webpasswordsafe.client.i18n.TextConstants;
import net.webpasswordsafe.client.i18n.TextMessages;
import net.webpasswordsafe.client.remote.PasswordService;
import net.webpasswordsafe.client.remote.UserService;
import net.webpasswordsafe.common.model.AccessLevel;
import net.webpasswordsafe.common.model.Password;
import net.webpasswordsafe.common.model.PasswordData;
import net.webpasswordsafe.common.model.Permission;
import net.webpasswordsafe.common.model.Subject;
import net.webpasswordsafe.common.model.Tag;
import net.webpasswordsafe.common.util.Utils;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.extjs.gxt.ui.client.widget.layout.AbsoluteLayout;
import com.extjs.gxt.ui.client.widget.layout.AbsoluteData;
import com.extjs.gxt.ui.client.widget.form.LabelField;


/**
 * @author Josh Drummond
 *
 */
public class PasswordDialog extends Window implements PermissionListener
{
    private final static TextConstants textConstants = GWT.create(TextConstants.class);
    private final static TextMessages textMessages = GWT.create(TextMessages.class);
    private Password password;
    private TextField<String> nameTextBox;
    private TextField<String> usernameTextBox;
    private TextField<String> passwordTextBox;
    private TextField<String> tagsTextBox;
    private NumberField maxHistoryTextBox;
    private TextArea notesTextArea;
    private CheckBox activeCheckBox;
    private TagLoadListener tagLoadListener;

    public PasswordDialog(Password password, TagLoadListener tagLoadListener)
    {
        this.password = password;
        this.tagLoadListener = tagLoadListener;
        boolean isPasswordReadOnly = password.getMaxEffectiveAccessLevel().equals(AccessLevel.READ);
        boolean isPasswordGrantable = password.getMaxEffectiveAccessLevel().equals(AccessLevel.GRANT);

        this.setHeading(textConstants.password());
        this.setModal(true);
        this.setLayout(new AbsoluteLayout());
        this.setSize(430, 415);
        this.setResizable(false);
        
        LabelField lblfldName = new LabelField(textConstants.title_());
        add(lblfldName, new AbsoluteData(7, 6));

        nameTextBox = new TextField<String>();
        nameTextBox.setReadOnly(password.getId() > 0);
        add(nameTextBox, new AbsoluteData(82, 6));
        nameTextBox.setSize("331px", "22px");

        LabelField lblfldUsername = new LabelField(textConstants.username_());
        add(lblfldUsername, new AbsoluteData(7, 34));
        
        usernameTextBox = new TextField<String>();
        usernameTextBox.setReadOnly(password.getId() > 0);
        add(usernameTextBox, new AbsoluteData(82, 34));
        usernameTextBox.setSize("331px", "22px");

        LabelField lblfldPassword = new LabelField(textConstants.password_());
        add(lblfldPassword, new AbsoluteData(7, 62));
        
        passwordTextBox = new TextField<String>();
        passwordTextBox.setReadOnly(isPasswordReadOnly);
        add(passwordTextBox, new AbsoluteData(82, 62));
        passwordTextBox.setSize("331px", "22px");

        Button generateButton = new Button(textConstants.generatePassword(), new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                doGeneratePassword();
            }
        });
        generateButton.setEnabled(!isPasswordReadOnly);
        add(generateButton, new AbsoluteData(82, 90));
        generateButton.setSize("127px", "22px");
        
        Button currentButton = new Button(textConstants.currentPassword(), new SelectionListener<ButtonEvent>()
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

        Button historyButton = new Button(textConstants.viewPasswordHistory(), new SelectionListener<ButtonEvent>()
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

        LabelField lblfldTags = new LabelField(textConstants.tags_());
        add(lblfldTags, new AbsoluteData(6, 118));
            
        tagsTextBox = new TextField<String>();
        tagsTextBox.setReadOnly(isPasswordReadOnly);
        add(tagsTextBox, new AbsoluteData(82, 118));
        tagsTextBox.setSize("331px", "22px");

        LabelField lblfldNotes = new LabelField(textConstants.notes_());
        add(lblfldNotes, new AbsoluteData(6, 146));
        
        notesTextArea = new TextArea();
        notesTextArea.setReadOnly(isPasswordReadOnly);
        add(notesTextArea, new AbsoluteData(82, 146));
        notesTextArea.setSize("331px", "75px");
        
        LabelField lblfldMaxHistory = new LabelField(textConstants.maxHistory_());
        add(lblfldMaxHistory, new AbsoluteData(6, 227));
        
        maxHistoryTextBox = new NumberField();
        maxHistoryTextBox.setReadOnly(isPasswordReadOnly);
        maxHistoryTextBox.setPropertyEditorType(Integer.class);
        add(maxHistoryTextBox, new AbsoluteData(82, 227));
        maxHistoryTextBox.setSize("76px", "22px");

        LabelField lblfldInfinite = new LabelField(textConstants.infinite());
        add(lblfldInfinite, new AbsoluteData(164, 227));
        
        activeCheckBox = new CheckBox();
        activeCheckBox.setReadOnly(isPasswordReadOnly);
        activeCheckBox.setBoxLabel(textConstants.active());
        add(activeCheckBox, new AbsoluteData(82, 255));
        
        Button editPermissionsButton = new Button(isPasswordGrantable ? 
                textConstants.editPermissions() : textConstants.viewPermissions(),
                new SelectionListener<ButtonEvent>()
        {
            @Override
            public void componentSelected(ButtonEvent ce) {
                doEditPermissions();
            }
        });
        add(editPermissionsButton, new AbsoluteData(82, 283));
        editPermissionsButton.setSize("260px", "22px");
        
        Button accessAuditButton = new Button(textConstants.viewAccessAuditLog(), 
                new SelectionListener<ButtonEvent>()
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

        Button saveButton = new Button(textConstants.save(), new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                doSave();
            }
        });
        saveButton.setEnabled(!isPasswordReadOnly);

        Button cancelButton = new Button(textConstants.cancel(), new SelectionListener<ButtonEvent>() {
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
            @Override
            public void onFailure(Throwable caught)
            {
                WebPasswordSafe.handleServerFailure(caught);
            }
            @Override
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
            @Override
            public void onFailure(Throwable caught)
            {
                WebPasswordSafe.handleServerFailure(caught);
            }
            @Override
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
            password.removePasswordData();
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

            final AsyncCallback<Void> callback = new AsyncCallback<Void>()
            {
                @Override
                public void onFailure(Throwable caught)
                {
                    WebPasswordSafe.handleServerFailure(caught);
                }
                @Override
                public void onSuccess(Void result)
                {
                    Info.display(textConstants.status(), textMessages.passwordSaved());
                    tagLoadListener.reloadTags();
                    hide();
                }
            };
            if (password.getId() == 0)
            {
                final AsyncCallback<Boolean> callbackCheck = new AsyncCallback<Boolean>()
                {
                    @Override
                    public void onFailure(Throwable caught)
                    {
                        WebPasswordSafe.handleServerFailure(caught);
                    }
                    @Override
                    public void onSuccess(Boolean result)
                    {
                        // true => password title already taken, else go ahead and save
                        if (result)
                        {
                            MessageBox.alert(textConstants.error(), textMessages.passwordTitleExists(), null);
                        }
                        else
                        {
                            PasswordService.Util.getInstance().addPassword(password, callback);
                        }
                    }
                };
                PasswordService.Util.getInstance().isPasswordTaken(password.getName(), callbackCheck);
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
            MessageBox.alert(textConstants.error(), textMessages.mustEnterTitle(), null);
            return false;
        }
        if (Utils.safeString(nameTextBox.getValue()).length() > Password.LENGTH_NAME)
        {
            MessageBox.alert(textConstants.error(), textMessages.tooLongTitle(), null);
            return false;
        }
        if (Utils.safeString(usernameTextBox.getValue()).equals(""))
        {
            MessageBox.alert(textConstants.error(), textMessages.mustEnterUsername(), null);
            return false;
        }
        if (Utils.safeString(usernameTextBox.getValue()).length() > Password.LENGTH_USERNAME)
        {
            MessageBox.alert(textConstants.error(), textMessages.tooLongUsername(), null);
            return false;
        }
        if ((password.getId() < 1) && (Utils.safeString(passwordTextBox.getValue()).equals("")))
        {
            MessageBox.alert(textConstants.error(), textMessages.mustEnterPassword(), null);
            return false;
        }
        if (Utils.safeString(passwordTextBox.getValue()).length() > PasswordData.LENGTH_PASSWORD)
        {
            MessageBox.alert(textConstants.error(), textMessages.tooLongPassword(), null);
            return false;
        }
        String[] tagNames = Utils.safeString(tagsTextBox.getValue()).replaceAll(",", " ").split(" ");
        for (String tagName : tagNames)
        {
            tagName = tagName.trim();
            if (!"".equals(tagName))
            {
                if (tagName.length() > Tag.LENGTH_NAME)
                {
                    MessageBox.alert(textConstants.error(), textMessages.tooLongTag(), null);
                    return false;
                }
            }
        }

        return true;
    }

    private void doEditPermissions()
    {
        AsyncCallback<List<Subject>> callback = new AsyncCallback<List<Subject>>()
        {
            @Override
            public void onFailure(Throwable caught)
            {
                WebPasswordSafe.handleServerFailure(caught);
            }
            @Override
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
     * @see net.webpasswordsafe.client.ui.PermissionListener#doPermissionsChanged(java.util.List)
     */
    @Override
    public void doPermissionsChanged(Set<Permission> permissions)
    {
        password.clearPermissions();
        for (Permission permission : permissions)
        {
            password.addPermission(permission);
        }
    }
}
