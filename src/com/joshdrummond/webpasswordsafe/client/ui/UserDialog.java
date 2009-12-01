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

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.joshdrummond.webpasswordsafe.client.remote.UserService;
import com.joshdrummond.webpasswordsafe.common.model.User;
import com.joshdrummond.webpasswordsafe.common.util.Utils;


/**
 * @author Josh Drummond
 *
 */
public class UserDialog extends Window
{
    private User user;
    private TextField<String> usernameTextBox;
    private TextField<String> fullnameTextBox;
    private TextField<String> emailTextBox;
    private TextField<String> password1TextBox;
    private TextField<String> password2TextBox;
    private CheckBox enabledCheckBox;
    
    public UserDialog(User user)
    {
        this.user = user;
        this.setHeading("User");
        this.setModal(true);
        
        FormPanel form = new FormPanel();
        form.setHeaderVisible(false);
        form.setFrame(true);

        usernameTextBox = new TextField<String>();
        usernameTextBox.setFieldLabel("Username");
        form.add(usernameTextBox, new FormData("-20"));
        fullnameTextBox = new TextField<String>();
        fullnameTextBox.setFieldLabel("Full Name");
        form.add(fullnameTextBox, new FormData("-20"));
        emailTextBox = new TextField<String>();
        emailTextBox.setFieldLabel("Email");
        form.add(emailTextBox, new FormData("-20"));
        password1TextBox = new TextField<String>();
        password1TextBox.setPassword(true);
        password1TextBox.setFieldLabel("Password");
        form.add(password1TextBox, new FormData("-20"));
        password2TextBox = new TextField<String>();
        password2TextBox.setPassword(true);
        form.add(password2TextBox, new FormData("-20"));
        enabledCheckBox = new CheckBox();
        enabledCheckBox.setFieldLabel("Enabled");
        form.add(enabledCheckBox, new FormData("-20"));

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

        this.add(form);
    }
    
    private void setFields()
    {
        usernameTextBox.setValue(user.getUsername());
        fullnameTextBox.setValue(user.getFullname());
        emailTextBox.setValue(user.getEmail());
        enabledCheckBox.setValue(user.isActiveFlag());
    }
    
    private boolean validateFields()
    {
        if (!(Utils.safeString(password2TextBox.getValue())).equals(Utils.safeString(password1TextBox.getValue())))
        {
            MessageBox.alert("Error", "Passwords don't match", null);
            return false;
        }
        return true;
    }
    
    private void doSave()
    {
        if (validateFields())
        {
            user.setUsername(Utils.safeString(usernameTextBox.getValue()));
            user.setFullname(Utils.safeString(fullnameTextBox.getValue()));
            user.setEmail(Utils.safeString(emailTextBox.getValue()));
            user.setActiveFlag(enabledCheckBox.getValue());
            user.setPassword(Utils.safeString(password1TextBox.getValue()));
            
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
            if (user.getId() == 0)
            {
                UserService.Util.getInstance().addUser(user, callback);
            }
            else
            {
                UserService.Util.getInstance().updateUser(user, callback);
            }
        }
    }
    
    private void doCancel()
    {
        hide();
    }

}
