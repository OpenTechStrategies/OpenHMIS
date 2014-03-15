package org.pathways.openciss.model;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the path_client database table.
 * 
 */
@Entity
@Table(name="path_client")
public class PathClient implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="client_key")
	private int clientKey;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="date_of_birth")
	private Date dateOfBirth;

	@Column(name="dob_type")
	private BigDecimal dobType;

	@Column(name="ethnicity_code")
	private BigDecimal ethnicityCode;

	@Column(name="gender_code")
	private BigDecimal genderCode;

	@Column(name="id_type")
	private String idType;
	
	@Column(name="inactive")
	private Integer inactive;

	@Column(name="name_first")
	private String nameFirst;

	@Column(name="name_first_alias")
	private String nameFirstAlias;

	@Column(name="name_last")
	private String nameLast;

	@Column(name="name_last_alias")
	private String nameLastAlias;

	@Column(name="name_middle")
	private String nameMiddle;

	@Column(name="name_suffix")
	private String nameSuffix;

	@Column(name="race_code")
	private BigDecimal raceCode;

	private int ssn;

	@Column(name="ssn_quality")
	private int ssnQuality;

	@Column(name="unaccompanied_child")
	private String unaccompaniedChild;

	@Column(name="veteran_status_code")
	private BigDecimal veteranStatusCode;

	public PathClient() {
	}

	public int getClientKey() {
		return this.clientKey;
	}

	public void setClientKey(int clientKey) {
		this.clientKey = clientKey;
	}

	public Date getDateOfBirth() {
		return this.dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public BigDecimal getDobType() {
		return this.dobType;
	}

	public void setDobType(BigDecimal dobType) {
		this.dobType = dobType;
	}

	public BigDecimal getEthnicityCode() {
		return this.ethnicityCode;
	}

	public void setEthnicityCode(BigDecimal ethnicityCode) {
		this.ethnicityCode = ethnicityCode;
	}

	public BigDecimal getGenderCode() {
		return this.genderCode;
	}

	public void setGenderCode(BigDecimal genderCode) {
		this.genderCode = genderCode;
	}

	public String getIdType() {
		return this.idType;
	}

	public void setIdType(String idType) {
		this.idType = idType;
	}

	public Integer getInactive() {
		return this.inactive;
	}

	public void setInactive(Integer inactive) {
		this.inactive = inactive;
	}

	public String getNameFirst() {
		return this.nameFirst;
	}

	public void setNameFirst(String nameFirst) {
		this.nameFirst = nameFirst;
	}

	public String getNameFirstAlias() {
		return this.nameFirstAlias;
	}

	public void setNameFirstAlias(String nameFirstAlias) {
		this.nameFirstAlias = nameFirstAlias;
	}

	public String getNameLast() {
		return this.nameLast;
	}

	public void setNameLast(String nameLast) {
		this.nameLast = nameLast;
	}

	public String getNameLastAlias() {
		return this.nameLastAlias;
	}

	public void setNameLastAlias(String nameLastAlias) {
		this.nameLastAlias = nameLastAlias;
	}

	public String getNameMiddle() {
		return this.nameMiddle;
	}

	public void setNameMiddle(String nameMiddle) {
		this.nameMiddle = nameMiddle;
	}

	public String getNameSuffix() {
		return this.nameSuffix;
	}

	public void setNameSuffix(String nameSuffix) {
		this.nameSuffix = nameSuffix;
	}

	public BigDecimal getRaceCode() {
		return this.raceCode;
	}

	public void setRaceCode(BigDecimal raceCode) {
		this.raceCode = raceCode;
	}

	public int getSsn() {
		return this.ssn;
	}

	public void setSsn(int ssn) {
		this.ssn = ssn;
	}

	public int getSsnQuality() {
		return this.ssnQuality;
	}

	public void setSsnQuality(int ssnQuality) {
		this.ssnQuality = ssnQuality;
	}

	public String getUnaccompaniedChild() {
		return this.unaccompaniedChild;
	}

	public void setUnaccompaniedChild(String unaccompaniedChild) {
		this.unaccompaniedChild = unaccompaniedChild;
	}

	public BigDecimal getVeteranStatusCode() {
		return this.veteranStatusCode;
	}

	public void setVeteranStatusCode(BigDecimal veteranStatusCode) {
		this.veteranStatusCode = veteranStatusCode;
	}

}