package org.pathways.openciss.server;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.nio.channels.Channels;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.pathways.openciss.info.hmis.schema._3_0.airs_3_0_mod.TSite;
import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.Agency;
import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.HMISServiceEvent;
import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.HealthStatus;
import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.FundingSourceServiceEvent;
import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.Household;
import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.IncomeAndSource;
import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.Destination;
import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.IncomeSourceCode;
import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.Member;
import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.Members;
import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.NonNegativeInteger;
import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.Person;
import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.PersonAddress;
import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.PersonHistorical;
import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.PhoneNumber;
import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.PriorResidence;
import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.Service;
import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.SevenValDKRefused2;
import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.SixValDKRefused2;
import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.ServiceEvent;
import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.SiteService;
import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.SiteServiceParticipation;
import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.Source;
import org.pathways.openciss.model.RosieIndexes;
import org.pathways.openciss.model.RosieRecords;
import org.pathways.openciss.shared.EMF;
import org.pathways.openciss.shared.HUDXML3Blob;
import org.pathways.openciss.shared.HUDXML3BlobService;
import org.pathways.openciss.shared.RosieIndexesService;
import org.pathways.openciss.shared.RosieRecordsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVWriter;

public class RosieCSVBlobServlet extends HttpServlet {
	
	private final GcsService gcsService = GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());
	private static final transient Logger log = LoggerFactory.getLogger(RosieCSVBlobServlet.class);
	private static final long serialVersionUID = 1L;
	List <Agency> agencyList;
	List <Household> householdList;
	List<Person> personList;
	List <Source> sourceList;
	BlobKey hudXMLURL;
	JAXBElement<?> sourcesJAXBElement;
	XMLInputFactory xmlif;
	Unmarshaller unmarshaller;
	
	static String[] csv1Record = new String[27];
	static String[] csv2Record = new String[27];
	static String[] csv3Record = new String[27];
	static String[] csv4Record = new String[27];
	static String[] csv5Record = new String[27];
	static String[] csv6Record = new String[27];
	static String[] csv7Record = new String[27];
	static String[][] csvRecords = {csv1Record, csv2Record, csv3Record, csv4Record, csv5Record, csv6Record, csv7Record };


