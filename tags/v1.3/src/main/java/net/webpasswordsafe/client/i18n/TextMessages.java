/*
    Copyright 2011-2013 Josh Drummond

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
package net.webpasswordsafe.client.i18n;

import com.google.gwt.i18n.client.Messages;


/**
 * Internationalization Messages Bundle
 * 
 * @author Josh Drummond
 *
 */
public interface TextMessages extends Messages
{
    @DefaultMessage("About")
    String about();

    @DefaultMessage("Access Audit")
    String accessAudit();

    @DefaultMessage("Access Level")
    String accessLevel();

    @DefaultMessage("Active")
    String active();

    @DefaultMessage("Active Only")
    String activeOnly();

    @DefaultMessage("Add")
    String add();

    @DefaultMessage("Add Template")
    String addTemplate();

    @DefaultMessage("Admin")
    String admin();

    @DefaultMessage("All")
    String all();

    @DefaultMessage("All rights reserved.")
    String allRightsReserved();

    @DefaultMessage("AND")
    String and();

    @DefaultMessage("Any")
    String any();

    @DefaultMessage("Available")
    String available();

    @DefaultMessage("Cancel")
    String cancel();

    @DefaultMessage("Change Password")
    String changePassword();

    @DefaultMessage("Close")
    String close();

    @DefaultMessage("Confirm Delete")
    String confirmDelete();

    @DefaultMessage("Copyright &#169; {0}.")
    String copyrightBy(String copyright);

    @DefaultMessage("Current Password")
    String currentPassword();

    @DefaultMessage("Date Accessed")
    String dateAccessed();

    @DefaultMessage("Date Created")
    String dateCreated();

    @DefaultMessage("Delete")
    String delete();

    @DefaultMessage("******")
    String displayCensored();

    @DefaultMessage("MM/dd/yyyy HH:mm:ss")
    String displayDateFormat();

    @DefaultMessage("Edit")
    String edit();

    @DefaultMessage("Edit Permissions")
    String editPermissions();

    @DefaultMessage("Email:")
    String email_();

    @DefaultMessage("Enabled")
    String enabled();

    @DefaultMessage("Error")
    String error();

    @DefaultMessage("Found {0} Password(s)")
    String foundPasswords(int count);

    @DefaultMessage("Full Name:")
    String fullname_();

    @DefaultMessage("Generate Password")
    String generatePassword();

    @DefaultMessage("Get Selected Password Value")
    String getSelectedPasswordValue();

    @DefaultMessage("GNU General Public License v2")
    String gpl2();

    @DefaultMessage("Group")
    String group();

    @DefaultMessage("Groups")
    String groups();

    @DefaultMessage("Groups:")
    String groups_();

    @DefaultMessage("Are you sure you want to permanently delete group and remove from any associated users and permissions?")
    String groupConfirmDelete();

    @DefaultMessage("Group deleted")
    String groupDeleted();

    @DefaultMessage("Group name already exists")
    String groupNameAlreadyExists();

    @DefaultMessage("Group saved")
    String groupSaved();

    @DefaultMessage("Help")
    String help();

    @DefaultMessage("(-1 infinite)")
    String infinite();

    @DefaultMessage("Date invalid (yyyy-MM-dd HH:mm)")
    String invalidDate();

    @DefaultMessage("Email invalid")
    String invalidEmail();

    @DefaultMessage("Invalid Login!")
    String invalidLogin();

    @DefaultMessage("Invalid User!")
    String invalidUser();

    @DefaultMessage("IP Address")
    String ipAddress();

    @DefaultMessage("IP Address doesn''t exist")
    String ipAddressNotExist();

    @DefaultMessage("IP Address unblocked")
    String ipAddressUnblocked();

    @DefaultMessage("Login")
    String login();

    @DefaultMessage("{0} logged in")
    String loggedIn(String username);

    @DefaultMessage("Logged In As: {0}")
    String loggedInAs(String name);

    @DefaultMessage("Logout")
    String logout();

    @DefaultMessage("Max History:")
    String maxHistory_();

    @DefaultMessage("Member Of")
    String memberOf();

    @DefaultMessage("Members")
    String members();

    @DefaultMessage("Must enter Email")
    String mustEnterEmail();

    @DefaultMessage("Must enter Full Name")
    String mustEnterFullName();

    @DefaultMessage("Must enter an IP Address")
    String mustEnterIPAddress();

    @DefaultMessage("Must enter Name")
    String mustEnterName();
    
    @DefaultMessage("Must enter Password")
    String mustEnterPassword();

    @DefaultMessage("Must enter Title")
    String mustEnterTitle();

    @DefaultMessage("Must enter Username")
    String mustEnterUsername();

    @DefaultMessage("Must have at least one permission")
    String mustHaveOnePermission();

    @DefaultMessage("Passwords must match")
    String mustMatchPasswords();

    @DefaultMessage("Name:")
    String name_();

    @DefaultMessage("New Password")
    String newPassword();

    @DefaultMessage("You don''t have access to read that password!")
    String noAccessPasswordRead();

