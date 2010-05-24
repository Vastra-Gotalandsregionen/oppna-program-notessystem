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
import se.vgregion.services.calendar.CalendarService;

/**
 * @author Anders Asplund - Callista Enterprise
 */
public class NotesCalendarViewControllerTest {
    private static final String USER_ID = String.valueOf(1);

    private NotesCalendarViewController notesCalendarViewController;
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

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        // renderRequest = getMockRenderRequest();
        // portletConfig = new MockPortletConfig();
        notesCalendarViewController = new NotesCalendarViewController(calendarService);
        notesCalendarViewController.setPortletConfig(portletConfig);
        notesCalendarViewController.setPortletData(portletData);
        model = new ModelMap();
    }

    @SuppressWarnings("unchecked")
    @Ignore
    @Test
    public void modelShouldContainAListOfCalendarItems() throws Exception {
        // // Given
        // given(calendarService.getCalendarEvents(anyString())).willReturn(calendarEvents);
        // given(calendarEvents.getWeek()).willReturn(any(WeekOfYear.class));
        // given(portletData.getPortletTitle(any(PortletConfig.class), any(RenderRequest.class))).willReturn(null);
        // doNothing().when(portletData).setPortletTitle(null, null);
        //
        // // When
        // notesCalendarViewController.displayCalendarEvents(model, renderRequest, renderResponse);
        //
        // // Then
        // List<List<CalendarItem>> events = (List<List<CalendarItem>>) model.get("calendarItems");
        // assertNotNull(events);
    }

    // private MockRenderRequest getMockRenderRequest() throws ReadOnlyException {
    // MockRenderRequest mockRenderRequest = new MockRenderRequest();
    // // Create user login id attribute.
    // Map<String, String> userInfo = new HashMap<String, String>();
    // userInfo.put(PortletRequest.P3PUserInfos.USER_LOGIN_ID.toString(), USER_ID);
    // mockRenderRequest.setAttribute(PortletRequest.USER_INFO, userInfo);
    // return mockRenderRequest;
    // }
}
