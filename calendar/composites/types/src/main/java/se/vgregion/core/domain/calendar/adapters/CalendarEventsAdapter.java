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
package se.vgregion.core.domain.calendar.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import se.vgregion.core.domain.calendar.CalendarEvents;

/**
 * @author Anders Asplund - Callista Enterprise
 * 
 */
public class CalendarEventsAdapter extends XmlAdapter<CalendarEvents, CalendarEvents> {

    @Override
    public CalendarEvents marshal(CalendarEvents event) throws Exception {
        throw new UnsupportedOperationException("Marshalling is unsupported for now.");
    }

    @Override
    public CalendarEvents unmarshal(CalendarEvents events) throws Exception {
        if (events.getStatus().equalsIgnoreCase("ERROR")) {
            throw new VgrCalendarWebServiceException(events.getMessage());
        }
        return events;
    }

}
