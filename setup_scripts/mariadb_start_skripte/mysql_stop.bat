@echo off
set DIR="C:\Program Files\MariaDB 11.5"
set DATA=%DIR%\bin
cd %DIR%
echo Mysql shutdowm ...
%DATA%\mysqladmin.exe -u root shutdown