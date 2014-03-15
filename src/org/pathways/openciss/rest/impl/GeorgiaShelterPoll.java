package org.pathways.openciss.rest.impl;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.pathways.openciss.model.ProgramProfileInfo;

import org.pathways.openciss.shared.ProgramProfileInfoService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.appengine.api.rdbms.AppEngineDriver;
//import java.util.logging.Logger;
// for JPA

@Path("/cron/georgia_shelter_poll")
//@Path("/test/georgia_shelter_poll")
public class GeorgiaShelterPoll {
    
	ProgramProfileInfoService ppis = new ProgramProfileInfoService();
	String results = null;
	String[] ELEMENT_NAMES = {"name", "phone", "address", "city", "zip"};//"id", "shortName" omitted
	//private static final Logger log = Logger.getLogger("log1");

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	 public String fetchShelterList() {
		Connection dbconn = null;
		HttpURLConnection httpconn = null;
		System.out.println("inside /cron/georgia_shelter_poll");
		try {
			DriverManager.registerDriver(new AppEngineDriver());
			dbconn = DriverManager.getConnection("jdbc:google:rdbms://pcni.org:openhmis:openciss/compass");		
			try {
				URL url = new URL("http://72.242.201.146:8898/PcniWS/services/shelterList");
				httpconn = (HttpURLConnection) url.openConnection();
				httpconn.setRequestMethod("GET");
				//System.out.println("timeout is: " + httpconn.getReadTimeout());
				httpconn.setReadTimeout(0);
				//System.out.println("timeout is now: " + httpconn.getReadTimeout());
				httpconn.connect();
				InputStream in = httpconn.getInputStream();
				// before we go any further, delete the old data, now that we can at least connect to the new data store
				try {
					String statement = "DELETE FROM compass.program_profile_info WHERE feed_source=?";
					PreparedStatement pstmt = dbconn.prepareStatement(statement);
					pstmt.setString(1, "2");
					pstmt.executeUpdate();
					// set the auto-increment counter to the next highest unused index.  otherwise it with keep making a longer and longer gap in the indexes.
					String statement1 = "ALTER TABLE compass.program_profile_info AUTO_INCREMENT = 1";
					PreparedStatement pstmt1 = dbconn.prepareStatement(statement1);
					pstmt1.executeUpdate();
				} 
				catch (Exception e) {
					//e.printStackTrace();
					System.out.println("deletion block failed");
				}
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.parse(in);
				doc.getDocumentElement().normalize();
				System.out.println("Root element " + doc.getDocumentElement().getNodeName());
				NodeList shelterNodeList = doc.getElementsByTagName("shelter");
				for (int s = 0; s < shelterNodeList.getLength(); s++) {//s < 5; s++) {
					ProgramProfileInfo ppiToPersist = new ProgramProfileInfo();
					Node shelterNode = shelterNodeList.item(s);
				    if (shelterNode.getNodeType() == Node.ELEMENT_NODE) {
			           Element shelter = (Element) shelterNode;
			           for (String tagName : ELEMENT_NAMES) {
			        	   //System.out.println("adding " + tagName);
			        	   addElement(shelter, tagName, ppiToPersist);
			           }
					   ppiToPersist.setFeedSource(2);
					   ppiToPersist.setUpdateTimeStamp(new Timestamp(System.currentTimeMillis()));
			           //System.out.println("Done with one shelter record, now persisting");
			           ppis.updateProgramProfileInfo_Shelter(ppiToPersist);
				    }
				}
				System.out.println("Read poll data loop ended");
				httpconn.disconnect();
			}
			catch (Exception e) {
				//e.printStackTrace();
				System.out.println("http connect and input stream try block failure");
			}
		}
		catch (Exception e) {
			//e.printStackTrace();
			System.out.println("db connection failure");
		}
		finally {
			if (dbconn != null) {
				try {
					dbconn.close();
				} 
				catch (SQLException e) {System.out.println("error closing db connection");}
	        }
		}
		return "200";
	}
	
	void addElement(Element shelter, String name, ProgramProfileInfo ppiToPersist) {
		NodeList nameList;
		try {
			nameList = shelter.getElementsByTagName(name);
			Element element = (Element) nameList.item(0);
		    //System.out.println(element.getNodeName() + " " + element.getTextContent());
			if (name == "address") {
				ppiToPersist.setProgramAddress(element.getTextContent());
				//System.out.println("inside address");
			}
			if (name == "city") {
				ppiToPersist.setProgramCity(element.getTextContent());
				//System.out.println("inside city");

			}
			if (name == "name") {
				ppiToPersist.setProgramName(element.getTextContent());
				//System.out.println("inside name");

			}
			if (name == "phone") {
				ppiToPersist.setContactPhone(element.getTextContent());
				//System.out.println("inside phone");

			}
			if (name == "zip") {
				ppiToPersist.setProgramZip(element.getTextContent());
				//System.out.println("inside zip");
			}
		} catch (Exception e ) {System.out.println("node " + name + " does not exist for shelter id: " + 
			((Element) shelter.getElementsByTagName("id").item(0)).getTextContent());}
	}
}