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
package org.geosdi.geoplatform.core.model;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Composite-key of GPUserFolders (Weak entity)
 * 
 * @author Vincenzo Monteverde
 * @email vincenzo.monteverde@geosdi.org - OpenPGP key ID 0xB25F4B38
 *
 */
@Embeddable
public class UserFolderPk implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -3603166230086425443L;
    //
    @ManyToOne(optional = false)
    // Delete on cascade don't run for weak entity
//    @ManyToOne(optional = false, cascade = javax.persistence.CascadeType.REMOVE)
//    @OnDelete(action = OnDeleteAction.CASCADE)
//    @Cascade(CascadeType.DELETE)
    private GPUser user;
    //
    @ManyToOne(optional = false)
    // Delete on cascade don't run for weak entity
//    @ManyToOne(optional = false, cascade = javax.persistence.CascadeType.REMOVE)
//    @OnDelete(action = OnDeleteAction.CASCADE)
//    @Cascade(CascadeType.DELETE_ORPHAN)
    private GPFolder folder;

    /**
     * Contructor no-args
     */
    public UserFolderPk() {
    }

    /**
     * Contructor with args
     * @param user
     * @param folder 
     */
    public UserFolderPk(GPUser user, GPFolder folder) {
        this.user = user;
        this.folder = folder;
    }

    /**
     * @return the user
     */
    public GPUser getUser() {
        return user;
    }

    /**
     * @param user
     *          the user to set
     */
    public void setUser(GPUser user) {
        this.user = user;
    }

    /**
     * @return the folder
     */
    public GPFolder getFolder() {
        return folder;
    }

    /**
     * @param folder
     *          the folder to set
     */
    public void setFolder(GPFolder folder) {
        this.folder = folder;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        UserFolderPk that = (UserFolderPk) o;
        if (user != null ? !user.equals(that.user) : that.user != null) {
            return false;
        }
        if (folder != null ? !folder.equals(that.folder) : that.folder != null) {
            return false;
        }

        return true;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result;
        result = (user != null ? user.hashCode() : 0);
        result = 71 * result + (folder != null ? folder.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(this.getClass().getSimpleName()).append(" {");
        if (user != null) {
            str.append(" user.username=").append(user.getUsername());
            str.append("(id=").append(user.getId()).append(")");
        } else {
            str.append(" user=null");
        }
        if (folder != null) {
            str.append(", folder.name=").append(folder.getName());
            str.append("(id=").append(folder.getId()).append(")");
        } else {
            str.append(", folder=null");
        }
        return str.append("}").toString();
    }
}
