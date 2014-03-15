package org.pathways.openciss.shared;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the hud_xml_3_blob database table.
 * 
 */
@Entity
@Table(name="hud_xml_3_blob")
public class HUDXML3Blob implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Column(name="csv_url1")
	private String csvUrl1;

	@Column(name="csv_url1_completed")
	private int csvUrl1Completed;

	@Column(name="csv_url1_indexed")
	private int csvUrl1Indexed;

	@Column(name="csv_url2")
	private String csvUrl2;

	@Column(name="csv_url2_completed")
	private int csvUrl2Completed;

	@Column(name="csv_url2_indexed")
	private int csvUrl2Indexed;

	@Column(name="csv_url3")
	private String csvUrl3;

	@Column(name="csv_url3_completed")
	private int csvUrl3Completed;

	@Column(name="csv_url3_indexed")
	private int csvUrl3Indexed;

	@Column(name="csv_url4")
	private String csvUrl4;

	@Column(name="csv_url4_completed")
	private int csvUrl4Completed;

	@Column(name="csv_url4_indexed")
	private int csvUrl4Indexed;

	@Column(name="csv_url5")
	private String csvUrl5;

	@Column(name="csv_url5_completed")
	private int csvUrl5Completed;

	@Column(name="csv_url5_indexed")
	private int csvUrl5Indexed;
	
	@Column(name="csv_url6")
	private String csvUrl6;

	@Column(name="csv_url6_completed")
	private int csvUrl6Completed;

	@Column(name="csv_url6_indexed")
	private int csvUrl6Indexed;

	@Column(name="csv_url7")
	private String csvUrl7;

	@Column(name="csv_url7_completed")
	private int csvUrl7Completed;

	@Column(name="csv_url7_indexed")
	private int csvUrl7Indexed;
	
	
	@Column(name="xml_url")
	private String xmlUrl;

	public HUDXML3Blob() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCsvUrl1() {
		return this.csvUrl1;
	}

	public void setCsvUrl1(String csvUrl1) {
		this.csvUrl1 = csvUrl1;
	}

	public int getCsvUrl1Completed() {
		return this.csvUrl1Completed;
	}

	public void setCsvUrl1Completed(int csvUrl1Completed) {
		this.csvUrl1Completed = csvUrl1Completed;
	}

	public int getCsvUrl1Indexed() {
		return this.csvUrl1Indexed;
	}

	public void setCsvUrl1Indexed(int csvUrl1Indexed) {
		this.csvUrl1Indexed = csvUrl1Indexed;
	}

	public String getCsvUrl2() {
		return this.csvUrl2;
	}

	public void setCsvUrl2(String csvUrl2) {
		this.csvUrl2 = csvUrl2;
	}

	public int getCsvUrl2Completed() {
		return this.csvUrl2Completed;
	}

	public void setCsvUrl2Completed(int csvUrl2Completed) {
		this.csvUrl2Completed = csvUrl2Completed;
	}

	public int getCsvUrl2Indexed() {
		return this.csvUrl2Indexed;
	}

	public void setCsvUrl2Indexed(int csvUrl2Indexed) {
		this.csvUrl2Indexed = csvUrl2Indexed;
	}

	public String getCsvUrl3() {
		return this.csvUrl3;
	}

	public void setCsvUrl3(String csvUrl3) {
		this.csvUrl3 = csvUrl3;
	}

	public int getCsvUrl3Completed() {
		return this.csvUrl3Completed;
	}

	public void setCsvUrl3Completed(int csvUrl3Completed) {
		this.csvUrl3Completed = csvUrl3Completed;
	}

	public int getCsvUrl3Indexed() {
		return this.csvUrl3Indexed;
	}

	public void setCsvUrl3Indexed(int csvUrl3Indexed) {
		this.csvUrl3Indexed = csvUrl3Indexed;
	}

	public String getCsvUrl4() {
		return this.csvUrl4;
	}

	public void setCsvUrl4(String csvUrl4) {
		this.csvUrl4 = csvUrl4;
	}

	public int getCsvUrl4Completed() {
		return this.csvUrl4Completed;
	}

	public void setCsvUrl4Completed(int csvUrl4Completed) {
		this.csvUrl4Completed = csvUrl4Completed;
	}

	public int getCsvUrl4Indexed() {
		return this.csvUrl4Indexed;
	}

	public void setCsvUrl4Indexed(int csvUrl4Indexed) {
		this.csvUrl4Indexed = csvUrl4Indexed;
	}

	public String getCsvUrl5() {
		return this.csvUrl5;
	}

	public void setCsvUrl5(String csvUrl5) {
		this.csvUrl5 = csvUrl5;
	}

	public int getCsvUrl5Completed() {
		return this.csvUrl5Completed;
	}

	public void setCsvUrl5Completed(int csvUrl5Completed) {
		this.csvUrl5Completed = csvUrl5Completed;
	}

	public int getCsvUrl5Indexed() {
		return this.csvUrl5Indexed;
	}

	public void setCsvUrl5Indexed(int csvUrl5Indexed) {
		this.csvUrl5Indexed = csvUrl5Indexed;
	}
		
	public String getCsvUrl6() {
		return this.csvUrl6;
	}

	public void setCsvUrl6(String csvUrl6) {
		this.csvUrl6 = csvUrl6;
	}

	public int getCsvUrl6Completed() {
		return this.csvUrl6Completed;
	}

	public void setCsvUrl6Completed(int csvUrl6Completed) {
		this.csvUrl6Completed = csvUrl6Completed;
	}

	public int getCsvUrl6Indexed() {
		return this.csvUrl6Indexed;
	}

	public void setCsvUrl6Indexed(int csvUrl6Indexed) {
		this.csvUrl6Indexed = csvUrl6Indexed;
	}

	public String getCsvUrl7() {
		return this.csvUrl7;
	}

	public void setCsvUrl7(String csvUrl7) {
		this.csvUrl7 = csvUrl7;
	}

	public int getCsvUrl7Completed() {
		return this.csvUrl7Completed;
	}

	public void setCsvUrl7Completed(int csvUrl7Completed) {
		this.csvUrl7Completed = csvUrl7Completed;
	}

	public int getCsvUrl7Indexed() {
		return this.csvUrl7Indexed;
	}

	public void setCsvUrl7Indexed(int csvUrl7Indexed) {
		this.csvUrl7Indexed = csvUrl7Indexed;
	}	
	
	public String getXmlUrl() {
		return this.xmlUrl;
	}

	public void setXmlUrl(String xmlUrl) {
		this.xmlUrl = xmlUrl;
	}

}