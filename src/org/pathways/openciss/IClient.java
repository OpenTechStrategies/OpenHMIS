package org.pathways.openciss;
import java.util.Date;

public interface IClient {
	
/* Search for client using any/all of personal identifiers (taken from Dave's mobile app mock-up):
 * 	first name
 * 	last name
 * 	middle initial
 * 	social security number (including partial SSNs with underscores for unknown numbers) 
 * 	gender
 * 	veteran 
 * 	race
 * 	ethnicity
 * 	age/date of birth
 * 	weightings of each factor affect search results?  what would the weightings be?
 * 		proposal: use HUD's technical guidelines on the subject, but use exact match at first until it's implemented
*/	
	
int[] clientSearch(String first_name, String last_name, String age, Date date_of_birth,
		int gender, int ethnicity, int race, String middle_initial, String SSN); 	
	// returns list of JSON client ids that probably or exactly match, parameters are HUD HMIS XML person type
	// let's begin with exact match, and maybe have that fail over to probabilistic

// Need to add a better get client method that returns a a HUD HMIS XML person type.
// look for the one in the code and make the name slightly different


/* Outreach - records an client outreach event
 * 	point of contact (just lat./long., which the app converts from an address?
 * 	contact date
 * 	engagement date
 * 	client id (obtained when the client was previously added)
*/

void recordOutreach(int client_id, double latitude, double longitude, Date contact_date, Date engagement_date);

/* Intake - records a client intake event
 * 	first name
 * 	last name
 * 	middle initial
 * 	social security number (including partial SSNs with underscores for unknown numbers) 
 * 	gender
 * 	veteran 
 * 	race
 * 	ethnicity
 * 	age/date of birth
 * 	photo of the client
*/

int addClient(int client_id, String first_name, String last_name, String age, String date_of_birth, String middle_initial, String SSN); 	
// returns client id that this person is now assigned to.  return value of -1 means client not added (for whatever reason)
// sent as POST data in HMIS XML format

int updateClient(int clientID, String first_name, String last_name, String age, String date_of_birth, String middle_initial, String SSN);
// returns 0 for success, -1 for failure, sent as POST data in HMIS XML format

int deleteClient(int client_id);
//returns 0 for success, -1 for failure, actually just inactivates the client record
// sent as url encoded integer argument

}
