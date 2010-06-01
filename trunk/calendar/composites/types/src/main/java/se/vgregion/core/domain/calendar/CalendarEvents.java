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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Wraps a calendar web service response.
 * 
 * @author Anders Asplund - Callista Enterprise
 */
@XmlRootElement(name = "calendarItems")
public class CalendarEvents {

    private static final long serialVersionUID = -8404092455565896114L;

    /**
     * Returns an empty {@link CalendarEvents}.
     */
    public static final CalendarEvents EMPTY_CALENDAR_EVENTS = new CalendarEvents();

    @XmlElement
    private String status;
    @XmlElement
    private String message;
    private List<CalendarItem> calendarItems;

    static {
        List<CalendarItem> e = Collections.emptyList();
        EMPTY_CALENDAR_EVENTS.setCalendarItems(e);
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<CalendarItem> getCalendarItems() {
        return calendarItems;
    }

    @XmlElementWrapper(name = "items")
    @XmlElement(name = "item")
    public void setCalendarItems(List<CalendarItem> calendarItems) {
        this.calendarItems = calendarItems;
    }

    /**
     * Groups calendar items in order of the start date of the calendar item.
     * 
     * @return an unmodifiable list of list with calendar items
     * @author Anders Asplund - Callista Enterprise
     */
    public List<List<CalendarItem>> getCalendarItemsGroupedByStartDate() {
        if (calendarItems == null || calendarItems.isEmpty()) {
            return Collections.emptyList();
        }
        List<CalendarItem> sortedItems = new ArrayList<CalendarItem>(calendarItems);
        Collections.sort(sortedItems);
        List<List<CalendarItem>> groupedItems = new ArrayList<List<CalendarItem>>();
        int fromIndex = 0;
        for (int toIndex = 0; toIndex < sortedItems.size(); toIndex++) {
            CalendarItem fromItem = sortedItems.get(fromIndex);
            CalendarItem toItem = sortedItems.get(toIndex);

            if (!toItem.getDayOfWeek().equals(fromItem.getDayOfWeek())) {
                groupedItems.add(Collections.unmodifiableList(sortedItems.subList(fromIndex, toIndex)));
                fromIndex = toIndex;
            }
        }
        groupedItems.add(Collections.unmodifiableList(sortedItems.subList(fromIndex, sortedItems.size())));
        return Collections.unmodifiableList(groupedItems);
    }

    /**
     * Filters out all calendar items with a valid interval.
     * 
     * @return a copy of this calendarevents with the calendar items filtered
     */
    public CalendarEvents filterOutCalendarItemsWithValidInterval() {
        List<CalendarItem> filteredItems = new ArrayList<CalendarItem>();
        CalendarEvents events = new CalendarEvents();
        for (CalendarItem item : calendarItems) {
            if (item.getInterval() != null) {
                filteredItems.add(item);
            }
        }
        events.setCalendarItems(filteredItems);
        events.message = getMessage();
        events.status = getStatus();
        return events;
    }

}