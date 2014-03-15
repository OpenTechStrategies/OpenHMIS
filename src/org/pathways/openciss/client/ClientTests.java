package org.pathways.openciss.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ClientTests implements EntryPoint {
	Button b;
	Button b1;
	Button b2;
	Button b3;
	Button b4;
	TextBox untb;
	PasswordTextBox ptb;
	
	@Override
	public void onModuleLoad() {
		System.out.println("ClientTests onModuleLoad Happening");
		b = new Button("Send Add XML", new SendXMLAddClickHandler());
		b1 = new Button("Send Search JSON", new SendSearchJSONClickHandler());
		b2 = new Button("Send Update XML", new SendXMLUpdateClickHandler());
		b3 = new Button("Send Add Outreach JSON", new SendOutreachJSONUpdateClickHandler());
		b4 = new Button("Log Into SafeResource", new LoginSafeResourceClickHandler());
	    untb = new TextBox();
	    ptb = new PasswordTextBox();
		
		// Add it to the root panel.
	    RootPanel.get().add(b);
	    RootPanel.get().add(b1);
	    RootPanel.get().add(b2);
	    RootPanel.get().add(b3);
	    RootPanel.get().add(untb);
	    RootPanel.get().add(ptb);
	    RootPanel.get().add(b4);

	    //b.setSize("100", "100");
	}
	
	public class LoginSafeResourceClickHandler implements ClickHandler {

		public void onClick(ClickEvent event) {
			sendCredentialsToWebService();
		}
		private void sendCredentialsToWebService() {
			b4.setEnabled(false);
			final String url = "safe"; 
			String pw = ptb.getText(); 
			String un = untb.getText();
			
			RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
			builder.setUser(un);
			builder.setPassword(pw);
			
			System.out.println("sending to " + url);
			try {
				builder.sendRequest(
					null, new RequestCallback() {
			    	
							    												public void onError(Request request, Throwable exception) {
							    													System.out.println("Error: Couldn't send credentials");
							    													System.out.println("Exception is: " + exception);
							    												}
							    												public void onResponseReceived(Request request, Response response) {
							    													if (200 == response.getStatusCode()) {
							    														System.out.println("Got a response from safe:" + response.getStatusText());
							    														
							    													} else {
							    														System.out.println("Couldn't send credentials (" + response.getStatusText() + ")");
							    													}
							    												}
																			});
		    } catch (RequestException e) {
		      System.out.println("Couldn't send credentials");
		    }
		}
	}
	
	class SendXMLAddClickHandler implements ClickHandler, KeyUpHandler {
		String long_xml = "<?xml version='1.0' encoding='UTF-8'?>\n\n" + "" +
				"<hmis:Person xmlns:hmis='http://www.hmis.info/schema/3_0/HUD_HMIS.xsd'><hmis:PersonID>" +
				"<hmis:IDNum>2090888539</hmis:IDNum></hmis:PersonID><hmis:DateOfBirth>" +
				"<hmis:Unhashed hmis:dateCollected='2011-05-27T18:51:58' hmis:dataCollectionStage='2'>2010-04-21</hmis:Unhashed>" +
				"</hmis:DateOfBirth>" +
				"<hmis:Ethnicity><hmis:Unhashed hmis:dateCollected='2011-06-25T18:51:58' hmis:dataCollectionStage='2'>8</hmis:Unhashed>" +
				"</hmis:Ethnicity><hmis:Gender><hmis:Unhashed hmis:dateCollected='2010-06-05T18:51:58' hmis:dateEffective='2011-05-27T18:51:58' hmis:dataCollectionStage='1'>2</hmis:Unhashed>" +
				"</hmis:Gender><hmis:LegalFirstName><hmis:Unhashed hmis:dateCollected='2011-01-27T18:51:58' hmis:dateEffective='2010-10-06T18:51:58' hmis:dataCollectionStage='3'>Biff</hmis:Unhashed>" +
				"</hmis:LegalFirstName><hmis:LegalLastName><hmis:Unhashed hmis:dateCollected='2011-03-05T18:51:58' hmis:dateEffective='2010-09-23T18:51:58' hmis:dataCollectionStage='2'>Jones</hmis:Unhashed>" +
				"</hmis:LegalLastName><hmis:LegalMiddleName><hmis:Unhashed hmis:dateCollected='2011-10-03T18:51:58' hmis:dateEffective='2010-12-24T18:51:58' hmis:dataCollectionStage='3'>C</hmis:Unhashed>" +
				"</hmis:LegalMiddleName></hmis:Person>";
		
		String short_xml = "<?xml version='1.0' encoding='UTF-8'?>\n\n" + "<hmis:Person xmlns:hmis='http://www.hmis.info/schema/3_0/HUD_HMIS.xsd'><hmis:PersonID><hmis:IDNum>2090888539</hmis:IDNum></hmis:PersonID><hmis:DateOfBirth><hmis:Unhashed hmis:dateCollected='2011-05-27T18:51:58' hmis:dataCollectionStage='2'>2010-04-21</hmis:Unhashed></hmis:DateOfBirth></hmis:Person>";

		
		/**
		 * Fired when the user clicks on the sendButton.
		 */
		public void onClick(ClickEvent event) {
			//ft.clear();
			sendXMLToServer();
		}

		/**
		 * Send the Person XML to the server and wait for a response.
		 * @param sendButton 
		 */
		private void sendXMLToServer() {
			RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, "./client/add");
			rb.setHeader("Content-Type", "application/xml");
			try {
				rb.sendRequest(long_xml,    
						new RequestCallback() { 
					
						public void onError(Request request, Throwable exception) {
							System.out.println("Error: Couldn't retrieve Response");
						}
						public void onResponseReceived(Request request, Response response) {
							if (200 == response.getStatusCode()) {
								System.out.println("Got a response from client/add:");
								System.out.println(response.getText());
							} else {
								System.out.println("Status code: " + response.getStatusCode() + " " + response.getStatusText());
							}
						}
				});
			} catch (RequestException e) {
				e.printStackTrace();
			} 
		}

		@Override
		public void onKeyUp(KeyUpEvent event) {
		}
	}
	class SendSearchJSONClickHandler implements ClickHandler, KeyUpHandler {
		String json = "{\"First_Name\":\"Hester\",\"First_Name_Alias\":\"\",\"Last_Name\":" +
				"\"Heston\",\"Last_Name_Alias\":\"Gorda\",\"Middle_Initial\":\"B\",\"Date_Of_Birth\":" +
				"\"2001-09-04\",\"Gender\":\"2\",\"SSN\":\"111-11-1111\",\"SSN_Quality\":\"1\"}";
		
		/**
		 * Fired when the user clicks on the sendButton.
		 */
		public void onClick(ClickEvent event) {
			//ft.clear();
			sendJSONToServer();
		}

		/**
		 * Send the search JSON to the server and wait for a response.
		 * @param sendButton 
		 */
		private void sendJSONToServer() {
			RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, "./client/search");
			rb.setHeader("Content-Type", "application/json");
			try {
				rb.sendRequest(json,    
						new RequestCallback() { 
					
						public void onError(Request request, Throwable exception) {
							System.out.println("Error: Couldn't retrieve Response");
						}
						public void onResponseReceived(Request request, Response response) {
							if (200 == response.getStatusCode()) {
								System.out.println("Got a response from client/search:");
								System.out.println(response.getText());
							} else {
								System.out.println("Status code: " + response.getStatusCode() + " " + response.getStatusText());
							}
						}
				});
			} catch (RequestException e) {
				e.printStackTrace();
			} 
		}

		@Override
		public void onKeyUp(KeyUpEvent event) {
		}
	}
	
	class SendXMLUpdateClickHandler implements ClickHandler, KeyUpHandler {
		String long_xml = "<?xml version='1.0' encoding='UTF-8'?>\n\n" + "" +
				"<hmis:Person xmlns:hmis='http://www.hmis.info/schema/3_0/HUD_HMIS.xsd'><hmis:PersonID>" +
				"<hmis:IDNum>2090888539</hmis:IDNum></hmis:PersonID><hmis:LegalFirstName><hmis:Unhashed hmis:dateCollected='2011-01-27T18:51:58' hmis:dateEffective='2010-10-06T18:51:58' hmis:dataCollectionStage='3'>Bjarney</hmis:Unhashed>" +
				"</hmis:LegalFirstName></hmis:Person>";
		
		String short_xml = "<?xml version='1.0' encoding='UTF-8'?>\n\n" + "<hmis:Person xmlns:hmis='http://www.hmis.info/schema/3_0/HUD_HMIS.xsd'><hmis:PersonID><hmis:IDNum>2090888539</hmis:IDNum></hmis:PersonID><hmis:DateOfBirth><hmis:Unhashed hmis:dateCollected='2011-05-27T18:51:58' hmis:dataCollectionStage='2'>2010-04-21</hmis:Unhashed></hmis:DateOfBirth></hmis:Person>";

		
		/**
		 * Fired when the user clicks on the sendButton.
		 */
		public void onClick(ClickEvent event) {
			//ft.clear();
			sendXMLToServer();
		}

		/**
		 * Send the Person XML to the server and wait for a response.
		 * @param sendButton 
		 */
		private void sendXMLToServer() {
			RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, "./client/update?id=1");
			rb.setHeader("Content-Type", "application/xml");
			try {
				rb.sendRequest(long_xml,    
						new RequestCallback() { 
					
						public void onError(Request request, Throwable exception) {
							System.out.println("Error: Couldn't retrieve Response");
						}
						public void onResponseReceived(Request request, Response response) {
							if (200 == response.getStatusCode()) {
								System.out.println("Got a response from client/update:");
								System.out.println(response.getText());
							} else {
								System.out.println("Status code: " + response.getStatusCode() + " " + response.getStatusText());
							}
						}
				});
			} catch (RequestException e) {
				e.printStackTrace();
			} 
		}

		@Override
		public void onKeyUp(KeyUpEvent event) {
		}
	}
	class SendOutreachJSONUpdateClickHandler implements ClickHandler, KeyUpHandler {
		String json = "{\"Client_ID\":\"2\",\"Latitude\":\"-1.4432\",\"Longitude\":\"104.81123\",\"Contact_Date\":\"2007-11-03T16:18:05GMT\",\"Engagement_Date\":\"2012-08-00T11:01:59GMT\"}";
		
		/**
		 * Fired when the user clicks on the sendButton.
		 */
		public void onClick(ClickEvent event) {
			//ft.clear();
			sendJSONToServer();
		}

		/**
		 * Send the search JSON to the server and wait for a response.
		 * @param sendButton 
		 */
		private void sendJSONToServer() {
			RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, "./client/outreach/add");
			rb.setHeader("Content-Type", "application/json");
			try {
				rb.sendRequest(json,    
						new RequestCallback() { 
					
						public void onError(Request request, Throwable exception) {
							System.out.println("Error: Couldn't retrieve Response");
						}
						public void onResponseReceived(Request request, Response response) {
							if (200 == response.getStatusCode()) {
								System.out.println("Got a response from client/outreach/add:");
								System.out.println(response.getText());
							} else {
								System.out.println("Status code: " + response.getStatusCode() + " " + response.getStatusText());
							}
						}
				});
			} catch (RequestException e) {
				e.printStackTrace();
			} 
		}
		@Override
		public void onKeyUp(KeyUpEvent event) {
		}
	}

}