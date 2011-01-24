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
package org.geosdi.geoplatform.gui.client.model;

import java.util.List;

import org.geosdi.geoplatform.gui.client.LayerResources;
import org.geosdi.geoplatform.gui.configuration.map.client.layer.ClientRasterInfo;
import org.geosdi.geoplatform.gui.impl.map.event.DisplayLayerEvent;
import org.geosdi.geoplatform.gui.impl.map.event.HideLayerEvent;
import org.geosdi.geoplatform.gui.model.GPRasterBean;
import org.geosdi.geoplatform.gui.model.tree.GPLayerTreeModel;
import org.geosdi.geoplatform.gui.model.tree.visitor.Visitor;
import org.geosdi.geoplatform.gui.puregwt.GPHandlerManager;

import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * @author Giuseppe La Scaleia - CNR IMAA geoSDI Group
 * @email giuseppe.lascaleia@geosdi.org
 * 
 */
public class RasterTreeNode extends GPLayerTreeModel implements GPRasterBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8265365333381641340L;

	private List<String> styles;

	/**
	 * @Constructor
	 * 
	 * @param label
	 */
	public RasterTreeNode(ClientRasterInfo layer) {
		super.setLabel(layer.getLayerName());
		super.setDataSource(layer.getDataSource());
		super.setCrs(layer.getCrs());
		super.setBbox(layer.getBbox());
		super.setzIndex(layer.getzIndex());
		super.setLayerType(layer.getLayerType());
		this.setStyles(layer.getStyles());
	}

	@Override
	public List<String> getStyles() {
		// TODO Auto-generated method stub
		return this.styles;
	}

	@Override
	public void setStyles(List<String> styles) {
		// TODO Auto-generated method stub
		this.styles = styles;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geosdi.geoplatform.gui.model.tree.GPBeanTreeModel#getIcon()
	 */
	@Override
	public AbstractImagePrototype getIcon() {
		// TODO Auto-generated method stub
		return LayerResources.ICONS.raster();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.geosdi.geoplatform.gui.model.tree.GPBeanTreeModel#notifyCheckEvent
	 * (boolean)
	 */
	@Override
	public void notifyCheckEvent(boolean isChecked) {
		// TODO Auto-generated method stub
		if (isParentChecked()) {
			if (isChecked)
				GPHandlerManager.fireEvent(new DisplayLayerEvent(this));
			else
				GPHandlerManager.fireEvent(new HideLayerEvent(this));
		}
	}

	@Override
	public void acceptForDisplay(Visitor visitor) {
		// TODO Auto-generated method stub
		visitor.visitForDisplay(this);
	}

	@Override
	public void acceptForHide(Visitor visitor) {
		// TODO Auto-generated method stub
		visitor.visitForHide(this);
	}

	@Override
	public void acceptForRemove(Visitor visitor) {
		// TODO Auto-generated method stub
		visitor.visitForRemove(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RasterTreeNode [getLabel()=" + getLabel() + ", styles="
				+ styles + ", getDataSource()=" + getDataSource()
				+ ", getCrs()=" + getCrs() + ", getBbox()=" + getBbox()
				+ ", getLayerType()=" + getLayerType() + ", getzIndex()="
				+ getzIndex() + "]";
	}

}
