package org.pathways.openciss.rest.impl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import au.com.bytecode.opencsv.CSVReader;
import com.google.appengine.api.rdbms.AppEngineDriver;
//import com.google.cloud.sql.jdbc.PreparedStatement;
//import java.util.logging.Logger;

@Path("/cron/new_jersey_shelter_poll")
//@Path("/test/new_jersey_shelter_poll")
public class NewJerseyShelterPoll {
    //private static final Logger log = Logger.getLogger("log1");

	@GET
	 public String fetchShelterList() {
		Connection dbconn = null;
		HttpURLConnection httpconn = null;
		System.out.println("inside /cron/new_jersey_shelter_poll");
		try {
			DriverManager.registerDriver(new AppEngineDriver());
			dbconn = DriverManager.getConnection("jdbc:google:rdbms://pcni.org:openhmis:openciss/compass");		
			try {
				URL url = new URL("http://hmis.njhmfaserv.org/lst/pol.php?id=24");
				httpconn = (HttpURLConnection) url.openConnection();
				httpconn.setRequestMethod("GET");
				//System.out.println("timeout is: " + httpconn.getReadTimeout());
				httpconn.setReadTimeout(0);
				//System.out.println("timeout is now: " + httpconn.getReadTimeout());
				httpconn.connect();
				InputStream in = httpconn.getInputStream();
				// before we go any further, delete the old data, now that we can at least connect to the new data store
				try {
					String statement = "DELETE FROM compass.program_profile_info WHERE feed_source=?";
					PreparedStatement pstmt = dbconn.prepareStatement(statement);
					pstmt.setString(1, "1");
					pstmt.executeUpdate();
					// set the auto-increment counter to the next highest unused index.  otherwise it with keep making a longer and longer gap in the indexes.
					String statement1 = "ALTER TABLE compass.program_profile_info AUTO_INCREMENT = 1";
					PreparedStatement pstmt1 = dbconn.prepareStatement(statement1);
					pstmt1.executeUpdate();
				} catch (Exception e) {e.printStackTrace();}
				CSVReader cSVReader = new CSVReader(new InputStreamReader(in), ',', '"', 1);
				String [] nextLine;
				while ((nextLine = cSVReader.readNext()) != null) {
					System.out.println("read from CSV url: " + nextLine[0] + "," + nextLine[1] + "," + "etc...");
					String statement = "insert into compass.program_profile_info " +
							"(agency_name, program_name, program_type_code, program_type, " +
							"target_pop_a_code, target_pop_a_name, units_total, units_occupied, " +
							"units_available, contact_name, contact_phone, program_address_full, feed_source, update_time_stamp) " +
							"values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
					PreparedStatement pstmt = dbconn.prepareStatement(statement);
					pstmt.setString(1, nextLine[0]);
					//System.out.println("agency name is: " + nextLine[0]);
					pstmt.setString(2, nextLine[1]);
					//System.out.println("program name is: " + nextLine[1]);
					String programTypeCode = translateProgramTypeCode(nextLine[2]);
					//System.out.println("program type code is: " + programTypeCode);
					pstmt.setString(3, programTypeCode);
					String programType = translateProgramType(nextLine[2]);
					//System.out.println("program type is: " + programType);
					pstmt.setString(4, programType);
					System.out.println("nextLine[3] is: " + nextLine[3]);  
					String targetPopulation = translateTargetPopulation(nextLine[3]);
					System.out.println("target population is: " + targetPopulation);
					pstmt.setString(5, targetPopulation);
					pstmt.setString(6, translateTargetPopulationCode(targetPopulation));
					// units total
					pstmt.setString(7, nextLine[5]);
					// units occupied
					pstmt.setString(8, nextLine[6]);
					// units available
					pstmt.setString(9, nextLine[7]);
					// contact/phone #/address
					String[] contactInfo = splitContactInfo(nextLine[8]);
					if (contactInfo.length == 1) {
						// contact
						pstmt.setString(10, contactInfo[0]);
						// phone
						pstmt.setString(11, null);
						// address
						pstmt.setString(12, null);
					}
					if (contactInfo.length == 2) {
						// contact
						pstmt.setString(10, null);
						// phone
						pstmt.setString(11, contactInfo[0]);
						// address
						pstmt.setString(12, contactInfo[1]);
					}
					if (contactInfo.length == 3) {
						// contact
						pstmt.setString(10, contactInfo[0]);
						// phone
						pstmt.setString(11, contactInfo[1]);
						// address
						pstmt.setString(12, contactInfo[2]);
					}
					else {
						// contact
						pstmt.setString(10, null);
						// phone
						pstmt.setString(11, null);
						// address
						pstmt.setString(12, null);
					}
					// data source code
					pstmt.setString(13, "1");
					pstmt.setTimestamp(14, new Timestamp(System.currentTimeMillis()));
					pstmt.executeUpdate();
				}
				System.out.println("While loop ended");
				httpconn.disconnect();
				cSVReader.close();
			} catch (Exception e) {e.printStackTrace();}
		} 
		catch (SQLException e) {
			e.printStackTrace();
		} 
		finally {
			if (dbconn != null) {
				try {
					dbconn.close();
				} 
				catch (SQLException ignore) {}
	        }
		}
		return "200";
	}
	
