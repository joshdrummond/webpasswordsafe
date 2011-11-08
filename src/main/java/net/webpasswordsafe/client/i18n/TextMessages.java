/*
    Copyright 2011 Josh Drummond

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
    @DefaultMessage("Copyright &#169; {0}.")
    String copyrightBy(String copyright);

    @DefaultMessage("Found {0} Password(s)")
    String foundPasswords(int count);

    @DefaultMessage("Group name already exists")
    String groupNameAlreadyExists();

    @DefaultMessage("Group saved")
    String groupSaved();

    @DefaultMessage("Email invalid")
    String invalidEmail();

    @DefaultMessage("Invalid Login!")
    String invalidLogin();

    @DefaultMessage("Invalid User!")
    String invalidUser();

    @DefaultMessage("IP Address doesn''t exist")
    String ipAddressNotExist();

    @DefaultMessage("IP Address unblocked")
    String ipAddressUnblocked();

    @DefaultMessage("{0} logged in")
    String loggedIn(String username);

    @DefaultMessage("Logged In As: {0}")
    String loggedInAs(String name);

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

    @DefaultMessage("Passwords don''t match")
    String passwordsNotMatch();

    @DefaultMessage("You don''t have access to read that password!")
    String noAccessPasswordRead();

    @DefaultMessage("Not Logged In")
    String notLoggedIn();

    @DefaultMessage("Password changed")
    String passwordChanged();

    @DefaultMessage("Password saved")
    String passwordSaved();

    @DefaultMessage("Password title already exists")
    String passwordTitleExists();

    @DefaultMessage("Please select a group")
    String pleaseSelectGroup();

    @DefaultMessage("Please select groups(s)")
    String pleaseSelectGroups();

    @DefaultMessage("Session Timeout. Please login again.")
    String sessionTimeout();

    @DefaultMessage("Template name already exists")
    String templateNameExists();

    @DefaultMessage("Template saved")
    String templateSaved();

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

    @DefaultMessage("User saved")
    String userSaved();

    @DefaultMessage("Username already exists")
    String usernameAlreadyExists();

    @DefaultMessage("Version {0}")
    String version(String version);

    @DefaultMessage("WebPasswordSafe v{0}")
    String webpasswordsafeTitle(String version);
    
}
