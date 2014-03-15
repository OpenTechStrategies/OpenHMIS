package org.pathways.openciss.shared;

//import java.io.StringReader;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.pathways.openciss.model.PathClient;
import org.pathways.openciss.model.PathClient_;
import org.pathways.openciss.shared.EMF;

/*All quoted material from "Technical Guidelines for Unduplicating and De-identifying HMIS
	Client Records" - July 2005, by Brian Sokol 
*/

public class Match {
	private static final transient Logger log = LoggerFactory.getLogger(Match.class);
	static String firstName, firstNameAlias, lastName, lastNameAlias,	middleName, 
		dateOfBirth, gender, ssn, ssnQuality;
	private TreeMap<BigInteger, Integer> matchList = new TreeMap<BigInteger, Integer>();
	static int part4MatchCount = 0; //need to get to at least 2 or fail Part 4
	//public static EntityManager em; 
	static int clientKeySelected;
	
	public BigInteger[] find(String[] ids) {
		BigInteger[] matched_clients = new BigInteger[0];

		// expects String[First Name, Last Name, Middle Name, Date of Birth, Gender, SSN, SSN Quality]
		
		firstName = ids[0]; firstNameAlias = ids[1]; lastName = ids[2]; lastNameAlias = ids[3]; 
		middleName = ids[4]; dateOfBirth = ids[5]; gender = ids[6]; ssn = ids[7]; ssnQuality = ids[8];
		
		// remember to use case-insensitive match, or consistently store identifiers
		if (ssn != null) {
			ssn = ssn.replaceAll( "[^\\d]", "" );
		}
		/*if (ssn.length() !=9) {
			// perhaps in the future, we could use place specific SSNs, like ___-__-1111
			ssn = "";
		}*/
		
		/*"Prior to engaging in client record matching, the algorithm checks that enough data are
		present in the record to potentially find a match. Otherwise, the record is set aside and
		not used for unduplicated counts and analysis."*/
		
		/* Part 1
		 *   IF SSN Is Null And Two of (First Name/First Name Alias, Last Name/Last Name Alias, 
		 *     Date of Birth) Are Null, DISCARD RECORD
		 */

		log.info("performing match process for : firstName: " + firstName + " lastName: " + lastName + 
			" ssn: " + ssn + " dateOfBirth: " + dateOfBirth + " gender: " + gender);
		
		if (ssn  == null || ssn.isEmpty()) {
			if (!nonSSNPersonalIdentifierSearch()) {
				// Part 2 is implemented by virtue of returning a null match, if no SSN and not enough non-SSN personal identifiers
				return matched_clients;
			}
		 

		} else {//we have an ssn, so go straight to part #3
			//boolean recordsAdded;
			part3AddMatches();
		}
		
		
		/* -- Part 3 
		 * Where existing records match the SSN of new record AND
		 *  • Neither Data Quality Code indicates Partial SSN
		 * OR
		 * -- Part 4
		 * Existing records match new record on at least 2 of the following elements:
		 * • First Name/First Name Alias (match first name and all first name aliases of
		 *      client against all first names and first name aliases in database);
		 * • Last Name/Last Name Alias (match last name and all last name aliases of
		 *      client against all last names and last name aliases in database);
		 * • Date of Birth
		 * • SSN where Data Quality Code indicates partial SSN
		 */
		
		
		//Not implemented, but useful for an unduplication algorithm:
		/*"The core approach to record matching has two steps. The first major step is to find
		potential matches. The second major step is to attempt to falsify or disprove the matches."*/	

		/*"the algorithm checks to see how many matches were found: zero, one, or more than 
		 * one."*/
		
		/*"If zero matches are found, then the record is considered new. If one match is found, 
		 * then the record is assumed to represent the same individual as the matched record. 
		 * If multiple matches are found, the records are flagged for human intervention."
		 */		
		int s = matchList.size();
		if (s == 0) {return matched_clients;}
		else {//if (s == 1||s > 1) 
			// populate the return array
			int m = 0;
			matched_clients = new BigInteger[matchList.size()];
			for (BigInteger i : matchList.keySet()) {
			// probably just change this to a keyset, so don't have to get all the entry values
				matched_clients[m++] = i;
			}
			return matched_clients;
		}
	}
	
