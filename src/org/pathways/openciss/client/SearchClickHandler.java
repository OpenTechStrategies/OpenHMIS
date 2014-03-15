package org.pathways.openciss.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

class SearchClickHandler implements ClickHandler, KeyUpHandler {
	/**
	 * Fired when the user clicks on the sendButton.
	 */
	public void onClick(ClickEvent event) {
		//ft.clear();
		sendNamesToServer();
	}

	/**
	 * Fired when the user types in the firstNameField.
	 * @param sendButton 
	 */
	public void onKeyUp(KeyUpEvent event, FocusWidget sendButton) {
		if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			sendNamesToServer();
		}
	}

	
	
	/**
	 * Send the first, last name to the server and wait for a response.
	 * @param sendButton 
	 * @param lastNameSuggestBox 
	 * @param firstNameSuggestBox 
	 */
	private void sendNamesToServer() {
		// send the input to the server.
		SearchDemo.sendButton.setEnabled(false);
		final String GET_RECORDS_URL = "clientrecord"; 
		String uRL = GET_RECORDS_URL + "?firstname=" + SearchDemo.firstNameSuggestBox.getText() + "&lastname=" + SearchDemo.lastNameSuggestBox.getText();
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
						    														//System.out.println("Got a response from clientrecords:");
						    														//System.out	.println(response.getText());
						    														FlexTable ft = new FlexTable();
						    														RootPanel.get("resultsContainer").add(ft);
						    														parseJSON(response, ft);
						    														SearchDemo.sendButton.setEnabled(true);
						    													} else {
						    														System.out.println("Couldn't retrieve JSON (" + response.getStatusText() + ")");
						    													}
						    												}
																		});
	    } catch (RequestException e) {
	      System.out.println("Couldn't retrieve JSON");
	    }
	}
	
	/*
	 * Takes in a trusted JSON String and evals it.
	 * @param JSON String that you trust
	 * @return JavaScriptObject that you can cast to an Overlay Type
	 */
//	public static native JavaScriptObject parseJson(String jsonStr) /*-{
//	  return eval(jsonStr);}-*/;
	
	void parseJSON(Response response, FlexTable f) {
		System.out.println("Got some search record results JSON");
		JSONValue val = JSONParser.parseStrict(response.getText());
		JSONObject ob = val.isObject();
		JSONValue value = null;
		value = ob.get("records");		
		JSONArray a = value.isArray();
		final int HEADER_ROW_INDEX = 0;

		f.insertRow(HEADER_ROW_INDEX);
		f.setWidget(HEADER_ROW_INDEX, 0, new Label("First Name"));
		f.setWidget(HEADER_ROW_INDEX, 1, new Label("Last Name"));
		f.setWidget(HEADER_ROW_INDEX, 2, new Label("Date Of Birth"));
		f.setWidget(HEADER_ROW_INDEX, 3, new Label("Gender"));
		
		int rowIndex = 1;
			for (int i=0; i<a.size(); i++) {
				int cellIndex = 0;
			String firstName = a.get(i).isObject().get("FirstName").toString().replaceAll("^\"|\"$", "");
			System.out.println("got: " + firstName);
			Label widget = new Label(firstName);
		    f.setWidget(rowIndex, cellIndex++, widget);
			String lastName = a.get(i).isObject().get("LastName").toString().replaceAll("^\"|\"$", "");
			//System.out.println("got: " + lastName);
			Label widget2 = new Label(lastName);
			f.setWidget(rowIndex, cellIndex++, widget2);
			String dOB = a.get(i).isObject().get("DOB").toString().replaceAll("^\"|\"$", "").substring(0, 9);
			System.out.println("got: " + dOB);
			Label widget3 = new Label(dOB);
			f.setWidget(rowIndex, cellIndex++, widget3);
			String gender = a.get(i).isObject().get("Gender").toString().replaceAll("^\"|\"$", "");
			switch (Integer.parseInt(gender)) {
				case 1: gender = "male"; break;
				case 2: gender = "female"; break;
				default: gender = "unmapped";
			}
			System.out.println("got: " + gender);
			Label widget4 = new Label(gender);
			f.setWidget(rowIndex, cellIndex++, widget4);
            rowIndex++;
			//f.add(a.get(i).get("FirstName"));
			//System.out.println("adding: " + a.get(i));
		}
		f.getRowFormatter().addStyleName(HEADER_ROW_INDEX, "headerRow");
	}

	@Override
	public void onKeyUp(KeyUpEvent event) {		
	}
}