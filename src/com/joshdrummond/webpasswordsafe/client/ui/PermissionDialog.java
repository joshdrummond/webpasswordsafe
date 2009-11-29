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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
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
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.joshdrummond.webpasswordsafe.common.model.AccessLevel;
import com.joshdrummond.webpasswordsafe.common.model.Password;
import com.joshdrummond.webpasswordsafe.common.model.Permission;
import com.joshdrummond.webpasswordsafe.common.model.Subject;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.AbsoluteLayout;
import com.extjs.gxt.ui.client.widget.layout.AbsoluteData;


/**
 * @author Josh Drummond
 * 
 */
public class PermissionDialog extends Window
{
    private Password password;
    private ListStore<PermissionData> permissionStore;
    private EditorGrid<PermissionData> permissionGrid;
    private ComboBox<SubjectData> comboSubjects;
    private PermissionData selectedPermission;
    private PermissionListener permissionListener;

    public PermissionDialog(PermissionListener permissionListener,
            Password password, List<Subject> subjects)
    {
        setSize("390", "330");
        this.password = password;
        this.permissionListener = permissionListener;
        this.setHeading("Permissions");
        this.setModal(true);
        permissionStore = new ListStore<PermissionData>();

        final SimpleComboBox<AccessLevel> accessLevelCombo = new SimpleComboBox<AccessLevel>();
        accessLevelCombo.setForceSelection(true);
        accessLevelCombo.setEditable(false);
        accessLevelCombo.setTriggerAction(TriggerAction.ALL);
        accessLevelCombo.add(Arrays.asList(AccessLevel.values()));
        CellEditor accessLevelEditor = new CellEditor(accessLevelCombo)
        {
            public Object preProcessValue(Object v)
            {
                if (v instanceof AccessLevel)
                {
                    return accessLevelCombo.findModel((AccessLevel) v);
                }
                return null;
            }

            @SuppressWarnings("unchecked")
            public Object postProcessValue(Object v)
            {
                return ((SimpleComboValue<AccessLevel>) v).get("value");
            }
        };

        ListStore<SubjectData> subjectStore = new ListStore<SubjectData>();
        for (Subject subject : subjects)
        {
            subjectStore.add(new SubjectData(subject));
        }
        setLayout(new FitLayout());

        ContentPanel panel = new ContentPanel();
        List<ColumnConfig> config = new ArrayList<ColumnConfig>(2);
        ColumnModel cm = new ColumnModel(config);
        ColumnConfig column = new ColumnConfig();
        column.setId("subject");
        column.setHeader("User/Group");
        column.setWidth(216);
        config.add(column);
        column = new ColumnConfig();
        column.setId("accessLevel");
        column.setHeader("Access Level");
        column.setWidth(113);
        column.setEditor(accessLevelEditor);
        config.add(column);
        permissionGrid = new EditorGrid<PermissionData>(permissionStore, cm);
        permissionGrid.setBorders(true);
        permissionGrid.setStripeRows(true);
        GridSelectionModel<PermissionData> gsm = permissionGrid.getSelectionModel();
        gsm.setSelectionMode(SelectionMode.SINGLE);
        permissionGrid.addListener(Events.CellClick, new Listener<GridEvent<PermissionData>>()
        {
            public void handleEvent(GridEvent<PermissionData> ge)
            {
                selectedPermission = ge.getModel();
            }
        });
        panel.setLayout(new AbsoluteLayout());
        permissionGrid.setSize(200, 200);
        AbsoluteData absoluteData_1 = new AbsoluteData(-1, 6);
        absoluteData_1.setAnchorSpec("-5");
        panel.add(permissionGrid, absoluteData_1);
        permissionGrid.setHeight("202px");

        Button removeButton = new Button("Remove Selected",
                new SelectionListener<ButtonEvent>()
                {
                    @Override
                    public void componentSelected(ButtonEvent ce)
                    {
                        doRemove();
                    }
                });
        panel.add(removeButton, new AbsoluteData(255, 216));

        Button addUserButton = new Button("Add",
                new SelectionListener<ButtonEvent>()
                {
                    @Override
                    public void componentSelected(ButtonEvent ce)
                    {
                        doAdd();
                    }
                });
        AbsoluteData absoluteData = new AbsoluteData(154, 216);
        absoluteData.setAnchorSpec("-385");
        panel.add(addUserButton, absoluteData);

        comboSubjects = new ComboBox<SubjectData>();
        panel.add(comboSubjects, new AbsoluteData(0, 216));
        comboSubjects.setEmptyText("Select a User/Group...");
        comboSubjects.setDisplayField("name");
        comboSubjects.setStore(subjectStore);
        comboSubjects.setTypeAhead(true);
        comboSubjects.setTriggerAction(TriggerAction.ALL);
        setTopComponent(panel);
        panel.setSize("315", "300");
        panel.setHeaderVisible(false);
        panel.setFrame(true);

        Button okayButton = new Button("Okay",
                new SelectionListener<ButtonEvent>()
                {
                    @Override
                    public void componentSelected(ButtonEvent ce)
                    {
                        doOkay();
                    }
                });

        Button cancelButton = new Button("Cancel",
                new SelectionListener<ButtonEvent>()
                {
                    @Override
                    public void componentSelected(ButtonEvent ce)
                    {
                        doCancel();
                    }
                });
        cancelButton.setSize("66px", "22px");
        panel.add(okayButton, new AbsoluteData(98, 259));
        okayButton.setSize("66px", "22px");
        panel.add(cancelButton, new AbsoluteData(193, 259));

        setFields();
    }

    /**
     * 
     */
    protected void doRemove()
    {
        //PermissionData data = permissionGrid.getSelectionModel().getSelectedItem();
        PermissionData data = selectedPermission;
        if (null != data)
        {
            permissionStore.remove(data);
            selectedPermission = null;
        }
    }

    /**
     * 
     */
    protected void doAdd()
    {
        SubjectData data = comboSubjects.getValue();
        if (null != data)
        {
            permissionStore.add(new PermissionData(new Permission((Subject)data.get("subject"), AccessLevel.READ)));
        }
    }

    /**
     * 
     */
    private void setFields()
    {
        permissionStore.removeAll();
        for (Permission permission : password.getPermissions())
        {
            permissionStore.add(new PermissionData(permission));
        }
    }

    /**
     * 
     */
    protected void doCancel()
    {
        permissionStore.rejectChanges();
        hide();
    }

    /**
     * 
     */
    protected void doOkay()
    {
        permissionStore.commitChanges();
        Set<Permission> permissions = new HashSet<Permission>(permissionStore.getCount());
        for (PermissionData data : permissionStore.getModels())
        {
            Permission permission = (Permission)data.get("permission");
            String newAccessLevel = ((AccessLevel)data.get("accessLevel")).name();
            if (!newAccessLevel.equals(permission.getAccessLevel()))
            {
                // if user changed the access level value in the GUI, treat it like a new permission
                permission = new Permission(permission.getSubject(), AccessLevel.valueOf(newAccessLevel));
            }
            permissions.add(permission);
        }
        permissionListener.doPermissionsChanged(permissions);
        hide();
    }

    private class PermissionData extends BaseModel
    {
        private static final long serialVersionUID = 1L;

        public PermissionData(Permission permission)
        {
            set("id", permission.getId());
            set("subject", permission.getSubject().getName());
            set("accessLevel", permission.getAccessLevelObj());
            set("permission", permission);
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
