package se.vgregion.services.calendar.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialStore;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.services.GoogleKeyInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author Patrik Bergström
 */
@Service
public class GoogleCalendarService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleCalendarService.class);

    @Value("${clientId}")
    private String clientId;
    @Value("${clientSecret")
    private String clientSecret;
    @Value("${apiKey}")
    private String apiKey;

    private GoogleAuthorizationCodeFlow authorizationCodeFlow;

    @Autowired
    public GoogleCalendarService(CredentialStore credentialStore) {
        this.authorizationCodeFlow = new GoogleAuthorizationCodeFlow.Builder(
                new NetHttpTransport(), new JacksonFactory(), clientId, clientSecret,
                Collections.singleton(CalendarScopes.CALENDAR)).setAccessType("offline")
                .setCredentialStore(credentialStore).build();
    }

    public Calendar getCalendar(String userId) {
        Calendar calendar = null;
        try {
            Credential credential = authorizationCodeFlow.loadCredential(userId);

            if (credential != null) {
                System.out.println(new Date(credential.getExpirationTimeMilliseconds()));
                System.out.println("refreshToken: " + credential.getRefreshToken());

                if (credential.getExpiresInSeconds() < 0 && credential.getRefreshToken() != null) {
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

    public List<Event> getCalendarEvents(String userId) {
        Calendar calendar = getCalendar(userId);

        try {
            CalendarList calendarList = calendar.calendarList().list().execute();

            List<CalendarListEntry> calendarListEntries = calendarList.getItems();

            List<Event> allEvents = new ArrayList<Event>();

            for (CalendarListEntry entry : calendarListEntries) {
                String description = entry.getDescription();
                Events events = calendar.events().list(entry.getId()).execute();
                if (events != null) {
                    List<Event> items = events.getItems();
                    if (items != null) {
                        allEvents.addAll(items);
                    }
                }
            }

            return allEvents;
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return null;
    }

    public String getRedirectUrl() {
        GoogleAuthorizationCodeRequestUrl googleAuthorizationCodeRequestUrl = authorizationCodeFlow
                .newAuthorizationUrl();

        googleAuthorizationCodeRequestUrl.setRedirectUri(
                "https://localhost:8443/sv/group/vgregion/test2/-/oauth2callback/auth");

        String authUrl = googleAuthorizationCodeRequestUrl.build();

        return authUrl;
    }

    public void authorize(String authorizationCode, String userId) throws IOException {

        GoogleAuthorizationCodeTokenRequest googleAuthorizationCodeTokenRequest = authorizationCodeFlow
                .newTokenRequest(authorizationCode);

        googleAuthorizationCodeTokenRequest.setRedirectUri(
                "https://localhost:8443/sv/group/vgregion/test2/-/oauth2callback/auth");

        GoogleTokenResponse googleTokenResponse = googleAuthorizationCodeTokenRequest.execute();

        Credential credential = authorizationCodeFlow.createAndStoreCredential(googleTokenResponse, userId);
    }

    public boolean isAuthorized(String userId) {
        try {
            Credential credential = authorizationCodeFlow.loadCredential(userId);
            getCalendar(userId).calendarList().list().execute();
            return credential != null;
        } catch (GoogleJsonResponseException e) {
            if (e.getStatusCode() == 401) {
                LOGGER.warn(e.getMessage());
                return false;
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }
}
