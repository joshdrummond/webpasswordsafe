/*
    Copyright 2008-2013 Josh Drummond

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

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 * @author Josh Drummond
 *
 */
public class JasyptBCAESEncryptionTest
{

    @Test
    public void testEncrypt()
    {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setProvider(new BouncyCastleProvider());
        encryptor.setAlgorithm("PBEWITHSHA256AND256BITAES-CBC-BC");
        encryptor.setPassword("w3bp@$$w0rd$@f3k3y");
        encryptor.setKeyObtentionIterations(1000);

        String clearText = "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890";
        System.out.println("clearText.length="+clearText.length());
        System.out.println("clearText="+clearText);
        String encryptedText = encryptor.encrypt(clearText);
        System.out.println("encryptedText.length="+encryptedText.length());
        System.out.println("encryptedText="+encryptedText);
        assertEquals(encryptedText.length(), 172);
        String decryptedText = encryptor.decrypt(encryptedText);
        assertEquals(decryptedText, clearText);
    }
}
