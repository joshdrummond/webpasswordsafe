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

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.Widget;
import com.joshdrummond.webpasswordsafe.client.remote.UserService;

/**
 * @author Josh Drummond
 *
 */
public class ChangePasswordDialog extends DialogBox
{

    private PasswordTextBox password1TextBox;
    private PasswordTextBox password2TextBox;

    public ChangePasswordDialog()
    {
        setHTML("Change Password");

        final FlexTable flexTable = new FlexTable();
        setWidget(flexTable);
        flexTable.setSize("100%", "100%");

        final Label password1Label = new Label("New Password");
        flexTable.setWidget(0, 0, password1Label);

        password1TextBox = new PasswordTextBox();
        flexTable.setWidget(0, 1, password1TextBox);
        password1TextBox.setWidth("100%");

        final Label password2Label = new Label("New Password");
        flexTable.setWidget(1, 0, password2Label);

        password2TextBox = new PasswordTextBox();
        flexTable.setWidget(1, 1, password2TextBox);
        password2TextBox.setWidth("100%");

        final FlowPanel flowPanel = new FlowPanel();
        flexTable.setWidget(2, 0, flowPanel);
        flexTable.getCellFormatter().setHorizontalAlignment(2, 0, HasHorizontalAlignment.ALIGN_CENTER);
        flexTable.getFlexCellFormatter().setColSpan(2, 0, 2);

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
        String pw1 = password1TextBox.getText().trim();
        String pw2 = password2TextBox.getText().trim();
        if (pw1.equals(pw2))
        {
            if (pw1.equals("") || pw2.equals(""))
            {
                Window.alert("Must enter a password");
            }
            else
            {
                doChangePassword(pw1);
            }
        }
        else
        {
            Window.alert("Passwords must match");
        }
    }

    /**
     * @param password
     */
    private void doChangePassword(String password)
    {
        AsyncCallback<Void> callback = new AsyncCallback<Void>()
        {
            public void onFailure(Throwable caught)
            {
                Window.alert("Error: "+caught.getMessage());
            }

            public void onSuccess(Void result)
            {
                hide();
            }
        };
        UserService.Util.getInstance().changePassword(password, callback);
    }

}
