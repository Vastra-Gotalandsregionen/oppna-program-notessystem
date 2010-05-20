package se.vgregion.core.domain.calendar;

import java.util.Date;

public class CalendarPeriod {

    private Date startDate;
    private int days;

    public CalendarPeriod(Date startDate, int days) {
        super();
        this.startDate = startDate;
        this.days = days;
    }

    public Date getStartDate() {
        return new Date(startDate.getTime());
    }

    public int getDays() {
        return days;
    }

}
