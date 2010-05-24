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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestOperations;

import se.vgregion.core.domain.calendar.CalendarEventRepository;
import se.vgregion.core.domain.calendar.CalendarEvents;
import se.vgregion.core.domain.calendar.CalendarEventPeriod;

/**
 * @author Anders Asplund - Callista Enterprise
 * 
 */
@Repository
public class RestCalendarEventRepository implements CalendarEventRepository {

    private static final String NOTES_CALENDAR_GET = "http://aida.vgregion.se/calendar.nsf/getinfo?openagent&userid={userid}&year={year}&month={month}&day={day}&period={period}";
    private RestOperations restTemplate;

    @Autowired
    public RestCalendarEventRepository(RestOperations restOperations) {
        restTemplate = restOperations;
    }

    @Override
    public CalendarEvents findCalendarEventsByCalendarPeriod(String userId, CalendarEventPeriod period) {
        CalendarEvents events = restTemplate.getForObject(NOTES_CALENDAR_GET, CalendarEvents.class, userId, period
                .getStartDate().getYear(), period.getStartDate().getMonthOfYear(), period.getStartDate()
                .getDayOfMonth(), period.getDays().getDays());
        return events;
    }

}
