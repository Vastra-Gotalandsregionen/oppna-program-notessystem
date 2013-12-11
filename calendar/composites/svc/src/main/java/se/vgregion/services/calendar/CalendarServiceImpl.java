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

package se.vgregion.services.calendar;

import com.microsoft.schemas.exchange.services._2006.types.CalendarItemType;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.component.VEvent;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import se.vgregion.core.domain.calendar.CalendarEvents;
import se.vgregion.core.domain.calendar.CalendarEventsPeriod;
import se.vgregion.core.domain.calendar.CalendarEventsRepository;
import se.vgregion.core.domain.calendar.CalendarItem;
import se.vgregion.exchange.service.EwsService;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.TimeZone;
import java.util.concurrent.Future;

@Service
public class CalendarServiceImpl implements CalendarService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CalendarServiceImpl.class);
    private CalendarEventsRepository calendarEventsRepository;
    private EwsService ewsService;

    /**
     * Constructs a CalendarServiceImpl.
     *
     * @param eventsRepository an eventsRepository
     */
    @Autowired
    public CalendarServiceImpl(CalendarEventsRepository eventsRepository, EwsService ewsService) {
        this.calendarEventsRepository = eventsRepository;
        this.ewsService = ewsService;
    }

    /*
     * (non-Javadoc)
     * 
     * @see se.vgregion.services.calendar.CalendarService#getCalendarEvents(java.lang.String,
     * se.vgregion.core.domain.calendar.CalendarEventsPeriod)
     */
    @Override
    public CalendarEvents getCalendarEvents(String userId, CalendarEventsPeriod period) {
        CalendarEvents events = calendarEventsRepository.findCalendarEventsByCalendarPeriod(userId, period);

        CalendarEvents ewsCalendarEvents = getEwsCalendarEvents(userId, period);

        events.getCalendarItems().addAll(ewsCalendarEvents.getCalendarItems());

        return events.filterOutCalendarItemsWithValidInterval();
    }

    @Override
    @Async
    public Future<CalendarEvents> getFutureCalendarEvents(String userId, CalendarEventsPeriod period) {
        return new AsyncResult<CalendarEvents>(getCalendarEvents(userId, period));
    }

    @Override
    public CalendarEvents getCalendarEventsFromIcalUrl(String url, CalendarEventsPeriod period, String type)
            throws CalendarServiceException {
        CalendarEvents calendarEvents = new CalendarEvents();
        calendarEvents.setCalendarItems(new ArrayList<CalendarItem>());

        Interval queryInterval = new Interval(period.getStartDate(), period.getEndDate());

        try {
            Calendar calendar = parseIcalUrl(url);

            Iterator<VEvent> itr = calendar.getComponents(
                    Component.VEVENT).iterator();

            while (itr.hasNext()) {
                VEvent vEvent = itr.next();

                Date startDate = vEvent.getStartDate().getDate();
                Date endDate = vEvent.getEndDate().getDate();

                boolean wholeDays = !(startDate instanceof net.fortuna.ical4j.model.DateTime);
                if (wholeDays) {
                    // Whole day event. Have had timezone problems with whole day events. Workaround it.
                    String datePartStart = startDate.toString();
                    String datePartEnd = endDate.toString();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                    sdf.setTimeZone(TimeZone.getDefault());
                    try {
                        java.util.Date startParsed = sdf.parse(datePartStart);
                        java.util.Date endParsed = sdf.parse(datePartEnd);

                        startDate = new net.fortuna.ical4j.model.DateTime(startParsed);
                        // Subtract a millisecond so we don't regard the event as taking place on the next day. Now we
                        // end the event one millisecond before the day turns.
                        endDate = new net.fortuna.ical4j.model.DateTime(endParsed.getTime() - 1);
                    } catch (ParseException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }

                Interval interval = new Interval(startDate.getTime(), endDate.getTime());

                if (interval.overlaps(queryInterval)) {
                    CalendarItem item = new CalendarItem();
                    item.setInterval(interval);
                    item.setTitle(vEvent.getSummary().getValue());
                    item.setCalendarType(type);
                    item.setWholeDays(wholeDays);
                    calendarEvents.getCalendarItems().add(item);
                }
            }
        } catch (IOException ioe) {
            throw new CalendarServiceException(ioe);
        } catch (ParserException pe) {
            throw new CalendarServiceException(pe);
        }

        return calendarEvents;
    }

    @Override
    @Async
    public Future<CalendarEvents> getFutureCalendarEventsFromIcalUrl(String url, CalendarEventsPeriod period,
                                                                     String type) throws CalendarServiceException {
        return new AsyncResult<CalendarEvents>(getCalendarEventsFromIcalUrl(url, period, type));
    }

    Calendar parseIcalUrl(String url) throws IOException, ParserException {
        BufferedInputStream bis = null;
        InputStream in = null;
        try {
            bis = null;
            in = null;

            URL url2 = new URL(url);
            in = url2.openStream();
            bis = new BufferedInputStream(in);

            CalendarBuilder builder = new CalendarBuilder();
            return builder.build(bis);
        } finally {
            if (bis != null) {
                bis.close();
            }
            if (in != null) {
                in.close();
            }
        }
    }

    @Override
    public void validateAsValidIcalUrl(String iCalUrl) throws CalendarServiceException {
        try {
            parseIcalUrl(iCalUrl);
        } catch (Exception ex) {
            throw new CalendarServiceException(ex);
        }
    }

    private CalendarEvents getEwsCalendarEvents(String userId, CalendarEventsPeriod period) {
        java.util.Date startDate = period.getStartDate().toGregorianCalendar().getTime();
        java.util.Date endDate = period.getEndDate().toGregorianCalendar().getTime();
        List<CalendarItemType> ewsCalendarItems = ewsService.fetchCalendarEvents(userId, startDate, endDate);

        CalendarEvents calendarEvents = new CalendarEvents();
        calendarEvents.setCalendarItems(new ArrayList<CalendarItem>());

        for (CalendarItemType calendarItemType : ewsCalendarItems) {
            CalendarItem item = new CalendarItem();
            item.setCalendarType("Outlook");
            String location = calendarItemType.getLocation();
            location = location == null ? "" : location;
            item.setTitle(calendarItemType.getSubject() + " - <b>" + location + "</b>");

            long start = calendarItemType.getStart().toGregorianCalendar().getTimeInMillis();
            long end = calendarItemType.getEnd().toGregorianCalendar().getTimeInMillis();

            item.setWholeDays(calendarItemType.isIsAllDayEvent());

            if (item.getWholeDays()) {
                // Subtract a millisecond so we don't regard the event as taking place on the next day. Now we
                // end the event one millisecond before the day turns.
                end -= 1;
                end = Math.max(start, end); // If startTime equals endTime we avoid end being less than start
            }

            Interval interval = new Interval(start, end);
            item.setInterval(interval);

            calendarEvents.getCalendarItems().add(item);
        }

        return calendarEvents;
    }


}
