/*
    Copyright 2009-2010 Josh Drummond

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
package com.joshdrummond.webpasswordsafe.server.report;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
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
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRXmlExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;


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


    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        processRequest(request, response);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        processRequest(request, response);
    }


    public void processRequest(HttpServletRequest req, HttpServletResponse res)
    {
        OutputStream outputStream = null;
        Connection jdbcConnection = null;
        try
        {
            String reportName = req.getParameter("name");
            String type = req.getParameter("type").trim().toLowerCase();
            if (isAuthorized(req, reportName))
            {
                JasperDesign jasperDesign = JRXmlLoader.load(getServletConfig().getServletContext().getResourceAsStream(
                        "/WEB-INF/reports/"+reportName+".jrxml"));
                JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
                Map<String, String> parameters = new HashMap<String, String>();
                DataSource dataSource = (DataSource)WebApplicationContextUtils.getWebApplicationContext(getServletContext()).getBean("dataSource");
                jdbcConnection = dataSource.getConnection();
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jdbcConnection);
                JRExporter exporter = null;
                
                if (type.equals("pdf"))
                {
                    res.setContentType("application/pdf");
                    exporter = new JRPdfExporter();
                }
                else if (type.equals("rtf"))
                {
                    res.setContentType("application/rtf");
                    exporter = new JRRtfExporter();
                }
                else if (type.equals("csv"))
                {
                    res.setContentType("text/csv");
                    exporter = new JRCsvExporter();
                }
                else if (type.equals("xml"))
                {
                    res.setContentType("text/xml");
                    exporter = new JRXmlExporter();
                }
                DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                res.setHeader("Content-Disposition", "attachment; filename=" + reportName +
                        dateFormat.format(System.currentTimeMillis())+"."+type);
                
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                outputStream = res.getOutputStream();
                exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
                exporter.exportReport();
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
    
    @SuppressWarnings("unchecked")
    private boolean isAuthorized(HttpServletRequest req, String reportName)
    {
        boolean isAuthorized = false;
        ReportAccessControl reportAccessControl = (ReportAccessControl)WebApplicationContextUtils.getWebApplicationContext(getServletContext()).getBean("reportAccessControl");
        String username = (String)req.getSession().getAttribute("username");

        if (reportAccessControl.isReportExist(reportName))
        {
            Set<String> userRoles = (Set<String>)req.getSession().getAttribute("roles");
            isAuthorized = userRoles.contains(reportAccessControl.getReportRole(reportName));
        }

        LOG.debug(username+" authorized to view "+reportName+" report? "+isAuthorized);
        return isAuthorized;
    }
}
