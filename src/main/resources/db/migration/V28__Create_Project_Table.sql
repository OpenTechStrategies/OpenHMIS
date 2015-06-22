CREATE TABLE `PROJECT` (
  `PROJECT_KEY` bigint(20) NOT NULL AUTO_INCREMENT,
  `PROJECT_NAME` varchar(600) DEFAULT NULL,
  `AGENCY_KEY` bigint(20) DEFAULT NULL,
  `COC_GROUP_KEY` int(11) DEFAULT NULL,
  `PRIMARY_SITE_KEY` bigint(20) DEFAULT NULL,
  `MAX_STAY_DAYS` int(11) DEFAULT NULL,
  `PROJECT_TYPE_CODE` int(11) DEFAULT NULL,
  `TARGET_POP_A_CODE` int(11) DEFAULT NULL,
  `TARGET_POP_B_CODE` int(11) DEFAULT NULL,
  `REC_ACTIVE_GCT` int(11) DEFAULT '1',
  `ENTRY_DATE_TIME` timestamp NULL DEFAULT NULL,
  `ENTRY_USER_KEY` bigint(20) DEFAULT NULL,
  `LOG_DATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `LOG_USER_KEY` bigint(20) DEFAULT NULL,
  `ACT_DATE_TIME` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `CONTINUUMPROJECT` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`PROJECT_KEY`),
  KEY `PROJECT_AK_IDX` (`AGENCY_KEY`),
  KEY `PROJECT_TYPE_CODE_IDX` (`PROJECT_TYPE_CODE`),
  KEY `PROJECT_REC_ACTIVE_IDX` (`REC_ACTIVE_GCT`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;