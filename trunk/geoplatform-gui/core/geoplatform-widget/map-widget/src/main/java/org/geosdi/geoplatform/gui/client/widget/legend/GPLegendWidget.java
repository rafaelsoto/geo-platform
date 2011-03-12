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
package org.geosdi.geoplatform.gui.client.widget.legend;

import org.geosdi.geoplatform.gui.model.GPLayerBean;
import org.geosdi.geoplatform.gui.model.GPRasterBean;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.google.gwt.user.client.ui.Image;

/**
 * @author Francesco Izzi - CNR IMAA - geoSDI Group
 * 
 */
public class GPLegendWidget extends Window {

	private static final String GET_LEGEND_REQUEST = "?REQUEST=GetLegendGraphic&VERSION=1.0.0&FORMAT=image/png&WIDTH=20&HEIGHT=20&LAYER=";

	private ContentPanel legendsStore;

	/**
	 * 
	 */
	public GPLegendWidget() {
		setMaximizable(false);
		setCollapsible(true);
		setHeading("Legend Window");
		setHeight(300);
		setWidth(200);
		setScrollMode(Scroll.AUTOY);
		setPosition(20, 450);

		this.legendsStore = new ContentPanel();
		this.legendsStore.setHeaderVisible(false);

		super.add(this.legendsStore);
	}

	/**
	 * Add Legend Item in the Legend Store
	 * 
	 * @param layerBean
	 */
	public void addLegend(GPLayerBean layerBean) {
		ContentPanel cp = new ContentPanel();
		cp.setHeading(layerBean.getLabel());
		cp.setId(layerBean.getLabel());

		Image image = new Image(
				layerBean instanceof GPRasterBean ? layerBean.getDataSource()
						+ GET_LEGEND_REQUEST + layerBean.getLabel() : layerBean
						.getDataSource().replaceAll("wfs", "wms")
						+ GET_LEGEND_REQUEST + layerBean.getLabel());

		cp.add(image);
		this.legendsStore.add(cp);

		if (!isVisible()) {
			super.show();
		}
		this.legendsStore.layout();
	}

	/**
	 * Remove Legend Item from Legend Store
	 * 
	 * @param layerBean
	 */
	public void hideLegenItem(GPLayerBean layerBean) {
		if (this.legendsStore.getItemByItemId(layerBean.getLabel()) != null) {
			this.legendsStore.remove(this.legendsStore
					.getItemByItemId(layerBean.getLabel()));
			this.legendsStore.layout();
		}

		if (this.legendsStore.getItemCount() == 0)
			super.hide();
	}
}
