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

package se.vgregion.portal.notes.calendar.mdc.controllers;

//import com.google.api.services.oauth2.model.Userinfo;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
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
import se.vgregion.portal.notes.calendar.controllers.NotesCalendarViewController;
import se.vgregion.portal.notes.calendar.controllers.PortletData;
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
public class MyDayCalendarViewController extends NotesCalendarViewController implements PortletConfigAware {
    private static final String TIME_FORMAT = "dd MMMM";
    private static final String SELECTED_GOOGLE_CALENDARS = "selectedGoogleCalendars";

    /**
     * The name of the view page to dispatch to on a render request.
     */
    public static final String VIEW = "one-day-view";
    public static final String NO_CALENDAR_VIEW = "no_calendar_view";
    private static final Logger LOGGER = LoggerFactory.getLogger(MyDayCalendarViewController.class);

    /*private CalendarService calendarService;
    private PortletConfig portletConfig = null;
    private PortletData portletData = null;
    private GoogleCalendarService googleCalendarService;
    private Random random = new Random();
    private Locale locale = new Locale("sv", "SE");*/

    /**
     * Constructs a MyDayCalendarViewController.
     *
     * @param calendarService a calendarService
     */
    @Autowired
    public MyDayCalendarViewController(CalendarService calendarService, GoogleCalendarService googleCalendarService) {
        super(calendarService, googleCalendarService);
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
    @Override
    public String displayCalendarEvents(ModelMap model, RenderRequest request, RenderResponse response) {
        CalendarEventsPeriod displayPeriod = (CalendarEventsPeriod) model.get("displayPeriod");
        if (displayPeriod == null) {
            DateTime startDate = new DateTime().withDayOfWeek(DateTimeConstants.MONDAY).withHourOfDay(0)
                    .withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
            displayPeriod = new CalendarEventsPeriod(startDate, CalendarEventsPeriod.ONE_DAY_PERIOD_LENGTH);
            model.put("displayPeriod", displayPeriod);
        }
        String result = super.displayCalendarEvents(model, request, response);
        if ("view".equals(result)) {
            return VIEW;
        }
        return result;
    }

    /**
     * Displays the editExternalSources view.
     *
     * @param request  the request
     * @param response the response
     * @param model    the model
     * @return the view to display
     * @throws ClassNotFoundException ClassNotFoundException
     * @throws java.io.IOException            IOException
     */
    @Override
    @RenderMapping(params = "action=editExternalSources")
    public String editExternalSources(RenderRequest request, RenderResponse response, Model model)
            throws ClassNotFoundException, IOException {
        return super.editExternalSources(request, response, model);
    }

    @Override
    @RenderMapping(params = "action=editGoogleCalendar")
    public String editGoogleCalendar(RenderRequest request, RenderResponse response, Model model) {
        return super.editGoogleCalendar(request, response, model);
    }

    @Override
    @ActionMapping(params = "action=addGoogleCalendar")
    public void addGoogleCalendar(ActionRequest request, ActionResponse response, Model model) throws IOException {
        super.addGoogleCalendar(request, response, model);
    }

    @Override
    @RenderMapping(params = "action=googleCallback")
    public String googleCallback(RenderRequest request, RenderResponse response, Model model) throws IOException {
        return super.googleCallback(request, response, model);
    }

    @Override
    public void saveGoogleCalendar(ActionRequest request, ActionResponse response) throws ReadOnlyException, ValidatorException, IOException {
        super.saveGoogleCalendar(request, response);
    }

    //@ActionMapping(params = "action=removeGoogleCalendar")
    @Override
    public void removeGoogleCalendar(ActionRequest request, ActionResponse response, Model model) throws GoogleCalendarServiceException {
        super.removeGoogleCalendar(request, response, model);
    }

    protected String lookupP3PInfo(PortletRequest req, PortletRequest.P3PUserInfos p3pInfo) {
        return super.lookupP3PInfo(req, p3pInfo);
    }

    @Override
    protected Map<String, String> decodeExternalSources(String externalSourcesEncoded) throws IOException, ClassNotFoundException {
        return super.decodeExternalSources(externalSourcesEncoded);
    }

    /**
     * Action to either add, update or remove an external calendar source.
     *
     * @param request  the request
     * @param response the response
     * @param model    the model
     * @throws java.io.IOException            IOException
     * @throws ClassNotFoundException ClassNotFoundException
     * @throws javax.portlet.ReadOnlyException      ReadOnlyException
     * @throws javax.portlet.ValidatorException     ValidatorException
     */
    @Override
    //@ActionMapping(params = "action=editExternalSource")
    public void editExternalSource(ActionRequest request, ActionResponse response, Model model) throws IOException, ClassNotFoundException, ReadOnlyException, ValidatorException {
        super.editExternalSource(request, response, model);
    }

    /**
     * Action method to step one period ahead.
     *
     * @param model the model
     */
    @Override
    // @RenderMapping(params = "navigate=next")
    public String nextWeek(ModelMap model, RenderRequest request, RenderResponse response) {
        return super.nextWeek(model, request, response);
    }

    /**
     * Action method to step one period back.
     *
     * @param model the model
     */
    //@RenderMapping(params = "navigate=previous")
    @Override
    public String previousWeek(ModelMap model, RenderRequest request, RenderResponse response) {
        return super.previousWeek(model, request, response);
    }

    /**
     * Exception handler method.
     *
     * @param exception the thrown exception
     * @return a {@link org.springframework.web.portlet.ModelAndView}
     */
    //@ExceptionHandler(Exception.class) // Is this annotation needed?
    @Override
    public ModelAndView handleException(Exception exception) {
        return super.handleException(exception);
    }

    @Override
    protected Map<String, String> getExternalSources(PortletPreferences preferences) throws IOException, ClassNotFoundException {
        return super.getExternalSources(preferences);
    }

    @Override
    protected String getFormattedDateIntervalToTitle(CalendarEventsPeriod displayPeriod, Locale locale) {
        return super.getFormattedDateIntervalToTitle(displayPeriod, locale);
    }

}
