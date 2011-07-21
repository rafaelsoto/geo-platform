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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.geosdi.geoplatform.core.dao.GPFolderDAO;
import org.geosdi.geoplatform.core.dao.GPLayerDAO;
import org.geosdi.geoplatform.core.dao.GPProjectDAO;
import org.geosdi.geoplatform.core.dao.GPStyleDAO;
import org.geosdi.geoplatform.core.dao.GPUserDAO;
import org.geosdi.geoplatform.core.model.GPBBox;
import org.geosdi.geoplatform.core.model.GPFolder;
import org.geosdi.geoplatform.core.model.GPLayer;
import org.geosdi.geoplatform.core.model.GPLayerInfo;
import org.geosdi.geoplatform.core.model.GPLayerType;
import org.geosdi.geoplatform.core.model.GPProject;
import org.geosdi.geoplatform.core.model.GPRasterLayer;
import org.geosdi.geoplatform.core.model.GPStyle;
import org.geosdi.geoplatform.core.model.GPUser;
import org.geosdi.geoplatform.core.model.GPVectorLayer;
import org.geosdi.geoplatform.exception.IllegalParameterFault;
import org.geosdi.geoplatform.exception.ResourceNotFoundFault;
import org.geosdi.geoplatform.responce.ShortLayerDTO;
import org.geosdi.geoplatform.responce.StyleDTO;
import org.geosdi.geoplatform.responce.collection.GPWebServiceMapData;

/**
 * @author Vincenzo Monteverde
 * @email vincenzo.monteverde@geosdi.org - OpenPGP key ID 0xB25F4B38
 *
 */
class LayerServiceImpl {

    final private static Logger logger = LoggerFactory.getLogger(LayerServiceImpl.class);
    // DAO
    private GPUserDAO userDao;
    private GPProjectDAO projectDao;
    private GPFolderDAO folderDao;
    private GPLayerDAO layerDao;
    private GPStyleDAO styleDao;

    //<editor-fold defaultstate="collapsed" desc="Setter methods">
    /**
     * @param userDao 
     *            the userDao to set
     */
    public GPUserDAO getUserDao() {
        return userDao;
    }

    /**
     * @param projecsDao
     *          the projecsDao to set
     */
    public void setProjectDao(GPProjectDAO projecsDao) {
        this.projectDao = projecsDao;
    }

    /**
     * @param userDao
     *            the userDao to set
     */
    public void setUserDao(GPUserDAO userDao) {
        this.userDao = userDao;
    }

    /**
     * @param folderDao 
     *            the folderDao to set
     */
    public void setFolderDao(GPFolderDAO folderDao) {
        this.folderDao = folderDao;
    }

    /**
     * @param layerDao
     *            the layerDao to set
     */
    public void setLayerDao(GPLayerDAO layerDao) {
        this.layerDao = layerDao;
    }

    /**
     * @param styleDao
     *            the styleDao to set
     */
    public void setStyleDao(GPStyleDAO styleDao) {
        this.styleDao = styleDao;
    }
    //</editor-fold>

    public List<StyleDTO> getLayerStyles(long layerId) {
        Search searchCriteria = new Search(GPStyle.class);

        searchCriteria.addSortAsc("name");
        searchCriteria.addFilterEqual("layer.id", layerId);

        List<GPStyle> foundStyle = styleDao.search(searchCriteria);
        return StyleDTO.convertToStyleDTOList(foundStyle);
    }

    public long insertLayer(GPLayer layer) throws IllegalParameterFault {
        this.checkLayer(layer); // TODO assert

        layerDao.persist(layer);
        return layer.getId();
    }

    public long updateRasterLayer(GPRasterLayer layer)
            throws ResourceNotFoundFault, IllegalParameterFault {
        this.checkLayer(layer); // TODO assert

        GPRasterLayer orig = (GPRasterLayer) layerDao.find(layer.getId());
        if (orig == null) {
            throw new ResourceNotFoundFault("Layer not found", layer.getId());
        }
        this.checkLayer(orig); // TODO assert

        orig.setLayerInfo(layer.getLayerInfo());
        this.updateLayer(orig, layer);

        layerDao.merge(orig);
        return orig.getId();
    }

