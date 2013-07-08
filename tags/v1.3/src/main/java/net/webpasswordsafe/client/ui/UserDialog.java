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
import net.webpasswordsafe.client.WebPasswordSafe;
import net.webpasswordsafe.client.i18n.TextMessages;
import net.webpasswordsafe.client.remote.UserService;
import net.webpasswordsafe.common.model.Group;
import net.webpasswordsafe.common.model.User;
import net.webpasswordsafe.common.model.UserAuthnPassword;
import net.webpasswordsafe.common.util.Constants;
import net.webpasswordsafe.common.util.Utils;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.DualListField;
import com.extjs.gxt.ui.client.widget.form.ListField;
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
public class UserDialog extends Window
{
    private final static TextMessages textMessages = GWT.create(TextMessages.class);
    private User user;
    private TextField<String> usernameTextBox;
    private TextField<String> fullnameTextBox;
    private TextField<String> emailTextBox;
    private TextField<String> password1TextBox;
    private TextField<String> password2TextBox;
    private CheckBox enabledCheckBox;
    private ListStore<GroupData> fromGroupStore;
    private ListStore<GroupData> toGroupStore;
    
    public UserDialog(User pUser)
    {
        this.user = pUser;
        this.setHeading(textMessages.user());
        this.setModal(true);
        this.setLayout(new AbsoluteLayout());
        this.setSize("455", "475");
        this.setResizable(false);
        
        LabelField lblfldUsername = new LabelField(textMessages.username_());
        add(lblfldUsername, new AbsoluteData(6, 6));
        usernameTextBox = new TextField<String>();
        usernameTextBox.setReadOnly(user.getId() > 0);
        add(usernameTextBox, new AbsoluteData(144, 6));
        usernameTextBox.setSize("271px", "22px");
        LabelField lblfldFullName = new LabelField(textMessages.fullname_());
        add(lblfldFullName, new AbsoluteData(6, 34));
        fullnameTextBox = new TextField<String>();
        add(fullnameTextBox, new AbsoluteData(144, 34));
        fullnameTextBox.setSize("271px", "22px");
        LabelField lblfldEmail = new LabelField(textMessages.email_());
        add(lblfldEmail, new AbsoluteData(6, 62));
        emailTextBox = new TextField<String>();
        add(emailTextBox, new AbsoluteData(144, 62));
        emailTextBox.setSize("271px", "22px");
        LabelField lblfldPassword = new LabelField(textMessages.password_());
        add(lblfldPassword, new AbsoluteData(6, 90));
        password1TextBox = new TextField<String>();
        password1TextBox.setPassword(true);
        add(password1TextBox, new AbsoluteData(144, 90));
        password1TextBox.setSize("271px", "22px");
        password2TextBox = new TextField<String>();
        password2TextBox.setPassword(true);
        add(password2TextBox, new AbsoluteData(144, 118));
        password2TextBox.setSize("271px", "22px");
        enabledCheckBox = new CheckBox();
        enabledCheckBox.setBoxLabel(textMessages.enabled());
        add(enabledCheckBox, new AbsoluteData(144, 146));
        enabledCheckBox.setSize("76px", "22px");

        LabelField lblfldGroups = new LabelField(textMessages.groups_());
        add(lblfldGroups, new AbsoluteData(6, 170));
        LabelField lblfldAvailable = new LabelField(textMessages.available());
        add(lblfldAvailable, new AbsoluteData(6, 195));
        LabelField lblfldMembers = new LabelField(textMessages.memberOf());
        add(lblfldMembers, new AbsoluteData(233, 195));

        DualListField<GroupData> membersListBox = new DualListField<GroupData>();
        add(membersListBox, new AbsoluteData(6, 216));
        membersListBox.setSize("424px", "183px");
        ListField<GroupData> from = membersListBox.getFromList();
        ListField<GroupData> to = membersListBox.getToList();
        from.setSize(300, 100);
        from.setDisplayField(Constants.NAME);
        fromGroupStore = new ListStore<GroupData>();
        fromGroupStore.sort(Constants.NAME, SortDir.ASC);
        from.setStore(fromGroupStore);
        to.setDisplayField(Constants.NAME);
        to.setSize(300, 100);
        toGroupStore = new ListStore<GroupData>();
        toGroupStore.sort(Constants.NAME, SortDir.ASC);
        to.setStore(toGroupStore);

        Button saveButton = new Button(textMessages.save(), new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                doSave();
            }
        });

        Button cancelButton = new Button(textMessages.cancel(), new SelectionListener<ButtonEvent>() {
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
    
    private void setFields()
    {
        usernameTextBox.setValue(user.getUsername());
        fullnameTextBox.setValue(user.getFullname());
        emailTextBox.setValue(user.getEmail());
        enabledCheckBox.setValue(user.isActiveFlag());
        for (Group group : user.getGroups())
        {
            if (!group.getName().equals(Constants.EVERYONE_GROUP_NAME))
            {
                toGroupStore.add(new GroupData(group));
            }
        }
        loadAvailableGroups();
    }
    
    private boolean validateFields()
    {
        if (!(Utils.safeString(password2TextBox.getValue())).equals(Utils.safeString(password1TextBox.getValue())))
        {
            MessageBox.alert(textMessages.error(), textMessages.passwordsNotMatch(), null);
            return false;
        }
        if (Utils.safeString(password1TextBox.getValue()).length() > UserAuthnPassword.LENGTH_PASSWORD)
        {
            MessageBox.alert(textMessages.error(), textMessages.tooLongPassword(), null);
            return false;
        }
        if (Utils.safeString(usernameTextBox.getValue()).equals(""))
        {
            MessageBox.alert(textMessages.error(), textMessages.mustEnterUsername(), null);
            return false;
        }
        if (Utils.safeString(usernameTextBox.getValue()).length() > User.LENGTH_USERNAME)
        {
            MessageBox.alert(textMessages.error(), textMessages.tooLongUsername(), null);
            return false;
        }
        if (Utils.safeString(fullnameTextBox.getValue()).equals(""))
        {
            MessageBox.alert(textMessages.error(), textMessages.mustEnterFullName(), null);
            return false;
        }
        if (Utils.safeString(fullnameTextBox.getValue()).length() > User.LENGTH_FULLNAME)
        {
            MessageBox.alert(textMessages.error(), textMessages.tooLongFullName(), null);
            return false;
        }
        if (Utils.safeString(emailTextBox.getValue()).equals(""))
        {
            MessageBox.alert(textMessages.error(), textMessages.mustEnterEmail(), null);
            return false;
        }
        if (Utils.safeString(emailTextBox.getValue()).length() > User.LENGTH_EMAIL)
        {
            MessageBox.alert(textMessages.error(), textMessages.tooLongEmail(), null);
            return false;
        }
        if (!Utils.isValidEmail(Utils.safeString(emailTextBox.getValue())))
        {
            MessageBox.alert(textMessages.error(), textMessages.invalidEmail(), null);
            return false;
        }
        return true;
    }
    
    private void doSave()
    {
        if (validateFields())
        {
            user.setUsername(Utils.safeString(usernameTextBox.getValue()));
            user.setFullname(Utils.safeString(fullnameTextBox.getValue()));
            user.setEmail(Utils.safeString(emailTextBox.getValue()));
            user.setActiveFlag(enabledCheckBox.getValue());
            user.updateAuthnPasswordValue(Utils.safeString(password1TextBox.getValue()));
            user.removeGroups();
            for (GroupData groupData : toGroupStore.getModels())
            {
                Group group = (Group)groupData.get(Constants.GROUP);
                user.addGroup(group);
            }
            
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
                    Info.display(textMessages.status(), textMessages.userSaved());
                    hide();
                }
            };
            if (user.getId() == 0)
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
                        // true => username already taken, else go ahead and save
                        if (result)
                        {
                            MessageBox.alert(textMessages.error(), textMessages.usernameAlreadyExists(), null);
                        }
                        else
                        {
                            UserService.Util.getInstance().addUser(user, callback);
                        }
                    }
                };
                UserService.Util.getInstance().isUserTaken(user.getUsername(), callbackCheck);
            }
            else
            {
                UserService.Util.getInstance().updateUser(user, callback);
            }
        }
    }
    
    private void doCancel()
    {
        hide();
    }

    private void loadAvailableGroups()
    {
        AsyncCallback<List<Group>> callback = new AsyncCallback<List<Group>>()
        {
            @Override
            public void onFailure(Throwable caught)
            {
                WebPasswordSafe.handleServerFailure(caught);
            }
            @Override
            public void onSuccess(List<Group> result)
            {
                refreshAvailableGroups(result);
            }
        };
        UserService.Util.getInstance().getGroups(false, callback);
    }
    
    private void refreshAvailableGroups(List<Group> groups)
    {
        for (Group group : groups)
        {
            if (!user.getGroups().contains(group))
            {
                fromGroupStore.add(new GroupData(group));
            }
        }
    }

    private class GroupData extends BaseModel
    {
        private static final long serialVersionUID = 1L;

        public GroupData(Group group)
        {
            set(Constants.ID, group.getId());
            set(Constants.NAME, Format.htmlEncode(group.getName()));
            set(Constants.GROUP, group);
        }

        @Override
        public String toString()
        {
            return get(Constants.NAME);
        }
    }
}
