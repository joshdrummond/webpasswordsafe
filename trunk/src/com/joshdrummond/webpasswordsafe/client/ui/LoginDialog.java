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
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.joshdrummond.webpasswordsafe.client.MainWindow;
import com.joshdrummond.webpasswordsafe.client.remote.LoginService;

public class LoginDialog extends DialogBox {

    private TextBox textBox;
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
        textBox = new TextBox();
        flexTable.setWidget(0, 2, textBox);
        textBox.addKeyboardListener(new KeyboardListener() {
            public void onKeyDown(Widget sender, char keyCode, int modifiers) {
            }
            public void onKeyPress(Widget sender, char keyCode, int modifiers) {
                if (keyCode == KEY_ENTER)
                {
                    passwordTextBox.setFocus(true);
                }
            }
            public void onKeyUp(Widget sender, char keyCode, int modifiers) {
            }
        });

        final Label passwordLabel = new Label("Password:");
        flexTable.setWidget(1, 0, passwordLabel);

        flexTable.setWidget(1, 2, passwordTextBox);
        passwordTextBox.addKeyboardListener(new KeyboardListener() {
            public void onKeyDown(Widget sender, char keyCode, int modifiers) {
            }
            public void onKeyPress(Widget sender, char keyCode, int modifiers) {
                if (keyCode == KEY_ENTER)
                {
                    submit();
                }
            }
            public void onKeyUp(Widget sender, char keyCode, int modifiers) {
            }
        });

        final Button enterButton = new Button();
        flexTable.setWidget(2, 0, enterButton);
        enterButton.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                submit();
            }
        });
        enterButton.setText("Submit");

        final Button cancelButton = new Button();
        flexTable.setWidget(2, 2, cancelButton);
        cancelButton.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                cancel();
            }
        });
        cancelButton.setText("Cancel");

    }

//        public void show()
//        {
//            super.show();
//            textBox.setFocus(true);
//        }
    
    private void submit()
    {
        AsyncCallback callback = new AsyncCallback()
        {

            public void onFailure(Throwable caught) {
                Window.alert("Error: "+caught.getMessage());
            }

            public void onSuccess(Object result) {
                if (((Boolean)result).booleanValue())
                {
                    main.getClientModel().setUserName(textBox.getText());
                    main.refreshLoginStatus();
                    hide();
                }
                else
                {
                    Window.alert("Invalid Login!");
                }
            }
        };
        LoginService.Util.getInstance().login(textBox.getText(), 
                passwordTextBox.getText(), callback);
    }
    
    private void cancel()
    {
        hide();
    }
}
