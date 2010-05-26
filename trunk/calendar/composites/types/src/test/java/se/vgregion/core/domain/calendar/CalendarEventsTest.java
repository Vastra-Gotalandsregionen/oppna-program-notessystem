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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.matchers.LessThan;

/**
 * @author Anders Asplund
 * 
 */
public class CalendarEventsTest {

    // @Mock
    private List<CalendarItem> calendarItems = new ArrayList<CalendarItem>();

    private CalendarEvents calendarEvents = new CalendarEvents();

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        calendarEvents.setCalendarItems(calendarItems);
    }

    /**
     * Test method for {@link se.vgregion.core.domain.calendar.CalendarEvents#getCalendarItemsGroupedByStartDate()}
     * .
     */
    @Test
    public final void testGetCalendarItemsGroupedByStartDate() throws Exception {
        // Given
        List<CalendarItem> items = new ArrayList<CalendarItem>();

        items.add(createStubbedCalendarItem(new Interval(new DateTime(2010, 5, 1, 12, 0, 0, 0), new DateTime(2010,
                5, 1, 13, 0, 0, 0))));
        items.add(createStubbedCalendarItem(new Interval(new DateTime(2010, 5, 1, 11, 0, 0, 0), new DateTime(2010,
                5, 1, 12, 0, 0, 0))));
        items.add(createStubbedCalendarItem(new Interval(new DateTime(2010, 4, 4, 12, 0, 0, 0), new DateTime(2010,
                4, 4, 13, 0, 0, 0))));
        calendarEvents.setCalendarItems(items);
        // When
        List<List<CalendarItem>> calendarItemsGroupedByStartDate = calendarEvents
                .getCalendarItemsGroupedByStartDate();
        // Then
        assertEquals(2, calendarItemsGroupedByStartDate.size());
        assertEquals(1, calendarItemsGroupedByStartDate.get(0).size());
        assertEquals(2, calendarItemsGroupedByStartDate.get(1).size());
        assertThat(calendarItemsGroupedByStartDate.get(0).get(0), new LessThan<CalendarItem>(
                calendarItemsGroupedByStartDate.get(1).get(0)));
    }

    @Test
    public void shouldReturnEmptyListIfCalendarItemsIsNull() throws Exception {
        // Given
        calendarEvents.setCalendarItems(null);

        // When
        List<List<CalendarItem>> calendarItemsGroupedByStartDate = calendarEvents
                .getCalendarItemsGroupedByStartDate();

        // Then
        assertEquals(0, calendarItemsGroupedByStartDate.size());
        assertTrue(calendarItemsGroupedByStartDate.isEmpty());
    }

    @Test
    public void shouldReturnEmptyListIfCalendarItemsIsEmpty() throws Exception {
        // Given
        List<CalendarItem> emptyList = Collections.emptyList();
        calendarEvents.setCalendarItems(emptyList);

        // When
        List<List<CalendarItem>> calendarItemsGroupedByStartDate = calendarEvents
                .getCalendarItemsGroupedByStartDate();

        // Then
        assertEquals(0, calendarItemsGroupedByStartDate.size());
        assertTrue(calendarItemsGroupedByStartDate.isEmpty());
    }

    @Test
    public void sholdFilterOutCalendarItemsWithNullInterval() throws Exception {
        // Given
        List<CalendarItem> items = new ArrayList<CalendarItem>();

        items.add(createStubbedCalendarItem(new Interval(new DateTime(2010, 5, 1, 12, 0, 0, 0), new DateTime(2010,
                5, 1, 13, 0, 0, 0))));
        items.add(createStubbedCalendarItem(null));
        items.add(createStubbedCalendarItem(new Interval(new DateTime(2010, 4, 4, 12, 0, 0, 0), new DateTime(2010,
                4, 4, 13, 0, 0, 0))));
        calendarEvents.setCalendarItems(items);

        // When
        calendarEvents.filter();

        // Then
        assertEquals(2, calendarEvents.getCalendarItems().size());
    }

    private CalendarItem createStubbedCalendarItem(Interval interval) {
        CalendarItem item = new CalendarItem();
        item.setCalendarType("CalendarType");
        item.setInterval(interval);
        return item;
    }

}
