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
package org.geosdi.geoplatform.core.dao;

import com.googlecode.genericdao.search.ISearch;
import java.util.List;
import org.geosdi.geoplatform.core.model.GPUserFolders;

/**
 * @author Vincenzo Monteverde
 * @email vincenzo.monteverde@geosdi.org - OpenPGP key ID 0xB25F4B38
 *
 */
public interface GPUserFoldersDAO {

    public List<GPUserFolders> findAll();

    public GPUserFolders find(long userFoldersId);

    public void persist(GPUserFolders... usersFolders);

    public GPUserFolders merge(GPUserFolders userFolders);

    public GPUserFolders[] merge(GPUserFolders... usersFolders);

    public boolean remove(GPUserFolders userFolders);

    public boolean removeById(long userFoldersId);

    public boolean removeByUserId(long userId);

    public boolean removeByFolderId(long folderId);

    public List<GPUserFolders> search(ISearch search);

    public int count(ISearch search);

    public List<GPUserFolders> findByUserId(long userId);

    public List<GPUserFolders> findByOwnerUserId(long userId);

    public List<GPUserFolders> findByFolderId(long folderId);

    public GPUserFolders find(long userId, long folderId);

    public boolean updatePositionsLowerBound(int lowerBoundPosition,
            int deltaValue);

    public boolean persistCheckStatusFolder(long idFolder, boolean checked);
}
