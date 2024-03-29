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
package org.geosdi.geoplatform.gui.client.action.toolbar;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import org.geosdi.geoplatform.gui.action.ISave;
import org.geosdi.geoplatform.gui.action.tree.ToolbarLayerTreeAction;
import org.geosdi.geoplatform.gui.client.BasicWidgetResources;
import org.geosdi.geoplatform.gui.client.LayerEvents;
import org.geosdi.geoplatform.gui.client.model.memento.save.GPMementoSaveCache;
import org.geosdi.geoplatform.gui.client.model.memento.puregwt.GPPeekCacheEventHandler;
import org.geosdi.geoplatform.gui.model.memento.IMemento;
import org.geosdi.geoplatform.gui.observable.Observable;
import org.geosdi.geoplatform.gui.observable.Observer;
import org.geosdi.geoplatform.gui.puregwt.layers.LayerHandlerManager;
import org.geosdi.geoplatform.gui.puregwt.progressbar.layers.event.DisplayLayersProgressBarEvent;
import org.geosdi.geoplatform.gui.puregwt.session.TimeoutHandlerManager;

/**
 * @author Nazzareno Sileno - CNR IMAA geoSDI Group
 * @email nazzareno.sileno@geosdi.org
 */
public class SaveTreeAction extends ToolbarLayerTreeAction
        implements GPPeekCacheEventHandler, Observer {

    private DisplayLayersProgressBarEvent displayEvent = new DisplayLayersProgressBarEvent(true);
    private boolean visibiltyProgressBar;

    public SaveTreeAction(TreePanel theTree) {
        super(theTree, BasicWidgetResources.ICONS.save(), "Save Tree State");
        displayEvent.setMessage("Saving Operations On Service");
        GPMementoSaveCache.getInstance().getObservable().addObserver(this);
        TimeoutHandlerManager.addHandler(GPPeekCacheEventHandler.TYPE, this);
        LayerHandlerManager.addHandler(GPPeekCacheEventHandler.TYPE, this);
    }

    @Override
    public void componentSelected(ButtonEvent ce) {
        this.showProgressBar();
        this.peek();
    }

    @Override
    public void update(Observable o, Object o1) {
        //System.out.println("SaveTreeAction receive observable notify");
        if (LayerEvents.SAVE_CACHE_NOT_EMPTY == ((EventType) o1)) {
            super.setEnabled(true);
        } else {
            super.setEnabled(false);
        }
    }

    @Override
    public void peek() {
        if (GPMementoSaveCache.getInstance().peek() != null) {
            IMemento<ISave> memento = GPMementoSaveCache.getInstance().peek();
            memento.getAction().executeSave(GPMementoSaveCache.getInstance().peek());
        } else {
            this.displayEvent.setVisible(false);
            LayerHandlerManager.fireEvent(this.displayEvent);
            this.visibiltyProgressBar = false;
        }
    }

    private void showProgressBar() {
        if (!visibiltyProgressBar) {
            this.displayEvent.setVisible(true);
            LayerHandlerManager.fireEvent(this.displayEvent);
            this.visibiltyProgressBar = true;
        }
    }
}