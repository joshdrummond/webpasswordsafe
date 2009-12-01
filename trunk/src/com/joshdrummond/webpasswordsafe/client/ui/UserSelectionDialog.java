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

import java.util.ArrayList;
import java.util.List;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.ListField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.joshdrummond.webpasswordsafe.common.model.User;


/**
 * @author Josh Drummond
 *
 */
public class UserSelectionDialog extends Window
{
    private ListField<UserData> userListBox;
    private ListStore<UserData> store;
    private UserListener userListener;
    private List<User> users;
    
    public UserSelectionDialog(UserListener userListener, List<User> users, boolean allowMultiple)
    {
        this.setHeading("Users");
        this.setModal(true);
        this.userListener = userListener;
        this.users = users;

        FormPanel form = new FormPanel();
        form.setHeaderVisible(false);
        form.setFrame(true);
        form.setLabelAlign(LabelAlign.TOP);
        form.setButtonAlign(HorizontalAlignment.CENTER);
        
        String selectLabelText = allowMultiple ? "Please select user(s)" : "Please select a user";
        store = new ListStore<UserData>();
        userListBox = new ListField<UserData>();
        userListBox.setSize(300, 150);
        userListBox.setDisplayField("fullname");
        userListBox.setFieldLabel(selectLabelText);
        form.add(userListBox);

        Button okayButton = new Button("Okay", new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
                doOkay();
			}
		});

        Button cancelButton = new Button("Cancel", new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
                doCancel();
			}
		});
        
        form.setButtonAlign(HorizontalAlignment.CENTER);
        form.addButton(okayButton);
        form.addButton(cancelButton);

        setFields();
        
        this.add(form);
    }

    private void setFields()
    {
    	store.removeAll();
        for (User user : users)
        {
            store.add(new UserData(user));
        }
        userListBox.setStore(store);
    }

    private void doCancel()
    {
        hide();
    }

    private void doOkay()
    {
    	List<UserData> dataSelected = userListBox.getSelection();
        List<User> usersSelected = new ArrayList<User>(dataSelected.size());
    	for (UserData ud : dataSelected)
    	{
    		usersSelected.add((User)ud.get("user"));
    	}
        userListener.doUsersChosen(usersSelected);
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
    }
}
