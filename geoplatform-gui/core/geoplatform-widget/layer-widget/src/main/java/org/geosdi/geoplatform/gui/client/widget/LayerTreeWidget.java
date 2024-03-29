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
package org.geosdi.geoplatform.gui.client.widget;

import org.geosdi.geoplatform.gui.client.widget.tree.GPTreePanel;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.store.TreeStore;
import org.geosdi.geoplatform.gui.client.listener.GPDNDListener;
import org.geosdi.geoplatform.gui.client.model.GPRootTreeNode;
import org.geosdi.geoplatform.gui.client.widget.tree.GeoPlatformTreeWidget;
import org.geosdi.geoplatform.gui.model.tree.GPBeanTreeModel;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.data.ModelStringProvider;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.dnd.TreePanelDragSource;
import com.extjs.gxt.ui.client.dnd.TreePanelDropTarget;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.DNDListener;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.TreePanelEvent;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.TreeStoreEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel.CheckCascade;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.geosdi.geoplatform.gui.client.LayerEvents;
import org.geosdi.geoplatform.gui.client.model.FolderTreeNode;
import org.geosdi.geoplatform.gui.client.model.visitor.VisitorDisplayHide;
import org.geosdi.geoplatform.gui.client.model.visitor.VisitorPosition;
import org.geosdi.geoplatform.gui.client.puregwt.timeout.IGPBuildTreeHandler;
import org.geosdi.geoplatform.gui.client.puregwt.timeout.IGPExpandTreeNodeHandler;
import org.geosdi.geoplatform.gui.client.puregwt.timeout.event.GPBuildTreeEvent;
import org.geosdi.geoplatform.gui.client.puregwt.timeout.event.GPExpandTreeNodeEvent;
import org.geosdi.geoplatform.gui.client.widget.SearchStatus.EnumSearchStatus;
import org.geosdi.geoplatform.gui.client.widget.contextmenu.GPDynamicTreeContextMenu;
import org.geosdi.geoplatform.gui.client.widget.decorator.GPLayerTreeDecorator;
import org.geosdi.geoplatform.gui.client.widget.store.GPTreeStoreWidget;
import org.geosdi.geoplatform.gui.client.widget.toolbar.mediator.MediatorToolbarTreeAction;
import org.geosdi.geoplatform.gui.configuration.map.client.layer.GPFolderClientInfo;
import org.geosdi.geoplatform.gui.configuration.map.client.layer.IGPFolderElements;
import org.geosdi.geoplatform.gui.configuration.message.GeoPlatformMessage;
import org.geosdi.geoplatform.gui.exception.GPSessionTimeout;
import org.geosdi.geoplatform.gui.impl.map.event.GPLoginEvent;
import org.geosdi.geoplatform.gui.impl.view.LayoutManager;
import org.geosdi.geoplatform.gui.puregwt.GPHandlerManager;
import org.geosdi.geoplatform.gui.puregwt.session.TimeoutHandlerManager;
import org.geosdi.geoplatform.gui.server.gwt.LayerRemoteImpl;
import org.geosdi.geoplatform.gui.view.event.GeoPlatformEvents;

/**
 * @author Giuseppe La Scaleia - CNR IMAA geoSDI Group
 * @email giuseppe.lascaleia@geosdi.org
 * 
 */