	@SuppressWarnings("unchecked")
	private boolean part3AddMatches() {
		EntityManager em;
		boolean recordsAdded = false;
		em =EMF.get().createEntityManager();
		/* Part 3 "Where existing records match the SSN of new record AND
		 *   • Neither Data Quality Code indicates Partial SSN"*/
		
		// Adding records for Part 3
		
		// Find potential matches
		List<Integer> results = null;		
		Query q = em.createQuery (
			"SELECT pc.clientKey FROM PathClient AS pc WHERE pc.ssn =  :ssn AND pc.inactive = '0'");
		if (ssn != null && !ssn.isEmpty()) {
			q.setParameter ("ssn", Integer.parseInt(ssn));
			//@SuppressWarnings("unchecked")
			results = q.getResultList();
		}

		if (results.isEmpty()) {
			log.info("no ssn match results (Part 3), so go back and see if there are enough of the other non-SSN identifiers.");
			if (!nonSSNPersonalIdentifierSearch()) {
			} else 	recordsAdded = true;
		}
		else {
			// deal with each SSN match sequentially and collect them in the matchList
			Iterator<Integer> i = results.iterator( );
			while (i.hasNext( )) {
				Integer clientKey = i.next();
				log.info("part 3 ssn match for client key: " + clientKey);
				Query q2 = em.createQuery ("SELECT pc.ssnQuality FROM PathClient AS pc WHERE " +
					"pc.clientKey =  :client_key");
				q2.setParameter ("client_key", clientKey);
				int result = (Integer) q2.getSingleResult();
				
				// Adding/Subtracting records for Part #3
				// also if the stored SSN Quality is partial, part 3 fails
				if (result == 2) { // 2 means a partial ssn was encountered
					//System.out.println("partial ssn quality code encountered for client_key: " + clientKey);
					// Part 3 is false;
				} else {
					//System.out.println("quality code is not empty and the contents are: " + result);
					// if the incoming SSN Quality is Partial, part 3 automatically fails
					//log.info("incoming ssnQuality is:" + ssnQuality + ".");
					if (ssnQuality !=null && !(ssnQuality.isEmpty())) {
						if (Integer.valueOf(ssnQuality) == 2) {
							// Part 3 is false;
						} else {
						// Part 3 is  true; both incoming and stored SSN Quality are not partial
						// add this record, since Part 3 is true
							if (!matchList.containsKey(BigInteger.valueOf(clientKey))) {
							matchList.put(BigInteger.valueOf(clientKey), 0); // so if the value is 0 (from part 3) or >=2 (from part 4, it will pass), 
							recordsAdded = true;
							}
						}
					// We'll allow a match if the SSN matches, but empty SSN Quality Code in search JSON string
					} else if (ssnQuality.isEmpty()) { 
						if (!matchList.containsKey(BigInteger.valueOf(clientKey))) {
							matchList.put(BigInteger.valueOf(clientKey), 0); // so if the value is 0 (from part 3) or >=2 (from part 4, it will pass), 
							recordsAdded = true;
						}
					} 
				} 
			}
		}
		return recordsAdded;
	}
	
	private void part4AddMatches() {
	// Adding records for Part #4
		
		// we should start at with any one identifier and get matches, then check the other 
		//    identifiers for at least two matches on those same records.  So we'll use a method that checks for 
		//    all four identifier matches.
		int score;
		List<Integer>firstNameAndAliasResults = matchFirstNameAndAlias();
		if (firstNameAndAliasResults !=null) {
			for (Integer result : firstNameAndAliasResults) {
				score = part4CompareAllIdentifiers(result);
				if (score >= 2) {
					if (!matchList.containsKey(BigInteger.valueOf(result))) {
						matchList.put(BigInteger.valueOf(result), score);
					}
				}
			}
		}
		List<Integer>lastNameAndAliasResults = matchLastNameAndAlias();
		if (lastNameAndAliasResults != null) {
			for (Integer result2 : lastNameAndAliasResults) {
				score = part4CompareAllIdentifiers(result2);
				if (score >= 2) {
					if (!matchList.containsKey(BigInteger.valueOf(result2))) {
						matchList.put(BigInteger.valueOf(result2), score);
					}
				}
			}
		}
		List<Integer>dateOfBirthResults = matchDateOfBirth();
		if (dateOfBirthResults != null) {
			for (Integer result3 : dateOfBirthResults) {
				score = part4CompareAllIdentifiers(result3);
				if (score >= 2) {
					if (!matchList.containsKey(BigInteger.valueOf(result3))) {
						matchList.put(BigInteger.valueOf(result3), score);
					}
				}
			}		
		}
		// so if the value is 0 (from part 3) or >=2 (from part 4), it will pass

	}

