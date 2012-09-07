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

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.portlet.MockPortletPreferences;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.portlet.ModelAndView;
import se.vgregion.core.domain.calendar.CalendarEvents;
import se.vgregion.core.domain.calendar.CalendarEventsPeriod;
import se.vgregion.core.domain.calendar.CalendarItem;
import se.vgregion.portal.calendar.util.EncodingUtil;
import se.vgregion.services.calendar.CalendarService;
import se.vgregion.services.calendar.google.GoogleCalendarService;

import javax.portlet.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.*;

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
        GoogleCalendarService googleCalendarService = mock(GoogleCalendarService.class);
        notesCalendarViewController = new NotesCalendarViewController(calendarService, googleCalendarService);
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

        // Then we should put CalendarEventsPeriod zero times in the model since we use the existing CalendarEventsPeriod.
        verify(model, times(0)).put(anyString(), isA(CalendarEventsPeriod.class));
    }

    @Test
    public void shouldIncrementDisplayedWeekInModelWithOneWeek() throws Exception {
        // Given
        ModelMap aModel = new ModelMap();
        CalendarEventsPeriod displayPeriod1 = new CalendarEventsPeriod(new DateTime(),
                CalendarEventsPeriod.DEFAULT_PERIOD_LENGTH);
        aModel.put("displayPeriod", displayPeriod1);

        // When
        notesCalendarViewController.nextWeek(aModel, renderRequest, renderResponse);

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
        notesCalendarViewController.previousWeek(aModel, renderRequest, renderResponse);

        // Then
        CalendarEventsPeriod displayPeriod2 = (CalendarEventsPeriod) aModel.get("displayPeriod");
        assertEquals(CalendarEventsPeriod.DEFAULT_PERIOD_LENGTH, Days.daysBetween(displayPeriod2.getStartDate(),
                displayPeriod1.getStartDate()));
    }

    @Test
    public void testEditExternalSources() throws Exception {

        // Given
        Map<String, String> externalSources = new HashMap<String, String>();
        externalSources.put("my calendar", "http://example.com/asdf.ics");

        String externalSourcesEncoded = EncodingUtil.encodeToString((Serializable) externalSources);

        PortletPreferences preferences = mock(PortletPreferences.class);
        given(preferences.getValue("externalSourcesEncoded", null)).willReturn(externalSourcesEncoded);

        RenderRequest request = mock(RenderRequest.class);
        given(request.getPreferences()).willReturn(preferences);

        RenderResponse response = mock(RenderResponse.class);
        Model model = mock(Model.class);

        // When
        notesCalendarViewController.editExternalSources(request, response, model);

        // Then
        verify(model).addAttribute(eq("externalSources"), eq(externalSources));
    }

    @Test
    public void testEditExternalSource_RemoveExternalSource() throws Exception {
        
        // Given
        Map<String, String> externalSources = new HashMap<String, String>();
        externalSources.put("my calendar", "http://example.com/asdf.ics");

        String externalSourcesEncoded = EncodingUtil.encodeToString((Serializable) externalSources);

        PortletPreferences preferences = new MockPortletPreferences();
        preferences.setValue("externalSourcesEncoded", externalSourcesEncoded);

        ActionRequest request = mock(ActionRequest.class);
        given(request.getPreferences()).willReturn(preferences);

        ActionResponse response = mock(ActionResponse.class);
        Model model = mock(Model.class);

        given(request.getParameter("submitType")).willReturn("Radera");
        given(request.getParameter("oldExternalSourceKey")).willReturn("my calendar");

        // When
        notesCalendarViewController.editExternalSource(request, response, model);

        // Then
        String externalSourcesEncodedNew = preferences.getValue("externalSourcesEncoded", null);
        Map<String, String> externalSourcesNew = EncodingUtil.decodeToObject(externalSourcesEncodedNew);

        assertEquals(0, externalSourcesNew.size());
    }

    @Test
    public void testEditExternalSource_UpdateExternalSource() throws Exception {

        // Given
        Map<String, String> externalSources = new HashMap<String, String>();
        externalSources.put("my calendar", "http://example.com/asdf.ics");

        String externalSourcesEncoded = EncodingUtil.encodeToString((Serializable) externalSources);

        PortletPreferences preferences = new MockPortletPreferences();
        preferences.setValue("externalSourcesEncoded", externalSourcesEncoded);

        ActionRequest request = mock(ActionRequest.class);
        given(request.getPreferences()).willReturn(preferences);

        ActionResponse response = mock(ActionResponse.class);
        Model model = mock(Model.class);

        given(request.getParameter("submitType")).willReturn("Uppdatera");
        given(request.getParameter("oldExternalSourceKey")).willReturn("my calendar");
        given(request.getParameter("externalSourceKey")).willReturn("my calendar2");
        given(request.getParameter("externalSourceUrl")).willReturn("http://example.com/asdf2.ics");

        // When
        notesCalendarViewController.editExternalSource(request, response, model);

        // Then
        String externalSourcesEncodedNew = preferences.getValue("externalSourcesEncoded", null);
        Map<String, String> externalSourcesNew = EncodingUtil.decodeToObject(externalSourcesEncodedNew);

        assertEquals(1, externalSourcesNew.size());
        assertEquals("http://example.com/asdf2.ics", externalSourcesNew.get("my calendar2"));
    }

    @Test
    public void testEditExternalSource_AddExternalSource() throws Exception {

        // Given
        Map<String, String> externalSources = new HashMap<String, String>();
        externalSources.put("my calendar", "http://example.com/asdf.ics");

        String externalSourcesEncoded = EncodingUtil.encodeToString((Serializable) externalSources);

        PortletPreferences preferences = new MockPortletPreferences();
        preferences.setValue("externalSourcesEncoded", externalSourcesEncoded);

        ActionRequest request = mock(ActionRequest.class);
        given(request.getPreferences()).willReturn(preferences);

        ActionResponse response = mock(ActionResponse.class);
        Model model = mock(Model.class);

        given(request.getParameter("externalSourceKey")).willReturn("my calendar2");
        given(request.getParameter("externalSourceUrl")).willReturn("http://example.com/asdf2.ics");

        // When
        notesCalendarViewController.editExternalSource(request, response, model);

        // Then
        String externalSourcesEncodedNew = preferences.getValue("externalSourcesEncoded", null);
        Map<String, String> externalSourcesNew = EncodingUtil.decodeToObject(externalSourcesEncodedNew);

        assertEquals(2, externalSourcesNew.size());
        assertEquals("http://example.com/asdf.ics", externalSourcesNew.get("my calendar"));
        assertEquals("http://example.com/asdf2.ics", externalSourcesNew.get("my calendar2"));
    }

    @Test
    public void testHandleException() {
        Exception exception = new Exception("Very strange exception");

        ModelAndView modelAndView = notesCalendarViewController.handleException(exception);

        assertEquals("errorMessage", modelAndView.getViewName());
        assertNotNull(modelAndView.getModel().get("errorMessage"));

    }

    @Test
    public void testArrayToString() {
        String[] array = new String[]{"ett", "två", "tre"};
        String s = notesCalendarViewController.arrayToString(array);

        assertEquals("ett==SEPARATOR==två==SEPARATOR==tre", s);
    }

    @Test
    public void testStringToArray() {
        String string = "ett==SEPARATOR==två==SEPARATOR==tre";
        String[] array = new String[]{"ett", "två", "tre"};
        String[] result = notesCalendarViewController.stringToArray(string);

        assertArrayEquals(array, result);
    }

}
