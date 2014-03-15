package org.pathways.openciss.server;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.pathways.openciss.shared.HUDXML3BlobService;
import org.pathways.openciss.shared.HUDXML3Blob;
import org.pathways.openciss.client.BlobService;

import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/*Flow of events: 
	1) client requests an upload url; upload url returned  [getBlobStoreUploadUrl()]
	2) client uploads file using url, and gets back an id (url key) part that maps the uploaded file 
			to the converted file (HUDXML3Blob represents this DB record)
	3) client submits request containing this id to download converted file*/

@SuppressWarnings("serial")
public class HUDXML3BlobServiceImpl extends RemoteServiceServlet implements
    BlobService {

  //Start a GAE BlobstoreService session and JPA session
  BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

  //Generate a Blobstore Upload URL from the GAE BlobstoreService
  public String getBlobStoreUploadUrl() {

    //Map the UploadURL to the uploadservice which will be called by the client
    //submitting from the FormPanel
	// Change the machine name to your machine name to avoid google chrome cross-site scripting blocking, 
	//    when running in dev mode (won't affect production mode) 
     String url = blobstoreService.createUploadUrl("/hudxml3upload/uploadservice").replace("ericasus", "127.0.0.1");
     //commented the following out, since you can't run a System Service as a backend, to my knowledge
     //url = url.replace("://", "://small.");
    System.out.println("upload url being returned is: " + url);
    return url;
  }
  
  //Retrieve the Blob's meta-data from Cloud SQL using JPA
  public HUDXML3Blob getHUDXML3BlobMetadata(String id) {
    HUDXML3BlobService hxbs = new HUDXML3BlobService(); 
    System.out.println("client wants to get HUDXML3Blob with ID: " + id);
    HUDXML3Blob b = hxbs.getHUDXML3Blob(id);
    return b;
  }
    
  //Override doGet to serve blobs to client.
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
	  	System.out.println("Received a request to serve a blob with the id: " + req.getParameter("id")
	  			+ ", and file: " + req.getParameter("file"));
	  	HUDXML3BlobService hxbs = new HUDXML3BlobService(); 
	    HUDXML3Blob b = hxbs.getHUDXML3Blob(req.getParameter("id"));
	    int requestedfileNumber = Integer.valueOf(req.getParameter("file"));
	    BlobKey blobKey;
	    switch (requestedfileNumber) {
        	case 1: blobKey = new BlobKey(b.getCsvUrl1());
        	break;
        	case 2: blobKey = new BlobKey(b.getCsvUrl2());
        	break;
        	case 3: blobKey = new BlobKey(b.getCsvUrl3());
        	break;
        	case 4: blobKey = new BlobKey(b.getCsvUrl4());
        	break;
        	case 6: blobKey = new BlobKey(b.getCsvUrl6());
        	break;
        	default: blobKey = new BlobKey(b.getCsvUrl1()); }
  	  	//BlobKey blobKey2 = new BlobKey("wWN4LMO8okJ-zlmkdQk8Bg");
	  	//System.out.println("overriding returned key:" + blobKey.getKeyString() + " with static key: " + blobKey2.getKeyString());
  	  	System.out.println("sending blob key: " + blobKey.getKeyString());
  	  	resp.setContentType("application/x-download");
  	  	BlobInfoFactory bif = new BlobInfoFactory();
  	  	String fileName = bif.loadBlobInfo(blobKey).getFilename();
  	  	resp.setHeader("Content-Disposition", "attachment; filename=" + fileName);
  	  	blobstoreService.serve(blobKey, resp);
  }
}