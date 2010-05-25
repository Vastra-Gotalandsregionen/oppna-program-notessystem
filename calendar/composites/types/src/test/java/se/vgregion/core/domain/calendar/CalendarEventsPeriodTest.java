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
package se.vgregion.core.domain.calendar;

import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Anders Asplund
 * 
 */
public class CalendarEventsPeriodTest {

    private DateTime dateTime;
    private Days days;
    private CalendarEventsPeriod eventsPeriod;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        dateTime = new DateTime(2010, 5, 1, 12, 0, 0, 0);
        days = Days.SEVEN;
        eventsPeriod = new CalendarEventsPeriod(dateTime, days);
    }

    /**
     * Test method for {@link se.vgregion.core.domain.calendar.CalendarEventsPeriod#next()}.
     */
    @Test
    public final void testNext() {
        CalendarEventsPeriod nextPeriod = eventsPeriod.next();
        assertEquals(8, nextPeriod.getStartDate().dayOfMonth().get());
        assertEquals(5, nextPeriod.getStartDate().monthOfYear().get());
    }

    /**
     * Test method for {@link se.vgregion.core.domain.calendar.CalendarEventsPeriod#previous()}.
     */
    @Test
    public final void testPrevious() {
        CalendarEventsPeriod nextPeriod = eventsPeriod.previous();
        assertEquals(24, nextPeriod.getStartDate().dayOfMonth().get());
        assertEquals(4, nextPeriod.getStartDate().monthOfYear().get());
    }

}
