/*CRUD operations on the rosie_indexes_table
*/
package org.pathways.openciss.shared;

//for JPA

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.pathways.openciss.model.RosieIndexes;
import org.pathways.openciss.server.RosieCSVBlobServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RosieIndexesService {
	public static EntityManager em; 
	private static final transient Logger log = LoggerFactory.getLogger(RosieCSVBlobServlet.class);

	
	public RosieIndexes getRosieIndexes(String id) {
		RosieIndexes result = null;
		em =EMF.get().createEntityManager();
		try {
			result = em.find(RosieIndexes.class, id);
		}
		finally {em.close();}
		return result;
	}

//not finished
	@SuppressWarnings("finally")
	public boolean updateRosieIndexes(RosieIndexes r) {
		boolean result = false;
		em =EMF.get().createEntityManager();
		try{
		   em.getTransaction().begin();
		   //This isn't finished yet, needs to handle the others
		   RosieIndexes rx = em.find(RosieIndexes.class, r.getId());
		   if (r.getUsed() != rx.getUsed()) {
			   rx.setUsed(r.getUsed());
		   }
		   em.getTransaction().commit();
		   result = true;
		 } 
		catch (Exception e){System.out.println("couldn't persist: " + e);
			result = false;
		 }
		 finally {
		   em.close();
		   return result;
		 }
	}
		
	public String createRosieIndexes(RosieIndexes r) {
		// returns -1 if no RosieIndexes added, otherwise, return's RosieIndexes's id.
		String key = "-1";
		// ideally should perform an unduplication routine before adding the RosieIndexes
		// for starters, we'll automatically add a new RosieIndexes.
		em =EMF.get().createEntityManager();
		try{
		   em.getTransaction().begin();
		   em.persist(r);
		   em.getTransaction().commit();
		   // get newly assigned id
		   key = r.getId();
		   log.info("persisted id: " + key + " in RosieIndexes table" );
		 } catch (Exception e){System.out.println("couldn't persist" + e);}
		 finally {
		   em.close();
		 }
		return key;
	}
	
	public int deleteAllRosieIndexes() {
		// returns -1 if no RosieIndexes added, otherwise, return's number of records deleted.
		int key = -1;
		em =EMF.get().createEntityManager();
		try{
		   em.getTransaction().begin();
		   Query q = em.createQuery ("DELETE FROM RosieIndexes");
		   int numberDeleted = q.executeUpdate();
		   em.getTransaction().commit();
		   log.info("number of records deleted: " + numberDeleted);
		 } catch (Exception e){System.out.println("couldn't persist" + e);}
		 finally {
		   em.close();
		 }
		return key;
	}
}
