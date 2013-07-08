/*
    Copyright 2008-2013 Josh Drummond

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
package net.webpasswordsafe.client;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import net.webpasswordsafe.client.i18n.TextMessages;
import net.webpasswordsafe.client.remote.LoginService;
import net.webpasswordsafe.client.remote.PasswordService;
import net.webpasswordsafe.client.remote.ServiceHelper;
import net.webpasswordsafe.client.remote.UserService;
import net.webpasswordsafe.client.ui.*;
import net.webpasswordsafe.common.model.AccessLevel;
import net.webpasswordsafe.common.model.Group;
import net.webpasswordsafe.common.model.Password;
import net.webpasswordsafe.common.model.Permission;
import net.webpasswordsafe.common.model.Template;
import net.webpasswordsafe.common.model.User;
import net.webpasswordsafe.common.util.Constants;
import net.webpasswordsafe.common.util.Constants.Function;
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
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.rpc.XsrfToken;
import com.google.gwt.user.client.rpc.XsrfTokenService;
import com.google.gwt.user.client.rpc.XsrfTokenServiceAsync;
import com.google.gwt.user.client.ui.RootPanel;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * 
 * @author Josh Drummond
 * 
 */
public class WebPasswordSafe implements EntryPoint, MainWindow, LoginWindow
{
    private Logger log = Logger.getLogger(WebPasswordSafe.class.getName());
    private ClientSessionUtil clientSessionUtil = ClientSessionUtil.getInstance();
    private final static TextMessages textMessages = GWT.create(TextMessages.class);
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

