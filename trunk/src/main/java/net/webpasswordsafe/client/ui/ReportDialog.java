/*
    Copyright 2012 Josh Drummond

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.webpasswordsafe.client.i18n.TextMessages;
import net.webpasswordsafe.common.util.Constants;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.HiddenField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.core.client.GWT;


/**
 * @author Josh Drummond
 *
 */
public class ReportDialog extends Window
{
    private FormPanel form;
    private FormData formData = new FormData("-20"); 
    private final static TextMessages textMessages = GWT.create(TextMessages.class);

    @SuppressWarnings("unchecked")
	public ReportDialog(Map<String, Object> report)
    {
        this.setHeading((String)report.get(Constants.I18N));
        this.setModal(true);
        
        form = new FormPanel();
        form.setAction(GWT.getHostPageBaseURL()+"report");
        form.setMethod(Method.POST);
        form.setHeaderVisible(false);
        form.setFrame(true);
        // display parameter inputs
        List<Map<String, Object>> params = (List<Map<String, Object>>)report.get(Constants.PARAMETERS);
        List<TextField<String>> inputs = new ArrayList<TextField<String>>(params.size());
        for (Map<String, Object> param : params)
        {
            TextField<String> input = new TextField<String>();
            input.setName((String)param.get(Constants.NAME));
            input.setAllowBlank(!Boolean.valueOf((String)param.get(Constants.REQUIRED)));
            input.setFieldLabel((String)param.get(Constants.I18N));
            inputs.add(input);
            form.add(input);
        }
        // create type choice
        Radio radioPDF = new Radio();
        radioPDF.setBoxLabel(Constants.REPORT_TYPE_PDF);
        radioPDF.setValue(true);
        radioPDF.setValueAttribute(Constants.REPORT_TYPE_PDF);
        Radio radioCSV = new Radio();
        radioCSV.setBoxLabel(Constants.REPORT_TYPE_CSV);
        radioCSV.setValueAttribute(Constants.REPORT_TYPE_CSV);
        RadioGroup typeRG = new RadioGroup(Constants.TYPE);
        typeRG.add(radioPDF);
        typeRG.add(radioCSV);
        typeRG.setFieldLabel("Type");
        form.add(typeRG, formData);
        // hidden params
        HiddenField<String> f1 = new HiddenField<String>();
        f1.setName(Constants.NAME);
        f1.setValue((String)report.get(Constants.NAME));
        form.add(f1);

        Button generateButton = new Button(textMessages.submit(), new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                doGenerate();
            }
        });
        Button closeButton = new Button(textMessages.close(), new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                doClose();
            }
        });
        form.setButtonAlign(HorizontalAlignment.CENTER);
        form.addButton(generateButton);
        form.addButton(closeButton);
        form.setScrollMode(Scroll.AUTO);
        this.add(form);
    }

    private void doGenerate()
    {
        if (validateFields())
        {
            form.submit();
        }
    }
    
    private void doClose()
    {
        hide();
    }

    private boolean validateFields()
    {
        /*
        if (Utils.safeString(password1.getValue()).equals(""))
        {
            MessageBox.alert(textMessages.error(), textMessages.mustEnterPassword(), null);
            return false;
        }
        */
        return true;
    }

}
