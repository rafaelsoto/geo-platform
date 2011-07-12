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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import org.geosdi.geoplatform.core.model.GPLayer;
import org.geosdi.geoplatform.core.model.GPLayerInfo;
import org.geosdi.geoplatform.core.model.GPLayerType;
import org.geosdi.geoplatform.core.model.GPRasterLayer;
import org.geosdi.geoplatform.core.model.GPVectorLayer;
import org.geosdi.geoplatform.exception.IllegalParameterFault;
import org.geosdi.geoplatform.exception.ResourceNotFoundFault;
import org.geosdi.geoplatform.responce.FolderDTO;
import org.geosdi.geoplatform.responce.ShortLayerDTO;
import org.geosdi.geoplatform.responce.collection.GPWebServiceMapData;
import org.geosdi.geoplatform.responce.collection.TreeFolderElements;

/**
 *
 * @author Giuseppe La Scaleia - CNR IMAA geoSDI Group
 * @email  giuseppe.lascaleia@geosdi.org
 */
public class WSLayerTest extends ServiceTest {

    private final String urlServer = "http://www.geosdi.org/test";
    private final String newUrlServer = "http://www.geosdi.org/newtest";
    private final String spatialReferenceSystem = "Geographic coordinate system";
    // Raster Layer 1
    private final String titleRaster1 = "raster_1";
    private GPRasterLayer raster1 = null;
    private long idRaster1 = -1;
    // Vector Layer 1
    private final String titleVector1 = "vector_1";
    private GPVectorLayer vector1 = null;
    private long idVector1 = -1;
    // Raster Layer 2
    private final String titleRaster2 = "raster_2";
    private GPRasterLayer raster2 = null;
    private long idRaster2 = -1;
    // Vector Layer 2
    private final String titleVector2 = "vector_2";
    private GPVectorLayer vector2 = null;
    private long idVector2 = -1;
    // Raster Layer 3
    private final String titleRaster3 = "raster_3";
    // Vector Layer 3
    private final String titleVector3 = "vector_3";

    @Before
    // "position" will be set without application logic, but only to have different values
    public void setUp() throws Exception {
        super.setUp();

        // "userTestRootFolderA" ---> "rasterLayer1"
        idRaster1 = createAndInsertRasterLayer(userTestRootFolderA, titleRaster1, "name_" + titleRaster1,
                "abstract_" + titleRaster1, 5, false, spatialReferenceSystem, urlServer);
        raster1 = geoPlatformService.getRasterLayer(idRaster1);
        // "userTestRootFolderA" ---> "vectorLayer1"
        idVector1 = createAndInsertVectorLayer(userTestRootFolderA, titleVector1, "name_" + titleVector1,
                "abstract_" + titleVector1, 4, false, spatialReferenceSystem, urlServer);
        vector1 = geoPlatformService.getVectorLayer(idVector1);
        //
        userTestRootFolderA.setPosition(6);
        userTestRootFolderA.getFolder().setNumberOfDescendants(2);
        geoPlatformService.updateUserFolder(userTestRootFolderA);

        // "userTestRootFolderB" ---> "rasterLayer2"
        idRaster2 = createAndInsertRasterLayer(userTestRootFolderB, titleRaster2, "name_" + titleRaster2,
                "abstract_" + titleRaster2, 2, false, spatialReferenceSystem, urlServer);
        raster2 = geoPlatformService.getRasterLayer(idRaster2);
        // "userTestRootFolderB" ---> "vectorLayer2"
        idVector2 = createAndInsertVectorLayer(userTestRootFolderB, titleVector2, "name_" + titleVector2,
                "abstract_" + titleVector2, 1, false, spatialReferenceSystem, urlServer);
        vector2 = geoPlatformService.getVectorLayer(idVector2);
        //
        userTestRootFolderB.setPosition(3);
        userTestRootFolderB.getFolder().setNumberOfDescendants(2);
        geoPlatformService.updateUserFolder(userTestRootFolderB);
    }

