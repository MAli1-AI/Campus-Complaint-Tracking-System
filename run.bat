@echo off
title Campus Complaint Tracking System
color F9

echo ====================================================
echo    CAMPUS COMPLAINT TRACKING SYSTEM LAUNCHER
echo ====================================================
echo.

REM Get the directory where this batch file is located
set "PROJECT_ROOT=%~dp0"
cd /d "%PROJECT_ROOT%"

REM Check if src folder exists
if not exist "src" (
    echo ERROR: src folder not found!
    echo Please make sure you are in the correct project directory.
    pause
    exit /b 1
)

echo [1/3] Compiling Java source files...
echo.

REM Compile all Java files
javac -d . -cp "src" src/com/campuscomplaint/Main.java src/com/campuscomplaint/model/*.java src/com/campuscomplaint/service/*.java src/com/campuscomplaint/ui/*.java src/com/campuscomplaint/exception/*.java src/com/campuscomplaint/util/*.java 2> compile_error.txt

REM Check if compilation succeeded
if %errorlevel% neq 0 (
    echo.
    echo ====================================================
    echo    COMPILATION FAILED!
    echo ====================================================
    echo.
    type compile_error.txt
    echo.
    echo Please check the errors above.
    del compile_error.txt 2>nul
    pause
    exit /b 1
)

del compile_error.txt 2>nul
echo.
echo [2/3] Compilation successful!
echo.

REM Check if data folder exists, if not create it
if not exist "data" (
    mkdir data
    echo Created data folder.
)

echo [3/3] Starting application...
echo.
echo ====================================================
echo    APPLICATION RUNNING
echo ====================================================
echo.

REM Run the application
java -cp . com.campuscomplaint.Main

echo.
echo ====================================================
echo    APPLICATION CLOSED
echo ====================================================
pause