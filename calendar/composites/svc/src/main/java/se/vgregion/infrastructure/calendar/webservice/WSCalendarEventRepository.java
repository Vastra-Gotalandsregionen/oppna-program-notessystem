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

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import se.vgregion.calendar.CalendarEvent;
import se.vgregion.calendar.CalendarEventRepository;
import se.vgregion.calendar.WeekOfYear;

/**
 * @author Anders Asplund - Callista Enterprise
 * 
 */
@Repository
public class WSCalendarEventRepository implements CalendarEventRepository {

    private static final String WS_BASE_ADDRESS = "http://localhost:8080/getinfo.xml";
    private RestTemplate restTemplate;

    public WSCalendarEventRepository() {
        restTemplate = new RestTemplate();
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
        return restTemplate.getForObject(WS_BASE_ADDRESS, CalendarEvents.class).getEvents();

    }

    @XmlRootElement(name = "calendarItems")
    private class CalendarEvents {
        private CalendarEvents() {

        }

        @XmlElement
        private String status;
        @XmlElement
        private String message;
        @XmlElement(name = "item")
        private List<CalendarEvent> events;

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public List<CalendarEvent> getEvents() {
            return events;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }

    public static void main(String[] args) {
        CalendarEventRepository repo = new WSCalendarEventRepository();
        List<CalendarEvent> events = repo.findCalendarEvents("");
        System.out.println(events);
    }
}
