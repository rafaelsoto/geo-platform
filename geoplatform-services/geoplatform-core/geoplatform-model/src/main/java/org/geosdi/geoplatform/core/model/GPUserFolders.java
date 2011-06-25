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
import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
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
@Table(name = "gp_user_folders")
public class GPUserFolders implements Serializable {

    @Id
//    @EmbeddedId
    @AssociationOverrides({
        @AssociationOverride(name = "pk.user", joinColumns =
        @JoinColumn(name = "user_id")),
        @AssociationOverride(name = "pk.folder", joinColumns =
        @JoinColumn(name = "folder_id"))
    })
    private UserFolderPk pk = new UserFolderPk();
//    private UserFolderPk pk = null;
    //
//    @Column(name = "owner", nullable = false) // TODO ? DEL and use Permission.ADMINISTRATOR ?
//    private boolean owner = false;
    //
    @Column(name = "permission_mask", nullable = false)
    private int permissionMask = BasePermission.ADMINISTRATION.getMask();
    //    
    @Column(name = "alias")
    private String alias = null;
    //
    @Column(name = "position") // TODO ? nullable = false ?
    private int position = -1;
    //
    @Column(name = "checked")
    private boolean checked = false;
    //
    @ManyToOne(optional = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumns({
        @JoinColumn(name = "parent_user_id"),
        @JoinColumn(name = "parent_folder_id")})
    private GPUserFolders parentFolder;

//    /**
//     * @return the owner
//     */
//    public boolean isOwner() {
//        return owner;
//    }
//
//    /**
//     * @param owner
//     *            the owner to set
//     */
//    public void setOwner(boolean owner) {
//        this.owner = owner;
//    }
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
     * @return the parentFolder
     */
    public GPUserFolders getParent() {
        return parentFolder;
    }

    /**
     * @param parentFolder
     *            the parentFolder to set
     */
    public void setParent(GPUserFolders parentFolder) {
        this.parentFolder = parentFolder;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(this.getClass().getSimpleName()).append(" {");
        str.append(" PK=").append(pk);
        str.append(", permission=").append(permissionMask);
        str.append(", alias=").append(getAlias());
        str.append(", position=").append(position);
        str.append(", checked=").append(checked);
        if (parentFolder != null) {
            str.append(", parentFolder.PK=").append(parentFolder.getPk());
        } else {
            str.append(", parentFolder=NULL (this is a root folder)");
        }
        return str.append("}").toString();
    }

    /**
     * @return the pk
     */
    public UserFolderPk getPk() {
        return pk;
    }

    /**
     * @param pk
     *          the pk to set
     */
    public void setPk(UserFolderPk pk) {
        this.pk = pk;
    }

//    @Transient
    public GPUser getUser() {
        return getPk().getUser();
    }

    public void setUser(GPUser user) {
        this.getPk().setUser(user);
    }

//    @Transient
    public GPFolder getFolder() {
        return getPk().getFolder();
    }

    public void setFolder(GPFolder folder) {
        getPk().setFolder(folder);
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
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GPUserFolders that = (GPUserFolders) o;
        if (getPk() != null ? !getPk().equals(that.getPk()) : that.getPk() != null) {
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
        return (getPk() != null ? getPk().hashCode() : 0);
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
}
