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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.AbsoluteData;
import com.extjs.gxt.ui.client.widget.layout.AbsoluteLayout;
import com.joshdrummond.webpasswordsafe.client.remote.PasswordService;
import com.joshdrummond.webpasswordsafe.client.remote.UserService;
import com.joshdrummond.webpasswordsafe.common.model.AccessLevel;
import com.joshdrummond.webpasswordsafe.common.model.Subject;
import com.joshdrummond.webpasswordsafe.common.model.Template;
import com.joshdrummond.webpasswordsafe.common.model.TemplateDetail;
import com.joshdrummond.webpasswordsafe.common.util.Utils;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * @author Josh Drummond
 *
 */
public class TemplateDialog extends Window
{
    private Template template;
    private TextField<String> templateNameTextBox;
    private ListStore<TemplateData> permissionStore;
    private EditorGrid<TemplateData> permissionGrid;
    private ComboBox<SubjectData> comboSubjects;
    private TemplateData selectedPermission;
    private CheckBox chkbxShared;
    private ListStore<SubjectData> subjectStore;
    
    public TemplateDialog(Template template)
    {
        this.template = template;
        setSize("380", "385");
        this.setResizable(false);
        this.setHeading("Template");
        this.setModal(true);
        this.setLayout(new AbsoluteLayout());
        permissionStore = new ListStore<TemplateData>();

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

        subjectStore = new ListStore<SubjectData>();

        LabelField lblfldTemplateName = new LabelField("Name:");
        add(lblfldTemplateName, new AbsoluteData(6, 6));
        templateNameTextBox = new TextField<String>();
        add(templateNameTextBox, new AbsoluteData(87, 6));
        templateNameTextBox.setSize("276px", "22px");

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
        permissionGrid = new EditorGrid<TemplateData>(permissionStore, cm);
        permissionGrid.setBorders(true);
        permissionGrid.setStripeRows(true);
        GridSelectionModel<TemplateData> gsm = permissionGrid.getSelectionModel();
        gsm.setSelectionMode(SelectionMode.SINGLE);
        permissionGrid.addListener(Events.CellClick, new Listener<GridEvent<TemplateData>>()
        {
            public void handleEvent(GridEvent<TemplateData> ge)
            {
                selectedPermission = ge.getModel();
            }
        });
        permissionGrid.setSize(200, 200);
        add(permissionGrid, new AbsoluteData(3, 33));
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
        add(removeButton, new AbsoluteData(258, 260));
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
        add(addUserButton, new AbsoluteData(160, 260));
        addUserButton.setSize("51px", "22px");

        comboSubjects = new ComboBox<SubjectData>();
        add(comboSubjects, new AbsoluteData(3, 260));
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
        add(btnRemoveAll, new AbsoluteData(258, 284));
        btnRemoveAll.setSize("105px", "22px");
        
        chkbxShared = new CheckBox();
        add(chkbxShared, new AbsoluteData(6, 284));
        chkbxShared.setBoxLabel("Shared?");
        chkbxShared.setHideLabel(true);
        
        Button saveButton = new Button("Save",
                new SelectionListener<ButtonEvent>()
                {
                    @Override
                    public void componentSelected(ButtonEvent ce)
                    {
                        doSave();
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
        setButtonAlign(HorizontalAlignment.CENTER);
        addButton(saveButton);
        addButton(cancelButton);
        
        setFields();
    }

    private void doRemoveAll()
    {
        permissionStore.removeAll();
    }

    private void doRemove()
    {
        TemplateData data = selectedPermission;
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
            permissionStore.add(new TemplateData(new TemplateDetail((Subject)data.get("subject"), AccessLevel.READ)));
        }
    }

    private void setFields()
    {
        templateNameTextBox.setValue(template.getName());
        chkbxShared.setValue(template.isShare());
        updateTemplateDetails();
        updateSubjects();
    }

    private void updateTemplateDetails()
    {
        permissionStore.removeAll();
        for (TemplateDetail templateDetail : template.getTemplateDetails())
        {
            permissionStore.add(new TemplateData(templateDetail));
        }  
    }
    
    private void updateSubjects()
    {
        AsyncCallback<List<Subject>> callback = new AsyncCallback<List<Subject>>()
        {
            public void onFailure(Throwable caught)
            {
                MessageBox.alert("Error", caught.getMessage(), null);
            }

            public void onSuccess(List<Subject> result)
            {
                for (Subject subject : result)
                {
                    subjectStore.add(new SubjectData(subject));
                }
            }
        };
        UserService.Util.getInstance().getSubjects(true, callback);
    }

    private void doCancel()
    {
        permissionStore.rejectChanges();
        hide();
    }

    private boolean validateFields()
    {
        if ("".equals(Utils.safeString(templateNameTextBox.getValue())))
        {
            MessageBox.alert("Error", "Name cannot be empty", null);
            return false;
        }
        return true;
    }
    
    private void doSave()
    {
        permissionStore.commitChanges();
        if (validateFields())
        {
            template.setName(Utils.safeString(templateNameTextBox.getValue()));
            template.setShare(chkbxShared.getValue());
            template.clearDetails();
            for (TemplateData data : permissionStore.getModels())
            {
                TemplateDetail templateDetail = (TemplateDetail)data.get("templateDetail");
                String newAccessLevel = ((AccessLevel)data.get("accessLevel")).name();
                if (!newAccessLevel.equals(templateDetail.getAccessLevel()))
                {
                    // if user changed the access level value in the GUI, treat it like a new permission
                    templateDetail = new TemplateDetail(templateDetail.getSubject(), AccessLevel.valueOf(newAccessLevel));
                }
                template.addDetail(templateDetail);
                Info.display("adding template detail", templateDetail.toString());
            }
            Info.display("template detail size", ""+template.getTemplateDetails().size());

            AsyncCallback<Void> callback = new AsyncCallback<Void>()
            {
                public void onFailure(Throwable caught)
                {
                    MessageBox.alert("Error", caught.getMessage(), null);
                }

                public void onSuccess(Void result)
                {
                    hide();
                }
            };
            if (template.getId() == 0)
            {
                PasswordService.Util.getInstance().addTemplate(template, callback);
            }
            else
            {
                PasswordService.Util.getInstance().updateTemplate(template, callback);
            }
        }
    }

    private class TemplateData extends BaseModel
    {
        private static final long serialVersionUID = 1L;

        public TemplateData(TemplateDetail templateDetail)
        {
            set("id", templateDetail.getId());
            set("subject", templateDetail.getSubject().getName());
            set("accessLevel", templateDetail.getAccessLevelObj());
            set("templateDetail", templateDetail);
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
