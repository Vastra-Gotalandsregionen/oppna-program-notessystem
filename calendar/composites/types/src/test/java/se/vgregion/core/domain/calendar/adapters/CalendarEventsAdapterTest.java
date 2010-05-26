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
package se.vgregion.core.domain.calendar.adapters;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import se.vgregion.core.domain.calendar.CalendarEvents;

/**
 * @author Anders Asplund
 * 
 */
public class CalendarEventsAdapterTest {

    private CalendarEventsAdapter adapter = new CalendarEventsAdapter();
    @Mock
    private CalendarEvents events;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test method for
     * {@link se.vgregion.core.domain.calendar.adapters.CalendarEventsAdapter#marshal(se.vgregion.core.domain.calendar.CalendarEvents)}
     * .
     */
    @Test(expected = UnsupportedOperationException.class)
    public final void marshalShouldBeUnsupported() throws Exception {
        adapter.marshal(events);
    }

    /**
     * Test method for
     * {@link se.vgregion.core.domain.calendar.adapters.CalendarEventsAdapter#unmarshal(se.vgregion.core.domain.calendar.CalendarEvents)}
     * .
     */
    @Test(expected = VgrCalendarWebServiceException.class)
    public final void unmarshalShouldThrowVgrCalendarWebServiceExceptionIfStatusEqualsError() throws Exception {
        // Given
        given(events.getStatus()).willReturn("ERROR");

        // When
        adapter.unmarshal(events);
    }

    @Test
    public void unmarshalShouldReturnTheSameCalendarEventsAsInputed() throws Exception {
        // Given
        given(events.getStatus()).willReturn("");

        // When
        CalendarEvents returnedEvents = adapter.unmarshal(events);

        // Then
        assertSame(events, returnedEvents);
    }
}
