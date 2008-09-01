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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.joshdrummond.webpasswordsafe.client.model.common.UserDTO;

/**
 * @author Josh Drummond
 *
 */
public class UserSelectionDialog extends DialogBox
{

    private ListBox userListBox;
    private UserListener userListener;
    private List<UserDTO> users;
    
    public UserSelectionDialog(UserListener userListener, List<UserDTO> users, boolean allowMultiple)
    {
        setHTML("Users");
        this.userListener = userListener;
        this.users = users;

        final FlexTable flexTable = new FlexTable();
        setWidget(flexTable);
        flexTable.setSize("100%", "100%");

        String selectLabelText = allowMultiple ? "Please select user(s):" : "Please select a user:";
        final Label usersLabel = new Label(selectLabelText);
        flexTable.setWidget(0, 0, usersLabel);

        userListBox = new ListBox();
        flexTable.setWidget(1, 0, userListBox);
        userListBox.setMultipleSelect(allowMultiple);
        flexTable.getCellFormatter().setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_CENTER);
        userListBox.setVisibleItemCount(5);

        final FlowPanel flowPanel = new FlowPanel();
        flexTable.setWidget(2, 0, flowPanel);
        flexTable.getCellFormatter().setHorizontalAlignment(2, 0, HasHorizontalAlignment.ALIGN_CENTER);

        final Button okayButton = new Button();
        flowPanel.add(okayButton);
        okayButton.addClickListener(new ClickListener() {
            public void onClick(final Widget sender)
            {
                doOkay();
            }
        });
        okayButton.setText("Okay");

        final Button cancelButton = new Button();
        flowPanel.add(cancelButton);
        cancelButton.addClickListener(new ClickListener() {
            public void onClick(final Widget sender)
            {
                doCancel();
            }
        });
        cancelButton.setText("Cancel");
        
        setFields();
    }

    /**
     * 
     */
    private void setFields()
    {
        for (UserDTO user : users)
        {
            userListBox.addItem(user.getFullname(), String.valueOf(user.getId()));
        }
    }

    /**
     * 
     */
    protected void doCancel()
    {
        hide();
    }

    /**
     * 
     */
    protected void doOkay()
    {
        List<UserDTO> usersSelected = new ArrayList<UserDTO>();
        for (int i = 0; i < userListBox.getItemCount(); i++)
        {
            if (userListBox.isItemSelected(i))
            {
                usersSelected.add(users.get(i));
            }
        }
        userListener.doUsersChosen(usersSelected);
        hide();
    }

}
