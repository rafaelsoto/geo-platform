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
package org.geosdi.geoplatform.gui.model.tree;

import com.extjs.gxt.ui.client.data.ModelData;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.geosdi.geoplatform.gui.configuration.map.client.layer.GPFolderClientInfo;
import org.geosdi.geoplatform.gui.model.GPLayerBean;
import org.geosdi.geoplatform.gui.model.tree.visitor.IVisitor;

public abstract class AbstractFolderTreeNode extends GPBeanTreeModel {

    /**
     *
     */
    private static final long serialVersionUID = 4886440607031207404L;

    public enum GPFolderKeyValue {

        LABEL("label");
        //
        private String value;

        GPFolderKeyValue(String theValue) {
            this.value = theValue;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }

    protected AbstractFolderTreeNode() {
    }

    protected AbstractFolderTreeNode(GPFolderClientInfo folder) {
        super(folder.getId(), folder.getLabel(), folder.getzIndex(), folder.isChecked());
    }

    @Override
    public void accept(IVisitor visitor) {
        visitor.visitFolder(this);
    }

    @Override
    public void setLabel(String label) {
        super.setLabel(label);
        super.set(GPFolderKeyValue.LABEL.toString(), label);
    }
    
    /**
     * The Folder Child as a Map
     * 
     * @return 
     *          Map<String, GPLayerBean>
     * 
     */
    public Map<String, GPLayerBean> getLayers() {
        Map<String, GPLayerBean> childMap = new HashMap<String, GPLayerBean>();

        List<ModelData> childList = super.getChildren();

        for (Iterator<ModelData> it = childList.iterator(); it.hasNext();) {
            ModelData model = it.next();

            if (model instanceof GPLayerBean) {
                childMap.put(((GPLayerBean) model).getLabel(),
                        (GPLayerBean) model);
            }
        }

        return childMap;
    }
}
