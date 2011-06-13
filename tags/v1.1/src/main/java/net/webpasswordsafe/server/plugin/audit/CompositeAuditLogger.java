/*
    Copyright 2010-2011 Josh Drummond

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
package net.webpasswordsafe.server.plugin.audit;

import java.util.Date;
import java.util.List;


/**
 * @author Josh Drummond
 *
 */
public class CompositeAuditLogger implements AuditLogger
{
    private List<AuditLogger> auditLoggers;
    
    @Override
    public void log(Date date, String user, String ip, String action, String target, boolean status, String message)
    {
        if (auditLoggers != null)
        {
            for (AuditLogger auditLogger : auditLoggers)
            {
                auditLogger.log(date, user, ip, action, target, status, message);
            }
        }
    }

    public List<AuditLogger> getAuditLoggers()
    {
        return auditLoggers;
    }

    public void setAuditLoggers(List<AuditLogger> auditLoggers)
    {
        this.auditLoggers = auditLoggers;
    }

}