		//*• SSN where Data Quality Code indicates partial SSN  *Not implemented*
			//Query q2 = em.createQuery ("SELECT pc.clientKey FROM PathClient AS pc WHERE " +
				//"(pc.ssn =  :ssn AND pc.ssnQuality = '2')");
				// Note: This *should* implement SSN placeholders like "_". 
		
		// Finish Part 4 by counting if at least 2 matches were obtained for each key.
		// The value will be either 1 or greater.
		// If the value is '1', remove from the list, because it didn't get enough part 4 points
		// If the value is '0', leave it in the list, because it passed a part 3 test
		
//		for (Iterator<Map.Entry<BigInteger,Integer>> i = matchList.entrySet().iterator(); i.hasNext();){
//			 Map.Entry<BigInteger, Integer> entry = i.next();
//			 if (entry.getValue().equals(1)) {
//				 //Integer o = entry.getKey();  
//			     i.remove();  
//			     //System.out.println("because not enough part 4 points, removing client id: " + o);

	
	@SuppressWarnings("rawtypes")
	static int part4CompareAllIdentifiers(Integer result3) {
		int score = 0;
		//List<String> results;
		//check against first name/alias
		EntityManager em;
		em =EMF.get().createEntityManager();
		Query q = em.createQuery ("SELECT pc.nameFirst, pc.nameFirstAlias FROM PathClient AS pc WHERE " +
			"(pc.clientKey =  :client_key)");
		q.setParameter ("client_key", result3);
		List results = q.getResultList();
		Object[] r = (Object[])results.get(0);
		String[] s = Arrays.copyOf(r, r.length, String[].class);
		//log.info("results.get(0)]; is: " + s);
		if (s[0] != null) {
			if (s[0].equals(firstName)) {
				score++;
			}
		} else if (s[1] != null) {
			  if (s[1].equals(firstNameAlias)) {
				  score++;
			  }
		}
		
		//check against last name/alias
		Query q1 = em.createQuery ("SELECT pc.nameLast, pc.nameLastAlias FROM PathClient AS pc WHERE " +
			"(pc.clientKey =  :client_key)");
		q1.setParameter ("client_key", result3);
		List results1 = q1.getResultList();
		Object[] r1 = (Object[])results1.get(0);
		String[] s1 = Arrays.copyOf(r1, r1.length, String[].class);
		if (s1[0] != null) {
			if (s1[0].equals(lastName)) {
				score++;
			}
		} else if (s[1] != null) {
			if (s[1].equals(lastNameAlias)) {
				score++;
			}
		}
		//check against dob
		Query q2 = em.createQuery ("SELECT pc.dateOfBirth FROM PathClient AS pc WHERE " +
				"(pc.clientKey =  :client_key)");
		q2.setParameter ("client_key", result3);
		List results2 = q2.getResultList();
		Date s2 = (Date) results2.get(0);
		//Object[] r2 = (Object[])results2.get(0);
		//Date[] s2 = Arrays.copyOf(r2, r2.length, Date[].class);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date dob = null;
		//System.out.println("trying to format: " + dateOfBirth);
		if (dateOfBirth != null) {
			try {
				dob = df.parse(dateOfBirth);
			} catch (ParseException e) {
				log.info("couldn't parse the date; it could just be empty: " + e.toString());
			}
		}
		//System.out.println("formatted string is: " + dob.toString());
		if (s2.equals(dob)) {
			score++;
		}
		//check against partial ssn (not implemented yet)
		return score;
	}
	
