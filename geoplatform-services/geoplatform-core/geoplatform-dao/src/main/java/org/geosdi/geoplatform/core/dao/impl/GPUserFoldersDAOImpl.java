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
package org.geosdi.geoplatform.core.dao.impl;

import org.geosdi.geoplatform.core.dao.GPUserFoldersDAO;
import org.geosdi.geoplatform.core.model.GPUserFolders;
import org.geosdi.geoplatform.core.model.UserFolderPk;

import com.googlecode.genericdao.search.ISearch;
import com.googlecode.genericdao.search.Search;
import java.util.List;
import javax.persistence.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Vincenzo Monteverde
 * @email vincenzo.monteverde@geosdi.org - OpenPGP key ID 0xB25F4B38
 *
 */
@Transactional
public class GPUserFoldersDAOImpl extends BaseDAO<GPUserFolders, UserFolderPk>
        implements GPUserFoldersDAO {

    @Override
    public List<GPUserFolders> findAll() {
        return super.findAll();
    }

    @Override
    public GPUserFolders find(UserFolderPk userFoldersId) {
//        return super.find(userFoldersId); // Run (magicaly) but execute a strange outer join on x_user_id=y_folder_id
        if (userFoldersId != null && userFoldersId.getUser() != null && userFoldersId.getFolder() != null) {
            this.find(userFoldersId.getUser().getId(), userFoldersId.getFolder().getId());
        }
        return null;
    }

    @Override
    public void persist(GPUserFolders... usersFolders) {
        super.persist(usersFolders);
    }

    @Override
    public GPUserFolders merge(GPUserFolders userFolders) {
        return super.merge(userFolders);
    }

    @Override
    public GPUserFolders[] merge(GPUserFolders... usersFolders) {
        return super.merge(usersFolders);
    }

    @Override
    public boolean remove(GPUserFolders userFolders) {
//        return super.remove(userFolders); // Don't run because GPUserFolders has UserFolderPk composite-key
        if (userFolders != null) {
            if (em().contains(userFolders)) {
                em().remove(userFolders);
                return true;
            } else {
                return this.removeById(userFolders.getPk());
            }
        }
        return false;
    }

    @Override
    public boolean removeById(UserFolderPk userFoldersId) {
//        return super.removeById(userFoldersId); // Don't run because GPUserFolders has UserFolderPk composite-key
        if (userFoldersId != null) {
            // Hibernate Query Language [HQL]
            StringBuilder str = new StringBuilder();
            str.append("select _it_.pk.user.id, _it_.pk.folder.id");
            str.append(" from ").append(getMetadataUtil().get(GPUserFolders.class).getEntityName()).append(" _it_");
            str.append(" where _it_.pk.user.id = ? and _it_.pk.folder.id = ?");
            // Set query
            Query query = em().createQuery(str.toString());
            query.setParameter(1, userFoldersId.getUser().getId());
            query.setParameter(2, userFoldersId.getFolder().getId());
            // Remove existent entity
            if (!query.getResultList().isEmpty()) {
                em().remove(em().getReference(GPUserFolders.class, userFoldersId));
                return true;
            }
        }
        return false;
    }

    @Override
    public List<GPUserFolders> search(ISearch search) {
        return super.search(search);
    }

    @Override
    public int count(ISearch search) {
        return super.count(search);
    }

    @Override
    public List<GPUserFolders> findByUserId(long userId) {
        Search search = new Search();
        search.addSortDesc("position");
        search.addFilterEqual("pk.user.id", userId);
        return search(search);
    }

    @Override
    public List<GPUserFolders> findByFolderId(long folderId) {
        Search search = new Search();
        search.addSortDesc("position");
        search.addFilterEqual("pk.folder.id", folderId);
        return search(search);
    }

    @Override
    public GPUserFolders find(long userId, long folderId) {
        Search search = new Search();
        search.addFilterEqual("pk.user.id", userId);
        search.addFilterEqual("pk.folder.id", folderId);
        return searchUnique(search);
    }
}
