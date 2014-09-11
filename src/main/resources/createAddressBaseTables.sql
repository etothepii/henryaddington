USE frederickNorth;
DROP TABLE IF EXISTS DeliveryPointAddress;
CREATE TABLE DeliveryPointAddress (
    RECORD_IDENTIFIER int (2) NOT NULL,
    CHANGE_TYPE VARCHAR (1) NOT NULL,
    PRO_ORDER BIGINT (16) NOT NULL,
    UPRN BIGINT (12) NOT NULL PRIMARY KEY,
    PARENT_ADDRESSABLE_UPRN BIGINT (12) NULL,
    RM_UDPRN int (8) NOT NULL,
    ORGANISATION_NAME VARCHAR (60) NULL,
    DEPARTMENT_NAME VARCHAR (60) NULL,
    SUB_BUILDING_NAME VARCHAR (30) NULL,
    BUILDING_NAME VARCHAR (50) NULL,
    BUILDING_NUMBER int (4) NULL,
    DEPENDENT_THOROUGHFARE_NAME VARCHAR (80) NULL,
    THOROUGHFARE_NAME VARCHAR (80) NULL,
    DOUBLE_DEPENDENT_LOCALITY VARCHAR (35) NULL,
    DEPENDENT_LOCALITY VARCHAR (35) NULL,
    POST_TOWN VARCHAR (30) NOT NULL,
    POSTCODE VARCHAR (8) NOT NULL ,
    POSTCODE_TYPE VARCHAR (1) NOT NULL,
    WELSH_DEPENDENT_THOROUGHFARE_NAME VARCHAR (80) NULL,
    WELSH_THOROUGHFARE_NAME VARCHAR (80) NULL,
    WELSH_DOUBLE_DEPENDENT_LOCALITY VARCHAR (35) NULL,
    WELSH_DEPENDENT_LOCALITY VARCHAR (35) NULL,
    WELSH_POST_TOWN VARCHAR (30) NULL,
    PO_BOX_NUMBER VARCHAR (6) NULL,
    PROCESS_DATE date NOT NULL,
    START_DATE date NOT NULL,
    END_DATE date NULL,
    LAST_UPDATE_DATE date NOT NULL,
    ENTRY_DATE date NOT NULL,
    INDEX `PARENT_ADDRESSABLE_UPRN` (`PARENT_ADDRESSABLE_UPRN`),
    INDEX `POSTCODE` (`POSTCODE`));
DROP TABLE IF EXISTS BLPU;
CREATE TABLE BLPU (
    RECORD_IDENTIFIER int (2) NOT NULL,
    CHANGE_TYPE VARCHAR (1) NOT NULL,
    PRO_ORDER BIGINT (16) NOT NULL,
    UPRN BIGINT (12) NOT NULL PRIMARY KEY,
    LOGICAL_STATUS int (1) NOT NULL,
    BLPU_STATE int (1) NULL,
    BLPU_STATE_DATE date NULL,
    PARENT_UPRN BIGINT (12) NULL,
    X_COORDINATE float (8,2) NOT NULL,
    Y_COORDINATE float (9,2) NOT NULL,
    RPC int (1) NOT NULL,
    LOCAL_CUSTODIAN_CODE int (4) NOT NULL,
    START_DATE date NOT NULL,
    END_DATE date NULL,
    LAST_UPDATE_DATE date NOT NULL,
    ENTRY_DATE date NOT NULL,
    POSTAL_ADDRESS VARCHAR (1) NOT NULL,
    POSTCODE_LOCATOR VARCHAR (8) NOT NULL,
    MULTI_OCC_COUNT int (4) NOT NULL,
    INDEX `X_COORDINATE` (`X_COORDINATE`),
    INDEX `Y_COORDINATE` (`Y_COORDINATE`));
DROP TABLE IF EXISTS Route;
CREATE TABLE Route (
    ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    NAME VARCHAR(64) NOT NULL,
    MIN_X float (8,2) NULL,
    MIN_Y float (9,2) NULL,
    MAX_X float (8,2) NULL,
    MAX_Y float (9,2) NULL,
    DELIVERED_BY INT (8) NULL,
    INDEX `MIN_X` (`MIN_X`),
    INDEX `MIN_Y` (`MIN_Y`),
    INDEX `MAX_X` (`MAX_X`),
    INDEX `MAX_Y` (`MAX_Y`),
    INDEX `DELIVERED_BY` (`DELIVERED_BY`));
DROP TABLE IF EXISTS RouteMember;
CREATE TABLE RouteMember (
    ROUTE_ID INT (8) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    LARN CHAR(12) NOT NULL,
    INDEX `ROUTE_ID` (`ROUTE_ID`),
    INDEX `LARN` (`LARN`));
DROP TABLE IF EXISTS Person;
CREATE TABLE Person (
    ID INT (8) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    SURNAME VARCHAR(64) NULL,
    OTHER_NAMES VARCHAR(64) NULL,
    TITLE VARCHAR (8) NULL,
    MAIN_CONTACT_NUMBER VARCHAR (11) NULL,
    DELIVERY_ADDRESS TINYTEXT NULL);
DROP TABLE IF EXISTS Dwelling;
CREATE TABLE Dwelling (
    VOA_ADDRESS VARCHAR(255) NOT NULL,
    POSTCODE CHAR(10) NOT NULL,
    COUNCIL_TAX_BAND CHAR(1) NOT NULL,
    LARN CHAR(30),
    ACCESSIBILITY CHAR(1) NULL,
    UPRN BIGINT(12) NULL,
    X_COORDINATE_OVERRIDE float (8,2) NULL,
    Y_COORDINATE_OVERRIDE float (9,2) NULL,
    INDEX `ACCESSIBILITY` (`ACCESSIBILITY`),
    INDEX `POSTCODE` (`POSTCODE`),
    INDEX `COUNCIL_TAX_BAND` (`COUNCIL_TAX_BAND`),
    INDEX `LARN` (`LARN`),
    INDEX `UPRN` (`UPRN`));
DROP TABLE IF EXISTS Postcode;
CREATE TABLE Postcode (
    POSTCODE CHAR(10) PRIMARY KEY,
    ACCURACY INT (6) NOT NULL,
    X_COORDINATE float (8,2) NOT NULL,
    Y_COORDINATE float (9,2) NOT NULL,
    INDEX `POSTCODE` (`POSTCODE`),
    INDEX `X_COORDINATE` (`X_COORDINATE`),
    INDEX `Y_COORDINATE` (`Y_COORDINATE`));
