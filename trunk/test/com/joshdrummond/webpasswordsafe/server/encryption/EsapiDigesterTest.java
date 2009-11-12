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
package com.joshdrummond.webpasswordsafe.server.encryption;

import static org.junit.Assert.assertTrue;
import org.junit.Test;


/**
 * @author Josh Drummond
 *
 */
public class EsapiDigesterTest
{

    /**
     * Test method for {@link com.joshdrummond.webpasswordsafe.server.encryption.JasyptDigester#check(java.lang.String, java.lang.String)}.
     */
    @Test
    public void testCheck()
    {
        EsapiDigester digester = new EsapiDigester();
        digester.setEsapiResources("/Users/josh/Documents/workspace/webpasswordsafe/war/WEB-INF/esapi");
        String password1 = digester.digest("1234567890123456789012345678901234567890123456789012345678901234");
        System.out.println("length="+password1.length());
        System.out.println("password1="+password1);
        String password2 = digester.digest("1234567890123456789012345678901234567890123456789012345678901234");
        System.out.println("password2="+password2);
        assertTrue(digester.check("1234567890123456789012345678901234567890123456789012345678901234", password1));
        assertTrue(digester.check("1234567890123456789012345678901234567890123456789012345678901234", password2));
        assertTrue(password1.equals(password2));
    }

}
