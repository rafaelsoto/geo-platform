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

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.geosdi.geoplatform.core.dao.GPFolderDAO;
import org.geosdi.geoplatform.core.dao.GPLayerDAO;
import org.geosdi.geoplatform.core.dao.GPUserDAO;
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
import org.geosdi.geoplatform.responce.collection.FolderList;
import org.geosdi.geoplatform.responce.collection.GPWebServiceMapData;
import org.geosdi.geoplatform.responce.collection.TreeFolderElements;

/**
 * @author giuseppe
 * 
 */
class FolderServiceImpl {

    final private static Logger logger = LoggerFactory.getLogger(FolderServiceImpl.class);
    //
    private GPFolderDAO folderDao;
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
    public long insertFolder(GPFolder folder) {
        folderDao.persist(folder);
        return folder.getId();
    }

    public long updateFolder(GPFolder folder)
            throws ResourceNotFoundFault, IllegalParameterFault {
        GPFolder orig = folderDao.find(folder.getId());
        if (orig == null) {
            throw new ResourceNotFoundFault("Folder not found", folder.getId());
        }
        // Update all properties (except the owner)
        orig.setParent(folder.getParent());
        orig.setName(folder.getName());
        orig.setPosition(folder.getPosition());
        orig.setShared(folder.isShared());
        orig.setChecked(folder.isChecked());
        orig.setNumberOfDescendants(folder.getNumberOfDescendants());

        folderDao.merge(orig);
        return orig.getId();
    }

    public boolean deleteFolder(RequestById request)
            throws ResourceNotFoundFault, IllegalParameterFault {
        GPFolder folder = folderDao.find(request.getId());

        if (folder == null) {
            throw new ResourceNotFoundFault("Folder not found", request.getId());
        }

        // data on ancillary tables should be deleted by cascading
        return folderDao.remove(folder);
    }

