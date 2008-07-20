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
package com.joshdrummond.webpasswordsafe.server.encryption;

import org.jasypt.util.text.TextEncryptor;

/**
 * @author Josh Drummond
 *
 */
public class JasyptEncryptor implements Encryptor
{
    private TextEncryptor textEncryptor;

    /* (non-Javadoc)
     * @see com.joshdrummond.webpasswordsafe.server.encryption.Encryptor#decrypt(java.lang.String)
     */
    public String decrypt(String cryptedText)
    {
        return textEncryptor.decrypt(cryptedText);
    }

    /* (non-Javadoc)
     * @see com.joshdrummond.webpasswordsafe.server.encryption.Encryptor#encrypt(java.lang.String)
     */
    public String encrypt(String clearText)
    {
        return textEncryptor.encrypt(clearText);
    }

    public TextEncryptor getTextEncryptor()
    {
        return this.textEncryptor;
    }

    public void setTextEncryptor(TextEncryptor textEncryptor)
    {
        this.textEncryptor = textEncryptor;
    }

}
