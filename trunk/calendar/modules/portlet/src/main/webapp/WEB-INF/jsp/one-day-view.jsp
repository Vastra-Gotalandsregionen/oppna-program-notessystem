<%--
Copyright 2010 Västra Götalandsregionen

This library is free software; you can redistribute it and/or modify
it under the terms of version 2.1 of the GNU Lesser General Public
License as published by the Free Software Foundation.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place, Suite 330,
Boston, MA 02111-1307 USA
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<portlet:renderURL escapeXml="false" var="next">
	<portlet:param name="navigate" value="next"/>
	<portlet:param name="noCache" value="${randomNumber}"/>
</portlet:renderURL>
<portlet:renderURL escapeXml="false" var="previous">
	<portlet:param name="navigate" value="previous"/>
	<portlet:param name="noCache" value="${randomNumber}"/>
</portlet:renderURL>
<portlet:renderURL var="editExternalSources" windowState="normal">
	<portlet:param name="action" value="editExternalSources"/>
</portlet:renderURL>

<div id="<portlet:namespace />calendarWrap" class="calendar-wrap">

	<c:if test="${not empty errorMessage}">
		<span class="portlet-msg-error">${errorMessage}</span>
	</c:if>

	<div class="calendar-listing content-box">
		<h2>Min dag</h2>
		<div class="content-box-bd">
			<c:choose>
				<c:when test="${signedIn}">

					<div class="pager clearfix cal-pager">
						<a class="prev" href="${previous}">Tidigare</a>
						<a class="next" href="${next}">Senare</a>
					</div>

					<c:forEach items="${calendarItems}" var="eventDay">
						<div class="calendar-items">
							<c:forEach items="${eventDay}" var="event" varStatus="eventStatus">

									<div class="calendar-item">
											<div class="entry-time">
												<div class="entry-time-inner">
													<div>${event.startTime}</div>
													<div>-</div>
													<div>${event.endTime}</div>
												</div>
											</div>
											<div class="entry-content">${event.title}</div>
									</div>

							</c:forEach>
						</div>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<div class="portlet-msg-info">
						Du m&aring;ste vara inloggad f&ouml;r att kunna se din kalender.
					</div>
				</c:otherwise>
			</c:choose>
		</div>
	</div>


</div>

<script type="text/javascript">
	AUI().ready('vgr-calendar-portlet', function(A) {
		var vgrCalendarPortlet = new A.VgrCalendarPortlet({
		calendarWrapNode: '#<portlet:namespace/>calendarWrap'
		}).render();
	});
</script>
