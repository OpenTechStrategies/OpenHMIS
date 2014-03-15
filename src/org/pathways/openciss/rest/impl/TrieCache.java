package org.pathways.openciss.rest.impl;

//added for memcache
import java.util.Collections;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;
//import com.google.appengine.api.memcache.MemcacheServiceFactory;
//import com.google.appengine.api.memcache.MemcacheService;
public class TrieCache {
	Cache cache;
	Trie t;
	
	public Trie getCachedTrie(String type) {	    
		// check to see if there is already a Trie in the memcache
		// to do that, first get the cache
	    try {
	        CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
	        cache = cacheFactory.createCache(Collections.emptyMap());
	    } catch (CacheException e) {}
	    // then, look in it for a Trie by Key value
	    if (cache.containsKey(type+ "_trie")) {t = (Trie) cache.get(type+ "_trie");}
	    // if a Trie exists already in the memcache, return a reference to that memcached trie
	    if (t != null) {return t;}
	    // if it doesn't already exist, create a Trie, store it in the memcache, and return a reference to the memcached Trie
	    else { 
	    	// create a regular Trie
			t = new Trie(type);
			t.populate();
	    	// store the Trie object in memcache
		    cache.put(type+ "_trie", t);
	    	// return a reference to the stored Trie
			return t; 
	    }
	}
}

