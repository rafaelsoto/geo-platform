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
package org.geosdi.geoplatform.responce;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geosdi.geoplatform.core.model.GPBBox;

import org.geosdi.geoplatform.core.model.GPLayer;
import org.geosdi.geoplatform.core.model.GPLayerType;

/**
 * @author Francesco Izzi - CNR IMAA - geoSDI
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"id", "name", "position", "shared", "checked",
    "title", "urlServer", "srs", "abstractText", "layerType", "bbox", "cached", "alias"})
@XmlSeeAlso(value = {RasterLayerDTO.class, VectorLayerDTO.class})
public class ShortLayerDTO extends AbstractElementDTO {

    private String title;
    private String urlServer;
    private String srs;
    private String abstractText;
    private GPLayerType layerType;
    private GPBBox bbox;
    private boolean cached;
    private String alias;

    //<editor-fold defaultstate="collapsed" desc="Constructor method">
    /**
     * Default constructor
     */
    public ShortLayerDTO() {
        super();
    }

    /**
     * Constructor with GPLayer as arg
     */
    public ShortLayerDTO(GPLayer layer) {
        super(layer.getId(), layer.getName(), layer.getPosition(),
                layer.isShared(), layer.isChecked());
        this.title = layer.getTitle();
        this.urlServer = layer.getUrlServer();
        this.srs = layer.getSrs();
        this.abstractText = layer.getAbstractText();
        this.layerType = layer.getLayerType();
        this.bbox = layer.getBbox();
        this.cached = layer.isCached();
        this.alias = layer.getAlias();
    }
    //</editor-fold>

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     *            the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the urlServer
     */
    public String getUrlServer() {
        return urlServer;
    }

    /**
     * @param urlServer
     *            the urlServer to set
     */
    public void setUrlServer(String urlServer) {
        this.urlServer = urlServer;
    }

    /**
     * @return the srs
     */
    public String getSrs() {
        return srs;
    }

    /**
     * @param srs
     *            the srs to set
     */
    public void setSrs(String srs) {
        this.srs = srs;
    }

    /**
     * @return the abstractText
     */
    public String getAbstractText() {
        return abstractText;
    }

    /**
     * @param abstractText
     *            the abstractText to set
     */
    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

    /**
     * @return the layerType
     */
    public GPLayerType getLayerType() {
        return layerType;
    }

    /**
     * @param layerType
     *            the layerType to set
     */
    public void setLayerType(GPLayerType layerType) {
        this.layerType = layerType;
    }

    /**
     * @return the bbox
     */
    public GPBBox getBbox() {
        return bbox;
    }

    /**
     * @param bbox the bbox to set
     */
    public void setBbox(GPBBox bbox) {
        this.bbox = bbox;
    }

    /**
     * @return the cached
     */
    public boolean isCached() {
        return cached;
    }

    /**
     * @param cached
     *          the cached to set
     */
    public void setCached(boolean cached) {
        this.cached = cached;
    }

    /**
     * @return the alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * @param alias
     *          the alias to set
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String s = super.toString()
                + ", title=" + title
                + ", urlServer=" + urlServer
                + ", srs=" + srs
                + ", abstractText=" + abstractText
                + ", layerType=" + layerType
                + ", " + bbox
                + ", cached=" + cached
                + ", alias=" + alias;
        return s;
    }
}
