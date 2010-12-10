/*
    Copyright 2009-2010 Josh Drummond

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
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.ListField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.joshdrummond.webpasswordsafe.common.model.Template;


/**
 * @author Josh Drummond
 *
 */
public class TemplateSelectionDialog extends Window
{
    private ListField<TemplateData> templateListBox;
    private ListStore<TemplateData> store;
    private TemplateListener templateListener;
    private List<Template> templates;
    
    public TemplateSelectionDialog(TemplateListener templateListener, List<Template> templates, boolean allowMultiple)
    {
        this.setHeading("Templates");
        this.setModal(true);
        this.templateListener = templateListener;
        this.templates = templates;
        this.setResizable(false);

        FormPanel form = new FormPanel();
        form.setHeaderVisible(false);
        form.setFrame(true);
        form.setLabelAlign(LabelAlign.TOP);
        form.setButtonAlign(HorizontalAlignment.CENTER);
        
        String selectLabelText = allowMultiple ? "Please select template(s)" : "Please select a template";
        store = new ListStore<TemplateData>();
        templateListBox = new ListField<TemplateData>();
        templateListBox.setSize(300, 150);
        templateListBox.setDisplayField("name");
        templateListBox.setFieldLabel(selectLabelText);
        templateListBox.getListView().getSelectionModel().setSelectionMode(allowMultiple ? SelectionMode.MULTI : SelectionMode.SINGLE);
        templateListBox.getListView().addListener(Events.OnDoubleClick, new Listener<BaseEvent>()
        {
            public void handleEvent(BaseEvent be)
            {
                doOkay();
            }
        });
        form.add(templateListBox);

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
        
        form.setButtonAlign(HorizontalAlignment.CENTER);
        form.addButton(okayButton);
        form.addButton(cancelButton);

        setFields();
        
        this.add(form);
    }

    private void setFields()
    {
        store.removeAll();
        for (Template template : templates)
        {
            store.add(new TemplateData(template));
        }
        templateListBox.setStore(store);
    }

    private void doCancel()
    {
        hide();
    }

    private void doOkay()
    {
        List<TemplateData> dataSelected = templateListBox.getSelection();
        List<Template> templatesSelected = new ArrayList<Template>(dataSelected.size());
        for (TemplateData td : dataSelected)
        {
            templatesSelected.add((Template)td.get("template"));
        }
        templateListener.doTemplatesChosen(templatesSelected);
        hide();
    }

    private class TemplateData extends BaseModel
    {
        private static final long serialVersionUID = 1L;
        public TemplateData(Template template)
        {
            set("id", template.getId());
            set("name", Format.htmlEncode(template.getName()));
            set("template", template);
        }
    }
}
