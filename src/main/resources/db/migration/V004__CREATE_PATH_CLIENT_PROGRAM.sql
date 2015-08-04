CREATE TABLE `PATH_CLIENT_PROGRAM` (
	`PROGRAM_KEY` INT PRIMARY KEY AUTO_INCREMENT, 
	`CLIENT_KEY` INT, 
	`ENTRY_DATE` DATE, 
	`ENTRY_CASH_GK` INT, 
	`ENTRY_NONCASH_GK` INT, 
	`EXIT_DATE` DATE, 
	`EXIT_CASH_GK` INT, 
	`EXIT_NONCASH_GK` INT, 
	`PROGRAM_NAME_KEY` INT, 
	`DESTINATION_KEY` INT, 
	`REASON_LEAVING_KEY` INT, 
	`CREATE_DATE` DATE, 
	`CREATE_USER_KEY` INT, 
	`UPDATE_DATE` DATE, 
	`UPDATE_USER_KEY` INT, 
	`STATUS` CHAR(1), 
	`TENURE_KEY` INT, 
	`SUBSIDY_KEY` INT, 
	`REC_ACTIVE` CHAR, 
	`SUBSIDY_OTHERS` VARCHAR(50), 
	`REASON_LEAVING_OTHERS` VARCHAR(50), 
	`DESTINATION_OTHERS` VARCHAR(50), 
	`HOUSING_STATUS_KEY` INT, 
	`COUNTYNAME` VARCHAR(64), 
	`ENGAGEMENT_DATE` DATE, 
	`BARCODE_PROFILE_KEY` INT, 
	`UPDATE_TIMESTAMP` TIMESTAMP, 
	`MIGRATION_FLAG` CHAR(2), 
	`ANNUAL_CASH_GK` INT, 
	`ANNUAL_NONCASH_GK` INT
   ) ;