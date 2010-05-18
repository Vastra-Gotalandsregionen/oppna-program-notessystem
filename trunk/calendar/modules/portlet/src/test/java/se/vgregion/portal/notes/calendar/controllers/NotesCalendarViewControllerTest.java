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

import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.ReadOnlyException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.portlet.MockRenderRequest;
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
    private CalendarService calendarService;

    @Mock
    private CalendarEvents calendarEvents;

    private MockRenderRequest renderRequest;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        renderRequest = getMockRenderRequest();
        notesCalendarViewController = new NotesCalendarViewController(calendarService);
        model = new ModelMap();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldContainModelWithListOfCalendarEvents() throws Exception {
        // Given
        given(calendarService.getCalendarEvents(anyString())).willReturn(calendarEvents);

        // When
        notesCalendarViewController.displayCalendarEvents(model, renderRequest);

        // Then
        CalendarEvents events = (CalendarEvents) model.get("calenderEvents");
        assertNotNull(events);
    }

    private MockRenderRequest getMockRenderRequest() throws ReadOnlyException {
        MockRenderRequest mockRenderRequest = new MockRenderRequest();
        // Create user login id attribute.
        Map<String, String> userInfo = new HashMap<String, String>();
        userInfo.put(PortletRequest.P3PUserInfos.USER_LOGIN_ID.toString(), USER_ID);
        mockRenderRequest.setAttribute(PortletRequest.USER_INFO, userInfo);
        return mockRenderRequest;
    }

}
