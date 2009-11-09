/*
    Copyright 2008-2009 Josh Drummond

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
package com.joshdrummond.webpasswordsafe.common.model;


/**
 * @author Josh Drummond
 *
 */
public enum AccessLevel 
{
	NONE(0), READ(1), WRITE(2), GRANT(3);
	private int id;
	private AccessLevel(int id) { this.id = id; }
	public int getId() { return id; }
	public String getName()
	{
	    switch (id)
	    {
	       case 1 : return "READ";
	       case 2 : return "WRITE";
	       case 3 : return "GRANT";
	       default : return "NONE";
	    }
	}
	@Override
	public String toString()
	{
	    return getName();
	}
	public static AccessLevel valueOf(int id)
	{
		switch (id)
		{
		case 1 : return READ;
		case 2 : return WRITE;
		case 3 : return GRANT;
		default : return NONE;
		}
	}
}