CREATE TABLE `CLIENT_NONCASH_BENEFITS` (
  `NONCASH_KEY` bigint(20) NOT NULL AUTO_INCREMENT,
  `CLIENT_KEY` bigint(20) NOT NULL,
  `INFORMATION_DATE` date DEFAULT NULL,
  `ANY_SOURCE_GCT` int(11) DEFAULT NULL,
  `SNAP_GCT` int(11) DEFAULT NULL,
  `WIC_GCT` int(11) DEFAULT NULL,
  `TANF_CC_GCT` int(11) DEFAULT NULL,
  `TANF_TRAN_GCT` int(11) DEFAULT NULL,
  `TANF_OTHER_GCT` int(11) DEFAULT NULL,
  `ONGOING_RENT_ASSIST_GCT` int(11) DEFAULT NULL,
  `RENT_TEMP_GCT` int(11) DEFAULT NULL,
  `OTHER_SOURCE_GCT` int(11) DEFAULT NULL,
  `REC_ACTIVE_GCT` int(11) DEFAULT '1',
  `ENTRY_DATE_TIME` timestamp NULL DEFAULT NULL,
  `ENTRY_USER_KEY` bigint(20) DEFAULT NULL,
  `LOG_DATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `LOG_USER_KEY` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`NONCASH_KEY`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;