param(
    [ValidateSet('startup', 'shutdown', 'sleep', 'wake')]
    [string]$Event,
    [string]$ServerUrl = 'http://localhost:8080',
    [int]$LocationId = 1,
    [int]$ReportInterval = 60,
    [switch]$RunOnce
)

$ErrorActionPreference = 'Stop'

function Get-MachineId {
    $hostname = $env:COMPUTERNAME
    $adapter = Get-CimInstance Win32_NetworkAdapterConfiguration |
        Where-Object { $_.IPEnabled -and $_.MACAddress } |
        Select-Object -First 1

    $mac = if ($adapter -and $adapter.MACAddress) {
        $adapter.MACAddress.Replace('-', ':')
    } else {
        'unknown'
    }

    return "$hostname-$mac"
}

function Get-PrimaryIpAddress {
    $adapter = Get-CimInstance Win32_NetworkAdapterConfiguration |
        Where-Object { $_.IPEnabled -and $_.IPAddress } |
        Select-Object -First 1

    if (-not $adapter) {
        return '127.0.0.1'
    }

    $ip = $adapter.IPAddress | Where-Object { $_ -match '^\d+\.\d+\.\d+\.\d+$' } | Select-Object -First 1
    return $(if ($ip) { $ip } else { '127.0.0.1' })
}

function Get-OsVersion {
    $os = Get-CimInstance Win32_OperatingSystem
    return "$($os.Caption) $($os.Version)"
}

function Get-CpuPercent {
    $sample1 = (Get-Counter '\Processor(_Total)\% Processor Time').CounterSamples[0].CookedValue
    Start-Sleep -Milliseconds 900
    $sample2 = (Get-Counter '\Processor(_Total)\% Processor Time').CounterSamples[0].CookedValue
    return [math]::Round((($sample1 + $sample2) / 2), 1)
}

function Get-MemoryPercent {
    $os = Get-CimInstance Win32_OperatingSystem
    $totalKb = [double]$os.TotalVisibleMemorySize
    $freeKb = [double]$os.FreePhysicalMemory
    if ($totalKb -le 0) {
        return 0.0
    }

    return [math]::Round((($totalKb - $freeKb) / $totalKb) * 100, 1)
}

function Get-DiskPercent {
    $disk = Get-CimInstance Win32_LogicalDisk -Filter "DeviceID='C:'"
    if (-not $disk -or [double]$disk.Size -le 0) {
        return 0.0
    }

    return [math]::Round((([double]$disk.Size - [double]$disk.FreeSpace) / [double]$disk.Size) * 100, 1)
}

function Get-Metrics {
    return @(
        @{
            type  = 'cpu'
            value = (Get-CpuPercent)
            unit  = '%'
        },
        @{
            type  = 'memory'
            value = (Get-MemoryPercent)
            unit  = '%'
        },
        @{
            type  = 'disk'
            value = (Get-DiskPercent)
            unit  = '%'
        }
    )
}

function Invoke-AgentPost {
    param(
        [string]$Path,
        [hashtable]$Payload
    )

    $uri = "$($ServerUrl.TrimEnd('/'))$Path"
    $json = $Payload | ConvertTo-Json -Depth 5
    Invoke-RestMethod -Uri $uri -Method Post -ContentType 'application/json; charset=utf-8' -Body $json | Out-Null
}

function Send-Heartbeat {
    $payload = @{
        machineId      = Get-MachineId
        hostname       = $env:COMPUTERNAME
        ipAddress      = Get-PrimaryIpAddress
        username       = $env:USERNAME
        osVersion      = Get-OsVersion
        locationId     = $LocationId
        reportInterval = $ReportInterval
        metrics        = Get-Metrics
    }

    Invoke-AgentPost -Path '/windows/heartbeat' -Payload $payload
    Write-Host "[$(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')] heartbeat sent"
}

function Send-Event {
    param([string]$EventType)

    $payload = @{
        machineId = Get-MachineId
        eventType = $EventType
    }

    Invoke-AgentPost -Path '/windows/event' -Payload $payload
    Write-Host "[$(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')] event sent: $EventType"
}

if ($Event) {
    Send-Event -EventType $Event
    exit 0
}

try {
    Send-Event -EventType 'startup'
} catch {
    Write-Warning "startup event failed: $($_.Exception.Message)"
}

do {
    try {
        Send-Heartbeat
    } catch {
        Write-Warning "heartbeat failed: $($_.Exception.Message)"
    }

    if (-not $RunOnce) {
        Start-Sleep -Seconds $ReportInterval
    }
} while (-not $RunOnce)
