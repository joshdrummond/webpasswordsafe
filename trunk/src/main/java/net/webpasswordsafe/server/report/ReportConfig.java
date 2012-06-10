/*
    Copyright 2012 Josh Drummond

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
package net.webpasswordsafe.server.report;

import java.util.List;
import java.util.Map;
import net.webpasswordsafe.common.util.Constants;


/**
 * @author Josh Drummond
 *
 */
public class ReportConfig
{
    private List<Map<String, Object>> reports;

    public List<Map<String, Object>> getReports()
    {
        return reports;
    }

    public void setReports(List<Map<String, Object>> reports)
    {
        this.reports = reports;
    }
    
    public Map<String, Object> getReport(String name)
    {
        for (Map<String, Object> report : reports)
        {
            if (name.equals((String)report.get(Constants.NAME)))
            {
                return report;
            }
        }
        return null;
    }
}
