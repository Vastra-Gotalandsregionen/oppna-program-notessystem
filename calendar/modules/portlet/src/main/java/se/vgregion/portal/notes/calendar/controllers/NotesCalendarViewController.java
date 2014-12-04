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

package se.vgregion.portal.notes.calendar.controllers;

import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
//import com.google.api.services.oauth2.model.Userinfo;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.context.PortletConfigAware;
import se.vgregion.core.domain.calendar.CalendarEvents;
import se.vgregion.core.domain.calendar.CalendarEventsPeriod;
import se.vgregion.core.domain.calendar.CalendarItem;
import se.vgregion.portal.calendar.util.EncodingUtil;
import se.vgregion.services.calendar.CalendarService;
import se.vgregion.services.calendar.CalendarServiceException;
import se.vgregion.services.calendar.google.GoogleCalendarService;
import se.vgregion.services.calendar.google.GoogleCalendarServiceException;

import javax.portlet.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Controller
@SessionAttributes("displayPeriod")
@RequestMapping("VIEW")
@SuppressWarnings("unchecked")
public class NotesCalendarViewController implements PortletConfigAware {
    private static final String TIME_FORMAT = "dd MMMM";
    private static final String SELECTED_GOOGLE_CALENDARS = "selectedGoogleCalendars";

    protected String displayPeriodKey = "displayPeriod";

    /**
     * The name of the view page to dispatch to on a render request.
     */
    public static final String VIEW = "view";
    public static final String NO_CALENDAR_VIEW = "no_calendar_view";
    private static final Logger LOGGER = LoggerFactory.getLogger(NotesCalendarViewController.class);
    private CalendarService calendarService;
    private PortletConfig portletConfig = null;
    private PortletData portletData = null;
    private GoogleCalendarService googleCalendarService;
    private Random random = new Random();
    private Locale locale = new Locale("sv", "SE");

    /**
     * Constructs a NotesCalendarViewController.
     *
     * @param calendarService a calendarService
     */
    @Autowired
    public NotesCalendarViewController(CalendarService calendarService, GoogleCalendarService googleCalendarService) {
        this.calendarService = calendarService;
        this.googleCalendarService = googleCalendarService;
    }

    public void setPortletConfig(PortletConfig portletConfig) {
        this.portletConfig = portletConfig;
    }

    @Autowired
    public void setPortletData(PortletData portletData) {
        this.portletData = portletData;
    }

