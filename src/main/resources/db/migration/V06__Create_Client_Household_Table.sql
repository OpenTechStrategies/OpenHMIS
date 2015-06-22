CREATE TABLE `CLIENT_HOUSEHOLD` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `HOUSEHOLD_KEY` bigint(20) NOT NULL,
  `CLIENT_KEY` bigint(20) NOT NULL,
  `ACTIVE_IN_HOUSEHOLD_GCT` int(11) DEFAULT NULL,
  `RELATIONSHIP_CODE` int(11) DEFAULT NULL,
  `DATE_ENTERED` timestamp NULL DEFAULT NULL,
  `DATE_LEFT` timestamp NULL DEFAULT NULL,
  `REC_ACTIVE_GCT` int(11) DEFAULT '1',
  `ENTRY_DATE_TIME` timestamp NULL DEFAULT NULL,
  `ENTRY_USER_KEY` bigint(20) DEFAULT NULL,
  `LOG_DATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `LOG_USER_KEY` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `CH_HK_IDX` (`HOUSEHOLD_KEY`),
  KEY `CH_CK_IDX` (`CLIENT_KEY`),
  KEY `CH_ACTIVE_HH_IDX` (`ACTIVE_IN_HOUSEHOLD_GCT`),
  KEY `CH_RC_IDX` (`RELATIONSHIP_CODE`),
  KEY `CH_DATE_ENTERED_IDX` (`DATE_ENTERED`),
  KEY `CH_DATE_LEFT_IDX` (`DATE_LEFT`),
  KEY `CH_REC_ACTIVE_IDX` (`REC_ACTIVE_GCT`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8;