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
package se.vgregion.infrastructure.webservice;

import java.util.List;

import se.vgregion.calendar.CalendarEvent;
import se.vgregion.calendar.CalendarEventRepository;
import se.vgregion.calendar.WeekOfYear;

/**
 * @author Anders Asplund
 *
 */
public class WSCalendarEventRepository implements CalendarEventRepository {

    /* (non-Javadoc)
     * @see se.vgregion.calendar.CalendarEventRepository#findCalendarEvents(java.lang.String)
     */
    @Override
    public List<CalendarEvent> findCalendarEvents(String userId) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see se.vgregion.calendar.CalendarEventRepository#findCalendarEvents(java.lang.String, se.vgregion.calendar.WeekOfYear)
     */
    @Override
    public List<CalendarEvent> findCalendarEvents(String userId, WeekOfYear weekOfYear) {
        // TODO Auto-generated method stub
        return null;
    }

}
