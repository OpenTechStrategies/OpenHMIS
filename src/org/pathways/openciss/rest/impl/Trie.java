package org.pathways.openciss.rest.impl;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.google.appengine.api.rdbms.AppEngineDriver;
// for populate()
// for memcache storage

public class Trie implements ITrie, Serializable {
	
	static final long serialVersionUID = 1L;
	
	public Node rootNode;
	ArrayList<Node> wordEndNodes = new ArrayList<Node>();
	String wordBuilder;
	private String type;
	
	Trie(String type) {
		this.type = type;
		System.out.println("Trie constructor called");
		// So now we need the data structure that holds the root node
		this.rootNode = new Node();
	}
	
	public void insert(String s) {
		// start off the root node
		// root node can not have a character value, since a word can start with any of the 58 letters
		this.rootNode.addWord(s, rootNode);
	}
	
	public boolean search(String s) {
		// returns true if the word is in the trie
		// start traversing always at the root node
		return rootNode.traverse(s);
	}
	
	public ArrayList<String> returnFullWordMatches(String prefix) {
		// returns all the words in the trie that begin with the given substring
		// if it does, fetch all the full word children
		
		// should return the root node for an empty prefix
		// if a non-existing prefix, return lastSearchNode = null
		Node lastSearchNode = rootNode.traverseGetSearchRootNode(prefix);
		
		ArrayList<Node> nodeResults = new ArrayList<Node>();
		ArrayList<String> wordResults = new ArrayList<String>();

		if (lastSearchNode != null) { // matched letters or nothing sent
			if (lastSearchNode.character != '0') {
				//System.out.println("last search node is: " + 	lastSearchNode.character );
			} else {System.out.println("character is unassigned");}
			nodeResults = spiderOutGetWordEndNodes(lastSearchNode);
		} else { //for unmatched letter
			return wordResults;
		}
		
		//System.out.println("Printing out nodes returned from Spider");
		//for (Node a : nodeResults){
			//System.out.println("node result: " + a.character);
		//}
		wordResults = getWordsFromEndNodes(nodeResults);
		//System.out.println("read words are: ");
		ArrayList<String>wordResultsReversed = new ArrayList<String>();
		if (wordResults != null) {
			for (String y : wordResults){
				wordResultsReversed.add(new StringBuffer(y).reverse().toString());
			}
		}
		return wordResultsReversed;
	}
	
	public ArrayList<String> getWordsFromEndNodes(ArrayList<Node> endNodes) {
		ArrayList<String> finalWords = new ArrayList<String>();
		for (Node n : endNodes){
			//System.out.println("Getting word for node: " + n.character);
			wordBuilder = "";
			wordBuilder = n.character + wordBuilder;
			String word = getWord(n);
			//System.out.println("Got word: " + word);
			finalWords.add(word);
		}
		return finalWords;
	}
	
	String getWord(Node n) {
		// construct a word from backlink chars
		if (n.backLink !=null) {
			//System.out.println("n.backLink.character is: " + n.backLink.character +".");
			if (n.backLink.character != '0') {
				wordBuilder = wordBuilder + n.backLink.character;
				//System.out.println("word is lengthened to: " + wordBuilder);
				getWord(n.backLink);
			} else {
			}
		} else {
		}
		return wordBuilder;
	}
	
	ArrayList<Node> spiderOutGetWordEndNodes(Node node) {
		// traverse the trie: look into child nodes for matches and return all matching full word terminating children nodes
		// traverse forward of the first level
		//System.out.println("searching through node " + node.character + "'s forward link");
		for (int i = 0; i<node.forwardLinks.length; i++){
			if (node.forwardLinks[i] !=null) {
				//System.out.println(node.character + "'s forward link to " + node.forwardLinks[i].character + " isn't null");
				if (node.forwardLinks[i].isWordEnd) {
					//System.out.println("node.forwardLinks[" + i + "] is a word end");
					wordEndNodes.add(node.forwardLinks[i]);
				}
				if (!(node.forwardLinks[i].isLeaf)) {
					//System.out.println("recursing");
					spiderOutGetWordEndNodes(node.forwardLinks[i]);
				} else {
				//System.out.println(node.character + "'s " +"forwardLinks[" + i + "] = " + node.forwardLinks[i].character + " is a leaf"); 
				continue;
				}
			} //else {System.out.println(node.character + " forwardLink to " + i + " is null");}
		} 
		return wordEndNodes;
	}
	
 	public void populate() {
		//System.out.println("Inside TriePopulate");
		AppEngineDriver a = new AppEngineDriver();
		
		try {
			Connection conn = null;
			DriverManager.registerDriver(a);
			conn = DriverManager.getConnection("jdbc:google:rdbms://pcni.org:openhmis:openciss/compass");
			Statement st = conn.createStatement();
			System.out.println("SELECT " + type + " FROM `compass`.`path_client`;");
			ResultSet rs = st.executeQuery("SELECT " + type + " FROM `compass`.`path_client`;");
			//System.out.println("inside try block");
			//System.out.println("rs is:" + rs.getFetchSize());
			while (rs.next()) {
				//System.out.println("inserting rs value: " + rs.getString(1));
				this.insert(rs.getString(1));
			}
		
		} catch (SQLException e) {e.printStackTrace();}
	}
	
