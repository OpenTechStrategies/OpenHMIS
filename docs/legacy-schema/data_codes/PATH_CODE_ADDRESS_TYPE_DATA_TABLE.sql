REM INSERTING into PATH_CODE_ADDRESS_TYPE
SET DEFINE OFF;
Insert into PATH_CODE_ADDRESS_TYPE (CODE_KEY,STATUS,DESCRIPTION,UPDATE_TIMESTAMP,MIGRATION_FLAG,SORT_ORDER) values (1,'A','Full Address Reported',null,null,1);
Insert into PATH_CODE_ADDRESS_TYPE (CODE_KEY,STATUS,DESCRIPTION,UPDATE_TIMESTAMP,MIGRATION_FLAG,SORT_ORDER) values (2,'A','Partial Address Reported',null,null,2);
Insert into PATH_CODE_ADDRESS_TYPE (CODE_KEY,STATUS,DESCRIPTION,UPDATE_TIMESTAMP,MIGRATION_FLAG,SORT_ORDER) values (3,'A','Client doesn''t know',null,null,3);
Insert into PATH_CODE_ADDRESS_TYPE (CODE_KEY,STATUS,DESCRIPTION,UPDATE_TIMESTAMP,MIGRATION_FLAG,SORT_ORDER) values (4,'A','Client refused',null,null,4);
Insert into PATH_CODE_ADDRESS_TYPE (CODE_KEY,STATUS,DESCRIPTION,UPDATE_TIMESTAMP,MIGRATION_FLAG,SORT_ORDER) values (99,'A','Data not collected',null,null,5);