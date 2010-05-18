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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import se.vgregion.core.domain.calendar.*;

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
    private CalendarEvents calendarEvents;

    @Mock
    WeekOfYear weekOfYear;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        notesCalendarService = new CalendarServiceImp(calendarEventRepository);
    }

    @Test
    public void shoudReturnAListOfCalendarEvents() throws Exception {
        // Given
        given(calendarEventRepository.findCalendarEventsById(any(CalendarEventsId.class))).willReturn(
                calendarEvents);

        // When
        CalendarEvents listOfEvents = notesCalendarService.getCalendarEvents(USER_ID_1);

        // Then
        assertNotNull(listOfEvents);
    }
    //
    // @Test
    // public void shouldReturnEmptyListIfUserIdIsNull() throws Exception {
    // // When
    // List<CalendarItem> calendarEvents = notesCalendarService.getCalendarEvents(null);
    //
    // // Then
    // assertNotNull(calendarEvents);
    // assertTrue(calendarEvents.isEmpty());
    // }
    //
    // @Test
    // public void shouldReturnEmptyListIfEmptyUserId() throws Exception {
    // // When
    // List<CalendarItem> calendarEvents = notesCalendarService.getCalendarEvents("");
    //
    // // Then
    // assertNotNull(calendarEvents);
    // assertTrue(calendarEvents.isEmpty());
    // }
}
