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
