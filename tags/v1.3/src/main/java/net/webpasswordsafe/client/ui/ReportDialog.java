/*
    Copyright 2012-2013 Josh Drummond

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.webpasswordsafe.client.i18n.TextMessages;
import net.webpasswordsafe.common.util.Constants;
import net.webpasswordsafe.common.util.Utils;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.HiddenField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;


/**
 * @author Josh Drummond
 *
 */
public class ReportDialog extends Window
{
    private FormPanel form;
    private FormData formData = new FormData("-20"); 
    private final static TextMessages textMessages = GWT.create(TextMessages.class);
    private Map<String, Map<String, Object>> paramFields;
    private static final String PARAM = "param";
    private static final String FIELD = "field";

    @SuppressWarnings("unchecked")
    public ReportDialog(Map<String, Object> report)
    {
        this.setHeading((String)report.get(Constants.I18N));
        this.setModal(true);
        setWidth("340");
        form = new FormPanel();
        form.setAction(GWT.getHostPageBaseURL()+"report");
        form.setMethod(Method.POST);
        form.setHeaderVisible(false);
        form.setFrame(true);
        // display parameter inputs
        List<Map<String, Object>> params = (List<Map<String, Object>>)report.get(Constants.PARAMETERS);
        paramFields = new HashMap<String, Map<String,Object>>(params.size());
        for (Map<String, Object> param : params)
        {
            Map<String, Object> paramField = new HashMap<String, Object>();
            paramField.put(PARAM, param);
            String type = (String)param.get(Constants.TYPE);
            if (type.equals(Constants.BOOLEAN))
            {
                SimpleComboBox<String> input = new SimpleComboBox<String>();
                input.add(" ");
                input.add("Y");
                input.add("N");
                input.setEditable(false);
                input.setForceSelection(true);
                input.setTriggerAction(TriggerAction.ALL);
                input.setName(Constants.REPORT_PARAM_PREFIX+(String)param.get(Constants.NAME));
                input.setFieldLabel((String)param.get(Constants.I18N));
                form.add(input);
                paramField.put(FIELD, input);
            }
            else //if (type.equals(Constants.DATE) || type.equals(Constants.TEXT))
            {
                TextField<String> input = new TextField<String>();
                input.setName(Constants.REPORT_PARAM_PREFIX+(String)param.get(Constants.NAME));
                input.setFieldLabel((String)param.get(Constants.I18N));
                form.add(input);
                paramField.put(FIELD, input);
            }
            paramFields.put((String)param.get(Constants.NAME), paramField);
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
        typeRG.setFieldLabel(textMessages.type());
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

    @SuppressWarnings("unchecked")
    private boolean validateFields()
    {
        for (String fieldKey : paramFields.keySet())
        {
            Map<String, Object> paramField = paramFields.get(fieldKey);
            String type = (String)((Map<String, Object>)paramField.get(PARAM)).get(Constants.TYPE);
            if (type.equals(Constants.DATE))
            {
                String value = Utils.safeString(((TextField<String>)paramField.get(FIELD)).getValue());
                if (!"".equals(value) && !isValidDateInput(value))
                {
                    MessageBox.alert(textMessages.error(), textMessages.invalidDate(), null);
                    return false;
                }
            }
        }
        return true;
    }
    
    private boolean isValidDateInput(String value)
    {
        return isValidDate(value, "yyyy-MM-dd") || isValidDate(value, "yyyy-MM-dd HH:mm");
    }
    
    private boolean isValidDate(String value, String format)
    {
        boolean isValid = false;
        try
        {
            DateTimeFormat df = DateTimeFormat.getFormat(format);
            df.parseStrict(value);
            isValid = true;
        }
        catch (IllegalArgumentException e)
        {
        }
        return isValid;
    }

}
