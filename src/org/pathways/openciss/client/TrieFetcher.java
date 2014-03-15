package org.pathways.openciss.client;

import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;

//import com.google.gwt.user.client.ui.RootPanel;

public class TrieFetcher implements KeyPressHandler {
	MultiWordSuggestOracle oracle;
	String type;
	String SEARCH_URL;
	
	TrieFetcher(MultiWordSuggestOracle oracle, String type) {
		this.oracle= oracle;
		this.type = type;
	}
	
	@Override
	public void onKeyPress(KeyPressEvent event) {
		System.out.println("Key pressed: " + event.getCharCode());
		System.out.println("Total String Received in " + type + " " + ((SuggestBox)(event.getSource())).getText());
		
		try {
			//request last names from rest server
			if (type == "name_first") {
				SEARCH_URL = "search/firstname";
			} 
			else if (type == "name_last") {
				SEARCH_URL = "search/lastname"; 
			}
			
			String prefixURL = SEARCH_URL + "/" + ((SuggestBox)event.getSource()).getText();

			// get substring to send
			// Send request to server and catch any errors.
		    RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, prefixURL);
		    System.out.println("sending to " + prefixURL);
		    builder.sendRequest(
		    		null, new RequestCallback() {
			    	
		    		public void onError(Request request, Throwable exception) {
			    		 displayError("Couldn't retrieve JSON");
			    	}
	
			        public void onResponseReceived(Request request, Response response) {
			          if (200 == response.getStatusCode()) {
			        	  parseJSON(response, oracle);
			        	  //System.out.println("Need to send a response to 'onKeyPress' here");
			          } else {
			            displayError("Couldn't retrieve JSON (" + response.getStatusText()
			                + ")");
			          }
			        }
			      });
		    } catch (RequestException e) {
		      displayError("Couldn't retrieve JSON");
		    }

	}
    void displayError(String error) {
	      errorMsgLabel.setText("Error: " + error);
	      errorMsgLabel.setVisible(true);
	}
    
    private Label errorMsgLabel = new Label();
    
	void parseJSON(Response response, MultiWordSuggestOracle o) {
		//JavaScriptObject jso = parseJson(responseJSON);
		System.out.println("Got some JSON");
		JSONValue val = JSONParser.parseStrict(response.getText());
		JSONObject ob = val.isObject();
		JSONValue value = null;
		
		if (type == "name_first") {
			value = ob.get("firstName");
		} 
		else if (type == "name_last") {
			value = ob.get("lastName");
		}
		
		JSONArray a = value.isArray();
		//System.out.println("printing last name" + a.get(0));
		o.clear();
		for (int i=0; i<a.size(); i++) {
			o.add(a.get(i).isString().stringValue());
			//System.out.println("adding: " + a.get(i));
		}
	}
}