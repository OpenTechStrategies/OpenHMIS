package org.pathways.openciss.rest.impl;

//import org.mortbay.log.Log;
import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.DOBHashingChoice;
import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.DateStatic;
import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.FourValDKRefusedHashingChoice;
import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.FourValDKRefusedStatic;
import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.HashingChoiceStamped;
import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.Person;
import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.SevenValDKRefused;
import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.SevenValDKRefused2HashingChoiceStatic;
import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.SevenValDKRefused2Static;
import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.SevenValDKRefusedHashingChoice;
import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.String50;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
//import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.pathways.openciss.model.ClientOutreach;
import org.pathways.openciss.model.PathClient;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.pathways.openciss.shared.ClientOutreachService;
import org.pathways.openciss.shared.EthnicityMap;
import org.pathways.openciss.shared.GenderMap;
import org.pathways.openciss.shared.Match;
import org.pathways.openciss.shared.PathClientService;
import org.pathways.openciss.shared.RaceMap;
//import javax.ws.rs.QueryParam;
//import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.HashingChoiceStamped;
//import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.String50;
//import org.pathways.openciss.IClient;
//import javax.xml.bind.JAXBType; no such thing
// package-info settings obviate the need for this:
//import org.pathways.openciss.shared.NamespacePrefixMap;
// for JPA persistence 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/client")
public class ClientAccess {
	
