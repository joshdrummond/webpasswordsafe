<%--
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
--%>
<%@page import="net.webpasswordsafe.common.util.Constants.AuthenticationStatus"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="SUCCESS" value="<%=AuthenticationStatus.SUCCESS%>"/>
<c:choose>
  <c:when test="${status == SUCCESS}">
    <c:redirect url="${baseUrl}/"/>
  </c:when>
  <c:otherwise>
WebPasswordSafe unknown user: ${fn:escapeXml(user)}<br>
<ul>
<li><a href="${baseUrl}/logout">Logout</a>
<li><a href="${baseUrl}/?bypassSSO=true">Bypass SSO</a>
</ul>
  </c:otherwise>
</c:choose>