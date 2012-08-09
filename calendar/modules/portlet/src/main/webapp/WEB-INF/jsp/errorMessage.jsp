<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--

    Copyright 2010 Västra Götalandsregionen

      This library is free software; you can redistribute it and/or modify
      it under the terms of version 2.1 of the GNU Lesser General Public
      License as published by the Free Software Foundation.

      This library is distributed in the hope that it will be useful,
      but WITHOUT ANY WARRANTY; without even the implied warranty of
      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
      GNU Lesser General Public License for more details.

      You should have received a copy of the GNU Lesser General Public
      License along with this library; if not, write to the
      Free Software Foundation, Inc., 59 Temple Place, Suite 330,
      Boston, MA 02111-1307  USA


--%>


<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<portlet:renderURL var="editExternalSources">
    <portlet:param name="action" value="editExternalSources"/>
</portlet:renderURL>

<portlet:renderURL var="viewCalendars">
</portlet:renderURL>

<p>
    <a href="${viewCalendars}" style="margin-right: 20px">Tillbaka till kalender</a>
    <a href="${editExternalSources}">Redigera externa källor</a>
</p>

<c:if test="${not empty errorMessage}">
    <span class="portlet-msg-error">${errorMessage}</span>
</c:if>