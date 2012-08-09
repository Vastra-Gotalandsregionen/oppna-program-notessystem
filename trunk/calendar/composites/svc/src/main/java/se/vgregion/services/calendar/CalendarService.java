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
 * Service interface for managing {@link CalendarEvents}.
 *
 * @author Anders Asplund - Callista Enterprise
 */
public interface CalendarService {
    /**
     * Find calendar events for a specific user and over a specified period.
     *
     * @param userId a userId
     * @param period a period
     * @return CalendarEvents containing all calendar items for user and period
     */
    CalendarEvents getCalendarEvents(String userId, CalendarEventsPeriod period);

    /**
     * Like {@link
     * CalendarService#getCalendarEvents(java.lang.String, se.vgregion.core.domain.calendar.CalendarEventsPeriod)}
     * but wraps the result in a {@link Future} and returns asynchronously.
     *
     * @param userId a userId
     * @param period a period
     * @return CalendarEvents containing all calendar items for user and period, wrapped in a {@link Future}
     */
    Future<CalendarEvents> getFutureCalendarEvents(String userId, CalendarEventsPeriod period);

    /**
     * Find calendar events from a specific URL, which should return content in iCal-format, for a specific period.
     *
     * @param url    the URL to retrieve the iCal content from
     * @param period the period which the calendar events should overlap in order to be included in the results
     * @param type   the type name which the calendar events should get, e.g. "Bob's Google calendar"
     * @return CalendarEvents from the given URL in the given period
     * @throws CalendarServiceException if the URL for some reason can't be read as iCal content
     */
    CalendarEvents getCalendarEventsFromIcalUrl(String url, CalendarEventsPeriod period, String type)
            throws CalendarServiceException;

    /**
     * Like {@link CalendarService#getCalendarEventsFromIcalUrl(java.lang.String,
       se.vgregion.core.domain.calendar.CalendarEventsPeriod, java.lang.String)} but wraps the result in a
     * {@link Future} and returns asynchronously.
     *
     * @param url    the URL to retrieve the iCal content from
     * @param period the period which the calendar events should overlap in order to be included in the results
     * @param type   the type name which the calendar events should get, e.g. "Bob's Google calendar"
     * @return CalendarEvents from the given URL in the given period, wrapped in a {@link Future}
     * @throws CalendarServiceException if the URL for some reason can't be read as iCal content
     */
    Future<CalendarEvents> getFutureCalendarEventsFromIcalUrl(String url, CalendarEventsPeriod period, String type)
            throws CalendarServiceException;

    /**
     * Validates that the response from the given URL can be parsed as iCal content.
     *
     * @param iCalUrl the URL
     * @throws CalendarServiceException if the response can't be parsed as iCal content
     */
    void validateAsValidIcalUrl(String iCalUrl) throws CalendarServiceException;
}
