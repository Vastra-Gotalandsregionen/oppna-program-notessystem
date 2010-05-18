package se.vgregion.core.domain.calendar;

import javax.xml.bind.annotation.XmlElement;

public class CalendarEventItemPeriod {
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
}