	class Node implements Serializable {
		static final long serialVersionUID = 1L;
		private Node[] forwardLinks=new Node[58];
		private Node backLink;
		private boolean isLeaf;
		private boolean isWordEnd;
		private char character = '0'; // character this node represents.  For this application, since the node's character 
		  // value can be derived from the index, there really is no need for a "char character"
		  // attribute, but this code is more reusable for other scenarios by separating the 
		  // index from its value. 
   
		Node() {} // I get Eclipse warnings if I don't have this 
		
		Node(char c) {
			character = c;
		}
		
		public void addWord(String word, Node parentNode) {
			if (word == null) {
				System.out.println("in addWord.  word is null");
				return;
			}
			else if (word.length() == 0) {
					System.out.println("in addWord.  length ==0");
					return;
			}
			
			int ch = word.charAt(0) - 'A';
			// A to z is: 65-122, but we subtracted A (65)
			assert(ch >= 0 && ch <= 57);
				// if child node doesn't exist ... 
			if (forwardLinks[ch] == null) {
				// System.out.println("in addWord.  forwardLinks[ch] == null, ch(" +  ch + ") is: " + (char)(ch + 'A'));
				// then make the child node for that character
				forwardLinks[ch] = new Node(word.charAt(0));
				// make a back/reverse link to the parent so we can traverse up to get the full word from any node
				forwardLinks[ch].backLink = parentNode;
			}
			if (word.length() == 1) {
				// System.out.println("in addWord.  word.length() == 1, character is: " + (char)(ch + 'A'));
				// since this is the last letter in the word, let's see if this node is a leaf 
				//  (i.e., no other words go further down this branch than this word did)
				// System.out.println("Final Step in addWord.  Changing isLeaf='true' for node "+ forwardLinks[ch].character + ".  parent is: " + forwardLinks[ch].backLink.character);
				if (forwardLinks[ch] != null) {
					forwardLinks[ch].isLeaf = true;
				}
			}
			// and the recursive cycle continues onward...
			if (word.length() > 1) {
			     forwardLinks[ch].addWord(word.substring(1), forwardLinks[ch]);
			     isLeaf=false;
			     // System.out.println("changing isLeaf=False: " + ch);
			} else forwardLinks[ch].isWordEnd = true;
		}
		
		private boolean search(int c) {
			// search through this node for a given child node
			for (Node i : forwardLinks) {
				if (i != null) {  
					//System.out.println("c is: " + (char)(c + 'A'));
					if ((int)(i.character) == (c + 'A')) {
						// System.out.println("Returning true from search");
						return true;
					}
				}
			}
			// System.out.println("Returning false from search");
			return false;
		}		
		
		Node traverseGetSearchRootNode(String s)  {
			// traverse the trie down through the end of the search string characters, and return the final node
			Node lastNode = this;
			if (s.isEmpty()) {
				//System.out.println("String is empty in traverseGetSearchRootNode");
				return null;
			}
			else {
				//System.out.println("String received is: " + s);
				// get the last node in the search string
				for (int i=0; i<s.length(); i++) {
					int ch = s.charAt(i) - 'A';
					//System.out.println("traversing search string at: " + (char)(ch+'A'));
					if (lastNode.search(ch)) {
						//System.out.println("yep, we found " + (char)(ch+'A'));
						lastNode = lastNode.forwardLinks[ch];
						//System.out.println("lastNode is: " + lastNode.character);
					}
					else {
						//System.out.println("No matches, so returning null");
						return null;}
				}
				//System.out.println("Search root node returned is: " + lastNode.character);
				return lastNode;
			}
		}
		
		boolean traverse(String s)  {
			// System.out.println("Entering plain old traverse method");
			// traverse the trie: look into child nodes for matches and recurse
			if (s.isEmpty()) return false;
			int ch = s.charAt(0) - 'A';
			// A to z is: 65-122, but we subtracted A (65)
		    assert(ch >= 0 && ch <= 57);
			// See if the current char is a child of the parent node
		    if (!isLeaf && this.search(ch)) {
		    	// System.out.println("in traverse. not a leaf, and " + ch + " was boolean true (found)");
		    	if (this.forwardLinks[ch].isWordEnd && s.length() == 1) {
			    	// System.out.println("in traverse" + ch + " is a word end, and length == 1");
					return true;
				}
		    	return this.forwardLinks[ch].traverse(s.substring(1));
			} 
			return false;
		}
	}

	public static void main (String args[]) {
		System.out.println("Starting");
		Trie t = new Trie("name_last");
		System.out.println("Adding Sage");
		t.insert("Sage");
		System.out.println("Adding Sageser");
		t.insert("Sageser");
		System.out.println("Adding Sagey");
		t.insert("Sagey");
		System.out.println("Adding Saggett");
		t.insert("Saggett");
		System.out.println("Adding Waege");
		t.insert("Waege");
		System.out.println("getting all the words the start with ''");
		//System.out.println(t.returnFullWordMatches("Sag").toString());

		for  (String i:t.returnFullWordMatches("")) {
			System.out.println(i);
		}
	}
}