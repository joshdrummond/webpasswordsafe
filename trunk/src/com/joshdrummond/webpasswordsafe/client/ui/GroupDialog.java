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
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.DualListField;
import com.extjs.gxt.ui.client.widget.form.ListField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.joshdrummond.webpasswordsafe.client.remote.UserService;
import com.joshdrummond.webpasswordsafe.common.model.Group;
import com.joshdrummond.webpasswordsafe.common.model.User;
import com.joshdrummond.webpasswordsafe.common.util.Utils;
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
        setSize("455", "330");
        this.group = pGroup;
        this.setHeading("Group");
        this.setModal(true);
        setLayout(new AbsoluteLayout());

        LabelField lblfldName = new LabelField("Name:");
        lblfldName.setFieldLabel("Name:");
        add(lblfldName, new AbsoluteData(6, 13));
        lblfldName.setSize("82px", "19px");

        nameTextBox = new TextField<String>();
        add(nameTextBox, new AbsoluteData(128, 13));
        nameTextBox.setSize("306px", "22px");

        LabelField lblfldMembers = new LabelField("Members:");
        add(lblfldMembers, new AbsoluteData(6, 55));

        DualListField<UserData> membersListBox = new DualListField<UserData>();
        add(membersListBox, new AbsoluteData(6, 80));
        membersListBox.setSize("428px", "183px");
        membersListBox.setFieldLabel("Members");
        ListField<UserData> from = membersListBox.getFromList();
        ListField<UserData> to = membersListBox.getToList();

        from.setSize(300, 100);
        from.setDisplayField("fullname");
        fromUserStore = new ListStore<UserData>();
        fromUserStore.setStoreSorter(new StoreSorter<UserData>());
        from.setStore(fromUserStore);
        to.setDisplayField("fullname");
        to.setSize(300, 100);
        toUserStore = new ListStore<UserData>();
        toUserStore.setStoreSorter(new StoreSorter<UserData>());
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
        add(saveButton, new AbsoluteData(137, 269));
        saveButton.setSize("74px", "22px");

        Button cancelButton = new Button("Cancel",
                new SelectionListener<ButtonEvent>()
                {
                    @Override
                    public void componentSelected(ButtonEvent ce)
                    {
                        doCancel();
                    }
                });
        add(cancelButton, new AbsoluteData(232, 269));
        cancelButton.setSize("82px", "22px");

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

    private boolean validateFields()
    {
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
            public void onFailure(Throwable caught)
            {
                MessageBox.alert("Error", caught.getMessage(), null);
            }
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
            set("fullname", user.getFullname());
            set("user", user);
        }

        public String toString()
        {
            return get("fullname");
        }
    }
}
