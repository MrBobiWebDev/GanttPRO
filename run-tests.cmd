@echo off
REM Script to run tests on Windows

echo.
echo ========================================
echo GanttPRO - Running Tests
echo ========================================
echo.

if exist mvnw.cmd (
    echo Using Maven Wrapper...
    call mvnw.cmd clean test
) else (
    echo Using system Maven...
    mvn clean test
)

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo Tests PASSED successfully! ✓
    echo ========================================
    echo.
) else (
    echo.
    echo ========================================
    echo Tests FAILED! ✗
    echo ========================================
    echo.
    exit /b 1
)

pause
