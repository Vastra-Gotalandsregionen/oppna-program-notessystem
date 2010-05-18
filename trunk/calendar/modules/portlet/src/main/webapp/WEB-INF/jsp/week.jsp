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

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<portlet:actionURL escapeXml="false" var="next">
  <portlet:param name="navigate" value="next"/>
</portlet:actionURL>
<portlet:actionURL escapeXml="false" var="previous">
  <portlet:param name="navigate" value="previous"/>
</portlet:actionURL>

<head>
<style type="text/css">
    @import url("${pageContext.request.contextPath}/style/module-calendar.css");
</style>
</head>
<div id="module-calendar" class="module">
<div class="yui-navset yui-navset-top">
<div class="yui-content">
<div class="tab-bd clearfix">

<c:forEach items="${calendarItems}" var="eventDay">
  <h4>${eventDay[0].dayOfWeek}<span class="date"> &ndash; ${eventDay[0].dayOfMonth} ${eventDay[0].monthOfYear}</span></h4>
  <ul>
<c:forEach items="${eventDay}" var="event" varStatus="eventStatus">
    <li class="cal-private">${event.startTime}&ndash;${event.endTime} <a class="dialog"
      href="mitt_jobb/dialog_mote.html">${event.title}</a><span style="float: right;" id="calendar-type">${event.calendarType}</span></li>
</c:forEach>
  </ul>
</c:forEach>
<p class="pager"><a class="prev" href="${previous}">Föregående</a> <a class="next" href="${next}">Nästa</a></p>
</div>
</div>
</div>
</div>
