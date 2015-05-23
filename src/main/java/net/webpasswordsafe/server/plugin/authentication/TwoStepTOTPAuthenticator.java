/*
    Copyright 2013 Josh Drummond

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
package net.webpasswordsafe.server.plugin.authentication;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.annotation.Resource;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import net.webpasswordsafe.common.model.User;
import net.webpasswordsafe.common.model.UserAuthnTOTP;
import net.webpasswordsafe.common.util.Constants.AuthenticationStatus;
import net.webpasswordsafe.common.util.Utils;
import net.webpasswordsafe.server.dao.UserDAO;
import net.webpasswordsafe.server.plugin.encryption.Encryptor;
import org.apache.commons.codec.binary.Base32;
import org.apache.log4j.Logger;


/**
 * @author Josh Drummond
 *
 */
public class TwoStepTOTPAuthenticator implements Authenticator
{
    private static Logger LOG = Logger.getLogger(TwoStepTOTPAuthenticator.class);
    private Authenticator authenticator;
    private int variance;
    @Resource
    private UserDAO userDAO;
    @Resource
    private Encryptor encryptor;

    @Override
    public AuthenticationStatus authenticate(String principal, String[] credentials)
    {
        AuthenticationStatus authStatus = AuthenticationStatus.FAILURE;
        try
        {
            authStatus = authenticator.authenticate(principal, credentials);
            if (AuthenticationStatus.SUCCESS == authStatus)
            {
                //if user totp enabled...
                User user = userDAO.findActiveUserByUsername(principal);
                if (null != user)
                {
                    UserAuthnTOTP userAuthnTOTP = user.getAuthnTOTPValue();
                    if (null != userAuthnTOTP)
                    {
                        if (userAuthnTOTP.isEnabled())
                        {
                            if (Utils.safeString(credentials[1]).equals(""))
                            {
                                authStatus = AuthenticationStatus.TWO_STEP_REQ;
                            }
                            else
                            {
                                String key = encryptor.decrypt(userAuthnTOTP.getKey());
                                int code = Utils.safeInt(credentials[1]);
                                long t = getTimeIndex();
                                authStatus = verifyCode(key, code, t, variance) ? AuthenticationStatus.SUCCESS : AuthenticationStatus.FAILURE;
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            LOG.debug("TwoStepTOTPAuthenticator error: "+e.getMessage());
            authStatus = AuthenticationStatus.FAILURE;
        }
        LOG.debug("TwoStepTOTPAuthenticator: login success for "+principal+"? "+authStatus.name());
        return authStatus;
    }
    
    private long getTimeIndex()
    {
        return System.currentTimeMillis() / 1000 / 30;
    }

    public static String generateKey()
    {
        byte[] buffer = new byte[10];
        new SecureRandom().nextBytes(buffer);
        Base32 codec = new Base32();
        return new String(codec.encode(buffer));
    }

    private boolean verifyCode(String secret, int code, long t, int variance)
        throws NoSuchAlgorithmException, InvalidKeyException
    {
        Base32 codec = new Base32();
        byte[] decodedKey = codec.decode(secret);
        for (int i = -variance; i <= variance; i++)
        {
            int hash = calculateCode(decodedKey, t+i);
            if (hash == code) return true;
        }
        return false;
    }

    private int calculateCode(byte[] key, long t)
        throws NoSuchAlgorithmException, InvalidKeyException
    {
        byte[] data = new byte[8];
        long value = t;
        for (int i = 8; i-- > 0; value >>>= 8)
        {
            data[i] = (byte)value;
        }
        SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signKey);
        byte[] hash = mac.doFinal(data);
        int offset = hash[20 - 1] & 0xF;
        long truncatedHash = 0;
        for (int i = 0; i < 4; ++i)
        {
            truncatedHash <<= 8;
            truncatedHash |= (hash[offset + i] & 0xFF);
        }
        truncatedHash &= 0x7FFFFFFF;
        truncatedHash %= 1000000;
        return (int)truncatedHash;
    }
    
    public Authenticator getAuthenticator()
    {
        return authenticator;
    }

    public void setAuthenticator(Authenticator authenticator)
    {
        this.authenticator = authenticator;
    }

    public int getVariance()
    {
        return variance;
    }

    public void setVariance(int variance)
    {
        this.variance = variance;
    }

}
