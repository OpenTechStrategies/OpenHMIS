package org.pathways.openciss.server;

import java.io.IOException;
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

//import org.mortbay.log.Log;
//import org.codehaus.jackson.JsonNode;
//import org.codehaus.jackson.map.ObjectMapper;
//import org.codehaus.jackson.node.ArrayNode;
import org.pathways.openciss.shared.HUDXML3Blob;
import org.pathways.openciss.shared.HUDXML3BlobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.pathways.openciss.shared.HUDXML3BlobService;
//commented out, because now we are using the backend to do this processing, which involves large files, and causes hard dealine exceeded errors
//import org.pathways.openciss.server.RosieCSVBlobService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.backends.BackendServiceFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions.Method;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.*;
//import com.google.appengine.api.backends.*;



//The FormPanel must submit to a servlet that extends HttpServlet RemoteServiceServlet cannot be used  (taken from http://www.fishbonecloud.com/2010/12/tutorial-gwt-application-for-storing.html)
@SuppressWarnings("serial")
public class HUDXML3UploadService extends HttpServlet {
	private static final transient Logger log = LoggerFactory.getLogger(RosieCSVBlobServlet.class);

  //"Override the doPost method to store the Blob's meta-data " 
  public void doPost(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
	  System.out.println("entered doPost of HUDXML3UploadService");
  	  BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
      //Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
	  Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
	  for (Map.Entry<String,  List<BlobKey>> entry : blobs.entrySet())
	  {
	      System.out.println(entry.getKey() + "/" + entry.getValue());
	  }

	  List<BlobKey> blobKeys = blobs.get("upload");

    //Get the parameters from the request to populate the HUDXML3Blob object
    HUDXML3Blob b = new HUDXML3Blob();
    b.setXmlUrl(blobKeys.get(0).getKeyString());
    log.info("setting HUDXML blob key to: " + blobKeys.get(0).getKeyString());
    // get the url id for the converted csv 
    //RosieCSVBlobService r = new RosieCSVBlobService();
    
    //start the conversion of the XML to CSV
    //String[] csvUrls = r.convertHUDXMLToRosieCSV(b.getXMLUrl());
    //use the backend servlet instead of a class
	//String json = "";
    //try{
		//URL url = new URL("http://rosie-conversion-backend-dot-open-ciss.appspot.com");
    	String rosieBackendAddress = BackendServiceFactory.getBackendService().getBackendAddress("big");
    	//String url = "http://" + rosieBackendAddress + "/rosie";
    	String charset = "UTF-8";
    	String hud_xml_url = b.getXmlUrl();
    	//String query = String.format("hud_xml_url=%s", URLEncoder.encode(hud_xml_url, charset));
    	//HttpURLConnection connection = (HttpURLConnection)(new URL(url + "?" + query).openConnection());
    	String key = "hud_xml_url";
    	String value = URLEncoder.encode(hud_xml_url, charset);
    	System.out.println("value is:" + value);
    	System.out.println("rosieBackendAddress is: " + rosieBackendAddress);
    	Queue queue = QueueFactory.getDefaultQueue();
    	//queue the task and quickly return to the client
    	//they'll have to get the blob id in another call
    	queue.add(withUrl("/rosie").param(key, value).header("Host", rosieBackendAddress).method(Method.GET));
    	//connection.setRequestProperty("Accept-Charset", charset);
    	//connection.setRequestMethod("GET");
    	//connection.setConnectTimeout(0);
    	//connection.connect();
		//InputStream in = connection.getInputStream();
		//int ch;
	    //while ((ch = in.read()) != -1)
	    	//json = json + String.valueOf((char) ch);
	    //System.out.println("Response Message is: " + json);
	    //connection.disconnect();
	//} catch(IOException ex) {System.out.println("made it here: " + ex.getMessage());  ex.printStackTrace();}
    
    //read the returned JSON's urls
//  	ObjectMapper mapper = new ObjectMapper();
//  	JsonNode ob =null;
//  	try {
//		// unmarshall the JSON
//		ob = mapper.readValue(json, ArrayNode.class);
//	} catch (Exception e) {e.printStackTrace();}
//    for (int i=0; i<5; i++) 
//	  	b.setCSVUrl1(ob.get(0).asText());
//	    b.setCSVUrl2(ob.get(1).asText());
//	    b.setCSVUrl3(ob.get(2).asText());
//	    b.setCSVUrl4(ob.get(3).asText());
//	    b.setCSVUrl6(ob.get(4).asText());
//
    HUDXML3BlobService hxbs = new HUDXML3BlobService();
    String blobRecordId = hxbs.createHUDXML3Blob(b);
    System.out.println("generated hud xml blob key is: " + blobRecordId);
//    //Map the HUDXML3BlobURL to the HUDXML3BlobService servlet, which will serve the HUDXML3Blob
//    //b.setXMLUrl("/blobstoreexample/blobservice?blob-key=" + blobKeys.get(0).getKeyString());//.getKeyString());
//    System.out.println("b blobRecordId is: " + b.getHUDXML3BlobRecordId());
//    System.out.println("CSV1 URL is:" + b.csvUrl1);
//    System.out.println("CSV2 URL is:" + b.csvUrl2);
//    System.out.println("CSV3 URL is:" + b.csvUrl3);
//    System.out.println("CSV4 URL is:" + b.csvUrl4);
//    System.out.println("CSV6 URL is:" + b.csvUrl6);
//    System.out.println("XML URL is:" + b.xmlUrl);
//    //Redirect recursively to this servlet (calls doGet)
//    res.sendRedirect("/hudxml3upload/uploadservice?id=" + b.getHUDXML3BlobRecordId());
    //res.getOutputStream().println(b.getHUDXML3BlobRecordId());
    
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
	System.out.println("doGet in HUDXML3UploadService entered");
    //Send the meta-data id back to the client in the HttpServletResponse response
    String id = req.getParameter("id");
    System.out.println("Parameter id received and to be sent is: " + id);
    resp.setHeader("Content-Type", "text/html");
    //PrintWriter w = resp.getWriter();
    //w.println(id);
    resp.getOutputStream().println(id);
  }
}
