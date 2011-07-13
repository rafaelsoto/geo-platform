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

import org.geosdi.geoplatform.gui.client.LayerEvents;
import org.geosdi.geoplatform.gui.client.widget.LayerManagementWidget;
import org.geosdi.geoplatform.gui.configuration.mvc.GeoPlatformView;
import org.geosdi.geoplatform.gui.impl.view.LayoutManager;

import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import org.geosdi.geoplatform.gui.client.widget.form.LayerProperties;
import org.geosdi.geoplatform.gui.client.widget.progressbar.GPLayerProgressBar;
import org.geosdi.geoplatform.gui.model.tree.GPBeanTreeModel;

/**
 * @author Giuseppe La Scaleia - CNR IMAA geoSDI Group
 * @email giuseppe.lascaleia@geosdi.org
 * 
 */
public class LayerView extends GeoPlatformView {

    private LayerManagementWidget layerManagement;
    private GPLayerProgressBar layerProgressBar;

    /**
     * @Constructor
     *
     * @param controller
     */
    public LayerView(Controller controller) {
        super(controller);
        // TODO Auto-generated constructor stub
        this.layerManagement = new LayerManagementWidget();
    }

    @Override
    protected void initialize() {
        this.layerProgressBar = new GPLayerProgressBar();
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
        if (event.getType() == LayerEvents.SHOW_LAYER_WIDGET) {
            onShowLayerWidget();
        }

        if (event.getType() == LayerEvents.HIDE_LAYER_WIDGET) {
            onHideLayerWidget();
        }
        
        if (event.getType() == LayerEvents.SHOW_LAYER_PROPERTIES) {
            onShowLayerProperties((GPBeanTreeModel)event.getData());
        }
    }

    /**
     * Hide Layer Widget
     *
     */
    private void onHideLayerWidget() {
        // TODO Auto-generated method stub
        LayoutManager.removeComponentFromWest(layerManagement);
        if (!LayoutManager.isOneWidgetVisibleAtWest()) {
            LayoutManager.manageWest(false);
        }
    }

    /**
     * Show Layer Widget
     *
     */
    private void onShowLayerWidget() {
        // TODO Auto-generated method stub
        this.layerManagement.buildComponents();
        if (!LayoutManager.isWestVisible()) {
            LayoutManager.manageWest(true);
        }
        LayoutManager.addComponentToWest(layerManagement);
    }

    private void onShowLayerProperties(GPBeanTreeModel model) {
        LayerProperties properties = new LayerProperties(true, model);
        properties.show();
    }
}
