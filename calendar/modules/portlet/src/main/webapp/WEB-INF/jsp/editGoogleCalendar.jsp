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

<style type="text/css">
    .calendar-list-entry {
        -moz-box-shadow: 5px 5px 4px #888;
        -webkit-box-shadow: 5px 5px 4px #888;
        box-shadow: 5px 5px 4px #888;
        margin: 15px 5px;
        padding: 5px;
    }

    .calendar-list-entry .checkbox-div {
        margin: auto;
    }

    .calendar-list-entry span {
        float: left;
    }
</style>

<portlet:renderURL var="editExternalSources">
    <portlet:param name="action" value="editExternalSources"/>
</portlet:renderURL>

<portlet:actionURL var="addGoogleCalendar">
    <portlet:param name="action" value="addGoogleCalendar"/>
</portlet:actionURL>

<portlet:actionURL var="saveGoogleCalendar">
    <portlet:param name="action" value="saveGoogleCalendar"/>
</portlet:actionURL>

<portlet:renderURL var="viewCalendars">
</portlet:renderURL>

<p>
    <a href="${viewCalendars}" style="margin-right: 20px">Tillbaka till kalender</a>
    <a href="${editExternalSources}">Redigera externa källor</a>
</p>

<form action="${saveGoogleCalendar}" method="post">
    <c:forEach items="${calendarListEntries}" var="entry">
        <fieldset class="calendar-list-entry">
            <legend><c:out value="${entry.id}"/></legend>
            <div>
                <table>
                    <col width="400"/>
                    <col width="100"/>
                    <tr>
                        <td>
                            <p>
                                <strong>Sammanfattning</strong><br/>
                                    ${entry.summary}
                            </p>
                            <p>
                                <strong>Beskrivning</strong><br/>
                                    ${entry.description}
                            </p>
                        </td>
                        <td>
                            <div class="checkbox-div">
                                <c:set var="checkedText" value=""/>
                                <c:forEach items="${selectedCalendars}" var="selectedCalendar">
                                    <c:if test="${entry.id eq selectedCalendar}">
                                        <c:set var="checkedText" value="checked=\"checked\""/>
                                    </c:if>
                                </c:forEach>
                                <input type="checkbox" name="selectedCalendars" ${checkedText} value="${entry.id}">
                            </div>
                        </td>
                    </tr>
                </table>
            </div>
        </fieldset>
    </c:forEach>
    <input type="submit" value="Spara">
</form>

<c:if test="${empty calendarListEntries}">
    <p><a href="${addGoogleCalendar}">Lägg till Google-kalender</a></p>
</c:if>
