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

public class ClientSearchTest {


	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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
	public void testClientSearch() {		
		String r = "";
		//String url = "http://localhost:8888/client/search";
		String url = "https://open-ciss.appspot.com/client/search";
		String charset = "UTF-8";
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Accept-Charset", charset);
			connection.setRequestProperty("Content-Type", "application/json");
			
			String un = "username_here";
			String pw = "password_here";
			//String un = "testuserdynamic";
			//String pw = "doesthiswork";
			
//			final String requestJson = "{\"First_Name\":\"Hester\",\"First_Name_Alias\":\"\",\"Last_Name\":" +
//				"\"Heston\",\"Last_Name_Alias\":\"Gorda\",\"Middle_Initial\":\"B\",\"Date_Of_Birth\":" +
//				"\"2001-09-04\",\"Gender\":\"2\",\"SSN\":\"111-11-1111\",\"SSN_Quality\":\"1\"}";

//issue 21 with an SSN Quality Cose.  SSN matches, but Quality Code is 2, which is a no-no, so doesn't look any further, should return empty []
	final String requestJson = "{\"First_Name\":\"Smyrna\",\"First_Name_Alias\":\"\",\"Last_Name\":" +
		"\"Bethune\",\"Last_Name_Alias\":\"\",\"Middle_Initial\":\"\",\"Date_Of_Birth\":" +
		"\"\",\"Gender\":\"\",\"SSN\":\"111-11-1111\",\"SSN_Quality\":\"2\"}";

//issue 21 with an SSN Quality Cose.  SSN matches two people, Quality Code is 1, so doesn't look at other identifiers further, should return [3,4]
//	final String requestJson = "{\"First_Name\":\"Smyrna\",\"First_Name_Alias\":\"\",\"Last_Name\":" +
//		"\"Bethune\",\"Last_Name_Alias\":\"\",\"Middle_Initial\":\"\",\"Date_Of_Birth\":" +
//		"\"\",\"Gender\":\"\",\"SSN\":\"111-11-1111\",\"SSN_Quality\":\"1\"}";
			
//issue 21 without SSN Quality, SSN matches two people, Quality Code is null, so doesn't look at other identifiers further, should return [3,4]
//	final String requestJson = "{\"First_Name\":\"Smyrna\",\"First_Name_Alias\":\"\",\"Last_Name\":" +
//		"\"Bethune\",\"Last_Name_Alias\":\"\",\"Middle_Initial\":\"\",\"Date_Of_Birth\":" +
//		"\"\",\"Gender\":\"\",\"SSN\":\"111-11-1111\",\"SSN_Quality\":\"\"}";

//issue 22, no SSN, just matching on first/last names, should return [4]
//	final String requestJson = "{\"First_Name\":\"Smyrna\",\"First_Name_Alias\":\"\",\"Last_Name\":"
//		+ "\"Bethune\",\"Last_Name_Alias\":\"\",\"Middle_Initial\":\"\",\"Date_Of_Birth\":"
//		+ "\"\",\"Gender\":\"\",\"SSN\":\"\",\"SSN_Quality\":\"1\"}";
			
//issue 22, no SSN, just matching on first/dob, should return [4]
//	final String requestJson = "{\"First_Name\":\"Smyrna\",\"First_Name_Alias\":\"\",\"Last_Name\":"
//		+ "\"\",\"Last_Name_Alias\":\"\",\"Middle_Initial\":\"\",\"Date_Of_Birth\":"
//		+ "\"1999-04-30\",\"Gender\":\"\",\"SSN\":\"\",\"SSN_Quality\":\"1\"}";
			
// try above with only SSN different.  ssn takes precedence, returns [2]
//	final String requestJson = "{\"First_Name\":\"Smyrna\",\"First_Name_Alias\":\"\",\"Last_Name\":"
//		+ "\"\",\"Last_Name_Alias\":\"\",\"Middle_Initial\":\"\",\"Date_Of_Birth\":"
//		+ "\"1999-04-30\",\"Gender\":\"\",\"SSN\":\"222222222\",\"SSN_Quality\":\"1\"}";
		
// try above with bad SSN (non-matching).  still matches on other identifiers, returns [4]
//	final String requestJson = "{\"First_Name\":\"Smyrna\",\"First_Name_Alias\":\"\",\"Last_Name\":"
//		+ "\"\",\"Last_Name_Alias\":\"\",\"Middle_Initial\":\"\",\"Date_Of_Birth\":"
//		+ "\"1999-04-30\",\"Gender\":\"\",\"SSN\":\"234232345\",\"SSN_Quality\":\"1\"}";

// try above with bad last name/first name/date of birth (non-matching).  still matches on ssn, returns [3,4], because there are two people with that SSN
//	final String requestJson = "{\"First_Name\":\"Zmyrna\",\"First_Name_Alias\":\"\",\"Last_Name\":"
//		+ "\"Neptune\",\"Last_Name_Alias\":\"\",\"Middle_Initial\":\"\",\"Date_Of_Birth\":"
//		+ "\"1999-04-30\",\"Gender\":\"\",\"SSN\":\"111111111\",\"SSN_Quality\":\"1\"}";
						
			//final String requestJson = "{\"First_Name\":\"Hester\",\"Last_Name\":\"\"}";
//			final String requestJson = "{\"First_Name\":\"Hester\",\"Last_Name\":\"Heston\"}";
			//final String requestJson = {"First_Name?":"Hester","Last_Name?":""};
			//final String requestJson = "{\"First_Name\":\"Biff\",\"Last_Name\":\"Sagey\"}";
			//final String requestJson = "{\"First_Name\":\"Biff\",\"First_Name_Alias\":\"\",\"Last_Name\":" +
				//"\"Sagey\",\"Last_Name_Alias\":\"\",\"Middle_Initial\":\"\",\"Date_Of_Birth\":\"1999-04-30\",\"SSN_Quality\":\"1\",\"SSN\":\"111-11-1111\"}";
					
			byte[] unpwBytes = (un + ":" +  pw).getBytes();
			byte[] unpwEnc = Base64.encodeBase64(unpwBytes);
			String unpwEncStr = new String(unpwEnc);
			connection.setRequestProperty("Authorization", "Basic " + unpwEncStr);
			//connection.setRequestProperty(key, value)
		    connection.setUseCaches (false);
		    connection.setDoInput(true);
		    connection.setDoOutput(true);
		    final OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
		    osw.write(requestJson);
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
	        System.out.println("Response is: " + response.toString());
	        r  = response.toString();
	        //System.out.println("We expect: [\"4\"]"); 
         } catch (Exception e) {
		 	System.out.println("Error: in catch block"); e.printStackTrace();
         } 
		r = r.replace("[" , "").replace("\"", "").replace("]" , "").replace("\r", "").replace(",", "");
		System.out.println("r cleansed is: " + r + ".");
		String expected = "4";
		System.out.println("expected is: " + expected  + ".");
		boolean b = expected.equals(r);
		System.out.println("b is: " + b + ".");
		assertTrue("Do they match?", b);
	}
}
