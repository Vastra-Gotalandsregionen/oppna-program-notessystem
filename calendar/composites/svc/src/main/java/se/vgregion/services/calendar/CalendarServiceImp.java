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

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import se.vgregion.calendar.CalendarEvent;
import se.vgregion.calendar.CalendarEventRepository;
import se.vgregion.calendar.WeekOfYear;

public class CalendarServiceImp implements CalendarService {

    @Autowired
    private CalendarEventRepository calendarEventRepository;

    @Override
    public List<CalendarEvent> getCalendarEvents(String userId) {
        if (StringUtils.isBlank(userId)) {
            return Collections.emptyList();
        }
        return calendarEventRepository.findCalendarEvents(userId);
    }

    @Override
    public void setCalendarEventRepository(CalendarEventRepository calendarEventRepository) {
        this.calendarEventRepository = calendarEventRepository;
    }

    @Override
    public List<CalendarEvent> getCalendarEvents(String userId, WeekOfYear weekOfYear) {
        if (StringUtils.isBlank(userId)) {
            return Collections.emptyList();
        }
        return calendarEventRepository.findCalendarEvents(userId, weekOfYear);
    }

}
