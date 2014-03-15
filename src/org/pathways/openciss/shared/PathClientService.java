/*CRUD operations on the path_client_table
*/
package org.pathways.openciss.shared;

//for JPA
import javax.persistence.EntityManager;
import org.pathways.openciss.shared.EMF;
import org.pathways.openciss.model.PathClient;

public class PathClientService {
	public static EntityManager em; 
	
	public PathClient getPathClient(int client_key) {
		PathClient result = null;
		em =EMF.get().createEntityManager();
		try {
			result = em.find(PathClient.class, client_key);
		}
		finally {em.close();}
		return result;
	}
	
	@SuppressWarnings("finally")
	public boolean updatePathClient(PathClient pc) {
		boolean result = false;
		em =EMF.get().createEntityManager();
		try{
		   em.getTransaction().begin();
		   //extract the client id
		   int clientKey = pc.getClientKey();
		   PathClient pcx = em.find(PathClient.class, clientKey);
		   //need to make it check for nulls before updating
		   if (pc.getNameFirst() !=null) {
			   pcx.setNameFirst(pc.getNameFirst());
		   }
		   if (pc.getNameFirstAlias() !=null) {
			   pcx.setNameFirstAlias(pc.getNameFirstAlias());
		   }
		   if (pc.getNameLast() !=null) {
			   pcx.setNameLast(pc.getNameLast());
		   }
		   if (pc.getNameLastAlias() !=null) {
			   pcx.setNameLastAlias(pc.getNameLastAlias());
		   }
		   
		   if (pc.getNameMiddle() !=null) {
			   pcx.setNameMiddle(pc.getNameMiddle());
		   }
		   
		   if (pc.getDateOfBirth() !=null) {
			   pcx.setDateOfBirth(pc.getDateOfBirth()); 
		   }
		   if (pc.getGenderCode() !=null) {
			   pcx.setGenderCode(pc.getGenderCode());
		   }
		   if (pc.getSsn() != 0) {
			   pcx.setSsn(pc.getSsn());
		   }
		   if (pc.getSsnQuality() != 0) {
			   pcx.setSsnQuality(pc.getSsnQuality());
		   }		   
		   if (pc.getInactive() != null) {
			   Integer inactive = pc.getInactive();
			   pcx.setInactive(inactive);
		   }
		   
		   em.getTransaction().commit();
		   result = true;
		 } 
		catch (Exception e){System.out.println("couldn't persist: " + e);
			e.printStackTrace();
			result = false;
		 }
		 finally {
		   em.close();
		   return result;
		 }
	}
		
	public int createPathClient(PathClient pc) {
		// returns -1 if no client added, otherwise, return's client's id.
		int key = -1;
		// ideally should perform an unduplication routine before adding the client
		// for starters, we'll automatically add a new client.
		em =EMF.get().createEntityManager();
		try{
		   em.getTransaction().begin();
		   em.persist(pc);
		   em.getTransaction().commit();
		   // get client id created
		   key = pc.getClientKey();
		 } catch (Exception e){System.out.println("couldn't persist" + e);}
		 finally {
		   em.close();
		 }
		return key;
	}
	
//	        logger.info("Entering createContact: [" 
//	                    + c.getFirstName() + "," 
//	                    + c.getLastName() + "]");           
//	        logger.info("Exiting createContact");

}
