package org.pathways.openciss.rest.impl;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.google.appengine.api.rdbms.AppEngineDriver;

@Path("/clientrecord")
public class GetClientRecord {
	 
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	 public String getRecord(@QueryParam("firstname") String firstName, @QueryParam("lastname") String lastName) {
		// This is being replaced by /client/get
		// Gets a record given a combination of possible parameters and returns 
		// one or more matching JSON records
		
		// start with just retrieving based on combination of first and last name
				
		AppEngineDriver a = new AppEngineDriver();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.createObjectNode(); // will be of type ObjectNode
		ArrayNode records = ((ObjectNode)rootNode).putArray("records");
	
		try {
			Connection conn = null;
			DriverManager.registerDriver(a);
			conn = DriverManager.getConnection("jdbc:google:rdbms://pcni.org:openhmis:openciss/compass");
			Statement st = conn.createStatement();
			System.out.println();
			ResultSet rs = st.executeQuery("SELECT name_first, name_last, date_of_birth, gender_code FROM compass.path_client WHERE name_first='"+firstName+"' AND name_last='"+lastName+"';");
			int i = 0;
			while (rs.next()) {
				System.out.println("got record: " + rs.toString());
				System.out.println(rs.getString("name_first") + "," + rs.getString("date_of_birth"));				
				
				JsonNode recordNode = mapper.createObjectNode(); // will be of type ObjectNode
				((ObjectNode)recordNode).put("FirstName", rs.getString("name_first"));
				((ObjectNode)recordNode).put("LastName", rs.getString("name_last"));
				((ObjectNode)recordNode).put("DOB", rs.getString("date_of_birth"));
				((ObjectNode)recordNode).put("Gender", rs.getString("gender_code"));
				records.insert(i++, recordNode);
			}
		
		} catch (SQLException e) {e.printStackTrace();}
		
		return rootNode.toString();
	 }
}