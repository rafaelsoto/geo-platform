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
package org.geosdi.geoplatform.gui.server.service.impl;

import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.geosdi.geoplatform.core.model.GPUser;
import org.geosdi.geoplatform.core.model.GeoPlatformServer;
import org.geosdi.geoplatform.exception.IllegalParameterFault;
import org.geosdi.geoplatform.exception.ResourceNotFoundFault;
import org.geosdi.geoplatform.gui.exception.GPSessionTimeout;

import org.geosdi.geoplatform.gui.global.GeoPlatformException;
import org.geosdi.geoplatform.gui.model.server.GPLayerGrid;
import org.geosdi.geoplatform.gui.model.server.GPServerBeanModel;
import org.geosdi.geoplatform.gui.server.service.IOGCService;
import org.geosdi.geoplatform.gui.server.service.converter.DTOServerConverter;
import org.geosdi.geoplatform.gui.utility.UserLoginEnum;
import org.geosdi.geoplatform.request.RequestById;
import org.geosdi.geoplatform.responce.ServerDTO;
import org.geosdi.geoplatform.services.GeoPlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 *
 * @author Giuseppe La Scaleia - CNR IMAA geoSDI Group
 * @email  giuseppe.lascaleia@geosdi.org
 */
@Service(value = "ogcService")
public class OGCService implements IOGCService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    //
    private GeoPlatformService geoPlatformServiceClient;
    //
    @Autowired
    private DTOServerConverter dtoServerConverter;

    @Override
    public ArrayList<GPServerBeanModel> loadServers()
            throws GeoPlatformException {
        return dtoServerConverter.convertServer(geoPlatformServiceClient.getAllServers());
    }

    @Override
    public GPServerBeanModel getServerDetails(long idServer)
            throws GeoPlatformException {
        try {
            GeoPlatformServer serverWS = geoPlatformServiceClient.getServerDetail(idServer);
            return dtoServerConverter.getServerDetail(serverWS);
        } catch (ResourceNotFoundFault ex) {
            logger.error("The server with id " + idServer + " was bean deleted.");
            throw new GeoPlatformException(
                    "The server with id " + idServer + " was bean deleted.");
        }
    }

    @Override
    public Boolean deleteServer(long idServer) throws GeoPlatformException {
        try {
            geoPlatformServiceClient.deleteServer(idServer);
        } catch (ResourceNotFoundFault ex) {
            logger.error("The server with id " + idServer + " was not bean deleted.");
            throw new GeoPlatformException(
                    "The server with id " + idServer + " was not bean deleted.");
        } catch (IllegalParameterFault il) {
            logger.error("The server with id " + idServer + " was not bean deleted.");
            throw new GeoPlatformException(
                    "The server with id " + idServer + " was not bean deleted.");
        }
        return true;
    }

    @Override
    public ArrayList<? extends GPLayerGrid> getCapabilities(
            HttpServletRequest httpServletRequest, long idServer)
            throws GeoPlatformException {
        try {
            HttpSession session = httpServletRequest.getSession();
            String token = (String) session.getAttribute("GOOGLE_TOKEN");

            RequestById req = new RequestById(idServer);

            ServerDTO server = geoPlatformServiceClient.getCapabilities(req, token);

            return dtoServerConverter.createRasterLayerList(server.getLayerList());
        } catch (ResourceNotFoundFault ex) {
            logger.error("Error GetCapabilities: " + ex);
            throw new GeoPlatformException(ex.getMessage());
        }
    }

    @Override
    public GPServerBeanModel insertServer(Long id, String aliasServerName, String urlServer)
            throws GeoPlatformException {
        ServerDTO serverWS = null;
        try {
            serverWS = geoPlatformServiceClient.saveServer(id, aliasServerName, urlServer);
        } catch (IllegalParameterFault ex) {
            logger.error(ex.getMessage());
            throw new GeoPlatformException(ex.getMessage());
        } catch (ResourceNotFoundFault ex) {
            logger.error("Insert Server Error : " + ex.getMessage());
            throw new GeoPlatformException(ex.getMessage());
        }
        return dtoServerConverter.convertServerWS(serverWS);
    }

    private GPUser getUserAlreadyFromSession(HttpServletRequest httpServletRequest) {
        GPUser user = null;
        HttpSession session = httpServletRequest.getSession();
        Object userObj = session.getAttribute(UserLoginEnum.USER_LOGGED.toString());
        if (userObj != null && userObj instanceof GPUser) {
            user = (GPUser) userObj;
        } else {
            throw new GeoPlatformException(new GPSessionTimeout("Session Timeout"));
        }
        return user;
    }

    @Override
    public ArrayList<String> findDistinctLayersDataSource(HttpServletRequest httpServletRequest)
            throws GeoPlatformException {
        ArrayList<String> dataSources = null;
        try {
            dataSources = geoPlatformServiceClient.getLayersDataSourceByOwner(
                    this.getUserAlreadyFromSession(httpServletRequest).getUsername());
        } catch (ResourceNotFoundFault e) {
            throw new GeoPlatformException("Error in findDistinctLayersDataSource: ResourceNotFoundFault "
                    + e);
        }
        return dataSources;
    }

    /**
     * @param geoPlatformServiceClient the geoPlatformServiceClient to set
     */
    @Autowired
    public void setGeoPlatformServiceClient(
            @Qualifier("geoPlatformServiceClient") GeoPlatformService geoPlatformServiceClient) {
        this.geoPlatformServiceClient = geoPlatformServiceClient;
    }
}
