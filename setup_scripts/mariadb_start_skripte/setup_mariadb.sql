-- SOURCE C:\\progs\\Aufrufsskripte\\mariadb_start_skripte\\setup_mariadb.sql;
-- SOURCE C:/progs/Aufrufsskripte/mariadb_start_skripte/setup_mariadb.sql;
-- One of the two above Is needed to use this script (From the running mariadb instance) (Once the first one worked, the other time the second)

DROP DATABASE IF EXISTS building_automation;
CREATE DATABASE building_automation CHARACTER SET utf8;

DROP USER IF EXISTS 'onlynono'@'localhost';
CREATE USER 'onlynono'@'localhost' IDENTIFIED BY 'osr$33';

GRANT ALL PRIVILEGES ON building_automation.* TO 'onlynono'@'localhost';
FLUSH PRIVILEGES;

USE building_automation;