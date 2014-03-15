package org.pathways.openciss.rest.impl;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("s")
public class Test {
    private static final Logger log = Logger.getLogger("log1");

    @GET
    public String sayHello(@QueryParam ("name") String id) {
        log.info("name QueryParam received is:" + id );
    	if ( id != null ) {
            return "Hello " + id + "!";
        }
        return "Hello World!";
    }
}