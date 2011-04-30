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
package org.geosdi.geoplatform.core.model;

import java.io.Serializable;
import javax.persistence.CascadeType;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * @author Francesco Izzi - geoSDI
 * 
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "gp_layer")
public abstract class GPLayer implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 5746325405739614413L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GP_LAYER_SEQ")
    @SequenceGenerator(name = "GP_LAYER_SEQ", sequenceName = "GP_LAYER_SEQ")
    @Column
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "position")
    private int position;

    @Column(name = "shared")
    private boolean shared = false;

    @Column(name = "abstract")
    private String abstractText;

    @Column(name = "title")
    private String title;

    @Column(name = "url_server")
    private String urlServer;

    @Column(name = "srs")
    private String srs;

    @Embedded
    private GPBBox bbox;

    @ManyToOne(cascade = CascadeType.REMOVE, optional = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private GPFolder folder;

    private GPLayerType layerType;

    //<editor-fold defaultstate="collapsed" desc="Getter and setter methods">
    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the position
     */
    public int getPosition() {
        return position;
    }

    /**
     * @param position the position to set
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * @return the shared
     */
    public boolean isShared() {
        return shared;
    }

    /**
     * @param shared the shared to set
     */
    public void setShared(boolean shared) {
        this.shared = shared;
    }

    /**
     * @return the abstractText
     */
    public String getAbstractText() {
        return abstractText;
    }

    /**
     * @param abstractText the abstractText to set
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
     * @return the bbox
     */
    public GPBBox getBbox() {
        return bbox;
    }

    /**
     * @param bbox
     *            the bbox to set
     */
    public void setBbox(GPBBox bbox) {
        this.bbox = bbox;
    }

    /**
     * @return the folder
     */
    public GPFolder getFolder() {
        return folder;
    }

    /**
     * @param folder
     *            the bbox to folder
     */
    public void setFolder(GPFolder folder) {
        this.folder = folder;
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
    //</editor-fold>

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        GPLayer other = (GPLayer) obj;
        if (id != other.id) {
            return false;
        }
        return true;
    }
}
