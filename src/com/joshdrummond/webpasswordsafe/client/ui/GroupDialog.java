/*
    Copyright 2008 Josh Drummond

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
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.DualListField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.ListField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.joshdrummond.webpasswordsafe.client.model.common.GroupDTO;
import com.joshdrummond.webpasswordsafe.client.model.common.UserDTO;

/**
 * @author Josh Drummond
 *
 */
public class GroupDialog extends Window
{
    private GroupDTO group;
    private TextField<String> nameTextBox;
    private DualListField<UserData> membersListBox;
    private FormData formData = new FormData("-20"); 
    
    public GroupDialog(GroupDTO pGroup)
    {
        this.group = pGroup;
        this.setHeading("Group");
        this.setModal(true);

        FormPanel form = new FormPanel();
        form.setHeaderVisible(false);
        form.setFrame(true);

        nameTextBox = new TextField<String>();
        nameTextBox.setFieldLabel("Name");
        form.add(nameTextBox, formData);

        membersListBox = new DualListField<UserData>();
        membersListBox.setFieldLabel("Members");
        ListField<UserData> from = membersListBox.getFromList();
        from.setSize(300, 100);
        from.setDisplayField("fullname");
        ListStore<UserData> store = new ListStore<UserData>();
        store.setStoreSorter(new StoreSorter<UserData>());
        from.setStore(store);
        ListField<UserData> to = membersListBox.getToList();
        to.setDisplayField("fullname");
        to.setSize(300, 100);
        store = new ListStore<UserData>();
        store.setStoreSorter(new StoreSorter<UserData>());
        to.setStore(store);
        
        form.add(membersListBox, formData);
        
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
        
//        this.setSize(600, 200);
        this.add(form);
    }
    
    /**
     * 
     */
    protected void doSave()
    {
        if (validateFields())
        {
            
        }
    }

    /**
     * @return
     */
    private boolean validateFields()
    {
        return true;
    }


    private void setFields()
    {
        nameTextBox.setValue(group.getName());
        
        for (UserDTO user : group.getUsers())
        {
        	membersListBox.getToList().getStore().add(new UserData(user));
        }
        
        List<UserDTO> allUsers = new ArrayList<UserDTO>();
        allUsers.add(new UserDTO(5, "hillary", "Hillary Clinton", "h@clinton.net", true));
        allUsers.add(new UserDTO(6, "edwards", "John Edwards", "j@edwards.net", true));
        for (UserDTO user : allUsers)
        {
        	if (!group.getUsers().contains(user))
        	{
        		membersListBox.getFromList().getStore().add(new UserData(user));
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
    	public UserData(UserDTO user)
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