    public long updateVectorLayer(GPVectorLayer layer)
            throws ResourceNotFoundFault, IllegalParameterFault {
        this.checkLayer(layer); // TODO assert

        GPVectorLayer orig = (GPVectorLayer) layerDao.find(layer.getId());
        if (orig == null) {
            throw new ResourceNotFoundFault("Layer not found", layer.getId());
        }
        this.checkLayer(orig); // TODO assert

        orig.setGeometry(layer.getGeometry());
        this.updateLayer(orig, layer);

        layerDao.merge(orig);
        return orig.getId();
    }

    public boolean deleteLayer(long layerId)
            throws ResourceNotFoundFault {
        GPLayer layer = layerDao.find(layerId);
        if (layer == null) {
            throw new ResourceNotFoundFault("Layer not found", layerId);
        }
        this.checkLayerLog(layer); // TODO assert

        // data on ancillary tables should be deleted by cascading
        return layerDao.remove(layer);
    }

    public long saveAddedLayerAndTreeModifications(GPLayer layer,
            GPWebServiceMapData descendantsMapData)
            throws ResourceNotFoundFault, IllegalParameterFault {
        logger.trace("\n\t@@@ saveAddedLayerAndTreeModifications @@@");
        this.checkLayer(layer); // TODO assert

        GPFolder parent = layer.getFolder();
        if (parent == null) {
            throw new IllegalParameterFault("Parent of layer with id " + layer.getId() + " not found");
        }
        this.checkFolder(parent); // TODO assert

        long idParent = parent.getId();
        GPFolder parentFromDB = folderDao.find(idParent);
        if (parentFromDB == null) {
            throw new ResourceNotFoundFault("Parent of layer not found", idParent);
        }
        this.checkFolder(parentFromDB); // TODO assert

        int newPosition = layer.getPosition();
        int increment = 1;
        // Shift positions
        layerDao.updatePositionsLowerBound(newPosition, increment);
        folderDao.updatePositionsLowerBound(newPosition, increment);

        layerDao.persist(layer);

        folderDao.updateAncestorsDescendants(descendantsMapData.getDescendantsMap());
        this.updateNumberOfElements(layer, increment);

        return layer.getId();
    }

    public ArrayList<Long> saveAddedLayersAndTreeModifications(List<GPLayer> layers,
            GPWebServiceMapData descendantsMapData)
            throws ResourceNotFoundFault, IllegalParameterFault {
        logger.trace("\n\t@@@ saveAddedLayersAndTreeModifications @@@");
        if (layers == null) {
            throw new IllegalParameterFault("List of layers is NULL");
        }

        GPLayer[] layersArray = layers.toArray(new GPLayer[layers.size()]);
        for (GPLayer gpLayer : layersArray) {
            this.checkLayer(gpLayer); // TODO assert
        }

        GPFolder parent = layersArray[0].getFolder();
        if (parent == null) {
            throw new IllegalParameterFault("Parent of layer with id " + layersArray[0].getId() + " not found");
        }
        this.checkFolder(parent); // TODO assert

        long idParent = parent.getId();
        GPFolder parentFromDB = folderDao.find(idParent);
        if (parentFromDB == null) {
            throw new ResourceNotFoundFault("Parent of layer not found", idParent);
        }
        this.checkFolder(parentFromDB); // TODO assert

        ArrayList<Long> arrayList = new ArrayList<Long>(layers.size());
        int newPosition = layers.get(layers.size() - 1).getPosition();
        int increment = layers.size();
        // Shift positions
        layerDao.updatePositionsLowerBound(newPosition, increment);
        folderDao.updatePositionsLowerBound(newPosition, increment);

        layerDao.persist(layersArray);

        for (int i = 0; i < layersArray.length; i++) {
            arrayList.add(layersArray[i].getId());
        }

        folderDao.updateAncestorsDescendants(descendantsMapData.getDescendantsMap());
        this.updateNumberOfElements(layers.get(0), increment);

        return arrayList;
    }

    public boolean saveDeletedLayerAndTreeModifications(long layerId,
            GPWebServiceMapData descendantsMapData)
            throws ResourceNotFoundFault {
        GPLayer layer = layerDao.find(layerId);
        if (layer == null) {
            throw new ResourceNotFoundFault("Layer not found", layerId);
        }
        this.checkLayerLog(layer); // TODO assert

        int oldPosition = layer.getPosition();
        boolean result = layerDao.remove(layer);

        int decrement = -1;
        // Shift positions
        layerDao.updatePositionsLowerBound(oldPosition, decrement);
        folderDao.updatePositionsLowerBound(oldPosition, decrement);

        folderDao.updateAncestorsDescendants(descendantsMapData.getDescendantsMap());
        this.updateNumberOfElements(layer, decrement);

        return result;
    }

