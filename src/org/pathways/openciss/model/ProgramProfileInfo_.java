package org.pathways.openciss.model;

import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-04-15T14:40:16.454-0400")
@StaticMetamodel(ProgramProfileInfo.class)
public class ProgramProfileInfo_ {
	public static volatile SingularAttribute<ProgramProfileInfo, Integer> agencyKey;
	public static volatile SingularAttribute<ProgramProfileInfo, String> agencyName;
	public static volatile SingularAttribute<ProgramProfileInfo, BigDecimal> cocKey;
	public static volatile SingularAttribute<ProgramProfileInfo, String> cocName;
	public static volatile SingularAttribute<ProgramProfileInfo, String> contactName;
	public static volatile SingularAttribute<ProgramProfileInfo, String> contactPhone;
	public static volatile SingularAttribute<ProgramProfileInfo, Integer> feedSource;
	public static volatile SingularAttribute<ProgramProfileInfo, String> hmisParticipant;
	public static volatile SingularAttribute<ProgramProfileInfo, BigDecimal> maxStayDays;
	public static volatile SingularAttribute<ProgramProfileInfo, String> mckinneyVento;
	public static volatile SingularAttribute<ProgramProfileInfo, String> programAddress;
	public static volatile SingularAttribute<ProgramProfileInfo, String> programAddressFull;
	public static volatile SingularAttribute<ProgramProfileInfo, String> programCity;
	public static volatile SingularAttribute<ProgramProfileInfo, Integer> programKey;
	public static volatile SingularAttribute<ProgramProfileInfo, String> programName;
	public static volatile SingularAttribute<ProgramProfileInfo, String> programType;
	public static volatile SingularAttribute<ProgramProfileInfo, BigDecimal> programTypeCode;
	public static volatile SingularAttribute<ProgramProfileInfo, String> programZip;
	public static volatile SingularAttribute<ProgramProfileInfo, String> siteGeocode;
	public static volatile SingularAttribute<ProgramProfileInfo, BigDecimal> siteKey;
	public static volatile SingularAttribute<ProgramProfileInfo, String> siteName;
	public static volatile SingularAttribute<ProgramProfileInfo, BigDecimal> targetPopACode;
	public static volatile SingularAttribute<ProgramProfileInfo, String> targetPopAName;
	public static volatile SingularAttribute<ProgramProfileInfo, BigDecimal> targetPopBCode;
	public static volatile SingularAttribute<ProgramProfileInfo, String> targetPopBName;
	public static volatile SingularAttribute<ProgramProfileInfo, Integer> unitsAvailable;
	public static volatile SingularAttribute<ProgramProfileInfo, Integer> unitsOccupied;
	public static volatile SingularAttribute<ProgramProfileInfo, Integer> unitsTotal;
	public static volatile SingularAttribute<ProgramProfileInfo, Date> updateTimeStamp;
}
