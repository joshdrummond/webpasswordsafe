/*
    Copyright 2008-2009 Josh Drummond

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

import java.util.List;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuBar;
import com.extjs.gxt.ui.client.widget.menu.MenuBarItem;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.joshdrummond.webpasswordsafe.client.remote.LoginService;
import com.joshdrummond.webpasswordsafe.client.remote.UserService;
import com.joshdrummond.webpasswordsafe.client.ui.*;
import com.joshdrummond.webpasswordsafe.common.model.AccessLevel;
import com.joshdrummond.webpasswordsafe.common.model.Group;
import com.joshdrummond.webpasswordsafe.common.model.Password;
import com.joshdrummond.webpasswordsafe.common.model.Permission;
import com.joshdrummond.webpasswordsafe.common.model.User;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * 
 * @author Josh Drummond
 * 
 */
public class WebPasswordSafe implements EntryPoint, MainWindow {
    private ClientSessionUtil clientSessionUtil = ClientSessionUtil.getInstance();
    private final static String NOT_LOGGED_IN = "Not Logged In";
    private final static String LOGGED_IN = "Logged In As: ";
    private final static String VERSION = "0.1";
    private final static String TITLE = "WebPasswordSafe v"+VERSION;
    private Viewport viewport; 
    private ContentPanel mainPanel, topPanel;
    private Menu userMenu;
    private Menu adminMenu;

