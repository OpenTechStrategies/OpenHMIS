package org.pathways.openciss.model;

import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-04-15T14:40:16.089-0400")
@StaticMetamodel(ClientOutreach.class)
public class ClientOutreach_ {
	public static volatile SingularAttribute<ClientOutreach, Integer> clientKey;
	public static volatile SingularAttribute<ClientOutreach, Date> contactDate;
	public static volatile SingularAttribute<ClientOutreach, Date> engagementDate;
	public static volatile SingularAttribute<ClientOutreach, BigDecimal> latitude;
	public static volatile SingularAttribute<ClientOutreach, BigDecimal> longitude;
	public static volatile SingularAttribute<ClientOutreach, Integer> outreachKey;
}
