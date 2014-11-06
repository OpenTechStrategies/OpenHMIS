package org.openhmis.vo;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RaceVO implements Serializable 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1294761108268233921L;
	
	private Integer codeKey;
	private String description;
	private String shortDesc;
	private String notes;
	private Integer recActiveGct;
	private Timestamp logDateTime;
	private Long logUserKey;
	public RaceVO() {
		super();
	}
	public RaceVO(Integer codeKey, String description, String shortDesc,
			String notes, Integer recActiveGct, Timestamp logDateTime,
			Long logUserKey) {
		super();
		this.codeKey = codeKey;
		this.description = description;
		this.shortDesc = shortDesc;
		this.notes = notes;
		this.recActiveGct = recActiveGct;
		this.logDateTime = logDateTime;
		this.logUserKey = logUserKey;
	}
	public Integer getCodeKey() {
		return codeKey;
	}
	public void setCodeKey(Integer codeKey) {
		this.codeKey = codeKey;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getShortDesc() {
		return shortDesc;
	}
	public void setShortDesc(String shortDesc) {
		this.shortDesc = shortDesc;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public Integer getRecActiveGct() {
		return recActiveGct;
	}
	public void setRecActiveGct(Integer recActiveGct) {
		this.recActiveGct = recActiveGct;
	}
	public Timestamp getLogDateTime() {
		return logDateTime;
	}
	public void setLogDateTime(Timestamp logDateTime) {
		this.logDateTime = logDateTime;
	}
	public Long getLogUserKey() {
		return logUserKey;
	}
	public void setLogUserKey(Long logUserKey) {
		this.logUserKey = logUserKey;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codeKey == null) ? 0 : codeKey.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((logDateTime == null) ? 0 : logDateTime.hashCode());
		result = prime * result
				+ ((logUserKey == null) ? 0 : logUserKey.hashCode());
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
		result = prime * result
				+ ((recActiveGct == null) ? 0 : recActiveGct.hashCode());
		result = prime * result
				+ ((shortDesc == null) ? 0 : shortDesc.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RaceVO other = (RaceVO) obj;
		if (codeKey == null) {
			if (other.codeKey != null)
				return false;
		} else if (!codeKey.equals(other.codeKey))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (logDateTime == null) {
			if (other.logDateTime != null)
				return false;
		} else if (!logDateTime.equals(other.logDateTime))
			return false;
		if (logUserKey == null) {
			if (other.logUserKey != null)
				return false;
		} else if (!logUserKey.equals(other.logUserKey))
			return false;
		if (notes == null) {
			if (other.notes != null)
				return false;
		} else if (!notes.equals(other.notes))
			return false;
		if (recActiveGct == null) {
			if (other.recActiveGct != null)
				return false;
		} else if (!recActiveGct.equals(other.recActiveGct))
			return false;
		if (shortDesc == null) {
			if (other.shortDesc != null)
				return false;
		} else if (!shortDesc.equals(other.shortDesc))
			return false;
		return true;
	}
}