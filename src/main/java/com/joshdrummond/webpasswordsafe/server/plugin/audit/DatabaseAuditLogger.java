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
package com.joshdrummond.webpasswordsafe.server.plugin.audit;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.joshdrummond.webpasswordsafe.common.model.AuditLog;
import com.joshdrummond.webpasswordsafe.server.dao.AuditLogDAO;


/**
 * @author Josh Drummond
 *
 */
public class DatabaseAuditLogger implements AuditLogger
{
    @Autowired
    private AuditLogDAO auditLogDAO;

    @Override
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public void log(Date date, String user, String ip, String action, String target, boolean status, String message)
    {
        AuditLog auditLog = new AuditLog(date, user, ip, action, target, status, message);
        auditLogDAO.makePersistent(auditLog);
    }

}
