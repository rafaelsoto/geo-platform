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
package org.geosdi.geoplatform.cxf;

import java.util.HashMap;
import java.util.Map;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.geosdi.geoplatform.services.GeoPlatformService;
import org.geosdi.geoplatform.services.Greeter;

/**
 *
 * @author Giuseppe La Scaleia - CNR IMAA geoSDI Group
 * @email  giuseppe.lascaleia@geosdi.org
 */
public class GeoPlatformWSServer {

    private String address = "http://localhost:8282/geoplatform-service/soap";

    public GeoPlatformService create() {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        
        factory.getInInterceptors().add(new LoggingInInterceptor());
        factory.getOutInterceptors().add(new LoggingOutInterceptor());
        
        Map<String, Object> outProps = new HashMap<String, Object>();
        
        // ----------- Only Encryption
//        outProps.put("action", "Encrypt");
//        outProps.put("encryptionPropFile", "Client_Encrypt.properties");
//        outProps.put("encryptionUser", "serverx509v1");
        
        // ----------- Only signature
        outProps.put("action", "Timestamp Signature");
        outProps.put("user", "serverx509v1");
        outProps.put("signaturePropFile", "Server_Decrypt.properties");
        
        // ----------- Signature and Encryption
//        outProps.put("action", "Timestamp Signature Encrypt");
//        outProps.put("user", "client");
//        outProps.put("signaturePropFile", "/client.properties");
//        outProps.put("encryptionPropFile", "/server.properties");
//        outProps.put("encryptionUser", "server");
        
//        outProps.put("signatureKeyIdentifier", "DirectReference");
        
        outProps.put("passwordCallbackClass", "org.geosdi.geoplatform.services.ServerKeystorePasswordCallback");
        
//        outProps.put("signatureParts", "{Element}{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd}Timestamp;{Element}{http://schemas.xmlsoap.org/soap/envelope/}Body");
//        outProps.put("encryptionParts", "{Element}{http://www.w3.org/2000/09/xmldsig#}Signature;{Content}{http://schemas.xmlsoap.org/soap/envelope/}Body");
//        outProps.put("encryptionParts", "{Content}{http://schemas.xmlsoap.org/soap/envelope/}Body");
//        outProps.put("encryptionSymAlgorithm", "http://www.w3.org/2001/04/xmlenc#tripledes-cbc");
        WSS4JOutInterceptor wssOut = new WSS4JOutInterceptor(outProps);
        factory.getOutInterceptors().add(wssOut);
        
        Map<String, Object> inProps = new HashMap<String, Object>();
        
        // ----------- Only Encryption
//        inProps.put("action", "Encrypt");
//        inProps.put("decryptionPropFile", "Client_Sign.properties");
        
//         ----------- Only signature
        inProps.put("action", "Timestamp Signature");
        inProps.put("signaturePropFile", "Server_SignVerf.properties");
//        
        // ----------- Signature and Encryption
//        Map<String, Object> inProps = new HashMap<String, Object>();
//        inProps.put("action", "Timestamp Signature Encrypt");
//        inProps.put("signaturePropFile", "/server.properties");
//        inProps.put("decryptionPropFile", "/client.properties");
        
        inProps.put("passwordCallbackClass", "org.geosdi.geoplatform.services.ServerKeystorePasswordCallback");
        
        WSS4JInInterceptor wssIn = new WSS4JInInterceptor(inProps);
        factory.getInInterceptors().add(wssIn);
        
        factory.setServiceClass(GeoPlatformService.class);

        factory.setAddress(this.address);

        return (GeoPlatformService) factory.create();
    }
    
//    public Greeter create() {
//        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
//        
//        factory.getInInterceptors().add(new LoggingInInterceptor());
//        factory.getOutInterceptors().add(new LoggingOutInterceptor());
//        
//        Map<String, Object> outProps = new HashMap<String, Object>();
//        
//        // ----------- Only Encryption
////        outProps.put("action", "Encrypt");
////        outProps.put("encryptionPropFile", "Client_Encrypt.properties");
////        outProps.put("encryptionUser", "serverx509v1");
//        
//        // ----------- Only signature
//        outProps.put("action", "Signature");
//        outProps.put("user", "clientx509v1");
//        outProps.put("signaturePropFile", "Client_Sign.properties");
//        
//        // ----------- Signature and Encryption
////        outProps.put("action", "Timestamp Signature Encrypt");
////        outProps.put("user", "client");
////        outProps.put("signaturePropFile", "/client.properties");
////        outProps.put("encryptionPropFile", "/server.properties");
////        outProps.put("encryptionUser", "server");
//        
////        outProps.put("signatureKeyIdentifier", "DirectReference");
//        
//        outProps.put("passwordCallbackClass", "org.geosdi.geoplatform.cxf.ClientKeystorePasswordCallback");
//        
////        outProps.put("signatureParts", "{Element}{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd}Timestamp;{Element}{http://schemas.xmlsoap.org/soap/envelope/}Body");
////        outProps.put("encryptionParts", "{Element}{http://www.w3.org/2000/09/xmldsig#}Signature;{Content}{http://schemas.xmlsoap.org/soap/envelope/}Body");
////        outProps.put("encryptionParts", "{Content}{http://schemas.xmlsoap.org/soap/envelope/}Body");
////        outProps.put("encryptionSymAlgorithm", "http://www.w3.org/2001/04/xmlenc#tripledes-cbc");
//        WSS4JOutInterceptor wssOut = new WSS4JOutInterceptor(outProps);
//        factory.getOutInterceptors().add(wssOut);
//        
//        Map<String, Object> inProps = new HashMap<String, Object>();
//        
//        // ----------- Only Encryption
////        inProps.put("action", "Encrypt");
////        inProps.put("decryptionPropFile", "Client_Sign.properties");
//        
////         ----------- Only signature
//        inProps.put("action", "Signature");
//        inProps.put("signaturePropFile", "Client_Encrypt.properties");
////        
//        // ----------- Signature and Encryption
////        Map<String, Object> inProps = new HashMap<String, Object>();
////        inProps.put("action", "Timestamp Signature Encrypt");
////        inProps.put("signaturePropFile", "/server.properties");
////        inProps.put("decryptionPropFile", "/client.properties");
//        
//        inProps.put("passwordCallbackClass", "org.geosdi.geoplatform.cxf.ClientKeystorePasswordCallback");
//        
//        WSS4JInInterceptor wssIn = new WSS4JInInterceptor(inProps);
//        factory.getInInterceptors().add(wssIn);
//        
//        factory.setServiceClass(Greeter.class);
//
//        factory.setAddress(this.address);
//
//        return (Greeter) factory.create();
//    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }
}