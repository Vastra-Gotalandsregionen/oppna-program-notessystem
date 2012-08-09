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

import se.vgregion.core.domain.calendar.CalendarEvents;
import se.vgregion.core.domain.calendar.CalendarEventsPeriod;

import java.util.concurrent.Future;

/**
 * @author Anders Asplund - Callista Enterprise
 * 
 */
public interface CalendarService {
    /**
     * Find calendar events for a specific user and over a specified period.
     * 
     * @param userId
     *            a userId
     * @param period
     *            a period
     * @return CalendarEvents containing all calendar items for user and period
     */
    CalendarEvents getCalendarEvents(String userId, CalendarEventsPeriod period);

    Future<CalendarEvents> getFutureCalendarEvents(String userId, CalendarEventsPeriod period);

    CalendarEvents getCalendarEventsFromIcalUrl(String url, CalendarEventsPeriod period, String type) throws CalendarServiceException;

    Future<CalendarEvents> getFutureCalendarEventsFromIcalUrl(String url, CalendarEventsPeriod period, String type) throws CalendarServiceException;

    void validateAsValidIcalUrl(String iCalUrl) throws CalendarServiceException;
}
