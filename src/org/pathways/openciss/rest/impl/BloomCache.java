package org.pathways.openciss.rest.impl;

//added for memcache
import java.util.Collections;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

public class BloomCache {
	Cache cache;
	BloomFilter b;
	
	public BloomFilter getCachedBloomFilter() {	    

		// check to see if there is already a Bloom Filter in the memcache
		// to do that, first get the cache
	    try {
	        CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
	        cache = cacheFactory.createCache(Collections.emptyMap());
	    } catch (CacheException e) {}
	    // then, look in it for a Bloom Filter by Key value
	    if (cache.containsKey("bloom_filter")) {b = (BloomFilter) cache.get("bloom_filter");}
	    // if a Bloom Filter exists already in the memcache, return a reference to that memcached Bloom Filter
	    if (b != null) {return b;}
	    // if it doesn't already exist, create a Bloom Filter, store it in the memcache, and return a reference to the memcached Bloom Filter
	    else { 
	    	// create a regular Bloom Filter
			b = new BloomFilter();
			b.populate();
	    	// store the Bloom Filter object in memcache
		    cache.put("bloom_filter", b);
	    	// return a reference to the stored Bloom Filter
			return b; 
	    }
	}
}