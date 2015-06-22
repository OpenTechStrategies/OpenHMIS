CREATE TABLE `CLIENT_DISABILITIES` (
  `DISABILITIES_KEY` bigint(20) NOT NULL AUTO_INCREMENT,
  `CLIENT_KEY` bigint(20) NOT NULL,
  `INFORMATION_DATE` date DEFAULT NULL,
  `PHYSICAL_GCT` int(11) DEFAULT NULL,
  `PHYSICAL_RECV_TREAT_GCT` int(11) DEFAULT NULL,
  `DEVELOPMENTAL_GCT` int(11) DEFAULT NULL,
  `DEVELOPMENTAL_RECV_TREAT_GCT` int(11) DEFAULT NULL,
  `CHRONIC_HEALTH_COND_GCT` int(11) DEFAULT NULL,
  `CHRONIC_RECV_TREAT_GCT` int(11) DEFAULT NULL,
  `HIVAIDS_GCT` int(11) DEFAULT NULL,
  `HIVAIDS_RECV_TREAT_GCT` int(11) DEFAULT NULL,
  `MENTAL_HEALTH_GCT` int(11) DEFAULT NULL,
  `MENTAL_HEALTH_LONG_GCT` int(11) DEFAULT NULL,
  `MENTAL_HLTH_RECV_TREAT_GCT` int(11) DEFAULT NULL,
  `SUBSTANCE_ABUSE_CODE` int(11) DEFAULT NULL,
  `SUBSTANCE_ABUSE_LONG_GCT` int(11) DEFAULT NULL,
  `SUBST_ABUSE_RECV_TREAT_GCT` int(11) DEFAULT NULL,
  `DOMES_VIOLENCE_GCT` int(11) DEFAULT NULL,
  `DOMES_VIOL_WHEN_CODE` int(11) DEFAULT NULL,
  `REC_ACTIVE_GCT` int(11) DEFAULT '1',
  `ENTRY_DATE_TIME` timestamp NULL DEFAULT NULL,
  `ENTRY_USER_KEY` bigint(20) DEFAULT NULL,
  `LOG_DATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `LOG_USER_KEY` bigint(20) DEFAULT NULL,
  `DOCUMENT_ON_FILE` varchar(20) DEFAULT NULL,
  `HOW_CONFIRMED` int(11) DEFAULT NULL,
  `SMI_HOW_CONFIRM` int(11) DEFAULT NULL,
  `ACT_DATE_TIME` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`DISABILITIES_KEY`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8;