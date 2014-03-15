package org.pathways.openciss.shared;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.apache.shiro.session.Session;

public class ShiroTest {
    private static final transient Logger log = LoggerFactory.getLogger(ShiroTest.class);

    public static void main(String[] args) {
        log.info("My First Apache Shiro Application");
        //System.out.println("My First Apache Shiro Application without Logger");
        Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");
        SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);
        Subject currentUser = SecurityUtils.getSubject();
        //Session session = currentUser.getSession();
        if ( !currentUser.isAuthenticated() ) {
            //collect user principals and credentials in a gui specific manner 
            //such as username/password html form, X509 certificate, OpenID, etc.
            //We'll use the username/password example here since it is the most common.
            UsernamePasswordToken token = new UsernamePasswordToken("lonestarr", "vespa");

            //this is all you have to do to support 'remember me' (no config - built in!):
            //token.setRememberMe(false);

            try {
                currentUser.login( token );
                //if no exception, that's it, we're done!
                log.info("username logged in is: " + token.getUsername());
            } catch ( UnknownAccountException uae ) {
                //username wasn't in the system, show them an error message?
            	log.info("Username wasn't in the system");
            } catch ( IncorrectCredentialsException ice ) {
                //password didn't match, try again?
            	log.info("Password didn't match");
            } catch ( LockedAccountException lae ) {
                //account for that username is locked - can't login.  Show them a message?
            	log.info("Account for that username is locked - can't login.");
            } 
            //catch ( AuthenticationException ae ) {}
            // ... more types exceptions to check if you want... 
            //unexpected condition - error?
    	}
        System.exit(0);
    }
}