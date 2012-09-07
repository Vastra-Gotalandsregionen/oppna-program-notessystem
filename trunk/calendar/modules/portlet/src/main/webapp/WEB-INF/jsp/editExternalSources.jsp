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

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>

<style type="text/css">
    .edit-external-sources .input-field {
        padding-bottom: 8px;
    }

    .edit-external-sources .input-field label {
        display: block;
    }

    .edit-external-sources input.wide {
        width: 98%;
    }

    .edit-external-sources .existing-source {
        border-bottom: solid 1px #bbbbbb;
        padding: 0px 4px 4px 4px;
    }

    .edit-external-sources fieldset {
        margin: 20px 0px;
    }

</style>

<portlet:renderURL var="backToView">
</portlet:renderURL>

<portlet:renderURL var="editGoogleCalendar">
    <portlet:param name="action" value="editGoogleCalendar"/>
</portlet:renderURL>

<portlet:actionURL var="editExternalSource">
    <portlet:param name="action" value="editExternalSource"/>
</portlet:actionURL>

<div class="edit-external-sources">

    <a href="${backToView}">Tillbaka till kalender</a>

    <c:if test="${not empty errorMessage}">
        <span class="portlet-msg-error">${errorMessage}</span>
    </c:if>

    <fieldset>
        <legend>Google-konto</legend>
        <c:if test="${not empty googleEmail}">
            <div>
                Du har anslutit ditt Google-konto med e-post <strong>${googleEmail}</strong>.
            </div>
        </c:if>
        <a href="${editGoogleCalendar}">Inställningar för Google-koppling</a>

    </fieldset>

    <c:if test="${not empty externalSources}">
        <fieldset>
            <legend>Befintliga kalendrar</legend>
            <c:forEach var="externalSource" items="${externalSources}">
                <div class="existing-source">
                    <form action="${editExternalSource}" method="post">
                        <input type="hidden" value="${externalSource.key}" name="oldExternalSourceKey"/>

                        <div class="input-field">
                            <label for="calendarTypeInput${externalSource.key}">Namn:</label>
                            <input id="calendarTypeInput${externalSource.key}" type="text" value="${externalSource.key}"
                                   name="externalSourceKey"/>
                        </div>
                        <div class="input-field">
                            <label for="calendarUrlInput${externalSource.key}">ICal-URL:</label>
                            <input class="wide" id="calendarUrlInput${externalSource.key}" type="text"
                                   value="${externalSource.value}" name="externalSourceUrl"/>
                        </div>
                        <div class="input-field">
                            <input type="submit" name="submitType" value="Radera">
                            <input type="submit" name="submitType" value="Uppdatera">
                        </div>
                    </form>
                </div>
            </c:forEach>
        </fieldset>
    </c:if>

    <fieldset>
        <legend>Lägg till extern källa:</legend>
        <form action="${editExternalSource}" method="post">
            <div class="input-field">
                <label for="calendarTypeInput">Namn:</label>
                <input id="calendarTypeInput" type="text" name="externalSourceKey">
            </div>
            <div class="input-field">
                <label for="calendarUrlInput">ICal-URL:</label>
                <input class="wide" id="calendarUrlInput" type="text" name="externalSourceUrl">
            </div>
            <div class="input-field">
                <input type="submit" name="addExternalSource" value="Lägg till">
            </div>

        </form>
    </fieldset>
</div>