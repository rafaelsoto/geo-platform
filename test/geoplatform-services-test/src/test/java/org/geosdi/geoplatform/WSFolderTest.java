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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import org.junit.Test;

import org.geosdi.geoplatform.core.model.GPUserFolders;
import org.geosdi.geoplatform.exception.IllegalParameterFault;
import org.geosdi.geoplatform.exception.ResourceNotFoundFault;
import org.geosdi.geoplatform.responce.FolderDTO;
import org.geosdi.geoplatform.responce.collection.GPWebServiceMapData;
import org.geosdi.geoplatform.responce.collection.TreeFolderElements;

/**
 *
 * @author Giuseppe La Scaleia - CNR IMAA geoSDI Group
 * @email  giuseppe.lascaleia@geosdi.org
 */
// TODO TEST DD on same position
// TODO TEST DD with many subelements
public class WSFolderTest extends ServiceTest {

    private final String nameFolder1 = "folder1";
    private final String nameFolder2 = "folder2";
    private final String nameFolder3 = "folder3";
    private final String nameFolder4 = "folder4";
    private final String nameFolder5 = "folder5";
    private long idUserTestFolder1 = -1;
    private long idUserTestFolder2 = -1;
    private long idUserTestFolder3 = -1;
    private long idUserTestFolder4 = -1;
    private long idUserTestFolder5 = -1;
    private GPUserFolders userTestFolder1 = null;
    private GPUserFolders userTestFolder2 = null;
    private GPUserFolders userTestFolder3 = null;
    private GPUserFolders userTestFolder4 = null;
    private GPUserFolders userTestFolder5 = null;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        // "rootFolderA" ---> "folder1"
        idUserTestFolder1 = createAndInsertFolder(userTest, nameFolder1, 6, false, userTestRootFolderA);
        userTestFolder1 = geoPlatformService.getUserFolderByUserAndFolderId(idUserTest, idUserTestFolder1);
        // "rootFolderA" ---> "folder2"
        idUserTestFolder2 = createAndInsertFolder(userTest, nameFolder2, 5, false, userTestRootFolderA);
        userTestFolder2 = geoPlatformService.getUserFolderByUserAndFolderId(idUserTest, idUserTestFolder2);
        //
        userTestRootFolderA.setPosition(7);
        userTestRootFolderA.getFolder().setNumberOfDescendants(2);
        geoPlatformService.updateUserFolder(userTestRootFolderA);

