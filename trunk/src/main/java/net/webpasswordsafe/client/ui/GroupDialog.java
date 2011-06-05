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
import net.webpasswordsafe.client.remote.UserService;
import net.webpasswordsafe.common.model.Group;
import net.webpasswordsafe.common.model.User;
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
import com.extjs.gxt.ui.client.widget.form.DualListField;
import com.extjs.gxt.ui.client.widget.form.ListField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.AbsoluteLayout;
import com.extjs.gxt.ui.client.widget.layout.AbsoluteData;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * @author Josh Drummond
 *
 */
public class GroupDialog extends Window
{
    private Group group;
    private TextField<String> nameTextBox;
    private ListStore<UserData> fromUserStore;
    private ListStore<UserData> toUserStore;

    public GroupDialog(Group pGroup)
    {
        this.group = pGroup;
        this.setHeading("Group");
        this.setModal(true);
        this.setLayout(new AbsoluteLayout());
        this.setSize("455", "355");
        this.setResizable(false);

        LabelField lblfldName = new LabelField("Name:");
        add(lblfldName, new AbsoluteData(6, 13));
        lblfldName.setSize("82px", "19px");

        nameTextBox = new TextField<String>();
        add(nameTextBox, new AbsoluteData(128, 13));
        nameTextBox.setSize("306px", "22px");

        LabelField lblfldUsers = new LabelField("Users:");
        add(lblfldUsers, new AbsoluteData(6, 49));
        lblfldUsers.setSize("54px", "19px");
        
        LabelField lblfldAvailable = new LabelField("Available");
        add(lblfldAvailable, new AbsoluteData(6, 74));
        lblfldAvailable.setSize("67px", "19px");

        LabelField lblfldMembers = new LabelField("Members");
        add(lblfldMembers, new AbsoluteData(232, 74));
        lblfldMembers.setSize("74px", "19px");

        DualListField<UserData> membersListBox = new DualListField<UserData>();
        add(membersListBox, new AbsoluteData(6, 96));
        membersListBox.setSize("428px", "183px");
        ListField<UserData> from = membersListBox.getFromList();
        ListField<UserData> to = membersListBox.getToList();

        from.setSize(300, 100);
        from.setDisplayField("fullname");
        fromUserStore = new ListStore<UserData>();
        fromUserStore.sort("fullname", SortDir.ASC);
        from.setStore(fromUserStore);
        to.setDisplayField("fullname");
        to.setSize(300, 100);
        toUserStore = new ListStore<UserData>();
        toUserStore.sort("fullname", SortDir.ASC);
        to.setStore(toUserStore);

        Button saveButton = new Button("Save",
                new SelectionListener<ButtonEvent>()
                {
                    @Override
                    public void componentSelected(ButtonEvent ce)
                    {
                        doSave();
                    }
                });

        Button cancelButton = new Button("Cancel",
                new SelectionListener<ButtonEvent>()
                {
                    @Override
                    public void componentSelected(ButtonEvent ce)
                    {
                        doCancel();
                    }
                });

        setButtonAlign(HorizontalAlignment.CENTER);
        addButton(saveButton);
        addButton(cancelButton);
        
        setFields();
    }

    private void doSave()
    {
        if (validateFields())
        {
            group.setName(Utils.safeString(nameTextBox.getValue()));
            group.removeUsers();
            for (UserData userData : toUserStore.getModels())
            {
                group.addUser((User)userData.get("user"));
            }
            
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
                    // true => group name already taken, else go ahead and save
                    if (result)
                    {
                        MessageBox.alert("Error", "Group name already exists", null);
                    }
                    else
                    {
                        AsyncCallback<Void> callback = new AsyncCallback<Void>()
                        {
                            @Override
                            public void onFailure(Throwable caught)
                            {
                                WebPasswordSafe.handleServerFailure(caught);
                            }
                            @Override
                            public void onSuccess(Void result)
                            {
                                Info.display("Status", "Group saved");
                                hide();
                            }
                        };
                        if (group.getId() == 0)
                        {
                            UserService.Util.getInstance().addGroup(group, callback);
                        }
                        else
                        {
                            UserService.Util.getInstance().updateGroup(group, callback);
                        }
                    }
                }
            };
            UserService.Util.getInstance().isGroupTaken(group.getName(), group.getId(), callbackCheck);
        }
    }

    private boolean validateFields()
    {
        if (Utils.safeString(nameTextBox.getValue()).equals(""))
        {
            MessageBox.alert("Error", "Must enter Name", null);
            return false;
        }
        if (Utils.safeString(nameTextBox.getValue()).length() > Group.LENGTH_NAME)
        {
            MessageBox.alert("Error", "Name too long", null);
            return false;
        }
        return true;
    }

    private void setFields()
    {
        nameTextBox.setValue(group.getName());
        for (User user : group.getUsers())
        {
            toUserStore.add(new UserData(user));
        }

        loadAvailableUsers();
    }

    private void loadAvailableUsers()
    {
        AsyncCallback<List<User>> callback = new AsyncCallback<List<User>>()
        {
            @Override
            public void onFailure(Throwable caught)
            {
                WebPasswordSafe.handleServerFailure(caught);
            }
            @Override
            public void onSuccess(List<User> result)
            {
                refreshAvailableUsers(result);
            }
        };
        UserService.Util.getInstance().getUsers(false, callback);
    }
    
    private void refreshAvailableUsers(List<User> users)
    {
        for (User user : users)
        {
            if (!group.getUsers().contains(user))
            {
                fromUserStore.add(new UserData(user));
            }
        }
    }
    
    private void doCancel()
    {
        hide();
    }

    private class UserData extends BaseModel
    {
        private static final long serialVersionUID = 1L;

        public UserData(User user)
        {
            set("id", user.getId());
            set("fullname", Format.htmlEncode(user.getFullname()));
            set("user", user);
        }

        @Override
        public String toString()
        {
            return get("fullname");
        }
    }
}
