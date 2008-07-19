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
    private MenuBar userMenu;
    private MenuBar adminMenu;
    private PasswordSearchPanel passwordSearchPanel;

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

            final MenuBar mainMenu = new MenuBar();
            mainMenu.setAutoOpen(true);
            rootPanel.add(mainMenu, 8, 33);
            mainMenu.setWidth("632px");

            userMenu = new MenuBar(true);

            userMenu.addItem("Login", new Command() {
                public void execute() {
                    doLogin();
                }
            });

            final MenuBar userSettingsMenu = new MenuBar(true);

            userSettingsMenu.addItem("General", (Command)null);

            userSettingsMenu.addItem("Change Password", (Command)null);

            userMenu.addItem("Settings", userSettingsMenu);

            final MenuBar userRoleMenu = new MenuBar(true);

            userRoleMenu.addItem("User", (Command)null);

            userRoleMenu.addItem("Admin", (Command)null);

            userMenu.addItem("Role", userRoleMenu);

            userMenu.addItem("Logout", new Command() {
                public void execute() {
                    doLogout();
                }
            });
//            menuItemLogout.setVisible(false);

            mainMenu.addItem("User", userMenu);

            final MenuBar aboutMenu = new MenuBar(true);

            aboutMenu.addItem("Help", (Command)null);

            aboutMenu.addItem("About", (Command)null);

            final MenuBar reportsMenu = new MenuBar(true);

            reportsMenu.addItem("User Audit", (Command)null);

            reportsMenu.addItem("Permissions", (Command)null);

            final MenuBar passwordMenu = new MenuBar(true);

            passwordMenu.addItem("New", new Command() {
                public void execute() {
                    doNewPassword();
                }
            });

            final MenuBar passwordTemplateMenu = new MenuBar(true);

            passwordTemplateMenu.addItem("New", (Command)null);

            passwordTemplateMenu.addItem("Edit", (Command)null);

            passwordMenu.addItem("Template", passwordTemplateMenu);

            passwordMenu.addItem("Search", new Command() {
                public void execute() {
                    doPasswordSearch();
                }
            });

            mainMenu.addItem("Password", passwordMenu);

            mainMenu.addItem("Reports", reportsMenu);

            adminMenu = new MenuBar(true);

            adminMenu.addItem("Settings", (Command)null);

            final MenuBar adminRoleMenu = new MenuBar(true);

            final MenuBar adminGroupMenu = new MenuBar(true);

            adminGroupMenu.addItem("Add", new Command() {
                public void execute() {
                    doAddGroup();
                }
            });

            adminGroupMenu.addItem("Edit", new Command() {
                public void execute() {
                    doEditGroup();
                }
            });

            final MenuBar adminUserMenu = new MenuBar(true);

            adminUserMenu.addItem("Add", new Command() {
                public void execute() {
                    doAddUser();
                }
            });

            adminUserMenu.addItem("Edit", new Command() {
                public void execute() {
                    doEditUser();
                }
            });

            adminMenu.addItem("Users", adminUserMenu);

            adminMenu.addItem("Groups", adminGroupMenu);

            adminMenu.addItem("Roles", adminRoleMenu);

            mainMenu.addItem("Admin", adminMenu);

            mainMenu.addItem("About", aboutMenu);
        }

        simplePanel = new SimplePanel();
        rootPanel.add(simplePanel, 10, 62);
        simplePanel.setSize("630px", "418px");

        passwordSearchPanel = new PasswordSearchPanel();
        passwordSearchPanel.setSize("100%", "100%");

        refreshMenu();
    }

    /**
     * 
     */
    protected void doPasswordSearch()
    {
        simplePanel.setWidget(passwordSearchPanel);
    }

    private void refreshMenu()
    {
        boolean isLoggedIn = clientModel.isLoggedIn();
        adminMenu.setVisible(isLoggedIn);
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
            notLoggedInLabel.setText(LOGGED_IN+clientModel.getLoggedInUser().getFullname());
        }
        else
        {
            notLoggedInLabel.setText(NOT_LOGGED_IN);
        }
        refreshMenu();
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
        else
        {
          Window.alert("Must be logged in first.");
        }
    }
    
    private void doLogout()
    {
        clientModel.getLoggedInUser().setUsername("");
        clientModel.setLoggedIn(false);
        refreshLoginStatus();
    }
    
    private void displayLoginDialog()
    {
        new LoginDialog(this).show();
    }
    
    private void displayUserDialog(UserDTO user)
    {
        new UserDialog(user).show();
    }
    
    public ClientModel getClientModel()
    {
        return clientModel;
    }
}