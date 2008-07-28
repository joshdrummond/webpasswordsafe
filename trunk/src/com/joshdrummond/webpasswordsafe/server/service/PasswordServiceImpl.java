/*
    Copyright 2008 Josh Drummond

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
package com.joshdrummond.webpasswordsafe.server.service;

import java.util.Date;

import org.apache.log4j.Logger;
import org.gwtwidgets.server.spring.ServletUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.joshdrummond.webpasswordsafe.client.model.common.PasswordDTO;
import com.joshdrummond.webpasswordsafe.client.remote.PasswordService;
import com.joshdrummond.webpasswordsafe.server.assembler.PasswordAssembler;
import com.joshdrummond.webpasswordsafe.server.dao.PasswordDAO;
import com.joshdrummond.webpasswordsafe.server.dao.UserDAO;
import com.joshdrummond.webpasswordsafe.server.model.Password;
import com.joshdrummond.webpasswordsafe.server.model.User;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * 
 * @author Josh Drummond
 *
 */
public class PasswordServiceImpl extends RemoteServiceServlet implements PasswordService {

    private static final long serialVersionUID = -9164403179286398287L;
    private static Logger LOG = Logger.getLogger(PasswordServiceImpl.class);
    private PasswordDAO passwordDAO;
    private UserDAO userDAO;

    @Transactional(propagation=Propagation.REQUIRED)
    public void addPassword(PasswordDTO passwordDTO)
    {
        Date now = new Date();
        Password password = PasswordAssembler.createDO(passwordDTO);
        User loggedInUser = userDAO.findActiveUserByUsername((String)ServletUtils.getRequest().getSession().getAttribute("username"));
        password.setUserCreated(loggedInUser);
        password.setDateCreated(now);
        password.setUserLastUpdate(loggedInUser);
        password.setDateLastUpdate(now);
        password.getPasswordData().get(0).setUserCreated(loggedInUser);
        password.getPasswordData().get(0).setDateCreated(now);
        passwordDAO.makePersistent(password);
        LOG.info(passwordDTO.getName() + " added");
    }

    @Transactional(propagation=Propagation.REQUIRED)
    public void updatePassword(PasswordDTO password)
    {
        LOG.debug("updating password");
    }

    public PasswordDAO getPasswordDAO()
    {
        return this.passwordDAO;
    }

    public void setPasswordDAO(PasswordDAO passwordDAO)
    {
        this.passwordDAO = passwordDAO;
    }

    public UserDAO getUserDAO()
    {
        return this.userDAO;
    }

    public void setUserDAO(UserDAO userDAO)
    {
        this.userDAO = userDAO;
    }
    
    
}
