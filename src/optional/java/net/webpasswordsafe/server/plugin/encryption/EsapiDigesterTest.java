/*
    Copyright 2009-2013 Josh Drummond

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

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import net.webpasswordsafe.server.plugin.encryption.EsapiDigester;


/**
 * @author Josh Drummond
 *
 */
public class EsapiDigesterTest
{

    /**
     * Test method for {@link net.webpasswordsafe.server.plugin.encryption.JasyptDigester#check(java.lang.String, java.lang.String)}.
     */
    @Test
    public void testCheck()
    {
        EsapiDigester digester = new EsapiDigester(true, "esapi");
        String clearText = "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890";
        System.out.println("clearText="+clearText);
        System.out.println("clearText.length="+clearText.length());
        String password1 = digester.digest(clearText);
        System.out.println("password1="+password1);
        System.out.println("password1.length="+password1.length());
        String password2 = digester.digest(clearText);
        System.out.println("password2="+password2);
        System.out.println("password2.length="+password2.length());
        assertTrue(digester.check(clearText, password1));
        assertTrue(digester.check(clearText, password2));
        assertTrue(password1.equals(password2));
    }

}
