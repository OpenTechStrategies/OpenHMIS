package org.pathways.openciss;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.apache.commons.codec.binary.Base64;

public class AuthenticationTest {

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
	public void testAuthentication() {		
		String url = "http://localhost:8888/client/get?id=4";
		//String url = "http://open-ciss.appspot.com/client/get?id=4";
		String charset = "UTF-8";
		try {
	    	URLConnection connection = new URL(url).openConnection();
			connection.setRequestProperty("Accept-Charset", charset);
			String un = "testuserdynamic";
			String pw = "doesthiswork";
			byte[] unpwBytes = (un + ":" +  pw).getBytes();
			byte[] unpwEnc = Base64.encodeBase64(unpwBytes);
			String unpwEncStr = new String(unpwEnc);
			connection.setRequestProperty("Authorization", "Basic " + unpwEncStr);
			InputStream response = connection.getInputStream();	
			System.out.println(response.toString());
         	b = true;
         } catch (Exception e) {
        	 b = false;
		 	System.out.println("in catch block");
         }
		assertTrue("Result", b);
	}
	
	@Test
	public void testAuthentication2() {		
		String url = "http://localhost:8888/client/get?id=4";
		//String url = "http://open-ciss.appspot.com/client/get?id=4";

    	String charset = "UTF-8";
    	//System.out.println(Base64.class.getProtectionDomain().getCodeSource().getLocation());
		try {
	    	URLConnection connection = new URL(url).openConnection();
			connection.setRequestProperty("Accept-Charset", charset);
			String un = "testuserdynamic";
			String pw = "wrongpassword";
			byte[] unpwBytes = (un + ":" +  pw).getBytes();
			byte[] unpwEnc = Base64.encodeBase64(unpwBytes);
			String unpwEncStr = new String(unpwEnc);
			connection.setRequestProperty("Authorization", "Basic " + unpwEncStr);
			//connection.setRequestProperty("Authorization", "Basic dGVzdHVzZXJkeW5hbWljOmRvZXN0aGlzd29yaw==");//+ codec.encode(un + ":" +  pw));
			InputStream response = connection.getInputStream();
			//			InputStream content = (InputStream)uc.getInputStream();
			//		    BufferedReader in = new BufferedReader (new InputStreamReader
			//		(content));
			//		    String line;
			//		    while ((line = in.readLine()) != null) {
			//		     retVal += line;
			//		    }
			//		   } catch (Exception e) {
			//		    return "";
			//		   }
			//		  } catch (MalformedURLException e) {
			//		   return(URL + " is not a parseable URL");
			//		  }
			System.out.println(response.toString());
         	b = true;
         } catch (Exception e) {
        	b = false;
		 	System.out.println("in catch block");
         }
		assertFalse("Result", b);
	}
}
