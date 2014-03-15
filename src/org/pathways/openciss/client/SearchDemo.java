package org.pathways.openciss.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SearchDemo implements EntryPoint {
	static SuggestBox firstNameSuggestBox;
	static SuggestBox lastNameSuggestBox;
	static MultiWordSuggestOracle oracle1 = new MultiWordSuggestOracle();
	static MultiWordSuggestOracle oracle2 = new MultiWordSuggestOracle();
	static Button sendButton = new Button("Search");
		
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		System.out.println("SearchDemo onModuleLoad Happening");
	    firstNameSuggestBox = new SuggestBox(oracle1); 
	    lastNameSuggestBox = new SuggestBox(oracle2); 

		// Add the firstNameField and sendButton to the RootPanel
		// Use RootPanel.get() to get the entire body element
		RootPanel.get("firstNameFieldContainer").add(firstNameSuggestBox);
		RootPanel.get("lastNameFieldContainer").add(lastNameSuggestBox);
		RootPanel.get("sendButtonContainer").add(sendButton);
		//Label spacer = new Label(".");
		//RootPanel.get("existsContainer").add(spacer);
		//spacer.setVisible(true);
		
		// Focus the cursor on the name field when the app loads
		firstNameSuggestBox.setFocus(true);
		firstNameSuggestBox.addSelectionHandler(new SuggestSelectedEventHandler());
		lastNameSuggestBox.addSelectionHandler(new SuggestSelectedEventHandler());
		firstNameSuggestBox.addKeyPressHandler(new TrieFetcher(oracle1, "name_first"));
		lastNameSuggestBox.addKeyPressHandler(new TrieFetcher(oracle2, "name_last"));
		
	    /**
	     * If can't get JSON, display error message.
	     * @param error
	     */
		
		// Add a handler to send the name to the server
		SearchClickHandler handler = new SearchClickHandler();
		sendButton.addClickHandler(handler);
	}
	
	class SuggestSelectedEventHandler implements SelectionHandler<SuggestOracle.Suggestion> {
		public void onSelection(SelectionEvent<SuggestOracle.Suggestion> event)  {
			checkIfNameExists();
		}
		/**
		 * Send the first, last name to the server and wait for an existence  response.
		 */
		private void checkIfNameExists() {
			// send the input to the server.
			//sendButton.setEnabled(true);
			final String GET_RECORDS_URL = "existence/search"; 
			String uRL = GET_RECORDS_URL + "?firstname=" + firstNameSuggestBox.getText() + "&lastname=" + lastNameSuggestBox.getText();
			RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, uRL);
		    System.out.println("sending to " + uRL);
			try {
				builder.sendRequest(
					null, new RequestCallback() {
			    	
						public void onError(Request request, Throwable exception) {
							System.out.println("Error: Couldn't retrieve JSON");
						}
						public void onResponseReceived(Request request, Response response) {
							if (200 == response.getStatusCode()) {
								//System.out.println("Got a response from existence/search:");
								//System.out	.println(response.getText());
								
								//RootPanel.get("resultsContainer").add(ft);
								parseBloomJSON(response);
							} else {
								System.out.println("Couldn't retrieve JSON (" + response.getStatusText() + ")");
							}
						}
					});
		    } catch (RequestException e) {
		      System.out.println("Couldn't retrieve JSON");
		    }
		}
		
		void parseBloomJSON(Response response) {
			JSONValue val = JSONParser.parseStrict(response.getText());
			JSONObject ob = val.isObject();
			JSONValue value = null;
			value = ob.get("Exists");
			if (value.isBoolean().booleanValue()) {
				//RootPanel.get("existsContainer").clear();
				//RootPanel.get("existsContainer").add(new Label("(name exists)"));
				sendButton.setEnabled(true);
				sendButton.setText("found matches, go get them");
				//RootPanel.get("existsContainer").setVisible(true);

			} else {
				//RootPanel.get("existsContainer").clear();
				sendButton.setEnabled(true);
				sendButton.setText("not found in quick lookup, but try anyway");
				//RootPanel.get("existsContainer").add(new Label("(name does not exist)"));
				//RootPanel.get("existsContainer").setVisible(true);
			}}
			
		}
}