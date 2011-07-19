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
package org.geosdi.geoplatform.services;

import com.googlecode.genericdao.search.Search;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.geosdi.geoplatform.core.dao.GPFolderDAO;
import org.geosdi.geoplatform.core.dao.GPLayerDAO;
import org.geosdi.geoplatform.core.dao.GPUserDAO;
import org.geosdi.geoplatform.core.dao.GPUserProjectsDAO;
import org.geosdi.geoplatform.core.model.GPFolder;
import org.geosdi.geoplatform.core.model.GPLayer;
import org.geosdi.geoplatform.core.model.GPUser;
import org.geosdi.geoplatform.exception.IllegalParameterFault;
import org.geosdi.geoplatform.exception.ResourceNotFoundFault;
import org.geosdi.geoplatform.request.PaginatedSearchRequest;
import org.geosdi.geoplatform.request.RequestById;
import org.geosdi.geoplatform.request.RequestByUserFolder;
import org.geosdi.geoplatform.request.SearchRequest;
import org.geosdi.geoplatform.responce.FolderDTO;
import org.geosdi.geoplatform.responce.collection.GPWebServiceMapData;
import org.geosdi.geoplatform.responce.collection.TreeFolderElements;
import org.springframework.security.acls.domain.BasePermission;

/**
 * @author giuseppe
 * 
 */
class FolderServiceImpl {

    final private static Logger logger = LoggerFactory.getLogger(FolderServiceImpl.class);
    // DAO
    private GPFolderDAO folderDao;
    private GPUserProjectsDAO userProjectsDao;
    private GPUserDAO userDao;
    private GPLayerDAO layerDao;

    //<editor-fold defaultstate="collapsed" desc="Setter methods">
    /**
     * @param folderDao
     *            the folderDao to set
     */
    public void setFolderDao(GPFolderDAO folderDao) {
        this.folderDao = folderDao;
    }

    /**
     * @param userProjectsDao
     *          the userProjectsDao to set
     */
    public void setUserProjectsDao(GPUserProjectsDAO userProjectsDao) {
        this.userProjectsDao = userProjectsDao;
    }

    /**
     * @param userDao
     *            the userDao to set
     */
    public void setUserDao(GPUserDAO userDao) {
        this.userDao = userDao;
    }

