@echo off
cd /d "%~dp0"
echo Compiling project...
.\mvnw.cmd clean compile > compile-output.log 2>&1
echo.
if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
) else (
    echo Compilation failed! Check compile-output.log for details.
)
pause
