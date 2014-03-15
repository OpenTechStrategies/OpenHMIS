/*CRUD operations on the hud_xml_3_blob_table
*/
package org.pathways.openciss.shared;

//for JPA
import java.util.List;

import javax.persistence.EntityManager;
//import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.pathways.openciss.shared.HUDXML3Blob;
import org.pathways.openciss.shared.HUDXML3Blob_;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import com.google.appengine.api.blobstore.BlobKey;


public class HUDXML3BlobService {
	private static final transient Logger log = LoggerFactory.getLogger(HUDXML3BlobService.class);

	public HUDXML3Blob getHUDXML3BlobFromXMLKey(String xml_key) {
		HUDXML3Blob result = null;
		List<HUDXML3Blob> resultList = subQuery(xml_key);
		if (resultList.isEmpty()) { 
			log.info("no results from query in getHUDXML3BlobFromXMLKey.  wait 200ms, then try again.");
			try {
				  Thread.sleep(200);
				  resultList = subQuery(xml_key);
				  result = resultList.get(0); log.info("Got hudxml3blob with id: " + resultList.get(0).getId());
			} catch (Exception e) {
				log.info("no results from query.  wait 1000ms, then try again. stack trace follows: ");  e.printStackTrace();
				try {
					Thread.sleep(1000);
					 result = resultList.get(0); log.info("Got hudxml3blob with id: " + resultList.get(0).getId());
				} catch (InterruptedException ie) {
				   log.info("Timer issue: ");
				   ie.printStackTrace();
				}
			}
		} else {result = resultList.get(0); log.info("Got hudxml3blob with id: " + resultList.get(0).getId());}
		return result;
	}
	
	private List<HUDXML3Blob> subQuery(String xml_key_inner) {
		List<HUDXML3Blob> resultList = null;
		try {
			EntityManager em =EMF.get().createEntityManager();
			CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
			//CriteriaQuery<PathClient> q = cb.createQuery(PathClient.class);
			CriteriaQuery<HUDXML3Blob> criteriaQuery = criteriaBuilder.createQuery(HUDXML3Blob.class);
			Root<HUDXML3Blob> blobs = criteriaQuery.from(HUDXML3Blob.class);
			criteriaQuery.select(blobs);
			log.info("searching for hudxml3blob with xml_key: " + xml_key_inner);
			Predicate predicate = criteriaBuilder.equal(blobs.get(HUDXML3Blob_.xmlUrl), xml_key_inner);
			criteriaQuery.where(predicate);
			TypedQuery<HUDXML3Blob> typedQuery = em.createQuery(criteriaQuery);
			try {
				  log.info("sleeping for 200ms for transaction to complete");
				  Thread.sleep(200);
				  resultList = typedQuery.getResultList();
			} catch (Exception e) {
				log.info("no results from query.  wait 1000ms more, then try again.  stack trace follows: "); e.printStackTrace();
				try {
					Thread.sleep(1000);
					resultList = typedQuery.getResultList();
				} catch (InterruptedException ie) {
				   log.info("Timer issue: ");
				   ie.printStackTrace();
				}
			}
		}
		catch (Exception e) {
			log.info("getHUDXML3BlobFromXMLKey criteria query issue:" + e.toString()); e.printStackTrace();
		}
		return resultList;
	}

	public HUDXML3Blob getHUDXML3Blob(String blob_key) {
		HUDXML3Blob result = null;
		EntityManager em =EMF.get().createEntityManager();
		try {
			result = em.find(HUDXML3Blob.class, blob_key);
		}
		finally {em.close();}
		return result;
	}
	
