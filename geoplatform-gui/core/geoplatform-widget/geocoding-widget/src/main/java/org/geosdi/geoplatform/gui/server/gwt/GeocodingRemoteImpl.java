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
package org.geosdi.geoplatform.gui.server.gwt;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.geosdi.geoplatform.gui.client.model.GeocodingBean;
import org.geosdi.geoplatform.gui.client.service.GeocodingRemote;
import org.geosdi.geoplatform.gui.global.GeoPlatformException;
import org.geosdi.geoplatform.gui.server.service.IGeocodingService;
import org.geosdi.geoplatform.gui.server.service.IReverseGeocoding;
import org.geosdi.geoplatform.gui.server.service.impl.GeocodingService;
import org.geosdi.geoplatform.gui.server.service.impl.ReverseGeocoding;
import org.geosdi.geoplatform.gui.spring.GeoPlatformContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author giuseppe
 * 
 */
public class GeocodingRemoteImpl extends RemoteServiceServlet implements
        GeocodingRemote {

    /**
     *
     */
    private static final long serialVersionUID = 8960403782525028063L;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private IGeocodingService geocodingService;
    private IReverseGeocoding reverseGeocoding;

    public GeocodingRemoteImpl() {
        this.geocodingService = (IGeocodingService) GeoPlatformContextUtil.getInstance().getBean(GeocodingService.class);
        this.reverseGeocoding = (IReverseGeocoding) GeoPlatformContextUtil.getInstance().getBean(ReverseGeocoding.class);
    }

    @Override
    public ArrayList<GeocodingBean> findLocations(String search)
            throws GeoPlatformException {
        // TODO Auto-generated method stub
        try {
            return this.geocodingService.findLocations(search);
        } catch (XPathExpressionException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage());
            throw new GeoPlatformException(e.getMessage());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage());
            throw new GeoPlatformException(e.getMessage());
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage());
            throw new GeoPlatformException(e.getMessage());
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage());
            throw new GeoPlatformException(e.getMessage());
        }
    }

    @Override
    public GeocodingBean findLocation(double lat, double lon)
            throws GeoPlatformException {
        // TODO Auto-generated method stub
        try {
            return this.reverseGeocoding.findLocation(lat, lon);
        } catch (XPathExpressionException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage());
            throw new GeoPlatformException(e.getMessage());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage());
            throw new GeoPlatformException(e.getMessage());
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage());
            throw new GeoPlatformException(e.getMessage());
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage());
            throw new GeoPlatformException(e.getMessage());
        }
    }
}