//	static String[] csv1Record = {"AgencyCode","TransactionDate_mmddyy","TransactionTime_hhmmss",
//		"SSNFamilyHead", "RecordType_1","StreetAddress","Apartment","City","State",
//		"Zip5","StreetAddress","Apartment","City","State","Zip5","WorkTelephone",
//		"HomeTelephone","ReasonForCrisis_Code","FamilySize","HousingSituation_Code",
//		"LengthInHousingSituation_Code","NumberNeedingShelter","InsuranceType1_Code",
//		"InsuranceType2_Code","ChronicHomeless_Y_N","Prior_Loc_Y_N"};
//	static String[] csv2Record = {"AgencyCode","TransactionDate_mmddyy","TransactionTime_hhmmss",
//		"SSNFamilyHead","RecordType_2","FamilyMemberNumber","FamilyMemberSSN",
//		"Relationship_Code","FirstName","MiddleName","LastName","DateOfBirth_mmddyy",
//		"Gender_M_F","Race1_Code","Race2_Code","Hispanic_Y_N","Pregnant_Y_N",
//		"MonthsPregnant","NeedShelter_Y_N","Veteran_Code","StreetAddress","Apartment",
//		"City","State","Zip5","HousingSituation_Code","LengthInHousingSituation_Code"};
//	static String[] csv3Record = {"AgencyCode","TransactionDate_mmddyy","TransactionTime_hhmmss",
//		"SSNFamilyHead","RecordType_3","FamilyMemberNumber","Income_ExpenseType_Code",
//		"MonthlyAmount"};
//	static String[] csv4Record = {"AgencyCode","TransactionDate_mmddyy","TransactionTime_hhmmss",
//		"SSNFamilyHead","RecordType_4","FamilyMemberNumber","HealthProblem_Code",
//		"HealthProblemDescription"};
//	static String[] csv5Record = {"AgencyCode","TransactionDate_mmddyy","TransactionTime_hhmmss",
//		"SSNFamilyHead","RecordType_5","FamilyMemberNumber","Status_Code",
//		"Status_Description","FinancialAssistance_Amount","Destination_Code"};
//	static String[] csv6Record = {"AgencyCode","TransactionDate_mmddyy","TransactionTime_hhmmss",
//		"SSNFamilyHead","RecordType_6","FamilyMemberNumber","AssistanceProvided_Code",
//		"AssistanceProvided_Description","AssistanceProvided_Amount"};
//	static String[] csv7Record = {"AgencyCode","TransactionDate_mmddyy","TransactionTime_hhmmss",
//		"SSNFamilyHead","RecordType_7","DateIfMoveIn_mmddyy","DateIfMoveOut_mmddyy",
//		"Bed_UnitNumber","ReasonForLeaving_Code","Destination_Code"};
	int householdMemberCounterCSV2;
	public static final String BUCKETNAME = "rosie";
	public static final String FILENAME1 = "fileRosieCSV1FamilyInformation";
	public static final String FILENAME2 = "fileRosieCSV2FamilyMembers";
	public static final String FILENAME3 = "fileRosieCSV3FamilyIncomeAndExpense";
	public static final String FILENAME4 = "fileRosieCSV4MemberHealthProblems";
	public static final String FILENAME5 = "fileRosieCSV5ProgramsProvided";
	public static final String FILENAME6 = "fileRosieCSV6FamilyAssistanceProvided";
	public static final String FILENAME7 = "fileRosieCSV7MoveInMoveOutTransaction";
	
	public void init() {
		try {
			xmlif = XMLInputFactory.newInstance();
		} catch (Exception e){}
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {		
		log.info("doGet in RosieCSVBlobServlet entered");
		//this needs to change to the GCS key in the future?
		hudXMLURL = new BlobKey(req.getParameter("hud_xml_url"));
		String[] blobStoreURL = new String[5];
		// check the completion state of each of the files by reading from the datastore
		    // look at the last/most recent record (either in file or in datastore/cloud sql)
		
		// if file1 already started and not finished, resume file 1
			// get last marker processed from file or db?  what is the key?
			// continue, writing out each time
		HUDXML3BlobService hxbs = new HUDXML3BlobService();
		log.info("trying to get HUDXML3Blob object from Key String: " + hudXMLURL.getKeyString());
		HUDXML3Blob blob = hxbs.getHUDXML3BlobFromXMLKey(hudXMLURL.getKeyString());
		if (blob != null) {
			String id = blob.getId().toString();
			if (!recordExists(id)) {
				log.info("for HUD XML blob record: " + id + " nothing has been done, not even file 1 indexing, so start up first time.");
				startUpFirstTime(id);
			} 
			else {
				log.info("HUD XML blob record: " + id + " already exists, so continuing from past session.");
				//everything keys of the household index
				if (!finishedConverting(id)) {
					convertHUDXMLToRosieCSV2(id);
					// set as finished in database
					blob = hxbs.getHUDXML3BlobFromXMLKey(hudXMLURL.getKeyString());
					blob.setCsvUrl2Completed(1);
					hxbs.updateHUDXML3Blob(blob);
				}
			}
			log.info("finished storing in db, now writing to a GCS file");
			writeToGoogleCloudStorage();
		}
		else {
			log.info("We have a problem, since the database didn't create an entry when uploading the HUDXML file");
		}
		//if no record for such a url, do all the things we always do to start things up as before

		//make response (this isn't really being used right now by the client, because of the long/unknown processing time and shutdowns
		resp.setHeader("Content-Type", "application/json");
		//create JSON response of blobStoreURL contents
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode blobStoreURLs = mapper.createArrayNode();
		for (int i=0; i<blobStoreURL.length; i++) {
			blobStoreURLs.add(blobStoreURL[i]);  
		}
		log.info("Responding with blobStoreURLs.toString()" + blobStoreURLs.toString());
		resp.getOutputStream().println(blobStoreURLs.toString());	
	}
	
	void startUpFirstTime(String id) {
		log.info("inside startUpFirstTime");
		//clear database of RosieIndexes
		log.info("calling delete all Rosie indexes");
		RosieIndexesService ris = new RosieIndexesService();
		ris.deleteAllRosieIndexes();
		startRecordsCreation();
		indexHouseholds(id);
		// set as finished in database
		HUDXML3BlobService hxbs = new HUDXML3BlobService();
		HUDXML3Blob blob = hxbs.getHUDXML3BlobFromXMLKey(hudXMLURL.getKeyString());
		//blob.setCsvUrl1Completed(1);
		//hxbs.updateHUDXML3Blob(blob);
		//blob = null;
		convertHUDXMLToRosieCSV2(id);
		blob = hxbs.getHUDXML3BlobFromXMLKey(hudXMLURL.getKeyString());
		blob.setCsvUrl2Completed(1);
		hxbs.updateHUDXML3Blob(blob);
		blob = null;
		log.info("finished processing in one pass!");
	}
	
	void startRecordsCreation() {
		// returns the blobstore urls for all of the to-be converted Rosie csv files, with headers  
		// the input string is the url of the uploaded input HUD XML file it needs to convert 
		//clear records
		for (int i = 0; i < csvRecords.length; i++) 
			for (int j=0; j < csvRecords[i].length; j++)
				csvRecords[i][j] = "";		
	}

	boolean recordExists(String id) {
		boolean result = false;
		HUDXML3BlobService hxbs = new HUDXML3BlobService();
		HUDXML3Blob blob = hxbs.getHUDXML3Blob(id);
		if (blob != null) {
			if (blob.getCsvUrl2Indexed() == 1) {
				result = true;
			}
		}
		return result;
	}

	String [] getNextIndexToProcess(String id) {
		log.info("inside getNextIndexToProcess");
		String[] nextIndexRecord = new String[2];
		//get next index for file from persistence
		EntityManager em;
		em = EMF.get().createEntityManager();
		String file_index_column = "file2index";
		//This needs to be improved, but it works fine.  It just doesn't go in true ascending order
		Query q = em.createQuery ("SELECT rosie FROM RosieIndexes rosie WHERE rosie.xmlBlobId = :xmlBlobId AND rosie." + file_index_column + " = (SELECT MIN(rosie." + file_index_column + ") FROM RosieIndexes rosie WHERE rosie.used = 0)");
		
		if (id != null) {
			q.setParameter ("xmlBlobId",(new BigInteger(id)));
			RosieIndexes r = null;
			try {
				@SuppressWarnings("unchecked")
				List<RosieIndexes> resultList = q.getResultList();
				for (RosieIndexes i : resultList) {
					log.info("RosieIndexes record ID returned in getNextIndexToProcess is: " + i.getId());
				}
				r = (RosieIndexes) q.getSingleResult();
			} 
			catch (NoResultException nr) {
				log.info("no results from query.  wait, then try again.");
				try {
					  Thread.sleep(70);
					  r = (RosieIndexes) q.getSingleResult();
				}  
				catch (NoResultException nr1) {
					log.info("no results from query.  wait, then try again.");
					try {
						Thread.sleep(200);
						r = (RosieIndexes) q.getSingleResult();
					} 
					catch (NoResultException nr2) {
						log.info("no results from query, even after waiting twice.");
						nextIndexRecord[0] = "-1";
						nextIndexRecord[1] = "-1";
						return nextIndexRecord;
						
					}
					catch (InterruptedException ie) {
						log.info("Timer issue: ");
						ie.printStackTrace();
					}
				}
				catch (InterruptedException ie) {
					log.info("Timer issue: ");
					ie.printStackTrace();
				}
			}
			
			String idResult = r.getId().toString();
			String indexResult = null;
			indexResult = r.getFile2index();
			nextIndexRecord[0] = idResult;
			nextIndexRecord[1] = indexResult;
			log.info("getNextIndexToProcess got next id: " + idResult + " and next index: " + indexResult);
			if (nextIndexRecord[0] == null || nextIndexRecord[0].isEmpty()) {
				nextIndexRecord[0] = "-1";
				nextIndexRecord[1] = "-1";
			}
		}
		return nextIndexRecord;
	}
	
	InputStream openXMLStream(BlobKey blobKeyHUDXML) {
		InputStream inputStream = null;
		try {	
			log.info("Using BlobstoreInputStream");
			inputStream = new BlobstoreInputStream(blobKeyHUDXML);
		} catch (Exception e) {
			log.info("Error reading from BlobstoreInputStream:" + e.toString());
		}			

		//unzip the input stream
		InputStream zis = null;
		try {
			zis = new BufferedInputStream(new GZIPInputStream(inputStream));
		} catch (IOException e) {
			log.info("Problem creating BufferedInputStream from GZIP" + e.toString());
		}
		return zis;
	}
	
	@SuppressWarnings("unchecked")
	boolean finishedIndexing(String id) {
		boolean status = false;
		//get status for file from persistence
		List<Integer> results = null;
		EntityManager em;
		em =EMF.get().createEntityManager();
		//String file_column = "csv_url2_indexed";
		String file_column = "csvUrl2Indexed";
		log.info("executing query: SELECT hblob." + file_column + " FROM HUDXML3Blob AS hblob WHERE hblob.id = :" + id);
		Query q = em.createQuery ("SELECT hblob." + file_column + " FROM HUDXML3Blob AS hblob WHERE hblob.id = :id");

		if (id != null) {
			Long idLong = Long.valueOf(id); 
			q.setParameter ("id", idLong);
			try {
				Thread.sleep(500);
				results = q.getResultList();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (results.get(0) == 1) {
			status = true;
		} else {log.warn("reindexing households, because the index wasn't detected as a '1'");}
		return status;
	}
	@SuppressWarnings("unchecked")
	boolean finishedConverting(String id) {
		boolean status = false;
		//get status for file from persistence
		List<Integer> results = null;
		EntityManager em;
		em =EMF.get().createEntityManager();		
		String file_column = "csvUrl2Completed";
		Query q = em.createQuery ("SELECT hblob." + file_column + " FROM HUDXML3Blob AS hblob WHERE hblob.id = :id");
		if (id != null) {
			q.setParameter ("id", Long.valueOf(id));
			//@SuppressWarnings("unchecked")
			results = q.getResultList();
		}
		if (results.get(0) == 1) {
			status = true;
		}
		return status;
	}
	
	void convertHUDXMLToRosieCSV2(String id) {

		//check if finished indexing
		if (finishedIndexing(id)) {
			log.info("it was determined that households have finished indexing");
			// pick an unprocessed index
			boolean keepRunning = true;
			while (keepRunning) {
				String[] nextIndexRecord = getNextIndexToProcess(id);
				String nextIndex = nextIndexRecord[1];
				if (nextIndex == null || nextIndex == "-1") {
					keepRunning = false;
				} else {
					processNextCSV2(nextIndex);
					//mark nextIndex as processed
					RosieIndexesService ris = new RosieIndexesService();
					RosieIndexes ri = ris.getRosieIndexes(nextIndexRecord[0]);
					ri.setUsed(1);
					ris.updateRosieIndexes(ri);
				}
			}
		} 
		//else index it
		else {
			indexHouseholds(id);
			//recurse this method to process remaining indexes, and remember to save/write as you go
			convertHUDXMLToRosieCSV2(id);
		}
	}

	void indexHouseholds(String id) {
		log.info("indexing file 2");
		InputStream zis = openXMLStream(hudXMLURL);
		
		//JAXBContext jc = null;

		//Use StAX to get all the Households one for each File 2  Record 
		XMLStreamReader xmlr0 = null;

		try {
			// set up a StAX reader
			xmlr0 = xmlif.createXMLStreamReader(zis);
			//Using StAX to get all the household ids for File 2 Household Membership Generation
			try {
				while(xmlr0.hasNext()) {
					// Get and check each SiteService for the right SiteServiceID
					xmlr0.next();
					if (xmlr0.isStartElement()) {
						// System.out.println("local name is: " + xmlr.getLocalName());
						if (xmlr0.getLocalName().equals("Household") && xmlr0.isStartElement()) {
							xmlr0.require(XMLStreamConstants.START_ELEMENT, null, "Household");
							// get the HouseholdID element following soon after this element
							while(xmlr0.hasNext()) {
								xmlr0.next();
								if (xmlr0.isStartElement()) {
									if (xmlr0.getLocalName().equals("IDStr")) {
										xmlr0.require(XMLStreamConstants.START_ELEMENT, null, "IDStr");
										//save this index in db for later sequential processing
										String index = xmlr0.getElementText();
										RosieIndexes r = new RosieIndexes();
										r.setXmlBlobId(new BigInteger(id));
										r.setFile2index(index);
										RosieIndexesService ris = new RosieIndexesService();
										//log.info("file 2 being indexed");
										ris.createRosieIndexes(r);
										break;
									} 
									else if (xmlr0.getLocalName().equals("IDNum")) {
										xmlr0.require(XMLStreamConstants.START_ELEMENT, null, "IDNum");
										//save this index in db for later sequential processing
										String index = xmlr0.getElementText();
										RosieIndexes r = new RosieIndexes();
										r.setXmlBlobId(new BigInteger(id));
										r.setFile2index(index);
										RosieIndexesService ris = new RosieIndexesService();
										//log.info("file 2 being indexed");
										ris.createRosieIndexes(r);
										break;
									}
								}
							}
						} 	
					}
				}
				//mark this file as indexed
				HUDXML3BlobService hxbs = new HUDXML3BlobService();
				log.info("hudXMLURL.getKeyString() for blob being searched for" + hudXMLURL.getKeyString());
				HUDXML3Blob blob = hxbs.getHUDXML3BlobFromXMLKey(hudXMLURL.getKeyString());
				blob.setCsvUrl2Indexed(1);
				hxbs.updateHUDXML3Blob(blob);	
			} catch (Exception e) {
				log.info("XML Stream exception caught: " + e.toString());
			}
			xmlr0.close();
			zis.close();
		} catch (Exception e) {
			log.info("exception caught creating StAX stream " + e.toString());
		}
	}
	
	void convertHouseholdToRosieCSV2(Household household) {
		// populate ROSIE CSV file 2 "Household Information" with each person that is a household member
		// each household member represents one record in Rosie CSV #2
		log.info("inside convertHouseholdtoRosieCSV()");

		//Use JAXB to get the Head of Household's ID
		BigInteger headOfHouseholdID = household.getHeadOfHouseholdID().getIDNum();
		//headOfHouseholdIDList = (NodeList) xpath.evaluate("/Sources/Source/Export/Household[HouseholdID/IDStr/text() = \"" + hid+"\"]/HeadOfHouseholdID/IDNum", doc, XPathConstants.NODESET);
		//log.info("Exception while getting the head of household's id for household " + hid + ": " + e.toString());
		String sSNFamilyHeadCSV2 = null;
		householdMemberCounterCSV2 = 1;

		//look up the head of household's SSN
		sSNFamilyHeadCSV2 = getHeadOfHouseholdSSNFromHousehold(household);

		log.info("Placing \'" + sSNFamilyHeadCSV2 + "\' into csv2Record[3]");
		if (!sSNFamilyHeadCSV2.isEmpty() && sSNFamilyHeadCSV2 != null) {
			csv2Record[3] = sSNFamilyHeadCSV2;
		} else {log.info("Problem getting head of household SSN");}
		
		//No mapping yet for RecordType_2
		csv2Record[4] = "2";
		
		String hh_id;
		if (household.getHouseholdID().getIDStr() != null) {
			hh_id = household.getHouseholdID().getIDStr();
		} else if (household.getHouseholdID().getIDNum() != null) {
			hh_id = household.getHouseholdID().getIDNum().toString();
		} else {log.info("couldn't get household id in convertHouseholdToRosieCSV2"); hh_id = "";}
		log.info("calling processHouseholdMembersToRosie for household: " + hh_id);
		
		// Because, in this particular XML, the Head Of Household is not listed as a household member (which it should be, also process that person separately.
		processMembersAndHeadsOfHouseholds(household, headOfHouseholdID);

		// then process all the members
		processHouseholdMembersToRosie(household);
		
		// reset the common household values back to empty strings, so they don't enter into 
		//    the next household record
		csv2Record[3] = "";
	}
	
	void processNextCSV2(String nextIndex) {
		log.info("inside processNextCSV2.  nextIndex is: " + nextIndex);
		
		//Use StAX to get Households
		JAXBContext jc = null;
		try {
			jc = JAXBContext.newInstance(Household.class);
		} catch (JAXBException e) {
			log.info("JAXB Household.class context problem: " + e.toString());
			e.printStackTrace();
		}
		try {
			unmarshaller = jc.createUnmarshaller();
		} catch (JAXBException e) {
			log.info("Problem unmarshalling Household context: " + e.toString());
			e.printStackTrace();
		}
		
		try {
 			InputStream zis = openXMLStream(hudXMLURL);
			XMLStreamReader xmlr0 = null;
			// set up a StAX reader to find the nextIndex within the XML
			xmlr0 = xmlif.createXMLStreamReader(zis);			
			while(xmlr0.hasNext()) {
				//Get each household
				xmlr0.next();
				if (xmlr0.isStartElement()) {
					//log.info("name is:" + xmlr1.getLocalName());
					if (xmlr0.getLocalName().equals("Household")) {
						xmlr0.require(XMLStreamConstants.START_ELEMENT, null, "Household");
						JAXBElement<Household> householdJAXB = unmarshaller.unmarshal(xmlr0,Household.class);
						Household householdElement = (Household)householdJAXB.getValue();
						if (householdElement.getHouseholdID().getIDStr() != null) {
							if ((householdElement.getHouseholdID().getIDStr()).equals(nextIndex)) {
								log.info("Now converting household: " + householdElement.getHouseholdID().getIDStr());
								try {
									convertHouseholdToRosieCSV2(householdElement);
								} catch (Exception e) {log.info("Problem converting household: " + householdElement.getHouseholdID().getIDStr() + " " + e.toString()); e.printStackTrace();}
								break;
							}
						}
						else if (householdElement.getHouseholdID().getIDNum() != null) {
							if ((householdElement.getHouseholdID().getIDNum().toString()).equals(nextIndex)) {
								log.info("Now converting household: " + householdElement.getHouseholdID().getIDNum());
								try {
									convertHouseholdToRosieCSV2(householdElement);
								} catch (Exception e) {log.info("Problem converting household: " + householdElement.getHouseholdID().getIDNum() + " " + e.toString()); e.printStackTrace();}
								break;
							}
						}
						else {log.info("Couldn't get household ID in processNextCSV2");}
					}
				}
			}
		} catch (Exception e) {
			log.info("XML Stream exception caught: " + e.toString());
			e.printStackTrace();
		}
	}
	
	void processFundingSourceServiceEvent(SiteServiceParticipation ssp, Household household) {
		//log.info("inside processFundingSourceServiceEvent()");
		String transactionDateCSV6 = null;
		String transactionTimeCSV6 = null;
		String AssistanceProvidedAmountCSV6 = null;
		for (int i=0; i<csv6Record.length; i++) {
			csv6Record[i]="";
		}

		//Get all of this household's fundingSources
		//"/Sources/Source/Export/Person/SiteServiceParticipation[HouseholdID/IDNum/text() = \"" + hid + "\"]/ServiceEvent/FundingSources/FundingSource", doc, XPathConstants.NODESET));
		if (!(ssp.getServiceEvent().isEmpty())) {  	
			for (ServiceEvent serviceEvent : ssp.getServiceEvent()) {
				for (FundingSourceServiceEvent fs : serviceEvent.getFundingSources().getFundingSource()) {
					//For each funding source, write a record.
					//Each record starts with an agency
					//Look up the service key, since we use that instead of agency
					csv6Record[0] = csv1Record[0];
					//Get transaction Date and Time from FundingSource/FinancialAssistanceAmount/@date_effective 
					//"/FinancialAssistanceAmount/@dateEffective", fundingSource, 
					try {
						GregorianCalendar calendar = serviceEvent.getServiceEventProvisionDate().toGregorianCalendar();
						SimpleDateFormat formatOutDate = new SimpleDateFormat("MMddyy");
						SimpleDateFormat formatOutTime = new SimpleDateFormat("HHmmss");
						formatOutDate.setCalendar(calendar);
						formatOutTime.setCalendar(calendar);
						//SimpleDateFormat formatIn = new SimpleDateFormat("yyyy-MM-dd'T'hh':'mm':'ss");
						Date date;
						date = calendar.getTime();
						transactionDateCSV6 = formatOutDate.format(date);
						transactionTimeCSV6 = formatOutTime.format(date);
						//log.info("Problem formatting a date: " + e.toString());
						csv6Record[1] = transactionDateCSV6;
						csv1Record[1] = csv6Record[1];
						csv2Record[1] = csv6Record[1]; 
						csv6Record[2] = transactionTimeCSV6;
						csv1Record[2] = csv6Record[2];
						csv2Record[2] = csv6Record[2];					
					} catch (Exception e) {
						log.info("Problem getting TransactionDates: " + e.toString());
					}
	
					//Get SSNFamilyHead
					//"/Sources/Source/Export/Person[PersonID/IDNum/text() = \"" + headOfHouseholdID+"\"]/SocialSecurityNumber/Unhashed/text()", doc, XPathConstants.NODESET)).item(0).getTextContent();
					//csvRecord[3] = getHeadOfHouseholdSSNFromHousehold(household);
					csv6Record[3] = csv2Record[3];
					csv6Record[4] = "6";
					csv6Record[5] = String.valueOf(householdMemberCounterCSV2);
	
					//Get AssistanceProvidedCode
					//No corresponding entry in HUD XML, exactly, but we'll use a merging of hmis and 
					//  hprp service types (ignore the AIRS code as well as the "other" type free text field values)
					
					// Get the service code from the XML
					String AssistanceProvidedCodeCSV6 = "";
					String AssistanceProvidedDescriptionCSV6 = "";
					HMISServiceEvent serviceEventCodeSet = null;
					Integer hPRPFinancialCodeSet = null;
					Integer hPRPRelocationCodeSet = null;
					Integer serviceEventCode = null;
					
					try {
						serviceEventCodeSet = serviceEvent.getHMISServiceEventCode();
						serviceEventCode = (int)(long)(serviceEventCodeSet.getTypeOfService());
					} catch (Exception e) {log.info("no HMIS Service Event");}
					try {				
						hPRPFinancialCodeSet = (int)(long)serviceEvent.getHPRPFinancialAssistanceServiceEventCode();
					} catch (Exception e) {log.info("no HPRP Financial Code Set");}
					try {
						hPRPRelocationCodeSet = (int)(long)serviceEvent.getHPRPRelocationStabilizationServiceEventCode();
					} catch (Exception e) {log.info("HPRP Relocation Code Set");}
					
					// ignore free text "other" value
					
					if (serviceEventCode != null) {
						// put the value in file, and look up the description
						AssistanceProvidedCodeCSV6 = serviceEventCode.toString();
					}
					
					else if (hPRPFinancialCodeSet != null) {
						
						Map<Integer, Integer> hprp_fin_map = new HashMap<Integer, Integer>();
						
						//22 (was 1) = Rental assistance
						//23 (was 2) = Security deposits
						//24 (was 3) = Utility deposits
						//25 (was 4) = Utility payments
						//26 (was 5) = Moving cost assistance
						//27 (was 6) = Motel and hotel vouchers
						
						hprp_fin_map.put(1, 22);
						hprp_fin_map.put(2, 23);
						hprp_fin_map.put(3, 24);
						hprp_fin_map.put(4, 25);
						hprp_fin_map.put(5, 26);
						hprp_fin_map.put(6, 27);
						
						AssistanceProvidedCodeCSV6 = hprp_fin_map.get(hPRPFinancialCodeSet).toString();
	
					}
					else if (serviceEvent.getHPRPRelocationStabilizationServiceEventCode() != null) {
						Map<Integer, Integer> hprp_rel_map = new HashMap<Integer, Integer>();
						
						//28 (was 1) = Case management
						//29 (was 2) = Outreach and engagement
						//30 (was 3) = Housing search and placement
						//31 (was 4) = Legal services
						//32 (was 5) = Credit repair
						
						hprp_rel_map.put(1, 28);
						hprp_rel_map.put(2, 29);
						hprp_rel_map.put(3, 30);
						hprp_rel_map.put(4, 31);
						hprp_rel_map.put(5, 32);
						
						AssistanceProvidedCodeCSV6 = hprp_rel_map.get(hPRPRelocationCodeSet).toString();
	
					}
					
					if (AssistanceProvidedCodeCSV6 != null) {
					
						Map<Integer, String> desc = new HashMap<Integer, String>();
								
						//Standard HMIS Service Types
						desc.put(1, "Food");
						desc.put(2, "Housing placement"); 
						desc.put(3, "Material goods");
						desc.put(4, "Temporary housing and other financial aid");
						desc.put(5, "Transportation");
						desc.put(6, "Consumer assistance and protection");
						desc.put(7, "Criminal justice/legal services");
						desc.put(10, "Education");
						desc.put(11, "HIV/AIDS-related services");
						desc.put(12, "Mental health care/counseling");
						desc.put(13, "Other health care");
						desc.put(14, "Substance abuse services");
						desc.put(15, "Employment");
						desc.put(16, "Case/care management");
						desc.put(17, "Day care");
						desc.put(18, "Personal enrichment");
						desc.put(19, "Referral to other service(s)"); 
						desc.put(20, "Outreach");
						desc.put(21, "Other");
						
						//Financial Assistance Type 
						desc.put(22, "Rental assistance");																						
						desc.put(23, "Security deposits");
						desc.put(24, "Utility deposits");
						desc.put(25, "Utility payments");
						desc.put(26, "Moving cost assistance");
						desc.put(27, "Motel and hotel vouchers");
						
						//Housing Relocation and Stabilization Services Provided
						desc.put(28, "Case management");
						desc.put(29, "Outreach and engagement");
						desc.put(30, "Housing search and placement");
						desc.put(31, "Legal services");
						desc.put(32, "Credit repair");
						log.info("Description mapping is: ");
						// Iterate all keys
						//for (Integer key : desc.keySet()) 
						   //log.info(key.toString());
						log.info("Looking up value of: " + AssistanceProvidedCodeCSV6);
						//log.info("Does the descriptors array contain the key?:" + desc.containsKey(Integer.valueOf(AssistanceProvidedCodeCSV6)));
						AssistanceProvidedDescriptionCSV6 = desc.get(Integer.valueOf(AssistanceProvidedCodeCSV6));
						
						log.info("Which is returning:" + AssistanceProvidedDescriptionCSV6);
						//log.info("get(1) = " + desc.get(1));
					}
					
					csv6Record[6] = AssistanceProvidedCodeCSV6;
					
					//Get AssistanceProvidedDescription
					// We'll use the mapped descriptions corresponding to the entry from csvRecord[6]
					csv6Record[7] = AssistanceProvidedDescriptionCSV6;
	
					//Get AssistanceProvidedAmount  
					AssistanceProvidedAmountCSV6 = fs.getFinancialAssistanceAmount().toString();
					//"FinancialAssistanceAmount/text()", fundingSource, XPathConstants.NODESET)).item(0).getTextContent();
					//log.info("Problem getting a FinancialAssistanceAmount: " + e.toString());
					csv6Record[8] = AssistanceProvidedAmountCSV6;
					
					// write out the whole record into CSV 1
					log.info("writing out file 1");
					writeNextAndClose(csv1Record);
					
					// write out the whole CSV 2 record
					log.info("Writing out CSV2 Record");
					writeNextAndClose(csv2Record);
					
					// write out the whole CSV 6 record
					writeNextAndClose(csv6Record);
	
					//clear record
					for (int i = 0; i < csv6Record.length; i++) {
						csv6Record[i] = "";
					}
				}
			}
		}
	}  

	void processHealthStatus(SiteServiceParticipation ssp, Household household) {
		log.info("inside processHealthStatus()");
		String transactionDateCSV4;
		String transactionTimeCSV4;
		//String sSNFamilyHeadCSV4;

		//Get all of this household's health statuses
		//ArrayList<SiteServiceParticipation> ssps = getSiteServiceParticipationsForHousehold(household, hudXMLURL);

		//for (SiteServiceParticipation siteServiceParticipation : ssps) {
		for (PersonHistorical ph : ssp.getPersonHistorical()) {
			for (HealthStatus healthStatus : ph.getHealthStatus()) {
				//For each health status, write a record.
				//Each record starts with a Service (would normally be agency)

				//Look up the Service Key 
				//BigInteger siteServiceID = ssp.getSiteServiceID();
				//csv4Record[0] = getServiceIDFromSiteServiceID(siteServiceID).toString();
				csv4Record[0] = csv1Record[0];
				//Get transaction Date and Time from IncomeAndSource/IncomeSourceCode@date_effective 
				GregorianCalendar calendar = healthStatus.getDateEffective().toGregorianCalendar();
				//log.info("Problem getting TransactionDates: " + e.toString());
				SimpleDateFormat formatOutDate = new SimpleDateFormat("MMddyy");
				SimpleDateFormat formatOutTime = new SimpleDateFormat("HHmmss");
				formatOutDate.setCalendar(calendar);
				formatOutTime.setCalendar(calendar);

				//SimpleDateFormat formatIn = new SimpleDateFormat("yyyy-MM-dd'T'hh':'mm':'ss");
				Date date;
				date = calendar.getTime();
				transactionDateCSV4 = formatOutDate.format(date);
				transactionTimeCSV4 = formatOutTime.format(date);
			
				csv4Record[1] = transactionDateCSV4;
				csv1Record[1] = csv4Record[1];
				csv2Record[1] = csv4Record[1];
				csv4Record[2] = transactionTimeCSV4;
				csv1Record[2] =csv4Record[2];
				csv2Record[2] =csv4Record[2];
				
				//Get SSNFamilyHead
				//sSNFamilyHeadCSV4 = getHeadOfHouseholdSSNFromHousehold(household);
				//"/Sources/Source/Export/Person[PersonID/IDNum/text() = \"" + headOfHouseholdID+"\"]/SocialSecurityNumber/Unhashed/text()", doc, XPathConstants.NODESET)).item(0).getTextContent();
				//log.info("Exception trying to get SSN for head of household: " + e.toString());
				//csvRecord[3] = sSNFamilyHeadCSV4;
				csv4Record[3] = csv2Record[3];
				csv4Record[4] = "4";
				csv4Record[5] =  String.valueOf(householdMemberCounterCSV2);

				//Get HealthProblemCode
				String healthProblemCodeCSV4;
				healthProblemCodeCSV4 = String.valueOf(healthStatus.getValue());
				csv4Record[6] = healthProblemCodeCSV4;

				//Get HealthProblemDescription
				//No corresponding entry in HUD XML
				csv4Record[7] = "";
				
				// write out the whole record into CSV 1
				log.info("writing out file 1");
				writeNextAndClose(csv1Record);
				
				// write out the whole record into CSV 2
				log.info("Writing out CSV2 Record");	
				writeNextAndClose(csv2Record);
				
				// write out the whole record into CSV 4
				writeNextAndClose(csv4Record);

				//clear record
				for (int i = 0; i < csv4Record.length; i++) {
					csv4Record[i] = "";
				}
			}
		}
		
	}

	void processHouseholdMembersToRosie(Household household) {
		log.info("inside processHouseholdMembersToRosie()");
		//look up each family member, then write to Rosie CSV 2 "Family Members"

		//Get all household members
		if (household.getHouseholdID().getIDStr() != null) {
			log.info("Searching for family members of household with id: " + household.getHouseholdID().getIDStr());
		} else if (household.getHouseholdID().getIDNum() != null) {
			log.info("Searching for family members of household with id: " + household.getHouseholdID().getIDNum());
		}
		Members householdMembers;
		try {	
			householdMembers = household.getMembers();
			if (householdMembers.getMember().isEmpty()) {
				if (household.getHouseholdID().getIDStr() != null) {
					log.info("No household members found within householdID/IDStr: " + household.getHouseholdID().getIDStr());
				} else if (household.getHouseholdID().getIDNum() != null) {
					log.info("No household members found within householdID/IDNum: " + household.getHouseholdID().getIDNum());
				}
			} else {
				log.info("Number of household members found is: " + householdMembers.getMember().size());
			}
			
			for (Member member : householdMembers.getMember()) {
			    csv2Record[5] = String.valueOf(++householdMemberCounterCSV2);

				//Get the family member's relationship to the head of household
				String relationshipCodeCSV2;
				try {
					relationshipCodeCSV2 = String.valueOf(member.getRelationshipToHeadOfHousehold().getValue());
					//relationshipCodeCSV2 = xpath.evaluate("RelationshipToHeadOfHousehold/text()", familyMember);
				} catch(Exception e) {
					log.info("Couldn't find RelationshipToHeadOfHousehold element within this Household/Member element: " + e.toString());
					relationshipCodeCSV2 = "";
				}
				csv2Record[7] = relationshipCodeCSV2;

				//Then, look up each person record for each household member, using the person id
				processMembersAndHeadsOfHouseholds(household, member);
			}
		} catch (NullPointerException e) {
			 if (household.getHouseholdID().getIDStr() != null) {
				 log.info("No members in this household: " + household.getHouseholdID().getIDStr());
			 }  else if (household.getHouseholdID().getIDNum() != null) {
				 log.info("No members in this household: " + household.getHouseholdID().getIDNum());
			 }
		}
		//householdMemberList = ((NodeList)xpath.evaluate(//"/Sources/Source/Export/Household[HouseholdID/IDNum/text() = \"" + hid +//"\"]/Members/Member", doc, XPathConstants.NODESET));
		//log.info("Problem parsing household's member list: " + e.toString());
	}

	void processIncomeAndSources(Household household, PersonHistorical ph, SiteServiceParticipation ssp) {		
		String transactionDateCSV3 = null;
		String transactionTimeCSV3 = null;
		//String sSNFamilyHeadCSV3 = null;
		//String familyMemberNumberCSV3 = null;
		String incomeExpenseTypeCodeCSV3 = null;
		
		//Get all of this household's income and expenses 
		if (ph.getIncomeAndSources() != null) {
			for (IncomeAndSource incomeAndSource : ph.getIncomeAndSources().getIncomeAndSource()) {
				List<Object> o = incomeAndSource.getIncomeSourceCodeAndIncomeSourceOtherAndReceivingIncomeSource();
				//For each IncomeAndSource, write a record.
				//Each record starts with an agency
				//Look up the agency code
				//csv3Record[0] = getServiceIDFromSiteServiceID(ssp.getSiteServiceID()).toString();
				csv3Record[0] = csv1Record[0];
				
				for (Object item : o) {
					if (item instanceof IncomeSourceCode) {
						// get the effective date and the code
						//Get transaction Date and Time from IncomeAndSource/IncomeSourceCode@date_effective 
						///IncomeSourceCodeID/@dateEffective", incomeAndSource, XPathConstants.NODESET);
						IncomeSourceCode incomeSourceCode = (IncomeSourceCode)item;
						GregorianCalendar incomeSourceCodeDateEffectiveCalendar = incomeSourceCode.getDateEffective().toGregorianCalendar();
						SimpleDateFormat formatOutDate = new SimpleDateFormat("MMddyy");
						SimpleDateFormat formatOutTime = new SimpleDateFormat("HHmmss");
						formatOutDate.setCalendar(incomeSourceCodeDateEffectiveCalendar);
						Date date = incomeSourceCodeDateEffectiveCalendar.getTime();
						transactionDateCSV3 = formatOutDate.format(date);
						transactionTimeCSV3 = formatOutTime.format(date);
						//Get IncomeExpenseTypeCode
						incomeExpenseTypeCodeCSV3 = String.valueOf(incomeSourceCode.getValue());
						//xpath.evaluate("/IncomeSourceCode/text()", incomeAndSource, XPathConstants.NODESET)).item(0).getTextContent();
						//log.info("Problem getting income source code: " + e.toString());
						csv3Record[1] = transactionDateCSV3;
						csv1Record[1] = csv3Record[1];
						csv2Record[1] = csv3Record[1];
						csv3Record[2] = transactionTimeCSV3;
						csv1Record[2] = csv3Record[2];
						csv2Record[2] = csv3Record[2];						
						csv3Record[6] = incomeExpenseTypeCodeCSV3;
					}	
					if (item instanceof NonNegativeInteger) {
						//Get MonthlyAmount
						String monthlyAmountCSV3 = ((NonNegativeInteger)item).getValue().toString();
						//incomeSourceAmount = ((NodeList) xpath.evaluate("/IncomeSourceAmount/text()", incomeAndSource, XPathConstants.NODESET)).item(0).getTextContent();
						//log.info("Problem getting income source amount: " + e.toString());
						csv3Record[7] = monthlyAmountCSV3;
					}
				}
			}

			//Get SSNFamilyHead
			//sSNFamilyHeadCSV3 = getHeadOfHouseholdSSNFromHousehold(household);
			//log.info("Exception trying to get SSN for head of household: " + e.toString());
			//csvRecord[3] = sSNFamilyHeadCSV3;
			csv3Record[3] = csv2Record[3];
			csv3Record[4] = "3";
			csv3Record[5] = String.valueOf(householdMemberCounterCSV2);
			
			// write out the whole record into CSV 1
			log.info("writing out file 1");
			writeNextAndClose(csv1Record);
			// write out the whole record into CSV 2
			log.info("Writing out CSV2 Record");	
			writeNextAndClose(csv2Record);
			
			// write out the whole record into CSV 3
			writeNextAndClose(csv3Record);
			//clear record
			for (int i = 0; i < csv3Record.length; i++) {
				csv3Record[i] = "";
			}		
		}
	}

	void processPersonAddress(PersonAddress personAddress) {
		log.info("inside processPersonAddress()");
		boolean isLastPermanentZIP = false;
		String isLastPermanentZIPString;
		isLastPermanentZIPString = String.valueOf(personAddress.getIsLastPermanentZIP().getValue()); 
			//xpath.evaluate("IsLastPermanentZIP", personAddress);
        //log.info("Couldn't evaluate IsLastPermanentZIP for this personAddress: " + e.toString());
		//csvRecord[20] = ""; csvRecord[21] = "";csvRecord[22] = "";csvRecord[23] = "";csvRecord[24] = "";}
		if (isLastPermanentZIPString.equals("1")) {
			// put fields in Last Permanent Address columns
			isLastPermanentZIP = true;
		} else if (isLastPermanentZIPString.equals("0")) {
			// put fields in Last Permanent Address columns
			isLastPermanentZIP = false;
		} else {
			log.info("isLastPermanentZIP == neither 1 nor 0");
			isLastPermanentZIP = false;
		}
		String streetAddressCSV2;
		String apartmentCSV2;
		String cityCSV2;
		String stateCSV2;
		String zIP5CSV2;
		//Sequentially process child nodes
		streetAddressCSV2 = personAddress.getLine1().getValue(); 
		if (isLastPermanentZIP)
			csv2Record[20] = streetAddressCSV2;
		else
			csv2Record[20] = "";
		apartmentCSV2 = personAddress.getLine2().getValue(); 
		//xpath.evaluate("Line2", personAddress);
		//log.info("Couldn't process child node Line2 of personAddress: " + e.toString());
		if (isLastPermanentZIP)
			csv2Record[21] = apartmentCSV2;
		else
			csv2Record[21] = "";
		cityCSV2 = personAddress.getCity().getValue(); 
		//xpath.evaluate("City", personAddress);	
		//log.info("Conuldn't process child node City of personAddress: " + e.toString());
		if (isLastPermanentZIP)
			csv2Record[22] = cityCSV2;
		else
			csv2Record[22] = "";
	
		stateCSV2 = personAddress.getState().getValue().value();
		//xpath.evaluate("State", personAddress);	

		//log.info("Couldn't process child node State of personAddress: " + e.toString());
		stateCSV2 = "";

		if (isLastPermanentZIP)
			csv2Record[23] = stateCSV2;
		else
			csv2Record[23] = "";

		zIP5CSV2 =  personAddress.getZIPCode().getValue();
		//xpath.evaluate("ZIPCode", personAddress);

   	    //log.info("Couldn't process child node ZIPCode of personAddress: " + e.toString());
		if (isLastPermanentZIP)
			csv2Record[24] = zIP5CSV2;
		else
			csv2Record[24] = "";
	}	

	void processPersonAddressHeadOfHousehold(String headOfHouseholdID, boolean isLastPermanent, PersonAddress personAddress) {
		log.info("inside processPersonAddressHeadOfHousehold()");
		String streetAddressCSV1 = null;
		String apartmentCSV1 = null;
		String cityCSV1 = null;
		String stateCSV1 = null;
		String zIP5CSV1 = null;

		//Sequentially process child nodes

		try {	
			streetAddressCSV1 = personAddress.getLine1().getValue();
			//streetAddressCSV1 = xpath.evaluate("Line1", personAddress);
		} catch(Exception e) {
			  log.info("Couldn't find Line1 element within this PersonAddress: " + e.toString());
		}				
		if (isLastPermanent)
			csv1Record[5] = streetAddressCSV1;
		else
			csv1Record[10] = streetAddressCSV1;

		try {	
			apartmentCSV1 = personAddress.getLine2().getValue();
			//apartmentCSV1 = xpath.evaluate("Line2", personAddress);
		} catch(Exception e) {
			  log.info("Couldn't find Line2 element within this PersonAddress: " + e.toString());
		}							
		if (isLastPermanent)
			csv1Record[6] = apartmentCSV1;
		else
			csv1Record[11] = apartmentCSV1;

		try {	
			cityCSV1 = personAddress.getCity().getValue();
			//cityCSV1 = xpath.evaluate("City", personAddress);	
		} catch(Exception e) {
			  log.info("Couldn't find City element within this PersonAddress: " + e.toString());
		}								
		if (isLastPermanent)
			csv1Record[7] = cityCSV1;
		else
			csv1Record[12] = cityCSV1;

		try {				
			stateCSV1 = personAddress.getState().getValue().value();
			//stateCSV1 = xpath.evaluate("State", personAddress);	
		} catch(Exception e) {
			  log.info("Couldn't find State element within this PersonAddress: " + e.toString());
		}								
		if (isLastPermanent)
			csv1Record[8] = stateCSV1;
		else
			csv1Record[13] = stateCSV1;

		try {
			zIP5CSV1 = personAddress.getZIPCode().getValue();
			//zIP5CSV1 = xpath.evaluate("ZIPCode", personAddress);	
		} catch(Exception e) {
			  log.info("Could not locate a ZIPCode element within this PersonAddress: " + e.toString());
		}										
		if (isLastPermanent)
			csv1Record[9] = zIP5CSV1;
		else
			csv1Record[14] = zIP5CSV1;
	}
	
	void processPersonHistoricalForCSV2(PersonHistorical personHistorical) {
		// for first personHistorical in personHistoricalList (should ideally look through all of them)
		log.info("inside processPersonHistoricalForCSV2()");
		String familyMemberPregnantCSV2 = null;
		String familyMemberMonthsPregnantCSV2 = null;
		String familyMemberVeteranCodeCSV2 = null;
		String housingSituationCodeCSV2 = null;
		String lengthInHousingSituationCodeCSV2 = null;

		//Get the family member's pregnancy status from their Person record
			// Get first pregnancy status, should get them all
		int pregnancyStatus = -1;
		try {
			pregnancyStatus = (int)personHistorical.getPregnancy().get(0).getPregnancyStatus().getValue();
			//evaluate("Pregnancy/PregnancyStatus/text()", personHistorical, XPathConstants.NODESET)).item(0).getTextContent());
			switch (pregnancyStatus) {
				case 0: familyMemberPregnantCSV2 = "N";
				break;
				case 1: familyMemberPregnantCSV2 = "Y";
				break;
				case 8:;
				break;
				case 9:;
				break;
				default:;
				}	
			csv2Record[16] = familyMemberPregnantCSV2;
		} catch (Exception e) {
			log.info("Couldn't get pregnancyStatus");
		}
		//log.info("Could not locate any Pregnancy/PregnancyStatus elements for this Person: " + e.toString());

		//Calculate the number of months pregnant from the HUD XML due date
		GregorianCalendar dueDateCalendar = null;
		GregorianCalendar conceptionDateCalendar = null;
		try {
			dueDateCalendar = personHistorical.getPregnancy().get(0).getDueDate().getValue().toGregorianCalendar();
			conceptionDateCalendar = new GregorianCalendar();
			conceptionDateCalendar = dueDateCalendar;
			//((NodeList)xpath.evaluate("Pregnancy/DueDate/text()", personHistorical, XPathConstants.NODESET)).item(0).getTextContent();
			// subtract 9 months from the due Date (conception date)
			conceptionDateCalendar.add(Calendar.MONTH, -9);
			Date conceptionDate = conceptionDateCalendar.getTime();
			// the get the difference of the current date from conception date to get months pregnant for Rosie CSV
			Date currentDate = new Date();
			int dateDiffInDays = (int)((currentDate.getTime() - conceptionDate.getTime())/ (1000 * 60 * 60 * 24));
			familyMemberMonthsPregnantCSV2 = String.valueOf(dateDiffInDays/30);
			//log.info("Could not locate any Pregancy/DueDate elements for this person: " + e.toString());
			csv2Record[17] =  familyMemberMonthsPregnantCSV2;
			//No mapping yet for Need_Shelter
			csv2Record[18] =  "";
		} catch (Exception e) {
			log.info("Couldn't get pregnancy");
		}
		
		//Calculate Veteran Code
		int veteranStatus = 0;
		try {
			veteranStatus =  (int) personHistorical.getVeteran().get(0).getVeteranStatus().get(0).getValue();
			familyMemberVeteranCodeCSV2 = String.valueOf(veteranStatus);
			csv2Record[19] =  familyMemberVeteranCodeCSV2;
		}  catch (Exception e) {
			log.info("Couldn't get veteran status");
		}
			//Integer.valueOf(((NodeList)xpath.evaluate("Veteran/VeteranStatus/text()", personHistorical, XPathConstants.NODESET)).item(0).getTextContent());
		//log.info("Could not find VeteranStatus for this person: " + e.toString());

		// get address of family member
		//Just handling one address per person
		PersonAddress personAddress = null;
		try {
			personAddress = personHistorical.getPersonAddress().get(0);
			processPersonAddress(personAddress);
		}  catch (Exception e) {
			log.info("Couldn't get personAddress");
		}
		//(Element)((NodeList) xpath.evaluate("PersonAddress", personHistorical, XPathConstants.NODESET)).item(0);
	    //log.info("Could not locate a PersonAddress element for this Person: " + e.toString());

		
		// get housing situation code
		try {
			housingSituationCodeCSV2 = String.valueOf(personHistorical.getHousingStatus().get(0).getValue());
			//((NodeList)xpath.evaluate("HousingStatus/text()", 	personHistorical, XPathConstants.NODESET)).item(0).getTextContent();
			csv2Record[25] = housingSituationCodeCSV2;
		} catch (Exception e) {
			log.info("Couldn't get HousingStatus");
		}
		// get length in housing situation code
		try {
			lengthInHousingSituationCodeCSV2 = String.valueOf(personHistorical.getLengthOfStayAtPriorResidence().get(0).getValue());
			//((NodeList)xpath.evaluate("LengthOfStayAtPriorResidence/text()", personHistorical, XPathConstants.NODESET)).item(0).getTextContent();
			csv2Record[26] = lengthInHousingSituationCodeCSV2;
		}	catch (Exception e) {
			log.info("Couldn't get LengthOfStayAtPriorResidence");
		}
	}
	
	BigInteger getAgencyKeyFromSiteID(BigInteger siteID) {
		long agencyKeyLong = -1l;
		JAXBContext jc = null;
		XMLStreamReader xmlr5 = null;
		InputStream zis;
		zis = openXMLStream(hudXMLURL);
		try {
			// set up a StAX reader
			xmlr5 = xmlif.createXMLStreamReader(zis);
		} catch (Exception e) {
			log.info("exception caught creating StAX stream " + e.toString());
		}
		
		try {
			// set up a StAX reader
			xmlr5 = xmlif.createXMLStreamReader(zis);
		} catch (Exception e) {
			log.info("exception caught creating StAX stream " + e.toString());
		}
		//Use StAX to get Agencies
		try {
			jc = JAXBContext.newInstance(Agency.class);
		} catch (JAXBException e) {
			log.info("JAXB Agency.class context problem: " + e.toString());
		}
		try {
			unmarshaller = jc.createUnmarshaller();
		} catch (JAXBException e) {
			log.info("Problem unmarshalling Agency context: " + e.toString());
		}
		try {
			while(xmlr5.hasNext()) {
				//Get each agency
				xmlr5.next();
				if (xmlr5.isStartElement()) {
					if (xmlr5.getLocalName().equals( "Agency")) {
						xmlr5.require(XMLStreamConstants.START_ELEMENT, null, "Agency");
						JAXBElement<Agency> agencyJAXB = unmarshaller.unmarshal(xmlr5, Agency.class);
						Agency agencyElement = (Agency)agencyJAXB.getValue();
						//iterate through all the sites in this agency
						for (TSite site : agencyElement.getSite()) {
							//Get the Site Key  from this Agency
							//This is tricky, because of JAXB's automated model creator
							for (Serializable content : site.getContent()) {
							//for (JAXBElement<?> content : site.getContent()) {
								if (content instanceof JAXBElement<?>) {
									//How do I know this is key
									JAXBElement<?> contentJAXBElement = (JAXBElement<?>)content;
									if (contentJAXBElement.getName().getLocalPart().equals("Key")) {
										BigInteger siteKey = (BigInteger) ((JAXBElement<?>)content).getValue();
										if (siteKey.equals(siteID)) {
											agencyKeyLong = agencyElement.getKey().longValue();
											break;
										}
									}
								}
							}
						} 	
					} else log.info("Problem getting AgencyKey containing SiteID: " + siteID);			
				}
			}
			xmlr5.close();
			zis.close();
		} catch (Exception e) {
			log.info("XML Stream exception caught: " + e.toString());
		}
		BigInteger agencyKey = BigInteger.valueOf(agencyKeyLong);
		return agencyKey;
		//xpath.evaluate("/Sources/Source/Export/Agency[Site/Key/text() = \"" + siteID + "\"]/Key/text()", doc, XPathConstants.NODESET);
	}
	
	BigInteger getServiceIDFromSiteServiceID(BigInteger siteServiceID) {
		//Instead, just get the Service's key directly from the SiteService and use that 
		//as the Agency Key
		BigInteger serviceKey = null;
		JAXBContext jc = null;
		InputStream zis = openXMLStream(hudXMLURL);
		XMLStreamReader xmlr6  = null;

		try {
			// set up a StAX reader
			xmlr6 = xmlif.createXMLStreamReader(zis);
		} catch (Exception e) {
			log.info("exception caught creating StAX stream " + e.toString());
		}
		
		//Use StAX to retrieve serviceID (proxy for agencyID) from the SiteService
		try {
			jc = JAXBContext.newInstance(SiteService.class);
		} catch (JAXBException e) {
			log.info("JAXB SiteService.class context problem: " + e.toString());
		}
		try {
			unmarshaller = jc.createUnmarshaller();
		} catch (JAXBException e) {
			log.info("Problem unmarshalling SiteService context: " + e.toString());
		}
		try {
			while(xmlr6.hasNext()) {
				//Get and check each SiteService for the right SiteServiceID
				xmlr6.next();
				if (xmlr6.isStartElement()) {
					if (xmlr6.getLocalName().equals("SiteService")) {
						xmlr6.require(XMLStreamConstants.START_ELEMENT, null, "SiteService");
						JAXBElement<SiteService> siteServiceJAXB = unmarshaller.unmarshal(xmlr6, SiteService.class);
						SiteService siteServiceElement = (SiteService)siteServiceJAXB.getValue();
						//Get the ServiceID from this Service
						if (siteServiceElement.getKey().equals(siteServiceID)) {
							serviceKey = siteServiceElement.getServiceID();
							break;
						}  
					} 	
				}
			}
			xmlr6.close();
			zis.close();
		} catch (Exception e) {
			log.info("XML Stream exception caught: " + e.toString());
			log.info("Problem getting ServiceKey" + e.toString());
		}
			//"/Sources/Source/Export/SiteService[Key/text() = \"" + siteServiceID+"\"]/ServiceID/text()", doc, XPathConstants.NODESET); 
		return serviceKey;
	}
	
	static ArrayList<SiteServiceParticipation> sspArrayList = new ArrayList<SiteServiceParticipation>();
	
	ArrayList<SiteServiceParticipation> getSiteServiceParticipationsForHousehold(Household household) {
		sspArrayList.clear();
		JAXBContext jc = null;
		InputStream zis = openXMLStream(hudXMLURL);
		XMLStreamReader xmlr7 = null;
		String searchHHID = household.getHouseholdID().getIDStr();
		log.info("searching for siteserviceparticipations with household ID: " + searchHHID);
		try {
			// set up a StAX reader
			xmlr7 = xmlif.createXMLStreamReader(zis);
		} catch (Exception e) {
			log.info("exception caught creating StAX stream " + e.toString());
		}
		
		//Use StAX to retrieve serviceID (proxy for agencyID) from the SiteService
		try {
			jc = JAXBContext.newInstance(SiteServiceParticipation.class);
		} catch (JAXBException e) {
			log.info("JAXB SiteServiceParticipation.class context problem: " + e.toString());
		}
		try {
			unmarshaller = jc.createUnmarshaller();
		} catch (JAXBException e) {
			log.info("Problem unmarshalling SiteServiceParticipation context: " + e.toString());
		}
		try {
			while(xmlr7.hasNext()) {
				xmlr7.next();
				if (xmlr7.isStartElement()) {
					//System.out.println("local name is: " + xmlr.getLocalName());
					if (xmlr7.getLocalName().equals("SiteServiceParticipation")) {
						xmlr7.require(XMLStreamConstants.START_ELEMENT, null, "SiteServiceParticipation");
						JAXBElement<SiteServiceParticipation> siteServiceParticipationJAXB = unmarshaller.unmarshal(xmlr7, SiteServiceParticipation.class);
						SiteServiceParticipation siteServiceParticipationElement = (SiteServiceParticipation)siteServiceParticipationJAXB.getValue();
						//Get only this household's SiteServiceParticipations 						
						try {
							String hhid = siteServiceParticipationElement.getHouseholdID().getIDStr();
							if (hhid != null && !hhid.isEmpty()) {
								if (hhid.equals(searchHHID)) {
									log.info("siteServiceParticipationElement.getHouseholdID().getIDStr is a match for household: " + searchHHID);
									sspArrayList.add(siteServiceParticipationElement);
									log.info("Added an ssp with id: " + siteServiceParticipationElement.getSiteServiceParticipationID().getIDNum().toString() + " for household:" + searchHHID);
								} else {
									log.info("siteServiceParticipationElement's hhid " + hhid + " is not a match for household: " + searchHHID);
								}
							} else {
								log.info("HouseholdID/IDStr was empty or null, so try to get the IDNum instead");
								checkIDNumInstead(sspArrayList, siteServiceParticipationElement, searchHHID);				
							}
						} catch (NullPointerException e) {
							log.info("siteServiceParticipationElement.getHouseholdID().getIDStr is null for household: " + searchHHID);
							log.info("let's try siteServiceParticipationElement.getHouseholdID().getIDNum instead");
							checkIDNumInstead(sspArrayList, siteServiceParticipationElement, searchHHID);				
						}
					} 	
				}
			}
			xmlr7.close();
			zis.close();
		} catch (Exception e) {
			log.info("XML Stream exception caught: " + e.toString());
		}
	
		return sspArrayList;
	}	
	
	ArrayList<SiteServiceParticipation> getSiteServiceParticipationsForPersonIDInHouseholdID(BigInteger personID, String householdID) {
		ArrayList<SiteServiceParticipation> ssps;
		ArrayList<SiteServiceParticipation> returnedSsps = new ArrayList<SiteServiceParticipation>();

		//assumes there is only one Person record for each unique person ID
		Person person = getPersonByID(personID);
		ssps = (ArrayList<SiteServiceParticipation>) person.getSiteServiceParticipation();
		for (SiteServiceParticipation ssp : ssps) {
			String hhid = ssp.getHouseholdID().getIDStr();
			if (hhid != null && !hhid.isEmpty()) {
				if (hhid.equals(householdID)) {
					returnedSsps.add(ssp);
				} else {continue;}
			} 
			else{
			// we have an IDNum instead of an IDStr
				BigInteger hhid2 = ssp.getHouseholdID().getIDNum();
				if (hhid2.toString().equals(householdID)) {
					returnedSsps.add(ssp);
				} else {continue;}
			}
		}
		return returnedSsps;
	}
	
	SiteServiceParticipation getOneSiteServiceParticipationForHousehold(Household household) {
		// used to ultimately get a program id for the first column of CSV2, not sure if it has any other use
		// makes the assumption that a household ID is bound to a single program.  Big assumption.
		// I think in Rosie, a household is attributed to only one agency.  Not in HUD HMIS XML where IDs link households to agency in a more dynamic way.
		SiteServiceParticipation siteServiceParticipationElement = null;
		JAXBContext jc = null;
		InputStream zis = openXMLStream(hudXMLURL);
		XMLStreamReader xmlr8 = null;
		String searchHHID = household.getHouseholdID().getIDStr();
		log.info("searching for siteserviceparticipations with household ID: " + searchHHID);
		try {
			// set up a StAX reader
			xmlr8 = xmlif.createXMLStreamReader(zis);
		} catch (Exception e) {
			log.info("exception caught creating StAX stream " + e.toString());
		}
		
		//Use StAX to retrieve serviceID (proxy for agencyID) from the SiteService
		try {
			jc = JAXBContext.newInstance(SiteServiceParticipation.class);
		} catch (JAXBException e) {
			log.info("JAXB SiteServiceParticipation.class context problem: " + e.toString());
		}
		try {
			unmarshaller = jc.createUnmarshaller();
		} catch (JAXBException e) {
			log.info("Problem unmarshalling SiteServiceParticipation context: " + e.toString());
		}
		try {
			while(xmlr8.hasNext()) {
				xmlr8.next();
				if (xmlr8.isStartElement()) {
					//System.out.println("local name is: " + xmlr.getLocalName());
					if (xmlr8.getLocalName().equals("SiteServiceParticipation")) {
						xmlr8.require(XMLStreamConstants.START_ELEMENT, null, "SiteServiceParticipation");
						JAXBElement<SiteServiceParticipation> siteServiceParticipationJAXB = unmarshaller.unmarshal(xmlr8, SiteServiceParticipation.class);
						siteServiceParticipationElement = (SiteServiceParticipation)siteServiceParticipationJAXB.getValue();
						//Get only this household's SiteServiceParticipations 						
						try {
							String hhid = siteServiceParticipationElement.getHouseholdID().getIDStr();
							if (hhid != null && !hhid.isEmpty()) {
								if (hhid.equals(searchHHID)) {
									log.info("siteServiceParticipationElement.getHouseholdID().getIDStr is a match for household: " + searchHHID);
									log.info("Found an ssp with id: " + siteServiceParticipationElement.getSiteServiceParticipationID().getIDNum().toString() + " for household:" + searchHHID);
									break;
								} else {
									log.info("siteServiceParticipationElement's hhid " + hhid + " is not a match for household: " + searchHHID);
								}
							} else {
								log.info("HouseholdID/IDStr was empty or null, so try to get the IDNum instead");
								boolean b = checkIDNumInsteadSingleElement(siteServiceParticipationElement, searchHHID);
								if (b) {
									break;//break out once we retrieve just one enrollment
								}
							}
						} catch (NullPointerException e) {
							log.info("siteServiceParticipationElement.getHouseholdID().getIDStr is null for household: " + searchHHID);
							log.info("let's try siteServiceParticipationElement.getHouseholdID().getIDNum instead");
							boolean b = checkIDNumInsteadSingleElement(siteServiceParticipationElement, searchHHID);
							if (b) {
								break;//break out once we retrieve just one enrollment
							}
						}
					} 	
				}
			}
			xmlr8.close();
			zis.close();
		} catch (Exception e) {
			log.info("XML Stream exception caught: " + e.toString());
		}
	
		return siteServiceParticipationElement;
	}
	
	boolean checkIDNumInsteadSingleElement(SiteServiceParticipation siteServiceParticipationElement, String searchHHID) {
		boolean b = false;
		try {
			String hhid = siteServiceParticipationElement.getHouseholdID().getIDNum().toString(); 
			if (hhid != null  && !hhid.isEmpty()) {
				log.info("comparing " + hhid + " to searching for: " + searchHHID );
				if (hhid.equals(searchHHID)) {
					log.info("siteServiceParticipationElement.getHouseholdID().getIDNum is a match for household: " + searchHHID);
					sspArrayList.add(siteServiceParticipationElement);
					log.info("Check matched an ssp with id: " + siteServiceParticipationElement.getSiteServiceParticipationID().getIDNum() + " for household:" + searchHHID);
					b = true;
				} else {
					log.info("siteServiceParticipationElement's hhid " + hhid + " is not a match for household: " + searchHHID);
				}
			} 
		} catch (NullPointerException f) {
			log.info("siteServiceParticipationElement.getHouseholdID().getIDNum is null for household: " + searchHHID);
		}
		return b;
	}
	
	void checkIDNumInstead(ArrayList<SiteServiceParticipation> sspArrayList, SiteServiceParticipation siteServiceParticipationElement, String searchHHID) {
		try {
			String hhid = siteServiceParticipationElement.getHouseholdID().getIDNum().toString(); 
			if (hhid != null  && !hhid.isEmpty()) {
				log.info("comparing " + hhid + " to searching for: " + searchHHID );
				if (hhid.equals(searchHHID)) {
					log.info("siteServiceParticipationElement.getHouseholdID().getIDNum is a match for household: " + searchHHID);
					sspArrayList.add(siteServiceParticipationElement);
					log.info("Added an ssp with id: " + siteServiceParticipationElement.getSiteServiceParticipationID().getIDNum() + " for household:" + searchHHID);
				} else {
					log.info("siteServiceParticipationElement's hhid " + hhid + " is not a match for household: " + searchHHID);
				}
			} 
		} catch (NullPointerException f) {
			log.info("siteServiceParticipationElement.getHouseholdID().getIDNum is null for household: " + searchHHID);
		}
	}
	
	String getHeadOfHouseholdSSNFromHousehold(Household household) {
		if (household.getHouseholdID().getIDStr() != null) {
			log.info("Retrieving head of household's SSN for household ID: " + household.getHouseholdID().getIDStr() + " and head of household id: " + household.getHeadOfHouseholdID().getIDNum().toString());
		} else if (household.getHouseholdID().getIDNum() != null) {
			log.info("Retrieving head of household's SSN for household ID: " + household.getHouseholdID().getIDNum() + " and head of household id: " + household.getHeadOfHouseholdID().getIDNum().toString());

		}
		JAXBContext jc = null;
		InputStream zis = openXMLStream(hudXMLURL);
		XMLStreamReader xmlr8 = null;
		try {
			// set up a StAX reader
			xmlr8 = xmlif.createXMLStreamReader(zis);
		} catch (Exception e) {
			log.info("exception caught creating StAX stream " + e.toString());
		}

		//Use StAX to retrieve the head of household's ssn from the Household
		String ssn = "";
		
		try {
			jc = JAXBContext.newInstance(Person.class);
		} catch (JAXBException e) {
			log.info("JAXB Person.class context problem: " + e.toString());
		}
		try {
			unmarshaller = jc.createUnmarshaller();
		} catch (JAXBException e) {
			log.info("Problem unmarshalling Person context: " + e.toString());
		}
		try {
			while(xmlr8.hasNext()) {
				//Get and check each Person with the right Head of Household ID
				xmlr8.next();
				if (xmlr8.isStartElement()) {
					if (xmlr8.getLocalName().equals("Person")) {
						xmlr8.require(XMLStreamConstants.START_ELEMENT, null, "Person");
						JAXBElement<Person> personJAXB = unmarshaller.unmarshal(xmlr8, Person.class);
						Person person = (Person)personJAXB.getValue();
						//Get only the person with the same id as the head of household's id 
						if (person.getPersonID().getIDNum().equals(household.getHeadOfHouseholdID().getIDNum())) {
							ssn = person.getSocialSecurityNumber().getUnhashed().getValue();
							break;
						}  
					} 	
				}
			}
			xmlr8.close();
			zis.close();
		} catch (Exception e) {
			log.info("XML Stream exception caught: " + e.toString());
		}
		return ssn;
	}	
	
	Person getPersonFromSiteServiceParticipation(SiteServiceParticipation ssp) {
		Person personReturn = null;
		JAXBContext jc = null;

		InputStream zis = openXMLStream(hudXMLURL);

		XMLStreamReader xmlr9 = null;
		try {
			// set up a StAX reader
			xmlr9 = xmlif.createXMLStreamReader(zis);
		} catch (Exception e) {
			log.info("exception caught creating StAX stream " + e.toString());
		}
		
		BigInteger sspID = ssp.getSiteServiceParticipationID().getIDNum();
		// Use StAX to get Person that possesses that ssp id
		try {
			jc = JAXBContext.newInstance(Person.class);
		} catch (JAXBException e) {
			log.info("JAXB Person.class context problem: " + e.toString());
		}
		try {
			unmarshaller = jc.createUnmarshaller();
		} catch (JAXBException e) {
			log.info("Problem unmarshalling Person context: " + e.toString());
		}
		try {
			while(xmlr9.hasNext()) {
				//Get and check each Person with the right Head of Household ID
				xmlr9.next();
				if (xmlr9.isStartElement()) {
					if (xmlr9.getLocalName().equals("Person")) {
						xmlr9.require(XMLStreamConstants.START_ELEMENT, null, "Person");
						JAXBElement<Person> personJAXB = unmarshaller.unmarshal(xmlr9, Person.class);
						Person person = (Person)personJAXB.getValue();
						//Get only the person with the same id as the head of household's id 
						for (SiteServiceParticipation ssp2 : person.getSiteServiceParticipation()) {
							if (ssp2.getSiteServiceParticipationID().getIDNum().equals(sspID)) {
								personReturn = person;
								break;
							}	
						}
					} 	
				}
			}
			xmlr9.close();
			zis.close();
		} catch (Exception e) {
			log.info("XML Stream exception caught: " + e.toString());
		}
		return personReturn;
	}
	
	Person getPersonFromHouseholdMember(Member member) {
		Person personReturn = null;
		JAXBContext jc = null;
		InputStream zis = openXMLStream(hudXMLURL);
		XMLStreamReader xmlr10 = null;
		try {
			// set up a StAX reader
			xmlr10 = xmlif.createXMLStreamReader(zis);
		} catch (Exception e) {
			log.info("exception caught creating StAX stream " + e.toString());
		}
		
		// Use StaX to search for a Person with the same ID as the household member
		try {
			jc = JAXBContext.newInstance(Person.class);
		} catch (JAXBException e) {
			log.info("JAXB Person.class context problem: " + e.toString());
		}
		try {
			unmarshaller = jc.createUnmarshaller();
		} catch (JAXBException e) {
			log.info("Problem unmarshalling Person context: " + e.toString());
		}
		try {
			while(xmlr10.hasNext()) {
				//Get and check each Person with the right Head of Household ID
				xmlr10.next();
				if (xmlr10.isStartElement()) {
					if (xmlr10.getLocalName().equals("Person")) {
						xmlr10.require(XMLStreamConstants.START_ELEMENT, null, "Person");
						JAXBElement<Person> personJAXB = unmarshaller.unmarshal(xmlr10, Person.class);
						Person person = (Person)personJAXB.getValue();
						//Get only the person with the same id as the head of household's id 
						if (person.getPersonID().getIDNum().equals(member.getPersonID().getIDNum())) {
								personReturn = person;
								break;
						}
					} 	
				}
			}
			xmlr10.close();
			zis.close();
		} catch (Exception e) {
			log.info("XML Stream exception caught: " + e.toString());
		}
		return personReturn;
	}
	
	void convertSiteServiceParticipationToRosieCSV1(SiteServiceParticipation ssp) {
		// populate ROSIE CSV file 1 "Household Information" with each household's program enrollment
		// each person's enrollment using the associated household represents one record in Rosie #1
		log.info("inside convertSiteServiceParticipationtoRosieCSV()");
		csv1Record = new String[27];
		BigInteger serviceKey = null;
		String serviceKeyCSV1 = "";
		String sSNFamilyHeadCSV1;
		csv1Record[3] = csv2Record[3]; 
		sSNFamilyHeadCSV1 = csv2Record[3];
		csv1Record[4] = "1";
 		//Get AgencyCode
		try {
			BigInteger siteServiceID = ssp.getSiteServiceID();
			log.info("siteServiceID we found in /Sources/Source/Export/Person/SiteServiceParticipation/SiteServiceID for that and ssp of: " + ssp.getSiteServiceParticipationID().getIDNum() + " and  household id of: " + ssp.getHouseholdID().getIDStr() + " is: " + siteServiceID);
			//list = (NodeList) xpath.evaluate("/Sources/Source/Export/Person[PersonID/IDNum/text() = \"" + familyMemberNumberCSV2 + "\"]/SiteServiceParticipation[HouseholdID/IDNum/text() = \"" + hid+"\"]/SiteServiceID/text()", doc, XPathConstants.NODESET);
			//Now, look up the program participation's parent program/agency/code 
			//First, look up the SiteID within that SiteService with same SiteServiceID (ServiceID wouldn't work in the XML I have)
			//Now, look up the Service's Key matching the SiteServiceID
			//Use Service (Program) as a proxy for agency, if agencies aren't used
			try {
				serviceKey = getServiceIDFromSiteServiceID(siteServiceID);
				log.info("Got back Service Key of: " + serviceKey + " from SiteServiceID: " + siteServiceID);
			}
			catch (Exception e) {
				log.info("Got back no Service Key from SiteServiceID: " + siteServiceID);
			}
			//log.info("Got back no Agency Key with same SiteID: " + siteID + ".  Exception: " + e.toString());
			if (!serviceKey.equals(null)) {
				if (serviceKey.signum() == 1) {
					log.info("Got back Service Key: " + serviceKey + " from SiteService ID of: " + siteServiceID);
					serviceKeyCSV1  = serviceKey.toString();
				}  else {
					log.info("Got back no matching ServiceID within same SiteServiceParticipation as siteServiceID: " + siteServiceID);
				}	
			} log.info("Got back no matching ServiceID within same SiteServiceParticipation as siteServiceID: " + siteServiceID);
	
			csv1Record[0] = serviceKeyCSV1;
			csv2Record[0] = csv1Record[0];
			//log.info("csvRecord[0] = " + csvRecord[0] );
		} catch (Exception e) {
			log.info("Problem getting SiteServiceID: " + e.toString());
			log.info("Got back no matching Service.Key");
		}
		
		//Use head of household's id to look up Person record's address info
		BigInteger headOfHouseholdID;
		if (ssp.getHouseholdID().getIDStr() != null) {
			headOfHouseholdID = getHeadOfHouseholdIDFromHouseholdID(ssp.getHouseholdID().getIDStr());
		} else if (ssp.getHouseholdID().getIDNum() != null) {
			headOfHouseholdID = getHeadOfHouseholdIDFromHouseholdID(ssp.getHouseholdID().getIDNum().toString());
		} else headOfHouseholdID = null;
		Household household = null;
		if (ssp.getHouseholdID().getIDStr() != null) {
			household = getHouseholdFromHouseholdID(ssp.getHouseholdID().getIDStr());
		} else if (ssp.getHouseholdID().getIDNum() != null) {
			household = getHouseholdFromHouseholdID(ssp.getHouseholdID().getIDNum().toString());
		} else {log.info("Couldn't look up household.");}
		
		// calculate FamilySize for this household (HUD HMIS XML doesn't use this; it just lists the members) 
		// This is a very simplistic calculation, and doesn't dedup nor search by all elements with same household id
		//"/Sources/Source/Export/Household[HouseholdID/IDNum/text() = \"" + hid + "\"]/Members/Member", doc, XPathConstants.NODESET);
		//"/Sources/Source/Export/Household[HouseholdID/IDStr/text() = \"" + hid + "\"]/Members/Member", doc, XPathConstants.NODESET);
		try {
			log.info("Getting household members for household: " + household.getHouseholdID());
			List<Member> members= household.getMembers().getMember();
			//add all the household members
			int familySize = members.size();
			//add the head of household (there is always 1)
			familySize++;
			String familySizeCSV1= String.valueOf(familySize);
			log.info("Number of members for this household: " + familySizeCSV1);
			csv1Record[18] = familySizeCSV1; 
		} catch (NullPointerException e) {
			log.info("Problem getting household members: ");// + e.toString());
			if (household.getHouseholdID().getIDStr() != null) {
				log.info("For family size, no members in household: " + household.getHouseholdID().getIDStr());
			} else if (household.getHouseholdID().getIDNum() != null) {
				log.info("For family size, no members in household: " + household.getHouseholdID().getIDNum());
			}
			log.info("So set family size equal to '1' for the head of household indicated");
			csv1Record[18] = "1"; 
		}
		//For CSV 1 Household Information, we look inside the siteserviceparticipation's personhistorical, but we should look into unnested personhistoricals as well
		log.info("Looking up PersonID: " + headOfHouseholdID.toString() + " Addresses");
		
		//Get the head of household's address
		
		//"/Sources/Source/Export/Person[PersonID/IDNum/text() = \"" + headOfHouseholdID + "\"]/SiteServiceParticipation/PersonHistorical/PersonAddress", doc, XPathConstants.NODESET);
		//"/Sources/Source/Export/Person[PersonID/IDStr/text() = \"" + headOfHouseholdID + "\"]/SiteServiceParticipation/PersonHistorical/PersonAddress", doc, XPathConstants.NODESET);					
		List<PersonHistorical> phs = ssp.getPersonHistorical();
		String housingSituationCodeCSV1 = "";
		String lengthInHousingSituationCodeCSV1 ="";
		String priorLocCSV1 = "";
		for (PersonHistorical ph : phs) {
			// get housing situation code; look up by the head of household's id
			List<SixValDKRefused2> housingStatuses = ph.getHousingStatus();
			
			if (!housingStatuses.isEmpty() && housingSituationCodeCSV1 != null) {
				 housingSituationCodeCSV1 = String.valueOf(housingStatuses.get(0).getValue());							
				csv1Record[19] = housingSituationCodeCSV1;
			}
			//"/Sources/Source/Export/Person[PersonID/IDNum/text() = \"" + headOfHouseholdID + "\"]/SiteServiceParticipation/PersonHistorical/HousingStatus/text()", 
			if (housingSituationCodeCSV1 == null || housingSituationCodeCSV1.isEmpty()) {
				log.info("Problem getting housing status.");
			}
			// get length in housing situation code; look up by the head of household's id
			List<SevenValDKRefused2> lengthOfStays = ph.getLengthOfStayAtPriorResidence();
			//"/Sources/Source/Export/Person[PersonID/IDNum/text() = \"" + headOfHouseholdID + "\"]/SiteServiceParticipation/PersonHistorical/LengthOfStayAtPriorResidence/text()", 
			if (!lengthOfStays.isEmpty() && housingSituationCodeCSV1 != null) {
				lengthInHousingSituationCodeCSV1 = String.valueOf(lengthOfStays.get(0).getValue());
				csv1Record[20] = lengthInHousingSituationCodeCSV1;
			}
			else if (lengthInHousingSituationCodeCSV1 == null || lengthInHousingSituationCodeCSV1.isEmpty()) {
				log.info("Problem getting Length of Stay");
			}
			
			// get Prior_Loc; look up by the head of household's id
			List<PriorResidence> priorResidences = ph.getPriorResidence();
			//"/Sources/Source/Export/Person[PersonID/IDNum/text() = \" + headOfHouseholdID + "\"]/SiteServiceParticipation/PersonHistorical/PriorResidence/PriorResidenceCode/text()"
			if (!priorResidences.isEmpty()) {
				priorLocCSV1 = String.valueOf(priorResidences.get(0).getPriorResidenceCode().getValue());
				csv1Record[25] = priorLocCSV1;
			}
			else if (priorLocCSV1 == null || priorLocCSV1.isEmpty()) {
				log.info("Problem getting prior location");
			}
			
			//write out the Family Income and Expense record to CSV3
			processIncomeAndSources(household, ph, ssp);
			
			try {
				PersonAddress personAddress = ph.getPersonAddress().get(0);
				if (personAddress  != null) {
					//for (PersonAddress personAddress : personAddresses) {
					//Process each address found, either temporary or last permanent
					//log.info("We located " + numberOfAddresses + " total addresses for the head of the household.");				
					// first, for each address in the list, check the lastpermanentzip element to make sure which csv column it goes in
					
					Integer isLastPermanentZIP = ((Long)personAddress.getIsLastPermanentZIP().getValue()).intValue();
					boolean isLastPermanent;
					//isLastPermanentZIP = xpath.evaluate("IsLastPermanentZIP", personAddress);
					//log.info("Problem getting IsLastPermanentZIP: " + e.toString());
					if (isLastPermanentZIP.equals("1")) {
							// put fields in Last Permanent Address columns
							isLastPermanent = true;
					} else if (isLastPermanentZIP.equals("0")) {
							// put fields in Temp Address columns
							isLastPermanent = false;
					} else {
						log.info("isLastPermanentZIP == neither 1 nor 0");
						isLastPermanent = false;
					}
					// then, for each address for that person, process it
					processPersonAddressHeadOfHousehold(headOfHouseholdID.toString(), isLastPermanent, personAddress);

					// process PersonPhoneNumber element
					//HUD HMIS XML doesn't differentiate between work and home telephone numbers, so we just get the first match and put it in home
					//String homeTelephoneCSV1= ((Element)personPhoneList.item(0)).getTextContent();										csvRecord[16] = "";					
					PhoneNumber personPhoneNumber = ph.getPersonPhoneNumber().get(0);
					//"/Sources/Source/Export/Person[PersonID/IDNum/text() = \"" + headOfHouseholdID + "\"]/SiteServiceParticipation/PersonHistorical/PersonPhoneNumber", doc, XPathConstants.NODESET);
					String homeTelephoneCSV1 = "";
					//if (!personPhoneNumbers.isEmpty()) {
					homeTelephoneCSV1 = personPhoneNumber.getValue();
					csv1Record[16] = homeTelephoneCSV1;
					//log.info("Problem getting PersonPhoneNumber: " + e.toString());
				}	
					
			} catch (Exception e) {
				log.info("Problem getting a personAddress; there may not have been one in the personhistorical.");
			}
		}

		// write out program participation to CSV 5, if it is a non-shelter stay and CSV 7 if it is a shelter stay
		//  to tell, use the the parent service's serviceType (aka Program Type Code).  The values are mapped as follows:
		/*Program Type Code
		1 = Emergency Shelter = CSV 7
		2 = Transitional Housing = CSV 5
		3 = Permanent Supportive Housing = CSV 5
		4 = Homeless Outreach = CSV 5
		5 = Homeless Prevention and Rapid Re-Housing = CSV 5
		6 = Services Only program = CSV 5
		7 = Other  = CSV 5
		8 = Safe Haven = CSV 5
		9 = Permanent Housing (e.g., Mod Rehab SRO, subsidized housing without services) = CSV 5
		
		For closed records (leave open blank), the 
		Reason for Leaving maps to Rosie Status_Code (CSV 5, field 7) like this:
		  			1 = Left for a housing opportunity before completing program: 940 = Left for Housing Before Completing Program
					2 = Completed program: 941 = Completed Program
					3 = Non-payment of rent/occupancy charge: 942 = Non-payment of Rent
					4 = Non-compliance with program: 943 = Non-compliance with Project
					5 = Criminal activity/destruction of property/violence: 944 = Criminal Activity/Destruction of Property/Violence
					6 = Reached maximum time allowed by project: 945 = Reached Maximum Time Allowed
		      		7 = Needs could not be met by program: 946 = Needs could not be met by project
		      		8 = Disagreement with rules/persons: 947 = Disagreement with Rules/persons
		      		9 = Death: 948 = Death
		      		10 = Unknown/disappeared:  950 = Unknown/Disappeared 
		      		11 = Other: 949 = Other
		      		
		 Destinations
		 for closed records also, just copy the value from HUD XML
		*/
		
		// write out program participation to CSV 5, if it is a non-shelter stay and CSV 7
		// get serviceType
		try {
			Service service = getServiceFromServiceKey(serviceKey);
			String serviceType = service.getServiceType().toString();
			if (serviceType != null) {
				if (serviceType.equals("1")) {
					writeOutCSV7(ssp, serviceKeyCSV1, 	sSNFamilyHeadCSV1);
				} else {
					writeOutCSV5(ssp, serviceKeyCSV1, 	sSNFamilyHeadCSV1);
				}
			}
			
		} catch (NullPointerException e) {
			log.info("Could not find a matching Service with the key: " + serviceKey);
		}
	}
	
	String getHeadOfHouseholdSSNFromHouseholdID(String householdID) {
		JAXBContext jc = null;
		//Use StAX to retrieve the head of household's ssn from the Household ID
		//First, use the Household ID to get the household element.
		InputStream zis = openXMLStream(hudXMLURL);
		String ssn = "";
		XMLStreamReader xmlr13 = null;
		try {
			// set up a StAX reader
			xmlr13 = xmlif.createXMLStreamReader(zis);
		} catch (Exception e) {
			log.info("exception caught creating StAX stream " + e.toString());
			e.printStackTrace();
		}
		try {
			jc = JAXBContext.newInstance(Household.class);
		} catch (JAXBException e) {
			log.info("JAXB Household.class context problem: " + e.toString());
			e.printStackTrace();
		}
		try {
			unmarshaller = jc.createUnmarshaller();
		} catch (JAXBException e) {
			log.info("Problem unmarshalling Household.class context: " + e.toString());
			e.printStackTrace();
		}
		try {
			while(xmlr13.hasNext()) {
				//Get and check each Household with the right Household ID
				xmlr13.next();
				if (xmlr13.isStartElement()) {
					if (xmlr13.getLocalName().equals("Household")) {
						xmlr13.require(XMLStreamConstants.START_ELEMENT, null, "Household");
						JAXBElement<Household> householdJAXB = unmarshaller.unmarshal(xmlr13, Household.class);
						Household household = (Household)householdJAXB.getValue();
						if (household.getHouseholdID().getIDStr() != null) {
							if (household.getHouseholdID().getIDStr().equals(householdID)) {
							//Then, use getHeadOfHouseholdSSNFromHousehold() method
								ssn = getHeadOfHouseholdSSNFromHousehold(household);
								break;
							}
						} else if (household.getHouseholdID().getIDNum() != null) {
							if ((household.getHouseholdID().getIDNum().toString()).equals(householdID)) {
							//Then, use getHeadOfHouseholdSSNFromHousehold() method
								ssn = getHeadOfHouseholdSSNFromHousehold(household);
								break;
							}
						}
					} 	
				}
			}
			xmlr13.close();
			zis.close();
		} catch (Exception e) {
			log.info("XML Stream exception caught: " + e.toString());
			e.printStackTrace();
		}
		return ssn;
	}
	
	BigInteger getHeadOfHouseholdIDFromHouseholdID(String householdID) {
		//Use StAX to retrieve the head of household's id from the Household ID
		//First, use the Household ID to get the household element.
		JAXBContext jc = null;
		InputStream zis = openXMLStream(hudXMLURL);
		BigInteger headOfHouseholdID = null;
		XMLStreamReader xmlr14 = null;
		try {
			// set up a StAX reader
			xmlr14 = xmlif.createXMLStreamReader(zis);
		} catch (Exception e) {
			log.info("exception caught creating StAX stream " + e.toString());
			e.printStackTrace();
		}
		try {
			jc = JAXBContext.newInstance(Household.class);
		} catch (JAXBException e) {
			log.info("JAXB Person.class context problem: " + e.toString());
			e.printStackTrace();
		}
		try {
			unmarshaller = jc.createUnmarshaller();
		} catch (JAXBException e) {
			log.info("Problem unmarshalling Person context: " + e.toString());
			e.printStackTrace();
		}
		try {
			while(xmlr14.hasNext()) {
				//Get and check each Household with the right Household ID
				xmlr14.next();
				if (xmlr14.isStartElement()) {
					if (xmlr14.getLocalName().equals("Household")) {
						xmlr14.require(XMLStreamConstants.START_ELEMENT, null, "Household");
						JAXBElement<Household> householdJAXB = unmarshaller.unmarshal(xmlr14, Household.class);
						Household household = (Household)householdJAXB.getValue();
						if (household.getHouseholdID().getIDStr() != null) {
							if (household.getHouseholdID().getIDStr().equals(householdID)) {
								headOfHouseholdID = household.getHeadOfHouseholdID().getIDNum();
								break;
							}
						} else if (household.getHouseholdID().getIDNum() != null) {
							if ((household.getHouseholdID().getIDNum().toString()).equals(householdID)) {
								headOfHouseholdID = household.getHeadOfHouseholdID().getIDNum();
								break;
							}
						}
					} 	
				}
			}
			xmlr14.close();
			zis.close();
		} catch (Exception e) {
			log.info("XML Stream exception caught: " + e.toString());
			e.printStackTrace();
		}
		return headOfHouseholdID;
	}
	
	Person getPersonByID(BigInteger headOfHouseholdID) {
		Person returnPerson = null;
		JAXBContext jc = null;
		InputStream zis = openXMLStream(hudXMLURL);
		XMLStreamReader xmlr15 = null;
		try {
			// set up a StAX reader
			xmlr15 = xmlif.createXMLStreamReader(zis);
		} catch (Exception e) {
			log.info("exception caught creating StAX stream " + e.toString());
			e.printStackTrace();
		}
		
		//Use StAX to look up the head of household's SSN
		try {
			jc = JAXBContext.newInstance(Person.class);
		} catch (JAXBException e) {
			log.info("JAXB Person.class context problem: " + e.toString());
			e.printStackTrace();
		}
		try {
			unmarshaller = jc.createUnmarshaller();
		} catch (JAXBException e) {
			log.info("Problem unmarshalling person context: " + e.toString());
			e.printStackTrace();
		}
		try {
			while(xmlr15.hasNext()) {
				//Get each person
				xmlr15.next();
				if (xmlr15.isStartElement()) {
					if (xmlr15.getLocalName().equals("Person")) {
						xmlr15.require(XMLStreamConstants.START_ELEMENT, null, "Person");
						JAXBElement<Person> personJAXB = unmarshaller.unmarshal(xmlr15, Person.class);
						Person person = (Person)personJAXB.getValue();
						if (person.getPersonID().getIDNum().equals(headOfHouseholdID)) {
							returnPerson = person;
							break;
						}
					}
				}
			}
			xmlr15.close();
			zis.close();
		} catch (Exception e) {
			log.info("XML Stream exception caught: " + e.toString());
			e.printStackTrace();
		}
		return returnPerson;
	}
	
	Household  getHouseholdFromHouseholdID(String householdID) {
		Household returnHousehold = null;
		log.info("Attempting to get household for household id: " + householdID);
		//Using StAX to get all the household ids for File 2 Household Membership Generation
		JAXBContext jc = null;
		InputStream zis = openXMLStream(hudXMLURL);

		XMLStreamReader xmlr15 = null;
		try {
			
			// set up a StAX reader
			xmlr15 = xmlif.createXMLStreamReader(zis);
		} catch (Exception e) {
			log.info("exception caught creating StAX stream " + e.toString());
			e.printStackTrace();
		}
		try {
			jc = JAXBContext.newInstance(Household.class);
		} catch (JAXBException e) {
			e.toString();
			e.printStackTrace();
		}
		Unmarshaller unmarshaller = null;
		try {
			unmarshaller = jc.createUnmarshaller();
		} catch (JAXBException e) {
			log.info(e.toString());
			e.printStackTrace();
		}
		try {
			while(xmlr15.hasNext()) {
				//Get each household
				xmlr15.next();
				if (xmlr15.isStartElement()) {
					//log.info("name is:" + xmlr15.getLocalName());
					if (xmlr15.getLocalName().equals("Household")  && xmlr15.isStartElement()) {
						xmlr15.require(XMLStreamConstants.START_ELEMENT, null, "Household");
						JAXBElement<Household> householdJAXB = unmarshaller.unmarshal(xmlr15,Household.class);
						Household householdElement = (Household)householdJAXB.getValue();
						if (householdElement.getHouseholdID().getIDStr() != null) {
							if (householdElement.getHouseholdID().getIDStr().equals(householdID)) {
								log.info("found a matching household for household id: " + householdID);
								returnHousehold = householdElement;
								break;
							}
						} else if (householdElement.getHouseholdID().getIDNum() != null) {
							if (householdElement.getHouseholdID().getIDNum().toString().equals(householdID)) {
								log.info("found a matching household for household id: " + householdID);
								returnHousehold = householdElement;
								break;
							}
						} else {log.info("Couldn't find a household match in getHouseholdFromHouseholdID.");}
					}
				}
			}
			xmlr15.close();
			zis.close();
		} catch (Exception e) {
			log.info("XML Stream exception caught: " + e.toString());
			e.printStackTrace();
		}
		return returnHousehold;
	}
	
	Service getServiceFromServiceKey(BigInteger serviceKey) {
		JAXBContext jc = null;
		Service retSvc = null;
		//Use StAX to retrieve the Service by ID
		//First, use the Household ID to get the household element.
		InputStream zis = openXMLStream(hudXMLURL);
		XMLStreamReader xmlr14 = null;
		try {
			// set up a StAX reader
			xmlr14 = xmlif.createXMLStreamReader(zis);
		} catch (Exception e) {
			log.info("exception caught creating StAX stream " + e.toString());
			e.printStackTrace();
		}
		try {
			jc = JAXBContext.newInstance(Service.class);
		} catch (JAXBException e) {
			log.info("JAXB Service.class context problem: " + e.toString());
			e.printStackTrace();
		}
		try {
			unmarshaller = jc.createUnmarshaller();
		} catch (JAXBException e) {
			log.info("Problem unmarshalling Service.class context: " + e.toString());
			e.printStackTrace();
		}
		try {
			while(xmlr14.hasNext()) {
				//Get and check each Household with the right Household ID
				xmlr14.next();
				if (xmlr14.isStartElement()) {
					if (xmlr14.getLocalName().equals("Service")) {
						xmlr14.require(XMLStreamConstants.START_ELEMENT, null, "Service");
						JAXBElement<Service> serviceJAXB = unmarshaller.unmarshal(xmlr14, Service.class);
						Service service = (Service)serviceJAXB.getValue();
						if (service.getKey() != null) {
							if (service.getKey().equals(serviceKey)) {
								retSvc = service;
								break;
							}
						} 
					} 	
				}
			}
			xmlr14.close();
			zis.close();
		} catch (Exception e) {
			log.info("XML Stream exception caught: " + e.toString());
			e.printStackTrace();
		}
		return retSvc;
	}
	
	String mapReasonForLeaving(Integer rosieValue) {
		String mappedValue;
		switch  (rosieValue) {
			case 1: //Left for a housing opportunity before completing program: 940 = Left for Housing Before Completing Program
				mappedValue = "940";
				break;
			case 2: //2 = Completed program: 941 = Completed Program
				mappedValue = "941";
				break;
			case 3: //3 = Non-payment of rent/occupancy charge: 942 = Non-payment of Rent
				mappedValue = "942";
				break;
			case 4:  //4 = Non-compliance with program: 943 = Non-compliance with Project
				mappedValue = "943";
				break;
			case 5:  //5 = Criminal activity/destruction of property/violence: 944 = Criminal Activity/Destruction of Property/Violence
				mappedValue = "944";
				break;
			case 6:  //6 = Reached maximum time allowed by project: 945 = Reached Maximum Time Allowed
				mappedValue = "945";
			case 7:  //7 = Needs could not be met by program: 946 = Needs could not be met by project
				mappedValue = "946";
				break;
			case 8:  //8 = Disagreement with rules/persons: 947 = Disagreement with Rules/persons
				mappedValue = "947";
			case 9:  //9 = Death: 948 = Death
				mappedValue = "948";
				break;
			case 10:  //10 = Unknown/disappeared:  950 = Unknown/Disappeared 
				mappedValue = "950";
				break;
			case 11:  //11 = Other: 949 = Other
				mappedValue = "949";
				break;
			default: mappedValue = "";
		}
		return mappedValue;
	}
	
	void processMembersAndHeadsOfHouseholds(Household household, Object o) {
		JAXBContext jc = null;
		log.info("inside processHouseholdMemberAndHeadsOfHouseholds()");
		InputStream zis = openXMLStream(hudXMLURL);
		String householdID = null;
		XMLStreamReader xmlr4 = null;
		try {

			// set up a StAX reader
			xmlr4 = xmlif.createXMLStreamReader(zis);
		}   catch (Exception e) {
			log.info("exception caught creating StAX stream " + e.toString());
			e.printStackTrace();
		}
		if (household.getHouseholdID().getIDStr() !=null) {
			householdID = household.getHouseholdID().getIDStr();
		} else if (household.getHouseholdID().getIDNum() !=null) {
			BigInteger householdIDNum = household.getHouseholdID().getIDNum();
			householdID = householdIDNum.toString();
		} else {log.info("couldn't extract ID (string or num from the household element");}
		
		BigInteger personID = null;
		if (o instanceof Member) {
			// if it's a household member, get IDNum
			personID = ((Member)o).getPersonID().getIDNum();
		}  else if (o instanceof BigInteger) {
			// if it's the Head of Household, get IDNum
			personID = ((BigInteger)o);
		} else {log.info("can't get IDNum of household member");}
		//Get Service Key (Rosie would normally get AgencyCode) from a SiteServiceParticipation the household member person has
		//This is problematic, since this household id might have received services from many agencies.  We should make household records for each of them with a different agency

		//Right now, this only looks at Export/Person/SiteServiceParticipation, but it should also look at Export/SiteServiceParticipation
		//Use StAX to find the matching Person element with the same ID as the household member
		try {
			jc = JAXBContext.newInstance(Person.class);
		}  catch (JAXBException e) {
			log.info("JAXB Person.class context problem: " + e.toString());
			e.printStackTrace();
		}
		try {
			unmarshaller = jc.createUnmarshaller();
		}  catch (JAXBException e) {
			log.info("Problem unmarshalling person context: " + e.toString());
			e.printStackTrace();
		}
		try {
			while(xmlr4.hasNext()) {
				//log.info("While inside xmlr.hasNext()");
				//Get each person
				xmlr4.next();
				if (xmlr4.isStartElement()) {
					if (xmlr4.getLocalName().equals("Person")) {
						//log.info("We found a Person element.");
						xmlr4.require(XMLStreamConstants.START_ELEMENT, null, "Person");
						JAXBElement<Person> personJAXB = unmarshaller.unmarshal(xmlr4, Person.class);
						Person person	= (Person)personJAXB.getValue();		
						// if it's not head of household, but a member
						if (o instanceof Member) {
							if (personID.equals(person.getPersonID().getIDNum())) {
								log.info("found a match.");
								processMemberStage2(o, household, person);
								convertSiteServiceParticipationsToRosieCSV1(householdID, household, personID);
								break;
							} 
						// if it's the head of household	
						} else if (o instanceof BigInteger) {
							if (((BigInteger)o).equals(person.getPersonID().getIDNum())) {
								csv2Record[5] = String.valueOf(householdMemberCounterCSV2);
						
								//Get the family member's relationship to the head of household
								String 	relationshipCodeCSV2 = "0";//self
									//relationshipCodeCSV2 = xpath.evaluate("RelationshipToHeadOfHousehold/text()", familyMember);
								csv2Record[7] = relationshipCodeCSV2;
								processMemberStage2(o, household, person);
								convertSiteServiceParticipationsToRosieCSV1(householdID, household, personID);
								break;
							}
						}
					}
				}
			}
			xmlr4.close();
			zis.close();
		}   catch (Exception e) {
				log.info("XML Stream exception caught: " + e.toString());
				e.printStackTrace();
			}
		}
		
		void convertSiteServiceParticipationsToRosieCSV1(String householdID, Household household, BigInteger personID) {
			// add in file 1 processing driven off enrollments
			try {
				ArrayList<SiteServiceParticipation> ssps = getSiteServiceParticipationsForPersonIDInHouseholdID(personID, householdID);
				for (SiteServiceParticipation ssp : ssps) {
					convertSiteServiceParticipationToRosieCSV1(ssp);
					//write out the Member Health Problems record to CSV4
					processHealthStatus(ssp, household);
					//write out the Family Assistance Provided record to CSV6
					processFundingSourceServiceEvent(ssp, household);
					//clear Record Type (file) 1
					//clear record - don't clear record types 1 or 2 until types 3-7 written out
					for (int i = 0; i < csv1Record.length; i++) {
						csv1Record[i] = "";
					}
				}
			} catch (NullPointerException e) {log.info("No enrollments found for personID:" + personID);}
	}

	void processMemberStage2(Object o, Household household, Person person) {
		String familyMemberSSNCSV2;
		String familyMemberFirstNameCSV2;
		String familyMemberMiddleNameCSV2;
		String familyMemberLastNameCSV2;
		String familyMemberDateOfBirthCSV2;
		String familyMemberGenderCSV2;
		String familyMemberRace1CSV2;
		String familyMemberRace2CSV2;
		String familyMemberHispanicCSV2;
		//Get person information
		//TransactionDate_mmddyy - use HeadOfHouseholdID's effectiveDate
		//String transactionDateCSV2;
		//String transactionTimeCSV2;
		
		//clear the parts of the record that are set in this section, so other members can reuse the common parts for the family
		//for (int i = 0; i < csvRecord.length; i++) {
		//csvRecord[i] = "";
		csv2Record[0] = "";
		csv2Record[1] = "";
		csv2Record[2] = "";
		csv2Record[6] = "";
		csv2Record[8] = "";
		csv2Record[9] = "";
		csv2Record[10] = "";
		csv2Record[11] = "";
		csv2Record[12] = "";
		csv2Record[13] = "";
		csv2Record[14] = "";
		csv2Record[15] = "";
		
		try {
			familyMemberSSNCSV2 = person.getSocialSecurityNumber().getUnhashed().getValue();
			csv2Record[6] = familyMemberSSNCSV2;
		} 
		catch (Exception e) {
			log.info("Couldn't get SSN");// + e.toString());
		}
		//Get the family member's first name from their Person record
		try {
			familyMemberFirstNameCSV2 = person.getLegalFirstName().getUnhashed().getValue();	
			//log.info("Problem getting LegalFirstName: " + e.toString());
			csv2Record[8] = familyMemberFirstNameCSV2;
		}
		catch (Exception e) {
			log.info("Couldn't get First Name: " + e.toString());
		} 
		try {
			//Get the family member's middle name from their Person record
			familyMemberMiddleNameCSV2 = person.getLegalMiddleName().getUnhashed().getValue();	
			//log.info("Problem getting LegalMiddleName: " + e.toString());
			csv2Record[9] = familyMemberMiddleNameCSV2;
		}
		catch  (Exception e) {
			log.info("Couldn't get Middle Name: " + e.toString());
		}
		try {
			//Get the family member's last name from their Person record
			familyMemberLastNameCSV2 =  person.getLegalLastName().getUnhashed().getValue();	
			//log.info("Problem getting LegalLastName: " + e.toString());
			csv2Record[10] = familyMemberLastNameCSV2;
		}
		catch (Exception e) {
			log.info("Couldn't get Last Name: " + e.toString());
		}
		try {
			//Get the family member's dob from their Person record
			GregorianCalendar calendar2 =  person.getDateOfBirth().getUnhashed().getValue().toGregorianCalendar();	
			//	String xMLDOB =  ((NodeList)xpath.evaluate(
			//		"DateOfBirth/Unhashed/text()", person, XPathConstants.NODESET)).item(0).getTextContent();
			//yyyymmdd->mmddyy
			SimpleDateFormat formatOutDate2 = new SimpleDateFormat("MMddyy");
			formatOutDate2.setCalendar(calendar2);
			Date date2;
			date2 = calendar2.getTime();
			//log.info("Problem parsing DOB: " + e.toString());
			familyMemberDateOfBirthCSV2 = formatOutDate2.format(date2);			
			//log.info("General exception handling DOB: " + e.toString());
			csv2Record[11] = familyMemberDateOfBirthCSV2;
		}
		catch (Exception e) {
			log.info("Couldn't get DOB: ");// + e.toString());
		}
		try {
			//Get the family member's gender from their Person record
			familyMemberGenderCSV2 =  String.valueOf(person.getGender().getUnhashed().getValue());
			//"Gender/Unhashed/text()", person, XPathConstants.NODESET)).item(0).getTextContent();
			//log.info("Problem getting gender: " + e.toString());
			csv2Record[12] = familyMemberGenderCSV2;
		}
		catch (Exception e) {
			log.info("Couldn't get Gender: " + e.toString());
		}
		try {						
			//Get the family member's races from their Person record
			//xpath.evaluate("Race", person, XPathConstants.NODESET);
			familyMemberRace1CSV2 = String.valueOf(person.getRace().get(0).getUnhashed().getValue());
			//log.info("Problem getting races: " + e.toString());
			csv2Record[13] = familyMemberRace1CSV2;
		}
		catch (Exception e) {
			log.info("Couldn't get Race1: " + e.toString());
		}
		try {						
			familyMemberRace2CSV2 = String.valueOf(person.getRace().get(1).getUnhashed().getValue());				
			csv2Record[14] = familyMemberRace2CSV2;
		}
		catch (Exception e) {
			log.info("Couldn't get Race2: ");// + e.toString());
		}
		try {						
			//Get the family member's ethnicity from their Person record			
			familyMemberHispanicCSV2 = "";
			int ethnicity =  (int) person.getEthnicity().getUnhashed().getValue();
			switch (ethnicity) {
			case 0: familyMemberHispanicCSV2 = "N";
			break;
			case 1: familyMemberHispanicCSV2 = "Y";
			break;
			case 8:;
			break;
			case 9:;
			break;
			default: ;
			}
			//log.info("Caught exception while getting ethnicity: " + e.toString());	
			csv2Record[15] = familyMemberHispanicCSV2;
		}
		catch (Exception e) {
			log.info("Couldn't get Ethnicity: " + e.toString());
		}
		//Get the person's household's agency affiliation.
		//We can't use the person's directly attributed enrollments to find this, because
		//  there may not be any enrollments for the person; they may all be under the head of household's record
		//SiteServiceParticipation oneSsp = getOneSiteServiceParticipationForHousehold(household);
		//BigInteger serviceKeyCSV2 = getServiceIDFromSiteServiceID(oneSsp.getSiteServiceID());
		//csvRecord[0] = serviceKeyCSV2.toString();
		log.info("csv2Record[0] = " + csv2Record[0] );
		//Get all this household member's enrollments
		List <SiteServiceParticipation> ssps = person.getSiteServiceParticipation();
		//Exclude those that don't have the same household id
		for (SiteServiceParticipation ssp : ssps) {
			String hoh_id;
			if (ssp.getHouseholdID().getIDStr() != null) {
				hoh_id = ssp.getHouseholdID().getIDStr();
			} else if (ssp.getHouseholdID().getIDNum() != null) {
				hoh_id = ssp.getHouseholdID().getIDNum().toString();
			} else {hoh_id = "";  log.info("Couldn't get the head of household");}
			
			String hh_id;
			if (household.getHouseholdID().getIDStr() != null) {
				hh_id = ssp.getHouseholdID().getIDStr();
			} else if (household.getHouseholdID().getIDNum() != null) {
				hh_id = household.getHouseholdID().getIDNum().toString();
			} else {hh_id = "";  log.info("Couldn't get the head of household");}
			
			try {
				if (hoh_id.equals(hh_id)) {
					//The rest of the CSV2 Family Information records are located in personHistorical, fetch that section for this person
					for (PersonHistorical personHistorical : ssp.getPersonHistorical()) {
						processPersonHistoricalForCSV2(personHistorical);											
					}
				} 
				else {
					log.info("household ids don't match");
				}
			} catch (Exception e) {log.info ("SiteServiceParticipation: " + ssp.getSiteServiceParticipationID().getIDNum().toString() + " doesn't have a householdID?" + e.toString());}
		}
	}
	
	void writeNextAndClose(String[] csvRecord) {
		//actually writes a line to the app engine file, but doesn't finalize it, so not persisted in console, but it's there
		try {
			//CSVWriter writer = new CSVWriter(new BufferedWriter(pw1), ',', '"', "\r\n");
			StringWriter sw = new StringWriter();
			CSVWriter writer = new CSVWriter(sw, ',', '"', "\r\n");
			writer.flush();
			writer.writeNext(csvRecord);
			writer.close();
			RosieRecords rr = new RosieRecords();
			log.info("writing out csv record:" + sw.toString());
			rr.setValue(sw.toString());
			Date now = new Date();
			rr.setTimestamp(now);
			rr.setUsed(0);
			RosieRecordsService rrs = new RosieRecordsService();
			rrs.createRosieRecords(rr);
		} catch (IOException e) {
			log.info("Problem closing file in writeNextAndClose: " + e.toString());
		}
	}
	
	void writeOutCSV5(SiteServiceParticipation ssp, String serviceKeyCSV1, 
			String sSNFamilyHeadCSV1) {
		
		//write out two records: one for the entry date(if it exists), one for the exit date (if it exists)
		if (ssp.getParticipationDates().getStartDate() != null) {
			GregorianCalendar calendar = ssp.getParticipationDates().getStartDate().toGregorianCalendar();
			SimpleDateFormat formatOutDate = new SimpleDateFormat("MMddyy");
			SimpleDateFormat formatOutTime = new SimpleDateFormat("HHmmss");
			formatOutDate.setCalendar(calendar);
			formatOutTime.setCalendar(calendar);
			//SimpleDateFormat formatIn = new SimpleDateFormat("yyyy-MM-dd'T'hh':'mm':'ss");
			Date date;
			date = calendar.getTime();
			String transactionDateCSV5entry = formatOutDate.format(date);
			String transactionTimeCSV5entry = formatOutTime.format(date);
			// write out the whole record into CSV 5
			log.info("writing out file 5");
			csv5Record[0] = serviceKeyCSV1;
			csv5Record[1] = transactionDateCSV5entry;
			csv1Record[1] = csv5Record[1];
			csv2Record[1] = csv5Record[1];
			csv5Record[2] = transactionTimeCSV5entry;
			csv1Record[2] = csv5Record[2];
			csv2Record[2] = csv5Record[2];			
			csv5Record[3] = sSNFamilyHeadCSV1;
			csv5Record[4] = "5";
			csv5Record[5] = String.valueOf(householdMemberCounterCSV2);
			csv5Record[6] = ""; //this is an entry, so always null
			csv5Record[7] = ""; //this is an entry, so always null
			//just gets first financial assistance from first service event.  could scan all service events for first financial assistance and use that
			csv5Record[8] = ""; //we assume there will be an exit, and we'll attribute the assistance to that.  we could check if there's an exit first, 
				//and record it if there isn't an exit.  we just have to avoid double-counting
			csv5Record[9] = ""; //only populated in exit
			
			// write out whole record into the datastore
			log.info("writing out file 1 record to cloud sql");
			writeNextAndClose(csv1Record);
	
			// write out the whole record into CSV 2
			log.info("Writing out CSV2 Record");
			writeNextAndClose(csv2Record);
			
			// write out the whole record into CSV 5
			writeNextAndClose(csv5Record);

			//clear record
			for (int i = 0; i < csv5Record.length; i++) {
				csv5Record[i] = "";
			}
		}
		
		if (ssp.getParticipationDates().getEndDate() != null) {
			GregorianCalendar calendar = ssp.getParticipationDates().getEndDate().toGregorianCalendar();
			SimpleDateFormat formatOutDate = new SimpleDateFormat("MMddyy");
			SimpleDateFormat formatOutTime = new SimpleDateFormat("HHmmss");
			formatOutDate.setCalendar(calendar);
			formatOutTime.setCalendar(calendar);
			//SimpleDateFormat formatIn = new SimpleDateFormat("yyyy-MM-dd'T'hh':'mm':'ss");
			Date date;
			date = calendar.getTime();
			String transactionDateCSV5exit = formatOutDate.format(date);
			String transactionTimeCSV5exit = formatOutTime.format(date);
			// write out the whole record into CSV 5
			log.info("writing out file 5");
			csv5Record[0] = serviceKeyCSV1;
			csv5Record[1] = transactionDateCSV5exit;
			csv1Record[1] = csv5Record[1]; 
			csv2Record[1] = csv5Record[1]; 
			csv5Record[2] = transactionTimeCSV5exit;
			csv1Record[2] = csv5Record[2]; 
			csv2Record[2] = csv5Record[2];
			csv5Record[3] = sSNFamilyHeadCSV1;
			csv5Record[4] = "5"; 
			csv5Record[5] = String.valueOf(householdMemberCounterCSV2);
			try {
				csv5Record[6] = mapReasonForLeaving((int)(ssp.getReasonsForLeaving().get(0).getReasonForLeaving().getValue()));
			} catch (Exception e) {e.printStackTrace();}
			try {
				for (ServiceEvent se : ssp.getServiceEvent()) {
					try {
						String fa = se.getFundingSources().getFundingSource().get(0).getFinancialAssistanceAmount().toString();
						if (fa != null && !fa.isEmpty()) {
							csv5Record[8] = fa;
							break;
						}
					} catch (NullPointerException e) {log.info("This funding source is null; checking for another.");}
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			//just gets the first destination in the first person historical.  It could scan all ph for the first destination as well.
			try {
				//gets the first destination it can find
				for (PersonHistorical ph : ssp.getPersonHistorical()) {
					try {
						Destination destination = ph.getDestinations().getDestination().get(0);
						String d = String.valueOf(destination.getDestinationCode().getValue());
						if (d != null && !d.isEmpty()) {
							csv5Record[9] = d;
							break;
						}
					} catch (NullPointerException e) {log.info("This destination is null; checking for another destination perhaps.");}
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			
			// write out the whole record into CSV 1
			log.info("writing out file 1");
			writeNextAndClose(csv1Record);
			
			// write out the whole record into CSV 2
			log.info("Writing out CSV2 Record");
			writeNextAndClose(csv2Record);
			
			// write out the whole record into CSV 5
			writeNextAndClose(csv5Record);
		}
		//clear record
		for (int i = 0; i < csv5Record.length; i++) {
			csv5Record[i] = "";
		}
	}
	
	void writeOutCSV7(SiteServiceParticipation ssp, String serviceKeyCSV1, 
		String sSNFamilyHeadCSV1) {
		// write out the whole record into CSV 7
		log.info("writing out file 7");
		// write a single record to record both the entry and exit date 
		csv7Record[0] = serviceKeyCSV1;
		GregorianCalendar calendar = ssp.getParticipationDates().getStartDate().toGregorianCalendar();
		SimpleDateFormat formatOutDate = new SimpleDateFormat("MMddyy");
		SimpleDateFormat formatOutTime = new SimpleDateFormat("HHmmss");
		formatOutDate.setCalendar(calendar);
		formatOutTime.setCalendar(calendar);
		//SimpleDateFormat formatIn = new SimpleDateFormat("yyyy-MM-dd'T'hh':'mm':'ss");
		Date date;
		date = calendar.getTime();
		String transactionDateCSV7 = formatOutDate.format(date);
		String transactionTimeCSV7 = formatOutTime.format(date);
		csv7Record[1] = transactionDateCSV7;
		csv1Record[1] = csv7Record[1];
		csv2Record[1] = csv7Record[1];
		csv7Record[2] = transactionTimeCSV7;
		csv1Record[2] = csv7Record[2];
		csv2Record[2] = csv7Record[2];
		csv7Record[3] = sSNFamilyHeadCSV1;
		csv7Record[4] = "7";
		//yyyymmdd->mmddyy
		SimpleDateFormat formatOutDate2CSV7 = new SimpleDateFormat("MMddyy");
		formatOutDate2CSV7.setCalendar(calendar);
		csv7Record[5] = formatOutDate2CSV7.format(date);			
		calendar = ssp.getParticipationDates().getEndDate().toGregorianCalendar();
		formatOutDate2CSV7.setCalendar(calendar);
		date = calendar.getTime();
		csv7Record[6] = formatOutDate2CSV7.format(date);
		csv7Record[7] = ""; //this export never has bed unit info
		try {
			csv7Record[8] = mapReasonForLeaving((int)(ssp.getReasonsForLeaving().get(0).getReasonForLeaving().getValue()));
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		//just gets the first destination in the first person historical.  It could scan all ph for the first destination as well.
		try {
			//gets the first destination it can find
			for (PersonHistorical ph : ssp.getPersonHistorical()) {
				try {
					Destination destination = ph.getDestinations().getDestination().get(0);
					String d = String.valueOf(destination.getDestinationCode().getValue());
					if (d != null && !d.isEmpty()) {
						csv7Record[9] = d;
						break;
					}
				} catch (NullPointerException e) {log.info("This destination is null; checking for another destination perhaps.");}
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		// write out the whole record into CSV 1
		log.info("writing out file 1");
		writeNextAndClose(csv1Record);

		// write out the whole record into CSV 2
		log.info("Writing out CSV2 Record");
		writeNextAndClose(csv2Record);
		
		// write out the whole CSV 7 record into 
		writeNextAndClose(csv7Record);

		//clear record
		for (int i = 0; i < csv7Record.length; i++) {
			csv7Record[i] = "";
		}
	}
	void writeToGoogleCloudStorage() {
		//for each unused Google Cloud SQL "rosie_records" table record, write a line into a single GCS bucket, and close the file finally.
		RosieRecords rr = new RosieRecords();
		EntityManager em;
		em = EMF.get().createEntityManager(); 
		Long id_max = (Long)em.createQuery("select max(rr.id) from RosieRecords rr where rr.used =0").getSingleResult(); // this will give you the current record's id
		Long id_min = (Long)em.createQuery("select min(rr.id) from RosieRecords rr where rr.used =0").getSingleResult(); // this will give you the current record's id
		em.close();
		RosieRecordsService rrs = new RosieRecordsService();
		GcsFilename filename = new GcsFilename(BUCKETNAME, "RosieConversion_" + (new Date()).toString());
		GcsFileOptions options = new GcsFileOptions.Builder().mimeType("text/html").acl("public-read").build();
		    //.addUserMetadata("myfield1", "my field value").build();
		GcsOutputChannel writeChannel;
		try {
			writeChannel = gcsService.createOrReplace(filename, options);
			// You can write to the channel using the standard Java methods.
			// Here we use a PrintWriter:
			PrintWriter out = new PrintWriter(Channels.newWriter(writeChannel, "UTF8"));
			for (Long id_count = id_min; id_count <= id_max;  id_count++) {
				//write record value as a new line in Google Cloud Storage object
				rr = rrs.getRosieRecords(Long.valueOf(id_count));
				if (rr.getUsed() != 1) {
					log.info("writing out rosie record id: " + id_count);
					out.print(rr.getValue());
					out.flush();
					rrs.setRosieRecordsUsed(id_count);
				}
			}
			log.info("Done writing...");
			writeChannel.close();
		} catch (IOException e) {
			log.info("problem writing to GCS");
			e.printStackTrace();
		}
	}
}