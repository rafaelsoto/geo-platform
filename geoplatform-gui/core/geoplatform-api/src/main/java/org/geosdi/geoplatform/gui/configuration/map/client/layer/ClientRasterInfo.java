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
package org.geosdi.geoplatform.gui.configuration.map.client.layer;

import java.util.ArrayList;
import org.geosdi.geoplatform.gui.model.tree.GPStyleStringBeanModel;
import org.geosdi.geoplatform.gui.model.tree.visitor.IVisitorClient;

/**
 * @author Giuseppe La Scaleia - CNR IMAA geoSDI Group
 * @email giuseppe.lascaleia@geosdi.org
 * 
 */
public class ClientRasterInfo extends GPLayerClientInfo {

    /**
     *
     */
    private static final long serialVersionUID = 3956898488739664952L;
    //
    private float opacity = 1.0f;
    private ArrayList<GPStyleStringBeanModel> styles;
    
    /**
     * @return the styles
     */
    public ArrayList<GPStyleStringBeanModel> getStyles() {
        return styles;
    }

    /**
     * @return the opacity
     */
    public float getOpacity() {
        return opacity;
    }

    /**
     * @param opacity the opacity to set
     */
    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    /**
     * @param styles
     *            the styles to set
     */
    public void setStyles(ArrayList<GPStyleStringBeanModel> styles) {
        this.styles = styles;
    }

    @Override
    public void accept(IVisitorClient visitor) {
        visitor.visitRaster(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ClientRasterInfo [layerName = " + super.getLayerName() +
                ", getDataSource() = " + getDataSource()
                + ", getCrs() = " + getCrs() + ", getBbox() = " + getBbox()
                + ", getLayerType() = " + getLayerType() + ", getOpacity() =  "
                + getOpacity() + ", getzIndex() = " + getzIndex() + "]";
    }
}
