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
package org.geosdi.geoplatform.gui.client;

import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import org.geosdi.geoplatform.gui.action.GeoPlatformToolbarAction;
import org.geosdi.geoplatform.gui.action.menu.MenuAction;
import org.geosdi.geoplatform.gui.action.menu.MenuActionCreator;
import org.geosdi.geoplatform.gui.action.menu.MenuActionRegistar;
import org.geosdi.geoplatform.gui.client.action.menu.LayerMenuAction;
import org.geosdi.geoplatform.gui.client.mvc.LayerController;

import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.google.gwt.core.client.EntryPoint;
import org.geosdi.geoplatform.gui.action.tree.ToolbarLayerTreeAction;
import org.geosdi.geoplatform.gui.action.tree.ToolbarTreeActionCreator;
import org.geosdi.geoplatform.gui.action.tree.ToolbarTreeActionRegistar;
import org.geosdi.geoplatform.gui.client.action.PreviewKmlTreeAction;
import org.geosdi.geoplatform.gui.client.action.PrintLayersAction;
import org.geosdi.geoplatform.gui.client.action.UploadShapeAction;
import org.geosdi.geoplatform.gui.client.action.menu.ExportoToKML;
import org.geosdi.geoplatform.gui.client.action.menu.ExportoToPDF;
import org.geosdi.geoplatform.gui.client.action.menu.ExportoToShpZip;
import org.geosdi.geoplatform.gui.client.action.menu.ExportoToTIFF;
import org.geosdi.geoplatform.gui.client.action.menu.ZoomToLayerExtentAction;
import org.geosdi.geoplatform.gui.client.action.toolbar.AddFolderTreeAction;
import org.geosdi.geoplatform.gui.client.action.toolbar.AddRasterTreeAction;
import org.geosdi.geoplatform.gui.client.action.toolbar.LoadWmsGetMapFromUrlTreeAction;
import org.geosdi.geoplatform.gui.client.action.toolbar.AddVectorTreeAction;
import org.geosdi.geoplatform.gui.client.action.toolbar.DeleteElementTreeAction;
import org.geosdi.geoplatform.gui.client.action.toolbar.SaveTreeAction;
import org.geosdi.geoplatform.gui.client.mvc.ServerController;
import org.geosdi.geoplatform.gui.impl.tree.ToolbarTreeClientTool;
import org.geosdi.geoplatform.gui.view.event.GeoPlatformEvents;

/**
 * @author Giuseppe La Scaleia - CNR IMAA geoSDI Group
 * @email giuseppe.lascaleia@geosdi.org
 * 
 */
public class LayerWidgetUI implements EntryPoint {

    private Dispatcher dispatcher;

    /*
     * (non-Javadoc)
     *
     * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
     */
    @Override
    public void onModuleLoad() {
        dispatcher = Dispatcher.get();

        dispatcher.addController(new LayerController());
        dispatcher.addController(new ServerController());

        addLayerWidgetAction();
        addToolbarTreeAction();

        dispatcher.fireEvent(GeoPlatformEvents.INIT_OGC_MODULES_WIDGET);
    }

    private void addLayerWidgetAction() {
        MenuActionRegistar.put("layerMenu", new MenuActionCreator() {

            @Override
            public MenuAction createAction() {
                return new LayerMenuAction();
            }
        });

        MenuActionRegistar.put("ZoomToLayerExtent", new MenuActionCreator() {

            @Override
            public MenuAction createAction() {
                return new ZoomToLayerExtentAction();
            }
        });

        MenuActionRegistar.put("exportToKML", new MenuActionCreator() {

            @Override
            public MenuAction createAction() {
                return new ExportoToKML();
            }
        });

        MenuActionRegistar.put("exportToPDF", new MenuActionCreator() {

            @Override
            public MenuAction createAction() {
                return new ExportoToPDF();
            }
        });


        MenuActionRegistar.put("exportToTIFF", new MenuActionCreator() {

            @Override
            public MenuAction createAction() {
                return new ExportoToTIFF();
            }
        });

        MenuActionRegistar.put("exportToShpZip", new MenuActionCreator() {

            @Override
            public MenuAction createAction() {
                return new ExportoToShpZip();
            }
        });

    }

