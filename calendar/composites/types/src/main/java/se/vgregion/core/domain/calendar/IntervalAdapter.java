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
package se.vgregion.core.domain.calendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Interval;

/**
 * @author Anders Asplund - Callista Enterprise
 * 
 */
public class IntervalAdapter extends XmlAdapter<CalendarEventItemPeriod, Interval> {

    @Override
    public CalendarEventItemPeriod marshal(Interval v) throws Exception {
        throw new UnsupportedOperationException("Marshalling is unsupported for now.");
    }

    @Override
    public Interval unmarshal(CalendarEventItemPeriod eventInterval) throws Exception {
        if (StringUtils.isBlank(eventInterval.getStartDate()) || StringUtils.isBlank(eventInterval.getEndDate())) {
            throw new VgrCalendarWebServiceException("Invalid date");
        }
        long start = parseStringDate(eventInterval.getStartDate(), eventInterval.getStartTime());
        long end = parseStringDate(eventInterval.getEndDate(), eventInterval.getEndTime());
        Interval interval = new Interval(start, end);
        return interval;
    }

    private Long parseStringDate(String date, String time) throws ParseException {
        StringBuilder formatString = new StringBuilder("yyyy-MM-ddHH:mm:ss");
        if (StringUtils.isBlank(time)) {
            time = "00:00:00";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(formatString.toString());
        Date d = sdf.parse(date + time);
        Calendar calendar = Calendar.getInstance(new Locale("sv", "SE"));
        calendar.setTime(d);
        long dt = d.getTime() + calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET);
        return dt;
    }
}