    @Test
    public void testAddLayers() {
        try {
            List<Long> idList = this.addLayer3();

            userTestRootFolderA = geoPlatformService.getUserFolderDetail(idUserRootFolderA);
            Assert.assertEquals("position of userTestRootFolderA", 8, userTestRootFolderA.getPosition());
            Assert.assertEquals("descendants of userTestRootFolderA", 4, userTestRootFolderA.getFolder().getNumberOfDescendants());

            GPLayer newRasterLayer3 = geoPlatformService.getRasterLayer(idList.get(0));
            Assert.assertEquals("title of newRasterLayer3", titleRaster3, newRasterLayer3.getTitle());
            Assert.assertEquals("position of newRasterLayer3", 7, newRasterLayer3.getPosition());

            GPLayer newVectorLayer3 = geoPlatformService.getVectorLayer(idList.get(1));
            Assert.assertEquals("title of newVectorLayer3", titleVector3, newVectorLayer3.getTitle());
            Assert.assertEquals("position of newVectorLayer3", 6, newVectorLayer3.getPosition());

            GPLayer newRasterLayer1 = geoPlatformService.getRasterLayer(raster1.getId());
            Assert.assertEquals("title of newRasterLayer1", titleRaster1, newRasterLayer1.getTitle());
            Assert.assertEquals("position of newRasterLayer1", 5, newRasterLayer1.getPosition());

            GPLayer newVectorLayer1 = geoPlatformService.getVectorLayer(vector1.getId());
            Assert.assertEquals("title of newVectorLayer1", titleVector1, newVectorLayer1.getTitle());
            Assert.assertEquals("position of newVectorLayer1", 4, newVectorLayer1.getPosition());

            userTestRootFolderB = geoPlatformService.getUserFolderDetail(idUserRootFolderB);
            Assert.assertEquals("position of userTestRootFolderB", 3, userTestRootFolderB.getPosition());
            Assert.assertEquals("descendants of userTestRootFolderB", 2, userTestRootFolderB.getFolder().getNumberOfDescendants());

            GPLayer newRasterLayer2 = geoPlatformService.getRasterLayer(raster2.getId());
            Assert.assertEquals("title of newRasterLayer1", titleRaster2, newRasterLayer2.getTitle());
            Assert.assertEquals("position of newRasterLayer1", 2, newRasterLayer2.getPosition());

            GPLayer newVectorLayer2 = geoPlatformService.getVectorLayer(vector2.getId());
            Assert.assertEquals("title of newVectorLayer1", titleVector2, newVectorLayer2.getTitle());
            Assert.assertEquals("position of newVectorLayer1", 1, newVectorLayer2.getPosition());
        } catch (IllegalParameterFault ipf) {
            Assert.fail("Layer has an Illegal Parameter");
        } catch (ResourceNotFoundFault rnnf) {
            Assert.fail("Layer with ID \"" + rnnf.getId() + "\"has a resource not found");
        }
    }

    @Test
    public void testGetLayer() {
        try {
            ShortLayerDTO shortRasterLayer1 = geoPlatformService.getShortLayer(idRaster1);
            Assert.assertNotNull("assertNotNull shortRasterLayer1", shortRasterLayer1);
            Assert.assertEquals("assertEquals shortRasterLayer1.getTitle()", shortRasterLayer1.getTitle(), titleRaster1);
            Assert.assertEquals("assertEquals shortRasterLayer1.getName()", shortRasterLayer1.getName(), "name_" + titleRaster1);
            Assert.assertEquals("assertEquals shortRasterLayer1.getPosition()", shortRasterLayer1.getPosition(), 5);
            Assert.assertEquals("assertEquals shortRasterLayer1.getSrs()", shortRasterLayer1.getSrs(), spatialReferenceSystem);
            Assert.assertEquals("assertEquals shortRasterLayer1.getUrlServer()", shortRasterLayer1.getUrlServer(), urlServer);
            Assert.assertEquals("assertEquals shortRasterLayer1.getLayerType()", shortRasterLayer1.getLayerType(), GPLayerType.RASTER);

            ShortLayerDTO shortVectorLayer1 = geoPlatformService.getShortLayer(idVector1);
            Assert.assertNotNull("assertNotNull shortVectorLayer1", shortVectorLayer1);
            Assert.assertEquals("assertEquals shortVectorLayer1.getTitle()", shortVectorLayer1.getTitle(), titleVector1);
            Assert.assertEquals("assertEquals shortVectorLayer1.getName()", shortVectorLayer1.getName(), "name_" + titleVector1);
            Assert.assertEquals("assertEquals shortVectorLayer1.getPosition()", shortVectorLayer1.getPosition(), 4);
            Assert.assertEquals("assertEquals shortVectorLayer1.getSrs()", shortVectorLayer1.getSrs(), spatialReferenceSystem);
            Assert.assertEquals("assertEquals shortVectorLayer1.getUrlServer()", shortVectorLayer1.getUrlServer(), urlServer);
            Assert.assertEquals("assertEquals shortVectorLayer1.getLayerType()", shortVectorLayer1.getLayerType(), GPLayerType.POLYGON);
        } catch (ResourceNotFoundFault rnnf) {
            Assert.fail("Layer with ID \"" + rnnf.getId() + "\" has a resource not found");
        }
    }

