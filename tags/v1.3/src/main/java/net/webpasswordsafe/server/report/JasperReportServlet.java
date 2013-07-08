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
package net.webpasswordsafe.server.report;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.springframework.web.context.support.WebApplicationContextUtils;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRXmlExporter;
import net.sf.jasperreports.engine.type.WhenNoDataTypeEnum;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.webpasswordsafe.common.model.User;
import net.webpasswordsafe.common.util.Constants;
import net.webpasswordsafe.common.util.Utils;
import net.webpasswordsafe.server.plugin.audit.AuditLogger;
import net.webpasswordsafe.server.plugin.authorization.Authorizer;
import net.webpasswordsafe.server.plugin.encryption.Encryptor;


/**
 * Servlet that handles generation of specified reports using Jasper Reports
 * 
 * @author Josh Drummond
 *
 */
public class JasperReportServlet extends HttpServlet
{
    private static Logger LOG = Logger.getLogger(JasperReportServlet.class);
    private static final long serialVersionUID = 2493946517487023931L;
    public static ThreadLocal<Encryptor> encryptorRef = new ThreadLocal<Encryptor>();

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        processRequest(request, response);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest req, HttpServletResponse res)
    {
        OutputStream outputStream = null;
        Connection jdbcConnection = null;
        try
        {
            String reportName = req.getParameter(Constants.NAME);
            String type = req.getParameter(Constants.TYPE).trim().toLowerCase();
            String locale = req.getParameter("locale");
            setNoCache(res);
            if (isAuthorizedReport(req, reportName))
            {
                JasperDesign jasperDesign = JRXmlLoader.load(getServletConfig().getServletContext().getResourceAsStream(
                        "/WEB-INF/reports/"+reportName+".jrxml"));
                JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
                jasperReport.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);
                Map<String, Object> parameters = new HashMap<String, Object>();
                if (null != locale) parameters.put(JRParameter.REPORT_LOCALE, new Locale(locale));
                parameters.put(Constants.SESSION_KEY_USERNAME, (String)req.getSession().getAttribute(Constants.SESSION_KEY_USERNAME));
                parameters.put(Constants.Function.BYPASS_PASSWORD_PERMISSIONS.name(), isAuthorized(req, Constants.Function.BYPASS_PASSWORD_PERMISSIONS.name()) ? "1":"0");
                ReportConfig reportConfig = (ReportConfig)WebApplicationContextUtils.getWebApplicationContext(getServletContext()).getBean("reportConfig");
                @SuppressWarnings("unchecked")
                Enumeration<String> e = req.getParameterNames();
                while (e.hasMoreElements())
                {
                    String param = e.nextElement();
                    if (param.startsWith(Constants.REPORT_PARAM_PREFIX))
                    {
                        String pKey = param.substring(Constants.REPORT_PARAM_PREFIX.length());
                        String pValue = Utils.safeString(req.getParameter(param));
                        if (reportConfig.isDateParam(reportName, pKey))
                        {
                            parameters.put(pKey, convertToDateTime(pValue));
                        }
                        else
                        {
                            parameters.put(pKey, pValue);
                        }
                    }
                }
                encryptorRef.set((Encryptor)WebApplicationContextUtils.getWebApplicationContext(getServletContext()).getBean("encryptor"));
                DataSource dataSource = (DataSource)WebApplicationContextUtils.getWebApplicationContext(getServletContext()).getBean("dataSource");
                jdbcConnection = dataSource.getConnection();
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jdbcConnection);
                JRExporter exporter = null;
                
                if (type.equals(Constants.REPORT_TYPE_PDF))
                {
                    res.setContentType("application/pdf");
                    exporter = new JRPdfExporter();
                }
                else if (type.equals("rtf"))
                {
                    res.setContentType("application/rtf");
                    exporter = new JRRtfExporter();
                }
                else if (type.equals(Constants.REPORT_TYPE_CSV))
                {
                    res.setContentType("text/csv");
                    exporter = new JRCsvExporter();
                }
                else if (type.equals("xml"))
                {
                    res.setContentType("text/xml");
                    exporter = new JRXmlExporter();
                }
                
                if (exporter != null)
                {
                    DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                    res.setHeader("Content-Disposition", "attachment; filename=" + reportName +
                            dateFormat.format(System.currentTimeMillis())+"."+type);
                    
                    exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                    outputStream = res.getOutputStream();
                    exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
                    exporter.exportReport();
                }
                else
                {
                    throw new RuntimeException("Invalid report type: "+type);
                }
            }
        }
        catch(Exception e)
        {
            LOG.error("JasperReportServlet Error " + e.getMessage(), e);
        }
        finally
        {
            // close the output stream
            if (outputStream != null)
            {
                try
                {
                    outputStream.close();
                }
                catch (IOException io)
                {
                    LOG.error("JasperReportServlet Error " + io.getMessage(), io);
                }
            }
            // close the db connection
            if (jdbcConnection != null)
            {
                try
                {
                    jdbcConnection.close();
                }
                catch (SQLException sql)
                {
                    LOG.error("JasperReportServlet Error "+sql.getMessage(), sql);
                }
                finally
                {
                    jdbcConnection = null;
                }
            }
        }
    }
    
    private Timestamp convertToDateTime(String pValue)
        throws ParseException
    {
        Timestamp pDateValue = null;
        if (!"".equals(pValue))
        {
            // try with timestamp
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            try
            {
                pDateValue = new Timestamp(dateFormat.parse(pValue).getTime());
            }
            catch (ParseException pe)
            {
                // try without timestamp
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                pDateValue = new Timestamp(dateFormat.parse(pValue).getTime());
                // throw exception if neither parsed
            }
        }
        return pDateValue;
    }
    
    private boolean isAuthorizedReport(HttpServletRequest req, String reportName)
    {
        boolean isAuthorized = isAuthorized(req, Constants.VIEW_REPORT_PREFIX+reportName);
        User user = new User();
        user.setUsername((String)req.getSession().getAttribute(Constants.SESSION_KEY_USERNAME));
        AuditLogger auditLogger = (AuditLogger)WebApplicationContextUtils.getWebApplicationContext(getServletContext()).getBean("auditLogger");
        auditLogger.log(new Date(), user.getUsername(), req.getRemoteAddr(), "view report", reportName, isAuthorized, (isAuthorized ? "" : "not authorized"));
        return isAuthorized;
    }
    
    @SuppressWarnings("unchecked")
    private boolean isAuthorized(HttpServletRequest req, String action)
    {
        boolean isAuthorized = false;
        Authorizer authorizer = (Authorizer)WebApplicationContextUtils.getWebApplicationContext(getServletContext()).getBean("authorizer");
        User user = new User();
        user.setUsername((String)req.getSession().getAttribute(Constants.SESSION_KEY_USERNAME));
        user.setRoles((Set<Constants.Role>)req.getSession().getAttribute(Constants.SESSION_KEY_ROLES));
        try
        {
            isAuthorized = authorizer.isAuthorized(user, action);
        }
        catch (Exception e)
        {
            isAuthorized = false;
        }
        return isAuthorized;
    }
    
    private void setNoCache(HttpServletResponse res)
    {
        res.setHeader("Pragma", "no-cache");
        res.setHeader("Cache-Control", "no-cache,no-store");
        res.setDateHeader("Expires", 0);
    }
}
