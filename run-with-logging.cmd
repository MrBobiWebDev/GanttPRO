@echo off
cd /d "%~dp0"
echo Starting GanttPRO with logging...
echo Output will be saved to run-output.log
.\mvnw.cmd clean spring-boot:run > run-output.log 2>&1
echo.
echo Process finished. Check run-output.log for details.
pause
