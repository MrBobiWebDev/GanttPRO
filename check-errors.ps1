# Simple error checking script
param(
    [switch]$CompileOnly = $false
)

$projectPath = Get-Location

Write-Host "================================"
Write-Host "GanttPRO - Error Checker" -ForegroundColor Cyan
Write-Host "================================"
Write-Host ""

if ($CompileOnly) {
    Write-Host "Running compilation only..." -ForegroundColor Yellow
    Write-Host ""
    & ".\mvnw.cmd" clean compile -X 2>&1 | Tee-Object -FilePath "compile-errors.log"
    Write-Host ""
    Write-Host "Compilation log saved to: compile-errors.log" -ForegroundColor Green
} else {
    Write-Host "Starting application with full output..." -ForegroundColor Yellow
    Write-Host ""
    & ".\mvnw.cmd" clean spring-boot:run 2>&1 | Tee-Object -FilePath "run-errors.log"
    Write-Host ""
    Write-Host "Run log saved to: run-errors.log" -ForegroundColor Green
}

Write-Host ""
Write-Host "Press any key to exit..." -ForegroundColor Yellow
$null = [Console]::ReadKey($true)
