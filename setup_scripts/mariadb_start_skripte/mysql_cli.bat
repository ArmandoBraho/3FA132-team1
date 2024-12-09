@echo off
cd "C:\Program Files\MariaDB 11.5\bin"

set USER=%1

if "%USER%"=="" (
    start wt.exe pwsh -Command "mysql -u root"
) else (
    start wt.exe pwsh -Command "mysql -u %USER% -p"
)
