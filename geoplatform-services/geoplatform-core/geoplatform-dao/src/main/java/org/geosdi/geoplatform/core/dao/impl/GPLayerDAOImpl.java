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

import com.googlecode.genericdao.search.ISearch;
import com.googlecode.genericdao.search.Search;
import java.util.List;

import org.geosdi.geoplatform.core.dao.GPLayerDAO;
import org.geosdi.geoplatform.core.model.GPLayer;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Francesco Izzi - geoSDI
 * @author Vincenzo Monteverde
 * @email vincenzo.monteverde@geosdi.org - OpenPGP key ID 0xB25F4B38
 * 
 */
@Transactional
public class GPLayerDAOImpl extends BaseDAO<GPLayer, Long> implements
        GPLayerDAO {

    @Override
    public void persist(GPLayer... layers) {
        super.persist(layers);
    }

    @Override
    public GPLayer merge(GPLayer layer) {
        return super.merge(layer);
    }

    @Override
    public GPLayer[] merge(GPLayer... layers) {
        return super.merge(layers);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<GPLayer> search(ISearch search) {
        return super.search(search);
    }

    @Override
    public boolean remove(GPLayer layer) {
        return super.remove(layer);
    }

    @Override
    public GPLayer findByLayerName(String layerName) {
        Search search = new Search();
        search.addFilterEqual("name", layerName);
        return searchUnique(search);
    }

    @Override
    public boolean updatePositionsRange(int beginPosition, int endPosition,
            int deltaValue) {
        // Select the layers of interest
        Search search = new Search();
        search.addFilterGreaterOrEqual("position", beginPosition).
                addFilterLessOrEqual("position", endPosition);
        List<GPLayer> matchingLayers = super.search(search);

        logger.debug("\n*** Layers with Position in range: {} to {} [# {}] *** deltaValue = {} ***",
                new Object[]{beginPosition, endPosition, endPosition - beginPosition + 1, deltaValue});
        logger.debug("\n*** Matching Layers count: {} ***", matchingLayers.size());

        return this.updatePositions(matchingLayers, deltaValue);
    }

    @Override
    public boolean updatePositionsLowerBound(int lowerBoundPosition, int deltaValue) {
        // Select the layers of interest
        Search search = new Search();
        search.addFilterGreaterOrEqual("position", lowerBoundPosition);
        List<GPLayer> matchingLayers = super.search(search);

        logger.debug("\n*** UPDATE Layers with Position from {} *** deltaValue = {} ***",
                new Object[]{lowerBoundPosition, deltaValue});
        logger.debug("\n*** Matching Layers count: {} ***", matchingLayers.size());

        return this.updatePositions(matchingLayers, deltaValue);
    }

    private boolean updatePositions(List<GPLayer> matchingLayers, int deltaValue) {
        // Update
        int[] oldPositions = new int[matchingLayers.size()];
        for (int ind = 0; ind < matchingLayers.size(); ind++) {
            GPLayer layer = matchingLayers.get(ind);
            oldPositions[ind] = layer.getPosition();
            layer.setPosition(layer.getPosition() + deltaValue);
        }
        GPLayer[] layersUpdated = merge(matchingLayers.toArray(new GPLayer[matchingLayers.size()]));

        // Check the update
        for (int ind = 0; ind < layersUpdated.length; ind++) {
            logger.trace("\n*** Position of the UPDATED GPLayer: {} ({} + {}) ***", new Object[]{
                        layersUpdated[ind].getPosition(), oldPositions[ind], deltaValue});
            if ((oldPositions[ind] + deltaValue) != layersUpdated[ind].getPosition()) {
                return false;
            }
        }
        return true;
    }
}
