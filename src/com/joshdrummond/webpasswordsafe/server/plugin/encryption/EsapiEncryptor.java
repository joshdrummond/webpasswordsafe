/*
    Copyright 2009 Josh Drummond

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
package com.joshdrummond.webpasswordsafe.server.plugin.encryption;

import org.apache.log4j.Logger;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.EncryptionException;


/**
 * @author Josh Drummond
 *
 */
public class EsapiEncryptor implements Encryptor
{
    private static Logger LOG = Logger.getLogger(EsapiEncryptor.class);

    /* (non-Javadoc)
     * @see com.joshdrummond.webpasswordsafe.server.plugin.encryption.Encryptor#decrypt(java.lang.String)
     */
    @SuppressWarnings("deprecation")
    public String decrypt(String cryptedText)
    {
        String clearText = null;
        try
        {
            clearText = ESAPI.encryptor().decrypt(cryptedText);
        }
        catch (EncryptionException e)
        {
            LOG.error("EsapiEncryptor.decrypt: "+e.getMessage(), e);
        }
        return clearText;
    }

    /* (non-Javadoc)
     * @see com.joshdrummond.webpasswordsafe.server.plugin.encryption.Encryptor#encrypt(java.lang.String)
     */
    @SuppressWarnings("deprecation")
    public String encrypt(String clearText)
    {
        String cryptedText = null;
        try
        {
            cryptedText = ESAPI.encryptor().encrypt(clearText);
        }
        catch (EncryptionException e)
        {
            LOG.error("EsapiEncryptor.encrypt: "+e.getMessage(), e);
        }
        return cryptedText;
    }

    public void setEsapiResources(String esapiResources)
    {
        System.setProperty("org.owasp.esapi.resources", esapiResources);
    }

}
