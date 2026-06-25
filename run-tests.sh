#!/bin/bash

# Script to run tests on Linux/Mac

echo ""
echo "========================================"
echo "GanttPRO - Running Tests"
echo "========================================"
echo ""

if [ -f "./mvnw" ]; then
    echo "Using Maven Wrapper..."
    ./mvnw clean test
else
    echo "Using system Maven..."
    mvn clean test
fi

if [ $? -eq 0 ]; then
    echo ""
    echo "========================================"
    echo "Tests PASSED successfully! ✓"
    echo "========================================"
    echo ""
else
    echo ""
    echo "========================================"
    echo "Tests FAILED! ✗"
    echo "========================================"
    echo ""
    exit 1
fi
