@echo off
chcp 65001 > nul
if not exist bin (
    echo 'bin' directory not found. Compiling first...
    call compile.bat
)
echo ====================================================
echo  Running Library Management System (PRO192 Group 4)
echo ====================================================
java -cp bin BusinessObject.UI.Program
pause