    private void addToolbarTreeAction() {
        ToolbarTreeActionRegistar.put(ToolbarTreeClientTool.TOOLBAR_ADD_FOLDER,
                new ToolbarTreeActionCreator() {

                    @Override
                    public GeoPlatformToolbarAction createActionTool(
                            TreePanel tree) {
                        ToolbarLayerTreeAction action = new AddFolderTreeAction(
                                tree);
                        setAction(action);
                        return action;
                    }
                });

        ToolbarTreeActionRegistar.put(ToolbarTreeClientTool.TOOLBAR_ADD_RASTER,
                new ToolbarTreeActionCreator() {

                    @Override
                    public GeoPlatformToolbarAction createActionTool(
                            TreePanel tree) {
                        ToolbarLayerTreeAction action = new AddRasterTreeAction(
                                tree);
                        setAction(action);
                        return action;
                    }
                });

        ToolbarTreeActionRegistar.put(ToolbarTreeClientTool.TOOLBAR_ADD_VECTOR,
                new ToolbarTreeActionCreator() {

                    @Override
                    public GeoPlatformToolbarAction createActionTool(
                            TreePanel tree) {
                        ToolbarLayerTreeAction action = new AddVectorTreeAction(
                                tree);
                        setAction(action);
                        return action;
                    }
                });

        ToolbarTreeActionRegistar.put(ToolbarTreeClientTool.TOOLBAR_REMOVE_ELEMENT,
                new ToolbarTreeActionCreator() {

                    @Override
                    public GeoPlatformToolbarAction createActionTool(
                            TreePanel tree) {
                        ToolbarLayerTreeAction action = new DeleteElementTreeAction(
                                tree);
                        setAction(action);
                        return action;
                    }
                });

        ToolbarTreeActionRegistar.put(ToolbarTreeClientTool.TOOLBAR_SAVE_TREE_STATE,
                new ToolbarTreeActionCreator() {

                    @Override
                    public GeoPlatformToolbarAction createActionTool(
                            TreePanel tree) {
                        ToolbarLayerTreeAction action = new SaveTreeAction(tree);
                        setAction(action);
                        return action;
                    }
                });

        ToolbarTreeActionRegistar.put(ToolbarTreeClientTool.TOOLBAR_PRINT_TREE_LAYERS,
                new ToolbarTreeActionCreator() {

                    @Override
                    public GeoPlatformToolbarAction createActionTool(
                            TreePanel tree) {
                        ToolbarLayerTreeAction action = new PrintLayersAction(tree);
                        setAction(action);
                        return action;
                    }
                });

        ToolbarTreeActionRegistar.put(ToolbarTreeClientTool.TOOLBAR_UPLOAD_SHAPE,
                new ToolbarTreeActionCreator() {

                    @Override
                    public GeoPlatformToolbarAction createActionTool(
                            TreePanel tree) {
                        ToolbarLayerTreeAction action = new UploadShapeAction(tree);
                        setAction(action);
                        return action;
                    }
                });

        ToolbarTreeActionRegistar.put(ToolbarTreeClientTool.TOOLBAR_LOAD_WMS_GETMAP_FROM_URL,
                new ToolbarTreeActionCreator() {

                    @Override
                    public GeoPlatformToolbarAction createActionTool(
                            TreePanel tree) {
                        ToolbarLayerTreeAction action = new LoadWmsGetMapFromUrlTreeAction(tree);
                        setAction(action);
                        return action;
                    }
                });

//        ToolbarTreeActionRegistar.put(ToolbarTreeClientTool.TOOLBAR_LOAD_KML_FROM_URL,
//                new ToolbarTreeActionCreator() {
//
//                    @Override
//                    public GeoPlatformToolbarAction createActionTool(
//                            TreePanel tree) {
//                        ToolbarLayerTreeAction action = new LoadKmlFromUrlTreeAction(tree);
//                        setAction(action);
//                        return action;
//                    }
//                });
//
//        ToolbarTreeActionRegistar.put(ToolbarTreeClientTool.TOOLBAR_UPLOAD_KML,
//                new ToolbarTreeActionCreator() {
//
//                    @Override
//                    public GeoPlatformToolbarAction createActionTool(
//                            TreePanel tree) {
//                        ToolbarLayerTreeAction action = new UploadKmlTreeAction(tree);
//                        setAction(action);
//                        return action;
//                    }
//                });

        ToolbarTreeActionRegistar.put(ToolbarTreeClientTool.TOOLBAR_PREVIEW_KML_FROM_URL,
                new ToolbarTreeActionCreator() {

                    @Override
                    public GeoPlatformToolbarAction createActionTool(
                            TreePanel tree) {
                        ToolbarLayerTreeAction action = new PreviewKmlTreeAction(tree);
                        setAction(action);
                        return action;
                    }
                });
    }
}