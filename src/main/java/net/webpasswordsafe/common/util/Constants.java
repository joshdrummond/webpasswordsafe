/*
    Copyright 2009-2011 Josh Drummond

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
package net.webpasswordsafe.common.util;


/**
 * Common constants
 * 
 * @author Josh Drummond
 *
 */
public class Constants
{
    // versioning
    public static final String VERSION = "1.2";
    public static final String COPYRIGHT = "2008-2011 Josh Drummond";
    public static final String URL_WEBPASSWORDSAFE = "http://www.webpasswordsafe.net";
    public static final String URL_HELP = "http://code.google.com/p/webpasswordsafe/w/list?q=label:help";
    public static final String URL_LICENSE = "http://webpasswordsafe.googlecode.com/svn/trunk/docs/license.txt";
    
    // hardcoded names
    public static final String ADMIN_USER_NAME = "admin";
    public static final String EVERYONE_GROUP_NAME = "Everyone";
    
    // unique tokens
    public static final String SESSION_KEY_USERNAME = "X-WPS-Username";
    public static final String SESSION_KEY_ROLES = "X-WPS-Roles";
    public static final String CSRF_TOKEN_KEY = "X-WPS-CSRFToken";

    // reports
    public static final String VIEW_REPORT_PREFIX = "VIEW_REPORT_";
    public static final String REPORT_TYPE_PDF = "pdf";
    public static final String REPORT_TYPE_CSV = "csv";
    
    // widget fields
    public static final String ID = "id";
    public static final String FULLNAME = "fullname";
    public static final String NAME = "name";
    public static final String USERNAME = "username";
    public static final String USER = "user";
    public static final String GROUP = "group";
    public static final String PASSWORD = "password";
    public static final String DATE = "date";
    public static final String TITLE = "title";
    public static final String TAG = "tag";
    public static final String TAGS = "tags";
    public static final String NOTES = "notes";
    public static final String SUBJECT = "subject";
    public static final String ACCESSLEVEL = "accessLevel";
    public static final String PERMISSION = "permission";
    public static final String TEMPLATE = "template";
    public static final String TEMPLATEDETAIL = "templateDetail";
    public static final String VALUE = "value";
    
    // enums
    public enum Match { ANY, ALL };
    public enum Role { ROLE_USER, ROLE_ADMIN };
    public enum Report { CurrentPasswordExport, Groups, PasswordAccessAudit, PasswordPermissions, Users };
    public enum Function { ADD_USER, UPDATE_USER, ADD_GROUP, UPDATE_GROUP, ADD_PASSWORD,
        BYPASS_PASSWORD_PERMISSIONS, ADD_TEMPLATE, UPDATE_TEMPLATE, BYPASS_TEMPLATE_SHARING,
        UNBLOCK_IP,
        VIEW_REPORT_CurrentPasswordExport, VIEW_REPORT_Groups, VIEW_REPORT_PasswordAccessAudit, 
        VIEW_REPORT_PasswordPermissions, VIEW_REPORT_Users };

}
