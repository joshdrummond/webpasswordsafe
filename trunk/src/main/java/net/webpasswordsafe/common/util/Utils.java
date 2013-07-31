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
package net.webpasswordsafe.common.util;


/**
 * Common utility methods
 * 
 * @author Josh Drummond
 *
 */
public class Utils
{

    public static String safeString(Object o)
    {
        return (null != o) ? o.toString().trim() : "";
    }
    
    public static int safeInt(Object o)
    {
        int num = -1;
        String s = safeString(o);
        try
        {
            if (!"".equals(s))
            {
                num = Integer.parseInt(s);
            }
        }
        catch (NumberFormatException e)
        {
            num = -1;
        }
        return num;
    }
    
    public static boolean isValidEmail(String s)
    {
        return s.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"); 
    }
}
