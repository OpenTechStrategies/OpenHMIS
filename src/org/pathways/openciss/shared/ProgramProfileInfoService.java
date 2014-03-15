/*CRUD operations on the _program_profile_info_table
*/
package org.pathways.openciss.shared;

//for JPA
import javax.persistence.EntityManager;

import org.pathways.openciss.model.ProgramProfileInfo;

public class ProgramProfileInfoService {
	public EntityManager em; 
	
	public ProgramProfileInfo getProgramProfileInfo(int program_key) {
		ProgramProfileInfo result = null;
		em =EMF.get().createEntityManager();
		try {
			result = em.find(ProgramProfileInfo.class, program_key);
		}
		finally {em.close();}
		return result;
	}
	
	@SuppressWarnings("finally")
	public boolean updateProgramProfileInfo_Occupany(ProgramProfileInfo ppi) {
		em =EMF.get().createEntityManager();
		try{
		   em.getTransaction().begin();
		   ProgramProfileInfo ppix = em.find(ProgramProfileInfo.class, ppi.getAgencyKey());
		   ppix.setUnitsAvailable(ppi.getUnitsAvailable()); 
		   ppix.setUnitsOccupied(ppi.getUnitsOccupied());
		   ppix.setUnitsTotal(ppi.getUnitsTotal());
		   em.getTransaction().commit();
		 } catch (Exception e){System.out.println("couldn't persist");}
		 finally {
		   em.close();
		   return false;
		 }
	}
	
	@SuppressWarnings("finally")
	public boolean updateProgramProfileInfo_Shelter(ProgramProfileInfo ppi) {
		em =EMF.get().createEntityManager();
		try{
		   em.getTransaction().begin();
		   //ProgramProfileInfo ppix = em.find(ProgramProfileInfo.class, ppi.getAgencyKey());
//		   ppix.setAgencyName(ppi.getAgencyName()); 
//		   ppix.setContactPhone(ppi.getContactPhone());
//		   ppix.setProgramAddress(ppi.getProgramAddress());
//		   ppix.setProgramCity(ppi.getProgramCity());
//		   ppix.setProgramZip(ppi.getProgramZip());
//		   ppix.setFeedSource(ppi.getFeedSource());
//		   System.out.println("ppix is: " +ppix.toString());
		   em.persist(ppi);
		   em.getTransaction().commit();
		 } catch (Exception e){System.out.println("couldn't persist");}
		 finally {
		   em.close();
		   return false;
		 }
	}
	
//	public ProgramProfileInfo createProgramProfileInfo(int program_key) {
		
//		public void createContact(Contact c) {
//	        logger.info("Entering createContact: [" 
//	                    + c.getFirstName() + "," 
//	                    + c.getLastName() + "]");           
//	        EntityManager mgr = EMF.get().createEntityManager();
//	        try {
//	            mgr.getTransaction().begin();
//	            mgr.persist(c);
//	            mgr.getTransaction().commit();
//	        } finally {
//	            mgr.close();
//	        }
//	        logger.info("Exiting createContact");

		
//		ProgramProfileInfo result = null;
//		EntityManager em =EMF.get().createEntityManager();
//		try {
//			result = em. (ProgramProfileInfo.class, program_key);
//		}
//		finally {em.close();}
//		return result;
//	}
}
