package org.pathways.openciss.rest.impl;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

//import java.util.logging.Level;
//import java.util.logging.Logger;

import com.google.appengine.api.rdbms.AppEngineDriver;


@Path("/shelterList")
public class GetShelterList {
	
	//private static final Logger log = Logger.getLogger("log1");

	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	 public String getShelterList() {
		System.out.println("inside /shelterList method");
		//log.setLevel(Level.ALL);
		//log.info("testinfo");
		//log.warning("testwarning");
		AppEngineDriver a = new AppEngineDriver();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.createObjectNode(); // will be of type ObjectNode
		ArrayNode records = ((ObjectNode)rootNode).putArray("Shelters");
		try {
			Connection conn = null;
			DriverManager.registerDriver(a);
			conn = DriverManager.getConnection("jdbc:google:rdbms://pcni.org:openhmis:openciss/compass");
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT program_key, program_name FROM compass.program_profile_info WHERE program_type_code='1';");
			int i = 0;
			while (rs.next()) {
				//System.out.println(rs.getString("program_key") + "," + rs.getString("program_name"));				
				JsonNode recordNode = mapper.createObjectNode(); // will be of type ObjectNode
				((ObjectNode)recordNode).put("ProgramKey", rs.getString("program_key"));
				((ObjectNode)recordNode).put("ProgramName", rs.getString("program_name"));
				records.insert(i++, recordNode);
			}
		
		} catch (SQLException e) {e.printStackTrace();}
		
		return rootNode.toString();
	 }
}
