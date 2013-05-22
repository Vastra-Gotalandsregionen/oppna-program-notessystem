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
package se.vgregion.domain.infrastructure.webservice;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientException;
import se.vgregion.core.domain.calendar.CalendarEvents;
import se.vgregion.core.domain.calendar.CalendarEventsPeriod;
import se.vgregion.core.domain.calendar.CalendarEventsRepository;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Implementation of {@link CalendarEventsRepository} which is used for restfull requests against service endpoint.
 * The service url is specified with the following pattern:
 * http://<host>?userid=<userid>&year=<year>&month=<month>&day=<day>&period=<period>
 *
 * @author Anders Asplund - Callista Enterprise
 */
public class RestCalendarEventsRepository implements CalendarEventsRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestCalendarEventsRepository.class);
    private String serviceUrl;

    /**
     * Constructs a RestCalendarEventsRepository for a restfull reqest to the specified service endpoint.
     *
     */
    public RestCalendarEventsRepository() {
    }

    /**
     * Sets the service endpoint.
     *
     * @param serviceEndpoint the service endpoint
     */
    public void setServiceEndpoint(String serviceEndpoint) {
        this.serviceUrl = serviceEndpoint + "userid=%s&year=%s&month=%s&day=%s&period=%s";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * se.vgregion.core.domain.calendar.CalendarEventsRepository#findCalendarEventsByCalendarPeriod(java.lang.String
     * , se.vgregion.core.domain.calendar.CalendarEventsPeriod)
     */
    @Override
    public CalendarEvents findCalendarEventsByCalendarPeriod(String userId, CalendarEventsPeriod period) {
        CalendarEvents events = CalendarEvents.EMPTY_CALENDAR_EVENTS;
        try {
            LOGGER.debug("Calling the following web service: {}", serviceUrl);
            LOGGER.debug("Paramters sent to web service: userid={}, year={}, month={}, day={}, period={}",
                    new Object[]{userId, period.getStartDate().getYear(),
                            period.getStartDate().getMonthOfYear(), period.getStartDate().getDayOfMonth(),
                            period.getDays().getDays()});

            String requestUrl = String.format(serviceUrl, userId, period.getStartDate()
                    .getYear(), period.getStartDate().getMonthOfYear(), period.getStartDate().getDayOfMonth(),
                    period.getDays().getDays());

            events = getCalendarEventsFromUrl(requestUrl);

        } catch (RestClientException e) {
            LOGGER.error("Unable to retreive information from web service: {}. CalendarEventPeriod={}",
                    new Object[]{serviceUrl, period});
            LOGGER.error("Web service exception", e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            LOGGER.error("Unable to retreive information from web service: {}. CalendarEventPeriod={}",
                    new Object[]{serviceUrl, period});
            LOGGER.error("Web service exception", e);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
        return events;
    }

    protected CalendarEvents getCalendarEventsFromUrl(String requestUrl) throws IOException, JAXBException {
        InputStream inputStream = null;
        BufferedInputStream bis = null;
        try {

            URL url = new URL(requestUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            inputStream = urlConnection.getInputStream();
            bis = new BufferedInputStream(inputStream);

            return extractCalendarEvents(bis);
        } finally {
            closeClosables(bis, inputStream);
        }
    }

    private static void closeClosables(Closeable... closables) {
        for (Closeable closeable : closables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
    }

    // This method uses either UTF-8 or ISO-8859-1 to decode depending on which method results in the most Swedish
    // characters.
    private CalendarEvents extractCalendarEvents(InputStream bis) throws IOException, JAXBException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int n;
        while ((n = bis.read(buf)) != -1) {
            baos.write(buf, 0, n);
        }

        String inUtf8 = new String(baos.toByteArray(), "UTF-8");
        String inIso8859 = new String(baos.toByteArray(), "ISO-8859-1");
        int numberSwedishCharactersFromUtf8 = StringUtils.countMatches(inUtf8, "å")
                + StringUtils.countMatches(inUtf8, "ä") + StringUtils.countMatches(inUtf8, "ö");
        int numberSwedishCharactersFromIso8859 = StringUtils.countMatches(inIso8859, "å")
                + StringUtils.countMatches(inIso8859, "ä") + StringUtils.countMatches(inIso8859, "ö");

        String toUse;
        if (numberSwedishCharactersFromIso8859 > numberSwedishCharactersFromUtf8) {
            toUse = inIso8859;
        } else {
            toUse = inUtf8;
        }

        JAXBContext jaxbContext = JAXBContext.newInstance(CalendarEvents.class);
        return (CalendarEvents) jaxbContext.createUnmarshaller().unmarshal(new StringReader(toUse));
    }

}