	List<Integer> matchFirstNameAndAlias() {
		EntityManager em;
		em =EMF.get().createEntityManager();

		// "• First Name/First Name Alias (match first name and all first name aliases of
			//    client against all first names and first name aliases in database);"
			// Note: the system does not yet accomodate multiple aliases; just one alias
		
		
		ArrayList<Integer> results = new ArrayList<Integer>();
		if (firstName == null || firstName.isEmpty()) {
			firstName = "";
		}
		if (firstNameAlias == null || firstNameAlias.isEmpty()) {
			firstNameAlias = "";
		}
		//I don't agree with both the name AND alias needing to match.  
		//Should be one or the other.  Matches on aliases alone would be rejected otherwise
		if (firstName != null || !firstName.isEmpty()) {
//			Query q = em.createQuery ("SELECT pc.clientKey FROM PathClient AS pc WHERE " +
//				"(pc.nameFirst =  :name_first)");
//			q.setParameter ("name_first", firstName);
			
			try {
				CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
				//CriteriaQuery<PathClient> q = cb.createQuery(PathClient.class);
				CriteriaQuery<PathClient> criteriaQuery = criteriaBuilder.createQuery(PathClient.class);
				Root<PathClient> clients = criteriaQuery.from(PathClient.class);
				criteriaQuery.select(clients);
				Predicate predicate = criteriaBuilder.equal(clients.get(PathClient_.nameFirst), firstName);
				criteriaQuery.where(predicate);
				TypedQuery<PathClient> typedQuery = em.createQuery(criteriaQuery);
				List<PathClient> resultList = typedQuery.getResultList();
				for (PathClient i : resultList) {
					results.add(i.getClientKey());
					log.info("adding: " + (i).getNameFirst() + " , " + (i).getNameLast());
				}
			} catch (Exception e) {log.info(e.toString() + e.getMessage());}
			
//			log.info("String of first result:" + String.valueOf(resultList.get(0)));
//			if (firstNameAlias != null || !firstNameAlias.isEmpty()) {
//				Query q1 = em.createQuery ("SELECT pc.clientKey FROM PathClient AS pc WHERE " +
//					"(pc.nameFirstAlias =  :name_first_alias)");
//				q1.setParameter ("name_first_alias", firstNameAlias);
//				List<Long>results1 = q1.getResultList();
//				results.addAll(results1);
//			}
//		}
		}
		return results;
	}
	
	@SuppressWarnings("unchecked")
	static List<Integer> matchLastNameAndAlias() {
		EntityManager em;
		em =EMF.get().createEntityManager();
		// • Last Name/Last Name Alias (match first name and all last name aliases of
		//    client against all last names and last name aliases in database);	
		List<Integer> results = null;
		if (lastName == null || lastName.isEmpty()) {
			lastName = "";
		}
		if (lastNameAlias == null || lastName.isEmpty()) {
			lastNameAlias = "";
		}
		if (lastName != null || !lastName.isEmpty()) {
			Query q = em.createQuery ("SELECT pc.clientKey FROM PathClient AS pc WHERE " +
				"(pc.nameLast =  :name_last)");
			q.setParameter ("name_last", lastName);
			results = q.getResultList();
			Query q1 = em.createQuery ("SELECT pc.clientKey FROM PathClient AS pc WHERE " +
				"(pc.nameLastAlias =  :name_last_alias)");
			q1.setParameter ("name_last_alias", lastNameAlias);
			List<Integer>results1 = q.getResultList();
			results.addAll(results1);
		}
		return results;
	}
	@SuppressWarnings("unchecked")
	List<Integer> matchDateOfBirth() {
		EntityManager em;

		em =EMF.get().createEntityManager();
		List<Integer> results = null;
		if (dateOfBirth != null && !dateOfBirth.isEmpty()) {
			Query q = em.createQuery ("SELECT pc.clientKey FROM PathClient AS pc WHERE " +
				"pc.dateOfBirth =  :date_of_birth");
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			Date dob = null;
			//System.out.println("trying to format: " + dateOfBirth);
			try {
				dob = df.parse(dateOfBirth);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			//System.out.println("formatted string is: " + dob.toString());
			q.setParameter ("date_of_birth", dob); 
			//System.out.println("dob query to string:" + q6.toString());
			log.info("date searching for (passed from JSON) is: " + dob);
			//
			results = q.getResultList();
			//if (results.isEmpty()) {
				//log.info("DOB part 4 matches are empty;");
			//}
		}
		return results;
	}
	
	boolean nonSSNPersonalIdentifierSearch() {
		boolean b = false;
		int nullCount = 0;
		if ( firstName == null || firstName.isEmpty()) {if ( firstNameAlias == null || firstNameAlias.isEmpty()) {nullCount++;}}
		if (lastName == null || lastName.isEmpty()) {if (lastNameAlias == null || lastNameAlias.isEmpty()) {nullCount++;}}
		if (dateOfBirth == null || dateOfBirth.isEmpty()) {nullCount++;}
		if (nullCount >=2) {
			// returning early is akin to Part 1 discarding the record
			//System.out.println("Part 1 discarding of record.");
			return b;
		} else if (nullCount ==1 || nullCount == 0) {
			part4AddMatches();
			b = true;
			return b;
		} else {return b;}
	}
}