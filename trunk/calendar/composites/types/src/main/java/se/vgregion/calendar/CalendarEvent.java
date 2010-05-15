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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Anders Asplund
 * 
 */
@XmlRootElement(name = "item")
public class CalendarEvent {

    @XmlElement(name = "type")
    private String calendarType;
    // @XmlElement(name = "") private Date startTime;
    // @XmlElement(name = "") private Date endTime;
    private String title;
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;

    public String getCalendarType() {
        return calendarType;
    }

    void setCalendarType(String calendarType) {
        this.calendarType = calendarType;
    }

    // public Date getStartTime() {
    // return new Date(startTime.getTime());
    // }
    //
    // void setStartTime(Date startTime) {
    // this.startTime = startTime;
    // }
    //
    // public Date getEndTime() {
    // return new Date(endTime.getTime());
    // }
    //
    // void setEndTime(Date endTime) {
    // this.endTime = endTime;
    // }

    public String getTitle() {
        return title;
    }

    void setTitle(String title) {
        this.title = title;
    }

}
