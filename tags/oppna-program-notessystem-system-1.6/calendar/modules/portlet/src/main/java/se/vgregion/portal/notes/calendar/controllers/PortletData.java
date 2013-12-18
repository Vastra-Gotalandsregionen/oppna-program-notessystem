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

import java.util.Map;

import javax.portlet.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Helper class for performing things related to the portlet specification.
 *
 * @author Anders Asplund - Callista Enterprise
 * 
 */
@Service
public class PortletData {
    /**
     * Resource variable for the portlet title.
     */
    public static final String JAVAX_PORTLET_TITLE = "javax.portlet.title";
    private static final Logger LOGGER = LoggerFactory.getLogger(PortletData.class);

    /**
     * Returns the id of the logged in user.
     * 
     * @param request
     *            the portletRequest
     * @return the id of the logged in user
     */
    @SuppressWarnings("unchecked")
    public String getUserId(RenderRequest request) {
        Map<String, String> attributes = (Map<String, String>) request.getAttribute(PortletRequest.USER_INFO);
        String userId = "";

        if (attributes != null) {
            userId = attributes.get(PortletRequest.P3PUserInfos.USER_LOGIN_ID.toString());
            if (userId == null) {
                userId = "";
            }
        }
        return userId;
    }

    /**
     * Returns the portlet title.
     * 
     * @param portletConfig
     *            the portletConfit
     * @param request
     *            the portletRequest
     * @return the portlet title
     */
    public String getPortletTitle(PortletConfig portletConfig, RenderRequest request) {
        return portletConfig.getResourceBundle(request.getLocale()).getString(JAVAX_PORTLET_TITLE);
    }

    /**
     * Sets the portlet title.
     * 
     * @param response
     *            the portletResponse
     * @param title
     *            the new title of the portlet
     */
    public void setPortletTitle(RenderResponse response, String title) {
        LOGGER.debug("Sets portlet title to {}", title);
        response.setTitle(title);
    }
}