    public boolean saveCheckStatusLayerAndTreeModifications(long layerId, boolean checked)
            throws ResourceNotFoundFault {
        GPLayer layer = layerDao.find(layerId);
        if (layer == null) {
            throw new ResourceNotFoundFault("Layer not found", layerId);
        }
        this.checkLayerLog(layer); // TODO assert

        boolean checkSave = layerDao.persistCheckStatusLayer(layerId, checked);

        // Iff checked is true, all the ancestor folders must be checked
        if (checked && checkSave) {
            Long[] layerAncestors = this.getIdsUserFolderAndAncestors(layer.getFolder());
            return folderDao.persistCheckStatusFolders(true, layerAncestors);
        }

        return checkSave;
    }

    /**
     * For Drag and Drop, fix the check of new ancestors for a layer checked
     * 
     * The old and new folders (parent) will be extraxted from DB
     */
    public boolean fixCheckStatusLayerAndTreeModifications(long layerId,
            long oldUserFolderId, long newUserFolderId) throws ResourceNotFoundFault {
        // Retrieve the layer
        GPLayer layer = layerDao.find(layerId);
        if (layer == null) {
            throw new ResourceNotFoundFault("Layer not found", layerId);
        }
//        assert (layer.isChecked()) : "For Fix the check, the layer must be checked";
        this.checkLayerLog(layer); // TODO assert

        // Retrieve the UserFolders parent
        GPFolder oldUserFolder = folderDao.find(oldUserFolderId);
        if (oldUserFolder == null) {
            throw new ResourceNotFoundFault("Old Folder not found", oldUserFolderId);
        }
        this.checkFolderLog(oldUserFolder); // TODO assert

        GPFolder newUserFolder = folderDao.find(newUserFolderId);
        if (newUserFolder == null) {
            throw new ResourceNotFoundFault("New Folder not found", newUserFolderId);
        }
        this.checkFolderLog(newUserFolder); // TODO assert

        // Test if the Check was valid (all the old ancestor must be checked)
        GPFolder[] oldAncestors = this.getUserFolderAndAncestors(oldUserFolder);
        if (this.isAllUserFoldersChecked(oldAncestors)) {
            Long[] idNewAncestors = this.getIdsUserFolderAndAncestors(newUserFolder);
            return folderDao.persistCheckStatusFolders(true, idNewAncestors);
        }

        return true;
    }

    public boolean saveDragAndDropLayerModifications(long idLayerMoved,
            long idNewParent, int newPosition, GPWebServiceMapData descendantsMapData)
            throws ResourceNotFoundFault, IllegalParameterFault {
        GPLayer layerMoved = layerDao.find(idLayerMoved);
        if (layerMoved == null) {
            throw new ResourceNotFoundFault("Layer with id " + idLayerMoved + " not found");
        }
        this.checkLayerLog(layerMoved); // TODO assert

        if (idNewParent == 0) {
            throw new IllegalParameterFault("New folder parent NOT declared");
        }

        GPFolder folderParent = folderDao.find(idNewParent);
        if (folderParent == null) {
            throw new ResourceNotFoundFault("The new parent does not exists", idNewParent);
        }
        this.checkFolderLog(folderParent); // TODO assert
        layerMoved.setFolder(folderParent);

        int startFirstRange = 0, endFirstRange = 0;
        if (layerMoved.getPosition() < newPosition) {// Drag & Drop to top
            startFirstRange = newPosition;
            endFirstRange = layerMoved.getPosition() + 1;
        } else if (layerMoved.getPosition() > newPosition) {// Drag & Drop to bottom
            startFirstRange = layerMoved.getPosition() - 1;
            endFirstRange = newPosition;
        }
        int shiftValue = 1;

        Search search = new Search();
        search.addFilterGreaterOrEqual("position", endFirstRange).
                addFilterLessOrEqual("position", startFirstRange);
        search.addFilterEqual("project.id", layerMoved.getProject().getId());
        List<GPFolder> matchingFoldersFirstRange = folderDao.search(search);
        List<GPLayer> matchingLayersFirstRange = layerDao.search(search);

        if (layerMoved.getPosition() < newPosition) {// Drag & Drop to top
            this.executeFoldersModifications(matchingFoldersFirstRange, -shiftValue);
            this.executeLayersModifications(matchingLayersFirstRange, -shiftValue);
        } else if (layerMoved.getPosition() > newPosition) {// Drag & Drop to bottom
            this.executeFoldersModifications(matchingFoldersFirstRange, shiftValue);
            this.executeLayersModifications(matchingLayersFirstRange, shiftValue);
        }

        folderDao.merge(matchingFoldersFirstRange.toArray(new GPFolder[matchingFoldersFirstRange.size()]));
        layerDao.merge(matchingLayersFirstRange.toArray(new GPLayer[matchingLayersFirstRange.size()]));

        layerMoved.setPosition(newPosition);
        layerDao.merge(layerMoved);

        folderDao.updateAncestorsDescendants(descendantsMapData.getDescendantsMap());

        return true;
    }

