/*
    Copyright 2008-2012 Josh Drummond

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
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.PasswordField;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * @author Josh Drummond
 *
 */
public class ChangePasswordDialog extends Window
{

    private PasswordField password1;
    private PasswordField password2;
//    private FormData formData = new FormData("-20"); 
    private final static TextMessages textMessages = GWT.create(TextMessages.class);

    public ChangePasswordDialog()
    {
        this.setHeadingText(textMessages.changePassword());
        this.setModal(true);
        
        FramedPanel panel = new FramedPanel();
        panel.setHeaderVisible(false);
        VerticalLayoutContainer p = new VerticalLayoutContainer();
        panel.add(p);
//        form.setFrame(true);
        password1 = new PasswordField();
        p.add(new FieldLabel(password1, textMessages.newPassword()));//, new VerticalLayoutData(1, -1));
        password1.addKeyPressHandler(new KeyPressHandler()
        {
            @Override
            public void onKeyPress(KeyPressEvent event)
            {
                if (event.getCharCode() == KeyCodes.KEY_ENTER)
                {
                    password2.focus();
                }
            }
        });
//        form.add(password1, formData);
        password2 = new PasswordField();
        p.add(new FieldLabel(password2, textMessages.reenterPassword()));//, new VerticalLayoutData(1, -1));
        password2.addKeyPressHandler(new KeyPressHandler()
        {
            @Override
            public void onKeyPress(KeyPressEvent event)
            {
                if (event.getCharCode() == KeyCodes.KEY_ENTER)
                {
                    doOkay();
                }
            }
        });
//        form.add(password2, formData);
        
        TextButton okayButton = new TextButton(textMessages.okay(), new SelectHandler()
        {
            @Override
            public void onSelect(SelectEvent event)
            {
                doOkay();
            }
        });
        TextButton cancelButton = new TextButton(textMessages.cancel(), new SelectHandler()
        {
            @Override
            public void onSelect(SelectEvent event)
            {
                doCancel();
            }
        });
        panel.setButtonAlign(BoxLayoutPack.CENTER);
        panel.addButton(okayButton);
        panel.addButton(cancelButton);
        this.add(panel);
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
            (new AlertMessageBox(textMessages.error(), textMessages.mustMatchPasswords())).show();
            return false;
        }
        if (Utils.safeString(password1.getValue()).equals(""))
        {
            (new AlertMessageBox(textMessages.error(), textMessages.mustEnterPassword())).show();
            return false;
        }
        if (Utils.safeString(password1.getValue()).length() > UserAuthnPassword.LENGTH_PASSWORD)
        {
            (new AlertMessageBox(textMessages.error(), textMessages.tooLongPassword())).show();
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
