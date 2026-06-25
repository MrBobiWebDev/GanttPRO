#!/bin/bash

# Script to run the application on Linux/Mac

echo ""
echo "========================================"
echo "GanttPRO - Starting Application"
echo "========================================"
echo ""

if [ -f "./mvnw" ]; then
    echo "Using Maven Wrapper..."
    ./mvnw spring-boot:run
else
    echo "Using system Maven..."
    mvn spring-boot:run
fi

echo ""
echo "Application stopped."
echo ""
