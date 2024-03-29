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
package org.geosdi.geoplatform.publish;


import org.slf4j.Logger;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Luca Paolino - geoSDI Group
 * @email luca.paolino@geosdi.org
 * this test try to publish two shapefiles. the first only in the preview workspace, the second firstly in the preview workspace and then into the data datastore of the preview2 workspace
 * In order to execute this test you should provide two shapefiles. The first should be in one ZIP compressed file while the second should be provided provifing its shp, prj, shx and dbf uncrompressed files.
 * You should also modify the paths to access them.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class PublisherWSTest extends TestCase {

    protected Logger logger = LoggerFactory.getLogger(org.geosdi.geoplatform.publish.PublisherWSTest.class);
    @Autowired
    private GPPublisherService gppublisherService;

    @Test
    public void testWS() {
        Assert.assertNotNull(gppublisherService);
//        try {
//            Attribute attribute1 = new Attribute("int", "1", "int");
//            Attribute attribute2 = new Attribute("String", "1", "String");
//            List<Attribute> attributeList1 = new ArrayList<Attribute>();
//            attributeList1.add(attribute1);
//            attributeList1.add(attribute2);
//            Feature feature = new Feature("POINT(1 1)", attributeList1);
//            List<Feature> featureList = new ArrayList<Feature>();
//            featureList.add(feature);
//            byte[] stream = gppublisherService.createSHP("luca", featureList, "feature.shp");
//            FileOutputStream outputStream = new FileOutputStream("C:\\Users\\Luca\\AppData\\Local\\Temp\\geoportal\\zip\\luca\\pippo.zip");
//            outputStream.write(stream);
//            outputStream.close();
//        } catch (Exception ex) {
//            logger.error("\n **** Generic exception" + ex.getMessage());
//        }
    }

    /*
    @Test
    public void testWS() {
    Assert.assertNotNull(gppublisherService);
    try {
    logger.info("\n **** START TEST");
    logger.info("\n **** CALL TO UPLOADSHAPEINPREVIEW ON prova.zip");
    File zipFile = new File("./src/test/resources/prova.zip");
    List<InfoPreview> infoList = gppublisherService.uploadZIPInPreview("luca",zipFile);
    for (InfoPreview info: infoList){
    logger.info("\n **** Preview at: "+info.getWorkspace()+":"+info.getDataStoreName()+" --> "+info.getMessage());
    logger.info("\n **** continue at: "+info.getCrs()+":"+info.getMinX()+","+info.getMinY());
    }
    /*            logger.info("\n **** CALL TO PUBLISH ON zip_it_aeropo ");
    logger.info("\n **** RESULT "+gppublisherService.publish("luca", "preview2", "data", "it_aeropo" ));
    logger.info("\n **** CALL TO REMOVEFROMPREVIEW zip_it_aree_meteoclimatiche ");
    
    File shpFile = new File("./src/test/resources/limiti_adb_4326.shp");
    File dbfFile = new File("./src/test/resources/limiti_adb_4326.dbf");
    File shxFile = new File("./src/test/resources/limiti_adb_4326.shx");
    File prjFile = new File("./src/test/resources/limiti_adb_4326.prj");
    List<InfoPreview> successfullPreview = null ;
    logger.info("\n **** CALL TO UPLOADSHAPEINPREVIEW ON Limiti_AdB_4326 files");
    successfullPreview = gppublisherService.uploadShapeInPreview("rosanna",shpFile, dbfFile, shxFile, prjFile, null);
    logger.info("\n **** RESULT: "+successfullPreview);
    if (successfullPreview!=null) {
    logger.info("\n **** CALL TO PUBLISH ON Limiti_AdB_4326 files");
    logger.info("\n **** RESULT "+gppublisherService.publish("rosanna","preview2", "data", "limiti_adb_4326"));
    }
    List<String> listNames = new ArrayList<String>();
    listNames.add("it_augustus_ccs");
    listNames.add("it_augustus_ccs_sede");
    logger.info("\n **** RESULT MULTIPLE PUBLISHING"+gppublisherService.publishAll("luca","preview2", "data", listNames));
    logger.info("\n **** CALL TO GETPREVIEWDATASTORES");
    List<InfoPreview> infoList2 = gppublisherService.getPreviewDataStores("luca");
    for (InfoPreview info: infoList2){
    logger.info("\n **** Preview at: "+info.getWorkspace()+":"+info.getDataStoreName()+" --> "+info.getMessage());
    }
    
    
    logger.info("\n **** END TEST");
    } catch (ResourceNotFoundFault ex) {
    logger.error("\n **** Eccezione nella pubblicazione: "+ex.getMessage());
    System.out.println("\n **** Eccezione nella pubblicazione: "+ex.getMessage());
    }
    catch (Exception ex) {
    logger.error("\n **** Generic exception"+ex.getMessage());
    ex.printStackTrace();
    
    }
    }
     */
}
