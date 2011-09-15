/*
 *  geo-platform
 *  Rich webgis framework
 *  http://geo-platform.org
 * ====================================================================
 *
 * Copyright (C) 2008-2011 geoSDI Group (CNR IMAA - Potenza - ITALY).
 *
 * This program is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version. This program is distributed in the 
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without 
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR 
 * A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details. You should have received a copy of the GNU General 
 * Public License along with this program. If not, see http://www.gnu.org/licenses/ 
 *
 * ====================================================================
 *
 * Linking this library statically or dynamically with other modules is 
 * making a combined work based on this library. Thus, the terms and 
 * conditions of the GNU General Public License cover the whole combination. 
 * 
 * As a special exception, the copyright holders of this library give you permission 
 * to link this library with independent modules to produce an executable, regardless 
 * of the license terms of these independent modules, and to copy and distribute 
 * the resulting executable under terms of your choice, provided that you also meet, 
 * for each linked independent module, the terms and conditions of the license of 
 * that module. An independent module is a module which is not derived from or 
 * based on this library. If you modify this library, you may extend this exception 
 * to your version of the library, but you are not obligated to do so. If you do not 
 * wish to do so, delete this exception statement from your version. 
 *
 */
package org.geosdi.geoplatform.gui.client.action;

import com.extjs.gxt.ui.client.event.MenuEvent;
import com.google.api.gwt.oauth2.client.Auth;
import com.google.api.gwt.oauth2.client.AuthRequest;
import com.google.api.gwt.oauth2.client.Callback;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.geosdi.geoplatform.gui.action.menu.OAuth2MenuBaseAction;
import org.geosdi.geoplatform.gui.client.OAuth2Resources;
import org.geosdi.geoplatform.gui.configuration.message.GeoPlatformMessage;
import org.geosdi.geoplatform.gui.puregwt.properties.WidgetPropertiesHandlerManager;
import org.geosdi.geoplatform.gui.puregwt.properties.event.GPToolbarIconWidgetEvent;
import org.geosdi.geoplatform.gui.server.gwt.OAuth2RemoteImpl;

/**
 * @author Michele Santomauro - CNR IMAA geoSDI Group
 * @email  michele.santomauro@geosdi.org
 *
 */
public class GoogleSignOnAction extends OAuth2MenuBaseAction {

    public GoogleSignOnAction() {
        super("Google Earth Builder", OAuth2Resources.ICONS.googleSignOnWhite());
    }

    @Override
    public void componentSelected(MenuEvent ce) {
        AuthRequest request = new AuthRequest(super.getGoogleAuthUrl(),
                super.getGoogleClientId()).withScopes(super.getScope());

        Auth auth = Auth.get();
        auth.login(request, new Callback<String, Throwable>() {

            @Override
            public void onSuccess(String token) {
                googleLoginCallback(token);
                
                setImage(OAuth2Resources.ICONS.googleSignOnGreen());
                setEnabled(false);
                
                WidgetPropertiesHandlerManager.fireEvent(new GPToolbarIconWidgetEvent("Signed on Google Earth Builder"));
            }

            @Override
            public void onFailure(Throwable caught) {
                GeoPlatformMessage.errorMessage("Sign on Google Earth Builder error", caught.getMessage());
            }
        });
    }

    private void googleLoginCallback(String token) {

        OAuth2RemoteImpl.Util.getInstance().googleUserLogin(token, new AsyncCallback<Object>() {

            @Override
            public void onFailure(Throwable caught) {
                GeoPlatformMessage.errorMessage("Error", caught.getMessage());
            }

            @Override
            public void onSuccess(Object result) {
            }
        });
    }
}
