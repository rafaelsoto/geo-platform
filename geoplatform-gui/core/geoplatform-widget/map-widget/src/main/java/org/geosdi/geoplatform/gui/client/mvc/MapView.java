/*
 *  geo-platform
 *  Rich webgis framework
 *  http://geo-plartform.org
 * ====================================================================
 *
 * Copyright (C) 2008-2010 geoSDI Group (CNR IMAA - Potenza - ITALY).
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

import org.geosdi.geoplatform.gui.client.MapWidgetEvents;
import org.geosdi.geoplatform.gui.client.widget.ButtonBar;
import org.geosdi.geoplatform.gui.client.widget.map.MapLayoutWidget;
import org.geosdi.geoplatform.gui.client.widget.map.MapUtility;
import org.geosdi.geoplatform.gui.configuration.mvc.GeoPlatformView;
import org.geosdi.geoplatform.gui.impl.view.LayoutManager;
import org.geosdi.geoplatform.gui.model.IGeoPlatformLocation;
import org.geosdi.geoplatform.gui.utility.GeoPlatformUtils;
import org.geosdi.geoplatform.gui.view.event.GeoPlatformEvents;
import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.feature.VectorFeature;
import org.gwtopenmaps.openlayers.client.layer.Layer;

import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;

/**
 * @author giuseppe
 * 
 */
public class MapView extends GeoPlatformView {

	private MapLayoutWidget mapLayout;
	private MapUtility mapUtility;

	private ButtonBar buttonBar;

	public MapView(Controller controller) {
		super(controller);

		this.mapLayout = new MapLayoutWidget();
	}

	public void initialize() {
		this.mapUtility = new MapUtility();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.extjs.gxt.ui.client.mvc.View#handleEvent(com.extjs.gxt.ui.client.
	 * mvc.AppEvent)
	 */
	@Override
	protected void handleEvent(AppEvent event) {
		if (event.getType() == MapWidgetEvents.INIT_MAP_WIDGET)
			onInitMapWidget();

		if (event.getType() == MapWidgetEvents.ATTACH_MAP_WIDGET)
			this.mapLayout.onAddToCenterPanel();

		if (event.getType() == MapWidgetEvents.ATTACH_TOOLBAR)
			onAttachToolbar();

		if (event.getType() == GeoPlatformEvents.REGISTER_GEOCODING_LOCATION)
			onRegisterGeocodingLocation((IGeoPlatformLocation) event.getData());

		if (event.getType() == GeoPlatformEvents.RemoveMarker)
			onRemoveMarker();

	}

	/**
	 * Remove Marker from Map
	 */
	private void onRemoveMarker() {
		// TODO Auto-generated method stub
		this.mapUtility.removeMarker();
	}

	/**
	 * Add a Marker on the Map with the coordinate of Location Found
	 * 
	 * @param event
	 */
	private void onRegisterGeocodingLocation(IGeoPlatformLocation bean) {
		// TODO Auto-generated method stub
		LonLat center = new LonLat(bean.getLon(), bean.getLat());
		center.transform("EPSG:4326", "EPSG:900913");
		this.mapUtility.addMarker(center, this.mapLayout.getMap());
	}

	/**
	 * Init Map Widget
	 */
	private void onInitMapWidget() {
		// TODO Auto-generated method stub
		this.addLayer(this.mapUtility.getMarkerLayer());
	}

	/**
	 * Activate Draw Control on Map
	 */
	public void deactivateDrawControl() {
		// TODO Auto-generated method stub
		this.mapLayout.deactivateDrawFeature();
	}

	/**
	 * Deactivate Draw Control on Map
	 */
	public void activateDrawControl() {
		// TODO Auto-generated method stub
		this.mapLayout.activateDrawFeature();
	}

	/**
	 * Attach GeoPlatform Toolbar to a LayoutManager
	 * 
	 * @param event
	 */
	private void onAttachToolbar() {
		mapLayout.setTools(GeoPlatformUtils.getInstance()
				.getGlobalConfiguration().getToolbarClientTool()
				.getClientTools());

		this.buttonBar = new ButtonBar(mapLayout);

		LayoutManager.addComponentToNorth(buttonBar.getToolBar());
	}

	/**
	 * Erase single Feature in MapLayout Vector Layer
	 * 
	 * @param event
	 */
	public void eraseFeature(VectorFeature vf) {
		this.mapLayout.eraseFeature(vf);
	}

	/**
	 * Update Map Size
	 */
	public void updateMapSize() {
		this.mapLayout.updateMapSize();
	}

	/**
	 * Add Layer to the Map
	 * 
	 * @param layer
	 */
	public void addLayer(Layer layer) {
		this.mapLayout.getMap().addLayer(layer);
	}

}
