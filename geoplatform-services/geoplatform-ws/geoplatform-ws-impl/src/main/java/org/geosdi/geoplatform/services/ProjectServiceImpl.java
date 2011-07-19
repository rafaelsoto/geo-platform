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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.geosdi.geoplatform.core.dao.GPProjectDAO;
import org.geosdi.geoplatform.core.model.GPProject;

/**
 * @author Michele Santomauro
 * @email michele.santomauro@geosdi.org
 * 
 */
class ProjectServiceImpl {

    final private static Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);
    // DAO
    private GPProjectDAO projectDao;

    //<editor-fold defaultstate="collapsed" desc="Setter methods">
    /**
     * @param projectDao
     *            the projectDao to set
     */
    public void setProjectDao(GPProjectDAO projectDao) {
        this.projectDao = projectDao;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Project">
    // ==========================================================================
    // === Project
    // ==========================================================================
    
    //</editor-fold>

    long insertProject(GPProject project) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    long updateProject(GPProject project) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    boolean deleteProject(long projectId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public GPProject getUserProject(long userProjectId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    // TODO refactoring
//    public boolean setProjectOwner(RequestByUserFolder request, boolean force)
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

}
