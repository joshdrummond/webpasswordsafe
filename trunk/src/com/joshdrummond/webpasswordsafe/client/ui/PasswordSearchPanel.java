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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.joshdrummond.webpasswordsafe.client.MainWindow;
import com.joshdrummond.webpasswordsafe.client.model.common.PasswordDTO;
import com.joshdrummond.webpasswordsafe.client.model.common.TagDTO;
import com.joshdrummond.webpasswordsafe.client.remote.PasswordService;

/**
 * @author Josh Drummond
 *
 */
public class PasswordSearchPanel extends Composite
{
    private static final int VISIBLE_ROW_COUNT=10;
    private FlexTable passwordTable;
    private Tree tagTree;
    private TreeItem rootTreeItem;
    private TextBox searchTextBox;
    private List<TagDTO> tags;
    private List<PasswordDTO> passwords;
    private MainWindow mainWindow;

    public PasswordSearchPanel(MainWindow mainWindow)
    {
        this();
        this.mainWindow = mainWindow;
    }
    
    public PasswordSearchPanel()
    {
        tags = new ArrayList<TagDTO>();
        final FlexTable flexTable = new FlexTable();
        initWidget(flexTable);

        final ScrollPanel scrollPanel = new ScrollPanel();
        flexTable.setWidget(1, 0, scrollPanel);
        scrollPanel.setSize("100", "300");
        flexTable.getCellFormatter().setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_TOP);
        flexTable.getCellFormatter().setHeight(1, 0, "100%");
        flexTable.getCellFormatter().setWidth(1, 0, "25%");

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

        final HorizontalPanel horizontalPanel = new HorizontalPanel();
        flexTable.setWidget(0, 0, horizontalPanel);
        flexTable.getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
        flexTable.getCellFormatter().setHeight(0, 0, "15");
        horizontalPanel.setHeight("20%");
        horizontalPanel.setSpacing(10);
        flexTable.getFlexCellFormatter().setColSpan(0, 0, 2);
        flexTable.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);

        final Label passwordLabel = new Label("Password");
        horizontalPanel.add(passwordLabel);

        searchTextBox = new TextBox();
        horizontalPanel.add(searchTextBox);
        searchTextBox.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event)
			{
                if (event.getCharCode() == KeyCodes.KEY_ENTER)
                {
                    doSearch();
                }
			}
        });
        searchTextBox.setVisibleLength(30);
        searchTextBox.setMaxLength(1000);
        searchTextBox.setFocus(true);

        final Button searchButton = new Button();
        horizontalPanel.add(searchButton);
        searchButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event)
			{
                doSearch();
			}
        });
        searchButton.setText("Search");

        passwordTable = new FlexTable();
        flexTable.setWidget(1, 1, passwordTable);
        passwordTable.setWidth("100%");
        flexTable.getCellFormatter().setWordWrap(1, 1, false);
        flexTable.getCellFormatter().setHorizontalAlignment(1, 1, HasHorizontalAlignment.ALIGN_CENTER);
        flexTable.getCellFormatter().setVerticalAlignment(1, 1, HasVerticalAlignment.ALIGN_TOP);
        
        rootTreeItem = new TreeItem("<b>Tags</b>");
        tagTree.addItem(rootTreeItem);
        initTable();
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
    protected void doTagExpanded(TreeItem item)
    {
        if (item.equals(rootTreeItem) && item.getState())
        {
            doLoadTags();
        }
    }

    /**
     * 
     */
    private void doLoadTags()
    {
        AsyncCallback<List<TagDTO>> callback = new AsyncCallback<List<TagDTO>>()
        {
            public void onFailure(Throwable caught)
            {
                Window.alert("Error: "+caught.getMessage());
            }
            public void onSuccess(List<TagDTO> result)
            {
                tags = result;
                refreshTags();
            }
        };
        PasswordService.Util.getInstance().getAvailableTags(callback);
    }
    
    /**
     * 
     */
    private void refreshTags()
    {
        rootTreeItem.removeItems();
        for (TagDTO tag : tags)
        {
            rootTreeItem.addItem(tag.getName());
        }
    }

    /**
     * @param item
     */
    protected void doTagClicked(TreeItem item)
    {
        if (item.equals(rootTreeItem) && (0 == item.getChildCount()))
        {
            doLoadTags();
        }
    }

    /**
     * 
     */
    protected void doSearch()
    {
        AsyncCallback<List<PasswordDTO>> callback = new AsyncCallback<List<PasswordDTO>>()
        {
            public void onFailure(Throwable caught)
            {
                Window.alert("Error: "+caught.getMessage());
            }
            public void onSuccess(List<PasswordDTO> result)
            {
                passwords = result;
                refreshTable();
            }
        };
        PasswordService.Util.getInstance().searchPassword(searchTextBox.getText().trim(), callback);
    }

    /**
     * @param data
     */
    private void refreshTable()
    {
        for (int i = 0; i < passwords.size(); i++)
        {
            PasswordDTO passwordDTO = (PasswordDTO)passwords.get(i);
            passwordTable.setWidget(i+1, 0, new PasswordEditLabel(passwordDTO));
            passwordTable.setText(i+1, 1, passwordDTO.getUsername());
            passwordTable.setWidget(i+1, 2, new Button("View", new ViewPasswordClickHandler(passwordDTO.getId()))); //password popup
            passwordTable.setWidget(i+1, 3, new NotesLabel(passwordDTO.getNotes()));
        }
        for (int i = passwords.size(); i < VISIBLE_ROW_COUNT; i++)
        {
            passwordTable.setText(i + 1, 0, "");
            passwordTable.setText(i + 1, 1, "");
            passwordTable.setText(i + 1, 2, "");
            passwordTable.setText(i + 1, 3, "");
        }
    }

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

		/* (non-Javadoc)
		 * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
		 */
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

    /**
     * 
     */
    private void initTable()
    {
            // Create the header row.
        passwordTable.setText(0, 0, "Title");
        passwordTable.setText(0, 1, "Username");
        passwordTable.setText(0, 2, "Password");
        passwordTable.setText(0, 3, "Notes");
//        passwordTable.setWidget(0, 3, navBar);
//        passwordTable.getRowFormatter().setStyleName(0, "mail-ListHeader");

            // Initialize the rest of the rows.
            for (int i = 0; i < VISIBLE_ROW_COUNT; ++i) {
                passwordTable.setText(i + 1, 0, "");
                passwordTable.setText(i + 1, 1, "");
                passwordTable.setText(i + 1, 2, "");
                passwordTable.setText(i + 1, 3, "");
                passwordTable.getCellFormatter().setWordWrap(i + 1, 0, false);
                passwordTable.getCellFormatter().setWordWrap(i + 1, 1, false);
                passwordTable.getCellFormatter().setWordWrap(i + 1, 2, false);
                passwordTable.getCellFormatter().setWordWrap(i + 1, 3, false);
//                passwordTable.getFlexCellFormatter().setColSpan(i + 1, 2, 2);
            }
          
    }

}
