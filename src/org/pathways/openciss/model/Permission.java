package org.pathways.openciss.model;

import java.io.Serializable;
import javax.persistence.*;

import java.util.Date;


/**
 * The persistent class for the permission database table.
 * 
 */
@Entity
@Table(name="permission")
public class Permission implements Serializable {
	private static final long serialVersionUID = 1L;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="create_date")
	private Date createDate;

	@Column(name="create_user_key")
	private int createUserKey;

	private byte inactive;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="permission_key")
	private int permissionKey;

	@Column(name="permission_name")
	private String permissionName;

	@Column(name="role_key")
	private int roleKey;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="update_date")
	private Date updateDate;

	@Column(name="update_user_key")
	private int updateUserKey;

	public Permission() {
	}

	public Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public int getCreateUserKey() {
		return this.createUserKey;
	}

	public void setCreateUserKey(int createUserKey) {
		this.createUserKey = createUserKey;
	}

	public byte getInactive() {
		return this.inactive;
	}

	public void setInactive(byte inactive) {
		this.inactive = inactive;
	}

	public int getPermissionKey() {
		return this.permissionKey;
	}

	public void setPermissionKey(int permissionKey) {
		this.permissionKey = permissionKey;
	}

	public String getPermissionName() {
		return this.permissionName;
	}

	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}

	public int getRoleKey() {
		return this.roleKey;
	}

	public void setRoleKey(int roleKey) {
		this.roleKey = roleKey;
	}

	public Date getUpdateDate() {
		return this.updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public int getUpdateUserKey() {
		return this.updateUserKey;
	}

	public void setUpdateUserKey(int updateUserKey) {
		this.updateUserKey = updateUserKey;
	}

}