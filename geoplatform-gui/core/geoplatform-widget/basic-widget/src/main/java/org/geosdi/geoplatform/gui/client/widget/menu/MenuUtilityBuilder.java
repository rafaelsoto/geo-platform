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
package org.geosdi.geoplatform.gui.client.widget.menu;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.widget.menu.CheckMenuItem;
import com.extjs.gxt.ui.client.widget.menu.DateMenu;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import java.util.List;
import org.geosdi.geoplatform.gui.action.menu.MenuActionRegistar;
import org.geosdi.geoplatform.gui.action.menu.MenuBaseAction;
import org.geosdi.geoplatform.gui.action.menu.MenuCheckAction;
import org.geosdi.geoplatform.gui.action.menu.OAuth2MenuBaseAction;
import org.geosdi.geoplatform.gui.action.menu.event.MenuActionChangeIconEvent;
import org.geosdi.geoplatform.gui.action.menu.event.MenuActionChangeIconHandler;
import org.geosdi.geoplatform.gui.action.menu.event.MenuActionDisabledEvent;
import org.geosdi.geoplatform.gui.action.menu.event.MenuActionEnabledEvent;
import org.geosdi.geoplatform.gui.action.menu.event.MenuCheckChangeActionHandler;
import org.geosdi.geoplatform.gui.configuration.menubar.CheckMenuClientTool;
import org.geosdi.geoplatform.gui.configuration.menubar.DateMenuClientTool;
import org.geosdi.geoplatform.gui.configuration.menubar.GroupMenuClientTool;
import org.geosdi.geoplatform.gui.configuration.menubar.MenuBarClientTool;
import org.geosdi.geoplatform.gui.configuration.menubar.OAuth2MenuBarClientTool;

/**
 * @author Nazzareno Sileno - CNR IMAA geoSDI Group
 * @email nazzareno.sileno@geosdi.org
 */
public class MenuUtilityBuilder {

    public static final String MENU_BAR_SEPARATOR = "MenuBarSeparator";

    /**
     *
     * @param tools
     * @param menu
     */
    public static void buildTools(Menu menu, List<MenuBarClientTool> tools) {
        for (MenuBarClientTool tool : tools) {
            if (tool.getId().equals(MENU_BAR_SEPARATOR)) {
                addMenuSeparator(menu);
            } else {
                checkToolType(tool, menu);
            }
        }
    }

    /**
     * Add a Separator in Menu
     * @param menu 
     */
    private static void addMenuSeparator(Menu menu) {
        menu.add(new SeparatorMenuItem());
    }

    /**
     *
     * @param tool
     * @param menu
     */
    private static void checkToolType(MenuBarClientTool tool, Menu menu) {
        if (tool instanceof CheckMenuClientTool) {
            addCheckMenuItem((CheckMenuClientTool) tool, menu);
        } else if (tool instanceof DateMenuClientTool) {
            addDateMenu(menu);
        } else if (tool instanceof GroupMenuClientTool) {
            addGroupMenuItem((GroupMenuClientTool) tool, menu);
        } else if (tool instanceof OAuth2MenuBarClientTool) {
            addOAuth2MenuItem((OAuth2MenuBarClientTool) tool, menu);
        } else {
            addMenuItem(tool, menu);
        }
    }

    /**
     * Add CheckMenuItem to a Menu
     *
     * @param tool
     * @param menu
     */
    private static void addCheckMenuItem(CheckMenuClientTool tool, final Menu menu) {
        MenuCheckAction action = (MenuCheckAction) MenuActionRegistar.get(tool.getId());
        
        final CheckMenuItem item = new CheckMenuItem(tool.getText());
        item.setItemId(tool.getId());
        
        menu.add(item);

        if (action != null) {
            action.setId(tool.getId());
            item.addSelectionListener(action);
            
            action.addMenuActionHandler(new MenuCheckChangeActionHandler() {

                @Override
                public void onActionCheckChange(boolean check) {
                    item.setChecked(check);
                    item.fireEvent(Events.Select, new MenuEvent(menu, item));
                }

                @Override
                public void onActionEnabled(MenuActionEnabledEvent event) {
                    item.setEnabled(true);
                }

                @Override
                public void onActionDisabled(MenuActionDisabledEvent event) {
                    item.setEnabled(false);
                }
            });
            
            action.setEnabled(tool.isEnabled());
            action.setChecked(tool.isChecked());
        }
    }

    /**
     * Add a DateMenu Item to Menu
     *
     * @param menu
     */
    private static void addDateMenu(Menu menu) {
        MenuItem date = new MenuItem("Choose a Date");
        menu.add(date);
        date.setSubMenu(new DateMenu());
    }

    /**
     * Add a MenuItem with sub menu
     *
     * @param tool
     * @param menu  
     */
    private static void addGroupMenuItem(GroupMenuClientTool tool, Menu menu) {
        MenuItem item = new MenuItem(tool.getText());
        menu.add(item);
        Menu subMenu = new Menu();
        buildTools(subMenu, tool.getTools());
        item.setSubMenu(subMenu);
    }

    /**
     * Add a MenuItem with sub menu
     *
     * @param tool
     * @param menu  
     */
    private static void addOAuth2MenuItem(OAuth2MenuBarClientTool tool, Menu menu) {
        OAuth2MenuBaseAction action = (OAuth2MenuBaseAction) MenuActionRegistar.get(
                tool.getId());

        final MenuItem item = new MenuItem(tool.getText());

        if (action != null) {
            action.setId(tool.getId());
            item.setIcon(action.getImage());
            item.setItemId(action.getId());
            item.addSelectionListener(action);

            action.addMenuActionHandler(new MenuActionChangeIconHandler() {

                @Override
                public void onActionEnabled(MenuActionEnabledEvent event) {
                    item.setEnabled(true);
                }

                @Override
                public void onActionDisabled(MenuActionDisabledEvent event) {
                    item.setEnabled(false);
                }

                @Override
                public void onActionChangeIcon(MenuActionChangeIconEvent event) {
                    item.setIcon(event.getImage());
                }
            });

            action.setEnabled(tool.isEnabled());
            action.setGoogleAuthUrl(tool.getGoogleAuthUrl());
            action.setGoogleClientId(tool.getGoogleClientId());
            action.setScope(tool.getScope());
        }
        menu.add(item);
    }

    /**
     * Add Simple MenuItem to Menu
     *
     * @param tool
     * @param menu
     */
    private static void addMenuItem(MenuBarClientTool tool, Menu menu) {
        MenuBaseAction action = (MenuBaseAction) MenuActionRegistar.get(
                tool.getId());

        final MenuItem item = new MenuItem(tool.getText());

        if (action != null) {
            action.setId(tool.getId());
            item.setIcon(action.getImage());
            item.setItemId(action.getId());
            item.addSelectionListener(action);

            action.addMenuActionHandler(new MenuActionChangeIconHandler() {

                @Override
                public void onActionEnabled(MenuActionEnabledEvent event) {
                    item.setEnabled(true);
                }

                @Override
                public void onActionDisabled(MenuActionDisabledEvent event) {
                    item.setEnabled(false);
                }

                @Override
                public void onActionChangeIcon(MenuActionChangeIconEvent event) {
                }
            });

            action.setEnabled(tool.isEnabled());
        }
        menu.add(item);
    }
}
