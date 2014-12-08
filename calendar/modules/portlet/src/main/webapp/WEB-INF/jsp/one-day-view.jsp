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
		<a class="prev" href="${previous}">Föregående dag</a>
		<a class="next" href="${next}">Nästa dag</a>
	</div>

    <div class="blogs-listing content-box">
        <h2>Min dag</h2>
        <div class="content-box-bd">
        	<c:forEach items="${calendarItems}" var="eventDay">
        		<c:forEach items="${eventDay}" var="event" varStatus="eventStatus">
					<div class="news-items">
						<div class="entry-item">
							<a href="https://ans.vgregion.se/c/blogs/find_entry?p_l_id=108278&amp;noSuchEntryRedirect=https%3A%2F%2Fans.vgregion.se%2Fhem%2F-%2Fasset_publisher%2FnFyiZOCBBjS3%2Fblog%2Fhej-dagboken%3F_101_INSTANCE_nFyiZOCBBjS3_redirect%3D%252F&amp;entryId=108807">
								<div class="entry-date">
									<div class="entry-date-inner">
										<div class="entry-date-month">${event.startTime}</div>
										<div class="entry-date-day"> - </div>
										<div class="entry-date-day">${event.endTime}</div>
									</div>
								</div>
								<div class="entry-content">${event.title}</div>
							</a>
						</div>
					</div>
				</c:forEach>
            </c:forEach>
        </div>
    </div>


</div>

<script type="text/javascript">

	AUI().ready('vgr-calendar-portlet', function(A) {
		var vgrCalendarPortlet = new A.VgrCalendarPortlet({
			calendarWrapNode: '#<portlet:namespace />calendarWrap'
		}).render();			
	});

</script>


