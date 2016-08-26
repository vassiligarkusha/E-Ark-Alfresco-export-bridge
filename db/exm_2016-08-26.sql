# ************************************************************
# Sequel Pro SQL dump
# Version 4541
#
# http://www.sequelpro.com/
# https://github.com/sequelpro/sequelpro
#
# Host: 127.0.0.1 (MySQL 5.5.5-10.0.20-MariaDB)
# Database: exm
# Generation Time: 2016-08-26 11:14:35 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table Mappings
# ------------------------------------------------------------

CREATE TABLE `Mappings` (
  `name` varchar(25) NOT NULL DEFAULT '',
  `sysPath` varchar(100) NOT NULL DEFAULT '',
  `created` date NOT NULL,
  `author` varchar(75) DEFAULT '',
  `realFileName` varchar(75) NOT NULL DEFAULT '',
  PRIMARY KEY (`name`,`sysPath`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table Profiles
# ------------------------------------------------------------

CREATE TABLE `Profiles` (
  `name` varchar(50) NOT NULL DEFAULT '',
  `url` varchar(255) DEFAULT NULL,
  `userName` varchar(50) DEFAULT NULL,
  `password` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table Repositories
# ------------------------------------------------------------

CREATE TABLE `Repositories` (
  `name` varchar(50) NOT NULL DEFAULT '',
  `repositoryName` varchar(70) NOT NULL DEFAULT '',
  PRIMARY KEY (`name`,`repositoryName`),
  CONSTRAINT `repositories_ibfk_1` FOREIGN KEY (`name`) REFERENCES `Profiles` (`name`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;




/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
