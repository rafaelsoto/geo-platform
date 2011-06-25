//<editor-fold defaultstate="collapsed" desc="License">
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
//</editor-fold>
package org.geosdi.geoplatform;

import org.geosdi.geoplatform.core.dao.GPAuthorityDAO;
import org.geosdi.geoplatform.core.dao.GPFolderDAO;
import org.geosdi.geoplatform.core.dao.GPLayerDAO;
import org.geosdi.geoplatform.core.dao.GPServerDAO;
import org.geosdi.geoplatform.core.dao.GPStyleDAO;
import org.geosdi.geoplatform.core.dao.GPUserDAO;
import org.geosdi.geoplatform.core.dao.GPUserFoldersDAO;
import org.geosdi.geoplatform.core.model.GPAuthority;
import org.geosdi.geoplatform.core.model.GPFolder;
import org.geosdi.geoplatform.core.model.GPLayer;
import org.geosdi.geoplatform.core.model.GPStyle;
import org.geosdi.geoplatform.core.model.GPUser;
import org.geosdi.geoplatform.core.model.GPUserFolders;
import org.geosdi.geoplatform.core.model.UserFolderPk;
import org.geotools.data.ows.Layer;
import org.geotools.data.ows.WMSCapabilities;
import org.geotools.data.wms.WebMapServer;
import org.geotools.ows.ServiceException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.geosdi.geoplatform.core.model.GPBBox;
import org.geosdi.geoplatform.core.model.GPLayerInfo;
import org.geosdi.geoplatform.core.model.GPLayerType;
import org.geosdi.geoplatform.core.model.GPRasterLayer;
import org.geosdi.geoplatform.core.model.GPVectorLayer;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

