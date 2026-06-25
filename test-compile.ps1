# PowerShell script to test compilation
Write-Host "Testing GanttPRO compilation..." -ForegroundColor Green
Write-Host "======================================" -ForegroundColor Green

$ErrorActionPreference = "Continue"

Write-Host "Running: mvnw.cmd clean compile" -ForegroundColor Yellow

& ".\mvnw.cmd" clean compile

if ($LASTEXITCODE -eq 0) {
    Write-Host "`n✓ Compilation successful!" -ForegroundColor Green
} else {
    Write-Host "`n✗ Compilation failed! Exit code: $LASTEXITCODE" -ForegroundColor Red
    Write-Host "Please check the error messages above." -ForegroundColor Red
}

Write-Host "`nPress any key to continue..." -ForegroundColor Yellow
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
