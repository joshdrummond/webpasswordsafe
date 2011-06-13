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
import org.owasp.esapi.errors.EncryptionException;


/**
 * @author Josh Drummond
 *
 */
public class EsapiDigester implements Digester
{
    private static Logger LOG = Logger.getLogger(EsapiDigester.class);

    public EsapiDigester(boolean useClasspath, String esapiResourceDir)
    {
        try
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
        catch (Exception e)
        {
            LOG.error(e.getMessage(), e);
        }
    }

    /* (non-Javadoc)
     * @see net.webpasswordsafe.server.plugin.encryption.Digester#check(java.lang.String, java.lang.String)
     */
    @Override
    public boolean check(String clearText, String cryptedText)
    {
        return cryptedText.equals(digest(clearText));
    }

    /* (non-Javadoc)
     * @see net.webpasswordsafe.server.plugin.encryption.Digester#digest(java.lang.String)
     */
    @Override
    public String digest(String clearText)
    {
        String cryptedText = null;
        try
        {
            cryptedText = ESAPI.encryptor().hash(clearText, clearText);
        }
        catch (EncryptionException e)
        {
            LOG.error("EsapiDigester.digest: "+e.getMessage(), e);
        }
        return cryptedText;
    }
    
}