	@SuppressWarnings("finally")
	public boolean updateHUDXML3Blob(HUDXML3Blob b) {
		boolean result = false;
		EntityManager em =EMF.get().createEntityManager();
		try{
		   em.getTransaction().begin();
		   //This isn't finished yet, needs to handle the others
		   HUDXML3Blob bx = em.find(HUDXML3Blob.class, b.getId());
		   if (b.getCsvUrl1() !=null) {
			   if (!(b.getCsvUrl1().equals(bx.getCsvUrl1()))) {
				   bx.setCsvUrl1(b.getCsvUrl1());
			   }
		   }
		   if (b.getCsvUrl2() !=null) {
			   if (!(b.getCsvUrl2().equals(bx.getCsvUrl2()))) {
				   bx.setCsvUrl2(b.getCsvUrl2());
			   }
		   }
		   if (b.getCsvUrl3() !=null) {
			   if (!(b.getCsvUrl3().equals(bx.getCsvUrl3()))) {
				   bx.setCsvUrl3(b.getCsvUrl3());
			   }
		   }
		   if (b.getCsvUrl4() !=null) {
			   if (!(b.getCsvUrl4().equals(bx.getCsvUrl4()))) {
				   bx.setCsvUrl4(b.getCsvUrl4());
			   }
		   }
		   if (b.getCsvUrl6() !=null) {
			   if (!(b.getCsvUrl6().equals(bx.getCsvUrl6()))) {
				   bx.setCsvUrl6(b.getCsvUrl6());
			   }
		   }
		   if (b.getXmlUrl() != null) {
			   if (!(b.getXmlUrl().equals(bx.getXmlUrl()))) {
				   bx.setXmlUrl(b.getXmlUrl());
			   }
		   }
		   if (b.getCsvUrl1Indexed() != bx.getCsvUrl1Indexed()) {
			   bx.setCsvUrl1Indexed(b.getCsvUrl1Indexed());
		   }
		   if (b.getCsvUrl2Indexed() != bx.getCsvUrl2Indexed()) {
			   bx.setCsvUrl2Indexed(b.getCsvUrl2Indexed());
		   }
		   if (b.getCsvUrl3Indexed() != bx.getCsvUrl3Indexed()) {
			   bx.setCsvUrl3Indexed(b.getCsvUrl3Indexed());
		   }
		   if (b.getCsvUrl4Indexed() != bx.getCsvUrl4Indexed()) {
			   bx.setCsvUrl4Indexed(b.getCsvUrl4Indexed());
		   }
		   if (b.getCsvUrl6Indexed() != bx.getCsvUrl6Indexed()) {
			   bx.setCsvUrl6Indexed(b.getCsvUrl6Indexed());
		   }
		   if (b.getCsvUrl1Completed() != bx.getCsvUrl1Completed()) {
			   bx.setCsvUrl1Completed(b.getCsvUrl1Completed());
		   }
		   if (b.getCsvUrl2Completed() != bx.getCsvUrl2Completed()) {
			   bx.setCsvUrl2Completed(b.getCsvUrl2Completed());
		   }
		   if (b.getCsvUrl3Completed() != bx.getCsvUrl3Completed()) {
			   bx.setCsvUrl3Completed(b.getCsvUrl3Completed());
		   }
		   if (b.getCsvUrl4Completed() != bx.getCsvUrl4Completed()) {
			   bx.setCsvUrl4Completed(b.getCsvUrl4Completed());
		   }
		   if (b.getCsvUrl6Completed() != bx.getCsvUrl6Completed()) {
			   bx.setCsvUrl6Completed(b.getCsvUrl6Completed());
		   }
		   em.getTransaction().commit();
		   result = true;
		 } 
		catch (Exception e){System.out.println("couldn't persist: " + e); e.printStackTrace();
			result = false;
		}
		finally {
		   em.close();
		   return result;
		}
	}
		
	public String createHUDXML3Blob(HUDXML3Blob b) {
		// returns "" if no blob added, otherwise, return's blob's id.
		String key = "";
		// ideally should perform an unduplication routine before adding the blob
		// for starters, we'll automatically add a new blob.
		EntityManager em =EMF.get().createEntityManager();
		try{
		   em.getTransaction().begin();
		   em.persist(b);
		   em.getTransaction().commit();
		   // get blob id created
		   key = b.getId();
		 } catch (Exception e){System.out.println("couldn't persist" + e);}
		 finally {
		   em.close();
		 }
		return key;
	}
}