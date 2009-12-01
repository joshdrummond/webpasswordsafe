/*
    Copyright 2009 Josh Drummond

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

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.extjs.gxt.ui.client.widget.button.Button;
import java.util.List;
import java.util.ArrayList;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout.VBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayoutData;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.joshdrummond.webpasswordsafe.client.remote.PasswordService;
import com.joshdrummond.webpasswordsafe.common.model.Password;
import com.joshdrummond.webpasswordsafe.common.model.PasswordData;


/**
 * @author Josh Drummond
 *
 */
public class PasswordHistoryDialog extends Window
{
    private ListStore<PasswordHistoryData> gridStore;
    
    public PasswordHistoryDialog(Password password)
    {
        setSize("430", "460");
        setHeading("Password History");
        VBoxLayout boxLayout = new VBoxLayout();
        boxLayout.setVBoxLayoutAlign(VBoxLayoutAlign.CENTER);
        setLayout(boxLayout);
        setModal(true);
        
        gridStore = new ListStore<PasswordHistoryData>();
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
        ColumnConfig columnConfigPassword = new ColumnConfig("password", "Password Value", 110);
        TextField<String> text = new TextField<String>();
        text.setSelectOnFocus(true);
        text.setReadOnly(true);
        columnConfigPassword.setEditor(new CellEditor(text));
        configs.add(columnConfigPassword);
        ColumnConfig columnConfigDate = new ColumnConfig("date", "Date Created", 120);
        columnConfigDate.setDateTimeFormat(DateTimeFormat.getFormat("MM/dd/yyyy HH:mm:ss"));
        configs.add(columnConfigDate);
        ColumnConfig columnConfigUser = new ColumnConfig("user", "User Created", 150);
        configs.add(columnConfigUser);
        
        EditorGrid<PasswordHistoryData> grid = new EditorGrid<PasswordHistoryData>(gridStore, new ColumnModel(configs));
        grid.setStripeRows(true);
        add(grid);
        grid.setSize("400px", "390px");
        grid.setBorders(true);
        
        Button closeButton = new Button("Close", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                doClose();
            }
        });
        closeButton.setAutoWidth(true);
        closeButton.setAutoHeight(true);
        add(closeButton, new VBoxLayoutData(5, 5, 5, 5));
        
        loadPasswordHistoryData(password.getId());
    }

    private void loadPasswordHistoryData(long passwordId)
    {
        AsyncCallback<List<PasswordData>> callback = new AsyncCallback<List<PasswordData>>()
        {
            public void onFailure(Throwable caught)
            {
                MessageBox.alert("Error", caught.getMessage(), null);
            }
            public void onSuccess(List<PasswordData> result)
            {
                refreshData(result);
            }
        };
        PasswordService.Util.getInstance().getPasswordHistoryData(passwordId, callback);
    }

    private void refreshData(List<PasswordData> passwordDataList)
    {
        gridStore.removeAll();
        for (PasswordData passwordData : passwordDataList)
        {
            gridStore.add(new PasswordHistoryData(passwordData));
        }
    }
    
    private void doClose()
    {
        hide();
    }
    
    private class PasswordHistoryData extends BaseModel
    {
        private static final long serialVersionUID = 1L;

        public PasswordHistoryData(PasswordData passwordData)
        {
            set("id", passwordData.getId());
            set("password", passwordData.getPassword());
            set("date", passwordData.getDateCreated());
            set("user", passwordData.getUserCreated().getName());
        }
    }

}
