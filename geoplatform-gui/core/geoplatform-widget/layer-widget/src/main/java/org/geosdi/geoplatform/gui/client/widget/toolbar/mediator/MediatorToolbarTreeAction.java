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
package org.geosdi.geoplatform.gui.client.widget.toolbar.mediator;

import org.geosdi.geoplatform.gui.action.GeoPlatformToolbarAction;
import org.geosdi.geoplatform.gui.action.tree.ToolbarTreeActionCreator;
import org.geosdi.geoplatform.gui.action.tree.ToolbarTreeActionRegistar;
import org.geosdi.geoplatform.gui.client.model.visitor.VisitorToolbarTreeAction;
import org.geosdi.geoplatform.gui.configuration.action.GeoPlatformActionCreator;
import org.geosdi.geoplatform.gui.model.tree.GPBeanTreeModel;

/**
 * @author Nazzareno Sileno - CNR IMAA geoSDI Group
 * @email  nazzareno.sileno@geosdi.org
 */
public class MediatorToolbarTreeAction {

    private VisitorToolbarTreeAction actionVisitor;

    public MediatorToolbarTreeAction() {
        this.actionVisitor = new VisitorToolbarTreeAction(this);
    }

    /**
     *
     * @param element
     */
    public void elementChanged(GPBeanTreeModel element) {
        element.accept(actionVisitor);
    }

    /**
     *
     * @param idActions
     */
    public void enableActions(String... idActions) {
        for (String idAcion : idActions) {
            GeoPlatformToolbarAction action = ToolbarTreeActionRegistar.get(
                    idAcion);
            if (action != null) {
                action.setEnabled(true);
            }
        }
    }

    /**
     *
     * @param idActions
     */
    public void disableActions(String... idActions) {
        for (String idAction : idActions) {
            GeoPlatformToolbarAction action = ToolbarTreeActionRegistar.get(
                    idAction);
            if (action != null) {
                action.setEnabled(false);
                
            }
        }
    }

    /**
     * Disable all Actions
     * 
     */
    public void disableAllActions() {
        for (GeoPlatformActionCreator actionCreator : ToolbarTreeActionRegistar.getActionsCreator()) {
            ((ToolbarTreeActionCreator) actionCreator).getAction().setEnabled(
                    false);
        }
    }
}