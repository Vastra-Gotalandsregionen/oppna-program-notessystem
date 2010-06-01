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

import java.util.Locale;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;

import se.vgregion.core.domain.calendar.adapters.IntervalAdapter;
import se.vgregion.core.domain.patterns.valueobjects.AbstractValueObject;

/**
 * @author Anders Asplund - Callista Enterprise
 * 
 */
public class CalendarItem extends AbstractValueObject<CalendarItem> implements Comparable<CalendarItem> {

    private static final long serialVersionUID = 6533441182147467365L;
    @XmlElement(name = "type")
    private String calendarType;
    @XmlElement
    private String title;

    @XmlJavaTypeAdapter(IntervalAdapter.class)
    @XmlElement(name = "period")
    private Interval interval;
    private static final Locale DEFAULT_LOCALE = new Locale("sv", "SE");

    public String getDayOfWeek() {
        return getDayOfWeek(DEFAULT_LOCALE);
    }

    /**
     * Capitalized and localized string of the day of week.
     * 
     * @param locale
     *            to use on the returned string
     * @return Capitalized and localized string of the day of week
     */
    public String getDayOfWeek(Locale locale) {
        String dayOfWeek = interval.getStart().dayOfWeek().getAsText(locale);
        return WordUtils.capitalize(dayOfWeek);
    }

    public String getDayOfMonth() {
        return getDayOfMonth(DEFAULT_LOCALE);
    }

    /**
     * Capitalized and localized string of the day of month.
     * 
     * @param locale
     *            to use on the returned string
     * @return Capitalized and localized string of the day of month
     */
    public String getDayOfMonth(Locale locale) {
        return interval.getStart().dayOfMonth().getAsText(locale);
    }

    public String getMonthOfYear() {
        return getMonthOfYear(DEFAULT_LOCALE);
    }

    /**
     * Capitalized and localized string of the month of year.
     * 
     * @param locale
     *            to use on the returned string
     * @return Capitalized and localized string of the month of year
     */
    public String getMonthOfYear(Locale locale) {
        String monthOfYear = interval.getStart().monthOfYear().getAsText(locale);
        return WordUtils.capitalize(monthOfYear);
    }

    public String getStartTime() {
        return DateTimeFormat.forPattern("HH.mm").print(interval.getStart());
    }

    public String getEndTime() {
        return DateTimeFormat.forPattern("HH.mm").print(interval.getEnd());
    }

    public String getCalendarType() {
        return WordUtils.capitalize(calendarType.toLowerCase());
    }

    void setCalendarType(String calendarType) {
        this.calendarType = calendarType;
    }

    public String getTitle() {
        return title;
    }

    public Interval getInterval() {
        return interval;
    }

    void setInterval(Interval interval) {
        this.interval = interval;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    @Override
    public int compareTo(CalendarItem ci) {
        return interval.getStart().compareTo(ci.interval.getStart());
    }

}
