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

import java.text.SimpleDateFormat;

import javax.xml.bind.annotation.adapters.XmlAdapter;

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
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ssyyyy-MM-dd");
        long start = sdf.parse(eventInterval.getStartTime() + eventInterval.getStartDate()).getTime();
        long end = sdf.parse(eventInterval.getEndTime() + eventInterval.getEndDate()).getTime();
        return new Interval(start, end);
    }

}
