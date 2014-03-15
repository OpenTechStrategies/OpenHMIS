package org.pathways.openciss.shared;

import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * A {@link Realm} which uses {@link UserService} to authenicate users
 * code taken from: http://www.gdevelop.com/blog/2010/12/apache-shiro-on-appengine/
 */
public class GaeAuthenticatingRealm extends AuthorizingRealm{
  public GaeAuthenticatingRealm() {
  }

  @Override
  protected void onInit() {
    // We use UserService to authenticate users, 
    // thus dont have any credentials
    setCredentialsMatcher(new AllowAllCredentialsMatcher());
  }

  protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) {
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    if (user == null){
      throw new AccountException("Not authenticated.");
    }
    
    return new SimpleAuthenticationInfo(user, null, getName());
  }

  protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
    // We perform Authentication only, thus return null
    return null;
  }
}