public class LayerTreeWidget extends GeoPlatformTreeWidget<GPBeanTreeModel>
        implements IGPBuildTreeHandler, IGPExpandTreeNodeHandler {

    private GPTreeStoreWidget treeStore;
    private GPLayerTreeDecorator treeDecorator;
    private VisitorDisplayHide visitorDisplay = new VisitorDisplayHide(this.tree);
    private TreePanelDragSource dragSource;
    private GPRootTreeNode root;
    private boolean initialized;
    private GPBuildTreeEvent buildEvent = new GPBuildTreeEvent();
    private ContentPanel parentPanel;

    /**
     * @param treePanel 
     * @Constructor
     */
    public LayerTreeWidget(ContentPanel treePanel) {
        super();
        this.parentPanel = treePanel;
        TimeoutHandlerManager.addHandler(IGPBuildTreeHandler.TYPE, this);
        TimeoutHandlerManager.addHandler(IGPExpandTreeNodeHandler.TYPE, this);
        this.buildRoot();
        this.setTreePanelProperties();
        this.treeStore = new GPTreeStoreWidget(this.tree);
        this.treeDecorator = new GPLayerTreeDecorator(tree);
        //Assigning a dynamic context menu to the tree
        GPDynamicTreeContextMenu dynamicMenu = new GPDynamicTreeContextMenu(tree);
    }

    /*
     * Create Root Composite Element
     *
     */
    private void buildRoot() {
        this.root = new GPRootTreeNode(this.tree);
    }

    /**
     * Build Tree
     */
    @Override
    public void buildTree() {
        if (!initialized) {
//            this.root.modelConverter(GeoPlatformUtils.getInstance().
//                    getGlobalConfiguration().getFolderStore().
//                    getFolders());
//            store.add(root, true);
//            initialized = true;

            LayoutManager.getInstance().getStatusMap().setBusy(
                    "Loading tree elements: please, wait untill contents fully loads.");
            LayerRemoteImpl.Util.getInstance().loadUserFolders(new AsyncCallback<ArrayList<GPFolderClientInfo>>() {

                @Override
                public void onFailure(Throwable caught) {
                    if (caught.getCause() instanceof GPSessionTimeout) {
                        GPHandlerManager.fireEvent(new GPLoginEvent(buildEvent));
                    } else {
                        GeoPlatformMessage.errorMessage("Error loading",
                                "An error occurred while making the requested connection.\n"
                                + "Verify network connections and try again."
                                + "\nIf the problem persists contact your system administrator.");
                        LayoutManager.getInstance().getStatusMap().setStatus(
                                "Error loading tree elements.",
                                EnumSearchStatus.STATUS_NO_SEARCH.toString());
                        System.out.println("Error loading tree elements: " + caught.toString()
                                + " data: " + caught.getMessage());
                    }
                }

                @Override
                public void onSuccess(
                        ArrayList<GPFolderClientInfo> result) {
                    root.modelConverter(result);
                    store.add(root, true);
                    visitorDisplay.enableCheckedComponent(root);
                    initialized = true;
                    tree.setExpanded(root, true);
                    LayoutManager.getInstance().getStatusMap().setStatus(
                            "Tree elements loaded successfully.",
                            EnumSearchStatus.STATUS_SEARCH.toString());
                }
            });
        }
    }

    /**
     * Set Tree Properties
     */
    @Override
    public void setTreePanelProperties() {
        this.addExpandListener();
        this.setTreePresenter();
        this.enableDDSupport();
        this.enableCheckChange();
    }

    /*
     * Define TreePresenter for both Icon and Label Presentation
     * 
     */
    private void setTreePresenter() {
        this.tree.setIconProvider(new ModelIconProvider<GPBeanTreeModel>() {

            @Override
            public AbstractImagePrototype getIcon(GPBeanTreeModel model) {
                return model.getIcon();
            }
        });

        this.tree.setLabelProvider(new ModelStringProvider<GPBeanTreeModel>() {

            @Override
            public String getStringValue(GPBeanTreeModel model, String property) {
                return model.getLabel();
            }
        });

        this.tree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        this.tree.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<GPBeanTreeModel>() {

            @Override
            public void selectionChanged(
                    SelectionChangedEvent<GPBeanTreeModel> se) {
                if (se.getSelectedItem() != null) {
                    MediatorToolbarTreeAction.getInstance().elementChanged(se.getSelectedItem());
                } else {
                    MediatorToolbarTreeAction.getInstance().disableAllActions();
                }

            }
        });
        this.setCheckable(true);
        this.setCheckStyle(CheckCascade.NONE);
        this.tree.setAutoHeight(true);
    }

    /*
     * Enable Check Box on Tree
     */
    private void enableCheckChange() {
        this.tree.addListener(Events.CheckChange, new GPCheckListener(visitorDisplay));
    }

    /*
     * Add Support for Drag and Drop
     * 
     */
    private void enableDDSupport() {
        dragSource = new TreePanelDragSource(super.tree);
        dragSource.addDNDListener(new DNDListener() {

            @Override
            public void dragStart(DNDEvent e) {
                ModelData sel = tree.getSelectionModel().getSelectedItem();
                if (sel != null
                        && sel == tree.getStore().getRootItems().get(0)) {
                    e.setCancelled(true);
                    e.getStatus().setStatus(false);
                    return;
                }
                super.dragStart(e);
                ((TreePanelDragSource) e.getSource()).fireEvent(
                        LayerEvents.GP_DRAG_START, new TreeStoreEvent<GPBeanTreeModel>(
                        tree.getStore()));
            }
        });

        GPDNDListener gpDNDListener = new GPDNDListener(this.visitorDisplay);
        dragSource.addListener(LayerEvents.GP_DRAG_START, gpDNDListener);
        dragSource.addListener(LayerEvents.GP_DRAG_LOST, gpDNDListener);

        //Listener for launch Drag Lost Events
        Listener listenerDragLost = new Listener() {

            @Override
            public void handleEvent(BaseEvent be) {
                ((TreePanelDragSource) be.getSource()).fireEvent(
                        LayerEvents.GP_DRAG_LOST, new TreeStoreEvent<GPBeanTreeModel>(
                        tree.getStore()));
                //System.out.println("DragSource: Ho intercettato il drag cancelled");
            }
        };
        //Intercepting Drag Lost Events
        dragSource.addListener(Events.DragCancel, listenerDragLost);
        dragSource.addListener(Events.DragEnd, listenerDragLost);
        dragSource.addListener(Events.DragFail, listenerDragLost);

        final TreePanelDropTarget dropTarget = new GPTreePanelDropTarget(super.tree);
        dropTarget.setAllowSelfAsSource(true);
        dropTarget.setAllowDropOnLeaf(false);
        dropTarget.setFeedback(Feedback.BOTH);

        dropTarget.addListener(LayerEvents.GP_DROP, gpDNDListener);

        super.store.addListener(Store.Add, gpDNDListener);
        //Important code to fix problem with D&D scroll support
        this.tree.addListener(Events.Render, new Listener() {

            @Override
            public void handleEvent(BaseEvent be) {
                dropTarget.getScrollSupport().setScrollElement(parentPanel.getLayoutTarget());
            }
        });
    } 

    @Override
    public GPTreePanel<GPBeanTreeModel> createTreePanel(TreeStore store) {
        return new GPTreePanel(store) {

            @Override
            protected boolean hasChildren(ModelData model) {
                return model instanceof FolderTreeNode || model instanceof GPRootTreeNode;
            }
        };
    }

    private void addExpandListener() {
        tree.addListener(Events.BeforeExpand, new Listener<TreePanelEvent<ModelData>>() {

            @Override
            public void handleEvent(TreePanelEvent<ModelData> be) {
                if ((be.getItem() instanceof FolderTreeNode)
                        && (!((FolderTreeNode) be.getItem()).isLoaded())
                        && (((FolderTreeNode) be.getItem()).getId() != 0L)) {
                    final VisitorPosition visitorPosition = new VisitorPosition();
                    final FolderTreeNode parentFolder = (FolderTreeNode) be.getItem();
                    parentFolder.setLoading(true);
                    LayoutManager.getInstance().getStatusMap().setBusy(
                            "Loading tree elements: please, wait untill contents fully loads.");

                    LayerRemoteImpl.Util.getInstance().loadFolderElements(
                            parentFolder.getId(), new AsyncCallback<ArrayList<IGPFolderElements>>() {

                        @Override
                        public void onFailure(Throwable caught) {
                            if (caught.getCause() instanceof GPSessionTimeout) {
                                GPHandlerManager.fireEvent(new GPLoginEvent(new GPExpandTreeNodeEvent(parentFolder)));
                            } else {
                                parentFolder.setLoading(false);
                                GeoPlatformMessage.errorMessage("Error loading",
                                        "An error occurred while making the requested connection.\n"
                                        + "Verify network connections and try again.\n"
                                        + "If the problem persists contact your system administrator.");
                                LayoutManager.getInstance().getStatusMap().setStatus(
                                        "Error loading tree elements.",
                                        EnumSearchStatus.STATUS_NO_SEARCH.toString());
                                System.out.println("Error loading tree elements: " + caught.toString()
                                        + " data: " + caught.getMessage());
                            }
                        }

                        @Override
                        public void onSuccess(ArrayList<IGPFolderElements> result) {
                            parentFolder.modelConverter(result);
                            List<GPBeanTreeModel> childrenList = new ArrayList<GPBeanTreeModel>();
                            visitorPosition.assignTmpIndex(parentFolder);
                            for (Iterator<ModelData> it = parentFolder.getChildren().iterator(); it.hasNext();) {
                                GPBeanTreeModel element = (GPBeanTreeModel) it.next();
                                element.accept(visitorPosition);
                                childrenList.add(element);
                            }
                            tree.getStore().insert(parentFolder, childrenList, 0, true);
                            visitorDisplay.enableCheckedComponent(parentFolder);
                            parentFolder.setLoading(false);
                            parentFolder.setLoaded(true);
                            tree.refreshIcon(parentFolder);
                            tree.fireEvent(GeoPlatformEvents.GP_NODE_EXPANDED);
                            LayoutManager.getInstance().getStatusMap().setStatus(
                                    "Tree elements loaded successfully.",
                                    EnumSearchStatus.STATUS_SEARCH.toString());
                        }
                    });
                }
            }
        });
        tree.addListener(Events.Expand, new Listener<TreePanelEvent<ModelData>>() {

            @Override
            public void handleEvent(TreePanelEvent<ModelData> be) {
                if (dragSource.getFiresEvents() && be.getItem() instanceof FolderTreeNode && !((FolderTreeNode) be.getItem()).isLoaded()) {
                    FolderTreeNode parentFolder = (FolderTreeNode) be.getItem();
                    tree.setExpanded(parentFolder, true);
                    dragSource.getDraggable().cancelDrag();
                } else if (be.getItem() instanceof FolderTreeNode && (((FolderTreeNode) be.getItem()).isLoaded())) {
                    tree.fireEvent(GeoPlatformEvents.GP_NODE_EXPANDED);
                }
            }
        });
    }

    @Override
    public void expandNode(FolderTreeNode node) {
        node.setLoading(true);
        this.tree.fireEvent(Events.BeforeExpand, new TreePanelEvent(this.tree, node));
        System.out.println("Called expanding node");
    }
}
