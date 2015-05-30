/*
    Copyright 2013-2015 Josh Drummond

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
import net.webpasswordsafe.client.WebPasswordSafe;
import net.webpasswordsafe.client.i18n.TextMessages;
import net.webpasswordsafe.client.remote.UserService;
import net.webpasswordsafe.common.model.UserAuthnTOTP;
import net.webpasswordsafe.common.util.Utils;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.AbsoluteData;
import com.extjs.gxt.ui.client.widget.layout.AbsoluteLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * @author Josh Drummond
 *
 */
public class TwoStepVerificationDialog extends Window
{
    private final static TextMessages textMessages = GWT.create(TextMessages.class);
    private TextField<String> keyTextBox;
    private UserAuthnTOTP userAuthnTOTP;
    private CheckBox enabledCheckBox;
    private Html qrCodeDiv;
    private JavaScriptObject qrCodeJS;

    public TwoStepVerificationDialog()
    {
        this.setHeading(textMessages.twoStepVerification());
        this.setModal(true);
        this.setLayout(new AbsoluteLayout());
        this.setSize(375, 387);
        this.setResizable(false);

        LabelField lblfldDesc = new LabelField(textMessages.twoStepKeyInstructions());
        add(lblfldDesc, new AbsoluteData(7, 6));

        enabledCheckBox = new CheckBox();
        enabledCheckBox.addListener(Events.OnClick, new Listener<BaseEvent>()
        {
            @Override
            public void handleEvent(BaseEvent be)
            {
                doFirstTimeKey();
            }
        });
        enabledCheckBox.setBoxLabel(textMessages.enabled());
        add(enabledCheckBox, new AbsoluteData(82, 34));

        Button generateButton = new Button(textMessages.newKey(), new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                doGenerateKey();
            }
        });
        add(generateButton, new AbsoluteData(290, 62));
        generateButton.setSize("60px", "22px");

        LabelField lblfldKey = new LabelField(textMessages.key_());
        add(lblfldKey, new AbsoluteData(7, 62));

        keyTextBox = new TextField<String>();
        keyTextBox.setReadOnly(true);
        add(keyTextBox, new AbsoluteData(82, 62));
        keyTextBox.setSize("200px", "22px");

        qrCodeDiv = new Html("<div id=\"qrcode\"></div>");
        add(qrCodeDiv, new AbsoluteData(82, 90));

        Button saveButton = new Button(textMessages.save(), new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                doSave();
            }
        });

        Button cancelButton = new Button(textMessages.cancel(), new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                doCancel();
            }
        });
        
        setButtonAlign(HorizontalAlignment.CENTER);
        addButton(saveButton);
        addButton(cancelButton);
        
        doLoadTwoStepVerificationInfo();
    }
    
    private void doFirstTimeKey()
    {
        if (enabledCheckBox.getValue() && Utils.safeString(keyTextBox.getValue()).equals(""))
        {
            doGenerateKey();
        }
    }
    
    @Override
    public void show()
    {
        super.show();
    }
    
    private void refreshKey(String key)
    {
        key = Utils.safeString(key);
        keyTextBox.setValue(key);
        if (!key.equals(""))
        {
            if (null == qrCodeJS)
            {
                qrCodeJS = createQRCode(getQRBarcodeURL());
            }
            else
            {
                updateQRCode(qrCodeJS, getQRBarcodeURL());
            }
        }
    }
    
    private String getQRBarcodeURL()
    {
        return getQRBarcodeURL(ClientSessionUtil.getInstance().getLoggedInUser().getUsername(),
                "WebPasswordSafe",
                Utils.safeString(keyTextBox.getValue()));
    }
    
    private String getQRBarcodeURL(String user, String issuer, String secret)
    { 
        return "otpauth://totp/"+issuer+":"+user+"?secret="+secret+"&issuer="+issuer;
    }
    
    private static native JavaScriptObject createQRCode(String url) /*-{
        var qrcode = new $wnd.QRCode("qrcode", {
           text: url,
           width: 200,
           height: 200,
           colorDark: "#000000",
           colorLight: "#ffffff",
           correctLevel: $wnd.QRCode.CorrectLevel.M
        });
        return qrcode;
    }-*/;
    
    private static native void updateQRCode(JavaScriptObject qrcode, String url) /*-{
        qrcode.makeCode(url);
    }-*/;

    private void setFields()
    {
        enabledCheckBox.setValue(userAuthnTOTP.isEnabled());
        refreshKey(userAuthnTOTP.getKey());
    }
    
    private void doLoadTwoStepVerificationInfo()
    {
        AsyncCallback<UserAuthnTOTP> callback = new AsyncCallback<UserAuthnTOTP>()
        {
            @Override
            public void onFailure(Throwable caught)
            {
                WebPasswordSafe.handleServerFailure(caught);
            }
            @Override
            public void onSuccess(UserAuthnTOTP result)
            {
                userAuthnTOTP = result;
                setFields();
            }
        };
        UserService.Util.getInstance().getCurrentUserTOTP(callback);
    }
    
    private void doGenerateKey()
    {
        AsyncCallback<String> callback = new AsyncCallback<String>()
        {
            @Override
            public void onFailure(Throwable caught)
            {
                WebPasswordSafe.handleServerFailure(caught);
            }
            @Override
            public void onSuccess(String result)
            {
                refreshKey(result);
            }
        };
        UserService.Util.getInstance().generateTOTPKey(callback);
    }
    
    private boolean validateFields()
    {
        return true;
    }
    
    private void doSave()
    {
        if (validateFields())
        {
            userAuthnTOTP.setKey(Utils.safeString(keyTextBox.getValue()));
            userAuthnTOTP.setEnabled(enabledCheckBox.getValue());

            final AsyncCallback<Void> callback = new AsyncCallback<Void>()
            {
                @Override
                public void onFailure(Throwable caught)
                {
                    WebPasswordSafe.handleServerFailure(caught);
                }
                @Override
                public void onSuccess(Void result)
                {
                    Info.display(textMessages.status(), textMessages.twoStepVerificationSaved());
                    hide();
                }
            };
            UserService.Util.getInstance().updateCurrentUserTOTP(userAuthnTOTP, callback);
        }
    }
    
    private void doCancel()
    {
        hide();
    }

}
