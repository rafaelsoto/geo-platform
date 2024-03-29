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
package org.geosdi.geoplatform.gui.client.widget.form;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.geosdi.geoplatform.gui.client.BasicWidgetResources;
import org.geosdi.geoplatform.gui.client.ServerWidgetResources;

import org.geosdi.geoplatform.gui.client.widget.DisplayServerWidget;
import org.geosdi.geoplatform.gui.client.widget.EnumSearchServer;
import org.geosdi.geoplatform.gui.client.widget.SaveStatus;
import org.geosdi.geoplatform.gui.client.widget.SaveStatus.EnumSaveStatus;
import org.geosdi.geoplatform.gui.client.widget.SearchStatus.EnumSearchStatus;
import org.geosdi.geoplatform.gui.configuration.message.GeoPlatformMessage;
import org.geosdi.geoplatform.gui.impl.view.LayoutManager;
import org.geosdi.geoplatform.gui.model.server.GPServerBeanModel;
import org.geosdi.geoplatform.gui.service.server.GeoPlatformOGCRemote;

/**
 *
 * @author Giuseppe La Scaleia - CNR IMAA geoSDI Group
 * @email  giuseppe.lascaleia@geosdi.org
 */
public class AddServerWidget extends GeoPlatformFormWidget<GPServerBeanModel> {

    private DisplayServerWidget displayServerWidget;
    private TextField<String> serverUrlTextField;
    private TextField<String> serverNameTextField;
    private Button save;
    private Button cancel;
    private PerformOperation performSaveServer;

    public AddServerWidget(DisplayServerWidget theWidget) {
        super(true);
        this.displayServerWidget = theWidget;
        this.performSaveServer = new PerformOperation();
    }

    @Override
    public void addComponentToForm() {
        this.fieldSet = new FieldSet();
        this.fieldSet.setHeading("Server");
        this.fieldSet.setToolTip("Insert a valid WMS Url and name for the server.");

        FormLayout layout = new FormLayout();
        layout.setLabelWidth(70);
        fieldSet.setLayout(layout);

        this.serverUrlTextField = new TextField<String>();
        this.serverNameTextField = new TextField<String>();
        this.serverUrlTextField.setFieldLabel("Address");
        this.serverNameTextField.setFieldLabel("Name");

        this.serverUrlTextField.addListener(Events.OnPaste, new Listener() {

            @Override
            public void handleEvent(BaseEvent be) {
                if (serverNameTextField.isDirty()) {
                    save.enable();
                }
            }
        });
        this.serverNameTextField.addListener(Events.OnPaste, new Listener() {

            @Override
            public void handleEvent(BaseEvent be) {
                if (serverUrlTextField.isDirty()) {
                    save.enable();
                }
            }
        });

        this.serverUrlTextField.addKeyListener(new KeyListener() {

            @Override
            public void componentKeyUp(ComponentEvent event) {
                if (serverUrlTextField.getValue() == null) {
                    if ((event.getKeyCode() == KeyCodes.KEY_BACKSPACE)
                            || (event.getKeyCode() == KeyCodes.KEY_DELETE)) {
                        reset();
                    }
                } else {
                    if (serverUrlTextField.getValue().length() > 15 && 
                            serverNameTextField.isDirty()) {
                        save.enable();
                    } else {
                        save.disable();
                    }
                }
            }

            @Override
            public void componentKeyPress(ComponentEvent event) {
                if (event.getKeyCode() == KeyCodes.KEY_ENTER && 
                        serverUrlTextField.isDirty() && serverNameTextField.isDirty()
                        && serverUrlTextField.getValue().length() > 15) {
                    execute();
                }
            }
        });
        this.serverNameTextField.addKeyListener(new KeyListener() {

            @Override
            public void componentKeyUp(ComponentEvent event) {
                if (serverNameTextField.getValue() == null) {
                    if ((event.getKeyCode() == KeyCodes.KEY_BACKSPACE)
                            || (event.getKeyCode() == KeyCodes.KEY_DELETE)) {
                        reset();
                    }
                } else {
                    if (serverUrlTextField.getValue().length() > 15) {
                        save.enable();
                    } else {
                        save.disable();
                    }
                }
            }

            @Override
            public void componentKeyPress(ComponentEvent event) {
                if (event.getKeyCode() == KeyCodes.KEY_ENTER && 
                        serverUrlTextField.isDirty() && 
                        serverUrlTextField.getValue().length() > 15) {
                    execute();
                }
            }
        });

        this.fieldSet.add(this.serverNameTextField);
        this.fieldSet.add(this.serverUrlTextField);

        this.formPanel.add(this.fieldSet);

        this.saveStatus = new SaveStatus();
        this.saveStatus.setAutoWidth(true);

        this.formPanel.getButtonBar().add(this.saveStatus);

        formPanel.setButtonAlign(HorizontalAlignment.RIGHT);

        save = new Button("Save", ServerWidgetResources.ICONS.addServer(),
                new SelectionListener<ButtonEvent>() {

                    @Override
                    public void componentSelected(ButtonEvent ce) {
                        execute();
                    }
                });

        save.setEnabled(false);

        this.formPanel.addButton(save);

        this.cancel = new Button("Cancel", BasicWidgetResources.ICONS.cancel(),
                new SelectionListener<ButtonEvent>() {

                    @Override
                    public void componentSelected(ButtonEvent ce) {
                        clearComponents();
                        clearStatusBarStatus();
                    }
                });

        this.formPanel.addButton(cancel);
    }

