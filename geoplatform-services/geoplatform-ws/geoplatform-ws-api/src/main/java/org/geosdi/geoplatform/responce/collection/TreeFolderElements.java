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
package org.geosdi.geoplatform.responce.collection;

import java.util.Collection;
import java.util.List;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.geosdi.geoplatform.core.model.GPLayer;
import org.geosdi.geoplatform.core.model.GPLayerType;
import org.geosdi.geoplatform.core.model.GPRasterLayer;
import org.geosdi.geoplatform.core.model.GPVectorLayer;
import org.geosdi.geoplatform.responce.FolderDTO;
import org.geosdi.geoplatform.responce.IElementDTO;
import org.geosdi.geoplatform.responce.RasterLayerDTO;
import org.geosdi.geoplatform.responce.ShortLayerDTO;
import org.geosdi.geoplatform.responce.VectorLayerDTO;

/**
 * @author Vincenzo Monteverde
 * @email vincenzo.monteverde@geosdi.org - OpenPGP key ID 0xB25F4B38
 * @author Michele Santomauro
 *
 */
/**
 * Ordered collection (wrt position) without duplicates
 */
// TODO
// !Handle the case where an element is not included in the tree!
// Note: super.add() return boolean
public class TreeFolderElements extends TreeSet<IElementDTO> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * @param folders
     *            list of FolderDTO
     */
    public void addFolderCollection(List<FolderDTO> folders) {
        super.addAll(folders);
    }

    /**
     * @param layerList
     *            list of GPlayer
     */
    public void addLayerCollection(Collection<GPLayer> layerList) throws ClassCastException {
        for (GPLayer layer : layerList) {
            GPLayerType layerType = layer.getLayerType();
            if (layerType.equals(GPLayerType.RASTER)) {
                GPRasterLayer rasterLayer = (GPRasterLayer) layer;
                RasterLayerDTO rasterLayerDTO = new RasterLayerDTO(rasterLayer);
                logger.debug("\n### RasterLayerDTO ###\n" + rasterLayerDTO + "\n###\t###\t###");
                super.add(rasterLayerDTO);
            } else { // More LayerType match a GPVectorLayer
                GPVectorLayer vectorLayer = (GPVectorLayer) layer;
                VectorLayerDTO vectorLayerDTO = new VectorLayerDTO(vectorLayer);
                logger.debug("\n### VectorLayerDTO ###\n" + vectorLayerDTO + "\n###\t###\t###");
                super.add(vectorLayerDTO);
            }
        }
    }

    /**
     * @param layers
     *            list of ShortLayerDTO
     */
    public void addLayerCollection(List<ShortLayerDTO> layers) {
        super.addAll(layers);
    }
}