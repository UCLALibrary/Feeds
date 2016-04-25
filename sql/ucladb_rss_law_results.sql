--------------------------------------------------------
--  File created - Monday-April-25-2016   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Table UCLADB_RSS_LAW_RESULTS
--------------------------------------------------------

  CREATE TABLE "VGER_SUPPORT"."UCLADB_RSS_LAW_RESULTS" 
   (	"LOCATION" VARCHAR2(60 BYTE), 
	"LOCATION_CODE" VARCHAR2(10 BYTE), 
	"CALL_NO_TYPE" CHAR(1 BYTE), 
	"CALL_NUMBER" VARCHAR2(300 BYTE), 
	"SORT_CALL_NUMBER" VARCHAR2(300 BYTE), 
	"ADDED_DATE" DATE, 
	"BIB_ID" NUMBER(*,0), 
	"LANGUAGE_CODE" VARCHAR2(3 BYTE), 
	"LANGUAGE" VARCHAR2(100 BYTE), 
	"EDITION" NVARCHAR2(4000), 
	"IMPRINT" NVARCHAR2(4000), 
	"SERIES" NVARCHAR2(4000), 
	"AUTHOR" NVARCHAR2(4000), 
	"TITLE" NVARCHAR2(4000), 
	"PUB_YEAR" NUMBER, 
	"BIB_SUBJECTS" NVARCHAR2(4000), 
	"URL" NVARCHAR2(2000)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "VGER_SUPPORT" ;
