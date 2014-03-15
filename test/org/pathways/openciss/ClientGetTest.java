package org.pathways.openciss;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
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

public class ClientGetTest {

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
	public void testClientGet() {		
		//String url = "http://localhost:8888/client/get?id=89";
		String url = "https://open-ciss.appspot.com/client/get?id=25";
		String charset = "UTF-8";
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept-Charset", charset);
			connection.setRequestProperty("Content-Type", "application/json");
			

			String un = "username_here";
			String pw = "password_here";

			byte[] unpwBytes = (un + ":" +  pw).getBytes();
			byte[] unpwEnc = Base64.encodeBase64(unpwBytes);
			String unpwEncStr = new String(unpwEnc);
			connection.setRequestProperty("Authorization", "Basic " + unpwEncStr);
			//connection.setRequestProperty(key, value)
		    connection.setUseCaches (false);
		    connection.setDoInput(true);
		    connection.setDoOutput(true);
		    //final OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
		    //osw.write(requestJson);
		    //osw.close();
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
	        System.out.println("response is: " + response.toString());
         	// need to make this so it checks for a return code of 200 and some json, empty or not
	        //if (response.) {
         		b = true;
         	//}
         } catch (Exception e) {
        	 b = false;
		 	System.out.println("in catch block");
         }
		assertTrue("Result", b);
	}
}
