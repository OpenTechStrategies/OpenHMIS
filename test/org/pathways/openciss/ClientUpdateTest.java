package org.pathways.openciss;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
//import java.net.URLConnection;
import org.junit.After;
import org.junit.AfterClass;
//import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
//import org.mortbay.log.Log;
//import org.pathways.openciss.rest.impl.ClientAccess;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;
import org.apache.commons.codec.binary.Base64;

public class ClientUpdateTest {
	//private static final transient Logger log = LoggerFactory.getLogger(ClientAccess.class);
	String charset = "UTF-8";
	static boolean b;
	String un = "username_here";
	String pw = "password_here";
	byte[] unpwBytes = (un + ":" +  pw).getBytes();
	byte[] unpwEnc = Base64.encodeBase64(unpwBytes);
	String unpwEncStr = new String(unpwEnc);
	String clientID; // client ID created in add and passed to update test

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
    	b = false;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
//	@Before
//	public void setUp() throws Exception {
//		String url = "http://localhost:8888/client/add";
//		//String url = "https://open-ciss.appspot.com/client/add";
//		//first, add a client that will be updated in the test.  get its id
//		final String toWriteOut = "<?xml version='1.0' encoding='UTF-8'?>\n\n" + "" +
//		"<hmis:Person xmlns:hmis='http://www.hmis.info/schema/3_0/HUD_HMIS.xsd'>" +
//		// this id is from the sending system, or a bogus id
//		"<hmis:PersonID><hmis:IDNum>1</hmis:IDNum></hmis:PersonID>" +
//		"<hmis:DateOfBirth><hmis:Unhashed hmis:dateCollected='2011-05-27T18:51:58' hmis:dataCollectionStage='2'>2004-03-11</hmis:Unhashed></hmis:DateOfBirth>" +
//		"<hmis:Ethnicity><hmis:Unhashed hmis:dateCollected='2011-06-25T18:51:58' hmis:dataCollectionStage='2'>8</hmis:Unhashed>" +
//		"</hmis:Ethnicity><hmis:Gender><hmis:Unhashed hmis:dateCollected='2010-06-05T18:51:58' hmis:dateEffective='2011-05-27T18:51:58' hmis:dataCollectionStage='1'>2</hmis:Unhashed>" +
//		"</hmis:Gender><hmis:LegalFirstName><hmis:Unhashed hmis:dateCollected='2011-01-27T18:51:58' hmis:dateEffective='2010-10-06T18:51:58' hmis:dataCollectionStage='3'>Jedediah</hmis:Unhashed>" +
//		"</hmis:LegalFirstName><hmis:LegalLastName><hmis:Unhashed hmis:dateCollected='2011-03-05T18:51:58' hmis:dateEffective='2010-09-23T18:51:58' hmis:dataCollectionStage='2'>Grimes</hmis:Unhashed>" +
//		"</hmis:LegalLastName><hmis:LegalMiddleName><hmis:Unhashed hmis:dateCollected='2011-10-03T18:51:58' hmis:dateEffective='2010-12-24T18:51:58' hmis:dataCollectionStage='3'>Bernard</hmis:Unhashed>" +
//		"</hmis:LegalMiddleName></hmis:Person>";
//		
//		try {
//			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
//			connection.setRequestMethod("POST");
//			connection.setRequestProperty("Accept-Charset", charset);
//			connection.setRequestProperty("Content-Type", "application/xml");
//			connection.setRequestProperty("Authorization", "Basic " + unpwEncStr);
//		    connection.setUseCaches (false);
//		    connection.setDoInput(true);
//		    connection.setDoOutput(true);
//		    final OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
//		    osw.write(toWriteOut);
//		    osw.close();
//			//Get Response	
//	        InputStream is = connection.getInputStream();
//	        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
//	        String line;
//	        StringBuffer response = new StringBuffer();
//	        while((line = rd.readLine()) != null) {
//		          response.append(line);
//		          clientID = response.toString();
//		          response.append('\r');
//		        }
//	        log.info("response from client/add is client id: " + response);
//	        rd.close();
//         } catch (Exception e) {
//        	 b = false;
//		 	System.out.println("in setup catch block");
//         }
//	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testClientUpdate() {		
		//String url = "http://localhost:8888/client/update";
		String url = "https://open-ciss.appspot.com/client/update";
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Accept-Charset", charset);
			connection.setRequestProperty("Content-Type", "application/xml");
			

			//just change the middle ame 
//			final String toWriteOut = "<?xml version='1.0' encoding='UTF-8'?>\n\n" + "" +
//					"<hmis:Person xmlns:hmis='http://www.hmis.info/schema/3_0/HUD_HMIS.xsd'><hmis:PersonID>" +
//					"<hmis:IDNum>" +  clientID + "</hmis:IDNum></hmis:PersonID>" +
//					"<hmis:DateOfBirth><hmis:Unhashed hmis:dateCollected='2011-05-27T18:51:58' hmis:dataCollectionStage='2'>2010-04-21</hmis:Unhashed>" +"</hmis:DateOfBirth>" +
//					"</hmis:Person>";
			
			//just change the dob for issue 23
			final String toWriteOut = "<?xml version='1.0' encoding='UTF-8'?>\n\n" + "" +
					"<hmis:Person xmlns:hmis='http://www.hmis.info/schema/3_0/HUD_HMIS.xsd'><hmis:PersonID>" +
					"<hmis:IDNum>25</hmis:IDNum></hmis:PersonID>" +
					"<hmis:DateOfBirth><hmis:Unhashed hmis:dateCollected='2011-05-27T18:51:58' hmis:dataCollectionStage='2'>2004-01-04</hmis:Unhashed>" +"</hmis:DateOfBirth>" +
					"</hmis:Person>";
			
			connection.setRequestProperty("Authorization", "Basic " + unpwEncStr);
			//connection.setRequestProperty(key, value)
		    connection.setUseCaches (false);
		    connection.setDoInput(true);
		    connection.setDoOutput(true);
		    final OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
		    osw.write(toWriteOut);
		    osw.close();
			//Get Response	
	        InputStream is = connection.getInputStream();
	        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	        String line;
	        StringBuffer response = new StringBuffer(); 
	        while((line = rd.readLine()) != null) {
	          response.append(line);
	          response.append('\r');
	        }
	        rd.close();
	        System.out.println(response.toString());
	        // need to make it so the unit test can fail if the response is not 200 or is -1.
         	b = true;
         } catch (Exception e) {
        	 b = false;
		 	System.out.println("in test catch block");
         }
		assertTrue("Result", b);
	}
}
