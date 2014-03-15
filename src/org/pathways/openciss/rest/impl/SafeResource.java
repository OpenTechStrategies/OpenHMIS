package org.pathways.openciss.rest.impl;

//import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
//import javax.servlet.http.HttpServletRequest;
import org.apache.shiro.SecurityUtils;
//import org.apache.shiro.authc.UsernamePasswordToken;
//import org.apache.shiro.subject.PrincipalCollection;
//import org.apache.shiro.subject.PrincipalMap;
//import org.apache.shiro.subject.Subject;
//import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;


@Path("/safe")
public class SafeResource {
    
	private static final Logger log = Logger.getLogger("log2");

    @GET
    //@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
    //public Response get(HttpServletRequest request) {
    public Response get() {
    	log.setLevel(Level.ALL);
        log.info("inside /safe method");
    	String state;
       	if (SecurityUtils.getSubject().hasRole("vip")) {
            state = "authorized";
        } else {
            state = "authenticated";
        }
    	//HttpServletRequest httpRequest = (HttpServletRequest)request;
    	//String username = httpRequest.getParameter("username");
    	//log.info("username received is: " + username);
        //char[] password = (httpRequest.getParameter("password")).toCharArray();
        //UsernamePasswordToken token = new UsernamePasswordToken(username, password );
        //Authenticate with subject's un/pw
        //s.login(token);
        
        //log.info("Principals Listing");
        //try {
        	//PrincipalCollection pc = s.getPrincipals();
        	//@SuppressWarnings("unchecked")
        	//Iterator<PrincipalMap> i = pc.iterator();
        	//while (i.hasNext())  {
            	//System.out.println("Principal is: " + i.next().toString());
                //log.info("Principal is: " + i.next().toString());
            //}
        //} 
        //catch (NullPointerException e) {
        	//log.info("No principals found");
        //}        
        //System.out.println(s.getPrincipals());
       
        return Response.ok(state).build();
    }
}