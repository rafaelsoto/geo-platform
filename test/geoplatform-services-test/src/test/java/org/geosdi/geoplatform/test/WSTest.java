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
import java.util.Map;
import javax.xml.ws.Endpoint;
import junit.framework.Assert;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.geosdi.geoplatform.cxf.GeoPlatformWSClient;
import org.geosdi.geoplatform.services.Greeter;
import org.geosdi.geoplatform.services.GreeterServiceImpl;
import org.geosdi.geoplatform.services.ServerKeystorePasswordCallback;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Michele Santomauro - CNR IMAA - geoSDI
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class WSTest implements InitializingBean {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    //
    @Autowired
    protected GeoPlatformWSClient gpWSClient;
    //
    protected Greeter greeter;
    
//    @Autowired
//    protected Server gpJettyServer;

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("WSTest - afterPropertiesSet-------------------------------> " + this.getClass().getName());
        
////        gpJettyServer.start();
//        
//        greeter = gpWSClient.create();
    }

    @Test
    public void testUpdateServer() {
        Object implementor = new GreeterServiceImpl();
        
        SpringBusFactory bf = new SpringBusFactory();
        Bus bus = bf.createBus();
        
        Map<String, Object> outProps = new HashMap<String, Object>();
        outProps.put("action", "Signature");
        outProps.put("user", "serverx509v1");
        outProps.put("passwordCallbackClass", ServerKeystorePasswordCallback.class.getName());
        outProps.put("signaturePropFile", "./Server_Decrypt.properties");
        bus.getOutInterceptors().add(new WSS4JOutInterceptor(outProps));
        Map<String, Object> inProps = new HashMap<String, Object>();
        inProps.put("action", "Signature");
        inProps.put("passwordCallbackClass", ServerKeystorePasswordCallback.class.getName());
        inProps.put("signaturePropFile", "./Server_SignVerf.properties");
        bus.getInInterceptors().add(new WSS4JInInterceptor(inProps));
        
        bf.setDefaultBus(bus);
        String address = "http://localhost:8282/geoplatform-service/greeter";
        Endpoint e = Endpoint.publish(address, implementor);
        
        System.out.println("Server ready...");
//        Thread.sleep(5 * 60 * 1000);
        
        greeter.greetMe("Anne");
        Assert.assertTrue(true);
        
        bus.shutdown(true);
    }
}