    @Override
    public void initSize() {
        setHeading("Add Server");
        setSize(380, 210);
    }

    @Override
    public void initSizeFormPanel() {
        this.formPanel.setHeaderVisible(false);
        this.formPanel.setSize(320, 120);
    }

    @Override
    public void execute() {
        this.saveStatus.setBusy("Adding Server");

        LayoutManager.getInstance().getStatusMap().setBusy("Saving Server");
        this.performSaveServer.addServer();
    }

    @Override
    public void reset() {
        this.save.disable();
        this.serverUrlTextField.clear();
        this.serverNameTextField.clear();
        this.saveStatus.clearStatus("");
    }

    public void showForm() {
        if (!isInitialized()) {
            super.init();
        }
        super.show();
    }

    private void clearComponents() {
        super.hide();
    }

    private void clearStatusBarStatus() {
        LayoutManager.getInstance().getStatusMap().clearStatus("");
    }

    /**
     * Internal Class for Business Logic
     * 
     */
    private class PerformOperation {

        private void addServer() {
            GPServerBeanModel server = displayServerWidget.containsServer(serverUrlTextField.getValue());
            if (server != null) {
                notifyServerPresence(server);
            } else {
                saveServer();
            }
        }

        private void saveServer() {
            GeoPlatformOGCRemote.Util.getInstance().insertServer(
                    null, serverNameTextField.getValue().trim(),
                    serverUrlTextField.getValue().trim(),
                    new AsyncCallback<GPServerBeanModel>() {

                        @Override
                        public void onFailure(Throwable caught) {
                            setStatus(EnumSaveStatus.STATUS_NO_SAVE.getValue(),
                                    EnumSaveStatus.STATUS_MESSAGE_NOT_SAVE.getValue());
                            LayoutManager.getInstance().getStatusMap().setStatus(
                                    "Save Server Error. " + caught.getMessage(),
                                    EnumSearchStatus.STATUS_SEARCH_ERROR.toString());
                        }

                        @Override
                        public void onSuccess(GPServerBeanModel server) {
                            clearComponents();
                            displayServerWidget.addServer(server);
                        }
                    });
        }

        private void notifyServerPresence(GPServerBeanModel server) {
            setStatus(EnumSaveStatus.STATUS_NO_SAVE.getValue(),
                    EnumSearchServer.STATUS_MESSAGE_SERVER_EXISTING.toString());
            LayoutManager.getInstance().getStatusMap().setStatus(
                    "Save Server",
                    EnumSearchStatus.STATUS_SEARCH_ERROR.toString());
            GeoPlatformMessage.alertMessage("Server Present",
                    "The Server with url : " + serverUrlTextField.getValue()
                    + " is already present in Combo Box with the name " +
                    server.getAlias() + ".");
        }
    }
}