    @Test
    public void testUpdateRasterLayer() {
        final String titleLayerUpdated = "rasterLayerUpdated";
        try {
            raster1.setTitle(titleLayerUpdated);

            geoPlatformService.updateRasterLayer(raster1);
            ShortLayerDTO layerUpdated = geoPlatformService.getShortLayer(idRaster1);

            Assert.assertNotNull("assertNotNull layerUpdated", layerUpdated);
            Assert.assertEquals("assertEquals layerUpdated.getTitle()", layerUpdated.getTitle(), titleLayerUpdated);
        } catch (IllegalParameterFault ipf) {
            Assert.fail("Layer has an Illegal Parameter");
        } catch (ResourceNotFoundFault rnnf) {
            Assert.fail("Layer with id \"" + rnnf.getId() + "\" was NOT found");
        }
    }

    @Test
    public void testUpdateVectorLayer() {
        Assert.assertTrue(true);
        // TODO build testUpdateVectorLayer
    }

    @Test
    public void testDeleteLayer() {
        try {
            // Assert total number of folders stored into DB before delete            
            List<ShortLayerDTO> allLayersBeforeDelete = geoPlatformService.getLayers();
            int totalLayers = allLayersBeforeDelete.size();
            Assert.assertTrue("assertEquals totalLayers", totalLayers >= 4); // SetUp() added 4 layers

            // Delete "rasterLayer1" from "userTestRootFolderA"
            boolean erased = geoPlatformService.deleteLayer(idRaster1);
            Assert.assertTrue("Deletion of the layer rasterLayer1", erased);

            // Get root folders for user
            List<FolderDTO> folderList = geoPlatformService.getUserFoldersByUserId(idUserTest);

            // Assert on the structure of user's folders
            Assert.assertEquals("assertEquals folderList.getList().size()", folderList.size(), 2);
            // Assert on the structure of "userTestRootFolderA"
            TreeFolderElements childrenRootFolderA = geoPlatformService.getChildrenElements(idUserRootFolderA);
            logger.debug("\n*** childrenRootFolderA:\n{}\n***", childrenRootFolderA);
            Assert.assertNotNull("assertNotNull childrenRootFolderA", childrenRootFolderA);
            Assert.assertEquals("assertEquals childrenRootFolderA.size()", childrenRootFolderA.size(), 1);
            // Assert on layers of "userTestRootFolderA"
            ShortLayerDTO shortVectorLayerRootFolderA = (ShortLayerDTO) childrenRootFolderA.iterator().next();
            Assert.assertEquals("assertEquals shortVectorLayerRootFolderA.getTitle()", shortVectorLayerRootFolderA.getTitle(), titleVector1);
            // Assert on the structure of "userTestRootFolderB"
            TreeFolderElements childrenRootFolderB = geoPlatformService.getChildrenElements(idUserRootFolderB);
            logger.debug("\n*** childrenRootFolderB:\n{}\n***", childrenRootFolderB);
            Assert.assertNotNull("assertNotNull childrenRootFolderB", childrenRootFolderB);
            Assert.assertEquals("assertEquals childrenRootFolderB.size()", childrenRootFolderB.size(), 2);
            // Assert on layers of "userTestRootFolderB"
            Iterator iterator = childrenRootFolderB.iterator();
            ShortLayerDTO shortRasterLayerRootFolderB = (ShortLayerDTO) iterator.next();
            Assert.assertEquals("assertEquals shortRasterLayerRootFolderB.getTitle()", shortRasterLayerRootFolderB.getTitle(), titleRaster2);
            ShortLayerDTO shortVectorLayerRootFolderB = (ShortLayerDTO) iterator.next();
            Assert.assertEquals("assertEquals shortVectorLayerRootFolderB.getTitle()", shortVectorLayerRootFolderB.getTitle(), titleVector2);

            // Assert total number of layers stored into DB after delete
            List<ShortLayerDTO> allLayersAfterDelete = geoPlatformService.getLayers();
            Assert.assertEquals("assertEquals allLayersAfterDelete.getList().size()", allLayersAfterDelete.size(), totalLayers - 1);
        } catch (IllegalParameterFault ipf) {
            Assert.fail("Folder has an Illegal Parameter");
        } catch (ResourceNotFoundFault rnff) {
            Assert.fail("Folder with id \"" + rnff.getId() + "\" was NOT found");
        } catch (Exception e) {
            Assert.fail("Exception: " + e.getClass());
        }
        // Check ON DELETE CASCADE of the subforders of "userTestRootFolderB"
        checkLayerDeleted(idRaster1);
    }

