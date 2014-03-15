package org.pathways.openciss.rest.impl;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

@Path("/existence/search")
public class ExistenceSearch {
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	 public String getRecord(@QueryParam("firstname") String firstName, @QueryParam("lastname") String lastName) {
		// Gets a record given a combination of possible parameters and returns 
		// one or more matching JSON records
		//System.out.println("In existence search, we got first name: " + firstName + ", last name:" + lastName);
		BloomFilter b = new BloomCache().getCachedBloomFilter();
		b.populate();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.createObjectNode(); // will be of type ObjectNode
		//JsonNode responseNode = mapper.createObjectNode(); // will be of type ObjectNode

		if (b.test(firstName+lastName)) { // that is, if it was found in the bloom filter
				((ObjectNode)rootNode).put("Exists", true);
		} else {((ObjectNode)rootNode).put("Exists", false);}
		
		return rootNode.toString();
	 }
}