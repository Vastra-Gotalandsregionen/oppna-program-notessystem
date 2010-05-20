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

import org.apache.commons.lang.Validate;

import se.vgregion.core.domain.patterns.valueobjects.AbstractValueObject;

/**
 * @author Anders Asplund
 * 
 */
public class CalendarEventsId extends AbstractValueObject<CalendarEventsId> {

    private static final long serialVersionUID = 3068540279744256010L;
    private String userId;
    private WeekOfYear week;

    public CalendarEventsId(final String userId, WeekOfYear week) {
        Validate.notNull(userId);
        this.userId = userId;
        this.week = week;
    }

    public String getUserId() {
        return userId;
    }

    public WeekOfYear getWeek() {
        return week;
    }

    @Override
    public String toString() {
        return userId + week.toString();
    }
}
