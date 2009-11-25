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

import java.util.ArrayList;
import java.util.List;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.layout.BoxLayout.BoxLayoutPack;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.joshdrummond.webpasswordsafe.client.remote.PasswordService;
import com.joshdrummond.webpasswordsafe.common.model.Password;
import com.joshdrummond.webpasswordsafe.common.model.Tag;


/**
 * @author Josh Drummond
 *
 */
public class PasswordSearchPanel extends ContentPanel
{
    private Grid<PasswordSearchData> passwordGrid;
    private ListStore<PasswordSearchData> store;
//    private Tree tagTree;
//    private TreeItem rootTreeItem;
    private TextField<String> searchTextBox;
    private CheckBox activeOnlyCheckBox;
    private List<Tag> tags;

    public PasswordSearchPanel()
    {
    	setLayout(new BorderLayout());
    	setHeaderVisible(false);
    	
    	ContentPanel northPanel = new ContentPanel();
    	northPanel.setHeading("Password Search");
    	ContentPanel westPanel = new ContentPanel(new FillLayout());
    	westPanel.setHeading("Tag(s)");
    	ContentPanel centerPanel = new ContentPanel(new FillLayout());
    	centerPanel.setHeading("Password(s)");
    	
    	HBoxLayout northLayout = new HBoxLayout();  
    	northLayout.setPadding(new Padding(5));  
    	northLayout.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);  
    	northLayout.setPack(BoxLayoutPack.CENTER);  
    	northPanel.setLayout(northLayout);  

//    	Text passwordLabel = new Text("Password");
    	searchTextBox = new TextField<String>();
        searchTextBox.addKeyListener(new KeyListener() {
        	@Override
        	public void componentKeyPress(ComponentEvent event) {
        		if (event.getKeyCode() == KeyCodes.KEY_ENTER)
        		{
                    doSearch();
        		}
        	}
        });
//        searchTextBox.setVisibleLength(30);
        searchTextBox.setWidth(300);
        searchTextBox.setMaxLength(1000);
        searchTextBox.focus();

        Button searchButton = new Button("Search", new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
                doSearch();
			}
		});
        activeOnlyCheckBox = new CheckBox();
        activeOnlyCheckBox.setBoxLabel("Active Only");
        activeOnlyCheckBox.setValue(true);
    	northPanel.add(searchTextBox, new HBoxLayoutData(new Margins(0, 5, 0, 0)));  
        northPanel.add(searchButton, new HBoxLayoutData(new Margins(0, 5, 0, 0)));  
        northPanel.add(activeOnlyCheckBox, new HBoxLayoutData(new Margins(0, 5, 0, 5)));  
        
        TreeStore<ModelData> treeStore = new TreeStore<ModelData>();  
//        store.add(model.getChildren(), true);  
        TreePanel<ModelData> tree = new TreePanel<ModelData>(treeStore);  
        tree.setDisplayProperty("name");  
//        tree.getStyle().setLeafIcon(Examples.ICONS.music());  
        tree.setWidth(250);  
        /*
        tags = new ArrayList<TagDTO>();
        tagTree = new Tree();
        scrollPanel.setWidget(tagTree);
        tagTree.addSelectionHandler(new SelectionHandler<TreeItem>() {
			public void onSelection(SelectionEvent<TreeItem> event)
			{
                doTagClicked(event.getSelectedItem());
			}
        });
        tagTree.addOpenHandler(new OpenHandler<TreeItem>() {
			public void onOpen(OpenEvent<TreeItem> event)
			{
                doTagExpanded(event.getTarget());
			}
        });
        tagTree.setSize("100%", "100%");
        rootTreeItem = new TreeItem("<b>Tags</b>");
        tagTree.addItem(rootTreeItem);
        */
        westPanel.add(tree);
        
        centerPanel.setScrollMode(Scroll.AUTOX);
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>(4);
        ColumnConfig column = new ColumnConfig();
        column.setId("name");
        column.setHeader("Name");
        column.setWidth(200);
        configs.add(column);
        column = new ColumnConfig();
        column.setId("username");
        column.setHeader("Username");
        column.setWidth(100);
        configs.add(column);
        column = new ColumnConfig();
        column.setId("password");
        column.setHeader("Password");
        column.setWidth(100);
        configs.add(column);
        column = new ColumnConfig();
        column.setId("tags");
        column.setHeader("Tags");
        column.setWidth(200);
        configs.add(column);
        column = new ColumnConfig();
        column.setId("notes");
        column.setHeader("Notes");
        column.setWidth(300);
        configs.add(column);
        
        store = new ListStore<PasswordSearchData>();
        ColumnModel cm = new ColumnModel(configs);
        passwordGrid = new Grid<PasswordSearchData>(store, cm);
        passwordGrid.setStyleAttribute("borderTop", "none");
        passwordGrid.setBorders(true);
        passwordGrid.setStripeRows(true);
        passwordGrid.addListener(Events.CellClick, new Listener<GridEvent<PasswordSearchData>>()
        {
            public void handleEvent(GridEvent<PasswordSearchData> ge)
            {
                if (2 == ge.getColIndex())
                {
                    doShowPasswordPopup((Long)ge.getModel().get("id"));
                }
                else
                {
                    doLoadPasswordDialog((Long)ge.getModel().get("id"));
                }
            }
        });
        centerPanel.add(passwordGrid);
        
    	BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH, 100);  
    	northData.setCollapsible(true);  
    	northData.setFloatable(false);  
    	northData.setHideCollapseTool(false);  
    	northData.setSplit(true);
    	northData.setMargins(new Margins(5, 5, 0, 5));  
    	
    	BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 150);  
    	westData.setSplit(true);  
    	westData.setCollapsible(true);  
    	westData.setMargins(new Margins(5));  
    	
    	BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);  
    	centerData.setMargins(new Margins(5, 0, 5, 0));  
    	
    	add(northPanel, northData);
    	add(westPanel, westData);
    	add(centerPanel, centerData);
    	
    	
        /*
        passwordTable = new FlexTable();
        flexTable.setWidget(1, 1, passwordTable);
        passwordTable.setWidth("100%");
        flexTable.getCellFormatter().setWordWrap(1, 1, false);
        flexTable.getCellFormatter().setHorizontalAlignment(1, 1, HasHorizontalAlignment.ALIGN_CENTER);
        flexTable.getCellFormatter().setVerticalAlignment(1, 1, HasVerticalAlignment.ALIGN_TOP);
        */
        

