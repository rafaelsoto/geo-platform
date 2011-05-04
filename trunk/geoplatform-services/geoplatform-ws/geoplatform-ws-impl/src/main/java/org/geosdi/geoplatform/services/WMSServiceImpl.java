//<editor-fold defaultstate="collapsed" desc="License">
/*
 *  geo-platform
 *  Rich webgis framework
 *  http://geo-plartform.org
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
//</editor-fold>
package org.geosdi.geoplatform.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.geosdi.geoplatform.core.dao.GPServerDAO;
import org.geosdi.geoplatform.core.model.GeoPlatformServer;
import org.geosdi.geoplatform.exception.ResourceNotFoundFault;
import org.geosdi.geoplatform.request.RequestById;
import org.geosdi.geoplatform.responce.collection.LayerList;
import org.geosdi.geoplatform.responce.AbstractLayerDTO;
import org.geosdi.geoplatform.responce.RasterLayerDTO;
import org.geotools.data.ows.Layer;
import org.geotools.data.ows.WMSCapabilities;
import org.geotools.data.wms.WebMapServer;
import org.geotools.ows.ServiceException;

/**
 * @author Francesco Izzi - CNR IMAA - geoSDI
 * 
 */
class WMSServiceImpl {

    final private static Logger LOGGER = Logger.getLogger(WMSServiceImpl.class);
    private GPServerDAO serverDao;

    //<editor-fold defaultstate="collapsed" desc="Setter method">
    /**
     * @param serverDao
     *            the serverDao to set
     */
    public void setServerDao(GPServerDAO serverDao) {
        this.serverDao = serverDao;
    }
    //</editor-fold>    

    public LayerList getCapabilities(RequestById request)
            throws ResourceNotFoundFault {

        GeoPlatformServer server = serverDao.find(request.getId());

        URL serverURL = null;
        WebMapServer wms = null;
        WMSCapabilities cap = null;

        try {
            serverURL = new URL(server.getServerUrl());
            wms = new WebMapServer(serverURL);
            cap = wms.getCapabilities();

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ServiceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return convertToShortList(cap.getLayerList());
    }

    // TODO Move to LayerList?
    // as constructor: LayerList list = new LayerList(List<Layer>);    
    // TODO Correct mapping Layer to AbstractLayerDTO
    private LayerList convertToShortList(List<Layer> layerList) {
        List<AbstractLayerDTO> shortLayers = new ArrayList<AbstractLayerDTO>(layerList.size());
        AbstractLayerDTO layerDTOIth = null;
        for (Layer layer : layerList) {
            layerDTOIth = new RasterLayerDTO(); // TODO AbstractLayerDTO as abstract class?
            layerDTOIth.setName(layer.getName());
            layerDTOIth.setAbstractText(layer.get_abstract());
            layerDTOIth.setTitle(layer.getTitle());
            shortLayers.add(layerDTOIth);
        }

        LayerList layers = new LayerList();
        layers.setList(shortLayers);
        return layers;
    }
}