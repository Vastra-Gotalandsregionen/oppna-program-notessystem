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
package se.vgregion.core.domain.calendar.adapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Interval;

import se.vgregion.core.domain.calendar.CalendarItemPeriod;

/**
 * @author Anders Asplund - Callista Enterprise
 * 
 */
public class IntervalAdapter extends XmlAdapter<CalendarItemPeriod, Interval> {

    @Override
    public CalendarItemPeriod marshal(Interval v) throws Exception {
        throw new UnsupportedOperationException("Marshalling is unsupported for now.");
    }

    @Override
    public Interval unmarshal(CalendarItemPeriod eventInterval) throws Exception {
        if (StringUtils.isBlank(eventInterval.getStartDate()) || StringUtils.isBlank(eventInterval.getEndDate())) {
            throw new VgrCalendarWebServiceException("Invalid date");
        }
        long start = parseStringDate(eventInterval.getStartDate(), eventInterval.getStartTime());
        long end = parseStringDate(eventInterval.getEndDate(), eventInterval.getEndTime());
        return new Interval(start, end);
    }

    private Long parseStringDate(String dateStr, String timeStr) throws ParseException {
        StringBuilder formatString = new StringBuilder("yyyy-MM-ddHH:mm:ss");
        Calendar calendar = Calendar.getInstance(new Locale("sv", "SE"));
        SimpleDateFormat sdf = new SimpleDateFormat(formatString.toString());
        if (StringUtils.isBlank(timeStr)) {
            timeStr = "00:00:00";
        }
        Date date = sdf.parse(dateStr + timeStr);
        calendar.setTime(date);
        return date.getTime() + calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET);
    }
}
