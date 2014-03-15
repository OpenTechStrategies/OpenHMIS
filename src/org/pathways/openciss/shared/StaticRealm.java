package org.pathways.openciss.shared;

//import java.util.logging.Level;
//import java.util.logging.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.pathways.openciss.shared.Safe;

public class StaticRealm extends AuthorizingRealm {
	//private static final Logger log = Logger.getLogger("log3");
	//private static final transient Logger log = LoggerFactory.getLogger(StaticRealm.class);
	final static Logger log = LoggerFactory.getLogger(StaticRealm.class);
	
	
	@Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
            throws AuthenticationException {
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        //System.out.println("Is log.isDebugEnabled()? " + log.isDebugEnabled());
        String username = upToken.getUsername();
        System.out.print("submitted username is: " + username);
        System.out.println(", submitted password is: " + upToken.getPassword().toString());
        checkNotNull(username, "Null usernames are not allowed by this realm.");
        String password = null;
        try {
        	password = Safe.getPassword(username); 
        } catch (Exception e) {System.out.println(e);}
        return new SimpleAuthenticationInfo(username, password.toCharArray(), getName());
    	//new SimpleAuthenticationInfo(username, "blue".toCharArray(), getName());

    }
 
    private void checkNotNull(Object reference, String message) {
        if (reference == null) {
            throw new AuthenticationException(message);
        }
        else {System.out.println("Not null");}
    }

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(
			PrincipalCollection principals) {
		return null;
	}
}
