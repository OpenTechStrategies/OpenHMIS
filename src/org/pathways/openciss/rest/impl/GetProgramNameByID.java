package org.pathways.openciss.rest.impl;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.pathways.openciss.model.ProgramProfileInfo;

import org.pathways.openciss.shared.EMF;
//for JPA
//import org.pathways.openciss.shared.ProgramProfileInfoService;


@Path("/programNameByID")
public class GetProgramNameByID {
	EntityManager em = EMF.get().createEntityManager();
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	 public String getRecord(@QueryParam("id") String id) {
		int program_key = Integer.valueOf(id);
		try {
	        // ... do stuff with em ...
			ProgramProfileInfo ppi = em.find(ProgramProfileInfo.class, program_key);
			String result = ppi.getProgramName();
			System.out.println(result);
			return result;
	    } finally {
	        em.close();
	    }
	}
}