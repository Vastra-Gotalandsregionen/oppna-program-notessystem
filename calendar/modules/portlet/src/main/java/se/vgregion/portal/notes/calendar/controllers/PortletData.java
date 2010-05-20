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

import org.springframework.stereotype.Service;

/**
 * @author Anders Asplund - Callista Enterprise
 * 
 */
@Service
public class PortletData {
    public static final String JAVAX_PORTLET_TITLE = "javax.portlet.title";

    @SuppressWarnings("unchecked")
    public String getUserId(RenderRequest request) {
        Map<String, String> attributes = (Map<String, String>) request.getAttribute(PortletRequest.USER_INFO);
        String userId = "";

        if (attributes != null) {
            userId = attributes.get(PortletRequest.P3PUserInfos.USER_LOGIN_ID.toString());
        }
        return userId;
    }

    public String getPortletTitle(PortletConfig portletConfig, RenderRequest request) {
        return portletConfig.getResourceBundle(request.getLocale()).getString(JAVAX_PORTLET_TITLE);
    }

    public void setPortletTitle(RenderResponse response, String title) {
        response.setTitle(title);
    }
}
