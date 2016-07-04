DROP DATABASE IF EXISTS temp;
CREATE DATABASE aem default character set utf8 default collate utf8_general_ci;
USE aem;

DROP TABLE IF EXISTS Profiles;
CREATE TABLE Profiles(
	profileName VARCHAR(50) PRIMARY KEY,
	url VARCHAR(255),
	userName VARCHAR(50),
	password VARCHAR(50)
);