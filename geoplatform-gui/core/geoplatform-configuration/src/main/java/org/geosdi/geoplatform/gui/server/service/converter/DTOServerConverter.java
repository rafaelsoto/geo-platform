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
package org.geosdi.geoplatform.gui.server.service.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.geosdi.geoplatform.core.model.GeoPlatformServer;

import org.geosdi.geoplatform.gui.configuration.map.client.geometry.BboxClientInfo;
import org.geosdi.geoplatform.gui.configuration.map.client.layer.GPLayerType;
import org.geosdi.geoplatform.gui.model.server.GPLayerGrid;
import org.geosdi.geoplatform.gui.model.server.GPRasterLayerGrid;
import org.geosdi.geoplatform.gui.model.server.GPServerBeanModel;
import org.geosdi.geoplatform.gui.model.tree.GPStyleStringBeanModel;
import org.geosdi.geoplatform.responce.RasterLayerDTO;
import org.geosdi.geoplatform.responce.ServerDTO;
import org.geosdi.geoplatform.responce.ShortLayerDTO;
import org.geosdi.geoplatform.responce.StyleDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author Giuseppe La Scaleia - CNR IMAA geoSDI Group
 * @email  giuseppe.lascaleia@geosdi.org
 */
@Component(value = "dtoServerConverter")
public class DTOServerConverter {

    private final Logger logger = LoggerFactory.getLogger(
            this.getClass().getName());

    /**
     *
     * @param serversWS
     * @return
     */
    public ArrayList<GPServerBeanModel> convertServer(
            Collection<ServerDTO> serversWS) {
        ArrayList<GPServerBeanModel> serversDTO = new ArrayList<GPServerBeanModel>();

        if (serversWS != null) {
            for (ServerDTO gpServer : serversWS) {
                GPServerBeanModel serverDTO = new GPServerBeanModel();
                serverDTO.setId(gpServer.getId());
                serverDTO.setAlias(gpServer.getAlias());
                serverDTO.setUrlServer(gpServer.getServerUrl());
                serverDTO.setName(gpServer.getName());
                serversDTO.add(serverDTO);
            }
        }

        return serversDTO;
    }

    /**
     * 
     * @param gpServer
     * @return
     */
    public GPServerBeanModel getServerDetail(GeoPlatformServer gpServer) {
        GPServerBeanModel serverDTO = new GPServerBeanModel();
        serverDTO.setId(gpServer.getId());
        serverDTO.setName(gpServer.getName());
        serverDTO.setUrlServer(gpServer.getServerUrl());
        serverDTO.setTitle(gpServer.getTitle());
        serverDTO.setAlias(gpServer.getAliasName());
        serverDTO.setContactOrganization(gpServer.getContactOrganization());
        serverDTO.setContactPerson(gpServer.getContactPerson());
        return serverDTO;
    }

    /**
     * 
     * @param layers
     * 
     * @return
     *        ArrayList<? extends GPLayerBeanModel>
     */
    public ArrayList<? extends GPLayerGrid> createRasterLayerList(
            List<? extends ShortLayerDTO> layers) {

        return this.createRasterLayerList(layers, new ArrayList<GPLayerGrid>());
    }

    private ArrayList<? extends GPLayerGrid> createRasterLayerList(
            List<? extends ShortLayerDTO> layers,
            ArrayList<GPLayerGrid> list) {
        if (layers != null) {
            for (ShortLayerDTO layer : layers) {
                if (((RasterLayerDTO) layer).getSubLayerList().size() > 0) {
                    this.createRasterLayerList(
                            ((RasterLayerDTO) layer).getSubLayerList(), list);
                } else {
                    GPRasterLayerGrid raster = this.convertToRasterLayerGrid((RasterLayerDTO) layer);
                    list.add(raster);
                }
            }
        }

        return list;
    }

    /**
     * 
     * @param serverWS
     * 
     * @return 
     *        GPServerBeanModel
     */
    public GPServerBeanModel convertServerWS(ServerDTO serverWS) {
        GPServerBeanModel serverDTO = new GPServerBeanModel();
        serverDTO.setId(serverWS.getId());
        serverDTO.setAlias(serverWS.getAlias());
        serverDTO.setName(serverWS.getName());
        serverDTO.setUrlServer(serverWS.getServerUrl());
        serverDTO.setLayers(serverWS.getLayerList() != null
                ? this.createRasterLayerList(serverWS.getLayerList()) : null);
        return serverDTO;
    }

    private GPRasterLayerGrid convertToRasterLayerGrid(RasterLayerDTO layer) {
        GPRasterLayerGrid raster = new GPRasterLayerGrid();
        raster.setLabel(layer.getTitle());
        raster.setTitle(layer.getTitle());
        raster.setAlias(layer.getAlias());
        raster.setName(layer.getName());
        raster.setAbstractText(layer.getAbstractText());
        raster.setLayerType(GPLayerType.RASTER);
        raster.setDataSource(layer.getUrlServer());
        if (layer.getBbox() != null) {
            raster.setBbox(new BboxClientInfo(layer.getBbox().getMinX(),
                    layer.getBbox().getMinY(), layer.getBbox().getMaxX(),
                    layer.getBbox().getMaxY()));
            raster.setCrs(layer.getSrs());
        }
        ArrayList<GPStyleStringBeanModel> styles = new ArrayList<GPStyleStringBeanModel>();
        GPStyleStringBeanModel style = null;
        for (String styleString : layer.getStyleList()) {
            style = new GPStyleStringBeanModel();
            style.setStyleString(styleString);
            styles.add(style);
        }
        raster.setStyles(styles);
        return raster;
    }
}