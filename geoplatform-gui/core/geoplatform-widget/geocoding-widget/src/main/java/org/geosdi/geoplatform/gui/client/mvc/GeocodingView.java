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
package org.geosdi.geoplatform.gui.client.mvc;

import java.util.ArrayList;

import org.geosdi.geoplatform.gui.client.GeocodingEvents;
import org.geosdi.geoplatform.gui.client.model.GeocodingBean;
import org.geosdi.geoplatform.gui.client.widget.GeocodingManagementWidget;
import org.geosdi.geoplatform.gui.client.widget.ReverseGeocodingDispatcher;
import org.geosdi.geoplatform.gui.configuration.mvc.GeoPlatformView;
import org.geosdi.geoplatform.gui.impl.view.LayoutManager;

import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.widget.grid.Grid;

/**
 * @author giuseppe
 * 
 */
public class GeocodingView extends GeoPlatformView {

    private GeocodingManagementWidget geocodingManagement;
    private ReverseGeocodingDispatcher reverseDispatcher;

    public GeocodingView(Controller controller) {
        super(controller);
        // TODO Auto-generated constructor stub
        this.geocodingManagement = new GeocodingManagementWidget();
    }

    /* (non-Javadoc)
     * @see com.extjs.gxt.ui.client.mvc.View#initialize()
     */
    @Override
    protected void initialize() {
        // TODO Auto-generated method stub
        this.reverseDispatcher = new ReverseGeocodingDispatcher();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.geosdi.geoplatform.gui.configuration.mvc.GeoPlatformView#handleEvent
     * (com.extjs.gxt.ui.client.mvc.AppEvent)
     */
    @Override
    protected void handleEvent(AppEvent event) {
        // TODO Auto-generated method stub
        if (event.getType() == GeocodingEvents.SHOW_GEOCODING_WIDGET) {
            onShowGeocodingWidget();
        }

        if (event.getType() == GeocodingEvents.HIDE_GEOCODING_WIDGET) {
            onHideGeocodingWidget();
        }
    }

    /**
     * Hide Geocoding Widget
     */
    private void onHideGeocodingWidget() {
        // TODO Auto-generated method stub
        if (LayoutManager.isWidgetPresentOnWest(geocodingManagement)) {
            LayoutManager.removeComponentFromWest(geocodingManagement);
            if (!LayoutManager.isOneWidgetVisibleAtWest()) {
                LayoutManager.manageWest(false);
            }
        }
    }

    /**
     * Show Geocoding Widget
     */
    private void onShowGeocodingWidget() {
        // TODO Auto-generated method stub
        if (!LayoutManager.isWestVisible()) {
            LayoutManager.manageWest(true);
        }
        LayoutManager.addComponentToWest(geocodingManagement);
    }

    /**
     * @return the geocodingManagement
     */
    public GeocodingManagementWidget getGeocodingManagement() {
        return geocodingManagement;
    }

    /**
     * Mask GeocodingGridWidget
     */
    public void maskGeocodingGrid() {
        this.geocodingManagement.getGeocodingGridWidget().maskGrid();
    }

    /**
     * Un Mask GeocodingGridWidget
     */
    public void unMaskGeocodingGrid() {
        this.geocodingManagement.getGeocodingGridWidget().unMaskGrid();
    }

    /**
     * Clean the Store
     */
    public void cleanStore() {
        this.geocodingManagement.getGeocodingGridWidget().getStore().removeAll();
    }

    /**
     * Fill GeocodingGridWidget Store
     *
     * @param beans
     */
    public void fillStore(ArrayList<GeocodingBean> beans) {
        this.geocodingManagement.getGeocodingGridWidget().fillStore(beans);
    }

    public Grid<GeocodingBean> getGrid() {
        return this.geocodingManagement.getGeocodingGridWidget().getGrid();
    }
}