    /**
     * Displays the calendar events for the logged in user.
     *
     * @param model    the model
     * @param request  the portletRequest
     * @param response the portletResponse
     * @return the view to display
     */
    @RenderMapping
    public String displayCalendarEvents(ModelMap model, RenderRequest request, RenderResponse response) {

        // It would seem that Joda Times DateTimeZone does not use Sun:s TimeZone.getDefault (user.timezone) as its
        // often claims but defaults to timezone UTC. So therefor we do this:
        DateTimeZone.setDefault(DateTimeZone.forTimeZone(TimeZone.getDefault()));
        // ... getting the value set in the java_opt - setting it as default for JT.

        String userId = portletData.getUserId(request);
        LOGGER.debug("Userid: {}", userId);
        CalendarEventsPeriod displayPeriod = (CalendarEventsPeriod) model.get(displayPeriodKey);
        if (displayPeriod == null) {
            DateTime startDate = new DateTime().withDayOfWeek(DateTimeConstants.MONDAY).withHourOfDay(0)
                    .withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
            displayPeriod = new CalendarEventsPeriod(startDate, CalendarEventsPeriod.DEFAULT_PERIOD_LENGTH);
            model.put(displayPeriodKey, displayPeriod);
        }
        try {
            Map<String, Future<CalendarEvents>> futureCalendarEvents = new HashMap<String, Future<CalendarEvents>>();

            // Initialize CalendarEvents
            CalendarEvents events = new CalendarEvents();
            events.setCalendarItems(new ArrayList<CalendarItem>());

            // Retrieve asynchronously
            futureCalendarEvents.put("VGR", calendarService.getFutureCalendarEvents(userId, displayPeriod));

            // Get from Google, asynchronously
//            String selectedCalendars = request.getPreferences().getValue(this.SELECTED_GOOGLE_CALENDARS, "");
//            List<String> selectedCalendarsList = Arrays.asList(stringToArray(selectedCalendars));
//            futureCalendarEvents.put("Google", googleCalendarService.getFutureCalendarEvents(userId, displayPeriod,
//                    selectedCalendarsList));

            // Get from other sources, asynchronously.
            Map<String, String> externalSources = getExternalSources(request.getPreferences());
            for (Map.Entry<String, String> externalSource : externalSources.entrySet()) {
                futureCalendarEvents.put(externalSource.getKey(), calendarService.getFutureCalendarEventsFromIcalUrl(
                        externalSource.getValue(), displayPeriod, externalSource.getKey()));
            }

            // Now that we have a list of Future objects which all are processed concurrently we start to "get()" them.
            List<String> failedRetrievals = new ArrayList<String>();
            for (Map.Entry<String, Future<CalendarEvents>> futureCalendarEvent : futureCalendarEvents.entrySet()) {
                try {
                    Future<CalendarEvents> value = futureCalendarEvent.getValue();
                    CalendarEvents calendarEvents = value.get(15, TimeUnit.SECONDS);
                    if (calendarEvents != null) {
                        List<CalendarItem> calendarItems = calendarEvents.getCalendarItems();
                        if (calendarItems != null) {
                            events.getCalendarItems().addAll(calendarItems);
                        }
                    }
                } catch (Exception ex) {
                    if (userId.equals("lifra1")) {
                        LOGGER.warn("Failed to get a calendar for user " + userId + ". " + ex.getMessage());
                    } else {
                        LOGGER.warn("Failed to get a calendar for user " + userId + ". " + ex.getMessage(), ex);
                    }
                    failedRetrievals.add(futureCalendarEvent.getKey());
                }
            }

            if (failedRetrievals.size() > 0) {
                String errorMessage = "Följande hämtningar misslyckades: "
                        + StringUtils.arrayToCommaDelimitedString(failedRetrievals.toArray()) + ".";
                model.addAttribute("errorMessage", errorMessage);
            }

            List<List<CalendarItem>> calendarItems = events.getCalendarItemsGroupedByStartDate();
            model.put("displayPeriodText", getFormattedDateIntervalToTitle(displayPeriod, locale));
            model.put("calendarItems", calendarItems);
            model.put("randomNumber", random.nextInt());

            return VIEW;
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage(), ex);
            return NO_CALENDAR_VIEW;
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            return NO_CALENDAR_VIEW;
        }
    }

    /**
     * Displays the editExternalSources view.
     *
     * @param request  the request
     * @param response the response
     * @param model    the model
     * @return the view to display
     * @throws ClassNotFoundException ClassNotFoundException
     * @throws IOException            IOException
     */
    @RenderMapping(params = "action=editExternalSources")
    public String editExternalSources(RenderRequest request, RenderResponse response, Model model)
            throws ClassNotFoundException, IOException {
        PortletPreferences preferences = request.getPreferences();

        /*String userId = lookupP3PInfo(request, PortletRequest.P3PUserInfos.USER_LOGIN_ID);
        Userinfo userinfo = googleCalendarService.getUserinfo(userId);

        if (userinfo != null) {
            model.addAttribute("googleEmail", userinfo.getEmail());
        }*/

        Map<String, String> externalSources = getExternalSources(preferences);

        model.addAttribute("externalSources", externalSources);

        return "editExternalSources";
    }

    @RenderMapping(params = "action=editGoogleCalendar")
    public String editGoogleCalendar(RenderRequest request, RenderResponse response, Model model) {

        String userId = lookupP3PInfo(request, PortletRequest.P3PUserInfos.USER_LOGIN_ID);

/*
        Calendar calendar = googleCalendarService.getCalendar(userId);

        if (calendar != null) {
            try {
                CalendarList calendarList = calendar.calendarList().list().execute();

                List<CalendarListEntry> calendarListEntries = calendarList.getItems();

                model.addAttribute("calendarListEntries", calendarListEntries);

                Userinfo userinfo = googleCalendarService.getUserinfo(userId);

                model.addAttribute("googleEmail", userinfo.getEmail());
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
*/

        String selectedCalendarsString = request.getPreferences().getValue(SELECTED_GOOGLE_CALENDARS, null);

        String[] selectedCalendars = stringToArray(selectedCalendarsString);

        model.addAttribute(this.SELECTED_GOOGLE_CALENDARS, selectedCalendars);

        return "editGoogleCalendar";
    }

    @ActionMapping(params = "action=addGoogleCalendar")
    public void addGoogleCalendar(ActionRequest request, ActionResponse response, Model model) throws IOException {
        String userId = lookupP3PInfo(request, PortletRequest.P3PUserInfos.USER_LOGIN_ID);

/*        if (!googleCalendarService.isAuthorized(userId)) {
            response.sendRedirect(googleCalendarService.getRedirectUrl());
        } else {
            response.setRenderParameter("action", "editGoogleCalendar");
        }*/
    }

    @RenderMapping(params = "action=googleCallback")
    public String googleCallback(RenderRequest request, RenderResponse response, Model model) throws IOException {

        String authorizationCode = request.getParameter("code");

        String userId = lookupP3PInfo(request, PortletRequest.P3PUserInfos.USER_LOGIN_ID);

/*        try {
            googleCalendarService.authorize(authorizationCode, userId);
        } catch (TokenResponseException e) {
            LOGGER.warn(e.getMessage());
        }*/


        return editGoogleCalendar(request, response, model);
    }

    @ActionMapping(params = "action=saveGoogleCalendar")
    public void saveGoogleCalendar(ActionRequest request, ActionResponse response) throws ReadOnlyException, ValidatorException, IOException {
        String[] selectedCalendarsArray = request.getParameterValues(SELECTED_GOOGLE_CALENDARS);
        String selectedCalendars = arrayToString(selectedCalendarsArray);
        PortletPreferences preferences = request.getPreferences();
        preferences.setValue(this.SELECTED_GOOGLE_CALENDARS, selectedCalendars);
        preferences.store();

        response.setRenderParameter("action", "editExternalSources");
    }

    @ActionMapping(params = "action=removeGoogleCalendar")
    public void removeGoogleCalendar(ActionRequest request, ActionResponse response, Model model)
            throws GoogleCalendarServiceException {
        String userId = lookupP3PInfo(request, PortletRequest.P3PUserInfos.USER_LOGIN_ID);

        //googleCalendarService.resetAuthorization(userId);

        response.setRenderParameter("action", "editExternalSources");
    }

    String arrayToString(String[] array) {
        if (array == null || array.length == 0) {
            return "";
        }
        final String separator = "==SEPARATOR==";
        StringBuilder sb = new StringBuilder();
        for (String s : array) {
            sb.append(separator + s);
        }
        sb.delete(0, separator.length());
        return sb.toString();
    }

    String[] stringToArray(String string) {
        if (string == null) {
            return new String[0];
        }
        return string.split("==SEPARATOR==");
    }

    protected String lookupP3PInfo(PortletRequest req, PortletRequest.P3PUserInfos p3pInfo) {
        Map<String, String> userInfo = (Map<String, String>) req.getAttribute(PortletRequest.USER_INFO);
        String info;
        if (userInfo != null) {
            info = userInfo.get(p3pInfo.toString());
        } else {
            return null;
        }
        return info;
    }


    protected Map<String, String> decodeExternalSources(String externalSourcesEncoded)
            throws IOException, ClassNotFoundException {
        Map<String, String> externalSources;
        try {
            externalSources = EncodingUtil.decodeToObject(externalSourcesEncoded);
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage(), ex);
            externalSources = new HashMap<String, String>();
        }
        return externalSources;
    }

    /**
     * Action to either add, update or remove an external calendar source.
     *
     * @param request  the request
     * @param response the response
     * @param model    the model
     * @throws IOException            IOException
     * @throws ClassNotFoundException ClassNotFoundException
     * @throws ReadOnlyException      ReadOnlyException
     * @throws ValidatorException     ValidatorException
     */
    @ActionMapping(params = "action=editExternalSource")
    public void editExternalSource(ActionRequest request, ActionResponse response, Model model)
            throws IOException, ClassNotFoundException, ReadOnlyException, ValidatorException {
        String externalSourceKey = request.getParameter("externalSourceKey");
        String externalSourceUrl = request.getParameter("externalSourceUrl");

        String action = request.getParameter("submitType");

        if (!"Radera".equals(action)) {
            try {
                calendarService.validateAsValidIcalUrl(externalSourceUrl);
            } catch (CalendarServiceException e) {
                LOGGER.info("The provided URL could not be parsed.", e);
                model.addAttribute("errorMessage", "URL:en \"" + externalSourceUrl + "\" gick inte att läsa.");
                response.setRenderParameter("action", "editExternalSources");
                return;
            }
        }

        PortletPreferences preferences = request.getPreferences();

        Map<String, String> externalSources = getExternalSources(preferences);

        if ("Uppdatera".equals(action)) {
            String oldExternalSourceKey = request.getParameter("oldExternalSourceKey");
            if (!externalSourceKey.equals(oldExternalSourceKey)) {
                externalSources.remove(oldExternalSourceKey);
            }
            externalSources.put(externalSourceKey, externalSourceUrl);
        } else if ("Radera".equals(action)) {
            String oldExternalSourceKey = request.getParameter("oldExternalSourceKey");
            externalSources.remove(oldExternalSourceKey);
        } else {
            // Add
            externalSources.put(externalSourceKey, externalSourceUrl);
        }

        // Encode
        String externalSourcesEncoded = EncodingUtil.encodeToString((Serializable) externalSources);

        preferences.setValue("externalSourcesEncoded", externalSourcesEncoded);

        preferences.store();

        response.setRenderParameter("action", "editExternalSources");

    }

    /**
     * Action method to step one period ahead.
     *
     * @param model the model
     */
    @RenderMapping(params = "navigate=next")
    public String nextWeek(ModelMap model, RenderRequest request, RenderResponse response) {
        CalendarEventsPeriod displayPeriod = (CalendarEventsPeriod) model.get(displayPeriodKey);
        if (displayPeriod != null) {
            model.put(displayPeriodKey, displayPeriod.next());
        }
        return displayCalendarEvents(model, request, response);
    }

    /**
     * Action method to step one period back.
     *
     * @param model the model
     */
    @RenderMapping(params = "navigate=previous")
    public String previousWeek(ModelMap model, RenderRequest request, RenderResponse response) {
        CalendarEventsPeriod displayPeriod = (CalendarEventsPeriod) model.get(displayPeriodKey);
        if (displayPeriod != null) {
            model.put(displayPeriodKey, displayPeriod.previous());
        }
        return displayCalendarEvents(model, request, response);
    }

    /**
     * Exception handler method.
     *
     * @param exception the thrown exception
     * @return a {@link ModelAndView}
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception exception) {
        final int maxNumber = 1000000;
        int randomNumber = new Random().nextInt(maxNumber);
        LOGGER.error(randomNumber + ": " + exception.getMessage(), exception);
        String errorMessage = "Tekniskt fel. Vid kontakt med systemansvarig uppge nummer " + randomNumber + ". "
                + "Med hjälp av numret kan teknisk personal lokalisera felet.";
        ModelAndView mav = new ModelAndView("errorMessage", "errorMessage", errorMessage);
        return mav;
    }

    protected Map<String, String> getExternalSources(PortletPreferences preferences)
            throws IOException, ClassNotFoundException {
        String externalSourcesEncoded = preferences.getValue("externalSourcesEncoded", null);

        Map<String, String> externalSources;
        if (externalSourcesEncoded != null) {
            // Decode
            externalSources = decodeExternalSources(externalSourcesEncoded);
        } else {
            externalSources = new HashMap<String, String>();
        }
        return externalSources;
    }

    protected String getFormattedDateIntervalToTitle(CalendarEventsPeriod displayPeriod, Locale locale) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(TIME_FORMAT).withLocale(locale);
        StringBuilder title = new StringBuilder(TIME_FORMAT.length() * 2 + " - ".length());

        title.append(formatter.print(displayPeriod.getStartDate()));
        title.append(" - ");
        title.append(formatter.print(displayPeriod.getEndDate()));

        return title.toString();
    }

}
