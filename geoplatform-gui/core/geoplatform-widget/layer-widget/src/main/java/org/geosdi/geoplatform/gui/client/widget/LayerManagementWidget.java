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
package org.geosdi.geoplatform.gui.client.widget;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.ScrollListener;
import com.extjs.gxt.ui.client.event.WidgetListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import org.geosdi.geoplatform.gui.client.widget.toolbar.LayerTreeToolbar;

/**
 * @author Giuseppe La Scaleia - CNR IMAA geoSDI Group
 * @email giuseppe.lascaleia@geosdi.org
 * 
 */
public class LayerManagementWidget extends ContentPanel {

    private LayerTreeWidget layerTree;
    //private LayerAsyncTreeWidget layerTree;
    private LayerTreeToolbar treeToolbar;
    private GPLegendPanel legendPanel;
    private ContentPanel treePanel;

    /*
     * @Constructor
     * 
     */
    public LayerManagementWidget() {
        setHeading("GeoPlatform - Layer Widget");
        setLayout(new BorderLayout());

        setLayoutOnChange(true);

        addComponents();

        addWidgetListener(new WidgetListener() {

            @Override
            public void widgetResized(ComponentEvent ce) {
                if (getHeight() > 0) {
                    treePanel.setHeight(getHeight() - 220);
                }
            }
        });

        setScrollMode(Scroll.NONE);
    }

    private void addComponents() {
        treePanel = new ContentPanel();
        treePanel.setScrollMode(Scroll.AUTOY);
        treePanel.setHeaderVisible(false);

        //This code fix a scroll problem on IE9
        treePanel.addScrollListener(new ScrollListener() {

           int posV = 0;

           /**
            * Fires when a component is scrolled.
            * 
            * @param ce the component event
            */
           @Override
           public void widgetScrolled(ComponentEvent ce) {
               if (posV > 9 && treePanel.getVScrollPosition() == 0) {
                   treePanel.setVScrollPosition(posV);
               }
               posV = treePanel.getVScrollPosition();
           }
       });
        this.layerTree = new LayerTreeWidget(treePanel);
        //this.layerTree = new LayerAsyncTreeWidget();

        BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH);
        northData.setMargins(new Margins(5, 5, 0, 5));

        treePanel.add(this.layerTree.getTree());

        this.treeToolbar = new LayerTreeToolbar(this.layerTree.getTree());

        this.treePanel.setTopComponent(this.treeToolbar.getToolBar());

        super.add(this.treePanel, northData);

        this.legendPanel = new GPLegendPanel();

        BorderLayoutData southData = new BorderLayoutData(LayoutRegion.SOUTH, 180);
        southData.setMargins(new Margins(5, 5, 5, 5));

        super.add(this.legendPanel, southData);
    }

    /**
     * Build Layer Widget with Spring Configuration
     *
     */
    public void buildComponents() {
        this.treeToolbar.buildToolbar();
        this.layerTree.buildTree();
    }

    /**
     * @return the layerTree
     */
    public LayerTreeWidget getLayerTree() {
        return layerTree;
    }
//    /**
//     * @return the layerTree
//     */
//    public LayerAsyncTreeWidget getLayerTree() {
//        return layerTree;
//    }

    /**
     * @return the legendPanel
     */
    public GPLegendPanel getLegendPanel() {
        return legendPanel;
    }
}
