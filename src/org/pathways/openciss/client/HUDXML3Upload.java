package org.pathways.openciss.client;

import org.pathways.openciss.shared.HUDXML3Blob;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class HUDXML3Upload implements EntryPoint {
	Button submit;
	Button browse;
	String text;

	final FormPanel formPanel = new FormPanel(); 
	VerticalPanel mainPanel = new VerticalPanel();
	FileUpload fileUpload = new FileUpload();
    TextBox t = new TextBox();
	BlobServiceAsync blobService = GWT.create(BlobService.class);


	  //Start Blobstore Sessions
	  //BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

	
	@Override
	public void onModuleLoad() {
		System.out.println("HUDXML3Upload onModuleLoad Happening");
		
		// "You must use a FormPanel to create a blobstore upload form" (taken from http://www.fishbonecloud.com/2010/12/tutorial-gwt-application-for-storing.html)
		formPanel.setEncoding(FormPanel.ENCODING_MULTIPART); 
	    formPanel.setMethod(FormPanel.METHOD_POST); 
	    formPanel.add(mainPanel);
	    fileUpload.setName("upload");
	    mainPanel.add(fileUpload);
	    text = "uploading ... please wait";
	    t.setVisibleLength(text.length()+45);
	    t.setText(text);
	    t.setVisible(false);
    	mainPanel.add(t);
	    submit = new Button("Submit XML", new SendHUDXMLAddClickHandler());
	    mainPanel.add(submit);
	    formPanel.addSubmitCompleteHandler(new SubmitCompleteHandler());
	    RootPanel.get().add(formPanel);
	}
	
	class SendHUDXMLAddClickHandler implements ClickHandler {

		public void onClick(ClickEvent event) {
			// "Use an RPC call to the Blob Service to get the blobstore upload url" (taken from http://www.fishbonecloud.com/2010/12/tutorial-gwt-application-for-storing.html)
			
			t.setVisible(true);
			blobService.getBlobStoreUploadUrl(new AsyncCallback<String>() {
			
              @Override
              public void onSuccess(String result) {
                // Use the blobstore upload URL we just got, to send the XML
            	// The reponse is an database record index that can be used to retrieve the converted file
                t.setText("uploading to: " + result);
            	System.out.println("onSuccess: upload url from blobservice is: " + result.toString());
                //result = result.replace("://", "://small.");
                formPanel.setAction(result);
                formPanel.submit();
                System.out.println("calling formPanel.submit()");
            	//formPanel.remove(fileUpload);
                //formPanel.reset();      
              }

              @Override
              public void onFailure(Throwable error) {
            	System.out.println("onFailure to get url from blobservice");
                error.printStackTrace();
              }
            });
		}
	  }

	class SubmitCompleteHandler implements FormPanel.SubmitCompleteHandler {
        @Override
        public void onSubmitComplete(SubmitCompleteEvent event) {
        	t.setText("upload finished, now please get the converted files from the blob viewer");
        	//t.setText("upload finished, now getting links to the converted file");
        	System.out.println("onSubmitComplete");
        	//now that the blob is uploaded, get the converted CSV file back
        	//first, make another request to the RosieCSVServiceImpl using the event's id we got 
        	//   back upon uploading successfully.  Use the response to retrieve 
        	//    the converted CSV's Blobstore url (which contains a blobstore id) to make an html download link   	
        	        	
        	// so make an asynchronous handler to wait for a response from the server
        	System.out.println("Response from HUDXML3UploadService is: event.getResults().trim(): " + event.getResults().trim());
        	//commented this out, since nothing comes back now, since it's queued
        	//getHUDXML3MetadataClient(event.getResults().trim());
        }
	}
	
	public void getHUDXML3MetadataClient(String id) {

		    //Make another call to the Blob Service to retrieve the meta-data
    		// Since it may take a long time for the backend to create the links to the newly generated
			//    file, the call should only return the HUDXML3Blob object once all its CSV fields are populated
			//    in the database (conversion complete).  Maybe check for memcache value.

		    System.out.println("getting HUDXML3BlobMetadata for ID: " + id);
		    blobService.getHUDXML3BlobMetadata(id, new AsyncCallback<HUDXML3Blob>() {

		    @Override
		     public void onSuccess(HUDXML3Blob result) {
		     // pull the converted CSV file's id out of the metadata
		        System.out.println("onSuccess");
		        System.out.println("result: " + result.getId());
		        System.out.println("result: " + result.getCsvUrl1());
		        System.out.println("result: " + result.getCsvUrl2());
		        System.out.println("result: " + result.getCsvUrl3());
		        System.out.println("result: " + result.getCsvUrl4());
		        System.out.println("result: " + result.getCsvUrl6());
		        System.out.println("making a hyperlink to: /hudxml3upload/blobservice?id=" + result.getId() + "&file=1" );
		        Anchor anchor = new Anchor("Download Converted Rosie CSV File 1 - Family Information", "/hudxml3upload/blobservice?id=" + result.getId() + "&file=1");// + result.csvUrl);7gRiRjUxCyZGdhN8kwUwZg
	        	anchor.setVisible(true);
	        	anchor.setFocus(true);
	        	mainPanel.add(anchor);
		        
	        	System.out.println("making a hyperlink to: /hudxml3upload/blobservice?id=" + result.getId() + "&file=2" );
		        Anchor anchor2 = new Anchor("Download Converted Rosie CSV File 2 - Family Members", "/hudxml3upload/blobservice?id=" + result.getId() + "&file=2");// + result.csvUrl);7gRiRjUxCyZGdhN8kwUwZg
	        	anchor2.setVisible(true);
	        	anchor2.setFocus(false);
	        	mainPanel.add(anchor2);
	        	
	        	System.out.println("making a hyperlink to: /hudxml3upload/blobservice?id=" + result.getId() + "&file=3" );
		        Anchor anchor3 = new Anchor("Download Converted Rosie CSV File 3 - Family Income And Expense", "/hudxml3upload/blobservice?id=" + result.getId() + "&file=3");// + result.csvUrl);7gRiRjUxCyZGdhN8kwUwZg
	        	anchor3.setVisible(true);
	        	anchor3.setFocus(false);
	        	mainPanel.add(anchor3);
	        	
	        	System.out.println("making a hyperlink to: /hudxml3upload/blobservice?id=" + result.getId() + "&file=4" );
		        Anchor anchor4 = new Anchor("Download Converted Rosie CSV File 4 - Member Health Problems", "/hudxml3upload/blobservice?id=" + result.getId() + "&file=4");// + result.csvUrl);7gRiRjUxCyZGdhN8kwUwZg
	        	anchor4.setVisible(true);
	        	anchor4.setFocus(false);
	        	mainPanel.add(anchor4);
	        	
	        	System.out.println("making a hyperlink to: /hudxml3upload/blobservice?id=" + result.getId() + "&file=6" );
		        Anchor anchor6 = new Anchor("Download Converted Rosie CSV File 6 - Family Assistance Provided", "/hudxml3upload/blobservice?id=" + result.getId() + "&file=6");
	        	anchor6.setVisible(true);
	        	anchor6.setFocus(false);
	        	mainPanel.add(anchor6);
		     }

		      @Override
		      public void onFailure(Throwable caught) {
		        System.out.println("On Failure caught");
		    	  caught.printStackTrace();
		      }

		    });
	 }
}
