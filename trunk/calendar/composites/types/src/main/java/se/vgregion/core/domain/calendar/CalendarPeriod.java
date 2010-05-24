package se.vgregion.core.domain.calendar;

import org.joda.time.DateTime;
import org.joda.time.Days;

import se.vgregion.core.domain.patterns.valueobjects.AbstractValueObject;

public class CalendarPeriod extends AbstractValueObject<CalendarPeriod> {

    private static final long serialVersionUID = -7922598817193391527L;
    private DateTime startDate;
    private Days days;

    public CalendarPeriod(DateTime startDate, Days days) {
        super();
        this.startDate = startDate;
        this.days = days;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public Days getDays() {
        return days;
    }

    public CalendarPeriod next() {
        return new CalendarPeriod(startDate.plusDays(days.getDays()), days);
    }

    public CalendarPeriod previous() {
        return new CalendarPeriod(startDate.minusDays(days.getDays()), days);
    }
}
