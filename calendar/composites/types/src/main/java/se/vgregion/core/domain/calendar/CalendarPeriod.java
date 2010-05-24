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

package se.vgregion.core.domain.calendar;

import org.joda.time.DateTime;
import org.joda.time.Days;

import se.vgregion.core.domain.patterns.valueobjects.AbstractValueObject;

public class CalendarPeriod extends AbstractValueObject<CalendarPeriod> {

    private static final long serialVersionUID = -7922598817193391527L;
    private DateTime startDate;
    private Days days;

    public CalendarPeriod(DateTime startDate, Days days) {
        super();
        this.startDate = startDate;
        this.days = days;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public Days getDays() {
        return days;
    }

    public CalendarPeriod next() {
        return new CalendarPeriod(startDate.plusDays(days.getDays()), days);
    }

    public CalendarPeriod previous() {
        return new CalendarPeriod(startDate.minusDays(days.getDays()), days);
    }
}
