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
package org.geosdi.geoplatform.core.dao.impl;

import com.googlecode.genericdao.search.ISearch;
import com.googlecode.genericdao.search.Search;
import java.util.List;

import org.geosdi.geoplatform.core.dao.GPAuthorityDAO;
import org.geosdi.geoplatform.core.model.GPAuthority;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Francesco Izzi - CNR IMAA - geoSDI Group
 * 
 */
@Transactional
public class GPAuthorityDAOImpl extends BaseDAO<GPAuthority, Long> implements
        GPAuthorityDAO {

    @Override
    public void persist(GPAuthority... authorities) {
        super.persist(authorities);
    }

    @Override
    public boolean remove(GPAuthority authority) {
        return super.remove(authority);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<GPAuthority> search(ISearch search) {
        return super.search(search);
    }

    @Override
    public List<GPAuthority> findByUsername(String username) {
        Search search = new Search();
        search.addFilterEqual("username", username);
        return super.search(search);
    }
}