    private void executeLayersModifications(List<GPLayer> elements, int value) {
        for (GPLayer layer : elements) {
            this.checkLayerLog(layer); // TODO assert
            layer.setPosition(layer.getPosition() + value);
        }
    }

    private void executeFoldersModifications(List<GPFolder> elements, int value) {
        for (GPFolder userFolder : elements) {
            this.checkFolderLog(userFolder); // TODO assert
            userFolder.setPosition(userFolder.getPosition() + value);
        }
    }

    public GPRasterLayer getRasterLayer(long layerId) throws ResourceNotFoundFault {
        GPRasterLayer layer = (GPRasterLayer) layerDao.find(layerId);
        if (layer == null) {
            throw new ResourceNotFoundFault("Layer not found", layerId);
        }
        this.checkLayerLog(layer); // TODO assert

        return layer;
    }

    public GPVectorLayer getVectorLayer(long layerId) throws ResourceNotFoundFault {
        GPVectorLayer layer = (GPVectorLayer) layerDao.find(layerId);
        if (layer == null) {
            throw new ResourceNotFoundFault("Layer not found", layerId);
        }
        this.checkLayerLog(layer); // TODO assert

        return layer;
    }

    public ShortLayerDTO getShortLayer(long layerId) throws ResourceNotFoundFault {
        GPLayer layer = layerDao.find(layerId);
        if (layer == null) {
            throw new ResourceNotFoundFault("Layer not found", layerId);
        }
        this.checkLayerLog(layer); // TODO assert

        return new ShortLayerDTO(layer);
    }

    public List<ShortLayerDTO> getLayers(long projectId) {
        Search searchCriteria = new Search(GPLayer.class);

        searchCriteria.addSortAsc("title");
        searchCriteria.addFilterEqual("project.id", projectId);

        List<GPLayer> foundLayer = layerDao.search(searchCriteria);

        this.checkLayerListLog(foundLayer); // TODO assert

        return ShortLayerDTO.convertToShortLayerDTOList(foundLayer);
    }

    public GPBBox getBBox(long layerId) throws ResourceNotFoundFault {
        GPLayer layer = layerDao.find(layerId);
        if (layer == null) {
            throw new ResourceNotFoundFault("Layer not found", layerId);
        }
        this.checkLayerLog(layer); // TODO assert

        return layer.getBbox();
    }

    public GPLayerInfo getLayerInfo(long layerId) throws ResourceNotFoundFault {
        GPRasterLayer layer = (GPRasterLayer) layerDao.find(layerId);
        if (layer == null) {
            throw new ResourceNotFoundFault("Layer not found", layerId);
        }
        this.checkLayerLog(layer); // TODO assert

        return layer.getLayerInfo();
    }

//    public Point getGeometry(long layerId) throws ResourceNotFoundFault {
//        GPVectorLayer layer = (GPVectorLayer)layerDao.find(layerId);
//        if (layer == null) {
//            throw new ResourceNotFoundFault("Layer not found", layerId);
//        }
//
//        return layer.getGeometry();
//    }
    public GPLayerType getLayerType(long layerId) throws ResourceNotFoundFault {
        GPLayer layer = layerDao.find(layerId);
        if (layer == null) {
            throw new ResourceNotFoundFault("Layer not found", layerId);
        }
        this.checkLayerLog(layer); // TODO assert

        return layer.getLayerType();
    }

    public ArrayList<String> getLayersDataSourceByProjectId(long projectId)
            throws ResourceNotFoundFault {
        GPProject project = projectDao.find(projectId);
        if (project == null) {
            throw new ResourceNotFoundFault("Project not found", projectId);
        }

        return layerDao.findDistinctDataSourceByProjectId(projectId);
    }

