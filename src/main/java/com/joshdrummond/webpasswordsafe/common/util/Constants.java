/*
    Copyright 2009-2010 Josh Drummond

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
package com.joshdrummond.webpasswordsafe.common.util;

/**
 * Common constants
 * 
 * @author Josh Drummond
 *
 */
public class Constants
{
    public static final String VERSION = "1.0";
    public static final String ADMIN_USER_NAME = "admin";
    public static final String EVERYONE_GROUP_NAME = "Everyone";
    public static final String SESSION_KEY_USERNAME = "X-WPS-Username";
    public static final String SESSION_KEY_ROLES = "X-WPS-Roles";
    public static final String CSRF_TOKEN_KEY = "X-WPS-CSRFToken";
    public static final String VIEW_REPORT_PREFIX = "VIEW_REPORT_";
    public enum Role { ROLE_USER, ROLE_ADMIN };
    public enum Report { CurrentPasswordExport, Groups, PasswordAccessAudit, PasswordPermissions, Users };
    public enum Function { ADD_USER, UPDATE_USER, ADD_GROUP, UPDATE_GROUP, ADD_PASSWORD,
        BYPASS_PASSWORD_PERMISSIONS, ADD_TEMPLATE, UPDATE_TEMPLATE, BYPASS_TEMPLATE_SHARING,
        VIEW_REPORT_CurrentPasswordExport, VIEW_REPORT_Groups, VIEW_REPORT_PasswordAccessAudit, 
        VIEW_REPORT_PasswordPermissions, VIEW_REPORT_Users };

}
