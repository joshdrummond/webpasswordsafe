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
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.joshdrummond.webpasswordsafe.client.model.ClientModel;
import com.joshdrummond.webpasswordsafe.client.model.common.*;
import com.joshdrummond.webpasswordsafe.client.ui.*;


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
            headerGwtLabel.setWidth("216px");

            notLoggedInLabel = new Label(NOT_LOGGED_IN);
            rootPanel.add(notLoggedInLabel, 310, 12);
            notLoggedInLabel.setStyleName("gwt-Label-LoggedInUser");
            notLoggedInLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
            notLoggedInLabel.setWidth("330px");

            final MenuBar menuBar = new MenuBar();
            menuBar.setAutoOpen(true);
            rootPanel.add(menuBar, 8, 33);
            menuBar.setWidth("632px");

            final MenuBar menuBar_2 = new MenuBar(true);

            menuBar_2.addItem("Login", new Command() {
                public void execute() {
                    doLogin();
                }
            });

            final MenuBar menuBar_1 = new MenuBar(true);

            menuBar_1.addItem("General", (Command)null);

            menuBar_1.addItem("Change Password", (Command)null);

            menuBar_2.addItem("Settings", menuBar_1);

            final MenuBar menuBar_13 = new MenuBar(true);

            menuBar_13.addItem("User", (Command)null);

            menuBar_13.addItem("Admin", (Command)null);

            menuBar_2.addItem("Role", menuBar_13);

            menuBar_2.addItem("Logout", new Command() {
                public void execute() {
                    doLogout();
                }
            });
//            menuItemLogout.setVisible(false);

            menuBar.addItem("User", menuBar_2);

            final MenuBar menuBar_3 = new MenuBar(true);

            menuBar_3.addItem("Help", (Command)null);

            menuBar_3.addItem("About", (Command)null);

            final MenuBar menuBar_5 = new MenuBar(true);

            menuBar_5.addItem("User Audit", (Command)null);

            menuBar_5.addItem("Permissions", (Command)null);

            final MenuBar menuBar_6 = new MenuBar(true);

            menuBar_6.addItem("New", new Command() {
                public void execute() {
                    doNewPassword();
                }
            });

            final MenuBar menuBar_7 = new MenuBar(true);

            menuBar_7.addItem("New", (Command)null);

            menuBar_7.addItem("Edit", (Command)null);

            menuBar_6.addItem("Template", menuBar_7);

            menuBar_6.addItem("Search", (Command)null);

            menuBar.addItem("Password", menuBar_6);

            menuBar.addItem("Reports", menuBar_5);

            final MenuBar menuBar_4 = new MenuBar(true);

            menuBar_4.addItem("Settings", (Command)null);

            final MenuBar menuBar_8 = new MenuBar(true);

            final MenuBar menuBar_9 = new MenuBar(true);

            menuBar_9.addItem("Add", new Command() {
                public void execute() {
                    doAddGroup();
                }
            });

            menuBar_9.addItem("Edit", new Command() {
                public void execute() {
                    doEditGroup();
                }
            });

            final MenuBar menuBar_10 = new MenuBar(true);

            menuBar_10.addItem("Add", new Command() {
                public void execute() {
                    doAddUser();
                }
            });

            menuBar_10.addItem("Edit", new Command() {
                public void execute() {
                    doEditUser();
                }
            });

            menuBar_4.addItem("Users", menuBar_10);

            menuBar_4.addItem("Groups", menuBar_9);

            menuBar_4.addItem("Roles", menuBar_8);

            menuBar.addItem("Admin", menuBar_4);

            menuBar.addItem("About", menuBar_3);
        }

        simplePanel = new SimplePanel();
        rootPanel.add(simplePanel, 10, 62);
        simplePanel.setSize("630px", "418px");

    }

    /**
     * 
     */
    protected void doNewPassword()
    {
        if (clientModel.isAuthorized("NEW_PASSWORD"))
        {
            displayPasswordDialog(new PasswordDTO());
        }
    }

    /**
     * @param passwordDTO
     */
    private void displayPasswordDialog(PasswordDTO passwordDTO)
    {
        new PasswordDialog(passwordDTO).show();
    }

    /**
     * 
     */
    protected void doEditGroup()
    {
        if (clientModel.isAuthorized("EDIT_GROUP"))
        {
            // for testing...
            GroupDTO group = new GroupDTO();
            group.setId(1);
            group.setName("Everyone");
            group.addUser(new UserDTO(1, "obama", "Barak Obama", "b@obama.net", true));
            group.addUser(new UserDTO(2, "mccain", "John McCain", "j@mccain.net", true));
            displayGroupDialog(group);
        }
    }

    /**
     * 
     */
    protected void doAddGroup()
    {
        if (clientModel.isAuthorized("ADD_GROUP"))
        {
            displayGroupDialog(new GroupDTO());
        }
    }

    /**
     * @param groupDTO
     */
    private void displayGroupDialog(GroupDTO group)
    {
        new GroupDialog(group).show();
    }

    public void refreshLoginStatus()
    {
        if (clientModel.isLoggedIn())
        {
            notLoggedInLabel.setText(LOGGED_IN+clientModel.getLoggedInUser().getUsername());
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

    private void doAddUser()
    {
        if (clientModel.isAuthorized("ADD_USER"))
        {
            displayUserDialog(new UserDTO());
        }
    }
    
    private void doEditUser()
    {
        if (clientModel.isAuthorized("EDIT_USER"))
        {
            UserDTO editUser = new UserDTO();
            editUser.setActive(true);
            editUser.setUsername("josh");
            editUser.setEmail("josh@test.com");
            editUser.setFullname("Josh Drummond");
            editUser.setId(1);
            displayUserDialog(editUser);
        }
    }
    
    private void doLogout()
    {
        clientModel.getLoggedInUser().setUsername("");
        clientModel.setLoggedIn(false);
        refreshLoginStatus();
    }

//    private void doOpenProject()
//    {
//        if (clientModel.isLoggedIn())
//        {
//            displayOpenProjectDialog();
//        }
//        else
//        {
//            Window.alert("Must be logged in first.");
//        }
//    }

//    private void doCloseProject()
//    {
////        clientModel.setProjectName("");
////        refreshProjectPanel();
//    }
    
    private void displayLoginDialog()
    {
        new LoginDialog(this).show();
    }
    
    private void displayUserDialog(UserDTO user)
    {
        new UserDialog(user).show();
    }
//    private void displayOpenProjectDialog()
//    {
////        new OpenProjectDialog(this).show();
//    }
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