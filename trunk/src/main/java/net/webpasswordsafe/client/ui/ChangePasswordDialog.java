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

import net.webpasswordsafe.client.WebPasswordSafe;
import net.webpasswordsafe.client.i18n.TextMessages;
import net.webpasswordsafe.client.remote.UserService;
import net.webpasswordsafe.common.model.UserAuthnPassword;
import net.webpasswordsafe.common.util.Utils;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
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
 * @author Josh Drummond
 *
 */
public class ChangePasswordDialog extends Window
{

    private TextField<String> password1;
    private TextField<String> password2;
    private FormData formData = new FormData("-20"); 
    private final static TextMessages textMessages = GWT.create(TextMessages.class);

    public ChangePasswordDialog()
    {
        this.setHeading(textMessages.changePassword());
        this.setModal(true);
        
        FormPanel form = new FormPanel();
        form.setHeaderVisible(false);
        form.setFrame(true);
        password1 = new TextField<String>();
        password1.setFieldLabel(textMessages.newPassword());
        password1.setPassword(true);
        password1.addKeyListener(new KeyListener()
        {
            @Override
            public void componentKeyPress(ComponentEvent event)
            {
                if (event.getKeyCode() == KeyCodes.KEY_ENTER)
                {
                    password2.focus();
                }
            }
        });
        form.add(password1, formData);
        password2 = new TextField<String>();
        password2.setFieldLabel(textMessages.reenterPassword());
        password2.setPassword(true);
        password2.addKeyListener(new KeyListener()
        {
            @Override
            public void componentKeyPress(ComponentEvent event)
            {
                if (event.getKeyCode() == KeyCodes.KEY_ENTER)
                {
                    doOkay();
                }
            }
        });
        form.add(password2, formData);
        
        Button okayButton = new Button(textMessages.okay(), new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                doOkay();
            }
        });
        Button cancelButton = new Button(textMessages.cancel(), new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                doCancel();
            }
        });
        form.setButtonAlign(HorizontalAlignment.CENTER);
        form.addButton(okayButton);
        form.addButton(cancelButton);
        this.add(form);
    }

    @Override
    public void show()
    {
        super.show();
        password1.focus();
    }

    private void doCancel()
    {
        hide();
    }

    private void doOkay()
    {
        if (validateFields())
        {
            doChangePassword(Utils.safeString(password1.getValue()));
        }
    }
    
    private boolean validateFields()
    {
        if (!(Utils.safeString(password2.getValue())).equals(Utils.safeString(password1.getValue())))
        {
            MessageBox.alert(textMessages.error(), textMessages.mustMatchPasswords(), null);
            return false;
        }
        if (Utils.safeString(password1.getValue()).equals(""))
        {
            MessageBox.alert(textMessages.error(), textMessages.mustEnterPassword(), null);
            return false;
        }
        if (Utils.safeString(password1.getValue()).length() > UserAuthnPassword.LENGTH_PASSWORD)
        {
            MessageBox.alert(textMessages.error(), textMessages.tooLongPassword(), null);
            return false;
        }
        return true;
    }

    /**
     * @param password
     */
    private void doChangePassword(String password)
    {
        AsyncCallback<Void> callback = new AsyncCallback<Void>()
        {
            @Override
            public void onFailure(Throwable caught)
            {
                WebPasswordSafe.handleServerFailure(caught);
            }
            @Override
            public void onSuccess(Void result)
            {
                hide();
                Info.display(textMessages.status(), textMessages.passwordChanged());
            }
        };
        UserService.Util.getInstance().changePassword(password, callback);
    }

}
