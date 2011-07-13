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
package org.geosdi.geoplatform.gui.model.tree.grid;

import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Giuseppe La Scaleia - CNR IMAA geoSDI Group
 * @email giuseppe.lascaleia@geosdi.org
 * 
 */
public abstract class GPTreeGridBeanModel extends BaseTreeModel {

    /**
     *
     */
    private static final long serialVersionUID = 4231108213570749653L;

    public enum GPKeyTreeGridModel {

        LABEL_NODE_VALUE("labelNode");
        private String value;

        /**
         *
         */
        GPKeyTreeGridModel(String theValue) {
            // TODO Auto-generated constructor stub
            this.value = theValue;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }

    
    private String labelNode;

    /**
     * @return the labelNode
     */
    public String getLabelNode() {
        return labelNode;
    }

    /**
     * @param labelNode
     *            the labelNode to set
     */
    public void setLabelNode(String labelNode) {
        this.labelNode = labelNode;
        set(GPKeyTreeGridModel.LABEL_NODE_VALUE.toString(), this.labelNode);
    }

    /**
     * Create Generic Widget for
     *
     * @return Widget
     */
    public abstract Widget getWidget();

    /**
     * Create Icon for each Node
     *
     * @return AbstractImagePrototype
     *
     */
    public abstract AbstractImagePrototype getIcon();
}
