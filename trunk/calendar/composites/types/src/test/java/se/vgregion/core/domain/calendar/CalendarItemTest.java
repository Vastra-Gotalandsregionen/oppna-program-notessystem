/**
 * 
 */
package se.vgregion.core.domain.calendar;

import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

/**
 * @author Anders Asplund
 * 
 */
public class CalendarItemTest {

    private Interval interval;
    private CalendarItem item;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        item = new CalendarItem();
        interval = new Interval(new DateTime(2010, 5, 1, 12, 0, 0, 0), new DateTime(2010, 5, 1, 13, 30, 0, 0));
        item.setCalendarType("private");
        item.setInterval(interval);
    }

    /**
     * Test method for {@link se.vgregion.core.domain.calendar.CalendarItem#getDayOfWeek()}.
     */
    @Test
    public final void testGetDayOfWeek() {
        String dayOfWeek = item.getDayOfWeek();
        assertEquals("Lördag", dayOfWeek);
    }

    /**
     * Test method for {@link se.vgregion.core.domain.calendar.CalendarItem#getDayOfMonth()}.
     */
    @Test
    public final void testGetDayOfMonth() {
        String dayOfMonth = item.getDayOfMonth();
        assertEquals("1", dayOfMonth);
    }

    /**
     * Test method for {@link se.vgregion.core.domain.calendar.CalendarItem#getMonthOfYear()}.
     */
    @Test
    public final void testGetMonthOfYear() {
        String monthOfYear = item.getMonthOfYear();
        assertEquals("Maj", monthOfYear);
    }

    /**
     * Test method for {@link se.vgregion.core.domain.calendar.CalendarItem#getStartTime()}.
     */
    @Test
    public final void testGetStartTime() {
        String startTime = item.getStartTime();
        assertEquals("12.00", startTime);
    }

    /**
     * Test method for {@link se.vgregion.core.domain.calendar.CalendarItem#getEndTime()}.
     */
    @Test
    public final void testGetEndTime() {
        String endTime = item.getEndTime();
        assertEquals("13.30", endTime);
    }

    /**
     * Test method for {@link se.vgregion.core.domain.calendar.CalendarItem#getCalendarType()}.
     */
    @Test
    public final void testGetCalendarType() {
        String calendarType = item.getCalendarType();
        assertEquals("Private", calendarType);
    }
}
