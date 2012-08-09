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
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.ui.ModelMap;

import se.vgregion.core.domain.calendar.CalendarEvents;
import se.vgregion.core.domain.calendar.CalendarEventsPeriod;
import se.vgregion.core.domain.calendar.CalendarItem;
import se.vgregion.services.calendar.CalendarService;

/**
 * @author Anders Asplund - Callista Enterprise
 */
@SuppressWarnings("unused")
public class NotesCalendarViewControllerTest {
    private NotesCalendarViewController notesCalendarViewController;
    @Mock
    private ModelMap model;
    @Mock
    private RenderRequest renderRequest;
    @Mock
    private RenderResponse renderResponse;
    @Mock
    private CalendarService calendarService;
    @Mock
    private CalendarEvents calendarEvents;
    @Mock
    private PortletData portletData;
    @Mock
    private List<List<CalendarItem>> calendarItems;
    @Mock
    private CalendarEventsPeriod displayPeriod;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        notesCalendarViewController = new NotesCalendarViewController(calendarService);
        notesCalendarViewController.setPortletData(portletData);
        PortletPreferences portletPreferences = mock(PortletPreferences.class);
        given(renderRequest.getPreferences()).willReturn(portletPreferences);
    }

    @Test
    public void shouldAddACalendarEventsPeriodToModel() throws Exception {
        // Given
        given(portletData.getPortletTitle(any(PortletConfig.class), any(RenderRequest.class))).willReturn("");
        given(portletData.getUserId(any(RenderRequest.class))).willReturn("");
        given(calendarService.getCalendarEvents(anyString(), any(CalendarEventsPeriod.class))).willReturn(
                calendarEvents);
        given(calendarEvents.getCalendarItemsGroupedByStartDate()).willReturn(calendarItems);
        given(model.get(anyString())).willReturn(displayPeriod);

        // When
        notesCalendarViewController.displayCalendarEvents(model, renderRequest, renderResponse);

        // Then
        CalendarEventsPeriod period = (CalendarEventsPeriod) model.get("displayPeriod");
        assertNotNull(period);
    }

    @Test
    public void shouldUseExistingCalendarEventsPeriodIfExists() throws Exception {
        // Given
        given(portletData.getPortletTitle(any(PortletConfig.class), any(RenderRequest.class))).willReturn("");
        given(portletData.getUserId(any(RenderRequest.class))).willReturn("");
        given(model.get("displayPeriod")).willReturn(displayPeriod);
        given(calendarService.getFutureCalendarEvents(anyString(), any(CalendarEventsPeriod.class))).willReturn(
                new AsyncResult<CalendarEvents>(calendarEvents));
        given(calendarEvents.getCalendarItemsGroupedByStartDate()).willReturn(calendarItems);
        given(calendarService.getFutureCalendarEventsFromIcalUrl(anyString(), any(CalendarEventsPeriod.class), anyString()))
                .willReturn(new AsyncResult<CalendarEvents>(calendarEvents));

        // When
        notesCalendarViewController.displayCalendarEvents(model, renderRequest, renderResponse);

        // Then
        verify(model, times(1)).put(anyString(), any(CalendarEventsPeriod.class));
    }

    @Test
    public void shouldIncrementDisplayedWeekInModelWithOneWeek() throws Exception {
        // Given
        ModelMap aModel = new ModelMap();
        CalendarEventsPeriod displayPeriod1 = new CalendarEventsPeriod(new DateTime(),
                CalendarEventsPeriod.DEFAULT_PERIOD_LENGTH);
        aModel.put("displayPeriod", displayPeriod1);

        // When
        notesCalendarViewController.nextWeek(aModel);

        // Then
        CalendarEventsPeriod displayPeriod2 = (CalendarEventsPeriod) aModel.get("displayPeriod");
        assertEquals(CalendarEventsPeriod.DEFAULT_PERIOD_LENGTH, Days.daysBetween(displayPeriod1.getStartDate(),
                displayPeriod2.getStartDate()));
    }

    @Test
    public void shouldDecreaseDisplayedWeekInModelWithOneWeek() throws Exception {
        // Given
        ModelMap aModel = new ModelMap();
        CalendarEventsPeriod displayPeriod1 = new CalendarEventsPeriod(new DateTime(),
                CalendarEventsPeriod.DEFAULT_PERIOD_LENGTH);
        aModel.put("displayPeriod", displayPeriod1);

        // When
        notesCalendarViewController.previousWeek(aModel);

        // Then
        CalendarEventsPeriod displayPeriod2 = (CalendarEventsPeriod) aModel.get("displayPeriod");
        assertEquals(CalendarEventsPeriod.DEFAULT_PERIOD_LENGTH, Days.daysBetween(displayPeriod2.getStartDate(),
                displayPeriod1.getStartDate()));
    }
}
