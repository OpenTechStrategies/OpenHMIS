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


@Path("/search/firstname")
public class FirstNameSearch {
	 
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{prefix}") 
	 public String getPerson(@PathParam("prefix") String prefix) {
		// Query Trie which is populated by database here
		// this is very inefficient, but I have no session or background process persisting a tree (memcache/backend)
		// working on the memcache implementation at: CachedTrie
		
		//PopulatedTrie pt = new PopulatedTrie("name_first");
		//Trie pt = new Trie("name_first");
		Trie t = new TrieCache().getCachedTrie("name_first");
		System.out.println("we got " + prefix);
		//ArrayList<String> words = pt.t.returnFullWordMatches(prefix);
		ArrayList<String> words = t.returnFullWordMatches(prefix);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.createObjectNode(); // will be of type ObjectNode
		ArrayNode an = ((ObjectNode)rootNode).putArray("firstName");
		int i = 0;
		for (String word : words) {
			an.insert(i++, word);
		}
		//return prefix + " that's what I got";
		return rootNode.toString();
	 }
}
