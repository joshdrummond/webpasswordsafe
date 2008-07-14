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

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;

/**
 * @author Josh Drummond
 *
 */
public class PasswordSearchPanel extends Composite
{

    public PasswordSearchPanel()
    {

        final FlexTable flexTable = new FlexTable();
        initWidget(flexTable);

        final ScrollPanel scrollPanel = new ScrollPanel();
        flexTable.setWidget(1, 0, scrollPanel);

        final Tree tree = new Tree();
        scrollPanel.setWidget(tree);
        tree.setSize("100%", "100%");

        final Grid grid = new Grid();
        flexTable.setWidget(1, 1, grid);
        grid.resize(1, 5);

        final HorizontalPanel horizontalPanel = new HorizontalPanel();
        flexTable.setWidget(0, 0, horizontalPanel);
        flexTable.getFlexCellFormatter().setColSpan(0, 0, 2);

        final Label passwordLabel = new Label("Password");
        horizontalPanel.add(passwordLabel);

        final TextBox textBox = new TextBox();
        horizontalPanel.add(textBox);
        textBox.setFocus(true);

        final Button searchButton = new Button();
        horizontalPanel.add(searchButton);
        searchButton.setText("Search");
    }

}
