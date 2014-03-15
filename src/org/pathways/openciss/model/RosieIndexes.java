package org.pathways.openciss.model;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigInteger;


/**
 * The persistent class for the rosie_indexes database table.
 * 
 */
@Entity
@Table(name="rosie_indexes")
public class RosieIndexes implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private String id;

	private String file1index;

	private String file2index;

	private String file3index;

	private String file4index;

	private String file6index;

	private int used;

	@Column(name="xml_blob_id")
	private BigInteger xmlBlobId;

	public RosieIndexes() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFile1index() {
		return this.file1index;
	}

	public void setFile1index(String file1index) {
		this.file1index = file1index;
	}

	public String getFile2index() {
		return this.file2index;
	}

	public void setFile2index(String file2index) {
		this.file2index = file2index;
	}

	public String getFile3index() {
		return this.file3index;
	}

	public void setFile3index(String file3index) {
		this.file3index = file3index;
	}

	public String getFile4index() {
		return this.file4index;
	}

	public void setFile4index(String file4index) {
		this.file4index = file4index;
	}

	public String getFile6index() {
		return this.file6index;
	}

	public void setFile6index(String file6index) {
		this.file6index = file6index;
	}

	public int getUsed() {
		return this.used;
	}

	public void setUsed(int used) {
		this.used = used;
	}

	public BigInteger getXmlBlobId() {
		return this.xmlBlobId;
	}

	public void setXmlBlobId(BigInteger xmlBlobId) {
		this.xmlBlobId = xmlBlobId;
	}

}