	String translateProgramType(String inputProgramType) {
		if (inputProgramType.equals("Transitional Housing")) return "TH"; 
		else if (inputProgramType.equals("Emergency Shelter")) return "ES";
		// 06-07-2012 ECJ: Compass uses the Target Pop to determine Fam/Ind, so we ignore Fam/Ind here
		else if (inputProgramType.equals("Transitional Housing Families")) return "TH";
		// 06-07-2012 ECJ: Compass uses the Target Pop to determine Fam/Ind, so we ignore Fam/Ind here
		else if (inputProgramType.equals("Emergency Shelter Families")) return "ES";
		else return null;
	}
	
	String translateProgramTypeCode(String inputProgramType) {
		if (inputProgramType.equals("Transitional Housing")) return "2"; 
		else if (inputProgramType.equals("Emergency Shelter")) return "1";
		// 06-07-2012 ECJ: Compass uses the Target Pop to determine Fam/Ind, so we ignore Fam/Ind here
		else if (inputProgramType.equals("Transitional Housing Families")) return "2";
		// 06-07-2012 ECJ: Compass uses the Target Pop to determine Fam/Ind, so we ignore Fam/Ind here
		else if (inputProgramType.equals("Emergency Shelter Families")) return "1";
		else return null;
	}
	
	String translateTargetPopulation(String inputTargetPopulation) {
		if (inputTargetPopulation.equals("SM=Single Males 18 years and older")) return "1";
		else if (inputTargetPopulation.equals("SF=Single Females 18 years and older")) return "2"; 
		else if (inputTargetPopulation.equals("SMF=Single Males and Females 18 years and older")) return "3";
		else if (inputTargetPopulation.equals("CO=Couples Only, No Children")) return "4";
		else if (inputTargetPopulation.equals("SM+HC=Single Males and Households with Children")) return "5";
		else if (inputTargetPopulation.equals("SF+HC=Single Females and Households with Children")) return "6";
		else if (inputTargetPopulation.equals("HC=Households with Children")) return "7";
		else if (inputTargetPopulation.equals("YM=Unaccompanied Young Males under 18")) return "8";
		else if (inputTargetPopulation.equals("YF=Unaccompanied Young Females under 18")) return "9";
		else if (inputTargetPopulation.equals("YMF=Unaccompanied Young Males and Females under 18")) return "10";
		else if (inputTargetPopulation.equals("SMF+HC=Single Male and Female and Households with Children")) return "11";
		else return null;
	}
	
	String translateTargetPopulationCode(String inputTargetPopulationCode) {
		if (inputTargetPopulationCode.equals("1")) return "SM";
		else if (inputTargetPopulationCode.equals("2")) return "SF";
		else if (inputTargetPopulationCode.equals("3")) return "SMF";
		else if (inputTargetPopulationCode.equals("4")) return "CO";
		else if (inputTargetPopulationCode.equals("5")) return "SM+HC";
		else if (inputTargetPopulationCode.equals("6")) return "SF+HC";
		else if (inputTargetPopulationCode.equals("7")) return "HC";
		else if (inputTargetPopulationCode.equals("8")) return "YM";
		else if (inputTargetPopulationCode.equals("9")) return "YF";
		else if (inputTargetPopulationCode.equals("10")) return "YMF";
		else if (inputTargetPopulationCode.equals("11")) return "SMF+HC";
		else return null;
	}
	
	String[] splitContactInfo(String contactInfo) {
		String[] results = new String[3];
		String[] prelim_results = contactInfo.split("::");
		if (prelim_results.length == 1) {
			// throw it all in contact
			results[0] = prelim_results[0];
		}
		else if (prelim_results.length == 2) {
			// assume it is just phone and address, which is usually the case
			// at the time of coding, the data is very inconsistent
			results[0] = "";
			results[1] = prelim_results[0];
			results[2] = prelim_results[1];
		}
		else if (prelim_results.length == 3) {
			// assume it is all three, which is usually the case
			// at the time of coding, the data is very inconsistent
			results[0] = prelim_results[0];
			results[1] = prelim_results[1];
			results[2] = prelim_results[2];
		}
		return results;
	}
}