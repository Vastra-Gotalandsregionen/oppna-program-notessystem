package se.vgregion.services.calendar.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialStore;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.services.GoogleKeyInitializer;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.api.services.oauth2.model.Userinfo;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import se.vgregion.core.domain.calendar.CalendarEvents;
import se.vgregion.core.domain.calendar.CalendarEventsPeriod;
import se.vgregion.core.domain.calendar.CalendarItem;

import javax.annotation.PostConstruct;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Future;

/**
 * @author Patrik Bergström
 */
@Service
public class GoogleCalendarService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleCalendarService.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Value("${clientId}")
    private String clientId;
    @Value("${clientSecret}")
    private String clientSecret;
    @Value("${apiKey}")
    private String apiKey;
    @Value("${googleCallbackUrl}")
    private String googleCallbackUrl;

    private GoogleAuthorizationCodeFlow authorizationCodeFlow;
    private CredentialStore credentialStore;

    public GoogleCalendarService() {

    }

    @Autowired
    public GoogleCalendarService(CredentialStore credentialStore) {
        this.credentialStore = credentialStore;
    }

    @PostConstruct
    public void setup() {
        this.authorizationCodeFlow = new GoogleAuthorizationCodeFlow.Builder(
                new NetHttpTransport(), new JacksonFactory(), clientId, clientSecret,
                new ArrayList<String>(Arrays.asList(CalendarScopes.CALENDAR_READONLY,
                        "https://www.googleapis.com/auth/userinfo.profile",
                        "https://www.googleapis.com/auth/userinfo.email"))).setAccessType("offline")
                .setCredentialStore(credentialStore).build();
    }

    public Calendar getCalendar(String userId) {
        Calendar calendar = null;
        try {
            Credential credential = authorizationCodeFlow.loadCredential(userId);

            if (credential != null) {
                System.out.println("refreshToken: " + credential.getRefreshToken());

                if ((credential.getExpiresInSeconds() == null || credential.getExpiresInSeconds() < 0)
                        && credential.getRefreshToken() != null) {
                    credential.refreshToken();
                }
                Calendar.Builder calendarBuilder = new Calendar.Builder(authorizationCodeFlow.getTransport(),
                        authorizationCodeFlow.getJsonFactory(), credential);
                calendarBuilder.setJsonHttpRequestInitializer(new GoogleKeyInitializer(apiKey));

                calendar = calendarBuilder.build();
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return calendar;
    }

    public Userinfo getUserinfo(String userId) {
        try {
            Credential credential = authorizationCodeFlow.loadCredential(userId);
            if (credential != null) {
                String accessToken = credential.getAccessToken();
                String userinfoUrl = "https://www.googleapis.com/userinfo/v2/me?key=" + apiKey + "&access_token="
                        + accessToken;
                HttpResponse httpResponse = authorizationCodeFlow.getTransport().createRequestFactory().buildGetRequest(
                        new GenericUrl(userinfoUrl)).execute();

                Userinfo userinfo = (Userinfo) authorizationCodeFlow.getJsonFactory().createJsonParser(httpResponse
                        .getContent()).parse(Userinfo.class, true, null);

                return userinfo;
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return null;
    }

    @Async
    public Future<CalendarEvents> getFutureCalendarEvents(String userId, CalendarEventsPeriod period,
                                                          List<String> selectedCalendarsList) {

        List<Event> googleCalendarEvents = getCalendarEvents(userId, selectedCalendarsList);

        CalendarEvents calendarEvents = new CalendarEvents();
        List<CalendarItem> calendarItems = new ArrayList<CalendarItem>();

        for (Event gEvent : googleCalendarEvents) {
            boolean wholeDay;
            if (gEvent.getStart().getDateTime() == null) {
                wholeDay = true;
            } else {
                wholeDay = false;
            }

            Interval periodInterval = new Interval(period.getStartDate().toDate().getTime(), period.getEndDate()
                    .toDate().getTime());

            Interval eventInterval = null;
            if (wholeDay) {
                try {
                    eventInterval = new Interval(DATE_FORMAT.parse(gEvent.getStart().getDate()).getTime(),
                            DATE_FORMAT.parse(gEvent.getEnd().getDate()).getTime() - 1); // Subtract a millisecond so we don't start the next day
                } catch (ParseException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            } else {
                eventInterval = new Interval(gEvent.getStart().getDateTime().getValue(), gEvent.getEnd().getDateTime()
                        .getValue());
            }

            if (periodInterval.overlaps(eventInterval)) {
                CalendarItem item = new CalendarItem();
                item.setTitle(gEvent.getSummary());
                item.setWholeDays(wholeDay);
                item.setCalendarType("Google: " + gEvent.getOrganizer().getDisplayName());
                item.setInterval(eventInterval);
                calendarItems.add(item);
            }
        }

        calendarEvents.setCalendarItems(calendarItems);
        return new AsyncResult<CalendarEvents>(calendarEvents);
    }

    public List<Event> getCalendarEvents(String userId, List<String> calendarIds) {
        Calendar calendar = getCalendar(userId);

        try {
            if (calendar != null && calendar.calendarList() != null && calendar.calendarList().list() != null) {
                CalendarList calendarList = calendar.calendarList().list().execute();

                List<CalendarListEntry> calendarListEntries = calendarList.getItems();

                List<Event> allEvents = new ArrayList<Event>();

                for (CalendarListEntry entry : calendarListEntries) {
                    if (!calendarIds.contains(entry.getId())) {
                        continue;
                    }

                    Events events = calendar.events().list(entry.getId()).execute();
                    if (events != null) {
                        List<Event> items = events.getItems();
                        if (items != null) {
                            allEvents.addAll(items);
                        }
                    }
                }

                return allEvents;
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return new ArrayList<Event>();
    }

    public List<Event> getCalendarEvents(List<CalendarListEntry> calendarListEntries, String userId) {
        List<String> strings = new ArrayList<String>();
        for (CalendarListEntry entry : calendarListEntries) {
            strings.add(entry.getId());
        }
        return getCalendarEvents(userId, strings);
    }

    public String getRedirectUrl() {
        GoogleAuthorizationCodeRequestUrl googleAuthorizationCodeRequestUrl = authorizationCodeFlow
                .newAuthorizationUrl();

        googleAuthorizationCodeRequestUrl.setRedirectUri(googleCallbackUrl);

        String authUrl = googleAuthorizationCodeRequestUrl.build();

        return authUrl;
    }

    public void authorize(String authorizationCode, String userId) throws IOException {

        GoogleAuthorizationCodeTokenRequest googleAuthorizationCodeTokenRequest = authorizationCodeFlow
                .newTokenRequest(authorizationCode);

        googleAuthorizationCodeTokenRequest.setRedirectUri(googleCallbackUrl);

        GoogleTokenResponse googleTokenResponse = googleAuthorizationCodeTokenRequest.execute();

        Credential credential = authorizationCodeFlow.createAndStoreCredential(googleTokenResponse, userId);
    }

    public boolean isAuthorized(String userId) {
        try {
            Credential credential = authorizationCodeFlow.loadCredential(userId);
            Calendar calendar = getCalendar(userId);
            if (calendar == null) {
                return false;
            }
            calendar.calendarList().list().execute();
            return credential != null;
        } catch (GoogleJsonResponseException e) {
            if (e.getStatusCode() == 401) {
                LOGGER.warn(e.getMessage());
                return false;
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (RuntimeException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }
}