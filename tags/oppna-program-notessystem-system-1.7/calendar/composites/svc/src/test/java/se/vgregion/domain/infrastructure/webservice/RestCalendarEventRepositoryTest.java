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
package se.vgregion.domain.infrastructure.webservice;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Matchers.*;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import se.vgregion.core.domain.calendar.CalendarEvents;
import se.vgregion.core.domain.calendar.CalendarEventsPeriod;

import javax.xml.bind.JAXBException;
import java.io.IOException;

/**
 * @author Anders Asplund - Callista Enterprise
 * 
 */
public class RestCalendarEventRepositoryTest {

    @Mock
    private CalendarEventsPeriod period;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldThrowRestClientException() throws Exception {
        // Given
        given(period.getStartDate()).willReturn(new DateTime());
        given(period.getDays()).willReturn(Days.SEVEN);

        RestCalendarEventsRepository repo = new RestCalendarEventsRepository() {
            @Override
            protected CalendarEvents getCalendarEventsFromUrl(String requestUrl) throws IOException, JAXBException {
                throw new IOException("Test exception.");
            }
        };
        repo.setServiceEndpoint("http://aida.vgregion.se/calendar.nsf/getinfo?openagent&");

        // When
        CalendarEvents events = repo.findCalendarEventsByCalendarPeriod("", period);

        // Then
        assertSame(CalendarEvents.EMPTY_CALENDAR_EVENTS, events);

    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldReturnCalendarEvents() throws Exception {
        // Given
        RestCalendarEventsRepository repo = new RestCalendarEventsRepository() {
            @Override
            protected CalendarEvents getCalendarEventsFromUrl(String requestUrl) throws IOException, JAXBException {
                return new CalendarEvents();
            }
        };

        repo.setServiceEndpoint("http://aida.vgregion.se/calendar.nsf/getinfo?openagent&");

        given(period.getStartDate()).willReturn(new DateTime());
        given(period.getDays()).willReturn(Days.SEVEN);

        // When
        CalendarEvents foundEvents = repo.findCalendarEventsByCalendarPeriod("", period);

        // Then
        assertNotNull(foundEvents);
    }
}