/**
 * @author Giuseppe La Scaleia - CNR IMAA geoSDI Group
 * @email giuseppe.lascaleia@geosdi.org
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public abstract class BaseDAOTest {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    //
    @Autowired
    protected GPUserDAO userDAO;
    //
    @Autowired
    protected GPUserFoldersDAO userFoldersDAO;
    //
    @Autowired
    protected GPFolderDAO folderDAO;
    //
    @Autowired
    protected GPLayerDAO layerDAO;
    //
    @Autowired
    protected GPStyleDAO styleDAO;
    //
    @Autowired
    protected GPServerDAO serverDAO;
    //
    @Autowired
    protected GPAuthorityDAO authorityDAO;
    //
    protected final String nameUserTest_1 = "user_test_1";
    protected final String nameUserTest_2 = "user_test_2";
    protected GPUser userTest_1 = null;
    protected GPUser userTest_2 = null;
    // ACL
    protected final String nameSuperUser = "super_user_test_acl";
    protected final String roleAdmin = "ROLE_ADMIN";
    protected final String roleUser = "ROLE_USER";

    //<editor-fold defaultstate="collapsed" desc="Remove all data">
    protected void removeAll() {
        removeAllStyles();
        removeAllLayers();
        removeAllUserFolders();
        removeAllFolders();
        removeAllAuthorities();
        removeAllUsers();
    }

    private void removeAllUserFolders() {
        List<GPUserFolders> userFolders = userFoldersDAO.findAll();
        // UserFolders sorted in descending order (wrt position)
        Comparator comp = new Comparator() {

            @Override
            public int compare(Object o1, Object o2) {
                GPUserFolders userFolder1 = (GPUserFolders) o1;
                GPUserFolders userFolder2 = (GPUserFolders) o2;
                return userFolder1.getPosition() - userFolder2.getPosition();
            }
        };
        Collections.sort(userFolders, comp);
        // Delete before the sub-UserFolders        
        for (GPUserFolders userFolder : userFolders) {
            logger.debug("\n*** UserFolder to REMOVE:\n{}\n***", userFolder); // TODO Trace
            boolean removed = userFoldersDAO.remove(userFolder);
            Assert.assertTrue("Old UserFolder NOT removed", removed);
        }
    }

    private void removeAllFolders() {
        List<GPFolder> folders = folderDAO.findAll();
        for (GPFolder folder : folders) {
            logger.debug("\n*** Folder to REMOVE:\n{}\n***", folder); // TODO Trace
            boolean removed = folderDAO.remove(folder);
            Assert.assertTrue("Old Folder NOT removed", removed);
        }
    }

    private void removeAllLayers() {
        List<GPLayer> layers = layerDAO.findAll();
        for (GPLayer layer : layers) {
            logger.trace("\n*** Layer to REMOVE:\n{}\n***", layer);
            boolean removed = layerDAO.remove(layer);
            Assert.assertTrue("Old Layer NOT removed", removed);
        }
    }

    private void removeAllStyles() {
        List<GPStyle> styles = styleDAO.findAll();
        for (GPStyle style : styles) {
            logger.trace("\n*** Style to REMOVE:\n{}\n***", style);
            boolean removed = styleDAO.remove(style);
            Assert.assertTrue("Old Style NOT removed", removed);
        }
    }

    protected void removeAllAuthorities() {
        List<GPAuthority> authorities = authorityDAO.findAll();
        for (GPAuthority authority : authorities) {
            logger.trace("\n*** Authority to REMOVE:\n{}\n***", authority);
            boolean removed = authorityDAO.remove(authority);
            Assert.assertTrue("Old Authority NOT removed", removed);
        }
    }

    protected void removeAllUsers() {
        List<GPUser> users = userDAO.findAll();
        for (GPUser user : users) {
            logger.trace("\n*** User to REMOVE:\n{}\n***", user);
            boolean removed = userDAO.remove(user);
            Assert.assertTrue("Old User NOT removed", removed);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Insert data">
    protected void insertData() throws ParseException {
        this.userTest_1 = this.insertUser(nameUserTest_1, roleAdmin);
        this.userTest_2 = this.insertUser(nameUserTest_2, roleUser);
        // ACL Data
        this.insertUser(nameSuperUser, roleAdmin, roleUser);
        this.insertUser("admin_acl_test", roleAdmin);
        this.insertUser("user_acl_test", roleUser);
    }

    protected GPUser insertUser(String name, String... roles) {
        GPUser user = createUser(name);
        userDAO.persist(user);
        logger.debug("\n*** User SAVED:\n{}\n***", user);

        if (roles.length > 0) {
            List<GPAuthority> authorities = createAuthorities(user.getUsername(), roles);
            user.setGpAuthorities(authorities);

            for (GPAuthority authority : authorities) {
                authorityDAO.persist(authority);
                logger.trace("\n*** Authority SAVED:\n{}\n***", authority);
            }
        }

        return user;
    }

    private List<GPAuthority> createAuthorities(String username, String... roles) {
        List<GPAuthority> authorities = new ArrayList<GPAuthority>();
        for (String role : roles) {
            GPAuthority auth = new GPAuthority(username, role);
            authorities.add(auth);
        }
        return authorities;
    }

    private GPUser createUser(String username) {
        GPUser user = new GPUser();
        user.setUsername(username);
        user.setEmailAddress(username + "@test");
        user.setEnabled(true);
        user.setPassword("pwd_" + username);
        user.setSendEmail(true);
        return user;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Insert folders">
    protected void insertFolders() throws ParseException {
        List<Layer> layerList = loadLayersFromServer();
        insertUserFolders(layerList);
    }

    private void insertUserFolders(List<Layer> layerList) {
        // +6 because {"only folders"; "empty subfolder A"; "empty subfolder B"; "my raster"; "IGM"; "vector layer"}
        int position = layerList.size() + 6;

        // "only folders"
        GPFolder onlyFolders = new GPFolder("only folders");
        GPUserFolders userOnlyFolders = this.createUserFolderBind(userTest_1, onlyFolders, position, null);

        // "only folders" ---> "empty subfolder A"
        GPFolder emptySubFolderA = new GPFolder("empty subfolder A");
        this.createUserFolderBind(userTest_1, emptySubFolderA, --position, userOnlyFolders);

        // "only folders" ---> "empty subfolder B"
        GPFolder emptySubFolderB = new GPFolder("empty subfolder B");
        this.createUserFolderBind(userTest_1, emptySubFolderB, --position, userOnlyFolders);

        onlyFolders.setNumberOfDescendants(2);
        folderDAO.persist(onlyFolders, emptySubFolderA, emptySubFolderB); // Persist also the GPUserFolders entities

//        // "my raster"
        GPFolder folderRaster = new GPFolder("my raster");
        GPUserFolders userFolderRaster = this.createUserFolderBind(userTest_1, folderRaster, --position, null);

        // "my raster" ---> _rasterLayer1_ ---> Styles
        GPRasterLayer rasterLayer1 = this.createRasterLayer(--position, userFolderRaster);
        GPStyle style1 = this.createStyle("style 1", rasterLayer1);
        GPStyle style2 = this.createStyle("style 2", rasterLayer1);
        //
        folderRaster.setNumberOfDescendants(layerList.size() + 2); // +2 because {"IGM"; "vector layer"}
        folderDAO.persist(folderRaster);
        layerDAO.persist(rasterLayer1);
        styleDAO.persist(style1, style2);

        List<GPRasterLayer> layers = this.loadRasterLayer(layerList, position, userFolderRaster);
        layerDAO.persist(layers.toArray(new GPRasterLayer[]{}));

        position = position - layerList.size();

        // ---> "my raster" --> "IGM"
        GPFolder folderIGM = new GPFolder("IGM");
        GPUserFolders userFolderIGM = this.createUserFolderBind(userTest_1, folderIGM, --position, userFolderRaster);

        // ---> "my raster" --> "IGM" _vectorLayer1_
        GPVectorLayer vectorLayer1 = this.createVectorLayer(--position, userFolderIGM);
        //
        folderIGM.setNumberOfDescendants(1);
        folderDAO.persist(folderIGM);
        layerDAO.persist(vectorLayer1);
    }

    protected GPUserFolders createUserFolderBind(GPUser user, GPFolder folder,
            int position, GPUserFolders userFolderParent) {
        GPUserFolders userFolder = new GPUserFolders();
        userFolder.setPk(new UserFolderPk(user, folder));
        userFolder.setPosition(position);
        userFolder.setParent(userFolderParent);

        folder.addUserFolder(userFolder);

        return userFolder;
    }

    protected GPRasterLayer createRasterLayer(int position, GPUserFolders userFolder) {
        String name = "deagostini_ita_250mila";
        // GPRasterLayer
        GPRasterLayer raster = new GPRasterLayer();
        raster.setTitle("StratiDiBase:" + name);
        raster.setName(name);
        raster.setAbstractText("abstract:" + name);
        raster.setPosition(position);
        raster.setSrs("EPSG:4326");
        raster.setUrlServer("http://dpc.geosdi.org/geoserver/wms");
        raster.setBbox(new GPBBox(6.342, 35.095, 19.003, 47.316));
        raster.setLayerType(GPLayerType.RASTER);
        raster.setUserFolders(userFolder);
        // GPLayerInfo
        GPLayerInfo info = new GPLayerInfo();
        List<String> keywords = new ArrayList<String>();
        keywords.add("IGM");
        info.setKeywords(keywords);
        info.setQueryable(true);
        raster.setLayerInfo(info);
        return raster;
    }

    private GPStyle createStyle(String name, GPRasterLayer layer) {
        GPStyle style = new GPStyle();
        style.setName(name);
        style.setTitle("The " + name);
        style.setAbstractText("Abstract for " + name);
        style.setLegendURL("http://www.geosdi.org/"
                + name.replaceAll("[ ]+", "-"));
        style.setLayer(layer);
        return style;
    }

    protected GPVectorLayer createVectorLayer(int position, GPUserFolders userFolder) {
        GPVectorLayer vector = new GPVectorLayer();
        vector.setName("Name of vectorLayer");
        vector.setTitle("Title of vectorLayer");
        vector.setPosition(position);
        vector.setAbstractText("AbstractText of vectorLayer");
        vector.setSrs("EPSG:4326");
        vector.setUrlServer("http://imaa.geosdi.org/geoserver/wms");
        vector.setBbox(new GPBBox(1.1, 2.2, 3.3, 3.3));
        vector.setLayerType(GPLayerType.MULTIPOLYGON);
        vector.setChecked(true);
        vector.setUserFolders(userFolder);
        return vector;
    }

    // Load imaa Layers
    private List<Layer> loadLayersFromServer() {
        URL url = null;
        try {
            url = new URL("http://imaa.geosdi.org/geoserver/wms?service=wms&version=1.1.1&request=GetCapabilities");
        } catch (MalformedURLException e) {
            logger.error("Error:" + e);
        }

        WebMapServer wms = null;
        List<Layer> layers = null;
        try {
            wms = new WebMapServer(url);

            WMSCapabilities capabilities = wms.getCapabilities();

            layers = capabilities.getLayerList();
        } catch (IOException e) {
            //There was an error communicating with the server
            //For example, the server is down
        } catch (ServiceException e) {
            //The server returned a ServiceException (unusual in this case)
        } catch (SAXException e) {
            //Unable to parse the response from the server
            //For example, the capabilities it returned was not valid
        }

        return layers;
    }

    private List<GPRasterLayer> loadRasterLayer(List<Layer> layers, int position, GPUserFolders userFolder) {
        List<GPRasterLayer> rasterLayers = null;
        rasterLayers = new ArrayList<GPRasterLayer>(layers.size());

        for (int i = 1; i < layers.size(); i++) {
            Layer layer = layers.get(i);

            GPRasterLayer raster = new GPRasterLayer();
            raster.setName(layer.getName());
            raster.setTitle(layer.getTitle());
            raster.setAbstractText(layer.get_abstract());
            raster.setSrs(layer.getSrs().toString());
            raster.setBbox(new GPBBox(
                    layer.getLatLonBoundingBox().getMinX(),
                    layer.getLatLonBoundingBox().getMinY(),
                    layer.getLatLonBoundingBox().getMaxX(),
                    layer.getLatLonBoundingBox().getMaxY()));

            GPLayerInfo infoLayer = new GPLayerInfo();
            infoLayer.setQueryable(layer.isQueryable());
            if (layer.getKeywords() != null) {
                List<String> keywordList = Arrays.asList(layer.getKeywords());
                if (keywordList.size() > 0) {
                    infoLayer.setKeywords(keywordList);
                }
            }
            raster.setLayerInfo(infoLayer);

            raster.setUserFolders(userFolder);
            raster.setLayerType(GPLayerType.RASTER);
            raster.setPosition(--position);
            raster.setUrlServer("http://imaa.geosdi.org/geoserver/wms");
            if (i < 5) {
                raster.setChecked(true);
            }
            rasterLayers.add(raster);
        }

        return rasterLayers;
    }
//</editor-fold>
}
