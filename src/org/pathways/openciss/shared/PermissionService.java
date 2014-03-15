/*CRUD operations on the permission_table
*/
package org.pathways.openciss.shared;

//for JPA
import javax.persistence.EntityManager;

import org.pathways.openciss.model.Permission;

public class PermissionService {
	public static EntityManager em; 
	
	public Permission getPermission(int permission_key) {
		Permission result = null;
		em =EMF.get().createEntityManager();
		try {
			result = em.find(Permission.class, permission_key);
		}
		finally {em.close();}
		return result;
	}
	
	@SuppressWarnings("finally")
	public boolean updatePermission(Permission p, String permissionID) {
		boolean result = false;
		em =EMF.get().createEntityManager();
		try{
		   em.getTransaction().begin();
		   Permission px = em.find(Permission.class, Integer.valueOf(permissionID));
		   px.setPermissionName(p.getPermissionName());
		   px.setInactive(p.getInactive());

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
		
	public int createPermission(Permission p) {
		// returns -1 if no permission added, otherwise, return's permission's id.
		int key = -1;
		// ideally should perform an unduplication routine before adding the client
		// for starters, we'll automatically add a new client.
		em =EMF.get().createEntityManager();
		try{
		   em.getTransaction().begin();
		   em.persist(p);
		   em.getTransaction().commit();
		   // get client id created
		   key = p.getPermissionKey();
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
