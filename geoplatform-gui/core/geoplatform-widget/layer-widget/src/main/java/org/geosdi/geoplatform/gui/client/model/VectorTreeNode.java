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
package org.geosdi.geoplatform.gui.client.model;

import org.geosdi.geoplatform.gui.client.LayerResources;
import org.geosdi.geoplatform.gui.configuration.map.client.layer.ClientVectorInfo;
import org.geosdi.geoplatform.gui.model.GPVectorBean;
import org.geosdi.geoplatform.gui.model.tree.GPLayerTreeModel;
import org.geosdi.geoplatform.gui.model.tree.visitor.IVisitor;

import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * @author Giuseppe La Scaleia - CNR IMAA geoSDI Group
 * @email giuseppe.lascaleia@geosdi.org
 * 
 */
public class VectorTreeNode extends GPLayerTreeModel implements GPVectorBean {

    private static final long serialVersionUID = -2445765797861311204L;
    //
    private String featureNameSpace;

    public VectorTreeNode() {
    }

    /**
     * @Constructor
     *
     * @param label
     */
    public VectorTreeNode(ClientVectorInfo layer) {
        super(layer);
        super.setLabel(layer.getFeatureType()); // Label of a raster is different
        this.setFeatureNameSpace(layer.getFeatureNameSpace());
    }

    @Override
    public String getFeatureNameSpace() {
        return featureNameSpace;
    }

    @Override
    public void setFeatureNameSpace(String featureNameSpace) {
        this.featureNameSpace = featureNameSpace;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geosdi.geoplatform.gui.model.tree.GPBeanTreeModel#getIcon()
     */
    @Override
    public AbstractImagePrototype getIcon() {
        switch (getLayerType()) {
            case POINT:
                return LayerResources.ICONS.point();
            case MULTIPOINT:
                return LayerResources.ICONS.point();
            case LINESTRING:
                return LayerResources.ICONS.line();
            case MULTILINESTRING:
                return LayerResources.ICONS.line();
            case POLYGON:
                return LayerResources.ICONS.shape();
            case MULTIPOLYGON:
                return LayerResources.ICONS.shape();
        }
        return null;
    }

    @Override
    public void accept(IVisitor visitor) {
        visitor.visitVector(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "VectorTreeNode [featureNameSpace=" + featureNameSpace
                + ", getDataSource()=" + getDataSource() + ", getCrs()="
                + getCrs() + ", getBbox()=" + getBbox() + ", getLayerType()="
                + getLayerType() + ", getzIndex()=" + getzIndex()
                + ", getLabel()=" + getLabel() + "]";
    }
}
