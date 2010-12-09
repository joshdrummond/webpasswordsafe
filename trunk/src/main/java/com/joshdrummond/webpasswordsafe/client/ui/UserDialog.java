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
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.DualListField;
import com.extjs.gxt.ui.client.widget.form.ListField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.joshdrummond.webpasswordsafe.client.remote.UserService;
import com.joshdrummond.webpasswordsafe.common.model.Group;
import com.joshdrummond.webpasswordsafe.common.model.User;
import com.joshdrummond.webpasswordsafe.common.util.Constants;
import com.joshdrummond.webpasswordsafe.common.util.Utils;
import com.extjs.gxt.ui.client.widget.layout.AbsoluteLayout;
import com.extjs.gxt.ui.client.widget.layout.AbsoluteData;
import com.extjs.gxt.ui.client.widget.form.LabelField;


/**
 * @author Josh Drummond
 *
 */
public class UserDialog extends Window
{
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
        this.setHeading("User");
        this.setModal(true);
        this.setLayout(new AbsoluteLayout());
        this.setSize("455", "475");
        this.setResizable(false);
        
        LabelField lblfldUsername = new LabelField("Username:");
        add(lblfldUsername, new AbsoluteData(6, 6));
        usernameTextBox = new TextField<String>();
        usernameTextBox.setEnabled(user.getId() < 1);
        add(usernameTextBox, new AbsoluteData(144, 6));
        usernameTextBox.setSize("271px", "22px");
        LabelField lblfldFullName = new LabelField("Full Name:");
        add(lblfldFullName, new AbsoluteData(6, 34));
        fullnameTextBox = new TextField<String>();
        add(fullnameTextBox, new AbsoluteData(144, 34));
        fullnameTextBox.setSize("271px", "22px");
        LabelField lblfldEmail = new LabelField("Email:");
        add(lblfldEmail, new AbsoluteData(6, 62));
        emailTextBox = new TextField<String>();
        add(emailTextBox, new AbsoluteData(144, 62));
        emailTextBox.setSize("271px", "22px");
        LabelField lblfldPassword = new LabelField("Password:");
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
        enabledCheckBox.setBoxLabel("Enabled");
        add(enabledCheckBox, new AbsoluteData(144, 146));
        enabledCheckBox.setSize("76px", "22px");

        LabelField lblfldGroups = new LabelField("Groups:");
        add(lblfldGroups, new AbsoluteData(6, 170));
        LabelField lblfldAvailable = new LabelField("Available");
        add(lblfldAvailable, new AbsoluteData(6, 195));
        LabelField lblfldMembers = new LabelField("Member Of");
        add(lblfldMembers, new AbsoluteData(233, 195));

        DualListField<GroupData> membersListBox = new DualListField<GroupData>();
        add(membersListBox, new AbsoluteData(6, 216));
        membersListBox.setSize("424px", "183px");
        ListField<GroupData> from = membersListBox.getFromList();
        ListField<GroupData> to = membersListBox.getToList();
        from.setSize(300, 100);
        from.setDisplayField("name");
        fromGroupStore = new ListStore<GroupData>();
        fromGroupStore.setStoreSorter(new StoreSorter<GroupData>());
        from.setStore(fromGroupStore);
        to.setDisplayField("name");
        to.setSize(300, 100);
        toGroupStore = new ListStore<GroupData>();
        toGroupStore.setStoreSorter(new StoreSorter<GroupData>());
        to.setStore(toGroupStore);

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
        for (Group group: user.getGroups())
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
            MessageBox.alert("Error", "Passwords don't match", null);
            return false;
        }
        if (Utils.safeString(usernameTextBox.getValue()).equals(""))
        {
            MessageBox.alert("Error", "Must enter Username", null);
            return false;
        }
        if (Utils.safeString(usernameTextBox.getValue()).length() > User.LENGTH_USERNAME)
        {
            MessageBox.alert("Error", "Username too long", null);
            return false;
        }
        if (Utils.safeString(fullnameTextBox.getValue()).equals(""))
        {
            MessageBox.alert("Error", "Must enter Full Name", null);
            return false;
        }
        if (Utils.safeString(fullnameTextBox.getValue()).length() > User.LENGTH_FULLNAME)
        {
            MessageBox.alert("Error", "Full Name too long", null);
            return false;
        }
        if (Utils.safeString(emailTextBox.getValue()).equals(""))
        {
            MessageBox.alert("Error", "Must enter Email", null);
            return false;
        }
        if (Utils.safeString(emailTextBox.getValue()).length() > User.LENGTH_EMAIL)
        {
            MessageBox.alert("Error", "Email too long", null);
            return false;
        }
        if (!Utils.isValidEmail(Utils.safeString(emailTextBox.getValue())))
        {
            MessageBox.alert("Error", "Email invalid", null);
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
            user.setPassword(Utils.safeString(password1TextBox.getValue()));
            user.removeGroups();
            for (GroupData groupData : toGroupStore.getModels())
            {
                Group group = (Group)groupData.get("group");
                user.addGroup(group);
            }
            
            final AsyncCallback<Void> callback = new AsyncCallback<Void>()
            {
                public void onFailure(Throwable caught)
                {
                    MessageBox.alert("Error", caught.getMessage(), null);
                }

                public void onSuccess(Void result)
                {
                    Info.display("Status", "User saved");
                    hide();
                }
            };
            if (user.getId() == 0)
            {
                final AsyncCallback<Boolean> callbackCheck = new AsyncCallback<Boolean>()
                {
                    public void onFailure(Throwable caught)
                    {
                        MessageBox.alert("Error", caught.getMessage(), null);
                    }

                    public void onSuccess(Boolean result)
                    {
                        // true => username already taken, else go ahead and save
                        if (result)
                        {
                            MessageBox.alert("Error", "Username already exists", null);
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
            public void onFailure(Throwable caught)
            {
                MessageBox.alert("Error", caught.getMessage(), null);
            }
            public void onSuccess(List<Group> result)
            {
                refreshAvailableGroups(result);
            }
        };
        UserService.Util.getInstance().getGroups(false, callback);
    }
    
    private void refreshAvailableGroups(List<Group> groups)
    {
        for (Group group: groups)
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
            set("id", group.getId());
            set("name", Format.htmlEncode(group.getName()));
            set("group", group);
        }

        public String toString()
        {
            return get("name");
        }
    }
}
