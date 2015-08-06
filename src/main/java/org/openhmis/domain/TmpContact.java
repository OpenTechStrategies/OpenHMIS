package org.openhmis.domain;

// Generated Aug 5, 2015 8:35:20 PM by Hibernate Tools 4.3.1

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * TmpContact generated by hbm2java
 */
@Entity
@Table(name = "TMP_CONTACT")
public class TmpContact implements java.io.Serializable {

	private Integer contactId;
	private Integer enrollmentId;
	private Date dateProvided;
	private Integer typeProvided;

	public TmpContact() {
	}

	public TmpContact(Integer enrollmentId, Date dateProvided,
			Integer typeProvided) {
		this.enrollmentId = enrollmentId;
		this.dateProvided = dateProvided;
		this.typeProvided = typeProvided;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "contactId", unique = true, nullable = false)
	public Integer getContactId() {
		return this.contactId;
	}

	public void setContactId(Integer contactId) {
		this.contactId = contactId;
	}

	@Column(name = "enrollmentId")
	public Integer getEnrollmentId() {
		return this.enrollmentId;
	}

	public void setEnrollmentId(Integer enrollmentId) {
		this.enrollmentId = enrollmentId;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "dateProvided", length = 10)
	public Date getDateProvided() {
		return this.dateProvided;
	}

	public void setDateProvided(Date dateProvided) {
		this.dateProvided = dateProvided;
	}

	@Column(name = "typeProvided")
	public Integer getTypeProvided() {
		return this.typeProvided;
	}

	public void setTypeProvided(Integer typeProvided) {
		this.typeProvided = typeProvided;
	}

}