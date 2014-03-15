package org.pathways.openciss.shared;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * An {@link AuthenticatingFilter} which uses UserService
 * to control the authentication process 
 * code taken from: http://www.gdevelop.com/blog/2010/12/apache-shiro-on-appengine/
 */

public class GaeAuthenticatingFilter extends AuthenticatingFilter {
  public GaeAuthenticatingFilter() {
    setLoginUrl(null);
  }

  @Override
  protected AuthenticationToken createToken(
      ServletRequest request, ServletResponse response) {
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    return createToken(user.getUserId(), null, request, response);
  }

  @Override
  protected boolean isRememberMe(ServletRequest request) {
    return false;
  }

  @Override
  protected boolean onAccessDenied(ServletRequest request,
                                   ServletResponse response) throws Exception{
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    
    if (user == null){
      saveRequest(request);
      
      String requestURI = WebUtils.getRequestUri(WebUtils.toHttp(request));
      
      String loginUrl = this.getLoginUrl();
      if (loginUrl == null){
        loginUrl = userService.createLoginURL(requestURI);
        WebUtils.issueRedirect(request, response, loginUrl);
      }else{
        request.setAttribute("requestURI", requestURI);
        request.getRequestDispatcher(loginUrl).forward(request, response);
      }
      
      return false;
    }else{
      // Perform the internal login process
      boolean result = executeLogin(request, response);
      return result;
    }
  }
}
