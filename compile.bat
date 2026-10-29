@echo off
chcp 65001 > nul
echo ====================================================
echo  Compiling Library Management System (PRO192 Group 4)
echo ====================================================
if not exist bin mkdir bin
javac -encoding UTF-8 -d bin BusinessObject\UI\*.java Core\Entities\*.java DataObjects\DAO\*.java Utilities\*.java
if %ERRORLEVEL% equ 0 (
    echo.
    echo Compilation successful! Classes stored in 'bin' folder.
) else (
    echo.
    echo Compilation failed!
)
