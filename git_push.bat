@echo off
chcp 65001 > nul
cd /d "C:\Users\Admin\OneDrive\FA26\PRO192\Project\Library Management"

echo ====================================================
echo  1. Checking Git Status...
echo ====================================================
git status

echo.
echo ====================================================
echo  2. Adding all modified and clean files...
echo ====================================================
git add -A

echo.
echo ====================================================
echo  3. Committing clean code to local repository...
echo ====================================================
git commit -m "refactor: eliminate dead code, remove unused constructors, and optimize NIO.2 file handling across all 21 files"

echo.
echo ====================================================
echo  4. Pushing to GitHub (origin main)...
echo ====================================================
git push origin main

if %ERRORLEVEL% equ 0 (
    echo.
    echo ====================================================
    echo  [SUCCESS] Code successfully pushed to GitHub!
    echo ====================================================
) else (
    echo.
    echo ====================================================
    echo  [ERROR] Git push encountered an issue.
    echo  Please check internet connection or GitHub credentials.
    echo ====================================================
)

echo.
pause
