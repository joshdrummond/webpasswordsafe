/*
    Copyright 2011 Josh Drummond

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
import net.webpasswordsafe.common.model.IPLockout;
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
public class IPUnblockDialog extends Window
{

    private TextField<String> ipaddress;
    private FormData formData = new FormData("-20"); 
    private final static TextMessages textMessages = GWT.create(TextMessages.class);

    public IPUnblockDialog()
    {
        this.setHeading(textMessages.unblockIP());
        this.setModal(true);
        
        FormPanel form = new FormPanel();
        form.setHeaderVisible(false);
        form.setFrame(true);
        ipaddress = new TextField<String>();
        ipaddress.setFieldLabel(textMessages.ipAddress());
        ipaddress.addKeyListener(new KeyListener()
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
        form.add(ipaddress, formData);
        
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
        ipaddress.focus();
    }

    private void doCancel()
    {
        hide();
    }

    private void doOkay()
    {
        if (validateFields())
        {
            doUnblockIP(Utils.safeString(ipaddress.getValue()));
        }
    }
    
    private boolean validateFields()
    {
        if (Utils.safeString(ipaddress.getValue()).equals(""))
        {
            MessageBox.alert(textMessages.error(), textMessages.mustEnterIPAddress(), null);
            return false;
        }
        if (Utils.safeString(ipaddress.getValue()).length() > IPLockout.LENGTH_IPADDRESS)
        {
            MessageBox.alert(textMessages.error(), textMessages.tooLongIPAddress(), null);
            return false;
        }
        return true;
    }

    private void doUnblockIP(String ipaddress)
    {
        AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>()
        {
            @Override
            public void onFailure(Throwable caught)
            {
                WebPasswordSafe.handleServerFailure(caught);
            }
            @Override
            public void onSuccess(Boolean result)
            {
                hide();
                if (result)
                {
                    Info.display(textMessages.status(), textMessages.ipAddressUnblocked());
                }
                else
                {
                    Info.display(textMessages.status(), textMessages.ipAddressNotExist());
                }
            }
        };
        UserService.Util.getInstance().unblockIP(ipaddress, callback);
    }

}
