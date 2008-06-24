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

package com.joshdrummond.webpasswordsafe.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.joshdrummond.webpasswordsafe.client.model.ClientModel;
import com.joshdrummond.webpasswordsafe.client.ui.LoginDialog;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WebPasswordSafe implements EntryPoint, MainWindow {
    private ClientModel clientModel = new ClientModel();
    private Label notLoggedInLabel;
    private final static String NOT_LOGGED_IN = "Not Logged In";
    private final static String LOGGED_IN = "Logged In As: ";
    private final static String VERSION = "0.1";
    private final static String TITLE = "WebPasswordSafe v"+VERSION;
    private Label headerGwtLabel;
    private SimplePanel simplePanel;

    public void onModuleLoad() {
        final RootPanel rootPanel = RootPanel.get();
        rootPanel.setSize("800", "600");

        {
            headerGwtLabel = new Label(TITLE);
            rootPanel.add(headerGwtLabel, 10, 12);
            headerGwtLabel.setStyleName("gwt-Label-MainTitle");
            headerGwtLabel.setWidth("499px");

            notLoggedInLabel = new Label(NOT_LOGGED_IN);
            rootPanel.add(notLoggedInLabel, 551, 12);
            notLoggedInLabel.setStyleName("gwt-Label-LoggedInUser");
            notLoggedInLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
            notLoggedInLabel.setWidth("276px");

            final MenuBar menuBar = new MenuBar();
            rootPanel.add(menuBar, 8, 33);
            menuBar.setWidth("834px");

            final MenuBar menuBar_2 = new MenuBar(true);

            menuBar_2.addItem("Login", new Command() {
                public void execute() {
                    doLogin();
                }
            });

            menuBar_2.addItem("Logout", new Command() {
                public void execute() {
                    doLogout();
                }
            });
//            menuItemLogout.setVisible(false);

            menuBar.addItem("User", menuBar_2);

            final MenuBar menuBar_1 = new MenuBar(true);

            menuBar.addItem("Project", menuBar_1);

            menuBar_1.addItem("Open...", new Command() {
                public void execute() {
                    doOpenProject();
                }
            });

            menuBar_1.addItem("Close", new Command() {
                public void execute() {
                    doCloseProject();
                }
            });
        }

        simplePanel = new SimplePanel();
        rootPanel.add(simplePanel, 10, 62);
        simplePanel.setSize("832px", "545px");

    }

    public void refreshLoginStatus()
    {
        if (clientModel.isLoggedIn())
        {
            notLoggedInLabel.setText(LOGGED_IN+clientModel.getUserName());
        }
        else
        {
            notLoggedInLabel.setText(NOT_LOGGED_IN);
        }
    }
    
    public void refreshProjectStatus()
    {

    }

    private void doLogin()
    {
        displayLoginDialog();
    }

    private void doLogout()
    {
        clientModel.setUserName("");
        refreshLoginStatus();
    }

    private void doOpenProject()
    {
        if (clientModel.isLoggedIn())
        {
            displayOpenProjectDialog();
        }
        else
        {
            Window.alert("Must be logged in first.");
        }
    }

    private void doCloseProject()
    {
//        clientModel.setProjectName("");
//        refreshProjectPanel();
    }
    
    private void displayLoginDialog()
    {
        new LoginDialog(this).show();
    }
    private void displayOpenProjectDialog()
    {
//        new OpenProjectDialog(this).show();
    }
    public void refreshProjectPanel()
    {
//        refreshProjectStatus();
//        if (!clientModel.isProjectOpen())
//        {
//            if (simplePanel.getWidget() != null)
//            {
//                simplePanel.remove(simplePanel.getWidget());
//            }
//        }
//        else
//        {
//            if (simplePanel.getWidget() == null)
//            {
//                simplePanel.add(new ProjectPanel(this));
//            }
//            else
//            {
//                simplePanel.remove(simplePanel.getWidget());
//                simplePanel.add(new ProjectPanel(this));
//            }
//        }
    }
    
    public ClientModel getClientModel()
    {
        return clientModel;
    }
}