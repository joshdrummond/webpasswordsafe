/*
    Copyright 2011-2013 Josh Drummond

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
package net.webpasswordsafe.client.ui;

import net.webpasswordsafe.client.i18n.TextMessages;
import net.webpasswordsafe.common.util.Constants;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.google.gwt.core.client.GWT;


/**
 * @author Josh Drummond
 *
 */
public class AboutDialog extends Dialog
{
    private final static TextMessages textMessages = GWT.create(TextMessages.class);

    public AboutDialog()
    {
        StringBuilder aboutText = new StringBuilder();
        aboutText.append("<br><center><b>");
        aboutText.append(textMessages.webpasswordsafe());
        aboutText.append("</b><br><b>");
        aboutText.append(textMessages.version(Constants.VERSION));
        aboutText.append("</b><br><a target=\"_blank\" href=\"");
        aboutText.append(Constants.URL_WEBPASSWORDSAFE);
        aboutText.append("\">");
        aboutText.append(Constants.URL_WEBPASSWORDSAFE);
        aboutText.append("</a><br><br>");
        aboutText.append(textMessages.copyrightBy(Constants.COPYRIGHT));
        aboutText.append("<br>");
        aboutText.append(textMessages.allRightsReserved());
        aboutText.append(" <a target=\"_blank\" href=\"");
        aboutText.append(GWT.getHostPageBaseURL() + Constants.URL_LICENSE);
        aboutText.append("\">");
        aboutText.append(textMessages.gpl2());
        aboutText.append("</a></center><br><br>");
        setHeading(textMessages.about());
        setButtons(Dialog.CLOSE);
        addText(aboutText.toString());
        setScrollMode(Scroll.AUTO);
        setHideOnButtonClick(true);
        setModal(true);
    }
}
