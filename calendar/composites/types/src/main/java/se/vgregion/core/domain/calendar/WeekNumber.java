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

import se.vgregion.core.domain.patterns.valueobjects.AbstractValueObject;

/**
 * @author Anders Asplund - Callista Enterprise
 * 
 */
public final class WeekNumber extends AbstractValueObject<WeekNumber> {

    private int weekNumber;

    public WeekNumber(int weekNumber) {
        if (!WeekNumber.isValid(weekNumber)) {
            throw new IllegalArgumentException("Weeknumber must be between 1 and 53");
        }
        this.weekNumber = weekNumber;
    }

    public int getValue() {
        return weekNumber;
    }

    public static boolean isValid(int weekNumber) {
        return weekNumber >= 1 && weekNumber <= 53;
    }

    @Override
    public String toString() {
        String strWeek = "0" + weekNumber;
        return strWeek.substring(strWeek.length() - 2);
    }
}
