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

package se.vgregion.calendar;

import static org.junit.Assert.*;

import org.junit.Test;

import se.vgregion.core.domain.calendar.WeekNumber;

public class WeekNumberTest {

    @Test
    public void shouldBeValidBetween1And53() throws Exception {
        for (int number = 1; number <= 53; number++) {
            // When
            WeekNumber weekNumber1 = new WeekNumber(number);

            // Then
            assertNotNull(weekNumber1);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldBeInValidLessThan1() throws Exception {
        new WeekNumber(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldBeInValidLMoreThan53() throws Exception {
        new WeekNumber(54);
    }

    @Test
    public void shouldReturnSameWeekNumberAsConstructed() throws Exception {
        // Given
        int expectedWeekNumber = 1;
        WeekNumber wn = new WeekNumber(expectedWeekNumber);

        // When
        int actualWeekNumber = wn.getValue();

        // Then
        assertEquals(expectedWeekNumber, actualWeekNumber);
    }

    @Test
    public void toStringShouldAlwaysReturnATwoDigitWeekNumber() throws Exception {
        for (int expectedWeekNumber = 1; expectedWeekNumber <= 53; expectedWeekNumber++) {
            // Given
            WeekNumber wn = new WeekNumber(expectedWeekNumber);

            // When
            String actualWeekNumber = wn.toString();

            // Then
            if (expectedWeekNumber < 10) {
                assertEquals("0" + expectedWeekNumber, actualWeekNumber);
            } else {
                assertEquals(String.valueOf(expectedWeekNumber), actualWeekNumber);
            }
        }
    }
}
