param(
    [string]$ServerUrl = 'http://localhost:8080',
    [int]$LocationId = 1,
    [int]$ReportInterval = 60
)

$ErrorActionPreference = 'Stop'

$taskName = 'IOTMonitor-WindowsAgent'
$scriptPath = Join-Path $PSScriptRoot 'windows_agent.ps1'
$escapedScriptPath = $scriptPath.Replace('"', '""')
$action = "powershell.exe -ExecutionPolicy Bypass -WindowStyle Hidden -File `"$escapedScriptPath`" -ServerUrl `"$ServerUrl`" -LocationId $LocationId -ReportInterval $ReportInterval"

schtasks /Create /TN $taskName /SC ONLOGON /TR $action /F | Out-Null
schtasks /Run /TN $taskName | Out-Null

Write-Host "Installed scheduled task: $taskName"
Write-Host "Action: $action"
