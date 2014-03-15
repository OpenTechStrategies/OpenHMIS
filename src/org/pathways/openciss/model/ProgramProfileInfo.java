package org.pathways.openciss.model;

import java.io.Serializable;
import javax.persistence.*;

import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the program_profile_info database table.
 * 
 */
@Entity
@Table(name="program_profile_info")
public class ProgramProfileInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="agency_key")
	private int agencyKey;

	@Column(name="agency_name")
	private String agencyName;

	@Column(name="coc_key")
	private BigDecimal cocKey;

	@Column(name="coc_name")
	private String cocName;

	@Column(name="contact_name")
	private String contactName;

	@Column(name="contact_phone")
	private String contactPhone;

	@Column(name="feed_source")
	private int feedSource;

	@Column(name="hmis_participant")
	private String hmisParticipant;

	@Column(name="max_stay_days")
	private BigDecimal maxStayDays;

	@Column(name="mckinney_vento")
	private String mckinneyVento;

	@Column(name="program_address")
	private String programAddress;

	@Column(name="program_address_full")
	private String programAddressFull;

	@Column(name="program_city")
	private String programCity;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="program_key")
	private int programKey;

	@Column(name="program_name")
	private String programName;

	@Column(name="program_type")
	private String programType;

	@Column(name="program_type_code")
	private BigDecimal programTypeCode;

	@Column(name="program_zip")
	private String programZip;

	@Column(name="site_geocode")
	private String siteGeocode;

	@Column(name="site_key")
	private BigDecimal siteKey;

	@Column(name="site_name")
	private String siteName;

	@Column(name="target_pop_a_code")
	private BigDecimal targetPopACode;

	@Column(name="target_pop_a_name")
	private String targetPopAName;

	@Column(name="target_pop_b_code")
	private BigDecimal targetPopBCode;

	@Column(name="target_pop_b_name")
	private String targetPopBName;

	@Column(name="units_available")
	private int unitsAvailable;

	@Column(name="units_occupied")
	private int unitsOccupied;

	@Column(name="units_total")
	private int unitsTotal;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="update_time_stamp")
	private Date updateTimeStamp;

	public ProgramProfileInfo() {
	}

	public int getAgencyKey() {
		return this.agencyKey;
	}

	public void setAgencyKey(int agencyKey) {
		this.agencyKey = agencyKey;
	}

	public String getAgencyName() {
		return this.agencyName;
	}

	public void setAgencyName(String agencyName) {
		this.agencyName = agencyName;
	}

	public BigDecimal getCocKey() {
		return this.cocKey;
	}

	public void setCocKey(BigDecimal cocKey) {
		this.cocKey = cocKey;
	}

	public String getCocName() {
		return this.cocName;
	}

	public void setCocName(String cocName) {
		this.cocName = cocName;
	}

	public String getContactName() {
		return this.contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactPhone() {
		return this.contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public int getFeedSource() {
		return this.feedSource;
	}

	public void setFeedSource(int feedSource) {
		this.feedSource = feedSource;
	}

	public String getHmisParticipant() {
		return this.hmisParticipant;
	}

	public void setHmisParticipant(String hmisParticipant) {
		this.hmisParticipant = hmisParticipant;
	}

	public BigDecimal getMaxStayDays() {
		return this.maxStayDays;
	}

	public void setMaxStayDays(BigDecimal maxStayDays) {
		this.maxStayDays = maxStayDays;
	}

	public String getMckinneyVento() {
		return this.mckinneyVento;
	}

	public void setMckinneyVento(String mckinneyVento) {
		this.mckinneyVento = mckinneyVento;
	}

	public String getProgramAddress() {
		return this.programAddress;
	}

	public void setProgramAddress(String programAddress) {
		this.programAddress = programAddress;
	}

	public String getProgramAddressFull() {
		return this.programAddressFull;
	}

	public void setProgramAddressFull(String programAddressFull) {
		this.programAddressFull = programAddressFull;
	}

	public String getProgramCity() {
		return this.programCity;
	}

	public void setProgramCity(String programCity) {
		this.programCity = programCity;
	}

	public int getProgramKey() {
		return this.programKey;
	}

	public void setProgramKey(int programKey) {
		this.programKey = programKey;
	}

	public String getProgramName() {
		return this.programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	public String getProgramType() {
		return this.programType;
	}

	public void setProgramType(String programType) {
		this.programType = programType;
	}

	public BigDecimal getProgramTypeCode() {
		return this.programTypeCode;
	}

	public void setProgramTypeCode(BigDecimal programTypeCode) {
		this.programTypeCode = programTypeCode;
	}

	public String getProgramZip() {
		return this.programZip;
	}

	public void setProgramZip(String programZip) {
		this.programZip = programZip;
	}

	public String getSiteGeocode() {
		return this.siteGeocode;
	}

	public void setSiteGeocode(String siteGeocode) {
		this.siteGeocode = siteGeocode;
	}

	public BigDecimal getSiteKey() {
		return this.siteKey;
	}

	public void setSiteKey(BigDecimal siteKey) {
		this.siteKey = siteKey;
	}

	public String getSiteName() {
		return this.siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public BigDecimal getTargetPopACode() {
		return this.targetPopACode;
	}

	public void setTargetPopACode(BigDecimal targetPopACode) {
		this.targetPopACode = targetPopACode;
	}

	public String getTargetPopAName() {
		return this.targetPopAName;
	}

	public void setTargetPopAName(String targetPopAName) {
		this.targetPopAName = targetPopAName;
	}

	public BigDecimal getTargetPopBCode() {
		return this.targetPopBCode;
	}

	public void setTargetPopBCode(BigDecimal targetPopBCode) {
		this.targetPopBCode = targetPopBCode;
	}

	public String getTargetPopBName() {
		return this.targetPopBName;
	}

	public void setTargetPopBName(String targetPopBName) {
		this.targetPopBName = targetPopBName;
	}

	public int getUnitsAvailable() {
		return this.unitsAvailable;
	}

	public void setUnitsAvailable(int unitsAvailable) {
		this.unitsAvailable = unitsAvailable;
	}

	public int getUnitsOccupied() {
		return this.unitsOccupied;
	}

	public void setUnitsOccupied(int unitsOccupied) {
		this.unitsOccupied = unitsOccupied;
	}

	public int getUnitsTotal() {
		return this.unitsTotal;
	}

	public void setUnitsTotal(int unitsTotal) {
		this.unitsTotal = unitsTotal;
	}

	public Date getUpdateTimeStamp() {
		return this.updateTimeStamp;
	}

	public void setUpdateTimeStamp(Date updateTimeStamp) {
		this.updateTimeStamp = updateTimeStamp;
	}

}