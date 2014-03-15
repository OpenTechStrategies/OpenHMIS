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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.apache.commons.codec.binary.Base64;

public class ClientAddTest {

	static boolean b;

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
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testClientAdd() {		
		//String url = "http://localhost:8888/client/add";
		String url = "https://open-ciss.appspot.com/client/add";
		String charset = "UTF-8";
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Accept-Charset", charset);
			connection.setRequestProperty("Content-Type", "application/xml");
			
			String un = "username_here";
			String pw = "password_here";
//			final String toWriteOut = "<?xml version='1.0' encoding='UTF-8'?>\n\n" + "" +
//					"<hmis:Person xmlns:hmis='http://www.hmis.info/schema/3_0/HUD_HMIS.xsd'><hmis:PersonID>" +
//					"<hmis:IDNum>2090888539</hmis:IDNum></hmis:PersonID><hmis:DateOfBirth>" +
//					"<hmis:Unhashed hmis:dateCollected='2011-05-27T18:51:58' hmis:dataCollectionStage='2'>2010-04-21</hmis:Unhashed>" +
//					"</hmis:DateOfBirth>" +
//					"<hmis:Ethnicity><hmis:Unhashed hmis:dateCollected='2011-06-25T18:51:58' hmis:dataCollectionStage='2'>8</hmis:Unhashed>" +
//					"</hmis:Ethnicity><hmis:Gender><hmis:Unhashed hmis:dateCollected='2010-06-05T18:51:58' hmis:dateEffective='2011-05-27T18:51:58' hmis:dataCollectionStage='1'>2</hmis:Unhashed>" +
//					"</hmis:Gender><hmis:LegalFirstName><hmis:Unhashed hmis:dateCollected='2011-01-27T18:51:58' hmis:dateEffective='2010-10-06T18:51:58' hmis:dataCollectionStage='3'>Biff</hmis:Unhashed>" +
//					"</hmis:LegalFirstName><hmis:LegalLastName><hmis:Unhashed hmis:dateCollected='2011-03-05T18:51:58' hmis:dateEffective='2010-09-23T18:51:58' hmis:dataCollectionStage='2'>Jones</hmis:Unhashed>" +
//					"</hmis:LegalLastName><hmis:LegalMiddleName><hmis:Unhashed hmis:dateCollected='2011-10-03T18:51:58' hmis:dateEffective='2010-12-24T18:51:58' hmis:dataCollectionStage='3'>C</hmis:Unhashed>" +
//					"</hmis:LegalMiddleName></hmis:Person>";
		
		// to test issue 19 "client/get doesn't work if DOB is missing"
		final String toWriteOut = "<?xml version='1.0' encoding='UTF-8'?>\n\n" + "" +
				"<hmis:Person xmlns:hmis='http://www.hmis.info/schema/3_0/HUD_HMIS.xsd'><hmis:PersonID>" +
				"<hmis:IDNum>2090888539</hmis:IDNum></hmis:PersonID>" +
				//"<hmis:DateOfBirth>" +
				//	"<hmis:Unhashed hmis:dateCollected='2011-05-27T18:51:58' hmis:dataCollectionStage='2'>2010-04-21</hmis:Unhashed>" +
				//	"</hmis:DateOfBirth>" +
				"<hmis:Ethnicity><hmis:Unhashed hmis:dateCollected='2011-06-25T18:51:58' hmis:dataCollectionStage='2'>8</hmis:Unhashed>" +
				"</hmis:Ethnicity><hmis:Gender><hmis:Unhashed hmis:dateCollected='2010-06-05T18:51:58' hmis:dateEffective='2011-05-27T18:51:58' hmis:dataCollectionStage='1'>2</hmis:Unhashed>" +
				"</hmis:Gender><hmis:LegalFirstName><hmis:Unhashed hmis:dateCollected='2011-01-27T18:51:58' hmis:dateEffective='2010-10-06T18:51:58' hmis:dataCollectionStage='3'>Jiminy</hmis:Unhashed>" +
				"</hmis:LegalFirstName><hmis:LegalLastName><hmis:Unhashed hmis:dateCollected='2011-03-05T18:51:58' hmis:dateEffective='2010-09-23T18:51:58' hmis:dataCollectionStage='2'>Jehosafat</hmis:Unhashed>" +
				"</hmis:LegalLastName><hmis:LegalMiddleName><hmis:Unhashed hmis:dateCollected='2011-10-03T18:51:58' hmis:dateEffective='2010-12-24T18:51:58' hmis:dataCollectionStage='3'>Harold</hmis:Unhashed>" +
				"</hmis:LegalMiddleName></hmis:Person>";
			byte[] unpwBytes = (un + ":" +  pw).getBytes();
			byte[] unpwEnc = Base64.encodeBase64(unpwBytes);
			String unpwEncStr = new String(unpwEnc);
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
         	b = true;
         } catch (Exception e) {
        	 b = false;
		 	System.out.println("in catch block");
         }
		assertTrue("Result", b);
	}
}
