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

import se.vgregion.core.domain.calendar.Year;

public class YearTest {

    @Test
    public void shouldBeValidBetween2000And2100() throws Exception {
        for (int i = 2000; i <= 2100; i++) {
            // When
            Year year = new Year(i);

            // Then
            assertNotNull(year);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldBeInValidLessThan2000() throws Exception {
        new Year(1999);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldBeInValidLMoreThan2100() throws Exception {
        new Year(2101);
    }

    @Test
    public void shouldReturnSameYearAsConstructed() throws Exception {
        // Given
        int expectedYear = 2000;
        Year y = new Year(expectedYear);

        // When
        int actualYear = y.getValue();

        // Then
        assertEquals(expectedYear, actualYear);
    }

    @Test
    public void toStringShouldReturnYearOnly() throws Exception {
        for (int expectedYear = 2000; expectedYear <= 2100; expectedYear++) {
            // Given
            Year year = new Year(expectedYear);

            // When
            String actualYear = year.toString();

            // Then
            assertEquals(String.valueOf(expectedYear), actualYear);
        }
    }
}
