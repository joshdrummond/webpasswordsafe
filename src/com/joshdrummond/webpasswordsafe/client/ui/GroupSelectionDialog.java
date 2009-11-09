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
import java.util.List;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.ListField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.joshdrummond.webpasswordsafe.common.model.Group;


/**
 * @author Josh Drummond
 *
 */
public class GroupSelectionDialog extends Window
{

    private ListField<GroupData> groupListBox;
    private ListStore<GroupData> store;
    private GroupListener groupListener;
    private List<Group> groups;
    
    public GroupSelectionDialog(GroupListener groupListener, List<Group> groups, boolean allowMultiple)
    {
        this.setHeading("Groups");
        this.setModal(true);
        this.groupListener = groupListener;
        this.groups = groups;

        FormPanel form = new FormPanel();
        form.setHeaderVisible(false);
        form.setFrame(true);
        form.setLabelAlign(LabelAlign.TOP);
        form.setButtonAlign(HorizontalAlignment.CENTER);
        
        String selectLabelText = allowMultiple ? "Please select groups(s)" : "Please select a group";
        store = new ListStore<GroupData>();
        groupListBox = new ListField<GroupData>();
        groupListBox.setSize(300, 150);
        groupListBox.setDisplayField("name");
        groupListBox.setFieldLabel(selectLabelText);
        form.add(groupListBox);

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

    /**
     * 
     */
    private void setFields()
    {
        store.removeAll();
        for (Group group : groups)
        {
            store.add(new GroupData(group));
            Info.display("Group", group.getName());
        }
        groupListBox.setStore(store);
    }

    /**
     * 
     */
    protected void doCancel()
    {
        hide();
    }

    /**
     * 
     */
    protected void doOkay()
    {
        List<GroupData> dataSelected = groupListBox.getSelection();
        List<Group> groupsSelected = new ArrayList<Group>(dataSelected.size());
        for (GroupData gd : dataSelected)
        {
            groupsSelected.add((Group)gd.get("group"));
        }
        groupListener.doGroupsChosen(groupsSelected);
        hide();
    }

    private class GroupData extends BaseModel
    {
        private static final long serialVersionUID = 1L;
        public GroupData(Group group)
        {
            set("id", group.getId());
            set("name", group.getName());
            set("group", group);
        }
    }
}
