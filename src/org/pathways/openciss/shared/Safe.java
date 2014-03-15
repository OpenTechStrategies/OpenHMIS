package org.pathways.openciss.shared;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
//import java.util.logging.Level;
//import java.util.logging.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.shiro.crypto.hash.Sha256Hash;
import com.google.common.collect.HashMultimap;
//import com.google.appengine.labs.repackaged.com.google.common.collect.HashMultimap;
//import com.google.appengine.repackaged.com.google.common.collect.HashMultimap;
	 
public class Safe {
	//private static final Logger log = Logger.getLogger("log2");
	final static Logger log = LoggerFactory.getLogger(StaticRealm.class);
	static Map<String, String> passwords = new HashMap<String, String>();
	static HashMultimap<String, String> roles = HashMultimap.create();
    static{
        passwords.put(""username_here, encrypt("password_here"));
        roles.put("username_here", "role_here");
    }
 
	private static String encrypt(String password) {
        return new Sha256Hash(password).toString();
    }
 
    public static String getPassword(String username) {
        log.info("inside Safe.getPassword method");
        log.error("test");
        System.out.println("Safe static passwords.get(username) is: " +passwords.get(username));
    	return passwords.get(username);
    }
 
    public static Set<String> getRoles(String username) {
        return roles.get(username);
    }
}

