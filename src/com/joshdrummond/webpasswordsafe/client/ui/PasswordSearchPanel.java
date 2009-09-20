/*
    Copyright 2008 Josh Drummond

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
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
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
import com.joshdrummond.webpasswordsafe.client.MainWindow;
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
    private List<Tag> tags;
//    private List<PasswordDTO> passwords;
    private MainWindow mainWindow;

    public PasswordSearchPanel(MainWindow mainWindow)
    {
        this();
        this.mainWindow = mainWindow;
    }
    
    public PasswordSearchPanel()
    {
    	setLayout(new BorderLayout());
    	setHeaderVisible(false);
    	
    	ContentPanel northPanel = new ContentPanel();
    	northPanel.setHeading("Password Search");
    	ContentPanel westPanel = new ContentPanel(new FillLayout());
    	ContentPanel centerPanel = new ContentPanel(new FillLayout());
    	
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
    	HBoxLayoutData northSearchData = new HBoxLayoutData(new Margins(0, 5, 0, 0));  
//    	northPanel.add(passwordLabel, northSearchData);  
    	northPanel.add(searchTextBox, northSearchData);  
    	northPanel.add(searchButton, northSearchData);  
        
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
        column.setId("title");
        column.setHeader("Title");
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
        PasswordService.Util.getInstance().searchPassword(searchTextBox.getValue(), callback);
    }

    /**
     * @param data
     */
    private void refreshTable(List<Password> passwords)
    {
    	store.removeAll();
        for (Password password : passwords)
        {
        	store.add(new PasswordSearchData(password.getId(), password.getName(), password.getUsername(), password.getNotes()));
//            passwordTable.setWidget(i+1, 0, new PasswordEditLabel(passwordDTO));
//            passwordTable.setText(i+1, 1, passwordDTO.getUsername());
//            passwordTable.setWidget(i+1, 2, new Button("View", new ViewPasswordClickHandler(passwordDTO.getId()))); //password popup
//            passwordTable.setWidget(i+1, 3, new NotesLabel(passwordDTO.getNotes()));
        }
    }

    private class PasswordSearchData extends BaseModel
    {
		private static final long serialVersionUID = 1L;

    	public PasswordSearchData(long id, String title, String username, String notes)
    	{
    		set("id", id);
    		set("title", title);
    		set("username", username);
    		set("password", "******");
    		set("notes", notes);
    	}
    }
    /*
    private class PasswordEditLabel extends Label
    {
        public PasswordEditLabel(final PasswordDTO password)
        {
            super();
            this.setText(password.getName());
            this.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event)
				{
                    mainWindow.displayPasswordDialog(password);
				}
            });
        }
    }
    private class NotesLabel extends Label
    {
        public NotesLabel(final String notes)
        {
            super();
            String shortNotes = (notes.length() > 10) ? notes.substring(0, 7) + "..." : notes;
            this.setText(shortNotes);
            this.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event)
                {
                    PopupPanel p = new PopupPanel(true);
                    VerticalPanel panel = new VerticalPanel();
                    panel.add(new Label("Notes:"));
                    panel.add(new Label(notes));
                    p.setWidget(panel);
                    p.setPopupPosition(event.getRelativeElement().getAbsoluteLeft(), event.getRelativeElement().getAbsoluteTop());
                    p.setStyleName("wps-NotesPopup");
                    p.show();
                }
            });
        }
    }

    private class ViewPasswordClickHandler
        implements ClickHandler
    {
        private long passwordId;
        
        public ViewPasswordClickHandler(long passwordId)
        {
            this.passwordId = passwordId;
        }

		public void onClick(ClickEvent event)
		{
            showPasswordPopup(passwordId, event.getRelativeElement().getAbsoluteLeft(), event.getRelativeElement().getAbsoluteTop());
		}
    }
    
    private void showPasswordPopup(long passwordId, final int x, final int y)
    {
        AsyncCallback<String> callback = new AsyncCallback<String>()
        {
            public void onFailure(Throwable caught)
            {
                Window.alert("Error: "+caught.getMessage());
            }
            public void onSuccess(String result)
            {
                String password = result;
                PopupPanel p = new PopupPanel(true);
                VerticalPanel panel = new VerticalPanel();
                panel.add(new Label("Current Password:"));
                panel.add(new Label(password));
                p.setWidget(panel);
                p.setPopupPosition(x, y);
                p.setStyleName("wps-CurrentPasswordPopup");
                p.show();
            }
        };
        PasswordService.Util.getInstance().getCurrentPassword(passwordId, callback);
    }
*/
}
