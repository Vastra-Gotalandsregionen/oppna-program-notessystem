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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

public class CalendarItemPeriod {
    @XmlElement
    private String startDate;
    @XmlElement
    private String startTime;
    @XmlElement
    private String endDate;
    @XmlElement
    private String endTime;

    public String getStartDate() {
        return startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getEndTime() {
        return endTime;
    }

    @XmlTransient
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    @XmlTransient
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    @XmlTransient
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @XmlTransient
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

}