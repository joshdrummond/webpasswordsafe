/*
    Copyright 2011 Josh Drummond

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
package com.joshdrummond.webpasswordsafe.client.ui;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.joshdrummond.webpasswordsafe.common.util.Constants;


/**
 * @author Josh Drummond
 *
 */
public class AboutDialog extends Dialog
{

    public AboutDialog()
    {
        StringBuilder aboutText = new StringBuilder();
        aboutText.append("<br><center><b>WebPasswordSafe</b><br><b>Version ");
        aboutText.append(Constants.VERSION);
        aboutText.append("</b><br><a target=\"_blank\" href=\"http://www.webpasswordsafe.net\">http://www.webpasswordsafe.net</a><br><br>");
        aboutText.append("Copyright &#169; 2008-2011 Josh Drummond.");
        aboutText.append("<br>All rights reserved. <a target=\"_blank\" href=\"http://webpasswordsafe.googlecode.com/svn/trunk/docs/license.txt\">");
        aboutText.append("GNU General Public License v2</a></center><br><br>");
        setHeading("About");
        setButtons(Dialog.CLOSE);
        addText(aboutText.toString());
        setScrollMode(Scroll.AUTO);
        setHideOnButtonClick(true);
        setModal(true);
    }
}
