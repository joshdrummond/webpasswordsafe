/*
    Copyright 2008-2011 Josh Drummond

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

import net.webpasswordsafe.client.ClientSessionUtil;
import net.webpasswordsafe.client.MainWindow;
import net.webpasswordsafe.client.WebPasswordSafe;
import net.webpasswordsafe.client.remote.LoginService;
import net.webpasswordsafe.common.util.Utils;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * 
 * @author Josh Drummond
 *
 */
public class LoginDialog extends Window
    implements LoginWindow
{
    private TextField<String> usernameTextBox;
    private TextField<String> passwordTextBox;
    private Button enterButton;
    private MainWindow main;
    private boolean isSubmitting;
    
    public LoginDialog(MainWindow main)
    {
        this.main = main;
        this.setHeading("Login");
        this.setModal(true);
        this.setClosable(false);
        this.setOnEsc(false);
        this.setResizable(false);
        this.isSubmitting = false;
        
        FormPanel form = new FormPanel();
        form.setHeaderVisible(false);
        form.setFrame(true);
        usernameTextBox = new TextField<String>();
        usernameTextBox.setFieldLabel("Username");
        usernameTextBox.addKeyListener(new KeyListener()
        {
            @Override
            public void componentKeyPress(ComponentEvent event)
            {
                if (event.getKeyCode() == KeyCodes.KEY_ENTER)
                {
                    passwordTextBox.focus();
                }
            }
        });
        form.add(usernameTextBox, new FormData("-20"));
        passwordTextBox = new TextField<String>();
        passwordTextBox.setFieldLabel("Password");
        passwordTextBox.setPassword(true);
        passwordTextBox.addKeyListener(new KeyListener()
        {
            @Override
            public void componentKeyPress(ComponentEvent event)
            {
                if (event.getKeyCode() == KeyCodes.KEY_ENTER)
                {
                    doSubmit();
                }
            }
        });
        form.add(passwordTextBox, new FormData("-20"));
        
        enterButton = new Button("Submit", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                doSubmit();
            }
        });
        form.setButtonAlign(HorizontalAlignment.CENTER);
        form.addButton(enterButton);
        this.add(form);
    }

    @Override
    public void show()
    {
        super.show();
        usernameTextBox.focus();
    }
    
    private void setSubmitting(boolean isSubmit)
    {
        usernameTextBox.setEnabled(!isSubmit);
        passwordTextBox.setEnabled(!isSubmit);
        enterButton.setEnabled(!isSubmit);
        isSubmitting = isSubmit;
    }
    
    private synchronized void doSubmit()
    {
        if (!isSubmitting)
        {
            setSubmitting(true);
            AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>()
            {
                @Override
                public void onFailure(Throwable caught) {
                    WebPasswordSafe.handleServerFailure(caught);
                    setSubmitting(false);
                }
                @Override
                public void onSuccess(Boolean result) {
                    if (result.booleanValue())
                    {
                        doLoginSuccess();
                    }
                    else
                    {
                        MessageBox.alert("Error", "Invalid Login!", null);
                        setSubmitting(false);
                    }
                }
            };
            LoginService.Util.getInstance().login(Utils.safeString(usernameTextBox.getValue()), 
                    Utils.safeString(passwordTextBox.getValue()), callback);
        }
    }
    
    private void doLoginSuccess()
    {
        main.doGetLoggedInUser(this);
    }
    
    @Override
    public void doGetLoginSuccess()
    {
        Info.display("Status", Format.htmlEncode(ClientSessionUtil.getInstance().getLoggedInUser().getUsername()) + " logged in");
        hide();
    }
    
    @Override
    public void doGetLoginFailure()
    {
        MessageBox.alert("Error", "Invalid User!", null);
    }
    
}
