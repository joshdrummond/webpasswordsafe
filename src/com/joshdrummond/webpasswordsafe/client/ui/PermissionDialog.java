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

import java.util.List;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.joshdrummond.webpasswordsafe.client.model.common.PasswordDTO;
import com.joshdrummond.webpasswordsafe.client.model.common.SubjectDTO;

/**
 * @author Josh Drummond
 *
 */
public class PermissionDialog extends DialogBox
{
    private PasswordDTO password;
    private List<SubjectDTO> subjects;
    private Label passwordNameLabel;
    private Grid permissionsGrid;
    private ListBox subjectListBox;
    
    public PermissionDialog(PermissionListener permissionListener, PasswordDTO password, List<SubjectDTO> subjects)
    {
        this.password = password;
        this.subjects = subjects;
        
        setHTML("Permissions");

        final FlexTable flexTable = new FlexTable();
        setWidget(flexTable);
        flexTable.setSize("100%", "100%");

        final Label passwordLabel = new Label("Password");
        flexTable.setWidget(0, 0, passwordLabel);

        passwordNameLabel = new Label("Password Name");
        flexTable.setWidget(0, 1, passwordNameLabel);

        final Label permissionsLabel = new Label("Permissions");
        flexTable.setWidget(1, 0, permissionsLabel);

        final HorizontalPanel horizontalPanel = new HorizontalPanel();
        flexTable.setWidget(1, 1, horizontalPanel);
        flexTable.getCellFormatter().setHorizontalAlignment(1, 1, HasHorizontalAlignment.ALIGN_RIGHT);
        horizontalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

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

        final Button okayButton = new Button();
        flowPanel.add(okayButton);
        okayButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event)
            {
                doOkay();
            }
        });
        okayButton.setText("Okay");

        final Button cancelButton = new Button();
        flowPanel.add(cancelButton);
        cancelButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event)
            {
                doCancel();
            }
        });
        cancelButton.setText("Cancel");
        
        setFields();
    }

    /**
     * 
     */
    private void setFields()
    {
        passwordNameLabel.setText(password.getName());
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
        // TODO Auto-generated method stub
        
    }

    /**
     * 
     */
    protected void doAddSubject()
    {
        // TODO Auto-generated method stub
        
    }

}
