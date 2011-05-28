/*
    Copyright 2008-2011 Josh Drummond

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
import java.util.Map;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
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
import com.joshdrummond.webpasswordsafe.client.remote.PasswordService;
import com.joshdrummond.webpasswordsafe.client.remote.UserService;
import com.joshdrummond.webpasswordsafe.client.ui.*;
import com.joshdrummond.webpasswordsafe.common.model.AccessLevel;
import com.joshdrummond.webpasswordsafe.common.model.Group;
import com.joshdrummond.webpasswordsafe.common.model.Password;
import com.joshdrummond.webpasswordsafe.common.model.Permission;
import com.joshdrummond.webpasswordsafe.common.model.Template;
import com.joshdrummond.webpasswordsafe.common.model.User;
import com.joshdrummond.webpasswordsafe.common.util.Constants;
import com.joshdrummond.webpasswordsafe.common.util.Constants.Function;
import com.joshdrummond.webpasswordsafe.common.util.Constants.Report;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * 
 * @author Josh Drummond
 * 
 */
public class WebPasswordSafe implements EntryPoint, MainWindow, LoginWindow
{
    private ClientSessionUtil clientSessionUtil = ClientSessionUtil.getInstance();
    private final static String NOT_LOGGED_IN = "Not Logged In";
    private final static String LOGGED_IN = "Logged In As: ";
    private final static String TITLE = "WebPasswordSafe v"+Constants.VERSION;
    private Viewport viewport; 
    private ContentPanel mainPanel, topPanel, menuPanel;
    private PasswordSearchPanel passwordSearchPanel;

    public void onModuleLoad()
    {
    	// menu panel
        menuPanel = new ContentPanel(new FillLayout());
        menuPanel.setHeaderVisible(false);
        menuPanel.setBorders(false);

        refreshMenu();

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
        viewport.add(menuPanel, new RowData(1, -1));
        //main panel
        mainPanel = new ContentPanel(new FillLayout());
        mainPanel.setHeaderVisible(false);
        viewport.add(mainPanel, new RowData(1, 1));
        
        RootPanel.get().add(viewport);

        doGetLoggedInUser(this);
    }

