package org.pathways.openciss.model;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-04-15T14:40:16.373-0400")
@StaticMetamodel(Permission.class)
public class Permission_ {
	public static volatile SingularAttribute<Permission, Date> createDate;
	public static volatile SingularAttribute<Permission, Integer> createUserKey;
	public static volatile SingularAttribute<Permission, Byte> inactive;
	public static volatile SingularAttribute<Permission, Integer> permissionKey;
	public static volatile SingularAttribute<Permission, String> permissionName;
	public static volatile SingularAttribute<Permission, Integer> roleKey;
	public static volatile SingularAttribute<Permission, Date> updateDate;
	public static volatile SingularAttribute<Permission, Integer> updateUserKey;
}
