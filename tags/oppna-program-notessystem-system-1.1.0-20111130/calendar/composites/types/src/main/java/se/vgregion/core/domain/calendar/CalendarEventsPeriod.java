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

import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.DateTime;
import org.joda.time.Days;

import se.vgregion.core.domain.patterns.valueobjects.AbstractValueObject;

/**
 * @author Anders Asplund - Callista Enterprise
 * 
 */
public class CalendarEventsPeriod extends AbstractValueObject<CalendarEventsPeriod> {

    private static final long serialVersionUID = -7922598817193391527L;
    private DateTime startDate;
    private Days days;

    /**
     * Default period is set to seven days.
     */
    public static final Days DEFAULT_PERIOD_LENGTH = Days.SEVEN;

    /**
     * Constructs a new CalendarEventPeriod.
     * 
     * @param startDate
     *            the start date of the period
     * @param days
     *            number of days the period should span
     */
    public CalendarEventsPeriod(DateTime startDate, Days days) {
        super();
        this.startDate = startDate;
        this.days = days;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    /**
     * Adds the number of days specified in the instance to the start date and returns a new DateTime.
     * 
     * @return the last day of the period.
     */
    public DateTime getEndDate() {
        return startDate.plus(days.minus(Days.ONE));
    }

    public Days getDays() {
        return days;
    }

    /**
     * Constructs a new CalendarEventsPeriod with the start date as the first day after the current period. The new
     * CalendarEventsPeriod gets the same period length as this instance. The difference between the start date of
     * the new period and the start date of the current period is the same as the days of the current period.
     * 
     * @return a new CalendarEventsPeriod with the start date as the first day after the current period
     */
    public CalendarEventsPeriod next() {
        return new CalendarEventsPeriod(startDate.plusDays(days.getDays()), days);
    }

    /**
     * Constructs a new CalendarEventsPeriod with the end date as the last day before the current period. The new
     * CalendarEventsPeriod gets the same period length as this instance. The difference between the start date of
     * the new period and the start date of the current period is the same as the days of the current period.
     * 
     * @return a new CalendarEventsPeriod with the end date as the last day before the current period
     */
    public CalendarEventsPeriod previous() {
        return new CalendarEventsPeriod(startDate.minusDays(days.getDays()), days);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
