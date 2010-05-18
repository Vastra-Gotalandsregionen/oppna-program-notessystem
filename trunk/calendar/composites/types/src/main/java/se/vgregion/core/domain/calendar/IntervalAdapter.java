/**
 * 
 */
package se.vgregion.core.domain.calendar;

import java.text.SimpleDateFormat;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.joda.time.Interval;

/**
 * @author Anders Asplund - Callista Enterprise
 * 
 */
public class IntervalAdapter extends XmlAdapter<CalendarEventItemPeriod, Interval> {

    @Override
    public CalendarEventItemPeriod marshal(Interval v) throws Exception {
        throw new UnsupportedOperationException("Marshalling is unsupported for now.");
    }

    @Override
    public Interval unmarshal(CalendarEventItemPeriod eventInterval) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ssyyyy-MM-dd");
        long start = sdf.parse(eventInterval.getStartTime() + eventInterval.getStartDate()).getTime();
        long end = sdf.parse(eventInterval.getEndTime() + eventInterval.getEndDate()).getTime();
        return new Interval(start, end);
    }

}