	private static final transient Logger log = LoggerFactory.getLogger(ClientAccess.class);
	//PathClientService pcs = new PathClientService();

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("outreach/add")
	public String outreachAdd(String incomingJSON) {
		// accepts JSON containing: int client_id, double latitude, double longitude, 
		//  Date contact_date, Date engagement_date
		// client id is the target key, not the origin key
		// returns outreach id upon success, and -1 upon failure
		// unmarshal the JSON into an array of personal identifiers
		//String[] outreachData = new String[5];

		System.out.println("POST  called on /outreach/add");
		System.out.println("POST data is: " + incomingJSON);
		ObjectMapper mapper = new ObjectMapper();
		try {
			// unmarshall the JSON
			JsonNode ob = mapper.readValue(incomingJSON, JsonNode.class);
			// look up this client, and add a sequential outreach
			ClientOutreach co = new ClientOutreach();
			PathClientService pcs = new PathClientService();
			
			try {
				Integer clientID = Integer.valueOf(ob.get("Client_ID").getTextValue());
				if (clientID != null) {
					System.out.println("client id is: " + clientID);
					co.setClientKey(pcs.getPathClient(clientID).getClientKey());
					//co.setClientKey(clientID.intValue());
				}
				BigDecimal latitude = BigDecimal.valueOf(Double.valueOf(ob.get("Latitude").getTextValue()));
				if (latitude != null) {
					co.setLatitude(latitude);
				}
				BigDecimal longitude = BigDecimal.valueOf(Double.valueOf(ob.get("Longitude").getTextValue()));
				if (longitude != null) {
					co.setLongitude(longitude);
				}
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
				Date contactDate =  df.parse(ob.get("Contact_Date").getTextValue());
				if (contactDate != null) {
					co.setContactDate(contactDate);
				}
				Date engagementDate =  df.parse(ob.get("Engagement_Date").getTextValue());
				if (engagementDate != null) {
					co.setEngagementDate(engagementDate);
				}
			} catch (Exception e){System.out.println(e);}
			ClientOutreachService cos = new ClientOutreachService();
			String outreachID = String.valueOf(cos.createClientOutreach(co));
			// return the generated id
			return String.valueOf(outreachID);
		} catch(Exception e){e.printStackTrace();}
		return "-1";
	}

	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("add")
	public String addClient(String incomingXML) {
		System.out.println("message POSTED to /client/add");
		String returnCode = String.valueOf(addProcess(incomingXML));
		return returnCode;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("update")
	public String updateClient(String incomingXML) {
		// need to determine how this will behave differently from client/add
		// both need to know which id to act upon.  Mainly, update also requires the
		//   ID to act upon.
		
		String clientID = String.valueOf(updateProcess(incomingXML));
		log.info("message POSTED to /client/update for client id: " + clientID);

		return clientID;
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("delete")
	public String deleteClient(@QueryParam("id") int clientID) {
		System.out.println("DELETE client called");
		//Person p = new Person();
		boolean b;
		PathClientService pcs = new PathClientService();
		PathClient pc = pcs.getPathClient(clientID);
		if (pc  != null) { // matching record found
			try {
				//b = (Boolean) pc.getInactive();
				b = (pc.getInactive())!=0;

				if (!b) { // that is, if active
					//byte by = (byte) (1);
					pc.setInactive(1); //make it inactive
					pcs.updatePathClient(pc); 
				} // otherwise, it's already inactive, so leave it alone
				return "200";
			} 
			catch (Exception e){e.printStackTrace();}
		}
		// no matching record found
		return "-1";
	}	
	
	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("get")
	public String getClient(@QueryParam("id") int clientID) {
		// accepts a client url id parameter
		// returns an hmis:Person XML instance, at least the basic personal identifiers: fn, ln, mn, dob, gender, eth, race
		// (ssn omitted, since it's usually just used for record matching identification/deduplication on add)
		// the rest will have to be obtained through subsequent, specialized queries.
		System.out.println("GET client called");
		String client;
		JAXBContext jc;
		try {
			//jc = JAXBContext.newInstance("info.hmis.schema._3_0.hud_hmis");
			Person p = new Person();
			jc = JAXBContext.newInstance(p.getClass());
			PathClientService pcs = new PathClientService();
			PathClient pc = pcs.getPathClient(clientID);
			if (pc  != null) {
				
				HashingChoiceStamped hcs_fn = new HashingChoiceStamped();
				String50 s50_fn = (new String50());
				s50_fn.setValue(pc.getNameFirst());
				hcs_fn.setUnhashed(s50_fn);
				p.setLegalFirstName(hcs_fn);	
				
				HashingChoiceStamped hcs_ln = new HashingChoiceStamped();
				String50 s50_ln = (new String50());
				s50_ln.setValue(pc.getNameLast());
				hcs_ln.setUnhashed(s50_ln);;	
				p.setLegalLastName(hcs_ln);	
				
				String50 s50_mn = new String50();
				HashingChoiceStamped hcs_mn = new HashingChoiceStamped();
				s50_mn.setValue(pc.getNameMiddle());
				hcs_mn.setUnhashed(s50_mn);
				p.setLegalMiddleName(hcs_mn);
				
				int eth = pc.getEthnicityCode().intValue();
				if (Integer.valueOf(eth) != null ) {
					FourValDKRefusedHashingChoice fourVal_hc = new FourValDKRefusedHashingChoice();
					FourValDKRefusedStatic fourVal_Static = new FourValDKRefusedStatic();
					eth = EthnicityMap.map(eth);
					if (eth != -1) {
						fourVal_Static.setValue((long)eth);
						fourVal_hc.setUnhashed(fourVal_Static);
						p.setEthnicity(fourVal_hc);
					}
				}
				
				int gen = pc.getGenderCode().intValue();
				if (Integer.valueOf(gen) != null ) {
					SevenValDKRefusedHashingChoice sevenValDKRef_hc = new SevenValDKRefusedHashingChoice();
					SevenValDKRefused sevenValDKRef = new SevenValDKRefused();
					gen = GenderMap.map(gen);
					if (gen != -1) {
						sevenValDKRef.setValue((long)gen);
						sevenValDKRef_hc.setUnhashed(sevenValDKRef);
						p.setGender(sevenValDKRef_hc);
					}
				}
				
				try {
					int race = pc.getRaceCode().intValue();
					if (Integer.valueOf(race) != null ) {
						SevenValDKRefused2HashingChoiceStatic sevenValDKRef_hcs = new SevenValDKRefused2HashingChoiceStatic();
						SevenValDKRefused2Static sevenValDKRef2S = new SevenValDKRefused2Static();
						race = RaceMap.map(race);
						if (race != -1) {
							sevenValDKRef2S.setValue((long)race);
							sevenValDKRef_hcs.setUnhashed(sevenValDKRef2S);
							p.getRace().add(sevenValDKRef_hcs);
						}
					}
				} catch (Exception e){e.printStackTrace();}
				if (pc.getDateOfBirth() !=null) {
					DOBHashingChoice dobhc = new DOBHashingChoice();
					DateStatic ds = new DateStatic();
					GregorianCalendar c = new GregorianCalendar();
					c.setTime(pc.getDateOfBirth());
					try {
						XMLGregorianCalendar gc = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
						gc.setTimezone(DatatypeConstants.FIELD_UNDEFINED);  
						gc.setTime(DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED);  
						ds.setValue(gc);
					} catch (DatatypeConfigurationException e) {e.printStackTrace();}	
					dobhc.setUnhashed(ds);
					p.setDateOfBirth(dobhc);
				}	
				Marshaller marshaller = jc.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				//NamespacePrefixMapper namespacePrefixMapper = new NamespacePrefixMapper();
				//marshaller.setProperty(	"com.sun.xml.bind.namespacePrefixMapper", namespacePrefixMapper);
				// marshaller.marshal( new JAXBElement(new QName("","rootTag"),Point.class,new Point(...)))
				//QName qname = new QName("person", "Person", "hmis");
				//QName qname = new QName("person", "Person");
				//JAXBElement<Person> person = new JAXBElement<Person>(qname, Person.class, p);
		        StringWriter sw = new StringWriter();
				//marshaller.marshal(person, sw);
				marshaller.marshal(p, sw);

		        client = sw.toString();
		        return client;
			}
			else {return "-1";}
			// if the client ID wasn't located in the database

			
        }
		catch (JAXBException e) {
            System.out.println(e);
        }
		return "-1";
	}
		
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("search")
	public String searchClient(String incomingJSON) {
		// accepts a JSON list of identifiers: f, l, mi, dob, gend, ssn
		// Technical Guidelines for Unduplicating and De-identifying HMIS Client Records
		// "Name, SSN, Date of Birth and Gender"
		// returns a list of ids of the clients that potentially match.  empty list means no matches

		// unmarshal the JSON into an array of personal identifiers
		String[] personalIdentifiers = new String[9];

		System.out.println("message POSTED to /client/search");
		System.out.println("POST data is: " + incomingJSON);
		//StringBuffer stringBuffer = new StringBuffer(incomingJSON); 
		ObjectMapper mapper = new ObjectMapper();
		
		// unmarshall the JSON
		JsonNode ob = null;
		try {
			ob = mapper.readValue(incomingJSON, JsonNode.class);
		} catch(Exception e){e.printStackTrace();}

		//personalIdentifiers.
		try {
			personalIdentifiers[0] = ob.get("First_Name").getTextValue();
		} catch (Exception e) {log.info("First_Name not retrieved");}
		try {	
			personalIdentifiers[1] = ob.get("First_Name_Alias").getTextValue();
		} catch (Exception e) {log.info("First_Name_Alias not retrieved");}
		try {
			personalIdentifiers[2] =  ob.get("Last_Name").getTextValue();
		} catch (Exception e) {log.info("Last_Name not retrieved");}
		try {
			personalIdentifiers[3] =  ob.get("Last_Name_Alias").getTextValue();				
		} catch (Exception e) {log.info("Last_Name_Alias not retrieved");}
		try {
			personalIdentifiers[4] =  ob.get("Middle_Initial").getTextValue();			
		} catch (Exception e) {log.info("Middle_Initial not retrieved");}
		try {
			personalIdentifiers[5] =  ob.get("Date_Of_Birth").getTextValue();
		} catch (Exception e) {log.info("Date_Of_Birth not retrieved");}
		try {				
			personalIdentifiers[6] = ob.get("Gender").getTextValue();
		} catch (Exception e) {log.info("Gender not retrieved");}
		try {				
			personalIdentifiers[7] =  ob.get("SSN").getTextValue();
		} catch (Exception e) {log.info("SSN not retrieved");}
		try {				
			personalIdentifiers[8] =  ob.get("SSN_Quality").getTextValue();
		} catch (Exception e) {log.info("SSN_Quality not retrieved");}
		//System.out.println("sent length of array to match: " + personalIdentifiers.length);
		//for (int i=0; i<personalIdentifiers.length; i++) {
			//System.out.println(personalIdentifiers[i]);
		//}
		// find matches for this client with these identifiers
		Match match = new Match();
		BigInteger[] matchResults = match.find(personalIdentifiers);
		
		// create JSON response of matching client ids
		ArrayNode matchedClientIDs = mapper.createArrayNode();
		System.out.println("matchResults are: " );
		for (int i=0; i<matchResults.length; i++) {
			log.info(matchResults[i].toString());
			matchedClientIDs.add(matchResults[i].toString());  
		}
		return matchedClientIDs.toString();
	}
	
	public int updateProcess(String incomingXML) {
		// returns the id of the client that was added
		// ideally should perform an unduplication routine before adding the client
		// for starters, we'll automatically add a new client.
		Integer clientID = null;
		System.out.println("POST data is: " + incomingXML);
		StringBuffer stringBuffer = new StringBuffer(incomingXML); 
		try {
			//JAXBContext jc = JAXBContext.newInstance("info.hmis.schema._3_0.hud_hmis");
			Person person = new Person();
			JAXBContext jc = JAXBContext.newInstance(person.getClass());
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			JAXBElement<?> p = unmarshaller.unmarshal(new StreamSource(new StringReader(stringBuffer.toString())), Person.class);
			Person pt = (Person)p.getValue();
			PathClientService pcs = new PathClientService();
			PathClient pc = new PathClient();
			
			try {
				String lfn = pt.getLegalFirstName().getUnhashed().getValue();
				if (lfn  != null) {
					pc.setNameFirst(lfn);
				}
			}
			catch(Exception e){log.info("Couldn't get LegalFirstName.  This is okay.");}
				
			try {
				String lln = pt.getLegalLastName().getUnhashed().getValue();
				if (lln  != null) {
					pc.setNameLast(lln);
				}
			}
			catch(Exception e){log.info("Couldn't get LegalLastName.  This is okay.");}
				
			try {
				String lmn = pt.getLegalMiddleName().getUnhashed().getValue();
				if (lmn  != null) {
					pc.setNameMiddle(lmn);
				}
			}
			catch(Exception e){log.info("Couldn't get LegalMiddleName.  This is okay.");}
				
			try {
				BigDecimal gender = new BigDecimal(pt.getGender().getUnhashed().getValue());
				if (gender  != null) {
					pc.setGenderCode(gender);
				}
			}
			catch(Exception e){log.info("Couldn't get Gender.  This is okay.");}
				
			try {				
				BigDecimal ethnicity = new BigDecimal(pt.getEthnicity().getUnhashed().getValue());
				if (ethnicity  != null) {
					pc.setEthnicityCode(ethnicity);
				}
			}
			catch(Exception e){log.info("Couldn't get Ethnicity.  This is okay.");}				
				
			try {				
				Date dob = pt.getDateOfBirth().getUnhashed().getValue().toGregorianCalendar().getTime();
				if (dob  != null) {
					pc.setDateOfBirth(dob);
				}
			}
			catch(Exception e){log.info("Couldn't get DoB.  This is okay.");}
			
			try {
				Integer ssn = Integer.valueOf(pt.getSocialSecurityNumber().getUnhashed().getValue());
				if (ssn  != null) {
					pc.setSsn(ssn);
				}
			}
			catch(Exception e){log.info("Couldn't get SSN.  This is okay.");}
			
			try {
				Integer ssnQualityCode = (int) pt.getSocialSecurityNumber().getSocialSecNumberQualityCode().getValue();
				if (ssnQualityCode  != null) {
					pc.setSsnQuality(ssnQualityCode);
				}
			}
			catch(Exception e){log.info("Couldn't get SSN Quality Code.  This is okay.");}
						
			//we only use int ids, not strings in openciss
			try {
				clientID = pt.getPersonID().getIDNum().intValue();
				if (clientID != 0) {
					pc.setClientKey(clientID);
				}
			}
			catch(Exception e){log.info("could not get person ID Number as an integer.  Maybe they sent strings or it is a client/add call?");}
			
			try {
				// assuming newly added clients aren't immediately inactivated
				if (pt.getPersonID().getDelete() == 1L) {
					pc.setInactive(1);
				} else {
					pc.setInactive(0);
				}
			}
			catch(Exception e){log.info("Could not set inactive to false");}
			
			
			if(pcs.updatePathClient(pc)) {
				return clientID;
			}		
			//client_id  is -1 if not successful, a positive number if successful
			else {
				return -1;
			}
		} catch (JAXBException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public int addProcess(String incomingXML) {
		// returns the id of the client that was added
		// ideally should perform an unduplication routine before adding the client
		// for starters, we'll automatically add a new client.
		Integer clientID = null;
		System.out.println("POST data is: " + incomingXML);
		StringBuffer stringBuffer = new StringBuffer(incomingXML); 
		try {
			//JAXBContext jc = JAXBContext.newInstance("info.hmis.schema._3_0.hud_hmis");
			Person person = new Person();
			JAXBContext jc = JAXBContext.newInstance(person.getClass());
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			JAXBElement<?> p = unmarshaller.unmarshal(new StreamSource(new StringReader(stringBuffer.toString())), Person.class);
			Person pt = (Person)p.getValue();
			PathClientService pcs = new PathClientService();
			PathClient pc = new PathClient();
			
			try {
				String lfn = pt.getLegalFirstName().getUnhashed().getValue();
				if (lfn  != null) {
					pc.setNameFirst(lfn);
				}
			}
			catch(Exception e){log.info("Couldn't get LegalFirstName.  This is okay.");}
				
			try {
				String lln = pt.getLegalLastName().getUnhashed().getValue();
				if (lln  != null) {
					pc.setNameLast(lln);
				}
			}
			catch(Exception e){log.info("Couldn't get LegalLastName.  This is okay.");}
				
			try {
				String lmn = pt.getLegalMiddleName().getUnhashed().getValue();
				if (lmn  != null) {
					pc.setNameMiddle(lmn);
				}
			}
			catch(Exception e){log.info("Couldn't get LegalMiddleName.  This is okay.");}
				
			try {
				BigDecimal gender = new BigDecimal(pt.getGender().getUnhashed().getValue());
				if (gender  != null) {
					pc.setGenderCode(gender);
				}
			}
			catch(Exception e){log.info("Couldn't get Gender.  This is okay.");}
				
			try {				
				BigDecimal ethnicity = new BigDecimal(pt.getEthnicity().getUnhashed().getValue());
				if (ethnicity  != null) {
					pc.setEthnicityCode(ethnicity);
				}
			}
			catch(Exception e){log.info("Couldn't get Ethnicity.  This is okay.");}				
				
			try {				
				Date dob = pt.getDateOfBirth().getUnhashed().getValue().toGregorianCalendar().getTime();
				if (dob  != null) {
					pc.setDateOfBirth(dob);
				}
			}
			catch(Exception e){log.info("Couldn't get DoB.  This is okay.");}
			
			try {
				Integer ssn = Integer.valueOf(pt.getSocialSecurityNumber().getUnhashed().getValue());
				if (ssn  != null) {
					pc.setSsn(ssn);
				}
			}
			catch(Exception e){log.info("Couldn't get SSN.  This is okay.");}
			
			try {
				Integer ssnQualityCode = (int) pt.getSocialSecurityNumber().getSocialSecNumberQualityCode().getValue();
				if (ssnQualityCode  != null) {
					pc.setSsnQuality(ssnQualityCode);
				}
			}
			catch(Exception e){log.info("Couldn't get SSN Quality Code.  This is okay.");}
			
			//we only use int ids, not strings in openciss
			try {
				clientID = pt.getPersonID().getIDNum().intValue();
				if (clientID != 0) {
					pc.setClientKey(clientID);
				}
			}
			catch(Exception e){log.info("could not get person ID Number as an integer.  Maybe they sent id strings?");}
			
			try {
				// assuming newly added clients aren't immediately inactivated
				pc.setInactive(0);
			}
			catch(Exception e){log.info("Could not set inactive to false");}
			
		    //clientID = pcs.createPathClient(pc);
			//client_id  is -1 if not successful, a positive client id number if successful
			return pcs.createPathClient(pc);
			
		} catch (JAXBException e) {
			e.printStackTrace();
			return -1;
		}
	}
}