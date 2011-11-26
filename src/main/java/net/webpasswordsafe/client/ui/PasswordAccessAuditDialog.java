/*
    Copyright 2009-2011 Josh Drummond

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

import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.extjs.gxt.ui.client.widget.button.Button;
import java.util.List;
import java.util.ArrayList;
import net.webpasswordsafe.client.WebPasswordSafe;
import net.webpasswordsafe.client.i18n.TextMessages;
import net.webpasswordsafe.client.remote.PasswordService;
import net.webpasswordsafe.common.model.Password;
import net.webpasswordsafe.common.model.PasswordAccessAudit;
import net.webpasswordsafe.common.util.Constants;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout.VBoxLayoutAlign;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * @author Josh Drummond
 *
 */
public class PasswordAccessAuditDialog extends Window
{
    private ListStore<PasswordAccessAuditData> gridStore;
    private final static TextMessages textMessages = GWT.create(TextMessages.class);

    public PasswordAccessAuditDialog(Password password)
    {
        this.setSize("350", "460");
        this.setHeading(textMessages.passwordAccessAuditLog());
        VBoxLayout boxLayout = new VBoxLayout();
        boxLayout.setVBoxLayoutAlign(VBoxLayoutAlign.CENTER);
        this.setLayout(boxLayout);
        this.setModal(true);
        this.setResizable(false);
        
        gridStore = new ListStore<PasswordAccessAuditData>();
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
        ColumnConfig columnConfigDate = new ColumnConfig(Constants.DATE, textMessages.dateAccessed(), 130);
        columnConfigDate.setDateTimeFormat(DateTimeFormat.getFormat(textMessages.displayDateFormat()));
        configs.add(columnConfigDate);
        ColumnConfig columnConfigUser = new ColumnConfig(Constants.USER, textMessages.userAccessed(), 160);
        configs.add(columnConfigUser);
        
        Grid<PasswordAccessAuditData> grid = new Grid<PasswordAccessAuditData>(gridStore, new ColumnModel(configs));
        grid.setStripeRows(true);
        add(grid);
        grid.setSize("320px", "390px");
        grid.setBorders(true);
        
        Button closeButton = new Button(textMessages.close(), new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                doClose();
            }
        });
        
        setButtonAlign(HorizontalAlignment.CENTER);
        addButton(closeButton);
        
        loadAccessAuditData(password.getId());
    }

    private void loadAccessAuditData(long passwordId)
    {
        AsyncCallback<List<PasswordAccessAudit>> callback = new AsyncCallback<List<PasswordAccessAudit>>()
        {
            @Override
            public void onFailure(Throwable caught)
            {
                WebPasswordSafe.handleServerFailure(caught);
            }
            @Override
            public void onSuccess(List<PasswordAccessAudit> result)
            {
                refreshData(result);
            }
        };
        PasswordService.Util.getInstance().getPasswordAccessAuditData(passwordId, callback);
    }

    private void refreshData(List<PasswordAccessAudit> passwordAccessAuditList)
    {
        gridStore.removeAll();
        for (PasswordAccessAudit passwordAccessAudit : passwordAccessAuditList)
        {
            gridStore.add(new PasswordAccessAuditData(passwordAccessAudit));
        }
    }
    
    private void doClose()
    {
        hide();
    }
    
    private class PasswordAccessAuditData extends BaseModel
    {
        private static final long serialVersionUID = 1L;

        public PasswordAccessAuditData(PasswordAccessAudit passwordAccessAudit)
        {
            set(Constants.ID, passwordAccessAudit.getId());
            set(Constants.DATE, passwordAccessAudit.getDateAccessed());
            set(Constants.USER, Format.htmlEncode(passwordAccessAudit.getUser().getName()));
        }
    }

}