    // Add @Transaction ?
    public long saveAddedFolderAndTreeModifications(GPFolder folder, GPWebServiceMapData descendantsMapData)
            throws ResourceNotFoundFault {
        assert ((folder.getOwner() == null && folder.getParent() != null)
                || (folder.getOwner() != null && folder.getParent() == null)) :
                this.getClass().getCanonicalName() + " on saveFolderAndTreeModifications - "
                + "Illegal argument exception: folder must have or Owner or Parent NOT NULL";
        assert ((folder.getOwner() != null && descendantsMapData.getDescendantsMap().isEmpty())
                || (folder.getParent() != null && !descendantsMapData.getDescendantsMap().isEmpty())) :
                "descendantsMapData must have one or more entries if the folder has a parent";

        if (folder.getOwner() != null) {
            // TODO verify problems when saving a folder with owner from interface
            GPUser user = userDao.findByUsername(folder.getOwner().getUsername());
            if (user == null) {
                throw new ResourceNotFoundFault("User " + folder.getOwner().getUsername() + " not found");
            }

            folder.setOwner(user);
        } else {
            GPFolder folderParent = folderDao.find(folder.getParent().getId());
            if (folderParent == null) {
                throw new ResourceNotFoundFault("Folder parent not found", folder.getParent().getId());
            }

            folder.setParent(folderParent);
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

    // Add @Transaction ?
    public boolean saveDeletedFolderAndTreeModifications(long folderId, GPWebServiceMapData descendantsMapData)
            throws ResourceNotFoundFault, IllegalParameterFault {
        GPFolder folder = folderDao.find(folderId);
        if (folder == null) {
            throw new ResourceNotFoundFault("Folder not found", folderId);
        }

        int oldPosition = folder.getPosition();
        int decrement = folder.getNumberOfDescendants() + 1;

        boolean result = folderDao.remove(folder);

        // Shift positions (shift must be done only after removing folder)
        folderDao.updatePositionsLowerBound(oldPosition, -decrement);
        layerDao.updatePositionsLowerBound(oldPosition, -decrement);

        folderDao.updateAncestorsDescendants(descendantsMapData.getDescendantsMap());

        return result;
    }

    public boolean saveCheckStatusFolderAndTreeModifications(long folderId, boolean isChecked)
            throws ResourceNotFoundFault {
        GPFolder folder = folderDao.find(folderId);
        if (folder == null) {
            throw new ResourceNotFoundFault("Folder not found", folderId);
        }

        return folderDao.persistCheckStatusFolder(folderId, isChecked);
    }

    // Add @Transaction ?
    /**
     * 
     * @param idFolderMoved
     * @param idNewParent: set conventionaly 0 if idFolderMoved is refer to a folder of root
     * @param owner
     * @param newPosition
     * @param descendantsMapData
     * @return
     * @throws ResourceNotFoundFault 
     */
    public boolean saveDragAndDropFolderModifications(long idFolderMoved, long idNewParent, GPUser owner,
            int newPosition, GPWebServiceMapData descendantsMapData) throws ResourceNotFoundFault {
        GPFolder folderMoved = folderDao.find(idFolderMoved);
        if (folderMoved == null) {
            throw new ResourceNotFoundFault("Folder with id " + idFolderMoved + " not found");
        }
        assert (folderMoved.getPosition() != newPosition) : "New Position must be NOT equal to Old Position";

        // Iff there isn't a folder of root
        if (idNewParent != 0) {
            GPFolder folderParent = folderDao.find(idNewParent);
            if (folderParent == null) { // Drag & Drop to the root
                logger.trace("***owner");
                GPUser ownerDetail = userDao.findByUsername(owner.getUsername());
                assert (ownerDetail != null) : "Unable to find user from DB with username " + owner.getUsername();
                folderMoved.setOwner(ownerDetail);
            } else { // Drag & Drop to a folder                
                logger.trace("***parent");
                folderMoved.setParent(folderParent);
            }
        }

        // Shift range of folders with ID from oldPosition to newPosition
        // (exclude descentants)
        int oldPosition = folderMoved.getPosition();
        int descendants = folderMoved.getNumberOfDescendants();
        int beginPosition = -1, endPosition = -1, delta = 0;
        if (oldPosition < newPosition) { // Drag & Drop to up
            logger.trace("***oldPosition < newPosition");
            beginPosition = oldPosition + 1;
            endPosition = newPosition;
            delta = -(descendants + 1);
        } else if (oldPosition > newPosition) { // Drag & Drop to down
            logger.trace("***oldPosition > newPosition");
            beginPosition = newPosition;
            endPosition = oldPosition - 1 - descendants;
            delta = descendants + 1;
        }
        logger.debug("\n*** beginPosition = {} | endPosition = {} | delta = {} ***",
                new Object[]{beginPosition, endPosition, delta});

        // Shift positions
        boolean resultUpdates = true;
        if (delta != 0) { // DEL if
            assert (descendants >= 0) : "descendants < 0";
            if (descendants == 0) {
                logger.trace("***descendants == 0");
                boolean resultUpdatesOfLayers = layerDao.updatePositionsRange(beginPosition, endPosition, delta);
                boolean resultUpdatesOfFolders = folderDao.updatePositionsRange(beginPosition, endPosition, delta);

                assert (resultUpdatesOfLayers) : "Errors occured when updating position of layers";
                assert (resultUpdatesOfFolders) : "Errors occured when updating position of folders";

                resultUpdates = resultUpdates && resultUpdatesOfLayers && resultUpdatesOfFolders;
            } else { // Shift descendants positions
                logger.trace("***descendants > 0");
                int beginPositionDescendants = oldPosition - descendants;
                int endPositionDescendants = oldPosition - 1;
                int deltaDescendants = -delta;
                logger.debug("\n*** beginPositionDescendants = {} | endPositionDescendants = {} | deltaDescendants = {} ***",
                        new Object[]{beginPositionDescendants, endPositionDescendants, deltaDescendants});
                //
                // TODO !!! shift folders and layers simultaneous
//                boolean resultUpdatesOfSubLayers = layerDao.updatePositionsRangeInOppositeWay(
//                        beginPosition, endPosition,
//                        beginPositionDescendants, endPositionDescendants, delta);
                boolean resultUpdatesOfSubFolders = folderDao.updatePositionsRangeInOppositeWay(
                        beginPosition, endPosition,
                        beginPositionDescendants, endPositionDescendants, delta);

//                assert (resultUpdatesOfSubLayers) : "Errors occured when updating position of sub-layers";
                assert (resultUpdatesOfSubFolders) : "Errors occured when updating position of sub-folders";

//                resultUpdates = resultUpdates && resultUpdatesOfSubLayers && resultUpdatesOfSubFolders;
                resultUpdates = resultUpdates && resultUpdatesOfSubFolders;
            }
            FolderList fs = this.getAllUserFoldersByUserId(owner.getId());
            for (FolderDTO folder : fs.getList()) {
                logger.trace("\n***\n{}\n***", folder);
                TreeFolderElements tree = this.getChildrenElements(folder.getId());
                for (Object object : tree) {
                    logger.trace("\n***\n{}\n***", object);
                }
            }
        }

        folderMoved.setPosition(newPosition);
        folderDao.merge(folderMoved);

        boolean resultUpdateAncestorsDescendants = folderDao.updateAncestorsDescendants(descendantsMapData.getDescendantsMap());
        assert (resultUpdateAncestorsDescendants) : "Errors occured when updating ancestors and descendants on tree";

        System.out.println("@@@@");
        System.out.print(resultUpdates + " - ");
        System.out.print(resultUpdateAncestorsDescendants);
        System.out.println("@@@@");

        return resultUpdates && resultUpdateAncestorsDescendants;
    }

    public FolderDTO getShortFolder(RequestById request) throws ResourceNotFoundFault {
        GPFolder folder = folderDao.find(request.getId());

        if (folder == null) {
            throw new ResourceNotFoundFault("Folder not found", request.getId());
        }

        FolderDTO folderDTO = new FolderDTO(folder);
        return folderDTO;
    }

    public GPFolder getFolderDetail(RequestById request) throws ResourceNotFoundFault {
        GPFolder folder = folderDao.find(request.getId());

        if (folder == null) {
            throw new ResourceNotFoundFault("Folder not found", request.getId());
        }

        return folder;
    }

    public FolderList searchFolders(PaginatedSearchRequest searchRequest) {
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

    public FolderList getFolders() {
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

    public FolderList getChildrenFolders(long folderId, int num, int page) {
        Search searchCriteria = new Search(GPFolder.class);
        searchCriteria.setMaxResults(num);
        searchCriteria.setPage(page);
        searchCriteria.addSortAsc("name");

        Filter parent = Filter.equal("parent.id", folderId);
        searchCriteria.addFilter(parent);

        List<GPFolder> foundFolder = folderDao.search(searchCriteria);
        return convertToFolderList(foundFolder);
    }

    public FolderList getChildrenFolders(long folderId) {
        Search searchCriteria = new Search(GPFolder.class);
        searchCriteria.addSortAsc("name");

        Filter parent = Filter.equal("parent.id", folderId);
        searchCriteria.addFilter(parent);

        List<GPFolder> foundFolder = folderDao.search(searchCriteria);
        return convertToFolderList(foundFolder);
    }

    public TreeFolderElements getChildrenElements(long folderId) {
        TreeFolderElements tree = new TreeFolderElements();

        Search searchCriteria = new Search(GPFolder.class);
        searchCriteria.addSortAsc("name");
        Filter parent = Filter.equal("parent.id", folderId);
        searchCriteria.addFilter(parent);
        List<GPFolder> foundFolder = folderDao.search(searchCriteria);
        tree.addFolderCollection(convertToFolderList(foundFolder));

        searchCriteria = new Search(GPLayer.class);
        searchCriteria.addSortAsc("name");
        Filter folder = Filter.equal("folder.id", folderId);
        searchCriteria.addFilter(folder);
        List<GPLayer> foundLayer = layerDao.search(searchCriteria);
        tree.addLayerCollection(foundLayer);

        return tree;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Folder / User">
    // ==========================================================================
    // === Folder / User
    // ==========================================================================
    public void setFolderShared(RequestById request)
            throws ResourceNotFoundFault {
        GPFolder folder = folderDao.find(request.getId());

        if (folder == null) {
            throw new ResourceNotFoundFault("Folder not found", request.getId());
        }

        folder.setShared(true);
        folder.setOwner(null);
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
        folder.setOwner(user);
        folderDao.merge(folder);

        return true;
    }

    public FolderList getUserFoldersByRequest(RequestById request) {
        Search searchCriteria = new Search(GPFolder.class);
        searchCriteria.setMaxResults(request.getNum());
        searchCriteria.setPage(request.getPage());
        searchCriteria.addSortAsc("position");

        searchCriteria.addFilterEqual("owner.id", request.getId());

        List<GPFolder> foundFolder = folderDao.search(searchCriteria);
        return convertToFolderList(foundFolder);
    }

    public FolderList getUserFoldersByUserId(long userId) {
        Search searchCriteria = new Search(GPFolder.class);
        searchCriteria.addSortAsc("position");

        searchCriteria.addFilterEqual("owner.id", userId);

        List<GPFolder> foundFolder = folderDao.search(searchCriteria);
        return convertToFolderList(foundFolder);
    }

    public FolderList getAllUserFolders(long userId, int num, int page) {
        Search searchCriteria = new Search(GPFolder.class);
        searchCriteria.setMaxResults(num);
        searchCriteria.setPage(page);
        searchCriteria.addSortAsc("name");

        Filter owner = Filter.equal("owner.id", userId);
        Filter shared = Filter.equal("shared", true);
        searchCriteria.addFilterOr(owner, shared);

        List<GPFolder> foundFolder = folderDao.search(searchCriteria);

        FolderList list = convertToFolderList(foundFolder);
        return list;
    }

    public FolderList getAllUserFoldersByUserId(long userId) {
        Search searchCriteria = new Search(GPFolder.class);
        searchCriteria.addSortAsc("name");

        Filter owner = Filter.equal("owner.id", userId);
        Filter shared = Filter.equal("shared", true);
        searchCriteria.addFilterOr(owner, shared);

        List<GPFolder> foundFolder = folderDao.search(searchCriteria);

        FolderList list = convertToFolderList(foundFolder);
        return list;
    }

    public long getUserFoldersCount(RequestById request) {
        Search searchCriteria = new Search(GPFolder.class);
        searchCriteria.addFilterEqual("owner.id", request.getId());
        return folderDao.count(searchCriteria);
    }

    public int getAllUserFoldersCount(long userId) {
        Search searchCriteria = new Search(GPFolder.class);

        Filter owner = Filter.equal("owner.id", userId);
        Filter shared = Filter.equal("shared", true);
        searchCriteria.addFilterOr(owner, shared);

        return folderDao.count(searchCriteria);
    }
    //</editor-fold>

    // TODO Move to FolderList?
    // as constructor: FolderList list = new FolderList(List<GPFolder>);
    private FolderList convertToFolderList(List<GPFolder> folderList) {
        List<FolderDTO> foldersDTO = new ArrayList<FolderDTO>(folderList.size());
        for (GPFolder folder : folderList) {
            FolderDTO folderDTO = new FolderDTO(folder);
            foldersDTO.add(folderDTO);
        }

        Collections.sort(foldersDTO);

        FolderList folders = new FolderList();
        folders.setList(foldersDTO);
        return folders;
    }
}
