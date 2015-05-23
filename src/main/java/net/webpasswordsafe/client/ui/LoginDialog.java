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

import net.webpasswordsafe.client.ClientSessionUtil;
import net.webpasswordsafe.client.MainWindow;
import net.webpasswordsafe.client.WebPasswordSafe;
import net.webpasswordsafe.client.i18n.TextMessages;
import net.webpasswordsafe.client.remote.LoginService;
import net.webpasswordsafe.common.util.Utils;
import net.webpasswordsafe.common.util.Constants.AuthenticationStatus;
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
import com.google.gwt.core.client.GWT;
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
    private boolean isSubmitting, isTwoStep;
    private String[] credentials;
    private final static TextMessages textMessages = GWT.create(TextMessages.class);

    public LoginDialog(MainWindow main)
    {
        this.main = main;
        this.setHeading(textMessages.login());
        this.setModal(true);
        this.setClosable(false);
        this.setOnEsc(false);
        this.setResizable(false);
        this.isSubmitting = false;
        this.isTwoStep = false;
        this.credentials = new String[2];
        
        FormPanel form = new FormPanel();
        form.setHeaderVisible(false);
        form.setFrame(true);
        usernameTextBox = new TextField<String>();
        usernameTextBox.setFieldLabel(textMessages.username());
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
        passwordTextBox.setFieldLabel(textMessages.password());
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
        
        enterButton = new Button(textMessages.submit(), new SelectionListener<ButtonEvent>() {
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
        setFocusWidget(usernameTextBox);
    }
    
    private void setSubmitting(boolean isSubmit)
    {
        usernameTextBox.setEnabled(!isSubmit);
        passwordTextBox.setEnabled(!isSubmit);
        enterButton.setEnabled(!isSubmit);
        isSubmitting = isSubmit;
    }
    
    private boolean validateFields()
    {
        if (Utils.safeString(usernameTextBox.getValue()).equals(""))
        {
            MessageBox.alert(textMessages.error(), textMessages.mustEnterUsername(), null);
            return false;
        }
        if (Utils.safeString(passwordTextBox.getValue()).equals(""))
        {
            MessageBox.alert(textMessages.error(), isTwoStep ? textMessages.mustEnterTwoStepVerificationCode() : textMessages.mustEnterPassword(), null);
            return false;
        }
        if (isTwoStep && (Utils.safeInt(passwordTextBox.getValue()) == -1))
        {
            MessageBox.alert(textMessages.error(), textMessages.mustEnterTwoStepVerificationCode(), null);
            return false;
        }
        return true;
    }

    private synchronized void doSubmit()
    {
        if (!isSubmitting)
        {
            if (validateFields())
            {
                setSubmitting(true);
                if (isTwoStep)
                {
                    credentials[1] = Utils.safeString(passwordTextBox.getValue());
                }
                else
                {
                    credentials[0] = Utils.safeString(passwordTextBox.getValue());
                    credentials[1] = "";
                }
                AsyncCallback<AuthenticationStatus> callback = new AsyncCallback<AuthenticationStatus>()
                {
                    @Override
                    public void onFailure(Throwable caught) {
                        WebPasswordSafe.handleServerFailure(caught);
                        setSubmitting(false);
                    }
                    @Override
                    public void onSuccess(AuthenticationStatus result) {
                        if (AuthenticationStatus.SUCCESS == result)
                        {
                            doLoginSuccess();
                        }
                        else if (AuthenticationStatus.TWO_STEP_REQ == result)
                        {
                            doTwoStep();
                        }
                        else
                        {
                            MessageBox.alert(textMessages.error(), textMessages.invalidLogin(), null);
                            isTwoStep = false;
                            passwordTextBox.setFieldLabel(textMessages.password());
                            passwordTextBox.setValue("");
                            setSubmitting(false);
                        }
                    }
                };
                LoginService.Util.getInstance().login(Utils.safeString(usernameTextBox.getValue()), 
                        credentials, callback);
            }
        }
    }
    
    private void doTwoStep()
    {
        setSubmitting(false);
        isTwoStep = true;
        usernameTextBox.setEnabled(false);
        passwordTextBox.setFieldLabel(textMessages.twoStepVerificationCode());
        passwordTextBox.setValue("");
        passwordTextBox.focus();
    }
    
    private void doLoginSuccess()
    {
        main.doGetLoggedInUser(this);
    }
    
    @Override
    public void doGetLoginSuccess()
    {
        Info.display(textMessages.status(), textMessages.loggedIn(Format.htmlEncode(ClientSessionUtil.getInstance().getLoggedInUser().getUsername())));
        hide();
    }
    
    @Override
    public void doGetLoginFailure()
    {
        MessageBox.alert(textMessages.error(), textMessages.invalidUser(), null);
    }
    
}