    public void onModuleLoad() {
    	
        userMenu = new Menu();
        MenuItem userSettings = new MenuItem("Settings");
        Menu userSettingsMenu = new Menu();
        userSettingsMenu.add(new MenuItem("General"));
        userSettingsMenu.add(new MenuItem("Change Password", new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
                doChangePassword();
            }
        }));
        userSettings.setSubMenu(userSettingsMenu);
        userMenu.add(userSettings);

        MenuItem userRole = new MenuItem("Role");
        Menu userRoleMenu = new Menu();
        userRoleMenu.add(new MenuItem("User"));
        userRoleMenu.add(new MenuItem("Admin"));
        userRole.setSubMenu(userRoleMenu);
        userMenu.add(userRole);

        userMenu.add(new MenuItem("Logout", new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
                doLogout();
            }
        }));


        Menu aboutMenu = new Menu();
        aboutMenu.add(new MenuItem("Help"));
        aboutMenu.add(new MenuItem("About"));

        Menu reportsMenu = new Menu();
        reportsMenu.add(buildReportMenuItem("Users", "Users"));
        reportsMenu.add(buildReportMenuItem("Groups", "Groups"));
        reportsMenu.add(buildReportMenuItem("Access Audit", "PasswordAccessAudit"));
        reportsMenu.add(buildReportMenuItem("Permissions", "PasswordPermissions"));

        Menu passwordMenu = new Menu();
        passwordMenu.add(new MenuItem("New", new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
                doNewPassword();
            }
        }));
        MenuItem passwordTemplate = new MenuItem("Template");
        Menu passwordTemplateMenu = new Menu();
        passwordTemplateMenu.add(new MenuItem("New"));
        passwordTemplateMenu.add(new MenuItem("Edit"));
        passwordTemplate.setSubMenu(passwordTemplateMenu);
        passwordMenu.add(passwordTemplate);
        passwordMenu.add(new MenuItem("Search", new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
                refreshPasswordSearch();
            }
        }));

        adminMenu = new Menu();
        adminMenu.add(new MenuItem("Settings"));

        MenuItem adminUser = new MenuItem("Users");
        Menu adminUserMenu = new Menu();
        adminUserMenu.add(new MenuItem("Add", new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
                doAddUser();
            }
        }));
        adminUserMenu.add(new MenuItem("Edit", new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
                doEditUser();
            }
        }));
        adminUser.setSubMenu(adminUserMenu);
        adminMenu.add(adminUser);
        
        MenuItem adminGroup = new MenuItem("Groups");
        Menu adminGroupMenu = new Menu();
        adminGroupMenu.add(new MenuItem("Add", new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
                doAddGroup();
            }
        }));
        adminGroupMenu.add(new MenuItem("Edit", new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
                doEditGroup();
            }
        }));
        adminGroup.setSubMenu(adminGroupMenu);
        adminMenu.add(adminGroup);

        MenuItem adminRole = new MenuItem("Roles");
        adminMenu.add(adminRole);

        MenuBar mainMenu = new MenuBar();
        mainMenu.add(new MenuBarItem("User", userMenu));
        mainMenu.add(new MenuBarItem("Password", passwordMenu));
        mainMenu.add(new MenuBarItem("Reports", reportsMenu));
        mainMenu.add(new MenuBarItem("Admin", adminMenu));
        mainMenu.add(new MenuBarItem("About", aboutMenu));

        viewport = new Viewport();
        viewport.setLayout(new RowLayout(Orientation.VERTICAL));
        // title panel
        topPanel = new ContentPanel();
        HBoxLayout titleLayout = new HBoxLayout();
        titleLayout.setPadding(new Padding(5));
        titleLayout.setHBoxLayoutAlign(HBoxLayoutAlign.TOP);
        topPanel.setLayout(titleLayout);
        topPanel.setHeight(25);
        topPanel.setHeaderVisible(false);
        topPanel.setBorders(false);
        refreshTopPanel();
        viewport.add(topPanel, new RowData(1, -1));
        //menu panel
        ContentPanel menuPanel = new ContentPanel(new FillLayout());
        menuPanel.add(mainMenu);
        menuPanel.setHeaderVisible(false);
        menuPanel.setBorders(false);
        viewport.add(menuPanel, new RowData(1, -1));
        //main panel
        mainPanel = new ContentPanel(new FillLayout());
        mainPanel.setHeaderVisible(false);
        viewport.add(mainPanel, new RowData(1, 1));
        
        RootPanel.get().add(viewport);

        refreshMenu();
        
        doGetLoggedInUser();
    }

    private MenuItem buildReportMenuItem(String menuName, String reportName)
    {
        MenuItem menuItem = new MenuItem(menuName);
        Menu subMenu =  new Menu();
        subMenu.add(buildReportMenuItemType(reportName, "pdf"));
        subMenu.add(buildReportMenuItemType(reportName, "csv"));
        menuItem.setSubMenu(subMenu);
        return menuItem;
    }

    private MenuItem buildReportMenuItemType(final String name, final String type)
    {
        return new MenuItem(type.toUpperCase(), new SelectionListener<MenuEvent>()
        {
            @Override
            public void componentSelected(MenuEvent ce)
            {
                doShowReport(name, type.toLowerCase());
            }
        });
    }

    private void refreshTopPanel()
    {
    	topPanel.removeAll();
        Text headerGwtLabel = new Text(TITLE);
        Text loggedInLabel = null;
        if (clientSessionUtil.isLoggedIn())
        {
            loggedInLabel = new Text(LOGGED_IN+clientSessionUtil.getLoggedInUser().getFullname());
        }
        else
        {
            loggedInLabel = new Text(NOT_LOGGED_IN);
        }
        topPanel.add(headerGwtLabel, new HBoxLayoutData(0, 5, 0, 0));
        HBoxLayoutData flex = new HBoxLayoutData(new Margins(0, 5, 0, 0));
        flex.setFlex(1);
        topPanel.add(new Text(), flex);
        topPanel.add(loggedInLabel, new HBoxLayoutData(new Margins(0)));
        topPanel.layout();
    }
    
    private void doShowReport(String reportName, String reportType)
    {
        Window.open(GWT.getHostPageBaseURL()+"report?name="+reportName+"&type="+reportType, "_blank", "");
    }

    /**
     * 
     */
    protected void doChangePassword()
    {
        new ChangePasswordDialog().show();
    }

    /**
     * 
     */
    protected void refreshPasswordSearch()
    {
        mainPanel.removeAll();
        if (clientSessionUtil.isLoggedIn())
        {
            PasswordSearchPanel passwordSearchPanel = new PasswordSearchPanel();
            passwordSearchPanel.setSize("100%", "100%");
            mainPanel.add(passwordSearchPanel);
        }
        mainPanel.layout();
    }

    private void refreshMenu()
    {
        boolean isLoggedIn = clientSessionUtil.isLoggedIn();
        adminMenu.setVisible(isLoggedIn);
    }
    
    /**
     * 
     */
    protected void doNewPassword()
    {
        if (clientSessionUtil.isAuthorized("NEW_PASSWORD"))
        {
            Password newPassword = new Password();
            newPassword.addPermission(new Permission(clientSessionUtil.getLoggedInUser(), AccessLevel.GRANT));
            new PasswordDialog(newPassword).show();
        }
    }

    /**
     * 
     */
    protected void doEditGroup()
    {
        if (clientSessionUtil.isAuthorized("EDIT_GROUP"))
        {
            AsyncCallback<List<Group>> callback = new AsyncCallback<List<Group>>()
            {
                public void onFailure(Throwable caught)
                {
                    MessageBox.alert("Error", caught.getMessage(), null);
                }
                public void onSuccess(List<Group> result)
                {
                    new GroupSelectionDialog(new EditGroupListener(), result, false).show();
                }
            };
            UserService.Util.getInstance().getGroups(callback);
        }
        else
        {
            MessageBox.alert("Error", "Must be logged in first.", null);
        }
    }

    /**
     * 
     */
    protected void doAddGroup()
    {
        if (clientSessionUtil.isAuthorized("ADD_GROUP"))
        {
            displayGroupDialog(new Group());
        }
    }

    /**
     * @param groupDTO
     */
    private void displayGroupDialog(Group group)
    {
        new GroupDialog(group).show();
    }

    public void refreshLoginStatus()
    {
        refreshTopPanel();
        refreshMenu();
        refreshPasswordSearch();
    }

    private void doAddUser()
    {
        if (clientSessionUtil.isAuthorized("ADD_USER"))
        {
            displayUserDialog(new User());
        }
    }

    private void doEditUser()
    {
        if (clientSessionUtil.isAuthorized("EDIT_USER"))
        {
            AsyncCallback<List<User>> callback = new AsyncCallback<List<User>>()
            {
                public void onFailure(Throwable caught)
                {
                    MessageBox.alert("Error", caught.getMessage(), null);
                }
                public void onSuccess(List<User> result)
                {
                    new UserSelectionDialog(new EditUserListener(), result, false).show();
                }
            };
            UserService.Util.getInstance().getUsers(true, callback);
        }
        else
        {
        	MessageBox.alert("Error", "Must be logged in first.", null);
        }
    }
    
    private void doGetLoggedInUser()
    {
        AsyncCallback<User> callback = new AsyncCallback<User>()
        {

            public void onFailure(Throwable caught)
            {
                MessageBox.alert("Error", caught.getMessage(), null);
            }

            public void onSuccess(User result)
            {
                if (null != result)
                {
                    getClientModel().setLoggedInUser(result);
                    getClientModel().setLoggedIn(true);
                    refreshLoginStatus();
                }
                else
                {
                    verifyInitialization();
                }
            }
        };
        LoginService.Util.getInstance().getLogin(callback);
    }
    
    private void verifyInitialization()
    {
        AsyncCallback<Void> callback = new AsyncCallback<Void>()
        {
            public void onFailure(Throwable caught)
            {
                MessageBox.alert("Error", caught.getMessage(), null);
            }
            public void onSuccess(Void result)
            {
                getEveryoneGroup();
            }
        };
        UserService.Util.getInstance().verifyInitialization(callback);
    }

    private void getEveryoneGroup()
    {
        AsyncCallback<Group> callback = new AsyncCallback<Group>()
        {
            public void onFailure(Throwable caught)
            {
                MessageBox.alert("Error", caught.getMessage(), null);
            }
            public void onSuccess(Group result)
            {
                ClientSessionUtil.getInstance().setEveryoneGroup(result);
                displayLoginDialog();
            }
        };
        UserService.Util.getInstance().getEveryoneGroup(callback);
    }

    private void doLogout()
    {
        AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>()
        {

            public void onFailure(Throwable caught)
            {
                MessageBox.alert("Error", caught.getMessage(), null);
            }

            public void onSuccess(Boolean result)
            {
                if (result)
                {
                    clientSessionUtil.getLoggedInUser().setUsername("");
                    clientSessionUtil.setLoggedIn(false);
                    refreshLoginStatus();
                    displayLoginDialog();
                }
            }
        };
        LoginService.Util.getInstance().logout(callback);
    }
    
    private void displayLoginDialog()
    {
        new LoginDialog(this).show();
    }
    
    private void displayUserDialog(User user)
    {
        new UserDialog(user).show();
    }
    
    public ClientSessionUtil getClientModel()
    {
        return clientSessionUtil;
    }
    
    private class EditUserListener implements UserListener
    {
        public void doUsersChosen(List<User> users)
        {
            if (users.size() > 0)
            {
                displayUserDialog(users.get(0));
            }
        }
    }
    private class EditGroupListener implements GroupListener
    {
        public void doGroupsChosen(List<Group> groups)
        {
            if (groups.size() > 0)
            {
                displayGroupDialog(groups.get(0));
            }
        }
    }
}