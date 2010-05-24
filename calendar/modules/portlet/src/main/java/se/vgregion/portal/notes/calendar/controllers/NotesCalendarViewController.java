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

import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.context.PortletConfigAware;

import se.vgregion.core.domain.calendar.CalendarEvents;
import se.vgregion.core.domain.calendar.CalendarItem;
import se.vgregion.core.domain.calendar.CalendarEventPeriod;
import se.vgregion.services.calendar.CalendarService;

@Controller
@SessionAttributes("displayPeriod")
@RequestMapping("VIEW")
public class NotesCalendarViewController implements PortletConfigAware {
    public static final String VIEW_WEEK = "week";
    public static final Days DEFAULT_PERIOD_LENGTH = Days.SEVEN;

    private CalendarService calendarService;
    private PortletConfig portletConfig = null;
    private PortletData portletData;

    @Autowired
    public NotesCalendarViewController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    public void setPortletConfig(PortletConfig portletConfig) {
        this.portletConfig = portletConfig;
    }

    @Autowired
    public void setPortletData(PortletData portletData) {
        this.portletData = portletData;
    }

    @RenderMapping
    public String displayCalendarEvents(ModelMap model, RenderRequest request, RenderResponse response) {
        String userId = portletData.getUserId(request);
        // String title = portletData.getPortletTitle(portletConfig, request);
        CalendarEvents events = null;
        CalendarEventPeriod displayPeriod = (CalendarEventPeriod) model.get("displayPeriod");
        if (displayPeriod == null) {
            displayPeriod = new CalendarEventPeriod(new DateTime(), DEFAULT_PERIOD_LENGTH);
            model.put("displayPeriod", displayPeriod);
        }
        events = calendarService.getCalendarEvents(userId, displayPeriod);
        List<List<CalendarItem>> calendarItems = events.getCalendarItemsGroupedByStartDate();
        // portletData.setPortletTitle(response, title + " - Vecka " + currentWeek.getWeekNumber());
        model.put("calendarItems", calendarItems);
        return VIEW_WEEK;
    }

    @ActionMapping(params = "navigate=next")
    public void nextWeek(ModelMap model) {
        CalendarEventPeriod displayPeriod = (CalendarEventPeriod) model.get("displayPeriod");
        if (displayPeriod != null) {
            model.put("displayPeriod", displayPeriod.next());
        }
    }

    @ActionMapping(params = "navigate=previous")
    public void previousWeek(ModelMap model) {
        CalendarEventPeriod displayPeriod = (CalendarEventPeriod) model.get("displayPeriod");
        if (displayPeriod != null) {
            model.put("displayPeriod", displayPeriod.previous());
        }
    }

}
