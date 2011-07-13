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
package org.geosdi.geoplatform.core.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;

/**
 * @author Vincenzo Monteverde
 * @email vincenzo.monteverde@geosdi.org - OpenPGP key ID 0xB25F4B38
 *
 */
@Entity
@Table(name = "gp_user_folders", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "folder_id"})})
public class GPUserFolders implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GP_USER_FOLDERS_SEQ")
    @SequenceGenerator(name = "GP_USER_FOLDERS_SEQ", sequenceName = "GP_USER_FOLDERS_SEQ")
    private long id = -1;
    //
    @ManyToOne(optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
//    @org.hibernate.annotations.Index(name = "USER_INDEX") // TODO Uncomment
    private GPUser user;
    //
    @ManyToOne(optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
//    @org.hibernate.annotations.Index(name = "FOLDER_INDEX") // TODO Uncomment
    private GPFolder folder;
    //
    @Column(name = "permission_mask", nullable = false)
    private int permissionMask = BasePermission.ADMINISTRATION.getMask();
    //    
    @Column(name = "alias_name")
    private String aliasName = null;
    //
    @Column(name = "position") // TODO ? nullable = false ?
    private int position = -1;
    //
    @Column(name = "checked")
    private boolean checked = false;
    //
    @ManyToOne(optional = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private GPUserFolders parent;

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id
     *          the id to set
     */
    public void setId(long id) {
        this.id = id;
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

    /**
     * @param user
     *          the user to set
     * @param folder
     *          the folder to set
     */
    public void setUserAndFolder(GPUser user, GPFolder folder) {
        this.user = user;
        this.folder = folder;
    }

    /**
     * @return the permissionMask
     */
    public int getPermissionMask() {
        return permissionMask;
    }

    /**
     * @param permissionMask
     *          the permissionMask to set
     */
    public void setPermissionMask(int permissionMask) {
        this.permissionMask = permissionMask;
    }

    /**
     * @return the alias
     */
    public String getAliasName() {
        return aliasName;
    }

    /**
     * @param aliasName
     *          the aliasName to set
     */
    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
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
     * @return the checked
     */
    public boolean isChecked() {
        return checked;
    }

    /**
     * @param checked
     *            the checked to set
     */
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    /**
     * @return the parent
     */
    public GPUserFolders getParent() {
        return parent;
    }

    /**
     * @param parent
     *            the parent to set
     */
    public void setParent(GPUserFolders parent) {
        this.parent = parent;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
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
        str.append(", permission=").append(permissionMask);
        str.append(", aliasName=").append(aliasName);
        str.append(", position=").append(position);
        str.append(", checked=").append(checked);
        if (parent != null) {
            str.append(", parent.id=").append(parent.getId());
        } else {
            str.append(", parent=NULL (this is a root folder)");
        }
        return str.append("}").toString();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final GPUserFolders other = (GPUserFolders) obj;
        if (this.id != other.id) {
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
}
