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
package org.geosdi.geoplatform.services;

import com.googlecode.genericdao.search.Search;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.geosdi.geoplatform.core.dao.GPFolderDAO;
import org.geosdi.geoplatform.core.dao.GPLayerDAO;
import org.geosdi.geoplatform.core.dao.GPUserDAO;
import org.geosdi.geoplatform.core.dao.GPUserFoldersDAO;
import org.geosdi.geoplatform.core.model.GPFolder;
import org.geosdi.geoplatform.core.model.GPLayer;
import org.geosdi.geoplatform.core.model.GPUser;
import org.geosdi.geoplatform.core.model.GPUserFolders;
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
    private GPUserFoldersDAO userFoldersDao;
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
     * @param userFoldersDao
     *          the userFoldersDao to set
     */
    public void setUserFoldersDao(GPUserFoldersDAO userFoldersDao) {
        this.userFoldersDao = userFoldersDao;
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
    public long insertUserFolder(GPUserFolders userFolder) throws IllegalParameterFault {
        logger.trace("\n\t@@@ insertUserFolder @@@");
        this.checkUserFolder(userFolder); // TODO assert

        GPFolder folder = userFolder.getFolder();
        folderDao.persist(folder);

        userFoldersDao.persist(userFolder);

        return userFolder.getId();
    }

    public long updateUserFolder(GPUserFolders userFolder)
            throws ResourceNotFoundFault, IllegalParameterFault {
        logger.trace("\n\t@@@ updateUserFolder @@@");
        this.checkUserFolder(userFolder); // TODO assert

        GPUserFolders origUserFolder = userFoldersDao.find(userFolder.getId());
        if (origUserFolder == null) {
            throw new ResourceNotFoundFault("UserFolder not found", userFolder.getId());
        }
        this.checkUserFolder(origUserFolder); // TODO assert

        // TODO FIX: Permission
//        if (!userFolder.getUser().equals(origUserFolder.getUser())) {
//            throw new IllegalParameterFault("User retrieved of UserFolder NOT match");
//        }
        if (!userFolder.getFolder().equals(origUserFolder.getFolder())) {
            throw new IllegalParameterFault("Folder retrieved of UserFolder NOT match");
        }

//        origUserFolder.setPermissionMask(userFolder.getPermissionMask()); // TODO ?!?
        origUserFolder.setAliasName(userFolder.getAliasName());
        origUserFolder.setPosition(userFolder.getPosition());
        origUserFolder.setChecked(userFolder.isChecked());
        if (userFolder.getParent() != null
                && (origUserFolder.getParent() == null
                || !userFolder.getParent().equals(origUserFolder.getParent()))) {
            logger.trace("*** UserFolder to UPDATE has a different parent ***");

            GPUserFolders newParent = userFoldersDao.find(userFolder.getParent().getId());
            if (newParent == null) {
                throw new ResourceNotFoundFault("New Parent UserFolder not found", userFolder.getParent().getId());
            }
            this.checkUserFolder(newParent); // TODO assert

            // If the parent is property of user
            if (!newParent.getUser().equals(origUserFolder.getUser())) {
                throw new IllegalParameterFault("New Parent UserFolder has a differente User");
            }
            // If user has the permission on parent UserFolder
            if (newParent.getPermissionMask() >= BasePermission.WRITE.getMask()) {
                origUserFolder.setParent(newParent);
            }
        }

        // If the user is the owner of the Folder
        if (origUserFolder.getPermissionMask() == BasePermission.ADMINISTRATION.getMask()) {
            logger.trace("+++ User \"" + userFolder.getUser().getUsername() + "\" is the owner of the Folder \""
                    + origUserFolder.getFolder().getName() + "\" +++");

            GPFolder origFolder = folderDao.find(userFolder.getFolder().getId());
            if (origFolder == null) {
                throw new ResourceNotFoundFault("Folder not found", userFolder.getFolder().getId());
            }

            // Update all properties of the Folder
            origFolder.setName(userFolder.getFolder().getName());
            origFolder.setNumberOfDescendants(userFolder.getFolder().getNumberOfDescendants());
            origFolder.setShared(userFolder.getFolder().isShared());

            folderDao.merge(origFolder);

        }
        userFoldersDao.merge(origUserFolder);

        return origUserFolder.getId();
    }

    public boolean deleteUserFolder(long userFolderId)
            throws ResourceNotFoundFault, IllegalParameterFault {
        logger.trace("\n\t@@@ deleteUserFolder @@@");
        GPUserFolders userFolder = userFoldersDao.find(userFolderId);
        if (userFolder == null) {
            throw new ResourceNotFoundFault("UserFolder not found", userFolderId);
        }
        this.checkUserFolder(userFolder); // TODO assert

        // If user is the owner of the Folder
        if (userFolder.getPermissionMask() == BasePermission.ADMINISTRATION.getMask()) {
            // One or more userFolders should be deleted by cascading
            return folderDao.remove(userFolder.getFolder());
        } else {
            return userFoldersDao.remove(userFolder);
        }
    }

    public long saveAddedFolderAndTreeModifications(GPUserFolders userFolder, GPWebServiceMapData descendantsMapData)
            throws ResourceNotFoundFault, IllegalParameterFault {
        logger.trace("\n\t@@@ saveAddedFolderAndTreeModifications @@@");
        this.checkUserFolder(userFolder); // TODO assert
        if (userFolder.getParent() != null && descendantsMapData.getDescendantsMap().isEmpty()) { // TODO assert
            throw new IllegalParameterFault("descendantsMapData must have one or more entries if the folder has a parent");
        }

        // TODO verify problems when saving a folder with owner from interface
        GPUser user = userDao.findByUsername(userFolder.getUser().getUsername());
        if (user == null) {
            throw new ResourceNotFoundFault("User " + userFolder.getUser().getUsername() + " not found");
        }
        userFolder.setUser(user);

        if (userFolder.getParent() != null) {
            GPUserFolders parentUserFolder = userFoldersDao.find(userFolder.getParent().getId());
            if (parentUserFolder == null) {
                throw new ResourceNotFoundFault("Folder parent not found", parentUserFolder.getParent().getId());
            }
            this.checkUserFolder(parentUserFolder); // TODO assert
            userFolder.setParent(parentUserFolder);
        }

        int newPosition = userFolder.getPosition();
        int increment = 1;
        // Shift positions
        userFoldersDao.updatePositionsLowerBound(newPosition, increment);
        layerDao.updatePositionsLowerBound(newPosition, increment);

        folderDao.persist(userFolder.getFolder());
        userFoldersDao.persist(userFolder);

        folderDao.updateAncestorsDescendants(descendantsMapData.getDescendantsMap());

        return userFolder.getId();
    }

    public boolean saveDeletedFolderAndTreeModifications(long userFolderId, GPWebServiceMapData descendantsMapData)
            throws ResourceNotFoundFault, IllegalParameterFault {
        GPUserFolders userFolder = userFoldersDao.find(userFolderId);
        if (userFolder == null) {
            throw new ResourceNotFoundFault("UserFolder not found", userFolderId);
        }
        this.checkUserFolder(userFolder); // TODO assert

        int oldPosition = userFolder.getPosition();
        int decrement = userFolder.getFolder().getNumberOfDescendants() + 1;

        boolean result = userFoldersDao.remove(userFolder);

        // Shift positions (shift must be done only after removing folder)
        userFoldersDao.updatePositionsLowerBound(oldPosition, -decrement);
        layerDao.updatePositionsLowerBound(oldPosition, -decrement);

        folderDao.updateAncestorsDescendants(descendantsMapData.getDescendantsMap());

        return result;
    }

    public boolean saveCheckStatusFolderAndTreeModifications(long userFolderId, boolean checked)
            throws ResourceNotFoundFault {
        GPUserFolders userFolder = userFoldersDao.find(userFolderId);
        if (userFolder == null) {
            throw new ResourceNotFoundFault("UserFolder not found", userFolderId);
        }
        try {
            this.checkUserFolder(userFolder); // TODO assert
        } catch (IllegalParameterFault ex) {
            logger.error("\n*** " + ex.getMessage() + " ***");
        }

        return userFoldersDao.persistCheckStatusFolder(userFolderId, checked);
    }

    /**
     * @param username
     * @param idUserFolderMoved
     * @param idNewParent: set conventionaly 0 if idFolderMoved is refer to a folder of root
     * @param newPosition
     * @param descendantsMapData
     * @return
     * @throws ResourceNotFoundFault 
     */
    public boolean saveDragAndDropFolderModifications(String username, long idUserFolderMoved, long idNewParent,
            int newPosition, GPWebServiceMapData descendantsMapData) throws ResourceNotFoundFault {
        GPUser user = userDao.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundFault("User with username \"" + username + "\" not found");
        }

        GPUserFolders folderMoved = userFoldersDao.find(idUserFolderMoved);
        if (folderMoved == null) {
            throw new ResourceNotFoundFault("UserFolder not found", idUserFolderMoved);
        }
        folderMoved.setUser(user);
//        assert (folderMoved.getPosition() != newPosition) : "New Position must be NOT equal to Old Position";

        if (idNewParent != 0) {
            logger.trace("*** Folder will have a Parent");
            GPUserFolders folderParent = userFoldersDao.find(idNewParent);
            if (folderParent == null) {
                throw new ResourceNotFoundFault("New Parent not found", idNewParent);
            }
            folderMoved.setParent(folderParent);
        } else {
            logger.trace("*** Folder will be a root folder");
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
            startFirstRange = folderMoved.getPosition() - folderMoved.getFolder().getNumberOfDescendants() - 1;
            endFirstRange = newPosition - folderMoved.getFolder().getNumberOfDescendants();
        }
        int startSecondRange = folderMoved.getPosition();
        int endSecondRange = folderMoved.getPosition() - folderMoved.getFolder().getNumberOfDescendants();
        int shiftValue = folderMoved.getFolder().getNumberOfDescendants() + 1;

        Search search = new Search();
        search.addFilterGreaterOrEqual("position", endFirstRange).
                addFilterLessOrEqual("position", startFirstRange);
        search.addFilterEqual("user.id", user.getId());
        List<GPUserFolders> matchingFoldersFirstRange = userFoldersDao.search(search);
        search.removeFiltersOnProperty("user.id");
        search.addFilterEqual("userFolder.user.id", user.getId());
        List<GPLayer> matchingLayersFirstRange = layerDao.search(search);

        search.clear();
        search.addFilterGreaterOrEqual("position", endSecondRange).
                addFilterLessOrEqual("position", startSecondRange);
        search.addFilterEqual("user.id", user.getId());
        List<GPUserFolders> matchingFoldersSecondRange = userFoldersDao.search(search);

        search.removeFiltersOnProperty("user.id");
        search.addFilterEqual("userFolder.user.id", user.getId());
        List<GPLayer> matchingLayersSecondRange = layerDao.search(search);
        System.out.println("Range: " + startFirstRange + " - " + endFirstRange + " - "
                + startSecondRange + " - " + endSecondRange + " - ");
//        System.out.println("### matchingFoldersFirstRange.size(): " + matchingFoldersFirstRange.size());
//        System.out.println("### matchingLayersFirstRange.size(): " + matchingLayersFirstRange.size());
        System.out.println((matchingLayersFirstRange.isEmpty() ? "lista vuota" : matchingLayersFirstRange.get(0)));
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

        userFoldersDao.merge(matchingFoldersFirstRange.toArray(new GPUserFolders[matchingFoldersFirstRange.size()]));
        userFoldersDao.merge(matchingFoldersSecondRange.toArray(new GPUserFolders[matchingFoldersSecondRange.size()]));
        layerDao.merge(matchingLayersFirstRange.toArray(new GPLayer[matchingLayersFirstRange.size()]));
        layerDao.merge(matchingLayersSecondRange.toArray(new GPLayer[matchingLayersSecondRange.size()]));
        folderMoved.setPosition(newPosition);
        userFoldersDao.merge(folderMoved);

        folderDao.updateAncestorsDescendants(descendantsMapData.getDescendantsMap());

        return true;
    }

    private void executeLayersModifications(List<GPLayer> elements, int value) {
        for (GPLayer gPLayer : elements) {
            gPLayer.setPosition(gPLayer.getPosition() + value);
            System.out.println("New position assignet to: " + gPLayer.getName() + " posiz: " + gPLayer.getPosition());
        }
    }

    private void executeFoldersModifications(List<GPUserFolders> elements, int value) {
        for (GPUserFolders userFolder : elements) {
            userFolder.setPosition(userFolder.getPosition() + value);
            System.out.println("New position assignet to: " + userFolder.getFolder().getName() + " posiz: " + userFolder.getPosition());
        }
    }

    public FolderDTO getShortFolder(long userFolderId) throws ResourceNotFoundFault {
        GPUserFolders userFolder = userFoldersDao.find(userFolderId);
        if (userFolder == null) {
            throw new ResourceNotFoundFault("UserFolder not found", userFolderId);
        }

        FolderDTO folderDTO = new FolderDTO(userFolder);
        return folderDTO;
    }

    public GPUserFolders getUserFolderDetail(long userFolderId) throws ResourceNotFoundFault {
        GPUserFolders userFolder = userFoldersDao.find(userFolderId);
        if (userFolder == null) {
            throw new ResourceNotFoundFault("UserFolder not found", userFolderId);
        }

        return userFolder;
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
        Search searchCriteria = new Search(GPUserFolders.class);

        searchCriteria.setMaxResults(request.getNum());
        searchCriteria.setPage(request.getPage());
        searchCriteria.addSortAsc("folder.name");
        searchCriteria.addFilterEqual("parent.id", request.getId());

        List<GPUserFolders> foundFolder = userFoldersDao.search(searchCriteria);
        return convertToUserFolderList(foundFolder);
    }

    public List<FolderDTO> getChildrenFolders(long userFolderId) {
        Search searchCriteria = new Search(GPUserFolders.class);

        searchCriteria.addSortAsc("folder.name");
        searchCriteria.addFilterEqual("parent.id", userFolderId);

        List<GPUserFolders> foundFolder = userFoldersDao.search(searchCriteria);
        return convertToUserFolderList(foundFolder);
    }

    public TreeFolderElements getChildrenElements(long userFolderId) {
        TreeFolderElements tree = new TreeFolderElements();

        Search searchCriteria = new Search(GPUserFolders.class);
        searchCriteria.addFilterEqual("parent.id", userFolderId);
        List<GPUserFolders> foundFolder = userFoldersDao.search(searchCriteria);
        tree.addFolderCollection(convertToUserFolderList(foundFolder));

        searchCriteria = new Search(GPLayer.class);
        searchCriteria.addFilterEqual("userFolder.id", userFolderId);
        List<GPLayer> foundLayer = layerDao.search(searchCriteria);
        tree.addLayerCollection(foundLayer);

        return tree;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Folder / User">
    // ==========================================================================
    // === Folder / User
    // ==========================================================================
    public GPUserFolders getUserFolder(long userFolderId)
            throws ResourceNotFoundFault {
        GPUserFolders userFolder = userFoldersDao.find(userFolderId);
        if (userFolder == null) {
            throw new ResourceNotFoundFault("UserFolder not found", userFolderId);
        }

        return userFolder;
    }

    public GPUserFolders getUserFolderByUserAndFolderId(long userId, long folderId)
            throws ResourceNotFoundFault {
        GPUserFolders userFolder = userFoldersDao.find(userId, folderId);
        if (userFolder == null) {
            throw new ResourceNotFoundFault("UserFolder not found (userId="
                    + userId + " - folderId=" + folderId + ")");
        }

        return userFolder;
    }

    public List<GPUserFolders> getUserFolderByUserId(long userId) {
        return userFoldersDao.findByUserId(userId);
    }

    public List<GPUserFolders> getUserFolderByFolderId(long folderId) {
        return userFoldersDao.findByFolderId(folderId);
    }

    public void setFolderShared(RequestById request)
            throws ResourceNotFoundFault {
        GPFolder folder = folderDao.find(request.getId());
        if (folder == null) {
            throw new ResourceNotFoundFault("Folder not found", request.getId());
        }

        folder.setShared(true);
//        folder.setOwner(null);
        folderDao.merge(folder);
    }

    public boolean setFolderOwner(RequestByUserFolder request, boolean force)
            throws ResourceNotFoundFault {
        GPFolder folder = folderDao.find(request.getFolderId());
        if (folder == null) {
            throw new ResourceNotFoundFault("Folder not found",
                    request.getFolderId());
        }

        GPUser user = userDao.find(request.getUserId());
        if (user == null) {
            throw new ResourceNotFoundFault("User not found",
                    request.getUserId());
        }

        // TODO: implement the logic described in this method's javadoc

        folder.setShared(false);
//        folder.setOwner(user);
        folderDao.merge(folder);

        return true;
    }

    /**
     * 
     * @param request
     * @return only root folders owned by user
     */
    public List<FolderDTO> getUserFoldersByRequest(RequestById request) {
        Search searchCriteria = new Search(GPUserFolders.class);

        searchCriteria.setMaxResults(request.getNum());
        searchCriteria.setPage(request.getPage());
        searchCriteria.addSortAsc("position");
        searchCriteria.addFilterEqual("user.id", request.getId());
        searchCriteria.addFilterEqual("permissionMask", BasePermission.ADMINISTRATION.getMask());
        searchCriteria.addFilterNull("parent.id");

        List<GPUserFolders> foundUserFolders = userFoldersDao.search(searchCriteria);
        return convertToUserFolderList(foundUserFolders);
    }

    /**
     * 
     * @param userId
     * @return only root folders owned by user
     */
    public List<FolderDTO> getUserFoldersByUserId(long userId) {
        Search searchCriteria = new Search(GPUserFolders.class);

        searchCriteria.addSortAsc("position");
        searchCriteria.addFilterEqual("user.id", userId);
        searchCriteria.addFilterEqual("permissionMask", BasePermission.ADMINISTRATION.getMask());
        searchCriteria.addFilterNull("parent.id");

        List<GPUserFolders> foundUserFolders = userFoldersDao.search(searchCriteria);
        return convertToUserFolderList(foundUserFolders);
    }

    /**
     * 
     * @param request
     * @return count only root folders owned by user
     */
    public long getUserFoldersCount(long userId) {
        Search searchCriteria = new Search(GPUserFolders.class);

        searchCriteria.addFilterEqual("user.id", userId);
        searchCriteria.addFilterEqual("permissionMask", BasePermission.ADMINISTRATION.getMask());
        searchCriteria.addFilterNull("parent.id");

        return userFoldersDao.count(searchCriteria);
    }

    // TODO Check
    /**
     * 
     * @param request
     * @return folders owned by user and shared with his
     */
    public List<FolderDTO> getAllUserFolders(RequestById request) {
        Search searchCriteria = new Search(GPUserFolders.class);

        searchCriteria.setMaxResults(request.getNum());
        searchCriteria.setPage(request.getPage());
//        searchCriteria.addSortAsc("folder.name");
        searchCriteria.addFilterEqual("user.id", request.getId());
        searchCriteria.addFilterNull("parent.id");

        List<GPUserFolders> foundUserFolders = userFoldersDao.search(searchCriteria);
        return convertToUserFolderList(foundUserFolders);
    }

    // TODO Check
    /**
     * 
     * @param userId
     * @return folders owned by user and shared with his
     */
    public List<FolderDTO> getAllUserFoldersByUserId(long userId) {
        Search searchCriteria = new Search(GPUserFolders.class);

//        searchCriteria.addSortAsc("folder.name");
        searchCriteria.addFilterEqual("user.id", userId);
        searchCriteria.addFilterNull("parent.id");

        List<GPUserFolders> foundUserFolders = userFoldersDao.search(searchCriteria);
        return convertToUserFolderList(foundUserFolders);
    }

    // TODO Check
    /**
     * 
     * @param userId
     * @return count all folders and sub-folders owned by user and shared with his
     */
    public int getAllUserFoldersCount(long userId) {
        Search searchCriteria = new Search(GPUserFolders.class);

        searchCriteria.addFilterEqual("user.id", userId);
//        searchCriteria.addFilterNull("parent.id");

        return userFoldersDao.count(searchCriteria);
    }
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

    private List<FolderDTO> convertToUserFolderList(List<GPUserFolders> userFolderList) {
        List<FolderDTO> foldersDTO = new ArrayList<FolderDTO>(userFolderList.size());
        for (GPUserFolders userFolder : userFolderList) {
            FolderDTO folderDTO = new FolderDTO(userFolder);
            foldersDTO.add(folderDTO);
        }

        Collections.sort(foldersDTO);

        return foldersDTO;
    }

    // TODO assert
    private void checkUserFolder(GPUserFolders userFolder) throws IllegalParameterFault {
        if (userFolder == null) { // TODO assert
            throw new IllegalParameterFault("UserFolder must be NOT NULL");
        }
        if (userFolder.getUser() == null) { // TODO assert
            throw new IllegalParameterFault("User of UserFolder must be NOT NULL");
        }
        if (userFolder.getFolder() == null) { // TODO assert
            throw new IllegalParameterFault("Folder of UserFolder must be NOT NULL");
        }
    }
}
