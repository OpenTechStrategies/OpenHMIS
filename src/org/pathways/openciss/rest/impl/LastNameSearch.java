package org.pathways.openciss.rest.impl;
import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

@Path("/search/lastname/")
public class LastNameSearch {
	 
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{prefix}") 
	 public String getPerson(@PathParam("prefix") String prefix) {
		// Query Trie which is populated by database here
		// this is very inefficient, but I have no session or background process persisting a tree (memcache/backend)
		//PopulatedTrie pt = new PopulatedTrie("name_last");
		Trie t = new TrieCache().getCachedTrie("name_last");
		//ArrayList<String> words = pt.t.returnFullWordMatches(prefix);
		ArrayList<String> words = t.returnFullWordMatches(prefix);
		System.out.println("we got " + prefix);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.createObjectNode(); // will be of type ObjectNode
		ArrayNode an = ((ObjectNode)rootNode).putArray("lastName");
		int i = 0;
		for (String word : words) {
			an.insert(i++, word);
		}
		return rootNode.toString();
	 }
}