        log.info(textMessages.webpasswordsafeTitle(Constants.VERSION));
        pingServer(this);
    }

    private void refreshTopPanel()
    {
        topPanel.removeAll();
        Text headerGwtLabel = new Text(textMessages.webpasswordsafeTitle(Constants.VERSION));
        Text loggedInLabel = null;
        if (clientSessionUtil.isLoggedIn())
        {
            loggedInLabel = new Text(textMessages.loggedInAs(Format.htmlEncode(clientSessionUtil.getLoggedInUser().getFullname())));
        }
        else
        {
            loggedInLabel = new Text(textMessages.notLoggedIn());
        }
        topPanel.add(headerGwtLabel, new HBoxLayoutData(0, 5, 0, 0));
        HBoxLayoutData flex = new HBoxLayoutData(new Margins(0, 5, 0, 0));
        flex.setFlex(1);
        topPanel.add(new Text(), flex);
        topPanel.add(loggedInLabel, new HBoxLayoutData(new Margins(0)));
        topPanel.layout();
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
        MenuItem userSettings = new MenuItem(textMessages.settings());
        Menu userSettingsMenu = new Menu();
        //userSettingsMenu.add(new MenuItem("General"));
        userSettingsMenu.add(new MenuItem(textMessages.changePassword(), new SelectionListener<MenuEvent>() {
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

        userMenu.add(new MenuItem(textMessages.logout(), new SelectionListener<MenuEvent>() {
            @Override
            public void componentSelected(MenuEvent ce) {
                doLogout();
            }
        }));

        mainMenu.add(new MenuBarItem(textMessages.user(), userMenu));
    }
    
    private void buildPasswordMenu(MenuBar mainMenu)
    {
        Menu passwordMenu = new Menu();
        if (clientSessionUtil.isAuthorized(Function.ADD_PASSWORD))
        {
            passwordMenu.add(new MenuItem(textMessages.add(), new SelectionListener<MenuEvent>() {
                @Override
                public void componentSelected(MenuEvent ce) {
                    doNewPassword();
                }
            }));
        }
        MenuItem passwordSearch = new MenuItem(textMessages.search());
        Menu passwordSearchMenu = new Menu();
        passwordSearchMenu.add(new MenuItem(textMessages.openSelectedPassword(), new SelectionListener<MenuEvent>() {
            @Override
            public void componentSelected(MenuEvent ce) {
                if (null != passwordSearchPanel)
                {
                    passwordSearchPanel.openSelectedPassword();
                }
            }
        }));
        passwordSearchMenu.add(new MenuItem(textMessages.getSelectedPasswordValue(), new SelectionListener<MenuEvent>() {
            @Override
            public void componentSelected(MenuEvent ce) {
                if (null != passwordSearchPanel)
                {
                    passwordSearchPanel.getSelectedCurrentPasswordData();
                }
            }
        }));
        passwordSearchMenu.add(new MenuItem(textMessages.refreshSearch(), new SelectionListener<MenuEvent>() {
            @Override
            public void componentSelected(MenuEvent ce) {
                refreshPasswordSearch();
            }
        }));
        passwordSearch.setSubMenu(passwordSearchMenu);
        passwordMenu.add(passwordSearch);

        MenuItem passwordTemplate = new MenuItem(textMessages.template());
        Menu passwordTemplateMenu = new Menu();
        if (clientSessionUtil.isAuthorized(Function.ADD_TEMPLATE))
        {
            passwordTemplateMenu.add(new MenuItem(textMessages.add(), new SelectionListener<MenuEvent>() {
                @Override
                public void componentSelected(MenuEvent ce) {
                    doAddTemplate();
                }
            }));
        }
        if (clientSessionUtil.isAuthorized(Function.UPDATE_TEMPLATE))
        {
            passwordTemplateMenu.add(new MenuItem(textMessages.edit(), new SelectionListener<MenuEvent>() {
                @Override
                public void componentSelected(MenuEvent ce) {
                    doEditTemplate();
                }
            }));
        }
        passwordTemplate.setSubMenu(passwordTemplateMenu);
        passwordMenu.add(passwordTemplate);

        mainMenu.add(new MenuBarItem(textMessages.password(), passwordMenu));
    }
    
    private void buildAdminMenu(MenuBar mainMenu)
    {
        // only create admin menu item if submenus allowed
        if (clientSessionUtil.isAuthorized(Function.ADD_USER) ||
            clientSessionUtil.isAuthorized(Function.UPDATE_USER) ||
            clientSessionUtil.isAuthorized(Function.ADD_GROUP) ||
            clientSessionUtil.isAuthorized(Function.UPDATE_GROUP) ||
            clientSessionUtil.isAuthorized(Function.UNBLOCK_IP))
        {
            Menu adminMenu = new Menu();
            //adminMenu.add(new MenuItem("Settings"));
    
            MenuItem adminUser = new MenuItem(textMessages.users());
            Menu adminUserMenu = new Menu();
            if (clientSessionUtil.isAuthorized(Function.ADD_USER))
            {
                adminUserMenu.add(new MenuItem(textMessages.add(), new SelectionListener<MenuEvent>() {
                    @Override
                    public void componentSelected(MenuEvent ce) {
                        doAddUser();
                    }
                }));
            }
            if (clientSessionUtil.isAuthorized(Function.UPDATE_USER))
            {
                adminUserMenu.add(new MenuItem(textMessages.edit(), new SelectionListener<MenuEvent>() {
                    @Override
                    public void componentSelected(MenuEvent ce) {
                        doEditUser();
                    }
                }));
            }
            adminUser.setSubMenu(adminUserMenu);
            adminMenu.add(adminUser);
            
            MenuItem adminGroup = new MenuItem(textMessages.groups());
            Menu adminGroupMenu = new Menu();
            if (clientSessionUtil.isAuthorized(Function.ADD_GROUP))
            {
                adminGroupMenu.add(new MenuItem(textMessages.add(), new SelectionListener<MenuEvent>() {
                    @Override
                    public void componentSelected(MenuEvent ce) {
                        doAddGroup();
                    }
                }));
            }
            if (clientSessionUtil.isAuthorized(Function.UPDATE_GROUP))
            {
                adminGroupMenu.add(new MenuItem(textMessages.edit(), new SelectionListener<MenuEvent>() {
                    @Override
                    public void componentSelected(MenuEvent ce) {
                        doEditGroup();
                    }
                }));
            }
            adminGroup.setSubMenu(adminGroupMenu);
            adminMenu.add(adminGroup);

            MenuItem adminTools = new MenuItem(textMessages.tools());
            Menu adminToolsMenu = new Menu();
            if (clientSessionUtil.isAuthorized(Function.UNBLOCK_IP))
            {
                adminToolsMenu.add(new MenuItem(textMessages.unblockIP(), new SelectionListener<MenuEvent>() {
                    @Override
                    public void componentSelected(MenuEvent ce) {
                        doUnblockIP();
                    }
                }));
            }
            adminTools.setSubMenu(adminToolsMenu);
            adminMenu.add(adminTools);
            
            mainMenu.add(new MenuBarItem(textMessages.admin(), adminMenu));
        }
//        MenuItem adminRole = new MenuItem("Roles");
//        adminMenu.add(adminRole);
    }
    
    private void buildReportsMenu(MenuBar mainMenu)
    {
        Menu reportsMenu = new Menu();
        List<Map<String, Object>> reports = getClientModel().getAvailableReports();
        for (final Map<String, Object> report : reports)
        {
            MenuItem menuItem = new MenuItem((String)report.get(Constants.I18N), new SelectionListener<MenuEvent>()
            {
                @Override
                public void componentSelected(MenuEvent ce)
                {
                    displayReportDialog(report);
                }
            });
            reportsMenu.add(menuItem);
        }
        mainMenu.add(new MenuBarItem(textMessages.reports(), reportsMenu));
    }
    
    private void displayReportDialog(Map<String, Object> report)
    {
        new ReportDialog(report).show();
    }

    private void buildAboutMenu(MenuBar mainMenu)
    {
        Menu aboutMenu = new Menu();
        aboutMenu.add(new MenuItem(textMessages.help(), new SelectionListener<MenuEvent>()
        {
            @Override
            public void componentSelected(MenuEvent ce)
            {
                doShowHelp();
            }
        }));
        aboutMenu.add(new MenuItem(textMessages.about(), new SelectionListener<MenuEvent>()
        {
            @Override
            public void componentSelected(MenuEvent ce)
            {
                doShowAbout();
            }
        }));
        
        mainMenu.add(new MenuBarItem(textMessages.about(), aboutMenu));
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
            MessageBox.alert(textMessages.error(), textMessages.notAuthorized(), null);
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
            MessageBox.alert(textMessages.error(), textMessages.notAuthorized(), null);
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
            MessageBox.alert(textMessages.error(), textMessages.notAuthorized(), null);
        }
    }

    private void doUnblockIP()
    {
        if (clientSessionUtil.isAuthorized(Function.UNBLOCK_IP))
        {
            displayIPUnblockDialog();
        }
        else
        {
            MessageBox.alert(textMessages.error(), textMessages.notAuthorized(), null);
        }
    }

    private void displayIPUnblockDialog()
    {
        new IPUnblockDialog().show();
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
            MessageBox.alert(textMessages.error(), textMessages.notAuthorized(), null);
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
            MessageBox.alert(textMessages.error(), textMessages.notAuthorized(), null);
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
            MessageBox.alert(textMessages.error(), textMessages.notAuthorized(), null);
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
            MessageBox.alert(textMessages.error(), textMessages.notAuthorized(), null);
        }
    }
    
    private void displayTemplateDialog(Template template)
    {
        new TemplateDialog(template).show();
    }

    private void pingServer(final LoginWindow loginWindow)
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
                initXsrfProtection(loginWindow);
            }
        };
        LoginService.Util.getInstance().ping(callback);
    }
    
    private void initXsrfProtection(final LoginWindow loginWindow)
    {
        XsrfTokenServiceAsync xsrf = (XsrfTokenServiceAsync)GWT.create(XsrfTokenService.class);
        ((ServiceDefTarget)xsrf).setServiceEntryPoint(GWT.getModuleBaseURL() + "xsrf");
        xsrf.getNewXsrfToken(new AsyncCallback<XsrfToken>() {
            @Override
            public void onSuccess(XsrfToken token)
            {
                ServiceHelper.setXsrfToken(token);
                doGetLoggedInUser(loginWindow);
            }
            @Override
            public void onFailure(Throwable caught)
            {
                WebPasswordSafe.handleServerFailure(caught);
            }
        });
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
                getLoginReports(loginWindow);
            }
        };
        LoginService.Util.getInstance().getLoginAuthorizations(null, callback);
    }

    private void getLoginReports(final LoginWindow loginWindow)
    {
        AsyncCallback<List<Map<String, Object>>> callback = new AsyncCallback<List<Map<String, Object>>>()
        {
            @Override
            public void onFailure(Throwable caught)
            {
                WebPasswordSafe.handleServerFailure(caught);
            }
            @Override
            public void onSuccess(List<Map<String, Object>> result)
            {
                getClientModel().setAvailableReports(result);
                refreshLoginStatus();
                loginWindow.doGetLoginSuccess();
            }
        };
        LoginService.Util.getInstance().getLoginReports(callback);
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
        Info.display(textMessages.status(), textMessages.loggedIn(Format.htmlEncode(getClientModel().getLoggedInUser().getUsername())));
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
        Window.open(GWT.getHostPageBaseURL() + Constants.URL_HELP, "_blank", "");
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
        String message = textMessages.sessionTimeout() + 
            (showDetails ? "<br>"+caught.getMessage() : "");
        MessageBox.alert(textMessages.error(), message, new ServerErrorListener());
    }
    
}