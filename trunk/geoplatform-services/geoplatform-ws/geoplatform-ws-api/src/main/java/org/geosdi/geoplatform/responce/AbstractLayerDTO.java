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
package org.geosdi.geoplatform.responce;

import java.util.Collection;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.geosdi.geoplatform.core.model.GPBBox;
import org.geosdi.geoplatform.core.model.GPLayer;
import org.geosdi.geoplatform.core.model.GPLayerType;

/**
 * @author Francesco Izzi - CNR IMAA - geoSDI
 * 
 */
@XmlTransient
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"abstractText", "title", "urlServer", "srs", "layerType", "bbox", "styles"})
@XmlSeeAlso(value = {RasterLayerDTO.class, VectorLayerDTO.class})
public abstract class AbstractLayerDTO extends AbstractElementDTO {

    private String abstractText;
    private String title;
    private String urlServer;
    private String srs;
    private GPLayerType layerType;
    private GPBBox bbox;
    @XmlElementWrapper(name = "stylesCollection")
    @XmlElement(name = "style")
    private Collection<StyleDTO> styles;

    //<editor-fold defaultstate="collapsed" desc="Constructor method">
    /**
     * Default constructor
     */
    public AbstractLayerDTO() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * Constructor with GPLayer as arg
     */
    public AbstractLayerDTO(GPLayer layer) {
        super(layer.getId(), layer.getName(), layer.getPosition(), layer.isShared());
        this.abstractText = layer.getAbstractText();
        this.title = layer.getTitle();
        this.urlServer = layer.getUrlServer();
        this.srs = layer.getSrs();
        this.layerType = layer.getLayerType();
        this.bbox = layer.getBbox();        
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getter and setter methods">
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

    public GPLayerType getLayerType() {
        return layerType;
    }

    public void setLayerType(GPLayerType layerType) {
        this.layerType = layerType;
    }

    public GPBBox getBbox() {
        return bbox;
    }

    public void setBbox(GPBBox bbox) {
        this.bbox = bbox;
    }

    public Collection<StyleDTO> getStyles() {
        return styles;
    }

    public void setStyles(Collection<StyleDTO> styles) {
        this.styles = styles;
    }
    //</editor-fold>

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String s = super.toString()
                + ", title=" + title + ", abstractText=" + abstractText
                + ", urlServer=" + urlServer + ", title=" + title
                + ", layerType=" + layerType.name() + ", " + bbox;
        if (styles != null) {
            s += ", #styles=" + styles.size();
        }
        return s;
    }
}