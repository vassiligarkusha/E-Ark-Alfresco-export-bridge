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

/* Insert dummy data */
INSERT INTO Profiles VALUES ('name1', 'url1', 'user1', 'pwd1');
INSERT INTO Profiles VALUES ('name2', 'url2', 'user2', 'pwd2');
INSERT INTO Profiles VALUES ('name3', 'url3', 'user3', 'pwd3');