    @Test
    public void testSaveAndDeleteLayerAndTreeModifications() {
        GPRasterLayer layerToTest = null;
        Map<Long, Integer> map = new HashMap<Long, Integer>();
        GPWebServiceMapData descendantsMapData = new GPWebServiceMapData();
        descendantsMapData.setDescendantsMap(map);
        try {
            String titleLayerToTest = "layerToTest";
            layerToTest = new GPRasterLayer();
            super.createLayer(layerToTest, userTestRootFolderB, titleLayerToTest, "name_" + titleLayerToTest,
                    "abstract_" + titleLayerToTest, 3, false, spatialReferenceSystem, urlServer);

            GPLayerInfo layerInfo = new GPLayerInfo();
            layerInfo.setKeywords(layerInfoKeywords);
            layerInfo.setQueryable(false);
            layerToTest.setLayerInfo(layerInfo);

            // Adding new layer to user's root folder B
            map.put(idUserRootFolderB, 3);

            // Adding new layer to user's root folder B
            long idLayerToTest = geoPlatformService.saveAddedLayerAndTreeModifications(
                    super.usernameTest, layerToTest, descendantsMapData);

            this.checkState(new int[]{7, 6, 5, 4, 2, 1}, new int[]{2, 3}, "before removing");

            // Removing layer from user's root
            map.clear();
            map.put(idUserRootFolderB, 2);
            geoPlatformService.saveDeletedLayerAndTreeModifications(idLayerToTest, descendantsMapData);

            this.checkInitialState("after removing");

        } catch (IllegalParameterFault ipf) {
            Assert.fail("Folder with id \"" + layerToTest.getId() + "\" was not found");
        } catch (ResourceNotFoundFault rnnf) {
            Assert.fail("Folder was not found " + rnnf.getId());
        }
    }

    @Test
    public void testDragAndDropLayerOnSameParent() {
        logger.trace("\n\t@@@ testDragAndDropLayerOnSameParent @@@");
        Map<Long, Integer> map = new HashMap<Long, Integer>();
        GPWebServiceMapData descendantsMapData = new GPWebServiceMapData();
        descendantsMapData.setDescendantsMap(map);
        try {
            // Move vector 2 before raster 2 (oldPosition < new Position)
            boolean checkDD = geoPlatformService.saveDragAndDropLayerAndTreeModifications(
                    super.usernameTest, idVector2, idUserRootFolderB, 2, descendantsMapData);
            Assert.assertTrue("Drag and Drop successful", checkDD);

            this.checkState(new int[]{6, 5, 4, 3, 1, 2}, new int[]{2, 2}, "after DD I on same parent");

            // Move vector 2 after raster 2, in initial position (oldPosition > new Position)
            checkDD = geoPlatformService.saveDragAndDropLayerAndTreeModifications(
                    super.usernameTest, idVector2, idUserRootFolderB, 1, descendantsMapData);
            Assert.assertTrue("Vector 2 doesn't moved to position 1", checkDD);

            this.checkInitialState("after DD II on same parent");

        } catch (ResourceNotFoundFault rnnf) {
            Assert.fail("Folder or Layer with ID \"" + rnnf.getId() + "\" was not found");
        }
    }

    @Test
    public void testDragAndDropLayerOnDifferentFolder() {
        logger.trace("\n\t@@@ testDragAndDropLayerOnDifferentFolder @@@");
        Map<Long, Integer> map = new HashMap<Long, Integer>();
        GPWebServiceMapData descendantsMapData = new GPWebServiceMapData();
        descendantsMapData.setDescendantsMap(map);
        try {
            map.put(idUserRootFolderA, 3);
            map.put(idUserRootFolderB, 1);
            // Move vector 2 before vector 1 (oldPosition < new Position)
            boolean checkDD = geoPlatformService.saveDragAndDropLayerAndTreeModifications(
                    super.usernameTest, idVector2, idUserRootFolderA, 4, descendantsMapData);
            Assert.assertTrue("Drag and Drop successful", checkDD);

            this.checkState(new int[]{6, 5, 3, 2, 1, 4}, new int[]{3, 1}, "after DD I on different parent");
            Assert.assertEquals("Parent of vector layer 2 after DD I on different parent", idUserRootFolderA, vector2.getUserFolder().getId());

            map.clear();
            map.put(idUserRootFolderA, 2);
            map.put(idUserRootFolderB, 2);
            // Move vector 2 after raster 2, in initial position (oldPosition > new Position)
            checkDD = geoPlatformService.saveDragAndDropLayerAndTreeModifications(
                    super.usernameTest, idVector2, idUserRootFolderB, 1, descendantsMapData);
            Assert.assertTrue("Vector 2 doesn't moved to position 1", checkDD);

            this.checkInitialState("after DD II on different parent");

        } catch (ResourceNotFoundFault rnnf) {
            Assert.fail("Folder or Layer with ID \"" + rnnf.getId() + "\" was not found");
        }
    }

