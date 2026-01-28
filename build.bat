@echo off
REM Batch file for building and running the Abalone Game on Windows

setlocal enabledelayedexpansion

set JAVAC=javac
set JAVA=java
set SRCDIR=src
set BINDIR=bin
set MAIN_CLASS=abalone.Main

REM Display help if no argument provided
if "%1"=="" (
    call :help
    exit /b 0
)

REM Process commands
if /i "%1"=="compile" (
    call :compile
) else if /i "%1"=="run" (
    call :compile
    if !errorlevel! equ 0 call :run
) else if /i "%1"=="clean" (
    call :clean
) else if /i "%1"=="rebuild" (
    call :clean
    call :compile
) else if /i "%1"=="help" (
    call :help
) else (
    echo Unknown command: %1
    call :help
    exit /b 1
)

exit /b 0

:compile
echo Compiling Java source files...
if not exist "%BINDIR%" mkdir "%BINDIR%"
%JAVAC% -d "%BINDIR%" -sourcepath "%SRCDIR%" "%SRCDIR%\abalone\*.java" "%SRCDIR%\abalone\controller\*.java" "%SRCDIR%\abalone\model\*.java" "%SRCDIR%\abalone\view\*.java"
if !errorlevel! equ 0 (
    echo Compilation successful.
) else (
    echo Compilation failed.
    exit /b 1
)
exit /b 0

:run
echo Running the game...
%JAVA% -cp "%BINDIR%" %MAIN_CLASS%
exit /b 0

:clean
echo Cleaning compiled files...
if exist "%BINDIR%" (
    rmdir /s /q "%BINDIR%"
    echo Clean completed.
) else (
    echo Nothing to clean.
)
exit /b 0

:help
echo.
echo Available commands:
echo   build compile   - Compile the Java source files
echo   build run       - Compile and run the game
echo   build clean     - Remove compiled files
echo   build rebuild   - Clean and recompile
echo   build help      - Display this help message
echo.
exit /b 0
