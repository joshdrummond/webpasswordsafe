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
import java.util.Arrays;
import java.util.List;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.joshdrummond.webpasswordsafe.common.model.AccessLevel;
import com.joshdrummond.webpasswordsafe.common.model.Password;
import com.joshdrummond.webpasswordsafe.common.model.Permission;
import com.joshdrummond.webpasswordsafe.common.model.Subject;


/**
 * @author Josh Drummond
 *
 */
public class PermissionDialog extends Window
{
    private Password password;
//    private List<Subject> subjects;
//    private Text passwordNameLabel;
    private ListStore<PermissionData> store;
//    private Grid permissionsGrid;
//    private ListBox subjectListBox;
    
    public PermissionDialog(PermissionListener permissionListener, Password password, List<Subject> subjects)
    {
        this.password = password;
//        this.subjects = subjects;
        this.setHeading("Permissions");
        this.setModal(true);
        
        for (Permission permission : password.getPermissions())
        {
            Info.display(permission.getId()+"", permission.getSubject().getName()+":"+permission.getAccessLevelObj().getName()+":"+permission.getAccessLevel());
        }
        
        ContentPanel panel = new ContentPanel();
        panel.setLayout(new RowLayout(Orientation.VERTICAL));
        panel.setHeaderVisible(false);
        panel.setFrame(true);
        
//        Text passwordLabel = new Text("Password");
//        passwordNameLabel = new Text("Password Name");
//        Text permissionsLabel = new Text("Permissions");
        
        store = new ListStore<PermissionData>();
        List<ColumnConfig> config = new ArrayList<ColumnConfig>(2);
        ColumnConfig column = new ColumnConfig();
        column.setId("subject");
        column.setHeader("User/Group");
        column.setWidth(150);
        config.add(column);
        
        final SimpleComboBox<AccessLevel> accessLevelCombo = new SimpleComboBox<AccessLevel>();
        accessLevelCombo.setEditable(false);
        accessLevelCombo.add(Arrays.asList(AccessLevel.values()));
        CellEditor accessLevelEditor = new CellEditor(accessLevelCombo) {
            public Object preProcessValue(Object v)
            {
                if (v instanceof AccessLevel) {
                    return accessLevelCombo.findModel((AccessLevel)v);
                }
                return AccessLevel.NONE;
            }
            @SuppressWarnings("unchecked")
            public Object postProcessValue(Object v)
            {
                return ((SimpleComboValue<AccessLevel>)v).get("value");
            }
        };
        
        column = new ColumnConfig();
        column.setId("accessLevel");
        column.setHeader("Access Level");
        column.setWidth(75);
        column.setEditor(accessLevelEditor);
        config.add(column);
        
        ColumnModel cm = new ColumnModel(config);
        EditorGrid<PermissionData> grid = new EditorGrid<PermissionData>(store, cm);
        grid.setBorders(true);
        grid.setStripeRows(true);
        grid.getSelectionModel().setSelectionMode(SelectionMode.MULTI);
        grid.setSize(200, 200);
        panel.add(grid, new RowData(1, .5, new Margins(4)));
        

        
        
//        passwordGrid.setStyleAttribute("borderTop", "none");
        
/*
        subjectListBox = new ListBox();
        horizontalPanel.add(subjectListBox);
        horizontalPanel.setCellHorizontalAlignment(subjectListBox, HasHorizontalAlignment.ALIGN_LEFT);

        final Button addButton = new Button();
        horizontalPanel.add(addButton);
        addButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event)
			{
                doAddSubject();
			}
        });
        addButton.setText("Add");

        final ScrollPanel scrollPanel = new ScrollPanel();
        flexTable.setWidget(2, 0, scrollPanel);
        flexTable.getCellFormatter().setHeight(2, 0, "200");
        flexTable.getCellFormatter().setWidth(2, 0, "400");
        flexTable.getFlexCellFormatter().setColSpan(2, 0, 2);

        permissionsGrid = new Grid();
        scrollPanel.setWidget(permissionsGrid);
        permissionsGrid.resize(2, 3);
        permissionsGrid.setSize("100%", "100%");

        final FlowPanel flowPanel = new FlowPanel();
        flexTable.setWidget(3, 0, flowPanel);
        flexTable.getCellFormatter().setHorizontalAlignment(3, 0, HasHorizontalAlignment.ALIGN_CENTER);
        flexTable.getFlexCellFormatter().setColSpan(3, 0, 2);
*/
        ListStore<SubjectData> store = new ListStore<SubjectData>();
        for (Subject subject : subjects)
        {
            store.add(new SubjectData(subject));
        }

        ComboBox<SubjectData> comboSubjects = new ComboBox<SubjectData>();
        comboSubjects.setEmptyText("Select a User/Group...");
        comboSubjects.setDisplayField("name");
        comboSubjects.setStore(store);
        comboSubjects.setTypeAhead(true);
        comboSubjects.setTriggerAction(TriggerAction.ALL);
        
        Button addUserButton = new Button("Add", new SelectionListener<ButtonEvent>()
        {
            @Override
            public void componentSelected(ButtonEvent ce)
            {
                doAddUser();
            }
        });

//        Button addGroupButton = new Button("Add Group", new SelectionListener<ButtonEvent>()
//        {
//            @Override
//            public void componentSelected(ButtonEvent ce)
//            {
//                doAddGroup();
//            }
//        });

        Button removeButton = new Button("Remove", new SelectionListener<ButtonEvent>()
        {
            @Override
            public void componentSelected(ButtonEvent ce)
            {
                doRemove();
            }
        });

        Button okayButton = new Button("Okay", new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
                doOkay();
			}
		});

        Button cancelButton = new Button("Cancel", new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
                doCancel();
			}
		});
        
        ContentPanel addPanel = new ContentPanel();
        addPanel.setLayout(new RowLayout(Orientation.VERTICAL));
        addPanel.setHeaderVisible(false);
        addPanel.setFrame(false);
        addPanel.add(comboSubjects);
        addPanel.add(addUserButton);
        
        panel.add(addPanel, new RowData(1, -1, new Margins(4)));
        panel.add(removeButton, new RowData(1, -1, new Margins(4)));
        panel.add(okayButton, new RowData(1, -1, new Margins(4)));
        panel.add(cancelButton, new RowData(1, -1, new Margins(4)));
        
        setFields();
        
        this.add(panel);
    }

    /**
     * 
     */
    protected void doRemove()
    {
        
    }

    /**
     * 
     */
    protected void doAddUser()
    {
        
    }
    
//    /**
//     * 
//     */
//    protected void doAddGroup()
//    {
//        
//    }

    /**
     * 
     */
    private void setFields()
    {
//        passwordNameLabel.setText(password.getName());
        store.removeAll();
        for (Permission permission : password.getPermissions())
        {
            store.add(new PermissionData(permission));
        }
    }

    /**
     * 
     */
    protected void doCancel()
    {
//        store.rejectChanges();
        hide();
    }

    /**
     * 
     */
    protected void doOkay()
    {
//        store.commitChanges();
    }

    /**
     * 
     */
    protected void doAddSubject()
    {
        
    }

    private class PermissionData extends BaseModel
    {
        private static final long serialVersionUID = 1L;
        private Permission permission;

        public Permission getPermission() { return permission; }
        public PermissionData(Permission permission)
        {
            set("id", permission.getId());
            set("subject", permission.getSubject().getName());
            set("accessLevel", permission.getAccessLevelObj());
        }
    }
    
    private class SubjectData extends BaseModel
    {
        private static final long serialVersionUID = 1L;
        public SubjectData(Subject subject)
        {
            set("id", subject.getId());
            set("name", subject.getName());
            set("subject", subject);
        }
    }
}
