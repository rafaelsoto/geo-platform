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
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.geosdi.geoplatform.cxf.GeoPlatformWSClient;
import org.geosdi.geoplatform.services.GeoPlatformService;
import org.geosdi.geoplatform.services.ServerKeystorePasswordCallback;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

/**
 *
 * @author Giuseppe La Scaleia - CNR IMAA geoSDI Group
 * @email  giuseppe.lascaleia@geosdi.org
 */
public class WSListenerServices implements TestExecutionListener {
    
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    //
    protected Endpoint e = null;
    protected Bus bus = null;
    
    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        System.out.println("@@@@ ------------- beforeTestClass");
        GeoPlatformService geoPlatformService = (GeoPlatformService)testContext.getApplicationContext().getBean("geoPlatformService");
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
        System.out.println(" @@@ OID server: " + geoPlatformService.toString());
    }

    @Override
    public void prepareTestInstance(TestContext testContext) throws Exception {
    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
    }

    @Override
    public void afterTestClass(TestContext testContext) throws Exception {
        System.out.println("@@@@ ------------- afterTestClass");
    }
}
