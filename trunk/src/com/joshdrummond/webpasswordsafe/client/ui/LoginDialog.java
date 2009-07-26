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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.joshdrummond.webpasswordsafe.client.MainWindow;
import com.joshdrummond.webpasswordsafe.client.model.common.UserDTO;
import com.joshdrummond.webpasswordsafe.client.remote.LoginService;

/**
 * 
 * @author Josh Drummond
 *
 */
public class LoginDialog extends DialogBox {

    private TextBox usernameTextBox;
    private PasswordTextBox passwordTextBox;
    private MainWindow main;
    
    public LoginDialog(MainWindow main) {
        //super();
        this.main = main;
        
        setText("Login");
        
        final FlexTable flexTable = new FlexTable();
        setWidget(flexTable);
        flexTable.setSize("100%", "100%");

        final Label usernameLabel = new Label("Username:");
        flexTable.setWidget(0, 0, usernameLabel);

        passwordTextBox = new PasswordTextBox();
        usernameTextBox = new TextBox();
        flexTable.setWidget(0, 2, usernameTextBox);
        usernameTextBox.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event)
			{
                if (event.getCharCode() == KeyCodes.KEY_ENTER)
                {
                    passwordTextBox.setFocus(true);
                }
			}
        });

        final Label passwordLabel = new Label("Password:");
        flexTable.setWidget(1, 0, passwordLabel);

        flexTable.setWidget(1, 2, passwordTextBox);
        passwordTextBox.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event)
			{
                if (event.getCharCode() == KeyCodes.KEY_ENTER)
                {
                    doSubmit();
                }
			}
        });

        final FlowPanel flowPanel = new FlowPanel();
        flexTable.setWidget(2, 0, flowPanel);
        flexTable.getCellFormatter().setHorizontalAlignment(2, 0, HasHorizontalAlignment.ALIGN_CENTER);
        flexTable.getFlexCellFormatter().setColSpan(2, 0, 3);

        final Button enterButton = new Button();
        flowPanel.add(enterButton);
        enterButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event)
			{
                doSubmit();
			}
        });
        enterButton.setText("Submit");

        final Button cancelButton = new Button();
        flowPanel.add(cancelButton);
        cancelButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                doCancel();
            }
        });
        cancelButton.setText("Cancel");

    }

    public void show()
    {
        super.show();
        usernameTextBox.setFocus(true);
    }
    
    private void doSubmit()
    {
        AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>()
        {

            public void onFailure(Throwable caught) {
                Window.alert("Error: "+caught.getMessage());
            }

            public void onSuccess(Boolean result) {
                if (result.booleanValue())
                {
                    doGetLoggedInUser();
                }
                else
                {
                    Window.alert("Invalid Login!");
                }
            }
        };
        LoginService.Util.getInstance().login(usernameTextBox.getText(), 
                passwordTextBox.getText(), callback);
    }
    
    private void doGetLoggedInUser()
    {
        AsyncCallback<UserDTO> callback = new AsyncCallback<UserDTO>()
        {

            public void onFailure(Throwable caught)
            {
                Window.alert("Error: "+caught.getMessage());
            }

            public void onSuccess(UserDTO result)
            {
                if (null != result)
                {
                    main.getClientModel().setLoggedInUser(result);
                    main.getClientModel().setLoggedIn(true);
                    main.refreshLoginStatus();
                    hide();
                }
                else
                {
                    Window.alert("Invalid User!");
                }
            }
        };
        LoginService.Util.getInstance().getLogin(callback);
    }
 
    private void doCancel()
    {
        hide();
    }
}
