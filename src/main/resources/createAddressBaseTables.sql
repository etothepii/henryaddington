USE frederickNorth;
DROP TABLE IF EXISTS Classification;
CREATE TABLE Classification (
    RECORD_IDENTIFIER int (2) NOT NULL,
    CHANGE_TYPE CHAR(1) NOT NULL,
    PRO_ORDER BIGINT (16) NOT NULL,
    UPRN BIGINT (12) NOT NULL PRIMARY KEY,
    CLASS_KEY CHAR(16) NOT NULL,
    CLASSIFICATION_CODE CHAR(4) NOT NULL,
    CLASS_SCHEME VARCHAR(255) NOT NULL,
    SCHEMA_VERSION float(3,1) NOT NULL,
    START_DATE date NOT NULL,
    END_DATE date NOT NULL,
    LAST_UPDATE_DATE date NOT NULL,
    ENTRY_DATE date NOT NULL );
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
    ID CHAR(36) NOT NULL PRIMARY KEY,
    NAME VARCHAR(64) NOT NULL,
    BOUNDED_AREA INT(8) NOT NULL,
    OWNER INT(8) NULL,
    OWNER_GROUP INT(8) NULL,
    DELIVERED_BY INT (8) NULL,
    BOUNDARY MEDIUMBLOB NULL,
    INDEX `BOUNDED_AREA` (`BOUNDED_AREA`),
    INDEX `DELIVERED_BY` (`DELIVERED_BY`));
DROP TABLE IF EXISTS RouteMember;
CREATE TABLE RouteMember (
    ROUTE_ID INT (8) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    UPRN BIGINT(12) NOT NULL,
    INDEX `ROUTE_ID` (`ROUTE_ID`),
    INDEX `UPRN` (`UPRN`));
DROP TABLE IF EXISTS Person;
CREATE TABLE Person (
    ID INT (8) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    SURNAME VARCHAR(64) NULL,
    OTHER_NAMES VARCHAR(64) NULL,
    TITLE VARCHAR (8) NULL,
    MAIN_CONTACT_NUMBER VARCHAR (11) NULL,
    DELIVERY_ADDRESS TINYTEXT NULL);
DROP TABLE IF EXISTS LeafletMap;
CREATE TABLE LeafletMap (
    ID CHAR(36) PRIMARY KEY,
    LEAFLET INT(8) NOT NULL,
    ROUTE INT(8) NOT NULL,
    DELIVERED date NULL,
    DELIVERED_BY INT(8) NULL,
    INDEX `LEAFLET` (`LEAFLET`),
    INDEX `ROUTE` (`ROUTE`),
    INDEX `DELIVERED_BY` (`DELIVERED_BY`));
DROP TABLE IF EXISTS Leaflet;
CREATE TABLE Leaflet (
    ID CHAR(36) NOT NULL PRIMARY KEY,
    DELIVERY_COMMENCED date NULL,
    TITLE VARCHAR(255) NULL,
    DESCRIPTION TEXT NULL,
    LEAFLET MEDIUMBLOB NULL);
DROP TABLE IF EXISTS Users;
CREATE TABLE Users (
    ID INT(8) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    EMAIL VARCHAR(255) NOT NULL,
    PERSON INT(8) NOT NULL,
    INDEX `EMAIL` (`EMAIL`));
DROP TABLE IF EXISTS Groups;
CREATE TABLE Groups (
    ID INT(8) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    NAME VARCHAR(255) NOT NULL);
DROP TABLE IF EXISTS GroupMembers;
CREATE TABLE GroupMembers (
    ID INT(8) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `GROUP` INT(8) NOT NULL,
    MEMBER INT(8) NULL,
    GROUP_MEMBER INT(8) NULL,
    INDEX `GROUP` (`GROUP`),
    INDEX `MEMBER` (`MEMBER`),
    INDEX `GROUP_MEMBER` (`GROUP_MEMBER`));
DROP TABLE IF EXISTS BoundedArea;
CREATE TABLE BoundedArea (
    ID CHAR(36) NOT NULL PRIMARY KEY,
    PARENT INT(8) NULL,
    OWNER INT(8) NULL,
    OWNER_GROUP INT(8) NULL,
    NAME VARCHAR(64) NOT NULL,
    BOUNDARY MEDIUMBLOB NULL,
    INDEX `PARENT` (`PARENT`));
