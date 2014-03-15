/*CRUD operations on the role_table
*/
package org.pathways.openciss.shared;

//for JPA
import javax.persistence.EntityManager;

import org.pathways.openciss.model.Role;

public class RoleService {
	public static EntityManager em; 
	
	public Role getRole(int role_key) {
		Role result = null;
		em =EMF.get().createEntityManager();
		try {
			result = em.find(Role.class, role_key);
		}
		finally {em.close();}
		return result;
	}
	
	@SuppressWarnings("finally")
	public boolean updateRole(Role r, String roleID) {
		boolean result = false;
		em =EMF.get().createEntityManager();
		try{
		   em.getTransaction().begin();
		   Role rx = em.find(Role.class, Integer.valueOf(roleID));
		   rx.setRoleName(r.getRoleName());
		   rx.setInactive(r.getInactive());

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
		
	public int createRole(Role u) {
		// returns -1 if no role added, otherwise, return's role's id.
		int key = -1;
		// ideally should perform an unduplication routine before adding the client
		// for starters, we'll automatically add a new client.
		em =EMF.get().createEntityManager();
		try{
		   em.getTransaction().begin();
		   em.persist(u);
		   em.getTransaction().commit();
		   // get client id created
		   key = u.getRoleKey();
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
