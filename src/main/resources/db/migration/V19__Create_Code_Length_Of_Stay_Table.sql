CREATE TABLE `CODE_LENGTH_OF_STAY` (
  `CODE_KEY` int(11) NOT NULL,
  `DESCRIPTION` varchar(200) DEFAULT NULL,
  `SHORT_DESC` varchar(200) DEFAULT NULL,
  `NOTES` varchar(200) DEFAULT NULL,
  `REC_ACTIVE_GCT` int(11) DEFAULT '1',
  `SORT_ORDER` int(11) DEFAULT NULL,
  `LOG_DATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `LOG_USER_KEY` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`CODE_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;