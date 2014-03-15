package org.pathways.openciss.model;

import java.io.Serializable;
import javax.persistence.*;

import java.util.Date;


/**
 * The persistent class for the role database table.
 * 
 */
@Entity
@Table(name="role")
public class Role implements Serializable {
	private static final long serialVersionUID = 1L;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="create_date")
	private Date createDate;

	@Column(name="create_user_key")
	private int createUserKey;

	private byte inactive;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="role_key")
	private int roleKey;

	@Column(name="role_name")
	private String roleName;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="update_date")
	private Date updateDate;

	@Column(name="update_user_key")
	private int updateUserKey;

	public Role() {
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

	public int getRoleKey() {
		return this.roleKey;
	}

	public void setRoleKey(int roleKey) {
		this.roleKey = roleKey;
	}

	public String getRoleName() {
		return this.roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
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