    @Test
    public void testTransactionOnAddLayer() throws IllegalParameterFault, ResourceNotFoundFault {
        logger.trace("\n\t@@@ testTransactionOnAddLayer @@@");
        Map<Long, Integer> map = new HashMap<Long, Integer>();
        GPWebServiceMapData descendantsMapData = new GPWebServiceMapData();
        descendantsMapData.setDescendantsMap(map);
        map.put(idUserRootFolderA, 3);

        try {
            GPRasterLayer raster = new GPRasterLayer();
            super.createLayer(raster, userTestRootFolderA, null, "", "",
                    5, false, spatialReferenceSystem, urlServer);
            geoPlatformService.saveAddedLayerAndTreeModifications(usernameTest, raster, descendantsMapData);
            Assert.fail("Add layer must fail because title value is null");
        } catch (Exception e) {
            checkInitialState("transaction test");
        }
    }

    @Test
    public void testGetLayerInfo() {
        try {
            GPLayerInfo layerInfo = geoPlatformService.getLayerInfo(idRaster2);
            Assert.assertNotNull("assertNotNull layerInfo", layerInfo);
            Assert.assertEquals("assertEquals layerInfo.isQueryable()", false, layerInfo.isQueryable());
            List<String> keywords = layerInfo.getKeywords();
            Assert.assertNotNull("assertNotNull keywords of layerInfo", keywords);
            Assert.assertEquals("assertEquals layerInfo.getKeywords()", layerInfoKeywords.size(), keywords.size());
            for (int i = 0; i < keywords.size(); i++) {
                String key = keywords.get(i);
                Assert.assertEquals("assert keyword: index = " + i, layerInfoKeywords.get(i), key);
            }
        } catch (ResourceNotFoundFault rnnf) {
            Assert.fail("Layer with id \"" + rnnf.getId() + "\" was NOT found");
        }
    }

    @Test
    public void testGetLayersDataSourceByOwner() {
        try {
            this.addLayer3();

            List<String> list = geoPlatformService.getLayersDataSourceByOwner(usernameTest);

            Assert.assertEquals("Number of elements of server's url", 2, list.size());
            Assert.assertTrue("List does not contain 'http://www.geosdi.org/test'", list.contains(urlServer));
            Assert.assertTrue("List does not contain 'http://www.geosdi.org/newtest'", list.contains(newUrlServer));
        } catch (IllegalParameterFault ex) {
            Assert.fail("Layer has an Illegal Parameter");
        } catch (ResourceNotFoundFault ex) {
            Assert.fail("User with username \"" + usernameTest + "\" was NOT found");
        }
    }

    private List<Long> addLayer3() throws IllegalParameterFault, ResourceNotFoundFault {
        // "userTestRootFolderA" ---> "rasterLayer3"
        GPRasterLayer rasterLayer3 = new GPRasterLayer();
        super.createLayer(rasterLayer3, userTestRootFolderA, titleRaster3, "", "",
                7, false, spatialReferenceSystem, newUrlServer);
        GPLayerInfo layerInfo = new GPLayerInfo();
        layerInfo.setKeywords(layerInfoKeywords);
        layerInfo.setQueryable(false);
        rasterLayer3.setLayerInfo(layerInfo);
        // "userTestRootFolderA" ---> "vectorLayer3"
        GPVectorLayer vectorLayer3 = new GPVectorLayer();
        super.createLayer(vectorLayer3, userTestRootFolderA, titleVector3, "", "",
                6, false, spatialReferenceSystem, newUrlServer);
        //
        ArrayList<GPLayer> arrayList = new ArrayList<GPLayer>();
        arrayList.add(rasterLayer3);
        arrayList.add(vectorLayer3);

        Map<Long, Integer> map = new HashMap<Long, Integer>();
        map.put(idUserRootFolderA, 4); // 2 + 2
        GPWebServiceMapData descendantsMapData = new GPWebServiceMapData();
        descendantsMapData.setDescendantsMap(map);

        return geoPlatformService.saveAddedLayersAndTreeModifications(
                super.usernameTest, arrayList, descendantsMapData);
    }

