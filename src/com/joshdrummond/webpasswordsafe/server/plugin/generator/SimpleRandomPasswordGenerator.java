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
package com.joshdrummond.webpasswordsafe.server.plugin.generator;

import java.util.Random;

/**
 * Default implementation of PasswordGenerator
 * 
 * @author Josh Drummond
 *
 */
public class SimpleRandomPasswordGenerator implements PasswordGenerator
{
    private int passwordLength;
    
    /**
     * Constructor creating default length of 10
     */
    public SimpleRandomPasswordGenerator()
    {
        passwordLength = 10;
    }
    
    /* (non-Javadoc)
     * @see com.joshdrummond.webpasswordsafe.server.plugin.generator.PasswordGenerator#generatePassword()
     */
    public String generatePassword()
    {
        Random random = new Random();
        StringBuilder password = new StringBuilder(passwordLength);
        for (int i = 0; i < passwordLength; i++)
        {
            password.append((char)(random.nextInt(92) + 33));
        }
        return password.toString();
    }

    public int getPasswordLength()
    {
        return this.passwordLength;
    }

    public void setPasswordLength(int passwordLength)
    {
        this.passwordLength = passwordLength;
    }

}
