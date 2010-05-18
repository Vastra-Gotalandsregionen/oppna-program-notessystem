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
package se.vgregion.calendar;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Test;

import se.vgregion.core.domain.calendar.WeekNumber;
import se.vgregion.core.domain.calendar.WeekOfYear;
import se.vgregion.core.domain.calendar.Year;

/**
 * @author Anders Asplund - Callsita Enterprise
 * 
 */
public class WeekOfYearTest {

    @Test
    public void shouldReturnAValidWeekOfYearFromADate() throws Exception {
        // Given
        int expectedYear = 2010;
        int expectedWeek = 19;
        WeekOfYear expectedWeekOfYear = new WeekOfYear(new Year(expectedYear), new WeekNumber(expectedWeek));

        Calendar calendar = new GregorianCalendar();
        calendar.set(expectedYear, 4, 13);
        Date date = calendar.getTime();

        // When
        WeekOfYear actualWeekOfYear = WeekOfYear.getWeekFromDate(date);

        // Then
        assertEquals(expectedWeekOfYear, actualWeekOfYear);
    }

    @Test
    public void toStringShouldBeOfCorrectFormat() throws Exception {
        int year = 2010;
        int week = 19;
        String expectedWeekString = year + "W" + week;

        // Given
        WeekOfYear weekOfYear = new WeekOfYear(new Year(year), new WeekNumber(week));

        // When
        String acutalWeekOfYearString = weekOfYear.toString();

        // Then
        assertEquals(expectedWeekString, acutalWeekOfYearString);
    }
}
