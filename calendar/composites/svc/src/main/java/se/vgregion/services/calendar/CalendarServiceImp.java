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

import java.util.Calendar;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.vgregion.core.domain.calendar.*;

@Service
public class CalendarServiceImp implements CalendarService {

    private CalendarEventRepository calendarEventRepository;

    @Autowired
    public CalendarServiceImp(CalendarEventRepository eventRepository) {
        this.calendarEventRepository = eventRepository;
    }

    @Override
    public CalendarEvents getCalendarEvents(String userId) {
        return getCalendarEvents(userId, WeekOfYear.getCurrentWeek());
    }

    @Override
    public CalendarEvents getCalendarEvents(String userId, WeekOfYear weekOfYear) {
        return calendarEventRepository.findCalendarEventsById(new CalendarEventsId(userId, weekOfYear));
    }

    @Override
    public CalendarEvents getCalendarEvents(String userId, CalendarPeriod period) {
        Calendar calendar = Calendar.getInstance(new Locale("sv", "SE"));
        calendar.setTime(period.getStartDate());
        return calendarEventRepository.findCalendarEventsByCalendarPeriod(userId, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), period.getDays());
    }
}
