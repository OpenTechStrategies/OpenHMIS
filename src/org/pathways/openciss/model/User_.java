package org.pathways.openciss.model;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-04-15T14:40:16.821-0400")
@StaticMetamodel(User.class)
public class User_ {
	public static volatile SingularAttribute<User, Date> createDate;
	public static volatile SingularAttribute<User, Integer> createUserKey;
	public static volatile SingularAttribute<User, Byte> inactive;
	public static volatile SingularAttribute<User, String> nameFirst;
	public static volatile SingularAttribute<User, String> nameLast;
	public static volatile SingularAttribute<User, String> passwordEnc;
	public static volatile SingularAttribute<User, Date> updateDate;
	public static volatile SingularAttribute<User, Integer> updateUserKey;
	public static volatile SingularAttribute<User, Integer> userKey;
	public static volatile SingularAttribute<User, String> username;
}
