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
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.joshdrummond.webpasswordsafe.client.model.common.PasswordDTO;
import com.joshdrummond.webpasswordsafe.client.model.common.SubjectDTO;

/**
 * @author Josh Drummond
 *
 */
public class PermissionDialog extends Window
{
    private PasswordDTO password;
    private List<SubjectDTO> subjects;
    private Text passwordNameLabel;
//    private Grid permissionsGrid;
//    private ListBox subjectListBox;
    
    public PermissionDialog(PermissionListener permissionListener, PasswordDTO password, List<SubjectDTO> subjects)
    {
        this.password = password;
        this.subjects = subjects;
        this.setHeading("Permissions");
        this.setModal(true);
        
        ContentPanel panel = new ContentPanel();
        panel.setHeaderVisible(false);
        panel.setFrame(true);
        
        Text passwordLabel = new Text("Password");
        passwordNameLabel = new Text("Password Name");
        Text permissionsLabel = new Text("Permissions");
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
        
        panel.add(okayButton);
        panel.add(cancelButton);
        
        setFields();
        
        this.add(panel);
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
