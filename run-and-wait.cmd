@echo off
chcp 65001 > nul
color 0A
cls
echo.
echo ================================
echo   GanttPRO - Diagnostics
echo ================================
echo.

cd /d "%~dp0"

echo Attempting to compile the project...
echo This may take a few minutes...
echo.

call mvnw.cmd clean compile

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✓ Compilation successful!
    echo.
    echo Now attempting to run the application...
    echo Press Ctrl+C to stop the application
    echo.
    call mvnw.cmd spring-boot:run
) else (
    echo.
    echo ✗ Compilation failed!
    echo.
    echo Error code: %ERRORLEVEL%
    echo.
    echo Check the error messages above for details.
)

echo.
echo Process finished.
echo.
pause
