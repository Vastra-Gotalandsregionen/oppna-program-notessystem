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

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import se.vgregion.core.domain.patterns.valueobjects.AbstractValueObject;

/**
 * @author Anders Asplund - Callista Enterprise
 * 
 */
public class WeekOfYear extends AbstractValueObject<WeekOfYear> implements Serializable {
    private static final long serialVersionUID = -895584731434843141L;
    private Year year;
    private WeekNumber weekNumber;

    public WeekOfYear(int year, int weekNumber) {
        this(new Year(year), new WeekNumber(weekNumber));
    }

    public WeekOfYear(Year year, WeekNumber weekNumber) {
        if (!WeekOfYear.isValid(year, weekNumber)) {
            throw new IllegalArgumentException("The weeknumber(" + weekNumber
                    + ") is invalid for the specified year(" + year + ").");
        }
        this.year = year;
        this.weekNumber = weekNumber;
    }

    private static boolean isValid(Year year, WeekNumber weekNumber) {
        return true;
    }

    public Year getYear() {
        return year;
    }

    public WeekNumber getWeekNumber() {
        return weekNumber;
    }

    public static WeekOfYear getCurrentWeek() {
        return getWeekFromDate(new Date());
    }

    public static WeekOfYear getWeekFromDate(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int weekNumber = calendar.get(Calendar.WEEK_OF_YEAR);
        int year = calendar.get(Calendar.YEAR);
        return new WeekOfYear(new Year(year), new WeekNumber(weekNumber));
    }

    @Override
    public String toString() {
        return year + "W" + weekNumber;
    }

    public WeekOfYear getNextWeek() {
        return new WeekOfYear(year.getValue(), weekNumber.getValue() + 1);
    }

    public WeekOfYear getPreviousWeek() {
        return new WeekOfYear(year.getValue(), weekNumber.getValue() - 1);
    }
}
