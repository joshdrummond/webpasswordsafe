/*
    Copyright 2008-2010 Josh Drummond

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
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.MessageBox;
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
import com.joshdrummond.webpasswordsafe.client.remote.PasswordService;
import com.joshdrummond.webpasswordsafe.common.model.AccessLevel;
import com.joshdrummond.webpasswordsafe.common.model.Password;
import com.joshdrummond.webpasswordsafe.common.model.Permission;
import com.joshdrummond.webpasswordsafe.common.model.Subject;
import com.joshdrummond.webpasswordsafe.common.model.Template;
import com.joshdrummond.webpasswordsafe.common.model.TemplateDetail;
import com.extjs.gxt.ui.client.widget.layout.AbsoluteLayout;
import com.extjs.gxt.ui.client.widget.layout.AbsoluteData;
import com.google.gwt.user.client.rpc.AsyncCallback;


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
        this.setSize("380", "350");
        this.setResizable(false);
        this.password = password;
        this.permissionListener = permissionListener;
        this.setHeading("Permissions");
        this.setModal(true);
        this.setLayout(new AbsoluteLayout());
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

        List<ColumnConfig> config = new ArrayList<ColumnConfig>(2);
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
        ColumnModel cm = new ColumnModel(config);
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
        permissionGrid.setSize(200, 200);
        add(permissionGrid, new AbsoluteData(3, 3));
        permissionGrid.setSize("360px", "221px");

        Button removeButton = new Button("Remove Selected",
                new SelectionListener<ButtonEvent>()
                {
                    @Override
                    public void componentSelected(ButtonEvent ce)
                    {
                        doRemove();
                    }
                });
        add(removeButton, new AbsoluteData(258, 230));
        removeButton.setSize("105px", "22px");

        Button addUserButton = new Button("Add",
                new SelectionListener<ButtonEvent>()
                {
                    @Override
                    public void componentSelected(ButtonEvent ce)
                    {
                        doAdd();
                    }
                });
        add(addUserButton, new AbsoluteData(160, 230));
        addUserButton.setSize("51px", "22px");

        comboSubjects = new ComboBox<SubjectData>();
        add(comboSubjects, new AbsoluteData(3, 230));
        comboSubjects.setEmptyText("Select a User/Group...");
        comboSubjects.setDisplayField("name");
        comboSubjects.setStore(subjectStore);
        comboSubjects.setTypeAhead(true);
        comboSubjects.setTriggerAction(TriggerAction.ALL);

        Button btnRemoveAll = new Button("Remove All");
        btnRemoveAll.addSelectionListener(new SelectionListener<ButtonEvent>()
        {
            @Override
            public void componentSelected(ButtonEvent ce)
            {
                doRemoveAll();
            }
        });
        add(btnRemoveAll, new AbsoluteData(258, 254));
        btnRemoveAll.setSize("105px", "22px");
        
        Button btnAddTemplate = new Button("Add Template",
                new SelectionListener<ButtonEvent>()
                {
                    @Override
                    public void componentSelected(ButtonEvent ce)
                    {
                        doAddTemplate();
                    }
                });
        add(btnAddTemplate, new AbsoluteData(126, 254));
        btnAddTemplate.setSize("85px", "22px");

        Button okayButton = new Button("Okay",
                new SelectionListener<ButtonEvent>()
                {
                    @Override
                    public void componentSelected(ButtonEvent ce)
                    {
                        doOkay();
                    }
                });
        okayButton.setEnabled(password.getMaxEffectiveAccessLevel().equals(AccessLevel.GRANT));

        Button cancelButton = new Button("Cancel",
                new SelectionListener<ButtonEvent>()
                {
                    @Override
                    public void componentSelected(ButtonEvent ce)
                    {
                        doCancel();
                    }
                });
        setButtonAlign(HorizontalAlignment.CENTER);
        addButton(okayButton);
        addButton(cancelButton);
        
        setFields();
    }

    private void doAddTemplate()
    {
        AsyncCallback<List<Template>> callback = new AsyncCallback<List<Template>>()
        {
            public void onFailure(Throwable caught)
            {
                MessageBox.alert("Error", caught.getMessage(), null);
            }
            public void onSuccess(List<Template> result)
            {
                new TemplateSelectionDialog(new ApplyTemplateListener(), result, false).show();
            }
        };
        PasswordService.Util.getInstance().getTemplates(true, callback);

    }
    
    private void applyTemplateDetails(Template template)
    {
        for (TemplateDetail templateDetail : template.getTemplateDetails())
        {
            permissionStore.add(new PermissionData(new Permission(templateDetail.getSubject(), templateDetail.getAccessLevelObj())));
        }
    }
    
    private void doRemoveAll()
    {
        permissionStore.removeAll();
    }

    private void doRemove()
    {
        //PermissionData data = permissionGrid.getSelectionModel().getSelectedItem();
        PermissionData data = selectedPermission;
        if (null != data)
        {
            permissionStore.remove(data);
            selectedPermission = null;
        }
    }

    private void doAdd()
    {
        SubjectData data = comboSubjects.getValue();
        if (null != data)
        {
            permissionStore.add(new PermissionData(new Permission((Subject)data.get("subject"), AccessLevel.READ)));
        }
    }

    private void setFields()
    {
        permissionStore.removeAll();
        for (Permission permission : password.getPermissions())
        {
            permissionStore.add(new PermissionData(permission));
        }
    }

    private void doCancel()
    {
        permissionStore.rejectChanges();
        hide();
    }

    private void doOkay()
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
    
    private class ApplyTemplateListener implements TemplateListener
    {
        public void doTemplatesChosen(List<Template> templates)
        {
            if (templates.size() > 0)
            {
                AsyncCallback<Template> callback = new AsyncCallback<Template>()
                {
                    public void onFailure(Throwable caught)
                    {
                        MessageBox.alert("Error", caught.getMessage(), null);
                    }
                    public void onSuccess(Template result)
                    {
                        applyTemplateDetails(result);
                    }
                };
                PasswordService.Util.getInstance().getTemplateWithDetails(templates.get(0).getId(), callback);
            }
        }
    }
    
    private class PermissionData extends BaseModel
    {
        private static final long serialVersionUID = 1L;

        public PermissionData(Permission permission)
        {
            set("id", permission.getId());
            set("subject", Format.htmlEncode(permission.getSubject().getName()));
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
            set("name", Format.htmlEncode(subject.getName()));
            set("subject", subject);
        }
    }
}
