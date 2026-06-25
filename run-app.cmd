@echo off
REM Script to run the application on Windows

echo.
echo ========================================
echo GanttPRO - Starting Application
echo ========================================
echo.

if exist mvnw.cmd (
    echo Using Maven Wrapper...
    call mvnw.cmd spring-boot:run
) else (
    echo Using system Maven...
    mvn spring-boot:run
)
