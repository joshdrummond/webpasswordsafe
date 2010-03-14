/*
    Copyright 2010 Josh Drummond

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
package com.joshdrummond.webpasswordsafe.server.report;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Josh Drummond
 *
 */
public class ReportAccessControl
{
    private Map<String, String> reportRoles;

    public ReportAccessControl()
    {
        reportRoles = new HashMap<String, String>();
    }

    public boolean isReportExist(String reportName)
    {
        return reportRoles.containsKey(reportName);
    }

    public String getReportRole(String reportName)
    {
        return reportRoles.get(reportName);
    }

    public Map<String, String> getReportRoles()
    {
        return this.reportRoles;
    }

    public void setReportRoles(Map<String, String> reportRoles)
    {
        this.reportRoles = reportRoles;
    }
    
}
