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
package net.webpasswordsafe.server.plugin.encryption;

import org.apache.log4j.Logger;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Base64;
import org.owasp.esapi.crypto.CipherText;
import org.owasp.esapi.crypto.PlainText;
import org.owasp.esapi.errors.EncryptionException;


/**
 * @author Josh Drummond
 *
 */
public class EsapiEncryptor implements Encryptor
{
    private static Logger LOG = Logger.getLogger(EsapiEncryptor.class);

    public EsapiEncryptor(boolean useClasspath, String esapiResourceDir)
    {
        if (useClasspath)
        {
            ESAPI.securityConfiguration().setResourceDirectory(esapiResourceDir); 
        }
        else
        {
            System.setProperty("org.owasp.esapi.resources", esapiResourceDir);
        }
    }
    
    /* (non-Javadoc)
     * @see net.webpasswordsafe.server.plugin.encryption.Encryptor#decrypt(java.lang.String)
     */
    @Override
    public String decrypt(String cryptedText)
    {
        String clearText = null;
        try
        {
            CipherText cipherText = CipherText.fromPortableSerializedBytes(Base64.decode(cryptedText));
            clearText = ESAPI.encryptor().decrypt(cipherText).toString();
        }
        catch (EncryptionException e)
        {
            LOG.error("EsapiEncryptor.decrypt: "+e.getMessage(), e);
        }
        return clearText;
    }

    /* (non-Javadoc)
     * @see net.webpasswordsafe.server.plugin.encryption.Encryptor#encrypt(java.lang.String)
     */
    @Override
    public String encrypt(String clearText)
    {
        String cryptedText = null;
        try
        {
            CipherText cipherText = ESAPI.encryptor().encrypt(new PlainText(clearText));
            cryptedText = Base64.encodeBytes(cipherText.asPortableSerializedByteArray());
        }
        catch (EncryptionException e)
        {
            LOG.error("EsapiEncryptor.encrypt: "+e.getMessage(), e);
        }
        return cryptedText;
    }

}
