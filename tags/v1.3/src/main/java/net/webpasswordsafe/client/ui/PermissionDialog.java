/*
    Copyright 2008-2011 Josh Drummond

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.webpasswordsafe.client.WebPasswordSafe;
import net.webpasswordsafe.client.i18n.TextMessages;
import net.webpasswordsafe.client.remote.PasswordService;
import net.webpasswordsafe.common.model.AccessLevel;
import net.webpasswordsafe.common.model.Password;
import net.webpasswordsafe.common.model.Permission;
import net.webpasswordsafe.common.model.Subject;
import net.webpasswordsafe.common.model.Template;
import net.webpasswordsafe.common.model.TemplateDetail;
import net.webpasswordsafe.common.util.Constants;
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
import com.extjs.gxt.ui.client.widget.layout.AbsoluteLayout;
import com.extjs.gxt.ui.client.widget.layout.AbsoluteData;
import com.google.gwt.core.client.GWT;
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
    private final static TextMessages textMessages = GWT.create(TextMessages.class);

    public PermissionDialog(PermissionListener permissionListener,
            Password password, List<Subject> subjects)
    {
        this.setSize("380", "350");
        this.setResizable(false);
        this.password = password;
        this.permissionListener = permissionListener;
        boolean isPasswordGrantable = password.getMaxEffectiveAccessLevel().equals(AccessLevel.GRANT);
        
        this.setHeading(textMessages.permissions());
        this.setModal(true);
        this.setLayout(new AbsoluteLayout());
        permissionStore = new ListStore<PermissionData>();

        final SimpleComboBox<AccessLevel> accessLevelCombo = new SimpleComboBox<AccessLevel>();
        accessLevelCombo.setEnabled(isPasswordGrantable);
        accessLevelCombo.setForceSelection(true);
        accessLevelCombo.setEditable(false);
        accessLevelCombo.setTriggerAction(TriggerAction.ALL);
        accessLevelCombo.add(Arrays.asList(AccessLevel.values()));
        CellEditor accessLevelEditor = new CellEditor(accessLevelCombo)
        {
            @Override
            public Object preProcessValue(Object v)
            {
                if (v instanceof AccessLevel)
                {
                    return accessLevelCombo.findModel((AccessLevel) v);
                }
                return null;
            }

            @Override
            @SuppressWarnings("unchecked")
            public Object postProcessValue(Object v)
            {
                return ((SimpleComboValue<AccessLevel>) v).get(Constants.VALUE);
            }
        };

        ListStore<SubjectData> subjectStore = new ListStore<SubjectData>();
        for (Subject subject : subjects)
        {
            subjectStore.add(new SubjectData(subject));
        }

        List<ColumnConfig> config = new ArrayList<ColumnConfig>(2);
        ColumnConfig column = new ColumnConfig();
        column.setId(Constants.SUBJECT);
        column.setHeader(textMessages.userGroup());
        column.setWidth(216);
        config.add(column);
        column = new ColumnConfig();
        column.setId(Constants.ACCESSLEVEL);
        column.setHeader(textMessages.accessLevel());
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
            @Override
            public void handleEvent(GridEvent<PermissionData> ge)
            {
                selectedPermission = ge.getModel();
            }
        });
        permissionGrid.setSize(200, 200);
        add(permissionGrid, new AbsoluteData(3, 3));
        permissionGrid.setSize("360px", "221px");

        Button removeButton = new Button(textMessages.removeSelected(),
                new SelectionListener<ButtonEvent>()
                {
                    @Override
                    public void componentSelected(ButtonEvent ce)
                    {
                        doRemove();
                    }
                });
        removeButton.setEnabled(isPasswordGrantable);
        add(removeButton, new AbsoluteData(258, 230));
        removeButton.setSize("105px", "22px");

        Button addUserButton = new Button(textMessages.add(),
                new SelectionListener<ButtonEvent>()
                {
                    @Override
                    public void componentSelected(ButtonEvent ce)
                    {
                        doAdd();
                    }
                });
        addUserButton.setEnabled(isPasswordGrantable);
        add(addUserButton, new AbsoluteData(160, 230));
        addUserButton.setSize("51px", "22px");

        comboSubjects = new ComboBox<SubjectData>();
        add(comboSubjects, new AbsoluteData(3, 230));
        comboSubjects.setEmptyText(textMessages.selectUserGroup());
        comboSubjects.setDisplayField(Constants.NAME);
        comboSubjects.setStore(subjectStore);
        comboSubjects.setTypeAhead(true);
        comboSubjects.setTriggerAction(TriggerAction.ALL);
        comboSubjects.setEnabled(isPasswordGrantable);

        Button btnRemoveAll = new Button(textMessages.removeAll());
        btnRemoveAll.addSelectionListener(new SelectionListener<ButtonEvent>()
        {
            @Override
            public void componentSelected(ButtonEvent ce)
            {
                doRemoveAll();
            }
        });
        btnRemoveAll.setEnabled(isPasswordGrantable);
        add(btnRemoveAll, new AbsoluteData(258, 254));
        btnRemoveAll.setSize("105px", "22px");
        
        Button btnAddTemplate = new Button(textMessages.addTemplate(),
                new SelectionListener<ButtonEvent>()
                {
                    @Override
                    public void componentSelected(ButtonEvent ce)
                    {
                        doAddTemplate();
                    }
                });
        btnAddTemplate.setEnabled(isPasswordGrantable);
        add(btnAddTemplate, new AbsoluteData(126, 254));
        btnAddTemplate.setSize("85px", "22px");

        Button okayButton = new Button(textMessages.okay(),
                new SelectionListener<ButtonEvent>()
                {
                    @Override
                    public void componentSelected(ButtonEvent ce)
                    {
                        doOkay();
                    }
                });
        okayButton.setEnabled(isPasswordGrantable);

        Button cancelButton = new Button(textMessages.cancel(),
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
            @Override
            public void onFailure(Throwable caught)
            {
                WebPasswordSafe.handleServerFailure(caught);
            }
            @Override
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
            permissionStore.add(new PermissionData(new Permission((Subject)data.get(Constants.SUBJECT), AccessLevel.READ)));
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
        if (permissionStore.getCount() > 0)
        {
            Set<Permission> permissions = new HashSet<Permission>(permissionStore.getCount());
            for (PermissionData data : permissionStore.getModels())
            {
                Permission permission = (Permission)data.get(Constants.PERMISSION);
                String newAccessLevel = ((AccessLevel)data.get(Constants.ACCESSLEVEL)).name();
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
        else
        {
            MessageBox.alert(textMessages.error(), textMessages.mustHaveOnePermission(), null);
        }
    }
    
    private class ApplyTemplateListener implements TemplateListener
    {
        @Override
        public void doTemplatesChosen(List<Template> templates)
        {
            if (templates.size() > 0)
            {
                AsyncCallback<Template> callback = new AsyncCallback<Template>()
                {
                    @Override
                    public void onFailure(Throwable caught)
                    {
                        WebPasswordSafe.handleServerFailure(caught);
                    }
                    @Override
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
            set(Constants.ID, permission.getId());
            set(Constants.SUBJECT, Format.htmlEncode(permission.getSubject().getName()));
            set(Constants.ACCESSLEVEL, permission.getAccessLevelObj());
            set(Constants.PERMISSION, permission);
        }
    }

    private class SubjectData extends BaseModel
    {
        private static final long serialVersionUID = 1L;

        public SubjectData(Subject subject)
        {
            set(Constants.ID, subject.getId());
            set(Constants.NAME, Format.htmlEncode(subject.getName()));
            set(Constants.SUBJECT, subject);
        }
    }
}