    private void checkInitialState(String info)
            throws ResourceNotFoundFault {
        userTestRootFolderA = geoPlatformService.getUserFolderDetail(idUserRootFolderA);
        Assert.assertEquals("Position of root folder A - " + info, 6, userTestRootFolderA.getPosition());
        Assert.assertEquals("Number of descendant of root folder A - " + info, 2, userTestRootFolderA.getFolder().getNumberOfDescendants());

        raster1 = geoPlatformService.getRasterLayer(idRaster1);
        Assert.assertEquals("Position of raster layer 1 - " + info, 5, raster1.getPosition());
        Assert.assertEquals("Parent of raster layer 1 - " + info, idUserRootFolderA, raster1.getUserFolder().getId());

        vector1 = geoPlatformService.getVectorLayer(idVector1);
        Assert.assertEquals("Position of vector layer 1 - " + info, 4, vector1.getPosition());
        Assert.assertEquals("Parent of vector layer 1 - " + info, idUserRootFolderA, vector1.getUserFolder().getId());

        userTestRootFolderB = geoPlatformService.getUserFolderDetail(idUserRootFolderB);
        Assert.assertEquals("Position of root folder B - " + info, 3, userTestRootFolderB.getPosition());
        Assert.assertEquals("Number of descendant of root folder B - " + info, 2, userTestRootFolderB.getFolder().getNumberOfDescendants());

        raster2 = geoPlatformService.getRasterLayer(idRaster2);
        Assert.assertEquals("Position of raster layer 2 - " + info, 2, raster2.getPosition());
        Assert.assertEquals("Parent of raster layer 2 - " + info, idUserRootFolderB, raster2.getUserFolder().getId());

        vector2 = geoPlatformService.getVectorLayer(idVector2);
        Assert.assertEquals("Position of vector layer 2 - " + info, 1, vector2.getPosition());
        Assert.assertEquals("Parent of vector layer 2 - " + info, idUserRootFolderB, vector2.getUserFolder().getId());
    }

    private void checkState(int[] positions, int[] numberOfDescendants, String info)
            throws ResourceNotFoundFault {
        Assert.assertEquals("Array positions must have exactly 6 elements", 6, positions.length);
        Assert.assertEquals("Array numberOfDescendants must have exactly 2 elements", 2, numberOfDescendants.length);

        userTestRootFolderA = geoPlatformService.getUserFolderDetail(idUserRootFolderA);
        Assert.assertEquals("Position of root folder A - " + info, positions[0], userTestRootFolderA.getPosition());
        Assert.assertEquals("Number of descendant of root folder A - " + info, numberOfDescendants[0], userTestRootFolderA.getFolder().getNumberOfDescendants());

        raster1 = geoPlatformService.getRasterLayer(idRaster1);
        Assert.assertEquals("Position of raster layer 1 - " + info, positions[1], raster1.getPosition());

        vector1 = geoPlatformService.getVectorLayer(idVector1);
        Assert.assertEquals("Position of vector layer 1 - " + info, positions[2], vector1.getPosition());

        userTestRootFolderB = geoPlatformService.getUserFolderDetail(idUserRootFolderB);
        Assert.assertEquals("Position of root folder B - " + info, positions[3], userTestRootFolderB.getPosition());
        Assert.assertEquals("Number of descendant of root folder B - " + info, numberOfDescendants[1], userTestRootFolderB.getFolder().getNumberOfDescendants());

        raster2 = geoPlatformService.getRasterLayer(idRaster2);
        Assert.assertEquals("Position of raster layer 2 - " + info, positions[4], raster2.getPosition());

        vector2 = geoPlatformService.getVectorLayer(idVector2);
        Assert.assertEquals("Position of vector layer 2 - " + info, positions[5], vector2.getPosition());
    }

    // Check if a folder was eliminated
    private void checkLayerDeleted(long idLayer) {
        try {
            ShortLayerDTO layer = geoPlatformService.getShortLayer(idLayer);
            Assert.fail("Layer with id \"" + idLayer + "\" was NOT deleted");
        } catch (ResourceNotFoundFault rnnf) {
            logger.trace("Layer with id {} was deleted", idLayer);
        }
    }
}
