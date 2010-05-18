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

import java.util.*;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import se.vgregion.core.domain.patterns.entity.AbstractEntity;

@XmlRootElement(name = "calendarItems")
public class CalendarEvents extends AbstractEntity<CalendarEvents, CalendarEventsId> {

    private static final long serialVersionUID = -8404092455565896114L;
    private transient CalendarEventsId id;
    @XmlElement
    private String status;
    @XmlElement
    private String message;
    @XmlElementWrapper(name = "items")
    @XmlElement(name = "item")
    private List<CalendarItem> calendarItems;

    public void setCalendarEventsId(CalendarEventsId id) {
        this.id = id;
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

    public List<List<CalendarItem>> getCalendarItemsGroupedByStartDate() {
        List<CalendarItem> sortedItems = new ArrayList<CalendarItem>(calendarItems);
        Collections.sort(sortedItems, ASCENDING_BY_START_DATE);
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

    private static final Comparator<CalendarItem> ASCENDING_BY_START_DATE = new Comparator<CalendarItem>() {
        public int compare(final CalendarItem ce1, final CalendarItem ce2) {
            return ce1.getInterval().getStart().compareTo(ce2.getInterval().getStart());
        }
    };

    @Override
    public CalendarEventsId getId() {
        return id;
    }
}