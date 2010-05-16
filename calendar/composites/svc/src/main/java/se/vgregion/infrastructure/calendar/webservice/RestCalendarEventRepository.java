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
package se.vgregion.infrastructure.calendar.webservice;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestOperations;

import se.vgregion.calendar.CalendarEvent;
import se.vgregion.calendar.CalendarEventRepository;
import se.vgregion.calendar.WeekOfYear;

/**
 * @author Anders Asplund - Callista Enterprise
 * 
 */
@Repository
public class RestCalendarEventRepository implements CalendarEventRepository {

    private static final String NOTES_CALENDAR_GET = "http://localhost:8080/getinfo.xml";
    // private static final String NOTES_CALENDAR_GET =
    // "http://aida.vgregion.se/calendar.nsf/getinfo?openagent&userid={userid}&week={week}&year={year}";
    private RestOperations restTemplate;

    @Autowired
    public RestCalendarEventRepository(RestOperations restOperations) {
        restTemplate = restOperations;
    }

    /*
     * (non-Javadoc)
     * 
     * @see se.vgregion.calendar.CalendarEventRepository#findCalendarEvents(java.lang.String)
     */
    public List<CalendarEvent> findCalendarEvents(String userId) {
        return findCalendarEvents(userId, WeekOfYear.getCurrentWeek());
    }

    /*
     * (non-Javadoc)
     * 
     * @see se.vgregion.calendar.CalendarEventRepository#findCalendarEvents(java.lang.String,
     * se.vgregion.calendar.WeekOfYear)
     */
    public List<CalendarEvent> findCalendarEvents(String userId, WeekOfYear weekOfYear) {
        CalendarEvents events = restTemplate.getForObject(NOTES_CALENDAR_GET, CalendarEvents.class);
        // CalendarEvents events = restTemplate.getForObject(NOTES_CALENDAR_GET, CalendarEvents.class, weekOfYear
        // .getWeekNumber().getValue(), weekOfYear.getYear().getValue());
        if (events.message.equalsIgnoreCase("ERROR")) {
            // TODO: Handle error.
        }
        return events.eventList;

    }

    @SuppressWarnings("unused")
    @XmlRootElement(name = "calendarItems")
    private static class CalendarEvents {
        @XmlElement
        private String status;
        @XmlElement
        private String message;
        @XmlElement(name = "item")
        private List<CalendarEvent> eventList;
    }

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        CalendarEventRepository repo = applicationContext.getBean(RestCalendarEventRepository.class);
        List<CalendarEvent> events = repo.findCalendarEvents("");
        System.out.println(events);
    }
}
