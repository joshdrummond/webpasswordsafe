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
package net.webpasswordsafe.server.service.helper;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import com.google.gwt.user.server.rpc.SerializationPolicy;
import com.google.gwt.user.server.rpc.SerializationPolicyLoader;


/**
 * Loads serialization policy file from default location
 * 
 * @author Josh Drummond
 *
 */
public class SerializationPolicyUtil
{
    public static SerializationPolicy loadSerializationPolicy(HttpServlet servlet,
        HttpServletRequest request, String moduleBaseURL, String strongName)
    {
        SerializationPolicy serializationPolicy = null;
        String serializationPolicyFilePath = SerializationPolicyLoader.getSerializationPolicyFileName("/webpasswordsafe/" + strongName);
 
        // Open the RPC resource file and read its contents.
        InputStream is = servlet.getServletContext().getResourceAsStream(serializationPolicyFilePath);
        try {
            if (is != null) {
                try {
                    serializationPolicy = SerializationPolicyLoader.loadFromStream(is, null);
                } catch (ParseException e) {
                    servlet.log("ERROR: Failed to parse the policy file '" + serializationPolicyFilePath + "'", e);
                } catch (IOException e) {
                    servlet.log("ERROR: Could not read the policy file '" + serializationPolicyFilePath + "'", e);
                }
            } else {
              String message = "ERROR: The serialization policy file '" + serializationPolicyFilePath +
                  "' was not found; did you forget to include it in this deployment?";
              servlet.log(message);
            }
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // Ignore this error
                }
            }
        }
        return serializationPolicy;
    }
}
