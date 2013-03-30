/*
    Copyright 2009-2013 Josh Drummond

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
import java.util.List;
import net.webpasswordsafe.client.ClientSessionUtil;
import net.webpasswordsafe.client.WebPasswordSafe;
import net.webpasswordsafe.client.i18n.TextMessages;
import net.webpasswordsafe.client.remote.PasswordService;
import net.webpasswordsafe.client.remote.UserService;
import net.webpasswordsafe.common.model.AccessLevel;
import net.webpasswordsafe.common.model.Subject;
import net.webpasswordsafe.common.model.Template;
import net.webpasswordsafe.common.model.TemplateDetail;
import net.webpasswordsafe.common.util.Constants;
import net.webpasswordsafe.common.util.Utils;
import net.webpasswordsafe.common.util.Constants.Function;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.Dialog;
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
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.google.gwt.core.client.GWT;
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
    private ClientSessionUtil clientSessionUtil = ClientSessionUtil.getInstance();
    private final static TextMessages textMessages = GWT.create(TextMessages.class);

    public TemplateDialog(Template template)
    {
        this.template = template;
        setSize("380", "385");
        this.setResizable(false);
        this.setHeading(textMessages.template());
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

        subjectStore = new ListStore<SubjectData>();

        LabelField lblfldTemplateName = new LabelField(textMessages.name_());
        add(lblfldTemplateName, new AbsoluteData(6, 6));
        templateNameTextBox = new TextField<String>();
        add(templateNameTextBox, new AbsoluteData(87, 6));
        templateNameTextBox.setSize("276px", "22px");

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
        permissionGrid = new EditorGrid<TemplateData>(permissionStore, cm);
        permissionGrid.setBorders(true);
        permissionGrid.setStripeRows(true);
        GridSelectionModel<TemplateData> gsm = permissionGrid.getSelectionModel();
        gsm.setSelectionMode(SelectionMode.SINGLE);
        permissionGrid.addListener(Events.CellClick, new Listener<GridEvent<TemplateData>>()
        {
            @Override
            public void handleEvent(GridEvent<TemplateData> ge)
            {
                selectedPermission = ge.getModel();
            }
        });
        permissionGrid.setSize(200, 200);
        add(permissionGrid, new AbsoluteData(3, 33));
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
        add(removeButton, new AbsoluteData(258, 260));
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
        add(addUserButton, new AbsoluteData(160, 260));
        addUserButton.setSize("51px", "22px");

        comboSubjects = new ComboBox<SubjectData>();
        add(comboSubjects, new AbsoluteData(3, 260));
        comboSubjects.setEmptyText(textMessages.selectUserGroup());
        comboSubjects.setDisplayField(Constants.NAME);
        comboSubjects.setStore(subjectStore);
        comboSubjects.setTypeAhead(true);
        comboSubjects.setTriggerAction(TriggerAction.ALL);

        Button btnRemoveAll = new Button(textMessages.removeAll());
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
        chkbxShared.setBoxLabel(textMessages.shared_());
        chkbxShared.setHideLabel(true);
        if (template.getId() != 0)
        {
            if (!clientSessionUtil.isAuthorized(Function.BYPASS_TEMPLATE_SHARING) &&
               (ClientSessionUtil.getInstance().getLoggedInUser().getId() != template.getUser().getId()))
            {
                chkbxShared.setReadOnly(true);
            }
        }
        
        Button saveButton = new Button(textMessages.save(),
                new SelectionListener<ButtonEvent>()
                {
                    @Override
                    public void componentSelected(ButtonEvent ce)
                    {
                        doSave();
                    }
                });

        Button deleteButton = new Button(textMessages.delete(),
                new SelectionListener<ButtonEvent>()
                {
                    @Override
                    public void componentSelected(ButtonEvent ce)
                    {
                        doVerifyDelete();
                    }
                });

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
        addButton(saveButton);
        if (template.getId() != 0)
        {
            if (clientSessionUtil.isAuthorized(Function.BYPASS_TEMPLATE_SHARING) ||
               (ClientSessionUtil.getInstance().getLoggedInUser().getId() == template.getUser().getId()))
            {
                addButton(deleteButton);
            }
        }
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
            permissionStore.add(new TemplateData(new TemplateDetail((Subject)data.get(Constants.SUBJECT), AccessLevel.READ)));
        }
    }

    private void setFields()
    {
        templateNameTextBox.setValue(template.getName());
        chkbxShared.setValue(template.isShared());
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
            @Override
            public void onFailure(Throwable caught)
            {
                WebPasswordSafe.handleServerFailure(caught);
            }
            @Override
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
            MessageBox.alert(textMessages.error(), textMessages.mustEnterName(), null);
            return false;
        }
        if (Utils.safeString(templateNameTextBox.getValue()).length() > Template.LENGTH_NAME)
        {
            MessageBox.alert(textMessages.error(), textMessages.tooLongName(), null);
            return false;
        }
        return true;
    }
    
    private void doVerifyDelete()
    {
        MessageBox.confirm(textMessages.confirmDelete(), textMessages.templateConfirmDelete(), new Listener<MessageBoxEvent>()
        {
            @Override
            public void handleEvent(MessageBoxEvent be)
            {
                if (be.getButtonClicked().getItemId().equals(Dialog.YES))
                {
                    doDelete();
                }
            }
        });
    }
    
    private void doDelete()
    {
        AsyncCallback<Void> callback = new AsyncCallback<Void>()
        {
            @Override
            public void onFailure(Throwable caught)
            {
                WebPasswordSafe.handleServerFailure(caught);
            }
            @Override
            public void onSuccess(Void result)
            {
                Info.display(textMessages.status(), textMessages.templateDeleted());
                hide();
            }
        };
        PasswordService.Util.getInstance().deleteTemplate(template, callback);
    }
    
    private void doSave()
    {
        permissionStore.commitChanges();
        if (validateFields())
        {
            template.setName(Utils.safeString(templateNameTextBox.getValue()));
            template.setShared(chkbxShared.getValue());
            template.clearDetails();
            for (TemplateData data : permissionStore.getModels())
            {
                TemplateDetail templateDetail = (TemplateDetail)data.get(Constants.TEMPLATEDETAIL);
                String newAccessLevel = ((AccessLevel)data.get(Constants.ACCESSLEVEL)).name();
                if (!newAccessLevel.equals(templateDetail.getAccessLevel()))
                {
                    // if user changed the access level value in the GUI, treat it like a new permission
                    templateDetail = new TemplateDetail(templateDetail.getSubject(), AccessLevel.valueOf(newAccessLevel));
                }
                template.addDetail(templateDetail);
            }

            final AsyncCallback<Boolean> callbackCheck = new AsyncCallback<Boolean>()
            {
                @Override
                public void onFailure(Throwable caught)
                {
                    WebPasswordSafe.handleServerFailure(caught);
                }
                @Override
                public void onSuccess(Boolean result)
                {
                    // true => template name already taken, else go ahead and save
                    if (result)
                    {
                        MessageBox.alert(textMessages.error(), textMessages.templateNameExists(), null);
                    }
                    else
                    {
                        AsyncCallback<Void> callback = new AsyncCallback<Void>()
                        {
                            @Override
                            public void onFailure(Throwable caught)
                            {
                                WebPasswordSafe.handleServerFailure(caught);
                            }
                            @Override
                            public void onSuccess(Void result)
                            {
                                Info.display(textMessages.status(), textMessages.templateSaved());
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
            };
            PasswordService.Util.getInstance().isTemplateTaken(template.getName(), template.getId(), callbackCheck);
        }
    }

    private class TemplateData extends BaseModel
    {
        private static final long serialVersionUID = 1L;

        public TemplateData(TemplateDetail templateDetail)
        {
            set(Constants.ID, templateDetail.getId());
            set(Constants.SUBJECT, Format.htmlEncode(templateDetail.getSubject().getName()));
            set(Constants.ACCESSLEVEL, templateDetail.getAccessLevelObj());
            set(Constants.TEMPLATEDETAIL, templateDetail);
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
