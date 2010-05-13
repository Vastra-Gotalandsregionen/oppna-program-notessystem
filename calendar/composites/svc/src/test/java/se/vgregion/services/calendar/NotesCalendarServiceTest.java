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

/**
 * 
 */
package se.vgregion.services.calendar;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Matchers.*;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import se.vgregion.calendar.CalendarEvent;
import se.vgregion.calendar.CalendarEventRepository;
import se.vgregion.calendar.WeekOfYear;
import se.vgregion.services.calendar.CalendarService;
import se.vgregion.services.calendar.CalendarServiceImp;

/**
 * @author Anders Asplund - Callista Enterprise
 * 
 */
public class NotesCalendarServiceTest {
    private static final String USER_ID_1 = "user1";

    private CalendarService notesCalendarService = null;

    @Mock
    private CalendarEventRepository calendarEventRepository;

    @Mock
    WeekOfYear weekOfYear;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        notesCalendarService = new CalendarServiceImp();
        notesCalendarService.setCalendarEventRepository(calendarEventRepository);
    }

    @Test
    public void shoudReturnAnIndividualListOfCalendarEvents() throws Exception {
        // Given
        List<CalendarEvent> expectedCalendarEvents = Arrays.asList(new CalendarEvent());
        given(calendarEventRepository.findCalendarEvents(anyString())).willReturn(expectedCalendarEvents);

        // When
        List<CalendarEvent> calendarEvents = notesCalendarService.getCalendarEvents(USER_ID_1);

        // Then
        assertNotNull(calendarEvents);
        assertThat(calendarEvents, IsInstanceOf.instanceOf(List.class));
        assertFalse(calendarEvents.isEmpty());
        assertThat(calendarEvents.get(0), IsInstanceOf.instanceOf(CalendarEvent.class));
    }

    @Test
    public void shouldReturnCalendarEventsForASpecificDate() throws Exception {
        // Given
        List<CalendarEvent> expectedCalendarEvents = Arrays.asList(new CalendarEvent());
        given(calendarEventRepository.findCalendarEvents(anyString(), any(WeekOfYear.class))).willReturn(
                expectedCalendarEvents);

        // When
        List<CalendarEvent> calendarEvents = notesCalendarService.getCalendarEvents(USER_ID_1, weekOfYear);

        // Then
        assertNotNull(calendarEvents);
        assertThat(calendarEvents, IsInstanceOf.instanceOf(List.class));
        assertFalse(calendarEvents.isEmpty());
        assertThat(calendarEvents.get(0), IsInstanceOf.instanceOf(CalendarEvent.class));
    }

    @Test
    public void shouldReturnEmptyListIfUserIdIsNull() throws Exception {
        // When
        List<CalendarEvent> calendarEvents = notesCalendarService.getCalendarEvents(null);

        // Then
        assertNotNull(calendarEvents);
        assertThat(calendarEvents, IsInstanceOf.instanceOf(List.class));
        assertTrue(calendarEvents.isEmpty());
    }

    @Test
    public void shouldReturnEmptyListIfEmptyUserId() throws Exception {
        // When
        List<CalendarEvent> calendarEvents = notesCalendarService.getCalendarEvents("");

        // Then
        assertNotNull(calendarEvents);
        assertThat(calendarEvents, IsInstanceOf.instanceOf(List.class));
        assertTrue(calendarEvents.isEmpty());
    }
}
