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

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.vgregion.core.domain.calendar.CalendarItemPeriod;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Adapter used by Jaxb when unmarshalling the calendar web service content. It handles the conversion of the
 * content in the period-tag and a Joda time {@link org.joda.time.Interval}.
 *
 * @author Anders Asplund - Callista Enterprise
 * @see org.joda.time.Interval
 */
public class IntervalAdapter extends XmlAdapter<CalendarItemPeriod, Interval> {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntervalAdapter.class);

    /**
     * The adpater only supports unmarshalling for now.
     *
     * @param interval the interval that should be converted to a {@link CalendarItemPeriod}
     * @return {@link CalendarItemPeriod}
     * @throws UnsupportedOperationException The adpater only supports unmarshalling for now.
     * @author Anders Asplund - Callista Enterprise
     */
    @Override
    public CalendarItemPeriod marshal(Interval interval) {
        throw new UnsupportedOperationException("Marshalling is unsupported for now.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
     */

    /**
     * Jaxb uses this method to converts the period tag from the web service response to a Joda time
     * {@link org.joda.time.Interval}.
     *
     * @param period the {@link CalendarItemPeriod} that should be converted to an Interval.
     * @return {@link Interval}
     * @throws VgrCalendarWebServiceException if the web service response contains a valid start or end date.
     * @throws ParseException                 if unable to parse the date periods in the web service response.
     * @author Anders Asplund - Callista Enterprise
     */
    @Override
    public Interval unmarshal(CalendarItemPeriod period) throws VgrCalendarWebServiceException, ParseException {
        if (StringUtils.isBlank(period.getStartDate()) || StringUtils.isBlank(period.getEndDate())) {
            throw new VgrCalendarWebServiceException("Invalid date");
        }
        DateTime start = new DateTime(parseStringDate(period.getStartDate(), period.getStartTime()));
        DateTime end = new DateTime(parseStringDate(period.getEndDate(), period.getEndTime()));
        if (end.isBefore(start)) {
            LOGGER.error("Sluttid är före starttid: {}", period);
            throw new VgrCalendarWebServiceException("Sluttid är före starttid");
        }
        return new Interval(start, end);
    }

    private Long parseStringDate(String dateStr, String timeStr) throws ParseException {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-ddHH:mm:ssZ");
            if (StringUtils.isBlank(timeStr)) {
                timeStr = "00:00:00";
            }
            timeStr = checkTimeZoneFormat(timeStr);
            Date date = sdf.parse(dateStr + timeStr);
            return date.getTime();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private String checkTimeZoneFormat(String timeStr) {
        final int fourteen = 14;
        if (timeStr.length() == fourteen) {
            String[] timeParts = timeStr.split(":");
            StringBuilder tmpTimeStr = new StringBuilder();
            for (int i = 0; i < timeParts.length; i++) {
                if (i == 2 || i == timeParts.length - 1) {
                    tmpTimeStr.append(timeParts[i]);
                } else {
                    tmpTimeStr.append(timeParts[i] + ":");
                }
            }
            timeStr = tmpTimeStr.toString();
        } else {
            final int eight = 8;
            if (timeStr.length() == eight) {
                timeStr += "-0100";
            }
        }
        return timeStr;
    }
}
