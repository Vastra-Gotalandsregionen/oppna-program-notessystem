package se.vgregion.portal.notes.calendar.mdc.controllers;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Test;
import se.vgregion.core.domain.calendar.CalendarEventsPeriod;

import java.util.Date;

public class MyDayCalendarViewControllerTest {

    @Test
    public void makeDisplayPeriod() throws Exception {
        MyDayCalendarViewController controller = new MyDayCalendarViewController(null, null);
        CalendarEventsPeriod result = controller.makeDisplayPeriod(new Date(0));
        Assert.assertEquals(1, result.getStartDate().dayOfMonth().get());
        Assert.assertEquals(4, result.getStartDate().dayOfWeek().get());
        System.out.println(result.getStartDate());

        result = controller.makeDisplayPeriod(new Date());
        //int dayOfWeek = result.getStartDate().getDayOfWeek();
        //System.out.println(dayOfWeek);
    }
}