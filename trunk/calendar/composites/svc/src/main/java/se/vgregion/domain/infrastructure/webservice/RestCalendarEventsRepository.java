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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import se.vgregion.core.domain.calendar.CalendarEvents;
import se.vgregion.core.domain.calendar.CalendarEventsPeriod;
import se.vgregion.core.domain.calendar.CalendarEventsRepository;

/**
 * Implementation of {@link CalendarEventsRepository} which is used for restfull requests against service endpoint.
 * The service url is specified with the following pattern:
 * http://<host>?userid=<userid>&year=<year>&month=<month>&day=<day>&period=<period>
 *
 * @author Anders Asplund - Callista Enterprise
 */
public class RestCalendarEventsRepository implements CalendarEventsRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestCalendarEventsRepository.class);
    private String serviceUrl;
    private RestOperations restTemplate;
    private String serviceEndpoint;

    /**
     * Constructs a RestCalendarEventsRepository for a restfull reqest to the specified service endpoint.
     *
     * @param restTemplate a restTemplate to use for making a request
     */
    @Autowired
    public RestCalendarEventsRepository(RestOperations restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Sets the service endpoint.
     *
     * @param serviceEndpoint the service endpoint
     */
    public void setServiceEndpoint(String serviceEndpoint) {
        this.serviceEndpoint = serviceEndpoint;
        this.serviceUrl = serviceEndpoint + "userid={userid}&year={year}&month={month}&day={day}&period={period}";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * se.vgregion.core.domain.calendar.CalendarEventsRepository#findCalendarEventsByCalendarPeriod(java.lang.String
     * , se.vgregion.core.domain.calendar.CalendarEventsPeriod)
     */
    @Override
    public CalendarEvents findCalendarEventsByCalendarPeriod(String userId, CalendarEventsPeriod period) {
        CalendarEvents events = CalendarEvents.EMPTY_CALENDAR_EVENTS;
        try {
            LOGGER.debug("Calling the following web service: {}", serviceUrl);
            LOGGER.debug("Paramters sent to web service: userid={}, year={}, month={}, day={}, period={}",
                    new Object[]{userId, period.getStartDate().getYear(),
                            period.getStartDate().getMonthOfYear(), period.getStartDate().getDayOfMonth(),
                            period.getDays().getDays()});
            events = restTemplate.getForObject(serviceUrl, CalendarEvents.class, userId, period.getStartDate()
                    .getYear(), period.getStartDate().getMonthOfYear(), period.getStartDate().getDayOfMonth(),
                    period.getDays().getDays());
        } catch (RestClientException e) {
            LOGGER.error("Unable to retreive information from web service: {}. CalendarEventPeriod={}",
                    new Object[]{serviceUrl, period});
            LOGGER.error("Web service exception", e);
        }
        return events;
    }

}