        // "rootFolderB" ---> "folder3"
        idUserTestFolder3 = createAndInsertFolder(userTest, nameFolder3, 3, false, userTestRootFolderB);
        userTestFolder3 = geoPlatformService.getUserFolderByUserAndFolderId(idUserTest, idUserTestFolder3);
        // "rootFolderB" ---> "folder4"
        idUserTestFolder4 = createAndInsertFolder(userTest, nameFolder4, 2, false, userTestRootFolderB);
        userTestFolder4 = geoPlatformService.getUserFolderByUserAndFolderId(idUserTest, idUserTestFolder4);
        // "rootFolderB" ---> "folder5"
        idUserTestFolder5 = createAndInsertFolder(userTest, nameFolder5, 1, false, userTestRootFolderB);
        userTestFolder5 = geoPlatformService.getUserFolderByUserAndFolderId(idUserTest, idUserTestFolder5);
        //
        userTestRootFolderB.setPosition(4);
        userTestRootFolderB.getFolder().setNumberOfDescendants(3);
        geoPlatformService.updateUserFolder(userTestRootFolderB);
    }

    @Test
    public void testFoldersTest() {
        Assert.assertNotNull("UserFolder 1 is NULL", userTestFolder1);
        Assert.assertEquals("ID of UserFolder 1 is incorrect", userTestFolder1.getId(), idUserTestFolder1);

        Assert.assertNotNull("UserFolder 2 is NULL", userTestFolder2);
        Assert.assertEquals("ID of UserFolder 2 is incorrect", userTestFolder2.getId(), idUserTestFolder2);

        Assert.assertNotNull("UserFolder 3 is NULL", userTestFolder3);
        Assert.assertEquals("ID of UserFolder 3 is incorrect", userTestFolder3.getId(), idUserTestFolder3);

        Assert.assertNotNull("UserFolder 4 is NULL", userTestFolder4);
        Assert.assertEquals("ID of UserFolder 4 is incorrect", userTestFolder4.getId(), idUserTestFolder4);

        Assert.assertNotNull("UserFolder 5 is NULL", userTestFolder5);
        Assert.assertEquals("ID of UserFolder 5 is incorrect", userTestFolder5.getId(), idUserTestFolder5);
    }

    @Test
    public void testGetShortFolder() {
        try {
            FolderDTO folderA = geoPlatformService.getShortFolder(idUserRootFolderA);
            Assert.assertNotNull("assertNotNull Folder A", folderA);
        } catch (ResourceNotFoundFault rnnf) {
            Assert.fail("Unable to find folder with id \"" + rnnf.getId());
        }

        try {
            FolderDTO folderB = geoPlatformService.getShortFolder(idUserRootFolderB);
            Assert.assertNotNull("assertNotNull Folder B", folderB);
        } catch (ResourceNotFoundFault rnnf) {
            Assert.fail("Unable to find folder with id \"" + rnnf.getId());
        }
    }

    @Test
    public void testUpdateFolder() {
        final String nameUserFolderUpdated = "folderUpdated";
        try {
            userTestFolder5.setParent(userTestRootFolderA);
            userTestFolder5.getFolder().setName(nameUserFolderUpdated);

            geoPlatformService.updateUserFolder(userTestFolder5);
            GPUserFolders userFolderUpdated = geoPlatformService.getUserFolderDetail(idUserTestFolder5);

            Assert.assertNotNull("userFolderUpdated is NULL", userFolderUpdated);
            Assert.assertNotNull("User of userFolderUpdated is NULL", userFolderUpdated.getUser());
            Assert.assertNotNull("Folder of userFolderUpdated is NULL", userFolderUpdated.getFolder());
            Assert.assertEquals("Folder name of userFolderUpdated NOT match", nameUserFolderUpdated, userFolderUpdated.getFolder().getName());
            Assert.assertEquals("Parent ID of userFolderUpdated NOT match", idUserRootFolderA, userFolderUpdated.getParent().getId());

        } catch (IllegalParameterFault ex) {
            Assert.fail("Folder has an Illegal Parameter");
        } catch (ResourceNotFoundFault rnnf) {
            Assert.fail("Folder with id \"" + rnnf.getId() + "\" not found");
        }
    }

    @Test
    public void testDeleteFolder() {
        try {
            // Assert number of folders of UserTest before delete
            int totalFolders = geoPlatformService.getAllUserFoldersCount(idUserTest);
            Assert.assertEquals("Number of all folders of UserTest before deleted",
                    7, totalFolders); // SetUp() added 2+5 folders
            //
            List<FolderDTO> rootFolderList = geoPlatformService.getAllUserFoldersByUserId(idUserTest);
            int totalRootFolders = rootFolderList.size();
            Assert.assertEquals("Number of root folders of UserTest before deleted",
                    2, totalRootFolders);

            // Delete "rootFolderB" and in cascade "folder3" & "folder4" & "folder5"
            geoPlatformService.deleteUserFolder(idUserRootFolderB);

            // "rootFolderA" ---> "folder1" & "folder2"
            List<FolderDTO> folderList = geoPlatformService.getAllUserFoldersByUserId(idUserTest);

            // Assert total number of folders of UserTest after delete
            Assert.assertEquals("Number of root folders of UserTest after deleted",
                    1, folderList.size());
            Assert.assertEquals("Number of all folders of UserTest after deleted",
                    totalFolders - 4, geoPlatformService.getAllUserFoldersCount(idUserTest));

            // Assert on the structure of user's folders
            // Assert on "rootFolderA"
            FolderDTO folderToCheck = folderList.iterator().next();
            logger.debug("\n*** folderToCheck:\n{}\n***", folderToCheck);
            Assert.assertEquals("Check idUserFolder on rootFolderA", idUserRootFolderA, folderToCheck.getIdUserFolder().longValue());
            Assert.assertEquals("Check id on rootFolderA", userTestRootFolderA.getFolder().getId(), folderToCheck.getId());
            // Assert on the structure of the subfolders of "rootFolderA"
            TreeFolderElements childrenRootFolderA = geoPlatformService.getChildrenElements(idUserRootFolderA);
            logger.debug("\n*** childrenRootFolderA:\n{}\n***", childrenRootFolderA);
            Assert.assertNotNull("Check childrenRootFolderA not null", childrenRootFolderA);
            Assert.assertEquals("Check size of childrenRootFolderA", 2, childrenRootFolderA.size());
            // Iterator for scan folder in descending order
            Iterator childsIterator = childrenRootFolderA.iterator();
            // Assert on "folder1"
            FolderDTO folderDTOToCheck = (FolderDTO) childsIterator.next();
            logger.debug("\n*** folder_1_DTOToCheck:\n{}\n***", folderDTOToCheck);
            Assert.assertEquals("Check idUserFolder of folder 1", idUserTestFolder1, folderDTOToCheck.getIdUserFolder().longValue());
            Assert.assertEquals("Check id of folder 1", userTestFolder1.getFolder().getId(), folderDTOToCheck.getId());
            // Assert on "folder2"
            folderDTOToCheck = (FolderDTO) childsIterator.next();
            logger.debug("\n*** folder_2_DTOToCheck:\n{}\n***", folderDTOToCheck);
            Assert.assertEquals("Check idUserFolder of folder 2", idUserTestFolder2, folderDTOToCheck.getIdUserFolder().longValue());
            Assert.assertEquals("Check id of folder 2", userTestFolder2.getFolder().getId(), folderDTOToCheck.getId());

            // Assert on "rootFolderB" (deleted)
            TreeFolderElements childrenRootFolderB = geoPlatformService.getChildrenElements(idUserRootFolderB);
            Assert.assertNull("Check childrenRootFolderB null", childrenRootFolderB);

        } catch (IllegalParameterFault ipf) {
            Assert.fail("Folder has an illegal parameter");
        } catch (ResourceNotFoundFault rnff) {
            Assert.fail("Folder with id \"" + rnff.getId() + "\" not found");
        } catch (Exception e) {
            Assert.fail("Exception: " + e.getClass());
        }

        // Check ON DELETE CASCADE of the subforders of "rootFolderB"
        this.checkUserFolderDeleted(idUserTestFolder3);
        this.checkUserFolderDeleted(idUserTestFolder4);
        this.checkUserFolderDeleted(idUserTestFolder5);
    }

    @Test
    public void testSaveAndDeleteFolderAndTreeModifications() {
        GPUserFolders folderToTest = null;
        Map<Long, Integer> map = new HashMap<Long, Integer>();
        GPWebServiceMapData descendantsMapData = new GPWebServiceMapData();
        descendantsMapData.setDescendantsMap(map); // Set an empty map
        try {
            List<FolderDTO> childrenFolders = geoPlatformService.getChildrenFolders(idUserRootFolderB);
            Assert.assertEquals("Before adding new folder - Number of subfolders of root folder B", 3, childrenFolders.size());

            String nameFolderToTest = "folderToTest";
            folderToTest = super.createUserFolder(userTest, nameFolderToTest, 1, false, null);

            // Adding new folder to user's root            
            long idFolderToTest = geoPlatformService.saveAddedFolderAndTreeModifications(folderToTest, descendantsMapData);

            this.checkState(new int[]{8, 7, 6, 5, 4, 3, 2}, new int[]{2, 3}, "before removing");

            // Removing folder from user's root
            boolean checkDelete = geoPlatformService.saveDeletedFolderAndTreeModifications(idFolderToTest, descendantsMapData);
            Assert.assertTrue("Delete NOT done for \"" + folderToTest.getFolder().getName() + "\"", checkDelete);

            this.checkInitialState("after removing");

        } catch (ResourceNotFoundFault rnff) {
            Assert.fail("Folder with id \"" + rnff.getId() + "\" was not found");
        } catch (IllegalParameterFault ex) {
            Assert.fail("Folder with id \"" + folderToTest.getId() + "\" was not found");
        }
    }

    @Test
    public void testSaveAndDeleteSubfolderAndTreeModifications() {
        GPUserFolders folderToTest = null;
        Map<Long, Integer> map = new HashMap<Long, Integer>();
        GPWebServiceMapData descendantsMapData = new GPWebServiceMapData();
        descendantsMapData.setDescendantsMap(map);
        try {
            List<FolderDTO> childrenFolders = geoPlatformService.getChildrenFolders(idUserRootFolderB);
            Assert.assertEquals("Before adding new folder - Number of subfolders of root folder B ", 3, childrenFolders.size());

            String nameFolderToTest = "folderToTest";
            folderToTest = super.createUserFolder(userTest, nameFolderToTest, 3, false, userTestRootFolderB);

            // Adding new folder to user's root folder B
            map.put(idUserRootFolderB, 4);
            long idFolderToTest = geoPlatformService.saveAddedFolderAndTreeModifications(folderToTest, descendantsMapData);

            childrenFolders = geoPlatformService.getChildrenFolders(idUserRootFolderB);
            Assert.assertEquals("After adding new folder - Number of subfolders of root folder B ", 4, childrenFolders.size());

            this.checkState(new int[]{8, 7, 6, 5, 4, 2, 1}, new int[]{2, 4}, "before removing");

            // Removing folder from user's root folder B
            map.clear();
            map.put(idUserRootFolderB, 3);
            boolean checkDelete = geoPlatformService.saveDeletedFolderAndTreeModifications(idFolderToTest, descendantsMapData);
            Assert.assertTrue("Delete NOT done for \"" + folderToTest.getFolder().getName() + "\"", checkDelete);

            childrenFolders = geoPlatformService.getChildrenFolders(idUserRootFolderB);
            Assert.assertEquals("After removing new folder - Number of subfolders of root folder B ", 3, childrenFolders.size());

            this.checkInitialState("after removing");

        } catch (ResourceNotFoundFault rnff) {
            Assert.fail("Folder with id \"" + rnff.getId() + "\" was not found");
        } catch (IllegalParameterFault ex) {
            Assert.fail("Folder with id \"" + folderToTest.getId() + "\" was not found");
        }
    }

    @Test
    public void testDragAndDropOnSameParent() {
        logger.trace("\n\t@@@ testDragAndDropOnSameParent @@@");
        Map<Long, Integer> map = new HashMap<Long, Integer>();
        GPWebServiceMapData descendantsMapData = new GPWebServiceMapData();
        descendantsMapData.setDescendantsMap(map);
        try {
            // Move folder 5 between folder 3 and folder 4 (oldPosition < new Position)
            boolean checkDD = geoPlatformService.saveDragAndDropFolderAndTreeModifications(
                    super.usernameTest, idUserTestFolder5, super.idUserRootFolderB, 2, descendantsMapData);
            Assert.assertTrue("Folder 5 doesn't moved to position 2", checkDD);

            this.checkState(new int[]{7, 6, 5, 4, 3, 1, 2}, new int[]{2, 3}, "after DD I on same parent");

            // Move folder 5 after folder 4, in initial position (oldPosition > new Position)
            checkDD = geoPlatformService.saveDragAndDropFolderAndTreeModifications(
                    super.usernameTest, idUserTestFolder5, super.idUserRootFolderB, 1, descendantsMapData);
            Assert.assertTrue("Folder 5 doesn't moved to position 1", checkDD);

            this.checkInitialState("after DD II on same parent");

        } catch (ResourceNotFoundFault rnff) {
            Assert.fail("Folder with id \"" + rnff.getId() + "\" was not found");
        }
    }

    @Test
    public void testDragAndDropOnDifferentParent() {
        logger.trace("\n\t@@@ testDragAndDropOnDifferentParent @@@");
        Map<Long, Integer> map = new HashMap<Long, Integer>();
        GPWebServiceMapData descendantsMapData = new GPWebServiceMapData();
        descendantsMapData.setDescendantsMap(map);
        try {
            map.put(super.idUserRootFolderA, 3);
            map.put(super.idUserRootFolderB, 2);
            // Move folder 4 between folder 1 and folder 2 (oldPosition < new Position)
            boolean checkDD = geoPlatformService.saveDragAndDropFolderAndTreeModifications(
                    super.usernameTest, idUserTestFolder4, super.idUserRootFolderA, 5, descendantsMapData);
            Assert.assertTrue("Folder 4 doesn't moved to position 5", checkDD);

            this.checkState(new int[]{7, 6, 4, 3, 2, 5, 1}, new int[]{3, 2}, "after DD I on different parent");

            // Move folder 4 after folder 3, in initial position (oldPosition > new Position)
            map.clear();
            map.put(super.idUserRootFolderA, 2);
            map.put(super.idUserRootFolderB, 3);
            checkDD = geoPlatformService.saveDragAndDropFolderAndTreeModifications(
                    super.usernameTest, idUserTestFolder4, super.idUserRootFolderB, 2, descendantsMapData);
            Assert.assertTrue("Folder 4 doesn't moved to position 2", checkDD);

            this.checkInitialState("after DD II on different parent");

        } catch (ResourceNotFoundFault rnff) {
            Assert.fail("Folder with id \"" + rnff.getId() + "\" was not found");
        }
    }

    @Test
    public void testDragAndDropOnRootParent() {
        logger.trace("\n\t@@@ testDragAndDropOnRootParent @@@");
        Map<Long, Integer> map = new HashMap<Long, Integer>();
        GPWebServiceMapData descendantsMapData = new GPWebServiceMapData();
        descendantsMapData.setDescendantsMap(map);
        try {
            // Move folder B before folder A (oldPosition < new Position)
            boolean checkDD = geoPlatformService.saveDragAndDropFolderAndTreeModifications(
                    super.usernameTest, super.idUserRootFolderB, 0, 7, descendantsMapData);
            Assert.assertTrue("Folder B doesn't moved to position 7", checkDD);

            this.checkState(new int[]{3, 2, 1, 7, 6, 5, 4}, new int[]{2, 3}, "after DD I on root parent");

            // Move folder B after folder A, in initial position (oldPosition > new Position)
            checkDD = geoPlatformService.saveDragAndDropFolderAndTreeModifications(
                    super.usernameTest, super.idUserRootFolderB, 0, 4, descendantsMapData);
            Assert.assertTrue("Folder 4 doesn't moved to position 4", checkDD);

            this.checkInitialState("after DD II on root parent");

        } catch (ResourceNotFoundFault rnff) {
            Assert.fail("Folder with id \"" + rnff.getId() + "\" was not found");
        }
    }

    @Test
    public void testDragAndDropFromRootToTop() {
        logger.trace("\n\t@@@ testDragAndDropFromRootToTop @@@");
        Map<Long, Integer> map = new HashMap<Long, Integer>();
        GPWebServiceMapData descendantsMapData = new GPWebServiceMapData();
        descendantsMapData.setDescendantsMap(map);
        try {
            map.put(idUserRootFolderA, 6);
            // Move Folder B after Folder 1 (oldPosition < new Position)
            boolean checkDD = geoPlatformService.saveDragAndDropFolderAndTreeModifications(
                    super.usernameTest, super.idUserRootFolderB, super.idUserRootFolderA, 6, descendantsMapData);
            Assert.assertTrue("Folder B doesn't moved to position 6", checkDD);

            this.checkState(new int[]{7, 2, 1, 6, 5, 4, 3}, new int[]{6, 3}, "after DD I from root to top");
            Assert.assertNull("Parent of root folder A after DD I from root to top", userTestRootFolderA.getParent());
            Assert.assertNotNull("Parent of root folder B after DD I from root to top", userTestRootFolderB.getParent());
            Assert.assertEquals("Parent of root folder B after DD I from root to top", idUserRootFolderA, userTestRootFolderB.getParent().getId());

            map.clear();
            map.put(idUserRootFolderA, 2);
            // Move folder B in initial position (oldPosition > new Position)
            checkDD = geoPlatformService.saveDragAndDropFolderAndTreeModifications(
                    super.usernameTest, super.idUserRootFolderB, 0, 4, descendantsMapData);
            Assert.assertTrue("Folder B doesn't moved to position 4", checkDD);

            this.checkInitialState("after DD II from root to top");

        } catch (ResourceNotFoundFault rnff) {
            Assert.fail("Folder with id \"" + rnff.getId() + "\" was not found");
        }
    }

    @Test
    public void testDragAndDropFromRootToBottom() {
        logger.trace("\n\t@@@ testDragAndDropFromRootToBottom @@@");
        Map<Long, Integer> map = new HashMap<Long, Integer>();
        GPWebServiceMapData descendantsMapData = new GPWebServiceMapData();
        descendantsMapData.setDescendantsMap(map);
        try {
            map.put(idUserRootFolderB, 6);
            // Move Folder A after Folder 3 (oldPosition > new Position)
            boolean checkDD = geoPlatformService.saveDragAndDropFolderAndTreeModifications(
                    super.usernameTest, super.idUserRootFolderA, super.idUserRootFolderB, 5, descendantsMapData);
            Assert.assertTrue("Folder A doesn't moved to position 5", checkDD);

            this.checkState(new int[]{5, 4, 3, 7, 6, 2, 1}, new int[]{2, 6}, "after DD I from root to bottom");
            Assert.assertNotNull("Parent of root folder A after DD I from root to bottom", userTestRootFolderA.getParent());
            Assert.assertEquals("Parent of root folder A after DD I from root to bottom", idUserRootFolderB, userTestRootFolderA.getParent().getId());
            Assert.assertNull("Parent of root folder B after DD I from root to bottom", userTestRootFolderB.getParent());

            map.clear();
            map.put(idUserRootFolderB, 3);
            // Move folder A in initial position (oldPosition < new Position)
            checkDD = geoPlatformService.saveDragAndDropFolderAndTreeModifications(
                    super.usernameTest, super.idUserRootFolderA, 0, 7, descendantsMapData);
            Assert.assertTrue("Folder B doesn't moved to position 7", checkDD);

            this.checkInitialState("after DD II from root to bottom");

        } catch (ResourceNotFoundFault rnff) {
            Assert.fail("Folder with id \"" + rnff.getId() + "\" was not found");
        }
    }

    private void checkInitialState(String info)
            throws ResourceNotFoundFault {

        userTestRootFolderA = geoPlatformService.getUserFolderDetail(idUserRootFolderA);
        Assert.assertEquals("Position of root folder A - " + info, 7, userTestRootFolderA.getPosition());
        Assert.assertEquals("Number of descendant of root folder A - " + info, 2, userTestRootFolderA.getFolder().getNumberOfDescendants());

        userTestFolder1 = geoPlatformService.getUserFolderDetail(idUserTestFolder1);
        Assert.assertEquals("Position of folder 1 - " + info, 6, userTestFolder1.getPosition());

        userTestFolder2 = geoPlatformService.getUserFolderDetail(idUserTestFolder2);
        Assert.assertEquals("Position of folder 2 - " + info, 5, userTestFolder2.getPosition());

        userTestRootFolderB = geoPlatformService.getUserFolderDetail(idUserRootFolderB);
        Assert.assertEquals("Position of root folder B - " + info, 4, userTestRootFolderB.getPosition());
        Assert.assertEquals("Number of descendant of root folder B - " + info, 3, userTestRootFolderB.getFolder().getNumberOfDescendants());

        userTestFolder3 = geoPlatformService.getUserFolderDetail(idUserTestFolder3);
        Assert.assertEquals("Position of folder 3 - " + info, 3, userTestFolder3.getPosition());

        userTestFolder4 = geoPlatformService.getUserFolderDetail(idUserTestFolder4);
        Assert.assertEquals("Position of folder 4 - " + info, 2, userTestFolder4.getPosition());

        userTestFolder5 = geoPlatformService.getUserFolderDetail(idUserTestFolder5);
        Assert.assertEquals("Position of folder 5 - " + info, 1, userTestFolder5.getPosition());
    }

    private void checkState(int[] positions, int[] numberOfDescendants, String info)
            throws ResourceNotFoundFault {
        Assert.assertEquals("Array positions must have exactly 7 elements", 7, positions.length);
        Assert.assertEquals("Array numberOfDescendants must have exactly 2 elements", 2, numberOfDescendants.length);

        userTestRootFolderA = geoPlatformService.getUserFolderDetail(idUserRootFolderA);
        Assert.assertEquals("Position of root folder A - " + info, positions[0], userTestRootFolderA.getPosition());
        Assert.assertEquals("Number of descendant of root folder A - " + info, numberOfDescendants[0], userTestRootFolderA.getFolder().getNumberOfDescendants());

        userTestFolder1 = geoPlatformService.getUserFolderDetail(idUserTestFolder1);
        Assert.assertEquals("Position of folder 1 - " + info, positions[1], userTestFolder1.getPosition());

        userTestFolder2 = geoPlatformService.getUserFolderDetail(idUserTestFolder2);
        Assert.assertEquals("Position of folder 2 - " + info, positions[2], userTestFolder2.getPosition());

        userTestRootFolderB = geoPlatformService.getUserFolderDetail(idUserRootFolderB);
        Assert.assertEquals("Position of root folder B - " + info, positions[3], userTestRootFolderB.getPosition());
        Assert.assertEquals("Number of descendant of root folder B - " + info, numberOfDescendants[1], userTestRootFolderB.getFolder().getNumberOfDescendants());

        userTestFolder3 = geoPlatformService.getUserFolderDetail(idUserTestFolder3);
        Assert.assertEquals("Position of folder 3 - " + info, positions[4], userTestFolder3.getPosition());

        userTestFolder4 = geoPlatformService.getUserFolderDetail(idUserTestFolder4);
        Assert.assertEquals("Position of folder 4 - " + info, positions[5], userTestFolder4.getPosition());

        userTestFolder5 = geoPlatformService.getUserFolderDetail(idUserTestFolder5);
        Assert.assertEquals("Position of folder 5 - " + info, positions[6], userTestFolder5.getPosition());
    }

    // Check if a UserFolder was eliminated
    private void checkUserFolderDeleted(long idFolder) {
        try {
            geoPlatformService.getUserFolderDetail(idFolder);
            Assert.fail("Folder with id \"" + idFolder + "\" was NOT deleted in cascade");
        } catch (Exception e) {
            logger.debug("\n*** Folder with id \"{}\" was deleted in cascade ***", idFolder);
        }
    }
}