//        initTable();
//        initTags();
    }

    /**
     * 
     */
    /*
    private void initTags()
    {
        doLoadTags();
        rootTreeItem.setState(true);
    }*/

    /**
     * @param item
     */
    /*
    protected void doTagExpanded(TreeItem item)
    {
        if (item.equals(rootTreeItem) && item.getState())
        {
            doLoadTags();
        }
    }
    */

    /**
     * 
     */
    private void doLoadTags()
    {
        AsyncCallback<List<Tag>> callback = new AsyncCallback<List<Tag>>()
        {
            public void onFailure(Throwable caught)
            {
                MessageBox.alert("Error", caught.getMessage(), null);
            }
            public void onSuccess(List<Tag> result)
            {
                tags = result;
//                refreshTags();
            }
        };
        PasswordService.Util.getInstance().getAvailableTags(callback);
    }
    
    private void doLoadPasswordDialog(long passwordId)
    {
        AsyncCallback<Password> callback = new AsyncCallback<Password>()
        {
            public void onFailure(Throwable caught)
            {
                MessageBox.alert("Error", caught.getMessage(), null);
            }
            public void onSuccess(Password result)
            {
                if (null != result)
                {
                    new PasswordDialog(result).show();
                }
                else
                {
                    MessageBox.alert("Error", "You don't have access to read that password!", null);
                }
            }
        };
        PasswordService.Util.getInstance().getPassword(passwordId, callback);
    }
    
    /**
     * 
     */
    /*
    private void refreshTags()
    {
        rootTreeItem.removeItems();
        for (TagDTO tag : tags)
        {
            rootTreeItem.addItem(tag.getName());
        }
    }
*/
    /**
     * @param item
     */
    /*
    protected void doTagClicked(TreeItem item)
    {
        if (item.equals(rootTreeItem) && (0 == item.getChildCount()))
        {
            doLoadTags();
        }
    }
*/
    
    /**
     * 
     */
    protected void doSearch()
    {
        AsyncCallback<List<Password>> callback = new AsyncCallback<List<Password>>()
        {
            public void onFailure(Throwable caught)
            {
                MessageBox.alert("Error", caught.getMessage(), null);
            }
            public void onSuccess(List<Password> result)
            {
                refreshTable(result);
            }
        };
        PasswordService.Util.getInstance().searchPassword(searchTextBox.getValue(), activeOnlyCheckBox.getValue(), callback);
    }

    /**
     * @param data
     */
    private void refreshTable(List<Password> passwords)
    {
    	store.removeAll();
        for (Password password : passwords)
        {
        	store.add(new PasswordSearchData(password.getId(), password.getName(), password.getUsername(), password.getTagsAsString(), password.getNotes()));
        }
    }

    private class PasswordSearchData extends BaseModel
    {
		private static final long serialVersionUID = 1L;

    	public PasswordSearchData(long id, String name, String username, String tags, String notes)
    	{
    		set("id", id);
    		set("name", name);
    		set("username", username);
    		set("password", "******");
            set("tags", tags);
            set("notes", notes);
    	}
    }

    private void doShowPasswordPopup(long passwordId)
    {
        AsyncCallback<String> callback = new AsyncCallback<String>()
        {
            public void onFailure(Throwable caught)
            {
                MessageBox.alert("Error", caught.getMessage(), null);
            }
            public void onSuccess(String result)
            {
                Dialog popup = new Dialog();
                popup.setHeading("Current Password");
                popup.setButtons(Dialog.CLOSE);
                popup.addText(result);
                popup.setScrollMode(Scroll.AUTO);
                popup.setHideOnButtonClick(true);
                popup.show();
            }
        };
        PasswordService.Util.getInstance().getCurrentPassword(passwordId, callback);
    }
}
