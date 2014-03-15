package org.pathways.openciss.model;

import java.io.Serializable;
import javax.persistence.*;

import java.util.Date;


/**
 * The persistent class for the user database table.
 * 
 */
@Entity
@Table(name="user")
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="create_date")
	private Date createDate;

	@Column(name="create_user_key")
	private int createUserKey;

	private byte inactive;

	@Column(name="name_first")
	private String nameFirst;

	@Column(name="name_last")
	private String nameLast;

	@Column(name="password_enc")
	private String passwordEnc;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="update_date")
	private Date updateDate;

	@Column(name="update_user_key")
	private int updateUserKey;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="user_key")
	private int userKey;

	private String username;

	public User() {
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

	public String getNameFirst() {
		return this.nameFirst;
	}

	public void setNameFirst(String nameFirst) {
		this.nameFirst = nameFirst;
	}

	public String getNameLast() {
		return this.nameLast;
	}

	public void setNameLast(String nameLast) {
		this.nameLast = nameLast;
	}

	public String getPasswordEnc() {
		return this.passwordEnc;
	}

	public void setPasswordEnc(String passwordEnc) {
		this.passwordEnc = passwordEnc;
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

	public int getUserKey() {
		return this.userKey;
	}

	public void setUserKey(int userKey) {
		this.userKey = userKey;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}