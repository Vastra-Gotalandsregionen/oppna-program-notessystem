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

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.component.VEvent;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.AbstractHandler;
import org.mortbay.jetty.handler.HandlerList;
import se.vgregion.core.domain.calendar.CalendarEvents;
import se.vgregion.core.domain.calendar.CalendarEventsPeriod;
import se.vgregion.core.domain.calendar.CalendarEventsRepository;
import se.vgregion.exchange.service.EwsService;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;

/**
 * @author Anders Asplund - Callista Enterprise
 */
public class NotesCalendarServiceTest {
    private static final String USER_ID_1 = "user1";

    private CalendarService notesCalendarService = null;

    @Mock
    private CalendarEventsRepository calendarEventRepository;

    @Mock
    private CalendarEvents calendarEvents;

    @Mock
    private EwsService ewsService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        notesCalendarService = new CalendarServiceImpl(calendarEventRepository, ewsService);
    }

    @Test
    public void shoudReturnACalendarEvents() throws Exception {
        // // Given
        given(
                calendarEventRepository.findCalendarEventsByCalendarPeriod(anyString(),
                        any(CalendarEventsPeriod.class))).willReturn(calendarEvents);
        given(calendarEvents.filterOutCalendarItemsWithValidInterval()).willReturn(calendarEvents);
        // When
        CalendarEvents listOfEvents = notesCalendarService.getCalendarEvents(USER_ID_1, new CalendarEventsPeriod(new DateTime(), Days.SEVEN));

        // Then
        assertNotNull(listOfEvents);
    }

    @Test
    public void testGetCalendarEventsFromIcalUrl() throws CalendarServiceException {

        //Given - Override parseIcalUrl method
        CalendarServiceImpl service = new CalendarServiceImpl(null, null) {
            Calendar parseIcalUrl(String url) {
                Component component1 = new VEvent(new net.fortuna.ical4j.model.DateTime(new Date().getTime()),
                        new net.fortuna.ical4j.model.DateTime(new Date().getTime() + 1000 * 60 * 60 * 24),
                        "Testhändelse");

                Component component2 = new VEvent(new net.fortuna.ical4j.model.Date(new Date().getTime()),
                        new net.fortuna.ical4j.model.Date(new Date().getTime() + 1000 * 60 * 60 * 24),
                        "Testhändelse heldagar");


                ComponentList componentList = new ComponentList();
                componentList.add(component1);
                componentList.add(component2);

                Calendar calendar = new Calendar(componentList);

                return calendar;
            }
        };

        CalendarEvents eventsFromIcalUrl = service.getCalendarEventsFromIcalUrl("anyUrl", new CalendarEventsPeriod(new DateTime(new Date().getTime()),
                Days.SEVEN), "anyType");

        System.out.println(eventsFromIcalUrl);

        assertEquals(2, eventsFromIcalUrl.getCalendarItems().size());
    }

    @Test
    public void testParsIcalUrl() throws Exception {
        Server server = new Server(8484);

        HandlerList handlerList = new HandlerList();
        handlerList.addHandler(new AbstractHandler() {
            @Override
            public void handle(String s, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, int i) throws IOException, ServletException {

                ServletOutputStream output = httpServletResponse.getOutputStream();

                InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("icalFile.ics");

                copy(resourceAsStream, output);

                httpServletResponse.setStatus(HttpServletResponse.SC_OK);

                output.close();
            }
        });

        server.addHandler(handlerList);

        server.start();

        CalendarServiceImpl service = new CalendarServiceImpl(null, null);

        service.parseIcalUrl("http://localhost:8484/");

        server.stop();
    }

    private static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] b = new byte[1024];
        int read;
        while ((read = in.read(b)) != -1) {
            out.write(b, 0, read);
        }
    }
    //
    // @Test
    // public void shouldReturnEmptyListIfUserIdIsNull() throws Exception {
    // // When
    // List<CalendarItem> calendarEvents = notesCalendarService.getCalendarEvents(null);
    //
    // // Then
    // assertNotNull(calendarEvents);
    // assertTrue(calendarEvents.isEmpty());
    // }
    //
    // @Test
    // public void shouldReturnEmptyListIfEmptyUserId() throws Exception {
    // // When
    // List<CalendarItem> calendarEvents = notesCalendarService.getCalendarEvents("");
    //
    // // Then
    // assertNotNull(calendarEvents);
    // assertTrue(calendarEvents.isEmpty());
    // }
}