    /**
     * @param layerDao
     *            the layerDao to set
     */
    public void setLayerDao(GPLayerDAO layerDao) {
        this.layerDao = layerDao;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Folder">
    // ==========================================================================
    // === Folder
    // ==========================================================================   
    public long insertFolder(GPFolder folder) throws IllegalParameterFault {
        logger.trace("\n\t@@@ insertFolder @@@");
        this.checkFolder(folder); // TODO assert

        folderDao.persist(folder);

        return folder.getId();
    }

    public long updateFolder(GPFolder folder)
            throws ResourceNotFoundFault, IllegalParameterFault {
        logger.trace("\n\t@@@ updateFolder @@@");
        this.checkFolder(folder); // TODO assert

        GPFolder origFolder = folderDao.find(folder.getId());
        if (origFolder == null) {
            throw new ResourceNotFoundFault("Folder not found", folder.getId());
        }
        this.checkFolder(origFolder); // TODO assert

        // Update all properties (except the parent and project)
        origFolder.setName(folder.getName());
        origFolder.setPosition(folder.getPosition());
        origFolder.setNumberOfDescendants(folder.getNumberOfDescendants());
        origFolder.setChecked(folder.isChecked());

        folderDao.merge(origFolder);

        return origFolder.getId();
    }

    public boolean deleteFolder(long folderId)
            throws ResourceNotFoundFault, IllegalParameterFault {
        logger.trace("\n\t@@@ deleteFolder @@@");
        GPFolder folder = folderDao.find(folderId);
        if (folder == null) {
            throw new ResourceNotFoundFault("Folder not found", folderId);
        }
        this.checkFolder(folder); // TODO assert

        return folderDao.remove(folder);
    }

    public long saveAddedFolderAndTreeModifications(GPFolder folder, GPWebServiceMapData descendantsMapData)
            throws ResourceNotFoundFault, IllegalParameterFault {
        logger.trace("\n\t@@@ saveAddedFolderAndTreeModifications @@@");
        this.checkFolder(folder); // TODO assert
        if (folder.getParent() != null && descendantsMapData.getDescendantsMap().isEmpty()) { // TODO assert
            throw new IllegalParameterFault("descendantsMapData must have one or more entries if the folder has a parent");
        }

        if (folder.getParent() != null) {
            GPFolder parentFolder = folderDao.find(folder.getParent().getId());
            if (parentFolder == null) {
                throw new ResourceNotFoundFault("Folder parent not found", parentFolder.getParent().getId());
            }
            this.checkFolder(parentFolder); // TODO assert
            folder.setParent(parentFolder);
        }

        int newPosition = folder.getPosition();
        int increment = 1;
        // Shift positions
        folderDao.updatePositionsLowerBound(newPosition, increment);
        layerDao.updatePositionsLowerBound(newPosition, increment);

        folderDao.persist(folder);
        folderDao.updateAncestorsDescendants(descendantsMapData.getDescendantsMap());

        return folder.getId();
    }

    public boolean saveDeletedFolderAndTreeModifications(long folderId, GPWebServiceMapData descendantsMapData)
            throws ResourceNotFoundFault, IllegalParameterFault {
        GPFolder folder = folderDao.find(folderId);
        if (folder == null) {
            throw new ResourceNotFoundFault("UserFolder not found", folderId);
        }
        this.checkFolder(folder); // TODO assert

        int oldPosition = folder.getPosition();
        int decrement = folder.getNumberOfDescendants() + 1;

        boolean result = folderDao.remove(folder);

        // Shift positions (shift must be done only after removing folder)
        folderDao.updatePositionsLowerBound(oldPosition, -decrement);
        layerDao.updatePositionsLowerBound(oldPosition, -decrement);

        folderDao.updateAncestorsDescendants(descendantsMapData.getDescendantsMap());

        return result;
    }

    public boolean saveCheckStatusFolderAndTreeModifications(long folderId, boolean checked)
            throws ResourceNotFoundFault {
        GPFolder folder = folderDao.find(folderId);
        if (folder == null) {
            throw new ResourceNotFoundFault("Folder not found", folderId);
        }
        this.checkFolderLog(folder); // TODO assert

        return folderDao.persistCheckStatusFolder(folderId, checked);
    }

    /**
     * @param username
     * @param idFolderMoved
     * @param idNewParent: set conventionaly 0 if idFolderMoved is refer to a folder of root
     * @param newPosition
     * @param descendantsMapData
     * @return
     * @throws ResourceNotFoundFault 
     */
    public boolean saveDragAndDropFolderModifications(long idFolderMoved, long idNewParent,
            int newPosition, GPWebServiceMapData descendantsMapData) throws ResourceNotFoundFault {
        GPFolder folderMoved = folderDao.find(idFolderMoved);
        if (folderMoved == null) {
            throw new ResourceNotFoundFault("Folder not found", idFolderMoved);
        }
//        assert (folderMoved.getPosition() != newPosition) : "New Position must be NOT equal to Old Position";
        this.checkFolderLog(folderMoved); // TODO assert

        if (idNewParent != 0) {
            logger.trace("*** Folder will have a Parent");
            GPFolder folderParent = folderDao.find(idNewParent);
            if (folderParent == null) {
                throw new ResourceNotFoundFault("New Parent not found", idNewParent);
            }
            folderMoved.setParent(folderParent);
        } else {
            logger.trace("*** Folder will be the root folder");
            folderMoved.setParent(null);
        }

        int startFirstRange = 0, endFirstRange = 0;
//        System.out.println("### folderMoved.getPosition(): " + folderMoved.getPosition());
//        System.out.println("### newPosition: " + newPosition);
        if (folderMoved.getPosition() < newPosition) {// Drag & Drop to top
//            System.out.println("### Drag & Drop to top");
            startFirstRange = newPosition;
            endFirstRange = folderMoved.getPosition() + 1;
        } else if (folderMoved.getPosition() > newPosition) {// Drag & Drop to bottom
//            System.out.println("### Drag & Drop to bottom");
            startFirstRange = folderMoved.getPosition() - folderMoved.getNumberOfDescendants() - 1;
            endFirstRange = newPosition - folderMoved.getNumberOfDescendants();
        }
        int startSecondRange = folderMoved.getPosition();
        int endSecondRange = folderMoved.getPosition() - folderMoved.getNumberOfDescendants();
        int shiftValue = folderMoved.getNumberOfDescendants() + 1;

        Search search = new Search();
        search.addFilterGreaterOrEqual("position", endFirstRange).
                addFilterLessOrEqual("position", startFirstRange);
        search.addFilterEqual("project.id", folderMoved.getProject().getId());
        List<GPFolder> matchingFoldersFirstRange = folderDao.search(search);
        List<GPLayer> matchingLayersFirstRange = layerDao.search(search);

        search.clear();
        search.addFilterGreaterOrEqual("position", endSecondRange).
                addFilterLessOrEqual("position", startSecondRange);
        search.addFilterEqual("project.id", folderMoved.getProject().getId());
        List<GPFolder> matchingFoldersSecondRange = folderDao.search(search);
        List<GPLayer> matchingLayersSecondRange = layerDao.search(search);

        logger.trace("Range: " + startFirstRange + " - " + endFirstRange + " - "
                + startSecondRange + " - " + endSecondRange + " - ");
//        System.out.println("### matchingFoldersFirstRange.size(): " + matchingFoldersFirstRange.size());
//        System.out.println("### matchingLayersFirstRange.size(): " + matchingLayersFirstRange.size());
        int moveValue = matchingFoldersFirstRange.size() + matchingLayersFirstRange.size();

//        System.out.println("### startFirstRange: " + startFirstRange);
//        System.out.println("### endFirstRange: " + endFirstRange);
//        System.out.println("### startSecondRange: " + startSecondRange);
//        System.out.println("### endSecondRange: " + endSecondRange);
//        System.out.println("### shiftValue: " + shiftValue);
//        System.out.println("### moveValue: " + moveValue);

        if (folderMoved.getPosition() < newPosition) {// Drag & Drop to top
            this.executeFoldersModifications(matchingFoldersFirstRange, -shiftValue);
            this.executeLayersModifications(matchingLayersFirstRange, -shiftValue);
            this.executeFoldersModifications(matchingFoldersSecondRange, moveValue);
            this.executeLayersModifications(matchingLayersSecondRange, moveValue);
        } else if (folderMoved.getPosition() > newPosition) {// Drag & Drop to bottom
            this.executeFoldersModifications(matchingFoldersFirstRange, shiftValue);
            this.executeLayersModifications(matchingLayersFirstRange, shiftValue);
            this.executeFoldersModifications(matchingFoldersSecondRange, -moveValue);
            this.executeLayersModifications(matchingLayersSecondRange, -moveValue);
        }

        folderDao.merge(matchingFoldersFirstRange.toArray(new GPFolder[matchingFoldersFirstRange.size()]));
        folderDao.merge(matchingFoldersSecondRange.toArray(new GPFolder[matchingFoldersSecondRange.size()]));
        layerDao.merge(matchingLayersFirstRange.toArray(new GPLayer[matchingLayersFirstRange.size()]));
        layerDao.merge(matchingLayersSecondRange.toArray(new GPLayer[matchingLayersSecondRange.size()]));
        folderMoved.setPosition(newPosition);
        folderDao.merge(folderMoved);

        folderDao.updateAncestorsDescendants(descendantsMapData.getDescendantsMap());

        return true;
    }

    private void executeLayersModifications(List<GPLayer> elements, int value) {
        for (GPLayer layer : elements) {
            layer.setPosition(layer.getPosition() + value);
        }
    }

    private void executeFoldersModifications(List<GPFolder> elements, int value) {
        for (GPFolder folder : elements) {
            folder.setPosition(folder.getPosition() + value);
        }
    }

    public FolderDTO getShortFolder(long folderId) throws ResourceNotFoundFault {
        GPFolder folder = folderDao.find(folderId);
        if (folder == null) {
            throw new ResourceNotFoundFault("Folder not found", folderId);
        }
        this.checkFolderLog(folder); // TODO assert

        FolderDTO folderDTO = new FolderDTO(folder);
        return folderDTO;
    }

    public GPFolder getFolderDetail(long folderId) throws ResourceNotFoundFault {
        GPFolder folder = folderDao.find(folderId);
        if (folder == null) {
            throw new ResourceNotFoundFault("Folder not found", folderId);
        }

        return folder;
    }

    public List<FolderDTO> searchFolders(PaginatedSearchRequest searchRequest) {
        Search searchCriteria = new Search(GPFolder.class);
        searchCriteria.setMaxResults(searchRequest.getNum());
        searchCriteria.setPage(searchRequest.getPage());
        searchCriteria.addSortAsc("name");

        String like = searchRequest.getNameLike();
        if (like != null) {
            searchCriteria.addFilterILike("name", like);
        }

        List<GPFolder> foundFolder = folderDao.search(searchCriteria);
        return convertToFolderList(foundFolder);
    }

    public List<FolderDTO> getFolders() {
        List<GPFolder> found = folderDao.findAll();
        return convertToFolderList(found);
    }

    public long getFoldersCount(SearchRequest searchRequest) {
        Search searchCriteria = new Search(GPFolder.class);
        if (searchRequest != null && searchRequest.getNameLike() != null) {
            searchCriteria.addFilterILike("name", searchRequest.getNameLike());
        }

        return folderDao.count(searchCriteria);
    }

    public List<FolderDTO> getChildrenFoldersByRequest(RequestById request) {
        Search searchCriteria = new Search(GPFolder.class);

        searchCriteria.setMaxResults(request.getNum());
        searchCriteria.setPage(request.getPage());
        searchCriteria.addSortAsc("name");
        searchCriteria.addFilterEqual("parent.id", request.getId());

        List<GPFolder> foundFolder = folderDao.search(searchCriteria);
        return convertToFolderList(foundFolder);
    }

    public List<FolderDTO> getChildrenFolders(long folderId) {
        Search searchCriteria = new Search(GPFolder.class);

        searchCriteria.addSortAsc("name");
        searchCriteria.addFilterEqual("parent.id", folderId);

        List<GPFolder> foundFolder = folderDao.search(searchCriteria);
        return convertToFolderList(foundFolder);
    }

    public TreeFolderElements getChildrenElements(long folderId) {
        TreeFolderElements tree = new TreeFolderElements();

        Search searchCriteria = new Search(GPFolder.class);
        searchCriteria.addFilterEqual("parent.id", folderId);
        List<GPFolder> foundFolder = folderDao.search(searchCriteria);
        tree.addFolderCollection(convertToFolderList(foundFolder));

        searchCriteria = new Search(GPLayer.class);
        searchCriteria.addFilterEqual("folder.id", folderId);
        List<GPLayer> foundLayer = layerDao.search(searchCriteria);
        tree.addLayerCollection(foundLayer);

        return tree;
    }
    //</editor-fold>

//    //<editor-fold defaultstate="collapsed" desc="Folder / User">
//    // ==========================================================================
//    // === Folder / User
//    // ==========================================================================
//    public GPUserFolders getUserFolder(long userFolderId)
//            throws ResourceNotFoundFault {
//        GPUserFolders userFolder = userProjectsDao.find(userFolderId);
//        if (userFolder == null) {
//            throw new ResourceNotFoundFault("UserFolder not found", userFolderId);
//        }
//
//        return userFolder;
//    }
//
//    public GPUserFolders getUserFolderByUserAndFolderId(long userId, long folderId)
//            throws ResourceNotFoundFault {
//        GPUserFolders userFolder = userProjectsDao.find(userId, folderId);
//        if (userFolder == null) {
//            throw new ResourceNotFoundFault("UserFolder not found (userId="
//                    + userId + " - folderId=" + folderId + ")");
//        }
//
//        return userFolder;
//    }
//
//    public List<GPUserFolders> getUserFolderByUserId(long userId) {
//        return userProjectsDao.findByUserId(userId);
//    }
//
//    public List<GPUserFolders> getUserFolderByFolderId(long folderId) {
//        return userProjectsDao.findByFolderId(folderId);
//    }
//
//    public void setFolderShared(RequestById request)
//            throws ResourceNotFoundFault {
//        GPFolder folder = folderDao.find(request.getId());
//        if (folder == null) {
//            throw new ResourceNotFoundFault("Folder not found", request.getId());
//        }
//
//        folder.setShared(true);
////        folder.setOwner(null);
//        folderDao.merge(folder);
//    }
//
//    public boolean setFolderOwner(RequestByUserFolder request, boolean force)
//            throws ResourceNotFoundFault {
//        GPFolder folder = folderDao.find(request.getFolderId());
//        if (folder == null) {
//            throw new ResourceNotFoundFault("Folder not found",
//                    request.getFolderId());
//        }
//
//        GPUser user = userDao.find(request.getUserId());
//        if (user == null) {
//            throw new ResourceNotFoundFault("User not found",
//                    request.getUserId());
//        }
//
//        // TODO: implement the logic described in this method's javadoc
//
//        folder.setShared(false);
////        folder.setOwner(user);
//        folderDao.merge(folder);
//
//        return true;
//    }
//
//    /**
//     * 
//     * @param request
//     * @return only root folders owned by user
//     */
//    public List<FolderDTO> getFoldersByRequest(RequestById request) {
//        Search searchCriteria = new Search(GPFolder.class);
//
//        searchCriteria.setMaxResults(request.getNum());
//        searchCriteria.setPage(request.getPage());
//        searchCriteria.addSortAsc("position");
//        searchCriteria.addFilterEqual("user.id", request.getId());
//        searchCriteria.addFilterEqual("permissionMask", BasePermission.ADMINISTRATION.getMask());
//        searchCriteria.addFilterNull("parent.id");
//
//        List<GPFolder> foundUserFolders = folderDao.search(searchCriteria);
//        return convertToFolderList(foundUserFolders);
//    }
//
//    /**
//     * 
//     * @param userId
//     * @return only root folders owned by user
//     */
//    public List<FolderDTO> getFoldersByUserId(long userId) {
//        Search searchCriteria = new Search(GPUserFolders.class);
//
//        searchCriteria.addSortAsc("position");
//        searchCriteria.addFilterEqual("user.id", userId);
//        searchCriteria.addFilterEqual("permissionMask", BasePermission.ADMINISTRATION.getMask());
//        searchCriteria.addFilterNull("parent.id");
//
//        List<GPUserFolders> foundUserFolders = userProjectsDao.search(searchCriteria);
//        return convertToUserFolderList(foundUserFolders);
//    }
//
//    /**
//     * 
//     * @param request
//     * @return count only root folders owned by user
//     */
//    public long getUserFoldersCount(long userId) {
//        Search searchCriteria = new Search(GPUserFolders.class);
//
//        searchCriteria.addFilterEqual("user.id", userId);
//        searchCriteria.addFilterEqual("permissionMask", BasePermission.ADMINISTRATION.getMask());
//        searchCriteria.addFilterNull("parent.id");
//
//        return userProjectsDao.count(searchCriteria);
//    }
//
//    // TODO Check
//    /**
//     * 
//     * @param request
//     * @return folders owned by user and shared with his
//     */
//    public List<FolderDTO> getAllUserFolders(RequestById request) {
//        Search searchCriteria = new Search(GPUserFolders.class);
//
//        searchCriteria.setMaxResults(request.getNum());
//        searchCriteria.setPage(request.getPage());
////        searchCriteria.addSortAsc("folder.name");
//        searchCriteria.addFilterEqual("user.id", request.getId());
//        searchCriteria.addFilterNull("parent.id");
//
//        List<GPUserFolders> foundUserFolders = userProjectsDao.search(searchCriteria);
//        return convertToUserFolderList(foundUserFolders);
//    }
//
//    // TODO Check
//    /**
//     * 
//     * @param userId
//     * @return folders owned by user and shared with his
//     */
//    public List<FolderDTO> getAllUserFoldersByUserId(long userId) {
//        Search searchCriteria = new Search(GPUserFolders.class);
//
////        searchCriteria.addSortAsc("folder.name");
//        searchCriteria.addFilterEqual("user.id", userId);
//        searchCriteria.addFilterNull("parent.id");
//
//        List<GPUserFolders> foundUserFolders = userProjectsDao.search(searchCriteria);
//        return convertToUserFolderList(foundUserFolders);
//    }
//
//    // TODO Check
//    /**
//     * 
//     * @param userId
//     * @return count all folders and sub-folders owned by user and shared with his
//     */
//    public int getAllUserFoldersCount(long userId) {
//        Search searchCriteria = new Search(GPUserFolders.class);
//
//        searchCriteria.addFilterEqual("user.id", userId);
////        searchCriteria.addFilterNull("parent.id");
//
//        return userProjectsDao.count(searchCriteria);
//    }
    //</editor-fold>
    // TODO ? DEL ?
    private List<FolderDTO> convertToFolderList(List<GPFolder> folderList) {
        List<FolderDTO> foldersDTO = new ArrayList<FolderDTO>(folderList.size());
        for (GPFolder folder : folderList) {
            FolderDTO folderDTO = new FolderDTO(folder);
            foldersDTO.add(folderDTO);
        }

        Collections.sort(foldersDTO);

        return foldersDTO;
    }

    // TODO assert
    private void checkFolder(GPFolder folder) throws IllegalParameterFault {
        if (folder.getName() == null) { // TODO assert
            throw new IllegalParameterFault("Folder \"name\" must be NOT NULL");
        }
        if (folder.getNumberOfDescendants() < 0) { // TODO assert
            throw new IllegalParameterFault("Folder \"numberOfDescendants\" must be greater or equal 0");
        }
        if (folder.getProject() == null) { // TODO assert
            throw new IllegalParameterFault("Folder \"project\" must be NOT NULL");
        }
    }

    // TODO assert
    private void checkFolderLog(GPFolder folder) {
        try {
            this.checkFolder(folder);
        } catch (IllegalParameterFault ex) {
            logger.error(ex.getMessage());
        }
    }
}
