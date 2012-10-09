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
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet"%>

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
    
	<div class="pager clearfix cal-pager">
		<a class="prev" href="${previous}">Föregående</a>
		<a class="next" href="${next}">Nästa</a>
		<span class="cal-display-interval">${displayPeriodText}</span>
	</div>    

	<c:forEach items="${calendarItems}" var="eventDay">
		<div class="cal-day-wrap">
			<h4 class="cal-date">${eventDay[0].dayOfWeek}<span class="date"> - ${eventDay[0].dayOfMonth} ${eventDay[0].monthOfYear}</span></h4>
		  	<ul class="cal-item-list clearfix">
				<c:forEach items="${eventDay}" var="event" varStatus="eventStatus">
		    		<li class="cal-item">
                        <c:choose>
                            <c:when test="${event.wholeDays}">
                                <span class="cal-item-time">
                                    <c:choose>
                                        <c:when test="${event.interval.endMillis - event.interval.startMillis >= 1000 * 60 * 60 * 24}">
                                            Heldagar t.o.m. ${event.endDayOfWeek} ${event.endDayOfMonth}/${event.endMonthOfYearAsNumber}
                                        </c:when>
                                        <c:otherwise>
                                            Heldag
                                        </c:otherwise>
                                    </c:choose>
                                </span>
                            </c:when>
                            <c:otherwise>
                                <span class="cal-item-time">
                                    ${event.startTime}-${event.endTime}
                                    <c:if test="${event.interval.endMillis - event.interval.startMillis >= 1000 * 60 * 60 * 24}">
                                        ${event.endDayOfWeek} ${event.endDayOfMonth}/${event.endMonthOfYearAsNumber}
                                    </c:if>
                                </span>
                            </c:otherwise>
                        </c:choose>
		    			<span class="cal-item-type">${event.calendarType}</span>
						<span class="cal-item-title">${event.title}</span>
	    			</li>
				</c:forEach>
		  	</ul>
	  	</div>
	</c:forEach>
	
	<div class="rp-link-wrap rp-link-wrap-right">
		<a class="rp-link" href="${editExternalSources}">Lägg till kalender</a>
	</div>	
	
</div>

<script type="text/javascript">

	AUI().ready('vgr-calendar-portlet', function(A) {
		var vgrCalendarPortlet = new A.VgrCalendarPortlet({
			calendarWrapNode: '#<portlet:namespace />calendarWrap'
		}).render();			
	});

</script>