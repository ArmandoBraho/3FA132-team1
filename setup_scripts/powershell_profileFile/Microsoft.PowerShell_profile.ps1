function ls-la
{
	Get-ChildItem . -Force
}

function .. 
{
	cd ..
}

function mariadb_user
{

	$username = "<username>"
	$userInput = Read-Host -Prompt "Please enter your input (press Enter for default: $username)"

	if ([string]::IsNullOrWhiteSpace($userInput)) {
		$userInput = $username
	}

	Start-Process -FilePath "C:\<path>\mariadb_start_skripte\mysql_start.bat" -ArgumentList $userInput -Verb RunAs
}

function mariadb_root 
{
    Start-Process -FilePath "C:\<path>\mariadb_start_skripte\mysql_start.bat" -Verb RunAs
}

function mariadb_stop 
{
    Start-Process -FilePath "C:\<path>\mariadb_start_skripte\mysql_stop.bat" -Verb RunAs
}

function mariadb_status
{
    Get-Service -Name MariaDB*
}

New-Alias -Name l -Value Get-ChildItem