    /**
     * Updates all common fiels of raster and vector layers (GPLayer) 
     */
    private void updateLayer(GPLayer layerToUpdate, GPLayer layer) {
        layerToUpdate.setProject(layer.getProject());
        layerToUpdate.setAbstractText(layer.getAbstractText());
        layerToUpdate.setBbox(layer.getBbox());
        layerToUpdate.setLayerType(layer.getLayerType());
        layerToUpdate.setName(layer.getName());
        layerToUpdate.setPosition(layer.getPosition());
        layerToUpdate.setShared(layer.isShared());
        layerToUpdate.setSrs(layer.getSrs());
        layerToUpdate.setTitle(layer.getTitle());
        layerToUpdate.setUrlServer(layer.getUrlServer());
    }

    /**
     * @return UserFolder argument and his ancestor folders
     */
    private GPFolder[] getUserFolderAndAncestors(GPFolder userFolderChild)
            throws ResourceNotFoundFault {
        Long[] idFolderAndAncestors = this.getIdsUserFolderAndAncestors(userFolderChild);
        GPFolder[] folderAndAncestors = folderDao.find(idFolderAndAncestors);
        if (folderAndAncestors.length == 0) {
            throw new ResourceNotFoundFault("Ancestors Folders of Layer not found");
        }
        return folderAndAncestors;
    }

    /**
     * @return IDs of UserFolder argument and his ancestor folders
     */
    private Long[] getIdsUserFolderAndAncestors(GPFolder userFolder) {
        List<Long> ancestors = new ArrayList<Long>();
        ancestors.add(userFolder.getId());

        GPFolder ancestorIth = userFolder.getParent();
        while (ancestorIth != null) {
            ancestors.add(ancestorIth.getId());
            ancestorIth = ancestorIth.getParent();
        }
        return ancestors.toArray(new Long[ancestors.size()]);
    }

    /**
     * @return True if all UserFolders are checked
     */
    private boolean isAllUserFoldersChecked(GPFolder... userFolders)
            throws ResourceNotFoundFault {
        for (GPFolder userFolder : userFolders) {
            if (!userFolder.isChecked()) {
                return false;
            }
        }
        return true;
    }

    private void updateNumberOfElements(GPLayer layer, int delta)
            throws ResourceNotFoundFault {
        long projectId = layer.getProject().getId();
        GPProject project = projectDao.find(projectId);
        if (project == null) {
            throw new ResourceNotFoundFault("Project not found", projectId);
        }

        project.deltaToNumberOfElements(delta);
        projectDao.merge(project);
    }

    // TODO assert
    private void checkLayer(GPLayer layer) throws IllegalParameterFault {
        if (layer == null) {
            throw new IllegalParameterFault("Layer must be NOT NULL");
        }
        if (layer.getTitle() == null) {
            throw new IllegalParameterFault("GPLayer: \"title\" must be NOT NULL");
        }
//        if (layer.getLayerType() == null) {
//            throw new IllegalParameterFault("GPLayer: \"layerType\" must be NOT NULL");
//        }
        if (layer.getFolder() == null) {
            throw new IllegalParameterFault("Layer must have a folder");
        }
    }

    // TODO assert
    private void checkLayerListLog(List<GPLayer> layers) {
        for (GPLayer layer : layers) {
            this.checkLayerLog(layer);
        }
    }

    // TODO assert
    private void checkLayerLog(GPLayer layer) {
        try {
            this.checkLayer(layer);
        } catch (IllegalParameterFault ex) {
            logger.error("\n--- " + ex.getMessage() + " ---");
        }
    }

    // TODO assert
    private void checkFolder(GPFolder folder) throws IllegalParameterFault {
        if (folder == null) {
            throw new IllegalParameterFault("Folder must be NOT NULL");
        }
        if (folder.getName() == null) {
            throw new IllegalParameterFault("Folder \"name\" must be NOT NULL");
        }
        if (folder.getNumberOfDescendants() < 0) {
            throw new IllegalParameterFault("Folder \"numberOfDescendants\" must be greater or equal 0");
        }
        if (folder.getProject() == null) {
            throw new IllegalParameterFault("Folder \"project\" must be NOT NULL");
        }
    }

    // TODO assert
    private void checkFolderLog(GPFolder userFolder) {
        try {
            this.checkFolder(userFolder);
        } catch (IllegalParameterFault ex) {
            logger.error("\n--- " + ex.getMessage() + " ---");
        }
    }
}
