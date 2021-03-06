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

import java.util.Locale;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;

import se.vgregion.core.domain.calendar.adapters.IntervalAdapter;
import se.vgregion.core.domain.patterns.valueobjects.AbstractValueObject;

/**
 * Class representing a calendar item like e.g. a meeting event.
 *
 * @author Anders Asplund - Callista Enterprise
 */
public class CalendarItem extends AbstractValueObject<CalendarItem> implements Comparable<CalendarItem> {

    private static final long serialVersionUID = 6533441182147467365L;
    @XmlElement(name = "type")
    private String calendarType;
    @XmlElement
    private String title;

    @XmlJavaTypeAdapter(IntervalAdapter.class)
    @XmlElement(name = "period")
    private Interval interval = null;

    private Boolean wholeDays = null;

    private static final Locale DEFAULT_LOCALE = new Locale("sv", "SE");

    public String getDayOfWeek() {
        return getDayOfWeek(DEFAULT_LOCALE);
    }

    /**
     * Capitalized and localized string of the day of week.
     *
     * @param locale to use on the returned string
     * @return Capitalized and localized string of the day of week
     */
    public String getDayOfWeek(Locale locale) {
        String dayOfWeek = interval.getStart().dayOfWeek().getAsText(locale);
        return WordUtils.capitalize(dayOfWeek);
    }

    /**
     * Capitalized string of the end day of week with sv_SE locale.
     *
     * @return Capitalized string of the day of week
     */
    public String getEndDayOfWeek() {
        String dayOfWeek = interval.getEnd().dayOfWeek().getAsText(DEFAULT_LOCALE);
        return WordUtils.capitalize(dayOfWeek);
    }

    /**
     * Capitalized string of the day of month with sv_SE locale.
     *
     * @return Capitalized and localized string of the day of month
     */
    public String getDayOfMonth() {
        return getDayOfMonth(DEFAULT_LOCALE);
    }

    /**
     * Capitalized and localized string of the day of month.
     *
     * @param locale to use on the returned string
     * @return Capitalized and localized string of the day of month
     */
    public String getDayOfMonth(Locale locale) {
        return interval.getStart().dayOfMonth().getAsText(locale);
    }

    /**
     * Capitalized and localized string of the day of month with sv_SE locale.
     *
     * @return Capitalized and localized string of the day of month
     */
    public String getEndDayOfMonth() {
        String dayOfMonth = interval.getEnd().dayOfMonth().getAsText(DEFAULT_LOCALE);
        return WordUtils.capitalize(dayOfMonth);
    }

    /**
     * Capitalized string of the month of year with sv_SE locale.
     *
     * @return Capitalized and localized string of the month of year
     */
    public String getMonthOfYear() {
        return getMonthOfYear(DEFAULT_LOCALE);
    }

    /**
     * Capitalized and localized string of the month of year.
     *
     * @param locale to use on the returned string
     * @return Capitalized and localized string of the month of year
     */
    public String getMonthOfYear(Locale locale) {
        String monthOfYear = interval.getStart().monthOfYear().getAsText(locale);
        return WordUtils.capitalize(monthOfYear);
    }

    /**
    * Month of year as integer
    *
    * @return Month of year as integer
    */
    public int getMonthOfYearAsNumber() {
      return interval.getStart().monthOfYear().get();
    }

    /**
     * Capitalized string of the end month of year with sv_SE locale.
     *
     * @return Capitalized and localized string of the month of year
     */
    public String getEndMonthOfYear() {
        String monthOfYear = interval.getEnd().monthOfYear().getAsText(DEFAULT_LOCALE);
        return WordUtils.capitalize(monthOfYear);
    }

    public int getEndMonthOfYearAsNumber() {
        return interval.getEnd().monthOfYear().get();
    }

    public String getStartTime() {
        return DateTimeFormat.forPattern("HH.mm").print(interval.getStart());
    }

    public String getEndTime() {
        return DateTimeFormat.forPattern("HH.mm").print(interval.getEnd());
    }

    /**
     * Get the type of the calendar item.
     *
     * @return the type of the calendar item
     */
    public String getCalendarType() {
        if (calendarType != null) {
            return WordUtils.capitalize(calendarType.toLowerCase(Locale.getDefault()));
        }
        return null;
    }

    @XmlTransient
    public void setCalendarType(String calendarType) {
        this.calendarType = calendarType;
    }

    public String getTitle() {
        return title;
    }

    @XmlTransient
    public void setTitle(String title) {
        this.title = title;
    }

    public Interval getInterval() {
        return interval;
    }

    @XmlTransient
    public void setInterval(Interval interval) {
        this.interval = interval;
    }

    public Boolean getWholeDays() {
        return wholeDays;
    }

    @XmlTransient
    public void setWholeDays(Boolean wholeDays) {
        this.wholeDays = wholeDays;
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