    @DefaultMessage("Not Authorized!")
    String notAuthorized();

    @DefaultMessage("Not Logged In")
    String notLoggedIn();

    @DefaultMessage("Notes")
    String notes();

    @DefaultMessage("Notes:")
    String notes_();

    @DefaultMessage("Okay")
    String okay();

    @DefaultMessage("Open Selected Password")
    String openSelectedPassword();

    @DefaultMessage("OR")
    String or();

    @DefaultMessage("Password")
    String password();

    @DefaultMessage("Password:")
    String password_();

    @DefaultMessage("Password(s)")
    String passwords();

    @DefaultMessage("Password Access Audit Log")
    String passwordAccessAuditLog();

    @DefaultMessage("Password changed")
    String passwordChanged();

    @DefaultMessage("Password Export")
    String passwordExport();

    @DefaultMessage("Password History")
    String passwordHistory();

    @DefaultMessage("Passwords don''t match")
    String passwordsNotMatch();

    @DefaultMessage("Password saved")
    String passwordSaved();

    @DefaultMessage("Password Search")
    String passwordSearch();

    @DefaultMessage("Password title and username already exists")
    String passwordTitleExists();

    @DefaultMessage("Password Value")
    String passwordValue();

    @DefaultMessage("Permissions")
    String permissions();

    @DefaultMessage("Please select a template")
    String pleaseSelectTemplate();

    @DefaultMessage("Please select template(s)")
    String pleaseSelectTemplates();

    @DefaultMessage("Please select a user")
    String pleaseSelectUser();

    @DefaultMessage("Please select user(s)")
    String pleaseSelectUsers();

    @DefaultMessage("Please select a group")
    String pleaseSelectGroup();

    @DefaultMessage("Please select group(s)")
    String pleaseSelectGroups();

    @DefaultMessage("Re-enter Password")
    String reenterPassword();

    @DefaultMessage("Refresh Search")
    String refreshSearch();

    @DefaultMessage("Remove All")
    String removeAll();

    @DefaultMessage("Remove Selected")
    String removeSelected();

    @DefaultMessage("Reports")
    String reports();

    @DefaultMessage("Save")
    String save();

    @DefaultMessage("Search")
    String search();

    @DefaultMessage("Select a User/Group...")
    String selectUserGroup();

    @DefaultMessage("Session Timeout. Please login again.")
    String sessionTimeout();

    @DefaultMessage("Settings")
    String settings();

    @DefaultMessage("Shared?")
    String shared_();

    @DefaultMessage("Status")
    String status();

    @DefaultMessage("Submit")
    String submit();

    @DefaultMessage("Tag(s)")
    String tags();

    @DefaultMessage("Tags:")
    String tags_();

    @DefaultMessage("Template")
    String template();

    @DefaultMessage("Templates")
    String templates();

    @DefaultMessage("Are you sure you want to permanently delete template?")
    String templateConfirmDelete();

    @DefaultMessage("Template deleted")
    String templateDeleted();

    @DefaultMessage("Template name already exists")
    String templateNameExists();

    @DefaultMessage("Template saved")
    String templateSaved();

    @DefaultMessage("Title")
    String title();

    @DefaultMessage("Title:")
    String title_();

    @DefaultMessage("Tools")
    String tools();

    @DefaultMessage("Email too long")
    String tooLongEmail();

    @DefaultMessage("Full Name too long")
    String tooLongFullName();

    @DefaultMessage("IP Address too long")
    String tooLongIPAddress();

    @DefaultMessage("Name too long")
    String tooLongName();

    @DefaultMessage("Password too long")
    String tooLongPassword();

    @DefaultMessage("Tag Name too long")
    String tooLongTag();

    @DefaultMessage("Title too long")
    String tooLongTitle();

    @DefaultMessage("Username too long")
    String tooLongUsername();

    @DefaultMessage("Type")
    String type();

    @DefaultMessage("Unblock IP")
    String unblockIP();

    @DefaultMessage("User")
    String user();

    @DefaultMessage("Users")
    String users();

    @DefaultMessage("Users:")
    String users_();

    @DefaultMessage("User Accessed")
    String userAccessed();

    @DefaultMessage("User Created")
    String userCreated();

    @DefaultMessage("User/Group")
    String userGroup();

    @DefaultMessage("User saved")
    String userSaved();

    @DefaultMessage("Username")
    String username();

    @DefaultMessage("Username:")
    String username_();

    @DefaultMessage("Username already exists")
    String usernameAlreadyExists();

    @DefaultMessage("Version {0}")
    String version(String version);

    @DefaultMessage("View Access Audit Log")
    String viewAccessAuditLog();

    @DefaultMessage("View Password History")
    String viewPasswordHistory();

    @DefaultMessage("View Permissions")
    String viewPermissions();

    @DefaultMessage("WebPasswordSafe")
    String webpasswordsafe();

    @DefaultMessage("WebPasswordSafe v{0}")
    String webpasswordsafeTitle(String version);
    
}
