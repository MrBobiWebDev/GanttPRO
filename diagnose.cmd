@echo off
chcp 65001 > nul
setlocal enabledelayedexpansion

cd /d "%~dp0"

echo. > diagnosis.log
echo ================================ >> diagnosis.log
echo   GanttPRO Diagnostics Report >> diagnosis.log
echo   %date% %time% >> diagnosis.log
echo ================================ >> diagnosis.log
echo. >> diagnosis.log

echo Checking Java installation...
java -version >> diagnosis.log 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Java not found! >> diagnosis.log
    echo Please install Java 17 or higher >> diagnosis.log
    goto :error
)

echo. >> diagnosis.log
echo Attempting Maven compilation... >> diagnosis.log
echo. >> diagnosis.log

call mvnw.cmd clean compile >> diagnosis.log 2>&1

if %ERRORLEVEL% EQU 0 (
    echo. >> diagnosis.log
    echo ✓ Compilation successful! >> diagnosis.log
    echo. >> diagnosis.log
    type diagnosis.log
    pause
    exit /b 0
) else (
    echo. >> diagnosis.log
    echo ✗ Compilation failed with errors >> diagnosis.log
    goto :error
)

:error
echo.
echo ================================
echo Diagnosis Report Generated
echo ================================
echo.
echo Contents of diagnosis.log:
echo.
type diagnosis.log
echo.
echo ================================
echo Report saved to: diagnosis.log
echo ================================
pause
exit /b 1
