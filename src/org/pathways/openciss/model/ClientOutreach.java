package org.pathways.openciss.model;

import java.io.Serializable;
import javax.persistence.*;

import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the client_outreach database table.
 * 
 */
@Entity
@Table(name="client_outreach")
public class ClientOutreach implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="client_key")
	private int clientKey;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="contact_date")
	private Date contactDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="engagement_date")
	private Date engagementDate;

	private BigDecimal latitude;

	private BigDecimal longitude;

	@Column(name="outreach_key")
	private int outreachKey;

	public ClientOutreach() {
	}

	public int getClientKey() {
		return this.clientKey;
	}

	public void setClientKey(int clientKey) {
		this.clientKey = clientKey;
	}

	public Date getContactDate() {
		return this.contactDate;
	}

	public void setContactDate(Date contactDate) {
		this.contactDate = contactDate;
	}

	public Date getEngagementDate() {
		return this.engagementDate;
	}

	public void setEngagementDate(Date engagementDate) {
		this.engagementDate = engagementDate;
	}

	public BigDecimal getLatitude() {
		return this.latitude;
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}

	public BigDecimal getLongitude() {
		return this.longitude;
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}

	public int getOutreachKey() {
		return this.outreachKey;
	}

	public void setOutreachKey(int outreachKey) {
		this.outreachKey = outreachKey;
	}

}