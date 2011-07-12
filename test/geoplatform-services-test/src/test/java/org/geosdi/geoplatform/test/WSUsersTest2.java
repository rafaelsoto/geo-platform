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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.ws.Endpoint;
import junit.framework.Assert;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.geosdi.geoplatform.cxf.GeoPlatformWSClient;
import org.junit.Test;

import org.geosdi.geoplatform.responce.UserDTO;
import org.geosdi.geoplatform.services.GeoPlatformService;
import org.geosdi.geoplatform.services.ServerKeystorePasswordCallback;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author Giuseppe La Scaleia - CNR IMAA geoSDI Group
 * @email  giuseppe.lascaleia@geosdi.org
 * @author Vincenzo Monteverde
 * @email vincenzo.monteverde@geosdi.org - OpenPGP key ID 0xB25F4B38
 */
@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
@ContextConfiguration(locations = {"classpath*:applicationContext.xml"})
//@TestExecutionListeners(value = {WSListenerServices.class})
public class WSUsersTest2 {
//public class WSUsersTest2 implements InitializingBean {
    // TODO check:
    //      searchUsers()
    //      updateUser()

    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    //
//    @Autowired
//    protected static GeoPlatformWSClient gpWSClient;
    //
//    @Resource
    @Autowired
    private GeoPlatformService geoPlatformService;
    //
//    @Autowired
    protected GeoPlatformService geoplatformServiceClient = GeoPlatformWSClient.getInstance().getGeoPLatformService();
    //
    protected Endpoint e = null;
    protected Bus bus = null;
    
//    @BeforeClass
//    public static void initialize() throws Exception {
//        System.out.println("\n\t@@@ {}.setUp @@@" + WSUsersTest2.class.getSimpleName());
//        System.out.println(" @@@ OID: " + WSUsersTest2.class.toString());
//
////        Assert.assertNotNull("gpWSClient is NULL", gpWSClient);
////        geoplatformServiceClient = gpWSClient.create();
//        Assert.assertNotNull("geoPlatformService is NULL", geoPlatformService);
//
//        Object implementor = geoPlatformService;
//        SpringBusFactory bf = new SpringBusFactory();
//        bus = bf.createBus();
//
//        Map<String, Object> outProps = new HashMap<String, Object>();
//        outProps.put("action", "Timestamp Signature");
//        outProps.put("user", "serverx509v1");
//        outProps.put("passwordCallbackClass", ServerKeystorePasswordCallback.class.getName());
//        outProps.put("signaturePropFile", "./Server_Decrypt.properties");
//        bus.getOutInterceptors().add(new WSS4JOutInterceptor(outProps));
//        Map<String, Object> inProps = new HashMap<String, Object>();
//        inProps.put("action", "Timestamp Signature");
//        inProps.put("passwordCallbackClass", ServerKeystorePasswordCallback.class.getName());
//        inProps.put("signaturePropFile", "./Server_SignVerf.properties");
//        bus.getInInterceptors().add(new WSS4JInInterceptor(inProps));
//
//        bf.setDefaultBus(bus);
//        String address = "http://localhost:8282/geoplatform-service/soap";
//        e = Endpoint.publish(address, implementor);
//
//        System.out.println("Server ready...");
//    }

    @Before
    public void setUp() throws Exception {
        logger.trace("\n\t@@@ {}.setUp @@@", this.getClass().getSimpleName());
        System.out.println(" @@@ OID: " + this.toString());

//        Assert.assertNotNull("gpWSClient is NULL", gpWSClient);
//        geoplatformServiceClient = gpWSClient.create();
        Assert.assertNotNull("geoPlatformService is NULL", geoPlatformService);

        Object implementor = geoPlatformService;
        SpringBusFactory bf = new SpringBusFactory();
        bus = bf.createBus();

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
        e = Endpoint.publish(address, implementor);

        System.out.println("Server ready...");
//        Thread.sleep(5 * 60 * 1000);
    }

    @After
    public void tearDown() {
        logger.trace("\n\t@@@ {}.tearDown @@@", this.getClass().getSimpleName());
        System.out.println(" @@@ OID: " + this.toString());

//        e.stop();
//        bus.shutdown(true);
    }

//    @Autowired
//    protected Server gpJettyServer;
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        logger.info("WSTest - afterPropertiesSet-------------------------------> " + this.getClass().getName());
//        
//    }
    @Test
    public void testUpdateServer1() {
        logger.info("@@@@ Before");
        System.out.println(" @@@ OID: " + this.toString());
//        System.out.println(" @@@ OID server: " + geoPlatformService.toString());
        System.out.println(" @@@ OID client: " + geoplatformServiceClient.toString());
        Assert.assertNotNull("geoplatformServiceClient is NULL", geoplatformServiceClient);
        List<UserDTO> userList = geoplatformServiceClient.getUsers();
        Assert.assertNotNull("userList is NULL", userList);
        logger.info("\n*** Number of Users into DB: {} ***", userList.size());
        if (userList != null) {
            for (Iterator<UserDTO> it = userList.iterator(); it.hasNext();) {
                logger.info("\n*** USER into DB:\n{}\n***", it.next());

            }
        }
    }

    @Test
    public void testUpdateServer2() {
        logger.info("@@@@ Before");
        System.out.println(" @@@ OID: " + this.toString());
//        System.out.println(" @@@ OID server: " + geoPlatformService.toString());
        System.out.println(" @@@ OID client: " + geoplatformServiceClient.toString());
        Assert.assertNotNull("geoplatformServiceClient is NULL", geoplatformServiceClient);
        List<UserDTO> userList = geoplatformServiceClient.getUsers();
        logger.info("\n*** Number of Users into DB: {} ***", userList.size());
        if (userList != null) {
            for (Iterator<UserDTO> it = userList.iterator(); it.hasNext();) {
                logger.info("\n*** USER into DB:\n{}\n***", it.next());

            }
        }
    }
}
