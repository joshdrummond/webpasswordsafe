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
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
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
import net.webpasswordsafe.client.WebPasswordSafe;
import net.webpasswordsafe.client.i18n.TextMessages;
import net.webpasswordsafe.client.remote.PasswordService;
import net.webpasswordsafe.common.model.Password;
import net.webpasswordsafe.common.model.PasswordData;
import net.webpasswordsafe.common.util.Constants;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout.VBoxLayoutAlign;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * @author Josh Drummond
 *
 */
public class PasswordHistoryDialog extends Window
{
    private ListStore<PasswordHistoryData> gridStore;
    private final static TextMessages textMessages = GWT.create(TextMessages.class);

    public PasswordHistoryDialog(Password password)
    {
        this.setSize("430", "460");
        this.setHeading(textMessages.passwordHistory());
        VBoxLayout boxLayout = new VBoxLayout();
        boxLayout.setVBoxLayoutAlign(VBoxLayoutAlign.CENTER);
        this.setLayout(boxLayout);
        this.setModal(true);
        this.setResizable(false);
        
        gridStore = new ListStore<PasswordHistoryData>();
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
        ColumnConfig columnConfigPassword = new ColumnConfig(Constants.PASSWORD, textMessages.passwordValue(), 110);
        TextField<String> text = new TextField<String>();
        text.setSelectOnFocus(true);
        text.setReadOnly(true);
        columnConfigPassword.setEditor(new CellEditor(text)
        {
            @Override
            public Object preProcessValue(Object value)
            {
                return Format.htmlDecode(String.valueOf(value));
            }
            @Override
            public Object postProcessValue(Object value)
            {
                return Format.htmlEncode(String.valueOf(value));
            }
        });
        configs.add(columnConfigPassword);
        ColumnConfig columnConfigDate = new ColumnConfig(Constants.DATE, textMessages.dateCreated(), 120);
        columnConfigDate.setDateTimeFormat(DateTimeFormat.getFormat(textMessages.displayDateFormat()));
        configs.add(columnConfigDate);
        ColumnConfig columnConfigUser = new ColumnConfig(Constants.USER, textMessages.userCreated(), 150);
        configs.add(columnConfigUser);
        
        EditorGrid<PasswordHistoryData> grid = new EditorGrid<PasswordHistoryData>(gridStore, new ColumnModel(configs));
        grid.setStripeRows(true);
        add(grid);
        grid.setSize("400px", "390px");
        grid.setBorders(true);
        
        Button closeButton = new Button(textMessages.close(), new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                doClose();
            }
        });
        
        setButtonAlign(HorizontalAlignment.CENTER);
        addButton(closeButton);

        loadPasswordHistoryData(password.getId());
    }

    private void loadPasswordHistoryData(long passwordId)
    {
        AsyncCallback<List<PasswordData>> callback = new AsyncCallback<List<PasswordData>>()
        {
            @Override
            public void onFailure(Throwable caught)
            {
                WebPasswordSafe.handleServerFailure(caught);
            }
            @Override
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
            set(Constants.ID, passwordData.getId());
            set(Constants.PASSWORD, Format.htmlEncode(passwordData.getPassword()));
            set(Constants.DATE, passwordData.getDateCreated());
            set(Constants.USER, Format.htmlEncode(passwordData.getUserCreated().getName()));
        }
    }

}
