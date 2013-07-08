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
package net.webpasswordsafe.client.ui;

import java.util.List;
import net.webpasswordsafe.client.ClientSessionUtil;
import net.webpasswordsafe.client.WebPasswordSafe;
import net.webpasswordsafe.client.i18n.TextMessages;
import net.webpasswordsafe.client.remote.UserService;
import net.webpasswordsafe.common.model.Group;
import net.webpasswordsafe.common.model.User;
import net.webpasswordsafe.common.util.Constants;
import net.webpasswordsafe.common.util.Utils;
import net.webpasswordsafe.common.util.Constants.Function;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.Dialog;
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
import com.google.gwt.core.client.GWT;
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
    private final static TextMessages textMessages = GWT.create(TextMessages.class);
    private ClientSessionUtil clientSessionUtil = ClientSessionUtil.getInstance();

    public GroupDialog(Group pGroup)
    {
        this.group = pGroup;
        this.setHeading(textMessages.group());
        this.setModal(true);
        this.setLayout(new AbsoluteLayout());
        this.setSize("455", "355");
        this.setResizable(false);

        LabelField lblfldName = new LabelField(textMessages.name_());
        add(lblfldName, new AbsoluteData(6, 13));
        lblfldName.setSize("82px", "19px");

        nameTextBox = new TextField<String>();
        add(nameTextBox, new AbsoluteData(128, 13));
        nameTextBox.setSize("306px", "22px");

        LabelField lblfldUsers = new LabelField(textMessages.users_());
        add(lblfldUsers, new AbsoluteData(6, 49));
        lblfldUsers.setSize("54px", "19px");
        
        LabelField lblfldAvailable = new LabelField(textMessages.available());
        add(lblfldAvailable, new AbsoluteData(6, 74));
        lblfldAvailable.setSize("67px", "19px");

        LabelField lblfldMembers = new LabelField(textMessages.members());
        add(lblfldMembers, new AbsoluteData(232, 74));
        lblfldMembers.setSize("74px", "19px");

        DualListField<UserData> membersListBox = new DualListField<UserData>();
        add(membersListBox, new AbsoluteData(6, 96));
        membersListBox.setSize("428px", "183px");
        ListField<UserData> from = membersListBox.getFromList();
        ListField<UserData> to = membersListBox.getToList();

        from.setSize(300, 100);
        from.setDisplayField(Constants.FULLNAME);
        fromUserStore = new ListStore<UserData>();
        fromUserStore.sort(Constants.FULLNAME, SortDir.ASC);
        from.setStore(fromUserStore);
        to.setDisplayField(Constants.FULLNAME);
        to.setSize(300, 100);
        toUserStore = new ListStore<UserData>();
        toUserStore.sort(Constants.FULLNAME, SortDir.ASC);
        to.setStore(toUserStore);

        Button saveButton = new Button(textMessages.save(),
                new SelectionListener<ButtonEvent>()
                {
                    @Override
                    public void componentSelected(ButtonEvent ce)
                    {
                        doSave();
                    }
                });

        Button deleteButton = new Button(textMessages.delete(),
                new SelectionListener<ButtonEvent>()
                {
                    @Override
                    public void componentSelected(ButtonEvent ce)
                    {
                        doVerifyDelete();
                    }
                });

        Button cancelButton = new Button(textMessages.cancel(),
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
        if ((group.getId() != 0) && clientSessionUtil.isAuthorized(Function.DELETE_GROUP))
        {
            addButton(deleteButton);
        }
        addButton(cancelButton);
        
        setFields();
    }

    private void doVerifyDelete()
    {
        MessageBox.confirm(textMessages.confirmDelete(), textMessages.groupConfirmDelete(), new Listener<MessageBoxEvent>()
        {
            @Override
            public void handleEvent(MessageBoxEvent be)
            {
                if (be.getButtonClicked().getItemId().equals(Dialog.YES))
                {
                    doDelete();
                }
            }
        });
    }
    
    private void doDelete()
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
                Info.display(textMessages.status(), textMessages.groupDeleted());
                hide();
            }
        };
        UserService.Util.getInstance().deleteGroup(group, callback);
    }

    private void doSave()
    {
        if (validateFields())
        {
            group.setName(Utils.safeString(nameTextBox.getValue()));
            group.removeUsers();
            for (UserData userData : toUserStore.getModels())
            {
                group.addUser((User)userData.get(Constants.USER));
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
                        MessageBox.alert(textMessages.error(), textMessages.groupNameAlreadyExists(), null);
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
                                Info.display(textMessages.status(), textMessages.groupSaved());
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
            MessageBox.alert(textMessages.error(), textMessages.mustEnterName(), null);
            return false;
        }
        if (Utils.safeString(nameTextBox.getValue()).length() > Group.LENGTH_NAME)
        {
            MessageBox.alert(textMessages.error(), textMessages.tooLongName(), null);
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
            set(Constants.ID, user.getId());
            set(Constants.FULLNAME, Format.htmlEncode(user.getFullname()));
            set(Constants.USER, user);
        }

        @Override
        public String toString()
        {
            return get(Constants.FULLNAME);
        }
    }
}