    private void refreshTopPanel()
    {
    	topPanel.removeAll();
        Text headerGwtLabel = new Text(TITLE);
        Text loggedInLabel = null;
        if (clientSessionUtil.isLoggedIn())
        {
            loggedInLabel = new Text(LOGGED_IN + Format.htmlEncode(clientSessionUtil.getLoggedInUser().getFullname()));
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

    private void doChangePassword()
    {
        new ChangePasswordDialog().show();
    }

    private void refreshPasswordSearch()
    {
        mainPanel.removeAll();
        if (clientSessionUtil.isLoggedIn())
        {
            passwordSearchPanel = new PasswordSearchPanel();
            passwordSearchPanel.setSize("100%", "100%");
            mainPanel.add(passwordSearchPanel);
        }
        mainPanel.layout();
    }

    private void refreshMenu()
    {
        MenuBar mainMenu = new MenuBar();

        buildUserMenu(mainMenu);
        buildPasswordMenu(mainMenu);
        buildAdminMenu(mainMenu);
        buildReportsMenu(mainMenu);
        buildAboutMenu(mainMenu);

        menuPanel.removeAll();
        menuPanel.add(mainMenu);
        menuPanel.layout();
    }
    
    private void buildUserMenu(MenuBar mainMenu)
    {
        Menu userMenu = new Menu();
        MenuItem userSettings = new MenuItem("Settings");
        Menu userSettingsMenu = new Menu();
        //userSettingsMenu.add(new MenuItem("General"));
        userSettingsMenu.add(new MenuItem("Change Password", new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
                doChangePassword();
            }
        }));
        userSettings.setSubMenu(userSettingsMenu);
        userMenu.add(userSettings);

//        MenuItem userRole = new MenuItem("Role");
//        Menu userRoleMenu = new Menu();
//        userRoleMenu.add(new MenuItem("User"));
//        userRoleMenu.add(new MenuItem("Admin"));
//        userRole.setSubMenu(userRoleMenu);
//        userMenu.add(userRole);

        userMenu.add(new MenuItem("Logout", new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
                doLogout();
            }
        }));

        mainMenu.add(new MenuBarItem("User", userMenu));
    }
    
    private void buildPasswordMenu(MenuBar mainMenu)
    {
        Menu passwordMenu = new Menu();
        if (clientSessionUtil.isAuthorized(Function.ADD_PASSWORD))
        {
	        passwordMenu.add(new MenuItem("New", new SelectionListener<MenuEvent>() {
				@Override
				public void componentSelected(MenuEvent ce) {
	                doNewPassword();
	            }
	        }));
        }
        MenuItem passwordSearch = new MenuItem("Search");
        Menu passwordSearchMenu = new Menu();
        passwordSearchMenu.add(new MenuItem("Open Selected Password", new SelectionListener<MenuEvent>() {
            @Override
            public void componentSelected(MenuEvent ce) {
                if (null != passwordSearchPanel)
                {
                    passwordSearchPanel.openSelectedPassword();
                }
            }
        }));
        passwordSearchMenu.add(new MenuItem("Get Selected Password Value", new SelectionListener<MenuEvent>() {
            @Override
            public void componentSelected(MenuEvent ce) {
                if (null != passwordSearchPanel)
                {
                    passwordSearchPanel.getSelectedCurrentPasswordData();
                }
            }
        }));
        passwordSearchMenu.add(new MenuItem("Refresh Search", new SelectionListener<MenuEvent>() {
            @Override
            public void componentSelected(MenuEvent ce) {
                refreshPasswordSearch();
            }
        }));
        passwordSearch.setSubMenu(passwordSearchMenu);
        passwordMenu.add(passwordSearch);

        MenuItem passwordTemplate = new MenuItem("Template");
        Menu passwordTemplateMenu = new Menu();
        if (clientSessionUtil.isAuthorized(Function.ADD_TEMPLATE))
        {
	        passwordTemplateMenu.add(new MenuItem("New", new SelectionListener<MenuEvent>() {
	            @Override
	            public void componentSelected(MenuEvent ce) {
	                doAddTemplate();
	            }
	        }));
        }
        if (clientSessionUtil.isAuthorized(Function.UPDATE_TEMPLATE))
        {
	        passwordTemplateMenu.add(new MenuItem("Edit", new SelectionListener<MenuEvent>() {
	            @Override
	            public void componentSelected(MenuEvent ce) {
	                doEditTemplate();
	            }
	        }));
        }
        passwordTemplate.setSubMenu(passwordTemplateMenu);
        passwordMenu.add(passwordTemplate);

        mainMenu.add(new MenuBarItem("Password", passwordMenu));
    }
    
    private void buildAdminMenu(MenuBar mainMenu)
    {
        // only create admin menu item if submenus allowed
        if (clientSessionUtil.isAuthorized(Function.ADD_USER) ||
        	clientSessionUtil.isAuthorized(Function.UPDATE_USER) ||
        	clientSessionUtil.isAuthorized(Function.ADD_GROUP) ||
        	clientSessionUtil.isAuthorized(Function.UPDATE_GROUP))
        {
	        Menu adminMenu = new Menu();
	        //adminMenu.add(new MenuItem("Settings"));
	
	        MenuItem adminUser = new MenuItem("Users");
	        Menu adminUserMenu = new Menu();
	        if (clientSessionUtil.isAuthorized(Function.ADD_USER))
	        {
		        adminUserMenu.add(new MenuItem("Add", new SelectionListener<MenuEvent>() {
					@Override
					public void componentSelected(MenuEvent ce) {
		                doAddUser();
		            }
		        }));
	        }
	        if (clientSessionUtil.isAuthorized(Function.UPDATE_USER))
	        {
		        adminUserMenu.add(new MenuItem("Edit", new SelectionListener<MenuEvent>() {
					@Override
					public void componentSelected(MenuEvent ce) {
		                doEditUser();
		            }
		        }));
	        }
	        adminUser.setSubMenu(adminUserMenu);
	        adminMenu.add(adminUser);
	        
	        MenuItem adminGroup = new MenuItem("Groups");
	        Menu adminGroupMenu = new Menu();
	        if (clientSessionUtil.isAuthorized(Function.ADD_GROUP))
	        {
		        adminGroupMenu.add(new MenuItem("Add", new SelectionListener<MenuEvent>() {
					@Override
					public void componentSelected(MenuEvent ce) {
		                doAddGroup();
		            }
		        }));
	        }
	        if (clientSessionUtil.isAuthorized(Function.UPDATE_GROUP))
	        {
		        adminGroupMenu.add(new MenuItem("Edit", new SelectionListener<MenuEvent>() {
					@Override
					public void componentSelected(MenuEvent ce) {
		                doEditGroup();
		            }
		        }));
	        }
	        adminGroup.setSubMenu(adminGroupMenu);
	        adminMenu.add(adminGroup);

	        mainMenu.add(new MenuBarItem("Admin", adminMenu));
        }
//        MenuItem adminRole = new MenuItem("Roles");
//        adminMenu.add(adminRole);
    }
    
    private void buildReportsMenu(MenuBar mainMenu)
    {
        Menu reportsMenu = new Menu();
        if (clientSessionUtil.isAuthorized(Function.VIEW_REPORT_Users))
        {
            reportsMenu.add(buildReportMenuItem("Users", Report.Users));
        }
        if (clientSessionUtil.isAuthorized(Function.VIEW_REPORT_Groups))
        {
        	reportsMenu.add(buildReportMenuItem("Groups", Report.Groups));
        }
        if (clientSessionUtil.isAuthorized(Function.VIEW_REPORT_PasswordAccessAudit))
        {
        	reportsMenu.add(buildReportMenuItem("Access Audit", Report.PasswordAccessAudit));
        }
        if (clientSessionUtil.isAuthorized(Function.VIEW_REPORT_PasswordPermissions))
        {
        	reportsMenu.add(buildReportMenuItem("Permissions", Report.PasswordPermissions));
        }
        if (clientSessionUtil.isAuthorized(Function.VIEW_REPORT_CurrentPasswordExport))
        {
        	reportsMenu.add(buildReportMenuItem("Password Export", Report.CurrentPasswordExport));
        }
        mainMenu.add(new MenuBarItem("Reports", reportsMenu));

    }
    
    private void buildAboutMenu(MenuBar mainMenu)
    {
        Menu aboutMenu = new Menu();
        aboutMenu.add(new MenuItem("Help", new SelectionListener<MenuEvent>()
        {
            @Override
            public void componentSelected(MenuEvent ce)
            {
                doShowHelp();
            }
        }));
        aboutMenu.add(new MenuItem("About", new SelectionListener<MenuEvent>()
        {
            @Override
            public void componentSelected(MenuEvent ce)
            {
                doShowAbout();
            }
        }));
        
        mainMenu.add(new MenuBarItem("About", aboutMenu));
    }
    
    private MenuItem buildReportMenuItem(String menuName, Constants.Report reportName)
    {
        MenuItem menuItem = new MenuItem(menuName);
        Menu subMenu =  new Menu();
        subMenu.add(buildReportMenuItemType(reportName.name(), "pdf"));
        subMenu.add(buildReportMenuItemType(reportName.name(), "csv"));
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

    private void doNewPassword()
    {
        if (clientSessionUtil.isAuthorized(Function.ADD_PASSWORD))
        {
            Password newPassword = new Password();
            newPassword.setMaxEffectiveAccessLevel(AccessLevel.GRANT);
            newPassword.addPermission(new Permission(clientSessionUtil.getLoggedInUser(), AccessLevel.GRANT));
            new PasswordDialog(newPassword, passwordSearchPanel).show();
        }
        else
        {
            MessageBox.alert("Error", "Not Authorized!", null);
        }
    }

    private void doEditGroup()
    {
        if (clientSessionUtil.isAuthorized(Function.UPDATE_GROUP))
        {
            AsyncCallback<List<Group>> callback = new AsyncCallback<List<Group>>()
            {
                @Override
                public void onFailure(Throwable caught)
                {
                    WebPasswordSafe.handleServerFailure(caught);
                }
                @Override
                public void onSuccess(List<Group> result)
                {
                    new GroupSelectionDialog(new EditGroupListener(), result, false).show();
                }
            };
            UserService.Util.getInstance().getGroups(false, callback);
        }
        else
        {
            MessageBox.alert("Error", "Not Authorized!", null);
        }
    }

    private void doAddGroup()
    {
        if (clientSessionUtil.isAuthorized(Function.ADD_GROUP))
        {
            displayGroupDialog(new Group());
        }
        else
        {
            MessageBox.alert("Error", "Not Authorized!", null);
        }
    }

    private void displayGroupDialog(Group group)
    {
        new GroupDialog(group).show();
    }

    @Override
    public void refreshLoginStatus()
    {
        refreshTopPanel();
        refreshMenu();
        refreshPasswordSearch();
    }

    private void doAddUser()
    {
        if (clientSessionUtil.isAuthorized(Function.ADD_USER))
        {
            displayUserDialog(new User());
        }
        else
        {
            MessageBox.alert("Error", "Not Authorized!", null);
        }
    }

    private void doEditUser()
    {
        if (clientSessionUtil.isAuthorized(Function.UPDATE_USER))
        {
            AsyncCallback<List<User>> callback = new AsyncCallback<List<User>>()
            {
                @Override
                public void onFailure(Throwable caught)
                {
                    WebPasswordSafe.handleServerFailure(caught);
                }
                @Override
                public void onSuccess(List<User> result)
                {
                    new UserSelectionDialog(new EditUserListener(), result, false).show();
                }
            };
            UserService.Util.getInstance().getUsers(false, callback);
        }
        else
        {
            MessageBox.alert("Error", "Not Authorized!", null);
        }
    }
    
    private void doAddTemplate()
    {
        if (clientSessionUtil.isAuthorized(Function.ADD_TEMPLATE))
        {
            displayTemplateDialog(new Template());
        }
        else
        {
            MessageBox.alert("Error", "Not Authorized!", null);
        }
    }

    private void doEditTemplate()
    {
        if (clientSessionUtil.isAuthorized(Function.UPDATE_TEMPLATE))
        {
            AsyncCallback<List<Template>> callback = new AsyncCallback<List<Template>>()
            {
                @Override
                public void onFailure(Throwable caught)
                {
                    WebPasswordSafe.handleServerFailure(caught);
                }
                @Override
                public void onSuccess(List<Template> result)
                {
                    new TemplateSelectionDialog(new EditTemplateListener(), result, false).show();
                }
            };
            PasswordService.Util.getInstance().getTemplates(true, callback);
        }
        else
        {
            MessageBox.alert("Error", "Not Authorized!", null);
        }
    }
    
    private void displayTemplateDialog(Template template)
    {
        new TemplateDialog(template).show();
    }
    
    @Override
    public void doGetLoggedInUser(final LoginWindow loginWindow)
    {
        AsyncCallback<User> callback = new AsyncCallback<User>()
        {
            @Override
            public void onFailure(Throwable caught)
            {
                WebPasswordSafe.handleServerFailure(caught);
            }
            @Override
            public void onSuccess(User result)
            {
                if (null != result)
                {
                    getClientModel().setLoggedInUser(result);
                    getClientModel().setLoggedIn(true);
                    getLoginAuthorizations(loginWindow);
                }
                else
                {
                	loginWindow.doGetLoginFailure();
                }
            }
        };
        LoginService.Util.getInstance().getLogin(callback);
    }
    
    private void getLoginAuthorizations(final LoginWindow loginWindow)
    {
        AsyncCallback<Map<Function, Boolean>> callback = new AsyncCallback<Map<Function, Boolean>>()
        {
            @Override
            public void onFailure(Throwable caught)
            {
                WebPasswordSafe.handleServerFailure(caught);
            }
            @Override
            public void onSuccess(Map<Function, Boolean> result)
            {
                getClientModel().setAuthorizations(result);
                refreshLoginStatus();
            	loginWindow.doGetLoginSuccess();
            }
        };
        LoginService.Util.getInstance().getLoginAuthorizations(null, callback);
    }

    private void verifyInitialization()
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
                getEveryoneGroup();
            }
        };
        UserService.Util.getInstance().verifyInitialization(callback);
    }

    private void getEveryoneGroup()
    {
        AsyncCallback<Group> callback = new AsyncCallback<Group>()
        {
            @Override
            public void onFailure(Throwable caught)
            {
                WebPasswordSafe.handleServerFailure(caught);
            }
            @Override
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
            @Override
            public void onFailure(Throwable caught)
            {
                WebPasswordSafe.handleServerFailure(caught);
            }
            @Override
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
    
    @Override
    public ClientSessionUtil getClientModel()
    {
        return clientSessionUtil;
    }
    
    @Override
    public void doGetLoginSuccess()
    {
    	Info.display("Status", Format.htmlEncode(getClientModel().getLoggedInUser().getUsername()) + " logged in");
    }
    
    @Override
    public void doGetLoginFailure()
    {
        verifyInitialization();
    }
    
    private void doShowAbout()
    {
        new AboutDialog().show();
    }
    
    private void doShowHelp()
    {
        Window.open("http://code.google.com/p/webpasswordsafe/w/list?q=label:help", "_blank", "");
    }
    
    private class EditUserListener implements UserListener
    {
        @Override
        public void doUsersChosen(List<User> users)
        {
            if (users.size() > 0)
            {
                AsyncCallback<User> callback = new AsyncCallback<User>()
                {
                    @Override
                    public void onFailure(Throwable caught)
                    {
                        WebPasswordSafe.handleServerFailure(caught);
                    }
                    @Override
                    public void onSuccess(User result)
                    {
                        displayUserDialog(result);
                    }
                };
                UserService.Util.getInstance().getUserWithGroups(users.get(0).getId(), callback);
            }
        }
    }

    private class EditGroupListener implements GroupListener
    {
        @Override
        public void doGroupsChosen(List<Group> groups)
        {
            if (groups.size() > 0)
            {
                AsyncCallback<Group> callback = new AsyncCallback<Group>()
                {
                    @Override
                    public void onFailure(Throwable caught)
                    {
                        WebPasswordSafe.handleServerFailure(caught);
                    }
                    @Override
                    public void onSuccess(Group result)
                    {
                        displayGroupDialog(result);
                    }
                };
                UserService.Util.getInstance().getGroupWithUsers(groups.get(0).getId(), callback);
            }
        }
    }
    
    private class EditTemplateListener implements TemplateListener
    {
        @Override
        public void doTemplatesChosen(List<Template> templates)
        {
            if (templates.size() > 0)
            {
                AsyncCallback<Template> callback = new AsyncCallback<Template>()
                {
                    @Override
                    public void onFailure(Throwable caught)
                    {
                        WebPasswordSafe.handleServerFailure(caught);
                    }
                    @Override
                    public void onSuccess(Template result)
                    {
                        displayTemplateDialog(result);
                    }
                };
                PasswordService.Util.getInstance().getTemplateWithDetails(templates.get(0).getId(), callback);
            }
        }
    }

    //// public static methods...
    
    public static void handleServerFailure(Throwable caught)
    {
        handleServerFailure(caught, false);
    }
    
    public static void handleServerFailure(Throwable caught, boolean showDetails)
    {
        String message = "Session Timeout. Please login again." + 
            (showDetails ? "<br>"+caught.getMessage() : "");
        MessageBox.alert("Error", message, new ServerErrorListener());
    }
    
}