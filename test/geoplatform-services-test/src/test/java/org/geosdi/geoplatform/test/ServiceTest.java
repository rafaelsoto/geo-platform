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
package org.geosdi.geoplatform.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.ws.Endpoint;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.geosdi.geoplatform.exception.IllegalParameterFault;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
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
import org.geosdi.geoplatform.request.SearchRequest;
import org.geosdi.geoplatform.services.GeoPlatformService;
import org.geosdi.geoplatform.services.ServerKeystorePasswordCallback;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.test.context.TestExecutionListeners;

/**
 * @author Francesco Izzi - CNR IMAA - geoSDI
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:applicationContext.xml"})
//@TestExecutionListeners(value = {WSListenerServices.class})
public abstract class ServiceTest implements InitializingBean {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    //
    @Autowired
    protected GeoPlatformWSClient gpWSClient;
    //
    @Autowired
    protected GeoPlatformService geoPlatformService;
    //
    protected GeoPlatformService geoplatformServiceClient;
    //
    Bus bus = null;
//    @Autowired
//    protected Server gpJettyServer;
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

//        Assert.assertNotNull("gpJettyServer is NULL", gpJettyServer);
//        gpJettyServer.start();
        
        Assert.assertNotNull("gpWSClient is NULL", gpWSClient);
//        geoplatformServiceClient = gpWSClient.create();
        geoplatformServiceClient = gpWSClient.getGeoPLatformService();
        
        Object implementor = geoPlatformService;
        SpringBusFactory bf = new SpringBusFactory();
        bus = bf.createBus();
        
        bus.getInInterceptors().add(new LoggingInInterceptor());
        bus.getOutInterceptors().add(new LoggingOutInterceptor());
        
        Map<String, Object> outProps = new HashMap<String, Object>();
        outProps.put("action", "Timestamp Signature");
        outProps.put("user", "serverx509v1");
        outProps.put("passwordCallbackClass", ServerKeystorePasswordCallback.class.getName());
        outProps.put("signaturePropFile", "./Server_Decrypt.properties");
        bus.getOutInterceptors().add(new WSS4JOutInterceptor(outProps));
        Map<String, Object> inProps = new HashMap<String, Object>();
        inProps.put("action", "Timestamp Signature");
        inProps.put("passwordCallbackClass", ServerKeystorePasswordCallback.class.getName());
        inProps.put("signaturePropFile", "./Server_SignVerf.properties");
        bus.getInInterceptors().add(new WSS4JInInterceptor(inProps));
        
        bf.setDefaultBus(bus);
        String address = "http://localhost:8282/geoplatform-service/soap";
        Endpoint e = Endpoint.publish(address, implementor);
        
        logger.debug("Server ready...");
    }
    
    @BeforeClass
    public static void setUpClass() throws Exception {
    }
    
    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        logger.trace("\n\t@@@ {}.setUp @@@", this.getClass().getSimpleName());

        // Insert User
        idUserTest = this.createAndInsertUser(usernameTest);
        userTest = geoplatformServiceClient.getUserDetailByName(new SearchRequest(usernameTest));

        // Create root folders for the user
        idUserRootFolderA = this.createAndInsertFolder(userTest, nameRootFolderA, 2, false, null);
        userTestRootFolderA = geoplatformServiceClient.getUserFolderByUserAndFolderId(idUserTest, idUserRootFolderA);

        idUserRootFolderB = this.createAndInsertFolder(userTest, nameRootFolderB, 1, false, null);
        userTestRootFolderB = geoplatformServiceClient.getUserFolderByUserAndFolderId(idUserTest, idUserRootFolderB);

        // Set the list of keywords (for raster layer)
        layerInfoKeywords = new ArrayList<String>();
        layerInfoKeywords.add("keyword_test");
    }

    @After
    public void tearDown() {
        logger.trace("\n\t@@@ {}.tearDown @@@", this.getClass().getSimpleName());
        // Delete user
        this.deleteUser(idUserTest);
//        bus.shutdown(true);
    }

    // Create and insert a User
    protected long createAndInsertUser(String username) throws IllegalParameterFault {
        GPUser user = createUser(username);
        logger.debug("\n*** GPUser to INSERT:\n{}\n***", user);
        long idUser = geoplatformServiceClient.insertUser(user);
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
            boolean check = geoplatformServiceClient.deleteUser(idUser);
            Assert.assertTrue("User with id = " + idUser + " has not been eliminated", check);
        } catch (Exception e) {
            Assert.fail("Error while deleting User with Id: " + idUser);
        }
    }

    // Delete (with assert) a Folder
    protected void deleteUserFolder(long idFolder) {
        try {
            boolean check = geoplatformServiceClient.deleteUserFolder(idFolder);
            Assert.assertTrue("Folder with id = " + idFolder + " has not been eliminated", check);
        } catch (Exception e) {
            Assert.fail("Error while deleting Folder with Id: " + idFolder);
        }
    }

    protected long createAndInsertFolder(GPUser owner, String folderName,
            int position, boolean shared, GPUserFolders parent) throws IllegalParameterFault {

        GPFolder folder = this.createFolder(folderName, shared);
        GPUserFolders userFolder = this.createBindingUserFolder(owner, folder, position, parent);
        return geoplatformServiceClient.insertUserFolder(userFolder);
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
            String abstractText, int position, boolean shared, String srs, String urlServer)
            throws IllegalParameterFault {
        GPRasterLayer rasterLayer = new GPRasterLayer();
        this.createLayer(rasterLayer, userFolder, title, name, abstractText, position, shared, srs, urlServer);

        GPLayerInfo layerInfo = new GPLayerInfo();
        layerInfo.setKeywords(layerInfoKeywords);
        layerInfo.setQueryable(false);
        rasterLayer.setLayerInfo(layerInfo);

        rasterLayer.setLayerType(GPLayerType.RASTER);
        return geoplatformServiceClient.insertLayer(rasterLayer);
    }

    protected long createAndInsertVectorLayer(GPUserFolders userFolder, String title, String name,
            String abstractText, int position, boolean shared, String srs, String urlServer)
            throws IllegalParameterFault {
        GPVectorLayer vectorLayer = new GPVectorLayer();
        this.createLayer(vectorLayer, userFolder, title, name, abstractText, position, shared, srs, urlServer);

        vectorLayer.setLayerType(GPLayerType.POLYGON);
        return geoplatformServiceClient.insertLayer(vectorLayer);
    }

    protected void createLayer(GPLayer gpLayer, GPUserFolders userFolder, String title, String name,
            String abstractText, int position, boolean shared, String srs, String urlServer) {
        gpLayer.setUserFolder(userFolder);

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
