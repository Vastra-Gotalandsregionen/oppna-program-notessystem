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
package se.vgregion.portal.notes.calendar.controllers;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletRequest;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.portlet.MockRenderRequest;
import org.springframework.mock.web.portlet.MockRenderResponse;

/**
 * @author Anders Asplund
 * 
 */
public class PortletDataTest {

    private static final String USER_ID = String.valueOf(1);
    private static final String PORTLET_TITLE = "A title";
    private MockRenderRequest request = new MockRenderRequest();
    private MockRenderResponse response = new MockRenderResponse();
    private PortletData portletData = new PortletData();

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * Test method for
     * {@link se.vgregion.portal.notes.calendar.controllers.PortletData#getUserId(javax.portlet.RenderRequest)}.
     */
    @Test
    public final void shouldReturnEmptyUserId() {
        // Given
        Map<String, String> userInfo = new HashMap<String, String>();
        request.setAttribute(PortletRequest.USER_INFO, userInfo);
        // When
        String userId = portletData.getUserId(request);

        // Then
        assertEquals("", userId);
    }

    /**
     * Test method for
     * {@link se.vgregion.portal.notes.calendar.controllers.PortletData#getUserId(javax.portlet.RenderRequest)}.
     */
    @Test
    public final void shouldReturnUserId() {
        // Given
        Map<String, String> userInfo = new HashMap<String, String>();
        userInfo.put(PortletRequest.P3PUserInfos.USER_LOGIN_ID.toString(), USER_ID);
        request.setAttribute(PortletRequest.USER_INFO, userInfo);

        // When
        String userId = portletData.getUserId(request);

        // Then
        assertEquals(USER_ID, userId);
    }

    /**
     * Test method for
     * {@link se.vgregion.portal.notes.calendar.controllers.PortletData#setPortletTitle(javax.portlet.RenderResponse, java.lang.String)}
     * .
     */
    @Test
    public final void shouldSetPortletTitle() {
        // Given
        // When
        portletData.setPortletTitle(response, PORTLET_TITLE);
        // Then
    }

}
