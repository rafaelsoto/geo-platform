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

import javax.xml.bind.annotation.XmlRootElement;

import org.geosdi.geoplatform.core.model.GPLayer;

/**
 * @author Francesco Izzi - CNR IMAA - geoSDI
 * 
 */
@XmlRootElement(name = "LayerDTO")
public class LayerDTO extends AbstractElementDTO {

    private String abstractText;
    private String title;
    private String urlServer;
    private String srs;
    // TODO add referece to:
//    private GPLayerType layerType;
//    private GPBBox bbox;
//    private Collection<StyleDTO> styles;

    //<editor-fold defaultstate="collapsed" desc="Constructor method">
    /**
     * Default constructor
     */
    public LayerDTO() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * Constructor with GPLayer as arg
     */
    public LayerDTO(GPLayer layer) {
        super(layer.getId(), layer.getName(), layer.getPosition(), layer.isShared());
        this.abstractText = layer.getAbstractText();
        this.title = layer.getTitle();
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

//    public GPLayerType getLayerType() {
//        return layerType;
//    }
//
//    public void setLayerType(GPLayerType layerType) {
//        this.layerType = layerType;
//    }
//
//    public GPBBox getBbox() {
//        return bbox;
//    }
//
//    public void setBbox(GPBBox bbox) {
//        this.bbox = bbox;
//    }
//
//    public Collection<StyleDTO> getStyles() {
//        return styles;
//    }
//
//    public void setStyles(Collection<StyleDTO> styles) {
//        this.styles = styles;
//    }
    //</editor-fold>

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "LayerDTO [" + super.toString()
                + "title=" + title + ", abstractText=" + abstractText + "]";
    }
}