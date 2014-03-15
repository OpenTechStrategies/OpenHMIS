package org.pathways.openciss.shared;

import java.util.logging.Level;

import javax.servlet.http.HttpServlet;

import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import org.pathways.openciss.rest.impl.BloomCache;
import org.pathways.openciss.rest.impl.BloomFilter;
import org.pathways.openciss.rest.impl.TrieCache;


@SuppressWarnings("serial")
public class WarmupServlet extends HttpServlet {
//	public void doGet(HttpServletRequest req, HttpServletResponse resp)
//			throws IOException {
	public void init() {
		// Using the synchronous cache
	    MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
	    syncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
	    //empty the entire cache before repopulating
	    syncCache.clearAll();
		System.out.println("calling BloomCache");
		BloomFilter b = new BloomCache().getCachedBloomFilter();
		b.populate();
		System.out.println("calling TrieCache");
		new TrieCache().getCachedTrie("name_first");
		new TrieCache().getCachedTrie("name_last");
	}
}
