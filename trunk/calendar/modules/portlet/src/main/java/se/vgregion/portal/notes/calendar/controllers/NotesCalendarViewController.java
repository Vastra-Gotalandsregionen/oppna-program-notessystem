/**
 * Copyright 2010 Västra Götalandsregionen
 *
 *   This library is free software; you can redistribute it and/or modify
 *   it under the terms of version 2.1 of the GNU Lesser General Public
 *   License as published by the Free Software Foundation.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the
 *   Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *   Boston, MA 02111-1307  USA
 *
 */

package se.vgregion.portal.notes.calendar.controllers;

import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import se.vgregion.services.calendar.CalendarService;

@Controller
@RequestMapping("VIEW")
public class NotesCalendarViewController {
    public static final String VIEW_WEEK = "week";

    @Autowired
    private CalendarService notesCalendarService;

    public void setNotesCalendarService(CalendarService notesCalendarService) {
        this.notesCalendarService = notesCalendarService;
    }

    @RenderMapping
    public String displayCalendarEvents(ModelMap model, RenderRequest request) {
        String userId = getUserId(request);
        model.put("calenderEvents", notesCalendarService.getCalendarEvents(userId));
        return VIEW_WEEK;
    }

    @SuppressWarnings("unchecked")
    private String getUserId(RenderRequest request) {
        Map<String, String> attributes = (Map<String, String>) request.getAttribute(PortletRequest.USER_INFO);
        String userId = "";

        if (attributes != null) {
            userId = attributes.get(PortletRequest.P3PUserInfos.USER_LOGIN_ID.toString());
        }
        return userId;
    }

}
