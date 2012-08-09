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

import org.joda.time.DateTime;
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

import javax.portlet.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.Future;

@Controller
@SessionAttributes("displayPeriod")
@RequestMapping("VIEW")
public class NotesCalendarViewController implements PortletConfigAware {
    private static final String TIME_FORMAT = "dd MMMM";
    /**
     * The name of the view page to dispatch to on a render request.
     */
    public static final String VIEW = "view";
    public static final String NO_CALENDAR_VIEW = "no_calendar_view";
    private static final Logger LOGGER = LoggerFactory.getLogger(NotesCalendarViewController.class);
    private CalendarService calendarService;
    private PortletConfig portletConfig = null;
    private PortletData portletData = null;

    /**
     * Constructs a NotesCalendarViewController.
     *
     * @param calendarService a calendarService
     */
    @Autowired
    public NotesCalendarViewController(CalendarService calendarService) {
        this.calendarService = calendarService;
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
        String userId = portletData.getUserId(request);
        LOGGER.debug("Userid: {}", userId);
        String title = portletData.getPortletTitle(portletConfig, request);
        CalendarEventsPeriod displayPeriod = (CalendarEventsPeriod) model.get("displayPeriod");
        if (displayPeriod == null) {
            displayPeriod = new CalendarEventsPeriod(new DateTime(), CalendarEventsPeriod.DEFAULT_PERIOD_LENGTH);
            model.put("displayPeriod", displayPeriod);
        }
        try {
            Map<String, Future<CalendarEvents>> futureCalendarEvents = new HashMap<String, Future<CalendarEvents>>();

            // Initialize CalendarEvents
            CalendarEvents events = new CalendarEvents();
            events.setCalendarItems(new ArrayList<CalendarItem>());

            // Retrieve asynchronously
            futureCalendarEvents.put("iNotes", calendarService.getFutureCalendarEvents(userId, displayPeriod));

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
                    events.getCalendarItems().addAll(futureCalendarEvent.getValue().get().getCalendarItems());
                } catch (Exception ex) {
                    LOGGER.warn("Failed to get a calendar for user " + userId + ".", ex);
                    failedRetrievals.add(futureCalendarEvent.getKey());
                }
            }

            if (failedRetrievals.size() > 0) {
                String errorMessage = "Följande hämtningar misslyckades: "
                        + StringUtils.arrayToCommaDelimitedString(failedRetrievals.toArray()) + ". Du kan behöva gå"
                        + " till \"Redigera externa källor\" och se över konfigurationen.";
                model.addAttribute("errorMessage", errorMessage);
            }

            List<List<CalendarItem>> calendarItems = events.getCalendarItemsGroupedByStartDate();
            portletData.setPortletTitle(response, title + " "
                    + getFormattedDateIntervalToTitle(displayPeriod, response.getLocale()));
            model.put("calendarItems", calendarItems);

            return VIEW;
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

        Map<String, String> externalSources = getExternalSources(preferences);

        model.addAttribute("externalSources", externalSources);

        return "editExternalSources";
    }

    private Map<String, String> decodeExternalSources(String externalSourcesEncoded)
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
    @ActionMapping(params = "navigate=next")
    public void nextWeek(ModelMap model) {
        CalendarEventsPeriod displayPeriod = (CalendarEventsPeriod) model.get("displayPeriod");
        if (displayPeriod != null) {
            model.put("displayPeriod", displayPeriod.next());
        }
    }

    /**
     * Action method to step one period back.
     *
     * @param model the model
     */
    @ActionMapping(params = "navigate=previous")
    public void previousWeek(ModelMap model) {
        CalendarEventsPeriod displayPeriod = (CalendarEventsPeriod) model.get("displayPeriod");
        if (displayPeriod != null) {
            model.put("displayPeriod", displayPeriod.previous());
        }
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

    private Map<String, String> getExternalSources(PortletPreferences preferences)
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

    private String getFormattedDateIntervalToTitle(CalendarEventsPeriod displayPeriod, Locale locale) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(TIME_FORMAT).withLocale(locale);
        StringBuilder title = new StringBuilder(TIME_FORMAT.length() * 2 + " - ".length());

        title.append(formatter.print(displayPeriod.getStartDate()));
        title.append(" - ");
        title.append(formatter.print(displayPeriod.getEndDate()));

        return title.toString();
    }

}
