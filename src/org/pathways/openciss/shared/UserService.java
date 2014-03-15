/*CRUD operations on the user_table
*/
package org.pathways.openciss.shared;

//for JPA
//import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
//import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.pathways.openciss.model.User;

public class UserService {
	public static EntityManager em; 
	
	public static User getUser(int user_key) {
		User result = null;
		em =EMF.get().createEntityManager();
		try {
			result = em.find(User.class, user_key);
		}
		finally {em.close();}
		return result;
	}
	
	public static User getUser(String username) {
		User result = null;
		em =EMF.get().createEntityManager();
		try {
			//result = em.find(User.class, username);
			//String queryStr = "SELECT DISTINCT u FROM User AS u WHERE (u.username = " + username + ")";
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<User> q = cb.createQuery(User.class);
			Root<User> u = q.from(User.class);
			q.select(u);
			q.where(cb.equal(u.get("username"), username));
			q.distinct(true);
			TypedQuery<User> query = em.createQuery(q);
			result = query.getSingleResult();
		}
		finally {em.close();}
		return result;
	}
	
	@SuppressWarnings("finally")
	public boolean updateUser(User u, String userID) {
		boolean result = false;
		em =EMF.get().createEntityManager();
		try{
		   em.getTransaction().begin();
		   User ux = em.find(User.class, Integer.valueOf(userID));
		   ux.setNameFirst(u.getNameFirst());
		   ux.setNameLast(u.getNameLast());
		   ux.setInactive(u.getInactive());

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
		
	public int createUser(User u) {
		// returns -1 if no user added, otherwise, return's user's id.
		int key = -1;
		// ideally should perform an unduplication routine before adding the client
		// for starters, we'll automatically add a new client.
		em =EMF.get().createEntityManager();
		try{
		   em.getTransaction().begin();
		   em.persist(u);
		   em.getTransaction().commit();
		   // get client id created
		   key = u.getUserKey();
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
