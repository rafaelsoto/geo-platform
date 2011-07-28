//<editor-fold defaultstate="collapsed" desc="License">
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
    public void setUp() throws Exception {
        super.setUp();

        // "rootFolderA" ---> "rasterLayer1"
        idRaster1 = createAndInsertRasterLayer(rootFolderA, titleRaster1, "name_" + titleRaster1,
                "abstract_" + titleRaster1, 5, spatialReferenceSystem, urlServer);
        raster1 = gpWSClient.getRasterLayer(idRaster1);
        // "rootFolderA" ---> "vectorLayer1"
        idVector1 = createAndInsertVectorLayer(rootFolderA, titleVector1, "name_" + titleVector1,
                "abstract_" + titleVector1, 4, spatialReferenceSystem, urlServer);
        vector1 = gpWSClient.getVectorLayer(idVector1);
        //
        rootFolderA.setPosition(6);
        rootFolderA.setNumberOfDescendants(2);
        gpWSClient.updateFolder(rootFolderA);

        // "rootFolderB" ---> "rasterLayer2"
        idRaster2 = createAndInsertRasterLayer(rootFolderB, titleRaster2, "name_" + titleRaster2,
                "abstract_" + titleRaster2, 2, spatialReferenceSystem, urlServer);
        raster2 = gpWSClient.getRasterLayer(idRaster2);
        // "rootFolderB" ---> "vectorLayer2"
        idVector2 = createAndInsertVectorLayer(rootFolderB, titleVector2, "name_" + titleVector2,
                "abstract_" + titleVector2, 1, spatialReferenceSystem, urlServer);
        vector2 = gpWSClient.getVectorLayer(idVector2);
        //
        rootFolderB.setPosition(3);
        rootFolderB.setNumberOfDescendants(2);
        gpWSClient.updateFolder(rootFolderB);

        super.projectTest.setNumberOfElements(projectTest.getNumberOfElements() + 4);
        gpWSClient.updateProject(projectTest);
    }

    @Test
    public void testAddLayers() {
        try {
            List<Long> idList = this.addLayer3();

            this.checkState(new int[]{8, 5, 4, 3, 2, 1}, new int[]{4, 2}, "after add layers");

            GPLayer newRasterLayer3 = gpWSClient.getRasterLayer(idList.get(0));
            Assert.assertEquals("title of newRasterLayer3", titleRaster3, newRasterLayer3.getTitle());
            Assert.assertEquals("position of newRasterLayer3", 7, newRasterLayer3.getPosition());

            GPLayer newVectorLayer3 = gpWSClient.getVectorLayer(idList.get(1));
            Assert.assertEquals("title of newVectorLayer3", titleVector3, newVectorLayer3.getTitle());
            Assert.assertEquals("position of newVectorLayer3", 6, newVectorLayer3.getPosition());

        } catch (IllegalParameterFault ipf) {
            Assert.fail("Layer has an Illegal Parameter");
        } catch (ResourceNotFoundFault rnnf) {
            Assert.fail("Layer with ID \"" + rnnf.getId() + "\"has a resource not found");
        }
    }

    @Test
    public void testGetLayer() {
        try {
            ShortLayerDTO shortRasterLayer1 = gpWSClient.getShortLayer(idRaster1);
            Assert.assertNotNull("assertNotNull shortRasterLayer1", shortRasterLayer1);
            Assert.assertEquals("assertEquals shortRasterLayer1.getTitle()", titleRaster1, shortRasterLayer1.getTitle());
            Assert.assertEquals("assertEquals shortRasterLayer1.getName()", "name_" + titleRaster1, shortRasterLayer1.getName());
            Assert.assertEquals("assertEquals shortRasterLayer1.getPosition()", 5, shortRasterLayer1.getPosition());
            Assert.assertEquals("assertEquals shortRasterLayer1.getSrs()", spatialReferenceSystem, shortRasterLayer1.getSrs());
            Assert.assertEquals("assertEquals shortRasterLayer1.getUrlServer()", urlServer, shortRasterLayer1.getUrlServer());
            Assert.assertEquals("assertEquals shortRasterLayer1.getLayerType()", GPLayerType.RASTER, shortRasterLayer1.getLayerType());

            ShortLayerDTO shortVectorLayer1 = gpWSClient.getShortLayer(idVector1);
            Assert.assertNotNull("assertNotNull shortVectorLayer1", shortVectorLayer1);
            Assert.assertEquals("assertEquals shortVectorLayer1.getTitle()", titleVector1, shortVectorLayer1.getTitle());
            Assert.assertEquals("assertEquals shortVectorLayer1.getName()", "name_" + titleVector1, shortVectorLayer1.getName());
            Assert.assertEquals("assertEquals shortVectorLayer1.getPosition()", 4, shortVectorLayer1.getPosition());
            Assert.assertEquals("assertEquals shortVectorLayer1.getSrs()", spatialReferenceSystem, shortVectorLayer1.getSrs());
            Assert.assertEquals("assertEquals shortVectorLayer1.getUrlServer()", urlServer, shortVectorLayer1.getUrlServer());
            Assert.assertEquals("assertEquals shortVectorLayer1.getLayerType()", GPLayerType.POLYGON, shortVectorLayer1.getLayerType());
        } catch (ResourceNotFoundFault rnnf) {
            Assert.fail("Layer with ID \"" + rnnf.getId() + "\" has a resource not found");
        }
    }

    @Test
    public void testUpdateRasterLayer() {
        final String titleLayerUpdated = "rasterLayerUpdated";
        try {
            raster1.setTitle(titleLayerUpdated);

            gpWSClient.updateRasterLayer(raster1);
            ShortLayerDTO layerUpdated = gpWSClient.getShortLayer(idRaster1);

            Assert.assertNotNull("assertNotNull layerUpdated", layerUpdated);
            Assert.assertEquals("assertEquals layerUpdated.getTitle()", titleLayerUpdated, layerUpdated.getTitle());
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
            List<ShortLayerDTO> allLayersBeforeDelete = gpWSClient.getLayers(idProjectTest);
            int totalLayers = allLayersBeforeDelete.size();
            Assert.assertTrue("assertEquals totalLayers", totalLayers == 4); // SetUp() added 4 layers

            // Delete "rasterLayer1" from "rootFolderA"
            boolean erased = gpWSClient.deleteLayer(idRaster1);
            Assert.assertTrue("Deletion of the layer rasterLayer1", erased);

            // Get root folders for project
            List<FolderDTO> folderList = gpWSClient.getRootFoldersByProjectId(idProjectTest);

            // Assert on the structure of project's folders
            Assert.assertEquals("assertEquals folderList.getList().size()", folderList.size(), 2);
            // Assert on the structure of "rootFolderA"
            TreeFolderElements childrenRootFolderA = gpWSClient.getChildrenElements(idRootFolderA);
            logger.trace("\n*** childrenRootFolderA:\n{}\n***", childrenRootFolderA);
            Assert.assertNotNull("assertNotNull childrenRootFolderA", childrenRootFolderA);
            Assert.assertEquals("assertEquals childrenRootFolderA.size()", childrenRootFolderA.size(), 1);
            // Assert on layers of "rootFolderA"
            ShortLayerDTO shortVectorLayerRootFolderA = (ShortLayerDTO) childrenRootFolderA.iterator().next();
            Assert.assertEquals("assertEquals shortVectorLayerRootFolderA.getTitle()", shortVectorLayerRootFolderA.getTitle(), titleVector1);
            // Assert on the structure of "rootFolderB"
            TreeFolderElements childrenRootFolderB = gpWSClient.getChildrenElements(idRootFolderB);
            logger.trace("\n*** childrenRootFolderB:\n{}\n***", childrenRootFolderB);
            Assert.assertNotNull("assertNotNull childrenRootFolderB", childrenRootFolderB);
            Assert.assertEquals("assertEquals childrenRootFolderB.size()", childrenRootFolderB.size(), 2);
            // Assert on layers of "rootFolderB"
            Iterator iterator = childrenRootFolderB.iterator();
            ShortLayerDTO shortRasterLayerRootFolderB = (ShortLayerDTO) iterator.next();
            Assert.assertEquals("assertEquals shortRasterLayerRootFolderB.getTitle()", shortRasterLayerRootFolderB.getTitle(), titleRaster2);
            ShortLayerDTO shortVectorLayerRootFolderB = (ShortLayerDTO) iterator.next();
            Assert.assertEquals("assertEquals shortVectorLayerRootFolderB.getTitle()", shortVectorLayerRootFolderB.getTitle(), titleVector2);

            // Assert total number of layers stored into DB after delete
            List<ShortLayerDTO> allLayersAfterDelete = gpWSClient.getLayers(idProjectTest);
            Assert.assertEquals("assertEquals allLayersAfterDelete.getList().size()", allLayersAfterDelete.size(), totalLayers - 1);
        } catch (ResourceNotFoundFault rnff) {
            Assert.fail("Folder with id \"" + rnff.getId() + "\" was NOT found");
        } catch (Exception e) {
            Assert.fail("Exception: " + e.getClass());
        }
        // Check ON DELETE CASCADE of the subforders of "rootFolderB"
        checkLayerDeleted(idRaster1);
    }

    @Test
    public void testSaveAndDeleteLayerAndTreeModifications() {
        GPRasterLayer layerToTest = null;
        Map<Long, Integer> map = new HashMap<Long, Integer>();
        GPWebServiceMapData descendantsMapData = new GPWebServiceMapData();
        descendantsMapData.setDescendantsMap(map);
        try {
            int totalElementsOfProject = gpWSClient.getNumberOfElementsProject(idProjectTest);
            Assert.assertEquals("Initial totalElementsOfProject",
                    6, totalElementsOfProject);  // SetUp() added 2 folders + 4 layers

            String titleLayerToTest = "layerToTest";
            layerToTest = new GPRasterLayer();
            super.createLayer(layerToTest, rootFolderB, titleLayerToTest, "name_" + titleLayerToTest,
                    "abstract_" + titleLayerToTest, 3, spatialReferenceSystem, urlServer);

            GPLayerInfo layerInfo = new GPLayerInfo();
            layerInfo.setKeywords(layerInfoKeywords);
            layerInfo.setQueryable(false);
            layerToTest.setLayerInfo(layerInfo);

            // Adding new layer to user's root folder B
            map.put(idRootFolderB, rootFolderB.getNumberOfDescendants() + 1);

            // Adding new layer to user's root folder B
            long idLayerToTest = gpWSClient.saveAddedLayerAndTreeModifications(
                    layerToTest, descendantsMapData);

            Assert.assertEquals("totalElementsOfProject after added",
                    totalElementsOfProject + 1, gpWSClient.getNumberOfElementsProject(idProjectTest));

            this.checkState(new int[]{7, 6, 5, 4, 2, 1}, new int[]{2, 3}, "before removing");

            // Removing layer from user's root
            map.clear();
            map.put(idRootFolderB, 2);
            gpWSClient.saveDeletedLayerAndTreeModifications(idLayerToTest, descendantsMapData);

            Assert.assertEquals("totalElementsOfProject after deleted",
                    totalElementsOfProject, gpWSClient.getNumberOfElementsProject(idProjectTest));

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
            boolean checkDD = gpWSClient.saveDragAndDropLayerAndTreeModifications(
                    idVector2, idRootFolderB, 2, descendantsMapData);
            Assert.assertTrue("Drag and Drop successful", checkDD);

            this.checkState(new int[]{6, 5, 4, 3, 1, 2}, new int[]{2, 2}, "after DD I on same parent");

            // Move vector 2 after raster 2, in initial position (oldPosition > new Position)
            checkDD = gpWSClient.saveDragAndDropLayerAndTreeModifications(
                    idVector2, idRootFolderB, 1, descendantsMapData);
            Assert.assertTrue("Vector 2 doesn't moved to position 1", checkDD);

            this.checkInitialState("after DD II on same parent");

        } catch (IllegalParameterFault ex) {
            Assert.fail("Layer has an Illegal Parameter: " + ex.getMessage());
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
            map.put(idRootFolderA, 3);
            map.put(idRootFolderB, 1);
            // Move vector 2 before vector 1 (oldPosition < new Position)
            boolean checkDD = gpWSClient.saveDragAndDropLayerAndTreeModifications(
                    idVector2, idRootFolderA, 4, descendantsMapData);
            Assert.assertTrue("Drag and Drop successful", checkDD);

            this.checkState(new int[]{6, 5, 3, 2, 1, 4}, new int[]{3, 1}, "after DD I on different parent");
            Assert.assertEquals("Parent of vector layer 2 after DD I on different parent", idRootFolderA, vector2.getFolder().getId());

            map.clear();
            map.put(idRootFolderA, 2);
            map.put(idRootFolderB, 2);
            // Move vector 2 after raster 2, in initial position (oldPosition > new Position)
            checkDD = gpWSClient.saveDragAndDropLayerAndTreeModifications(
                    idVector2, idRootFolderB, 1, descendantsMapData);
            Assert.assertTrue("Vector 2 doesn't moved to position 1", checkDD);

            this.checkInitialState("after DD II on different parent");

        } catch (IllegalParameterFault ex) {
            Assert.fail("Layer has an Illegal Parameter: " + ex.getMessage());
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
        map.put(idRootFolderA, 3);
        try {
            GPRasterLayer raster = new GPRasterLayer();
            super.createLayer(raster, rootFolderA, null, "", "",
                    5, spatialReferenceSystem, urlServer); // Title must be NOT NULL
            gpWSClient.saveAddedLayerAndTreeModifications(raster, descendantsMapData);
            Assert.fail("Add layer must fail because title value is null");
        } catch (Exception e) {
            this.checkInitialState("transaction test");
        }
    }
    
    @Test
    public void testTransactionOnRemoveAndAddLayer() throws IllegalParameterFault, ResourceNotFoundFault {
        logger.trace("\n\t@@@ testTransactionOnRemoveAndAddLayer @@@");
        Map<Long, Integer> map = new HashMap<Long, Integer>();
        GPWebServiceMapData descendantsMapData = new GPWebServiceMapData();
        descendantsMapData.setDescendantsMap(map);
        map.put(idRootFolderA, 3);
        try {
            // Delete "rasterLayer1" from "rootFolderA"
            boolean erased = gpWSClient.deleteLayer(idRaster1);
            Assert.assertTrue("Deletion of the layer rasterLayer1", erased);

            GPRasterLayer raster = new GPRasterLayer();
            super.createLayer(raster, rootFolderA, null, "", "",
                    5, spatialReferenceSystem, urlServer); // Title must be NOT NULL
            gpWSClient.saveAddedLayerAndTreeModifications(raster, descendantsMapData);
            Assert.fail("Add layer must fail because title value is null");
        } catch (IllegalParameterFault e) {
            try {
                raster1 = gpWSClient.getRasterLayer(idRaster1);
                Assert.fail("rasterLayer1 must not exist");
            } catch (ResourceNotFoundFault rnf) {}
        }
    }

    @Test
    public void testCorrectnessOnAddLayers() throws ResourceNotFoundFault {
        logger.trace("\n\t@@@ testCorrectnessOnAddLayers @@@");
        Map<Long, Integer> map = new HashMap<Long, Integer>();
        GPWebServiceMapData descendantsMapData = new GPWebServiceMapData();
        descendantsMapData.setDescendantsMap(map);

        ArrayList<GPLayer> arrayList = new ArrayList<GPLayer>();
        try {
            List<Long> longList = gpWSClient.saveAddedLayersAndTreeModifications(
                    arrayList, descendantsMapData);
            Assert.fail("Test must fail because list of layers is empty");
        } catch (IllegalParameterFault ex) {
            this.checkInitialState("correctess on AddLayers");
        }

    }

    @Test
    public void testGetLayerInfo() {
        try {
            GPLayerInfo layerInfo = gpWSClient.getLayerInfo(idRaster2);
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
    public void testGetLayersDataSourceByProject() {
        try {
            this.addLayer3();

            List<String> list = gpWSClient.getLayersDataSourceByProjectId(idProjectTest);

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
        // "rootFolderA" ---> "rasterLayer3"
        GPRasterLayer rasterLayer3 = new GPRasterLayer();
        super.createLayer(rasterLayer3, rootFolderA, titleRaster3, "", "",
                7, spatialReferenceSystem, newUrlServer);
        GPLayerInfo layerInfo = new GPLayerInfo();
        layerInfo.setKeywords(layerInfoKeywords);
        layerInfo.setQueryable(false);
        rasterLayer3.setLayerInfo(layerInfo);
        // "rootFolderA" ---> "vectorLayer3"
        GPVectorLayer vectorLayer3 = new GPVectorLayer();
        super.createLayer(vectorLayer3, rootFolderA, titleVector3, "", "",
                6, spatialReferenceSystem, newUrlServer);
        //
        ArrayList<GPLayer> arrayList = new ArrayList<GPLayer>();
        arrayList.add(rasterLayer3);
        arrayList.add(vectorLayer3);

        Map<Long, Integer> map = new HashMap<Long, Integer>();
        map.put(idRootFolderA, rootFolderA.getNumberOfDescendants() + 2);
        GPWebServiceMapData descendantsMapData = new GPWebServiceMapData();
        descendantsMapData.setDescendantsMap(map);

        return gpWSClient.saveAddedLayersAndTreeModifications(
                arrayList, descendantsMapData);
    }

    private void checkInitialState(String info)
            throws ResourceNotFoundFault {
        rootFolderA = gpWSClient.getFolderDetail(idRootFolderA);
        Assert.assertEquals("Position of root folder A - " + info, 6, rootFolderA.getPosition());
        Assert.assertNull("Parent of root folder A - " + info, rootFolderA.getParent());
        Assert.assertEquals("Number of descendant of root folder A - " + info, 2, rootFolderA.getNumberOfDescendants());

        raster1 = gpWSClient.getRasterLayer(idRaster1);
        Assert.assertEquals("Position of raster layer 1 - " + info, 5, raster1.getPosition());
        Assert.assertEquals("Parent of raster layer 1 - " + info, idRootFolderA, raster1.getFolder().getId());

        vector1 = gpWSClient.getVectorLayer(idVector1);
        Assert.assertEquals("Position of vector layer 1 - " + info, 4, vector1.getPosition());
        Assert.assertEquals("Parent of vector layer 1 - " + info, idRootFolderA, vector1.getFolder().getId());

        rootFolderB = gpWSClient.getFolderDetail(idRootFolderB);
        Assert.assertEquals("Position of root folder B - " + info, 3, rootFolderB.getPosition());
        Assert.assertNull("Parent of root folder B - " + info, rootFolderB.getParent());
        Assert.assertEquals("Number of descendant of root folder B - " + info, 2, rootFolderB.getNumberOfDescendants());

        raster2 = gpWSClient.getRasterLayer(idRaster2);
        Assert.assertEquals("Position of raster layer 2 - " + info, 2, raster2.getPosition());
        Assert.assertEquals("Parent of raster layer 2 - " + info, idRootFolderB, raster2.getFolder().getId());

        vector2 = gpWSClient.getVectorLayer(idVector2);
        Assert.assertEquals("Position of vector layer 2 - " + info, 1, vector2.getPosition());
        Assert.assertEquals("Parent of vector layer 2 - " + info, idRootFolderB, vector2.getFolder().getId());
    }

    private void checkState(int[] positions, int[] numberOfDescendants, String info)
            throws ResourceNotFoundFault {
        Assert.assertEquals("Array positions must have exactly 6 elements", 6, positions.length);
        Assert.assertEquals("Array numberOfDescendants must have exactly 2 elements", 2, numberOfDescendants.length);

        rootFolderA = gpWSClient.getFolderDetail(idRootFolderA);
        Assert.assertEquals("Position of root folder A - " + info, positions[0], rootFolderA.getPosition());
        Assert.assertNull("Parent of root folder A - " + info, rootFolderA.getParent());
        Assert.assertEquals("Number of descendant of root folder A - " + info, numberOfDescendants[0], rootFolderA.getNumberOfDescendants());

        raster1 = gpWSClient.getRasterLayer(idRaster1);
        Assert.assertEquals("Position of raster layer 1 - " + info, positions[1], raster1.getPosition());

        vector1 = gpWSClient.getVectorLayer(idVector1);
        Assert.assertEquals("Position of vector layer 1 - " + info, positions[2], vector1.getPosition());

        rootFolderB = gpWSClient.getFolderDetail(idRootFolderB);
        Assert.assertEquals("Position of root folder B - " + info, positions[3], rootFolderB.getPosition());
        Assert.assertNull("Parent of root folder B - " + info, rootFolderB.getParent());
        Assert.assertEquals("Number of descendant of root folder B - " + info, numberOfDescendants[1], rootFolderB.getNumberOfDescendants());

        raster2 = gpWSClient.getRasterLayer(idRaster2);
        Assert.assertEquals("Position of raster layer 2 - " + info, positions[4], raster2.getPosition());

        vector2 = gpWSClient.getVectorLayer(idVector2);
        Assert.assertEquals("Position of vector layer 2 - " + info, positions[5], vector2.getPosition());
    }

    // Check if a folder was eliminated
    private void checkLayerDeleted(long idLayer) {
        try {
            ShortLayerDTO layer = gpWSClient.getShortLayer(idLayer);
            Assert.fail("Layer with id \"" + idLayer + "\" was NOT deleted");
        } catch (ResourceNotFoundFault rnnf) {
            logger.trace("Layer with id {} was deleted", idLayer);
        }
    }
}