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

import java.util.ArrayList;
import java.util.List;
import org.geosdi.geoplatform.exception.IllegalParameterFault;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mortbay.jetty.Server;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.geosdi.geoplatform.core.model.GPBBox;
import org.geosdi.geoplatform.core.model.GPFolder;
import org.geosdi.geoplatform.core.model.GPLayer;
import org.geosdi.geoplatform.core.model.GPLayerInfo;
import org.geosdi.geoplatform.core.model.GPLayerType;
import org.geosdi.geoplatform.core.model.GPRasterLayer;
import org.geosdi.geoplatform.core.model.GPUser;
import org.geosdi.geoplatform.core.model.GPUserFolders;
import org.geosdi.geoplatform.core.model.GPVectorLayer;
import org.geosdi.geoplatform.cxf.GeoPlatformWSClient;
import org.geosdi.geoplatform.request.RequestById;
import org.geosdi.geoplatform.request.SearchRequest;
import org.geosdi.geoplatform.services.GeoPlatformService;

/**
 * @author Francesco Izzi - CNR IMAA - geoSDI
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public abstract class ServiceTest implements InitializingBean {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    //
    @Autowired
    protected GeoPlatformWSClient gpWSClient;
    //
    protected GeoPlatformService geoPlatformService;
    //
    @Autowired
    protected Server gpJettyServer;
    // User
    protected final String usernameTest = "username_test_ws";
    protected long idUserTest = -1;
    protected GPUser userTest = null;
    // Folders
    protected final String nameRootFolderA = "rootFolderA";
    protected final String nameRootFolderB = "rootFolderB";
    protected long idUserRootFolderA = -1;
    protected long idUserRootFolderB = -1;
    protected GPUserFolders userTestRootFolderA = null;
    protected GPUserFolders userTestRootFolderB = null;
    //
    protected List<String> layerInfoKeywords;

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("ServiceTest - afterPropertiesSet-------------------------------> " + this.getClass().getName());

        Assert.assertNotNull("gpJettyServer is NULL", gpJettyServer);
        gpJettyServer.start();

        geoPlatformService = gpWSClient.create();
        Assert.assertNotNull("geoPlatformService is NULL", geoPlatformService);
    }

    @Before
    public void setUp() throws Exception {
        logger.trace("\n\t@@@ {}.setUp @@@", this.getClass().getSimpleName());

        // Insert User
        idUserTest = this.createAndInsertUser(usernameTest);
        userTest = geoPlatformService.getUserDetailByName(new SearchRequest(usernameTest));

        // Create root folders for the user
        idUserRootFolderA = this.createAndInsertFolder(userTest, nameRootFolderA, 2, false, null);
        userTestRootFolderA = geoPlatformService.getUserFolderByUserAndFolderId(idUserTest, idUserRootFolderA);

        idUserRootFolderB = this.createAndInsertFolder(userTest, nameRootFolderB, 1, false, null);
        userTestRootFolderB = geoPlatformService.getUserFolderByUserAndFolderId(idUserTest, idUserRootFolderB);

        // Set the list of keywords (for raster layer)
        layerInfoKeywords = new ArrayList<String>();
        layerInfoKeywords.add("keyword_test");
    }

    @After
    public void tearDown() {
        logger.trace("\n\t@@@ {}.tearDown @@@", this.getClass().getSimpleName());
        // Delete user
        this.deleteUser(idUserTest);
    }

    // Create and insert a User
    protected long createAndInsertUser(String username) throws IllegalParameterFault {
        GPUser user = createUser(username);
        logger.debug("\n*** GPUser to INSERT:\n{}\n***", user);
        long idUser = geoPlatformService.insertUser(user);
        logger.debug("\n*** Id ASSIGNED at the User in the DB: {} ***", idUser);
        Assert.assertTrue("Id ASSIGNED at the User in the DB", idUser > 0);
        return idUser;
    }

    private GPUser createUser(String username) {
        GPUser user = new GPUser();
        user.setUsername(username);
        user.setEmailAddress(username + "@test");
        user.setEnabled(true);
        // TODO FIX: Utility.md5("pwd_" + username)
        user.setPassword("918706bb28e76c3a5f3c7f0dd6f06ff0"); // clear password: 'pwd_username_test_ws'
        user.setSendEmail(true);
        return user;
    }

    // Delete (with assert) a User
    protected void deleteUser(long idUser) {
        try {
            boolean check = geoPlatformService.deleteUser(idUser);
            Assert.assertTrue("User with id = " + idUser + " has not been eliminated", check);
        } catch (Exception e) {
            Assert.fail("Error while deleting User with Id: " + idUser);
        }
    }

    // Delete (with assert) a Folder
    protected void deleteUserFolder(long idFolder) {
        try {
            boolean check = geoPlatformService.deleteUserFolder(idFolder);
            Assert.assertTrue("Folder with id = " + idFolder + " has not been eliminated", check);
        } catch (Exception e) {
            Assert.fail("Error while deleting Folder with Id: " + idFolder);
        }
    }

    protected long createAndInsertFolder(GPUser owner, String folderName,
            int position, boolean shared, GPUserFolders parent) throws IllegalParameterFault {
        
        GPFolder folder = this.createFolder(folderName, shared);
        GPUserFolders userFolder = this.createBindingUserFolder(owner, folder, position, parent);
        return geoPlatformService.insertUserFolder(userFolder);
    }
    
    protected GPUserFolders createUserFolder(GPUser owner, String folderName,
            int position, boolean shared, GPUserFolders parent) throws IllegalParameterFault {
        
        GPFolder folder = this.createFolder(folderName, shared);
        return this.createBindingUserFolder(owner, folder, position, parent);
    }

    protected GPFolder createFolder(String folderName, boolean shared) {
        GPFolder folder = new GPFolder(folderName);
        folder.setShared(shared);
        return folder;
    }

    protected GPUserFolders createBindingUserFolder(GPUser user, GPFolder folder,
            int position, GPUserFolders parent) {
        GPUserFolders userFolder = new GPUserFolders();
        userFolder.setUserAndFolder(user, folder);
        userFolder.setPosition(position);
        userFolder.setParent(parent);

//        folder.addUserFolder(userFolder);

        return userFolder;
    }

    protected long createAndInsertRasterLayer(GPUserFolders userFolder, String title, String name,
            String abstractText, int position, boolean shared, String srs, String urlServer) {
        GPRasterLayer rasterLayer = new GPRasterLayer();
        this.createLayer(rasterLayer, userFolder, title, name, abstractText, position, shared, srs, urlServer);

        GPLayerInfo layerInfo = new GPLayerInfo();
        layerInfo.setKeywords(layerInfoKeywords);
        layerInfo.setQueryable(false);
        rasterLayer.setLayerInfo(layerInfo);

        rasterLayer.setLayerType(GPLayerType.RASTER);
        return geoPlatformService.insertLayer(rasterLayer);
    }

    protected long createAndInsertVectorLayer(GPUserFolders userFolder, String title, String name,
            String abstractText, int position, boolean shared, String srs, String urlServer) {
        GPVectorLayer vectorLayer = new GPVectorLayer();
        this.createLayer(vectorLayer, userFolder, title, name, abstractText, position, shared, srs, urlServer);

        vectorLayer.setLayerType(GPLayerType.POLYGON);
        return geoPlatformService.insertLayer(vectorLayer);
    }

    protected void createLayer(GPLayer gpLayer, GPUserFolders userFolder, String name, String title,
            String abstractText, int position, boolean shared, String srs, String urlServer) {
        gpLayer.setUserFolders(userFolder);

        gpLayer.setTitle(title);
        gpLayer.setName(name);
        gpLayer.setAbstractText(abstractText);
        gpLayer.setPosition(position);
        gpLayer.setShared(shared);
        gpLayer.setSrs(srs);
        gpLayer.setUrlServer(urlServer);

        GPBBox bBox = new GPBBox(10, 10, 20, 20);
        gpLayer.setBbox(bBox);
    }
}
