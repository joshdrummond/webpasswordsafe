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

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.joshdrummond.webpasswordsafe.client.model.common.UserDTO;

/**
 * @author Josh Drummond
 *
 */
public class UserDialog extends DialogBox
{
//    private MainWindow main;
    private UserDTO user;
    private TextBox usernameTextBox;
    private TextBox fullnameTextBox;
    private TextBox emailTextBox;
    private PasswordTextBox password1TextBox;
    private PasswordTextBox password2TextBox;
    private CheckBox enabledCheckBox;
    
    public UserDialog(UserDTO pUser)
    {
//        this.main = main;
        this.user = pUser;
        setHTML("User");

        final FlexTable flexTable = new FlexTable();
        setWidget(flexTable);
        flexTable.setSize("100%", "100%");

        final Label usernameLabel = new Label("Username");
        flexTable.setWidget(0, 0, usernameLabel);

        final Label fullnameLabel = new Label("Full Name");
        flexTable.setWidget(1, 0, fullnameLabel);

        final Label emailLabel = new Label("Email");
        flexTable.setWidget(2, 0, emailLabel);

        final Label password1Label = new Label("Password");
        flexTable.setWidget(3, 0, password1Label);

        final Label password2Label = new Label("");
        flexTable.setWidget(4, 0, password2Label);

        final Label enabledLabel = new Label("Enabled");
        flexTable.setWidget(5, 0, enabledLabel);

        usernameTextBox = new TextBox();
        flexTable.setWidget(0, 1, usernameTextBox);
        usernameTextBox.setWidth("100%");

        fullnameTextBox = new TextBox();
        flexTable.setWidget(1, 1, fullnameTextBox);
        fullnameTextBox.setWidth("100%");

        emailTextBox = new TextBox();
        flexTable.setWidget(2, 1, emailTextBox);
        emailTextBox.setWidth("100%");

        password1TextBox = new PasswordTextBox();
        flexTable.setWidget(3, 1, password1TextBox);
        password1TextBox.setWidth("100%");

        password2TextBox = new PasswordTextBox();
        flexTable.setWidget(4, 1, password2TextBox);
        password2TextBox.setWidth("100%");

        enabledCheckBox = new CheckBox();
        flexTable.setWidget(5, 1, enabledCheckBox);

        final FlowPanel flowPanel = new FlowPanel();
        flexTable.setWidget(6, 0, flowPanel);
        flexTable.getCellFormatter().setHorizontalAlignment(6, 0, HasHorizontalAlignment.ALIGN_CENTER);
        flexTable.getFlexCellFormatter().setColSpan(6, 0, 2);

        final Button saveButton = new Button();
        flowPanel.add(saveButton);
        saveButton.addClickListener(new ClickListener() {
            public void onClick(final Widget sender)
            {
                doSave();
            }
        });
        saveButton.setText("Save");

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
    
    private void setFields()
    {
        usernameTextBox.setText(user.getUsername());
        fullnameTextBox.setText(user.getFullname());
        emailTextBox.setText(user.getEmail());
        enabledCheckBox.setChecked(user.isActive());
    }
    
    private boolean validateFields()
    {
        return true;
    }
    
    private void doSave()
    {
        if (validateFields())
        {
            
        }
    }
    private void doCancel()
    {
        hide();
    }

}
