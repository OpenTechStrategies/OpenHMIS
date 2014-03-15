package org.pathways.openciss.shared;

//for JPA
import javax.persistence.EntityManager;

import org.pathways.openciss.model.ClientOutreach;
//import com.google.appengine.api.datastore.Key;
//import com.google.appengine.api.datastore.KeyFactory;

public class ClientOutreachService {
	
	public static EntityManager em;
	
	public int createClientOutreach(ClientOutreach co) {
		// returns -1 if no client outreach added, otherwise, return's client outreach's id.
		//KeyFactory.createKey(ClientOutreach.class.getSimpleName(), "-1");
		int key = -1;
		//co.setOutreachKey(Integer.valueOf(key.toString()));
		// ideally should perform an unduplication routine before adding the client outreach
		// for starters, we'll automatically add a new client outreach.
		em =EMF.get().createEntityManager();
		try{
		   em.getTransaction().begin();
		   em.persist(co);
		   em.getTransaction().commit();
		   // get client id created
		   key = co.getOutreachKey();
		 } catch (Exception e){System.out.println("couldn't persist" + e);}
		 finally {
		   em.close();
		 }
		return key;
	}
}