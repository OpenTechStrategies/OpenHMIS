package org.pathways.openciss.model;

import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-04-15T14:40:16.363-0400")
@StaticMetamodel(PathClient.class)
public class PathClient_ {
	public static volatile SingularAttribute<PathClient, Integer> clientKey;
	public static volatile SingularAttribute<PathClient, Date> dateOfBirth;
	public static volatile SingularAttribute<PathClient, BigDecimal> dobType;
	public static volatile SingularAttribute<PathClient, BigDecimal> ethnicityCode;
	public static volatile SingularAttribute<PathClient, BigDecimal> genderCode;
	public static volatile SingularAttribute<PathClient, String> idType;
	public static volatile SingularAttribute<PathClient, Integer> inactive;
	public static volatile SingularAttribute<PathClient, String> nameFirst;
	public static volatile SingularAttribute<PathClient, String> nameFirstAlias;
	public static volatile SingularAttribute<PathClient, String> nameLast;
	public static volatile SingularAttribute<PathClient, String> nameLastAlias;
	public static volatile SingularAttribute<PathClient, String> nameMiddle;
	public static volatile SingularAttribute<PathClient, String> nameSuffix;
	public static volatile SingularAttribute<PathClient, BigDecimal> raceCode;
	public static volatile SingularAttribute<PathClient, Integer> ssn;
	public static volatile SingularAttribute<PathClient, Integer> ssnQuality;
	public static volatile SingularAttribute<PathClient, String> unaccompaniedChild;
	public static volatile SingularAttribute<PathClient, BigDecimal> veteranStatusCode;
}
