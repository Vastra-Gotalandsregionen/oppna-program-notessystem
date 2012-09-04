package se.vgregion.services.calendar.google;

import com.google.api.client.auth.oauth2.CredentialStore;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.oauth2.model.Userinfo;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * @author Patrik Bergström
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:serviceContext.xml", "classpath:applicationContext-test.xml"})
public class GoogleCalendarServiceIT {

    @Autowired
    private GoogleCalendarService googleCalendarService;

    @Test
    public void testGetUserinfo() throws Exception {
        Userinfo userinfo = googleCalendarService.getUserinfo("myUser");
        assertNotNull(userinfo);
    }

    @Test
    public void testGetCalendar() throws Exception {

    }

    @Test
    public void testGetCalendarEvents() throws Exception {

    }

    @Test
    public void testGetRedirectUrl() throws Exception {

    }

    @Test
    @Ignore // This test requires user input
    public void testAuthorize() throws Exception {

        String redirectUrl = googleCalendarService.getRedirectUrl();

        System.out.println("Paste this in your browser:");
        System.out.println(redirectUrl);

        System.out.println("Paste the response code.");

        String code = new BufferedReader(new InputStreamReader(System.in)).readLine();

        code = URLDecoder.decode(code, "UTF-8");

        if (code.contains("=")) {
            code = code.split("=")[1];
        }

        googleCalendarService.authorize(code, "myUser");

        Calendar calendar = googleCalendarService.getCalendar("myUser");

        assertNotNull(calendar);
    }

    private GoogleCalendarService getService() {
        return googleCalendarService;
    }

    public static void main(String[] args) throws Exception {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath*:serviceContext.xml",
                "classpath:applicationContext-test.xml");

        GoogleCalendarServiceIT t = new GoogleCalendarServiceIT();

        ReflectionTestUtils.setField(t, "googleCalendarService", ctx.getBean(GoogleCalendarService.class));

        boolean authorized = t.getService().isAuthorized("myUser");
        System.out.println(authorized);

        if (!authorized) {
            t.testAuthorize();
        }

        authorized = t.getService().isAuthorized("myUser");

        System.out.println(authorized);

        if (authorized) {
            Calendar calendar = t.getService().getCalendar("myUser");

            CalendarList calendarList = calendar.calendarList().list().execute();

            List<CalendarListEntry> items = calendarList.getItems();

            for (CalendarListEntry item : items) {
                System.out.println("summary: " + item.getSummary());
                System.out.println("getDescription: " + item.getDescription());
                System.out.println("getId: " + item.getId());
                System.out.println();
            }

            assertNotNull(calendar);
            List<Event> events = t.getService().getCalendarEvents("myUser");
            for (Event event : events) {
                System.out.println(event.getStart().getDateTime() + " - " + event.getSummary() + " - " + event.getICalUID());
            }
        }

        Userinfo myUser = t.getService().getUserinfo("myUser");


    }
}
