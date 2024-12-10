@echo off

if exist "C:\Program Files\MariaDB 11.5\data\mysqld.pid" del "C:\Program Files\MariaDB 11.5\data\mysqld.pid"
net start mariadb

cd "C:\Program Files\MariaDB 11.5"
echo MySQL is starting

rem TODO changing the Data dir into the OneDrive files of the MariaDB.
rem Also understand how the MariaDB file system works.

set DIR="C:\Program Files\MariaDB 11.5"
set DATA=%DIR%\data
set PATH=%DIR%\bin;%PATH%

if not exist %DATA% ( 
	echo Creating the database 
	mysql_install_db 
	echo --------------------------- 
) 


if "%1"=="" (
    call "C:\progs\Aufrufsskripte\mariadb_start_skripte\mysql_cli.bat"
) else (
    call "C:\progs\Aufrufsskripte\mariadb_start_skripte\mysql_cli.bat" %1
)

rem Start the second script to open a root MySQL session
rem pause