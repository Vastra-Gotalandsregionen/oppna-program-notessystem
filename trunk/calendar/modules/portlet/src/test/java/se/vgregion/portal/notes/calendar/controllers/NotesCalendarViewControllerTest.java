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

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.ModelMap;

import se.vgregion.core.domain.calendar.CalendarEvents;
import se.vgregion.core.domain.calendar.CalendarEventsPeriod;
import se.vgregion.core.domain.calendar.CalendarItem;
import se.vgregion.services.calendar.CalendarService;

/**
 * @author Anders Asplund - Callista Enterprise
 */
public class NotesCalendarViewControllerTest {
    private static final String USER_ID = String.valueOf(1);

    private NotesCalendarViewController notesCalendarViewController;
    @Mock
    private ModelMap model;
    @Mock
    private RenderRequest renderRequest;
    @Mock
    private RenderResponse renderResponse;
    @Mock
    private PortletConfig portletConfig;
    @Mock
    private CalendarService calendarService;
    @Mock
    private CalendarEvents calendarEvents;
    @Mock
    private PortletData portletData;
    @Mock
    private List<List<CalendarItem>> calendarItems;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        notesCalendarViewController = new NotesCalendarViewController(calendarService);
        notesCalendarViewController.setPortletData(portletData);
    }

    @Test
    public void shouldAddACalendarEventsPeriodToModel() throws Exception {
        // Given

        ModelMap aModel = new ModelMap();
        given(portletData.getPortletTitle(any(PortletConfig.class), any(RenderRequest.class))).willReturn("");
        given(portletData.getUserId(any(RenderRequest.class))).willReturn("");
        given(calendarService.getCalendarEvents(anyString(), any(CalendarEventsPeriod.class))).willReturn(
                calendarEvents);
        given(calendarEvents.getCalendarItemsGroupedByStartDate()).willReturn(calendarItems);

        // When
        notesCalendarViewController.displayCalendarEvents(aModel, renderRequest, renderResponse);

        // Then
        CalendarEventsPeriod period = (CalendarEventsPeriod) aModel.get("displayPeriod");
        assertNotNull(period);
    }

    @Test
    @Ignore
    public void shouldUseExistingCalendarEventsPeriodIfExists() throws Exception {
        // Given
        given(portletData.getPortletTitle(any(PortletConfig.class), any(RenderRequest.class))).willReturn("");
        given(portletData.getUserId(any(RenderRequest.class))).willReturn("");
        given(calendarService.getCalendarEvents(anyString(), any(CalendarEventsPeriod.class))).willReturn(
                calendarEvents);
        given(calendarEvents.getCalendarItemsGroupedByStartDate()).willReturn(calendarItems);
        model.put("displayPeriod", any(CalendarEventsPeriod.class));

        // When
        notesCalendarViewController.displayCalendarEvents(model, renderRequest, renderResponse);

        // Then
        verify(model, never()).put("displayPeriod", any(CalendarEventsPeriod.class));
    }
}
