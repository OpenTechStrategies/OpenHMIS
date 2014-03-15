package org.pathways.openciss.rest.impl;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

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
import com.google.cloud.sql.jdbc.PreparedStatement;
//import java.util.Arrays;
//import com.sun.jersey.*;
//import java.util.logging.Logger;
//import java.io.UnsupportedEncodingException;
//import com.google.cloud.sql.jdbc.Statement;
//import java.sql.PreparedStatement;
//import java.sql.Statement;


@Path("/programInfoByID")
public class GetProgramInfoByID {
	String idStr;
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	 public String getRecord(@QueryParam("id") String id) {
//		final Logger log = Logger.getLogger("programinfo");
//		log.info("received from web service: " + id);
//		byte[] idUTF8Bytes = null;
//		try {
//			idUTF8Bytes = id.getBytes("UTF-8");
//		} catch (UnsupportedEncodingException e1) {
//			e1.printStackTrace();
//		}
//		try {
//			idStr = new String(idUTF8Bytes,"UTF8");
//		} catch (UnsupportedEncodingException e1) {
//			e1.printStackTrace();
//		}
		// Gets a record given id parameter and returns corresponding JSON records
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.createObjectNode(); // will be of type ObjectNode
		ArrayNode jSONrecords = ((ObjectNode)rootNode).putArray("ProgramRecord");
	
		try {
			Connection conn = null;
			DriverManager.registerDriver(new AppEngineDriver());
			conn = DriverManager.getConnection("jdbc:google:rdbms://pcni.org:openhmis:openciss/compass");
			String stmt = "SELECT program_key, program_name, agency_name, program_type, site_geocode, target_pop_a_name, update_time_stamp, units_total, units_available, units_occupied, contact_name, contact_phone, program_address, program_city, program_zip, program_address_full FROM compass.program_profile_info WHERE program_key=?";
			PreparedStatement pstmt = (PreparedStatement)conn.prepareStatement(stmt);
//			try {
//				System.out.println("value=[" + idStr + "]; bytes=" + Arrays.toString(idStr.getBytes("UTF-8")));
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//			}
			pstmt.setString(1, id);
			//pstmt.setString(1, "3459");
			pstmt.toString();
			ResultSet rs = pstmt.executeQuery();
			int i = 0;
			while (rs.next()) {
				JsonNode recordNode = mapper.createObjectNode(); // will be of type ObjectNode
				((ObjectNode)recordNode).put("ProgramKey", rs.getString("program_key"));
				((ObjectNode)recordNode).put("ProgramName", rs.getString("program_name"));
				((ObjectNode)recordNode).put("AgencyName", rs.getString("agency_name"));
				((ObjectNode)recordNode).put("ProgramType", rs.getString("program_type"));
				((ObjectNode)recordNode).put("SiteGeocode", rs.getString("site_geocode"));
				((ObjectNode)recordNode).put("TargetPopAName", rs.getString("target_pop_a_name"));
				((ObjectNode)recordNode).put("UpdateTimeStamp", rs.getString("update_time_stamp"));
				((ObjectNode)recordNode).put("UnitsTotal", rs.getString("units_total"));
				((ObjectNode)recordNode).put("UnitsAvailable", rs.getString("units_available"));
				((ObjectNode)recordNode).put("UnitsOccupied", rs.getString("units_occupied"));
				((ObjectNode)recordNode).put("ContactName", rs.getString("contact_name"));
				((ObjectNode)recordNode).put("ContactPhone", rs.getString("contact_phone"));
				((ObjectNode)recordNode).put("ProgramAddress", rs.getString("program_address"));
				((ObjectNode)recordNode).put("ProgramCity", rs.getString("program_city"));
				((ObjectNode)recordNode).put("ProgramZip", rs.getString("program_zip"));
				((ObjectNode)recordNode).put("ProgramAddressFull", rs.getString("program_address_full"));
				jSONrecords.insert(i++, recordNode);
			}
			conn.close();
			return rootNode.toString();
		} 
		catch (SQLException e) {
			e.printStackTrace();
			return "SQLException is:" + e;
		}
	 }
}