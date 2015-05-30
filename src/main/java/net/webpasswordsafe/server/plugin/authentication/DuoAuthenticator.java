/*
    Copyright 2015 Josh Drummond

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

import net.webpasswordsafe.common.util.Constants.AuthenticationStatus;
import net.webpasswordsafe.common.util.Utils;
import net.webpasswordsafe.server.ServerSessionUtil;
import net.webpasswordsafe.server.plugin.authentication.duosecurity.client.Http;
import org.apache.log4j.Logger;
import org.json.JSONObject;


/**
 * @author Josh Drummond
 *
 */
public class DuoAuthenticator implements Authenticator
{
    private static Logger LOG = Logger.getLogger(DuoAuthenticator.class);
    private Authenticator authenticator;
    private String apiHost, ikey, skey, proxyHost, proxyPort;

    @Override
    public AuthenticationStatus authenticate(String principal, String[] credentials)
    {
        AuthenticationStatus authStatus = AuthenticationStatus.FAILURE;
        try
        {
            credentials = parseCredentials(credentials);
            authStatus = authenticator.authenticate(principal, credentials);
            if (AuthenticationStatus.SUCCESS == authStatus)
            {
                authStatus = verifyDuo(principal, credentials[2]) ? AuthenticationStatus.SUCCESS : AuthenticationStatus.FAILURE;
            }
        }
        catch (Exception e)
        {
            LOG.debug("DuoAuthenticator error: "+e.getMessage());
            authStatus = AuthenticationStatus.FAILURE;
        }
        LOG.debug("DuoAuthenticator: login success for "+principal+"? "+authStatus.name());
        return authStatus;
    }
    
    private String[] parseCredentials(String[] originalCredentials)
    {
        String[] modifiedCredentials = new String[3];
        originalCredentials[0] = Utils.safeString(originalCredentials[0]);
        String firstFactor = originalCredentials[0];
        String secondFactor = "";
        int i = originalCredentials[0].lastIndexOf(",");
        if (i >= 0)
        {
            if (i < (originalCredentials[0].length()-1))
            {
                secondFactor = originalCredentials[0].substring(i+1);
            }
            firstFactor = originalCredentials[0].substring(0, i);
        }
        modifiedCredentials[0] = firstFactor;
        modifiedCredentials[1] = originalCredentials[1];
        modifiedCredentials[2] = secondFactor;
        return modifiedCredentials;
    }
    
    private boolean verifyDuo(String username, String passcode)
        throws Exception
    {
        String duoPreAuth = verifyDuoPreAuth(username);
        if (duoPreAuth.equals("auth"))
        {
            return verifyDuoAuth(username, passcode);
        }
        else if (duoPreAuth.equals("allow"))
        {
            return true;
        }
        else //deny or enroll
        {
            return false;
        }
    }
    
    private String verifyDuoPreAuth(String username)
            throws Exception
    {
        Http request = new Http("POST", apiHost, "/auth/v2/preauth");
        request.addParam("username", username);
        request.addParam("ipaddr", ServerSessionUtil.getIP());
        request.signRequest(ikey, skey);
        if (!Utils.safeString(proxyHost).equals(""))
        {
            request.setProxy(Utils.safeString(proxyHost), Utils.safeInt(proxyPort));
        }
        JSONObject result = (JSONObject)request.executeRequest();
        LOG.debug("DuoAuthenticator: preauth for "+username+": "+result.getString("status_msg"));
        return result.getString("result");
    }
        
    private boolean verifyDuoAuth(String username, String passcode)
        throws Exception
    {
        boolean isPasscode = !(passcode.equalsIgnoreCase("push") || passcode.equals(""));
        Http request = new Http("POST", apiHost, "/auth/v2/auth");
        request.addParam("username", username);
        if (isPasscode)
        {
            request.addParam("factor", "passcode");
            request.addParam("passcode", passcode);
        }
        else
        {
            request.addParam("factor", "auto");
            request.addParam("device", "auto");
        }
        request.signRequest(ikey, skey);
        if (!Utils.safeString(proxyHost).equals(""))
        {
            request.setProxy(Utils.safeString(proxyHost), Utils.safeInt(proxyPort));
        }
        JSONObject result = (JSONObject)request.executeRequest();
        return result.getString("result").equals("allow");
    }
    
    public Authenticator getAuthenticator()
    {
        return authenticator;
    }

    public void setAuthenticator(Authenticator authenticator)
    {
        this.authenticator = authenticator;
    }

    public String getApiHost() {
        return apiHost;
    }

    public void setApiHost(String apiHost) {
        this.apiHost = apiHost;
    }

    public String getIkey() {
        return ikey;
    }

    public void setIkey(String ikey) {
        this.ikey = ikey;
    }

    public String getSkey() {
        return skey;
    }

    public void setSkey(String skey) {
        this.skey = skey;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public String getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(String proxyPort) {
        this.proxyPort = proxyPort;
    }

}
